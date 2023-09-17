package app.bee.types;

import app.bee.Token;

public class LiteralValue extends Token {
    public enum Type {
        BOOLEAN(TokenType.TOK_BOOLEAN),
        STRING(TokenType.TOK_STRING),
        NUMBER(TokenType.TOK_NUMBER);

        Type(TokenType tokenType) {
            this.tokenType = tokenType;
        }

        private TokenType tokenType;
    }

    private final Type type;
    private final Object value;

    public Type getType() {
        return type;
    }

    public LiteralValue(Type type, Object value) {
        super(type.tokenType, value);
        this.type = type;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
