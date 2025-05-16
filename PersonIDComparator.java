import java.util.Comparator;

/**
 * PersonIDComparator.java
 *
 * A comparator for sorting {@link Person} objects. This comparator prioritizes
 * {@link OCCCPerson} instances, sorting them by their student ID in a case-insensitive,
 * ascending order. Non-OCCCPerson instances are grouped together after OCCCPersons.
 *
 * The specific order among non-OCCCPerson instances, or between a non-OCCCPerson
 * and another non-OCCCPerson, is not defined by this comparator (they are treated as equal
 * for the purpose of student ID comparison).
 *
 * Demonstrates:
 * - Implementation of the Comparator interface for custom sorting logic
 * - Type checking using 'instanceof' within a comparator
 * - Case-insensitive string comparison for sorting criteria
 * - Defining a sort order that prioritizes certain subtypes
 *
 * Author: Amida Fombutu
 * Course: CS2463 Advanced Java – Spring 2025
 */
public class PersonIDComparator implements Comparator<Person> {

  
    @Override
    public int compare(Person p1, Person p2) {
        boolean isP1OCCCPerson = p1 instanceof OCCCPerson;
        boolean isP2OCCCPerson = p2 instanceof OCCCPerson;

        if (isP1OCCCPerson && isP2OCCCPerson) {
            // Both are OCCCPersons, compare by student ID (case-insensitive)
            return ((OCCCPerson) p1).getStudentID().compareToIgnoreCase(((OCCCPerson) p2).getStudentID());
        } else if (isP1OCCCPerson) {
            // p1 is an OCCCPerson, p2 is not; p1 comes first.
            return -1;
        } else if (isP2OCCCPerson) {
            // p2 is an OCCCPerson, p1 is not; p2 comes first (so p1 comes after).
            return 1;
        } else {
      
            return 0;
        }
    }
}