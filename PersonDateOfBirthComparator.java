// --- File: PersonDateOfBirthComparator.java ---


import java.util.Comparator;

/**
 * Comparator to sort Person objects by date of birth.
 */
public class PersonDateOfBirthComparator implements Comparator<Person> {
    @Override
    public int compare(Person p1, Person p2) {
        int date1 = p1.getDateOfBirth().getYear() * 10000 +
                    p1.getDateOfBirth().getMonthNumber() * 100 +
                    p1.getDateOfBirth().getDayOfMonth();

        int date2 = p2.getDateOfBirth().getYear() * 10000 +
                    p2.getDateOfBirth().getMonthNumber() * 100 +
                    p2.getDateOfBirth().getDayOfMonth();

        return Integer.compare(date1, date2);
    }
}