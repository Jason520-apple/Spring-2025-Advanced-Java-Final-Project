// --- File: OCCCDateException.java ---
package personUI;

public class OCCCDateException extends Exception {
    private static final long serialVersionUID = 1L; // Recommended for Serializable classes

    public OCCCDateException(String message) {
        super(message);
    }

    public OCCCDateException(String message, Throwable cause) {
        super(message, cause);
    }
}