package app.bee.exceptions;

import java.util.Locale;

public class BeeSyntaxError extends BeeException {
    public BeeSyntaxError(int column, String message) {
        super(String.format(Locale.US, "COLUMN %d: %s", column, message));
    }
}
