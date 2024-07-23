package service;

/**
 * Indicates there was an error in the request
 */
public class BadRequestException extends Exception{
    public BadRequestException(String message) {
        super(message);
    }
}
