package hospital.datastructures;

/**
 * Thrown when a removal or inspection is attempted on an empty queue or stack.
 *
 * <p>Returning {@code null} for these cases would force every caller to remember a null
 * check; an explicit exception makes the empty condition impossible to ignore. The menu
 * layer catches it and prints a friendly message instead of a stack trace.</p>
 */
public class EmptyStructureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EmptyStructureException(String message) {
        super(message);
    }
}
