package epi.exception;

/** Represents an expected, user-facing error in Epi. */
public class EpiException extends Exception {
    /** Creates an exception with the message shown to the user. */
    public EpiException(String message) {
        super(message);
    }

    /** Preserves the technical cause while supplying a safe, actionable user message. */
    public EpiException(String message, Throwable cause) {
        super(message, cause);
    }
}
