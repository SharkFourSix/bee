package app.bee.expressions;

import app.bee.exceptions.BeeException;

public class UnexpectedEndOfInput extends BeeException {
    public UnexpectedEndOfInput() {
        super("Unexpected end of input");
    }
}
