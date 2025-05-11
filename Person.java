


import java.io.Serializable;

/**
 * Represents a general person with basic identity attributes.
 * Implements Comparable to enable sorting by last name.
 */
public class Person implements Serializable, Comparable<Person> {
    private static final long serialVersionUID = 1L;

    protected String firstName;
    protected String lastName;
    protected OCCCDate dateOfBirth;

    /**
     * Constructor to initialize a Person object.
     * @param firstName First name of the person.
     * @param lastName Last name of the person.
     * @param dateOfBirth Date of birth using the OCCCDate class.
     */
    public Person(String firstName, String lastName, OCCCDate dateOfBirth) {
        if (firstName == null || lastName == null || dateOfBirth == null) {
            throw new IllegalArgumentException("Name and date of birth must not be null.");
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
    }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public OCCCDate getDateOfBirth() { return dateOfBirth; }

    public void setDateOfBirth(OCCCDate dob) { this.dateOfBirth = dob; }

    /**
     * Allows sorting people by last name (case-insensitive).
     */
    @Override
    public int compareTo(Person other) {
        return this.lastName.compareToIgnoreCase(other.lastName);
    }

    @Override
    public String toString() {
        return "Person: " + firstName + " " + lastName + ", DOB: " + dateOfBirth;
    }
}
