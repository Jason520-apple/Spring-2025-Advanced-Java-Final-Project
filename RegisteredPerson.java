// --- File: RegisteredPerson.java ---


/**
 * Extension of Person class that includes government ID.
 */
public class RegisteredPerson extends Person {
    private static final long serialVersionUID = 1L;

    protected String governmentID;

    public RegisteredPerson(String firstName, String lastName, OCCCDate dateOfBirth, String governmentID) {
        super(firstName, lastName, dateOfBirth);
        this.governmentID = governmentID;
    }

    public String getGovernmentID() { return governmentID; }

    @Override
    public String toString() {
        return super.toString() + ", Government ID: " + governmentID;
    }
}