// File: PersonIDComparator.java


import java.util.Comparator;

/**
 * Sorts OCCCPersons by student ID, other types fall back to bottom.
 */
public class PersonIDComparator implements Comparator<Person> {
    @Override
    public int compare(Person p1, Person p2) {
        if (p1 instanceof OCCCPerson && p2 instanceof OCCCPerson) {
            return ((OCCCPerson) p1).getStudentID().compareToIgnoreCase(((OCCCPerson) p2).getStudentID());
        } else if (p1 instanceof OCCCPerson) {
            return -1; // OCCCPerson comes first
        } else if (p2 instanceof OCCCPerson) {
            return 1;  // OCCCPerson comes first
        }
        return 0; // Non-OCCCPersons considered equal
    }
}
