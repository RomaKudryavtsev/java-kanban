package managers;

public class NonemptySlotsException extends RuntimeException {
    public NonemptySlotsException() {
    }

    public NonemptySlotsException(final String message) {
        super(message);
    }
}
