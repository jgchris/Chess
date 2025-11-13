package client;

/**
 * Indicates there was an error from the server handled by ServerFacade
 */
public class ServerError extends Exception{
    public ServerError(String message) {
        super(message);
    }
    public ServerError(String message, Throwable ex) {
        super(message, ex);
    }
}
