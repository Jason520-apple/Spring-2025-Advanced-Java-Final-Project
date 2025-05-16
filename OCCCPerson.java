// Travis Bauman, Amida Fombutu, Jason Vo
// OCCC Spring 2025
// Advanced Java
// Final Project - Person GUI Application

public class OCCCPerson extends RegisteredPerson {

    // Unique student identifier
    private String studentID;

    // Construct OCCCPerson from an existing RegisteredPerson and student ID
    public OCCCPerson(RegisteredPerson p, String studentID) {
        super(p.getFirstName(), p.getLastName(), p.getDOB(), p.getGovernmentID());
        this.studentID = studentID;
    }

    // Copy constructor
    public OCCCPerson(OCCCPerson p) {
        super(p);
        this.studentID = p.studentID;
    }

    // Getter
    public String getStudentID() {
        return studentID;
    }

    // Setter (used in GUI when editing)
    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    // Equality comparison with another OCCCPerson
    public boolean equals(OCCCPerson p) {
        return this.studentID.equalsIgnoreCase(p.studentID);
    }

    // Overloaded comparison with a RegisteredPerson (ignores student ID)
    public boolean equals(RegisteredPerson p) {
        return this.getGovernmentID().equalsIgnoreCase(p.getGovernmentID())
            && this.getFirstName().equalsIgnoreCase(p.getFirstName())
            && this.getLastName().equalsIgnoreCase(p.getLastName());
    }

    // Overloaded comparison with a basic Person (ignores gov ID and student ID)
    public boolean equals(Person p) {
        return this.getFirstName().equalsIgnoreCase(p.getFirstName())
            && this.getLastName().equalsIgnoreCase(p.getLastName())
            && this.getDOB().equals(p.getDOB());
    }

    @Override
    public String toString() {
        return super.toString() + " {" + studentID + "}\n";
    }
}
