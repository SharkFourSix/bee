package app.bee;

import java.math.BigDecimal;
import java.util.Objects;

public class Token {
    public enum TokenType {

        TOK_TEST_AND("AND"),
        TOK_TEST_OR("OR"),
        TOK_OPEN_GROUP("("),
        TOK_CLOSE_GROUP(")"),
        TOK_IDENTIFIER(null),
        TOK_STRING(null),
        TOK_BOOLEAN(null),
        TOK_OPERATOR_EQUALS("=="),
        TOK_OPERATOR_GT(">"),
        TOK_OPERATOR_LT("<"),
        TOK_OPERATOR_LTE("<="),
        TOK_OPERATOR_GTE(">="),
        TOK_OPERATOR_NOT("<>"),
        TOK_OPERATOR_NOT2("!="),
        TOK_NUMBER(null);

        TokenType(String literal) {
            this.literal = literal;
        }

        public final String literal;

        public static final TokenType[] TOKEN_TYPES = values();

        public static TokenType ofLiteral(String literal) {
            Objects.requireNonNull(literal);
            for (var tt : TOKEN_TYPES) {
                if (literal.equals(tt.literal)) {
                    return tt;
                }
            }
            return null;
        }
    }

    private final Object tokenValue;
    private final TokenType tokenType;

    public Token(TokenType tokenType, Object tokenValue) {
        this.tokenValue = tokenValue;
        this.tokenType = tokenType;
    }

    public Token(TokenType tokenType) {
        this.tokenValue = null;
        this.tokenType = tokenType;
    }

    public Object getTokenValue() {
        return tokenValue;
    }

    public BigDecimal getNumber() {
        return (BigDecimal) tokenValue;
    }

    public String getString() {
        return (String) tokenValue;
    }

    public Boolean getBoolean() {
        return (Boolean) tokenValue;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    @Override
    public String toString() {
        return "{TYPE: " + tokenType + ", VALUE: " + (tokenValue != null ? tokenValue : "<NULL>") + "}";
    }
}
