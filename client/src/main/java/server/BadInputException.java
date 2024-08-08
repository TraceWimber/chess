package server;

public class BadInputException extends Exception {

    private final int status;
    private final String message;

    public BadInputException(int status, String message) {
        super(String.format("HTTP %d: %s", status, message));
        this.status = status;
        this.message = message;
    }

    public int getStatusCode() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
