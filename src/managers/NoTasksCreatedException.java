package managers;

public class NoTasksCreatedException extends RuntimeException {
    public NoTasksCreatedException() {
    }
    public NoTasksCreatedException(final String message) {super(message);}
}
