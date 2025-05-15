// Jason Vo
// OCCC Spring 2025
// Advanced Java
// Unit 4 Homework - Person / RegisteredPerson / OCCCPerson (without OCCCDate)

public class OCCCPerson extends RegisteredPerson {
	
	//studentID string
	private String studentID;
	
	// constructors
	public OCCCPerson (RegisteredPerson p, String studentID) {
		super(p.getFirstName(), p.getLastName(), p.getGovernmentID()); // pass in the parameters of a registered person to parent constructor
		this.studentID = studentID;
	}
	
	public OCCCPerson(OCCCPerson p) {
		super(p);
		this.studentID = p.studentID;
	}
	
	//getter
	public String getStudentID() {
		return studentID;
	}
	
	//comparing two OCCCPerson objects
	public boolean equals(OCCCPerson p) {
		return this.studentID.equalsIgnoreCase(p.studentID);
	}
	
	// method overloading, for if a registered person, or a person object is passed in for comparison
	public boolean equals(RegisteredPerson p) {
		//compare only RegisteredPerson fields, ignore student ID
		return this.getGovernmentID().equalsIgnoreCase(p.getGovernmentID()) && this.getFirstName().equalsIgnoreCase(p.getFirstName()) && this.getLastName().equalsIgnoreCase(p.getLastName());
		//must use getters since govID, firstName, and lastName are part of parent classes not directly in this OCCCPerson class
	}
	
	public boolean equals(Person p) {
		//compare only names and DOB
		return this.getFirstName().equalsIgnoreCase(p.getFirstName()) && this.getLastName().equalsIgnoreCase(p.getLastName());
	}
	
	// toString method
	@Override
	public String toString() {
		return (super.toString() + " {" + studentID + "}\n");
	}
	
	
	
	
}
