package ascii_art;

public class IllegalAddCommandException extends Exception {

    private static final String EXCEPTION_MESSAGE = "Did not add due to incorrect format";

    public IllegalAddCommandException() {
        super(EXCEPTION_MESSAGE);
    }
}
