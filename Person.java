// Jason Vo
// OCCC Spring 2025
// Advanced Java
// Unit 4 Homework - Person / RegisteredPerson / OCCCPerson (without OCCCDate)

import java.io.Serializable;
import java.time.Year;
import java.util.Calendar;

public class Person implements Serializable, Comparable {

	// private data fields
	private String firstName;
	private String lastName;
	private OCCCDate dob;

	// constructors, used within the main/test program that is used to create a
	// Person object, that is then filled with data
	// general parameterized constructors that accept arguments
	public Person(String firstName, String lastName) {
		this.firstName = firstName; // the object's data is updated, specified with the "this" keyword
		this.lastName = lastName;
	}

	public Person(String firstName, String lastName, OCCCDate dob) {
		this.firstName = firstName; // the object's data is updated, specified with the "this" keyword
		this.lastName = lastName;
		this.dob = dob;
	}

	// copy constructor that takes in another instance of the Person class
	public Person(Person p) {
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

	public OCCCDate getDOB() {
		return this.dob;
	}

	// truncated not rounded, this acts like a floor function
	public int getAge() {

		OCCCDate currentDate = new OCCCDate();
		OCCCDate personDOB = this.dob;

		int age = currentDate.getYear() - personDOB.getYear();

		if (currentDate.getMonthNumber() < personDOB.getMonthNumber()
				|| currentDate.getMonthNumber() == personDOB.getMonthNumber()
						&& currentDate.getDayOfMonth() < personDOB.getDayOfMonth()) {
			age--; // to truncate and not overestimate, will subtract by 1 if not on same month or
					// day
		}

		return age;
	}

	// toString method so that when calling the object in test program, it will
	// return a
	// statement describing the object and its properties/state rather than just a
	// pointer to its address in memory
	@Override
	public String toString() {
		return lastName + ", " + firstName + " (" + dob + ")";
	}

	// compare a person instance to another person
	// ignore case on the first and last names
	public boolean equals(Person p) {
		return firstName.equalsIgnoreCase(p.firstName) && lastName.equalsIgnoreCase(p.lastName);
	}

	// member functions, what the person object can do
	public void eat() {
		System.out.println(getClass().getName() + " " + toString() + " is eating!"); // calling the class name/type
																						// (Person), and the toString to
																						// display the specific person's
																						// data

	}

	public void sleep() {
		System.out.println(getClass().getName() + " " + toString() + " is sleeping!");
	}

	public void play() {
		System.out.println(getClass().getName() + " " + toString() + " is playing!");
	}

	public void run() {
		System.out.println(getClass().getName() + " " + toString() + " is running!");
	}

	// compareTo function from extending Comparable
	@Override
	public int compareTo(Object o) {

		// must cast the object into what we want to compare
		Person compared = (Person) o;

		// compare in the order last name, first name, date of birth. You'll need to
		// "extend comparable" for both
		// Person and OCCCDate. Compare dates in the order year - month - day as always.

		// -1 if less than, 1 if greater than, 0 if equal
		// will use nested if else statements, using compareTo instead of == to compare
		// Strings

		// last name
		if (this.getLastName().compareTo(compared.getLastName()) > 0) {
			return 1; // this means that this object is greater than and will go after the one that it
						// is being compared to
		} else if (this.getLastName().compareTo(compared.getLastName()) < 0) {
			return -1; // this object is less than the one it is being compared to, goes before
		}

		else { // in the case that the last names are both equal, move on to the first name and
				// compare those

			if (this.getFirstName().compareTo(compared.getFirstName()) > 0) {
				return 1;
			} else if (this.getFirstName().compareTo(compared.getFirstName()) < 0) {
				return -1;
			}

			// compare the date of birth
			else {

				if (this.getDOB().compareTo(compared.getDOB()) > 0) {
					return 1;
				} else if (this.getDOB().compareTo(compared.getDOB()) < 0) {
					return -1;
				}

				// if the last name, first name, and the DOB are equal
				else {
					return 0;
				}

			}

		}

	}

}
