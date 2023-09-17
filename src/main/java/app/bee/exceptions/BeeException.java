package app.bee.exceptions;

public class BeeException extends Exception {
    public BeeException(String message) {
        super(message);
    }

    public BeeException(Exception exception) {
        super(exception);
    }
}
