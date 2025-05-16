import java.util.Comparator;

/**
 * PersonDateOfBirthComparator.java
 *
 * A comparator for sorting {@link Person} objects based on their date of birth
 * in ascending order (chronological order, earliest date first).
 * It assumes that the {@code Person} objects have a valid, non-null
 * {@code OCCCDate} object representing their date of birth.
 *
 * The comparison is achieved by converting the year, month, and day of birth
 * into a single integer (in YYYYMMDD format) for each person and then
 * comparing these integers.
 *
 * Demonstrates:
 * - Implementation of the Comparator interface for custom sorting logic
 * - Date comparison by converting date components to a comparable integer
 * - Sorting objects based on a chronological attribute
 *
 * Author: Amida Fombutu
 * Course: CS2463 Advanced Java – Spring 2025
 */
public class PersonDateOfBirthComparator implements Comparator<Person> {

   
    @Override
    public int compare(Person p1, Person p2) {
     

        // Convert date of birth for p1 to an integer YYYYMMDD
        int date1Numeric = p1.getDateOfBirth().getYear() * 10000 +
                           p1.getDateOfBirth().getMonthNumber() * 100 +
                           p1.getDateOfBirth().getDayOfMonth();

        // Convert date of birth for p2 to an integer YYYYMMDD
        int date2Numeric = p2.getDateOfBirth().getYear() * 10000 +
                           p2.getDateOfBirth().getMonthNumber() * 100 +
                           p2.getDateOfBirth().getDayOfMonth();

        // Compare the two numeric representations of the dates
        return Integer.compare(date1Numeric, date2Numeric);
    }
}