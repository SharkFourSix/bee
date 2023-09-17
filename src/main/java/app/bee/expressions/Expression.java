package app.bee.expressions;

import app.bee.Token;

public class Expression extends Token {
    //public enum Co

    public Expression(TokenType tokenType, Object tokenValue) {
        super(tokenType, tokenValue);
    }

    public Expression(TokenType tokenType) {
        super(tokenType);
    }

    public void evaluate() {

    }
}
