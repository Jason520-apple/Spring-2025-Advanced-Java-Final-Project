// OCCC Spring 2025
// Advanced Java
// Unit 8 Project - Person GUI

public class RegisteredPerson extends Person {

    // Government ID associated with this registered person
    private String govID;

    // Full constructor
    public RegisteredPerson(String firstName, String lastName, OCCCDate dob, String govID) {
        super(firstName, lastName, dob);
        this.govID = govID;
    }

    // Construct from Person with added gov ID
    public RegisteredPerson(Person p, String govID) {
        super(p.getFirstName(), p.getLastName());
        this.govID = govID;
    }

    // Copy constructor
    public RegisteredPerson(RegisteredPerson p) {
        super(p);
        this.govID = p.govID;
    }

    // Getter for government ID
    public String getGovernmentID() {
        return govID;
    }

    // Setter for government ID
    public void setGovernmentID(String govID) {
        this.govID = govID;
    }

    // Equality comparison with another RegisteredPerson
    public boolean equals(RegisteredPerson p) {
        return this.govID.equalsIgnoreCase(p.govID);
    }

    // Comparison ignoring govID — only name match
    public boolean equals(Person p) {
        return this.getFirstName().equalsIgnoreCase(p.getFirstName()) &&
               this.getLastName().equalsIgnoreCase(p.getLastName());
    }

    // Human-readable representation
    @Override
    public String toString() {
        return super.toString() + " [" + govID + "]";
    }
}
