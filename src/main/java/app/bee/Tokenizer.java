package app.bee;

import app.bee.exceptions.BeeException;
import app.bee.exceptions.BeeSyntaxError;
import app.bee.exceptions.InvalidCharacterException;
import app.bee.expressions.UnexpectedEndOfInput;
import app.bee.types.LiteralValue;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Locale;

public class Tokenizer {
    private enum State {
        start,
        expression,
        symbol,
        ws,
        operator,
        literal,
        conjunction,
        start_group,
        end_group
    }

    private State state;
    private CharReader reader;

    public Tokenizer(String expression) {
        this.state = State.start;
        this.reader = new CharReader(expression.toCharArray());
    }

    public LinkedList<Token> tokenize() throws BeeException {
        int numberOfGroups = 0;
        int numberOfExpressionsInCurrentGroup = 0;
        var tokens = new LinkedList<Token>();
        while (reader.hasNext()) {
            //
            // STATEMENT : CONJUNCTION? (WS+)? START_GROUP (WS+)? expression+ (WS+)? END_GROUP
            // expression : (WS+)? IDENTIFIER (WS+)? OPERATOR (WS+?) LITERAL (WS+)? CONJUNCTION?
            // IDENTIFIER : [a-zA-Z]+([a-zA-Z0-9_]+)?
            // LITERAL : STRING | NUMBER | BOOLEAN
            // STRING: '.*'
            // NUMBER: ^[0-9]+(.[0-9]+)?$
            // BOOLEAN: true | TRUE | false | FALSE
            // OPERATOR: == | != | >= | <= | > | < | <>
            // CONJUNCTION: OR | AND
            // WS: \s | \t | \r | \n | \f
            // START_GROUP: (
            // END_GROUP: )
            char symbol;
            switch (state) {
                case start:
                    swallowSpace();
                    if (numberOfGroups > 0) {
                        var conjunction = readConjunction();
                        tokens.push(new Token(conjunction));
                        swallowSpace();
                    }
                    symbol = reader.next();
                    if (symbol == '(') {
                        state = State.start_group;
                        tokens.push(new Token(Token.TokenType.TOK_OPEN_GROUP));
                    } else {
                        throw new InvalidCharacterException(reader.position(), symbol);
                    }
                case start_group:
                    state = State.expression;
                    break;
                case end_group:
                    numberOfGroups++;
                    numberOfExpressionsInCurrentGroup = 0;
                    state = State.start;
                    swallowSpace();
                    break;
                case expression:
                    swallowSpace();
                    symbol = reader.next();

                    if (symbol == ')') {
                        state = State.end_group;
                        tokens.push(new Token(Token.TokenType.TOK_CLOSE_GROUP));
                        continue;
                    }
                    reader.rewind();

                    if (numberOfExpressionsInCurrentGroup > 0) {
                        var conjunction = readConjunction();
                        tokens.push(new Token(conjunction));
                        swallowSpace();
                    }

                    var identifier = readIdentifier();

                    if ("TRUE".equalsIgnoreCase(identifier) || "FALSE".equalsIgnoreCase(identifier) || "AND".equalsIgnoreCase(identifier) || "OR".equalsIgnoreCase(identifier)) {
                        throw new BeeSyntaxError(reader.position(),
                                String.format(Locale.US, "Reserved keyword '%s' used as an identifier.", identifier)
                        );
                    }

                    tokens.push(new Token(Token.TokenType.TOK_IDENTIFIER, identifier));

                    swallowSpace();
                    var operator = readOperator();
                    tokens.push(new Token(operator));

                    swallowSpace();
                    var literal = readLiteralValue();
                    tokens.push(literal);

                    numberOfExpressionsInCurrentGroup++;
                    break;
            }
        }
        return tokens;
    }

    private Token.TokenType readConjunction() throws BeeException {
        // CONJUNCTION: OR | AND
        if (reader.hasNext()) {
            var c = reader.next();
            if (c == 'o' || c == 'O') {
                reader.rewind();
                expectIgnoreCase('O', 'R');
                return Token.TokenType.TOK_TEST_OR;
            }
            if (c == 'a' || c == 'A') {
                reader.rewind();
                expectIgnoreCase('A', 'N', 'D');
                return Token.TokenType.TOK_TEST_AND;
            }
            throw new BeeSyntaxError(reader.position(), "Expected conjunctions: 'OR', 'AND'");
        }
        throw new UnexpectedEndOfInput();
    }

    private LiteralValue readLiteralValue() throws BeeException {
        // LITERAL : STRING | NUMBER | BOOLEAN
        // STRING: '.*'
        // NUMBER: ^[0-9]+(.[0-9]+)?$
        // BOOLEAN: true | TRUE | false | FALSE
        if (reader.hasNext()) {
            var c = reader.next();

            if (c == '\'' || c == '"') {
                // Read string
                var expectClosing = c;
                var sb = new StringBuilder();

                while (reader.hasNext()) {
                    c = reader.next();
                    if (c == expectClosing) {
                        if (reader.peekBack() == '\\') {
                            sb.setCharAt(sb.length() - 1, c);
                        } else {
                            return new LiteralValue(LiteralValue.Type.STRING, sb.toString());
                        }
                    } else {
                        sb.append(c);
                    }
                }
                throw new UnexpectedEndOfInput();
            }
            if ((c >= '0' && c <= '9') || (c == '-')) {
                // read number
                boolean hasDecimal = false;
                var sb = new StringBuilder(Character.toString(c));

                while (reader.hasNext()) {
                    c = reader.next();
                    if (c >= '0' && c <= '9') {
                        sb.append(c);
                    } else {
                        if (c == '.') {
                            if (hasDecimal) {
                                throw new BeeSyntaxError(reader.position(), "Decimal point already present in value.");
                            } else {
                                hasDecimal = true;
                                sb.append(c);
                            }
                        } else {
                            reader.rewind();
                            return new LiteralValue(LiteralValue.Type.NUMBER, new BigDecimal(sb.toString()));
                        }
                    }
                }
                throw new UnexpectedEndOfInput();
            }
            if (c == 'T' || c == 't' || c == 'F' || c == 'f') { // set makes TRUE/FALSE
                // read boolean
                if (c == 'T' || c == 't') {
                    // TRUE
                    reader.rewind();
                    expectIgnoreCase('T', 'R', 'U', 'E');
                    return new LiteralValue(LiteralValue.Type.BOOLEAN, true);
                }
                // FALSE
                reader.rewind();
                expectIgnoreCase('F', 'A', 'L', 'S', 'E');
                return new LiteralValue(LiteralValue.Type.BOOLEAN, false);
            }
            throw new BeeSyntaxError(reader.position(), "invalid value");
        }
        throw new UnexpectedEndOfInput();
    }

    private Token.TokenType readOperator() throws BeeException {
        String operator;

        if (reader.hasNext()) {
            var c = reader.next();

            if (!"=!><".contains(Character.toString(c))) {
                throw new BeeSyntaxError(reader.position(), "Invalid character in operator");
            }

            operator = Character.toString(c);
            if (reader.hasNext()) {
                c = reader.next();
                if (!"=!><".contains(Character.toString(c))) {
                    reader.rewind();
                } else {
                    switch (operator) {
                        case "=":
                            switch (c) {
                                case '=':
                                    operator = "==";
                                    break;
                                case '>':
                                    throw new BeeSyntaxError(reader.position(), "Invalid operator syntax. Did you mean '>='?");
                                case '<':
                                    throw new BeeSyntaxError(reader.position(), "Invalid operator syntax. Did you mean '<='?");
                                case '!':
                                    throw new BeeSyntaxError(reader.position(), "Invalid operator syntax. Did you mean '!='?");
                            }
                            break;
                        case "!":
                            if (c == '=') {
                                operator = "!=";
                            } else {
                                throw new BeeSyntaxError(reader.position(), "Invalid operator syntax.");
                            }
                            break;
                        case ">":
                            if (c == '=') {
                                operator = ">=";
                            } else {
                                throw new BeeSyntaxError(reader.position(), "Invalid operator syntax.");
                            }
                            break;
                        case "<":
                            switch (c) {
                                case '=':
                                    operator = "<=";
                                    break;
                                case '>':
                                    operator = "<>";
                                    break;
                                default:
                                    throw new BeeSyntaxError(reader.position(), "Invalid operator syntax.");
                            }
                            break;
                    }
                }
            }

            return Token.TokenType.ofLiteral(operator);
        }

        throw new UnexpectedEndOfInput();
    }

    private String readIdentifier() throws BeeException {
        StringBuilder builder = new StringBuilder();
        int length = 0;
        while (reader.hasNext()) {
            if (reader.peek() == ' ') {
                break;
            }
            var c = reader.next();
            if (length == 0) {
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c >= 'Z') || (c == '_')) {
                    builder.append(c);
                } else {
                    // TODO Fix the error message here
                    throw new BeeSyntaxError(reader.position(), "Identifiers may not start with numbers");
                }
            } else {
                if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c == '_')) {
                    builder.append(c);
                } else {
                    throw new BeeSyntaxError(reader.position(), "Invalid character in identifier");
                }
            }
            length++;
        }
        if (length == 0) {
            throw new BeeSyntaxError(reader.position(), "Expected identifier");
        }
        if (length <= 256) {
            return builder.toString();
        }
        throw new BeeSyntaxError(reader.position(), "Identifier too long");
    }

    private void swallowSpace() {
        while (reader.hasNext()) {
            var c = reader.next();
            if (c == ' ' || c == '\f' || c == '\r' || c == '\t' || c == '\n') {
                continue;
            }
            reader.rewind();
            break;
        }
    }

    private void expectIgnoreCase(char... chars) throws BeeException {
        for (var expected : chars) {
            if (!reader.hasNext()) {
                throw new UnexpectedEndOfInput();
            }
            if (Character.toUpperCase(expected) != Character.toUpperCase(reader.next())) {
                throw new BeeSyntaxError(reader.position(), String.format(Locale.US, "expected '%c'", expected));
            }
        }
    }
}
