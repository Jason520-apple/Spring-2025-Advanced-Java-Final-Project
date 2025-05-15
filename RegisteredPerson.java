// Jason Vo
// OCCC Spring 2025
// Advanced Java
// Unit 4 Homework - Person / RegisteredPerson / OCCCPerson (without OCCCDate)

public class RegisteredPerson extends Person {

	// the RegistedPerson class is a child class of Person and extends the Person class
	// new data field is govID, a String
	private String govID;
	
	// constructors
	public RegisteredPerson (String firstName, String lastName, String govID) {
		super (firstName, lastName); //super keyword to call to the super/parent class to use its constructor 
		this.govID = govID;
	}
	
	public RegisteredPerson (Person p, String govID) {
		super(p.getFirstName(), p.getLastName()); //ensures that the parent class’s state is copied or properly initialized before adding any additional state or behavior
		this.govID = govID;
	}
	
	// copy constructor
	public RegisteredPerson(RegisteredPerson p) {
		super(p);
		this.govID = p.govID; // adds this on top for the RegisteredPerson class
	}
	
	// getter for government ID
	public String getGovernmentID () {
		return govID;
	}
	
	// compare two RegisteredPerson objects
	public boolean equals(RegisteredPerson p) {
		return this.govID.equalsIgnoreCase(p.govID);
	}
	
	public boolean equals(Person p) {
		return this.getFirstName().equalsIgnoreCase(p.getFirstName()) && this.getLastName().equalsIgnoreCase(p.getLastName());
		//compare only Person fields, ignore government ID, so compare the first and last names
		// use this keyword to specify the RegisteredPerson object's data
	}
	
	// toString method
	@Override
	public String toString() {
		return (super.toString() + " [" + govID + "]");
	}
	
}
