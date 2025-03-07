package net.cybercake.display.status;

public class RequestDeniedException extends IllegalStateException {

    public RequestDeniedException(String msg) {
        super("Request denied: " + msg);
    }

}
