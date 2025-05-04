// Jason Vo
// OCCC Spring 2025
// Advanced Java
// Unit 4 Homework - Person / RegisteredPerson / OCCCPerson (without OCCCDate)

import java.io.Serializable;

public class Person implements Serializable {
	
	// private data fields
	private String firstName;
	private String lastName;
	
	
	// constructors, used within the main/test program that is used to create a Person object, that is then filled with data
	// general parameterized constructors that accept arguments
	public Person (String firstName, String lastName) {
		this.firstName = firstName; // the object's data is updated, specified with the "this" keyword
		this.lastName = lastName;
	}
	

	
	
	// copy constructor that takes in another instance of the Person class
	public Person (Person p) {
		this.firstName = p.firstName;
		this.lastName = p.lastName;
	}
	
	
	
	// getters and setters / accessors and mutators
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	

	
	
	// toString method so that when calling the object in test program, it will return a 
	// statement describing the object and its properties/state rather than just a pointer to its address in memory
	@Override
	public String toString() {
		return (lastName + ", " + firstName);
	}
	
	
	// compare a person instance to another person
	// ignore case on the first and last names
	public boolean equals(Person p) {
		return firstName.equalsIgnoreCase(p.firstName) &&
				lastName.equalsIgnoreCase(p.lastName);
	}
	
	
	// member functions, what the person object can do
	public void eat() {
		System.out.println(getClass().getName() + " " + toString() + 
				" is eating!"); // calling the class name/type (Person), and the toString to display the specific person's data


}
	
	public void sleep() {
		System.out.println(getClass().getName() + " " + toString() + 
				" is sleeping!");
	}
	
	public void play() {
		System.out.println(getClass().getName() + " " + toString() + 
				" is playing!");
	}
	
	public void run() {
		System.out.println(getClass().getName() + " " + toString() + 
				" is running!");
	}

}
