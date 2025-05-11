// --- File: OCCCPerson.java ---

/**
 * Special type of RegisteredPerson that includes a student ID.
 */
public class OCCCPerson extends RegisteredPerson {
    private static final long serialVersionUID = 1L;

    protected String studentID;

    public OCCCPerson(String firstName, String lastName, OCCCDate dateOfBirth, String governmentID, String studentID) {
        super(firstName, lastName, dateOfBirth, governmentID);
        this.studentID = studentID;
    }

    public String getStudentID() { return studentID; }

    @Override
    public String toString() {
        return super.toString() + ", Student ID: " + studentID;
    }
}
