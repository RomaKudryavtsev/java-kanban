package managers;

public class NonexistentIdException extends RuntimeException {
    public NonexistentIdException() {
    }

    public NonexistentIdException(final String message) {
        super(message);
    }
}
