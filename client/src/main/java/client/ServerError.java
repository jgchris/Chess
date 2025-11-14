package client;

/**
 * Indicates there was an error from the server handled by ServerFacade
 */
public class ServerError extends Exception{
    public ServerError(String message) {
        super(message);
    }
    public ServerError(String message, int status_code) {
        super(String.format("%d: %s", status_code, message));
    }
}
