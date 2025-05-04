// Jason Vo
// OCCC Spring 2025
// Advanced Java
// Unit 4 Homework - Person / RegisteredPerson / OCCCPerson (without OCCCDate)

import java.util.Scanner;

public class TestPerson {

	public static void main(String[] args) {

		//scanner for user input
		Scanner input = new Scanner(System.in);
		
		
		//Prompt the user for a data for a Person (first and last name)
		System.out.println("Hello, this is the TestPerson program. It will create a Person, RegisteredPerson, and a OCCCPerson object. " +
							"First we will create a Person object. Please enter the first name: ");
		String personFirstName = input.nextLine();
		
		System.out.println("Please enter the last name: ");
		String personLastName = input.nextLine();
		
		
		//Create and Display that person (using toString)
		System.out.println("\nCreating and displaying Person object.");
		
		
		// create an instance of the person class, then use the constructor to fill in data
		Person myPerson = new Person(personFirstName, personLastName);
		System.out.println(myPerson);
		
		
		//Prompt the user for the data for a RegisteredPerson (first name, last name, and government ID)
		System.out.println("\nNext we will create a RegisteredPerson. Please enter the first name: ");
		String regPersonFN = input.nextLine();
		
		System.out.println("Please enter the last name: ");
		String regPersonLN = input.nextLine();
		
		System.out.println("Please enter the RegisteredPerson government ID: ");
		String govID = input.nextLine();
		
		
		//Create and Display that RegisteredPerson (using toString)
		System.out.println("\nCreating and displaying RegisteredPerson object: ");
		
		RegisteredPerson myRegPerson = new RegisteredPerson(regPersonFN, regPersonLN, govID);
		System.out.println(myRegPerson);
		
		
		//Prompt the user for the data for an OCCCPerson (first name, last name, government ID, and student ID)
		System.out.println("\nWe will create an OCCCPerson using the parameter constructor. Please enter the first name: ");
		String OCCCFN = input.nextLine();
		
		System.out.println("Please enter the last name: ");
		String OCCCLN = input.nextLine();
		
		System.out.println("Please enter the government ID: ");
		String OCCCgovID = input.nextLine();
		
		System.out.println("Please enter the studentID: ");
		String OCCCstudentID = input.nextLine();
		
		
		//Create and Display that OCCCPerson (using toString)
		System.out.println("\nCreating and displaying OCCCPerson object: ");
		
		//creating and using a RegisteredPerson object to create a OCCCPerson
		RegisteredPerson temp = new RegisteredPerson(OCCCFN, OCCCLN, OCCCgovID);
		
		// passing in a RegisteredPerson object named temp to use the RegisteredPerson constructor
		OCCCPerson myOCCCPerson = new OCCCPerson(temp, OCCCstudentID);
		
		System.out.println(myOCCCPerson);
		
		
		//Prompt the user for a government ID, then create a new RegisteredPerson using that ID and your existing Person
		System.out.println("\nPlease enter a government ID to create a new RegisteredPerson from the existing Person: ");
		String govID2 = input.nextLine();
		
		
		//using the copy constructor, pass in myPerson(the existing Person) and govID2
		RegisteredPerson copyRegPerson = new RegisteredPerson(myPerson, govID2);
		
		
		//Display that RegisteredPerson (using toString)
		System.out.println("\nPrinting another RegisteredPerson: ");
		System.out.println(copyRegPerson);
		
		
		//Prompt the user for a student ID, then create a new OCCCPerson using that ID and the newly-created RegisteredPerson
		System.out.println("\nNow, we will create a OCCCPerson from our newly created RegisteredPerson. Please enter the student ID: ");
		String OCCCID2 = input.nextLine();
		
		//creating object, pass in the copyRegPerson object then OCCCID2
		OCCCPerson copyOCCCPerson = new OCCCPerson(copyRegPerson, OCCCID2);
		
		
		//Display that OCCCPerson (using toString)
		System.out.println("\nPrinting the new OCCCPerson: ");
		System.out.println(copyOCCCPerson);
		
		
		System.out.println("Program has finished running :)");
		
		input.close();
	}

}
