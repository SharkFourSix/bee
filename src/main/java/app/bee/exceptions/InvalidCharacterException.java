package app.bee.exceptions;

import java.util.Locale;

public class InvalidCharacterException extends BeeException {
    public InvalidCharacterException(int column, char found) {
        super(String.format(Locale.US, "Unexpected character '%c' at %d.", found, column));
    }
}
