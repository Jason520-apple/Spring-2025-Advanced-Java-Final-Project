// --- File: OCCCDate.java ---


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Custom date class that wraps around GregorianCalendar with formatting options.
 * Throws OCCCDateException for invalid date components.
 */
public class OCCCDate implements Serializable {
    private static final long serialVersionUID = 1L;

    private int dayOfMonth;
    private int monthOfYear; // 1-indexed (January is 1)
    private int year;
    private transient GregorianCalendar gc; // Marked transient, will be reconstructed

    private boolean dateFormat = FORMAT_US;
    private boolean dateStyle = STYLE_NUMBERS;
    private boolean dateDayName = SHOW_DAY_NAME;

    public static final boolean FORMAT_US = true;
    public static final boolean FORMAT_EURO = false;
    public static final boolean STYLE_NUMBERS = true;
    public static final boolean STYLE_NAMES = false;
    public static final boolean SHOW_DAY_NAME = true;
    public static final boolean HIDE_DAY_NAME = false;

    /**
     * Default constructor initializes to current date.
     */
    public OCCCDate() {
        this.gc = new GregorianCalendar();
        this.dayOfMonth = gc.get(Calendar.DAY_OF_MONTH);
        this.monthOfYear = gc.get(Calendar.MONTH) + 1; // Calendar.MONTH is 0-indexed
        this.year = gc.get(Calendar.YEAR);
    }

    /**
     * Constructor with specific date.
     * Validates that the provided day, month (1-12), and year form a valid date
     * according to GregorianCalendar rules without automatic adjustment of invalid values.
     * @param day the day of the month (1-31)
     * @param month the month of the year (1-12)
     * @param year the year
     * @throws OCCCDateException if the provided date components do not form a valid calendar date.
     */
    public OCCCDate(int day, int month, int year) throws OCCCDateException {
        // Basic range checks for user-friendliness before trying Calendar
        if (year < 1582) { // Gregorian calendar adopted at different times, but 1582 is a common reference
            throw new OCCCDateException("Invalid year: " + year + ". Year must be 1582 or later.");
        }
        if (month < 1 || month > 12) {
            throw new OCCCDateException("Invalid month: " + month + ". Month must be between 1 and 12.");
        }
        // Day check is more complex due to month lengths/leap years, delegate to Calendar
        if (day < 1 || day > 31) { // Simplistic preliminary check for day
             throw new OCCCDateException("Invalid day: " + day + ". Day must be between 1 and 31 (and valid for the month).");
        }

        GregorianCalendar tempCalendar = new GregorianCalendar();
        tempCalendar.setLenient(false); // Crucial for strict validation

        try {
            // Calendar.MONTH is 0-indexed (0 for January)
            tempCalendar.set(year, month - 1, day);
            // Force validation by trying to get a field.
            // This will throw IllegalArgumentException if date is invalid with lenient set to false.
            tempCalendar.getTimeInMillis(); // Accessing the time forces validation
        } catch (IllegalArgumentException e) {
            throw new OCCCDateException("Invalid date: " + month + "/" + day + "/" + year + ". Not a valid calendar day (e.g., Feb 30).", e);
        }

        // If validation passed, assign to the instance's fields
        this.year = year;
        this.monthOfYear = month;
        this.dayOfMonth = day;

        // Initialize the transient gc field
        reinitializeCalendar();

        // Final check: ensure the input matches the calendar's interpretation
        // This is per the spec: "only allow dates where the user input matches the corresponding GregorianCalendar values"
        if (this.gc.get(Calendar.YEAR) != year ||
            (this.gc.get(Calendar.MONTH) + 1) != month ||
            this.gc.get(Calendar.DAY_OF_MONTH) != day) {
            throw new OCCCDateException("Input date " + month + "/" + day + "/" + year +
                                        " was interpreted differently by the calendar: " +
                                        (this.gc.get(Calendar.MONTH) + 1) + "/" + this.gc.get(Calendar.DAY_OF_MONTH) + "/" + this.gc.get(Calendar.YEAR) +
                                        ". This usually means the day is invalid for the given month/year (e.g. April 31st).");
        }
    }

    private void reinitializeCalendar() {
        if (this.year != 0 && this.monthOfYear != 0 && this.dayOfMonth != 0) {
            this.gc = new GregorianCalendar(this.year, this.monthOfYear - 1, this.dayOfMonth);
        } else {
            // Should not happen if constructors are used properly
            this.gc = new GregorianCalendar(); // Default to current if state is invalid
        }
    }

    public String getDayName() {
        if (gc == null) reinitializeCalendar();
        return gc.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
    }

    public int getDayOfMonth() { return dayOfMonth; }
    public int getMonthNumber() { return monthOfYear; } // Already 1-indexed
    public int getYear() { return year; }

    public void setDateFormat(boolean df) { dateFormat = df; }
    public void setStyleFormat(boolean sf) { dateStyle = sf; }
    public void setDayName(boolean nf) { dateDayName = nf; }

    @Override
    public String toString() {
        if (gc == null) reinitializeCalendar();
        String formattedDate;
        if (dateStyle == STYLE_NUMBERS) {
            formattedDate = dateFormat == FORMAT_US ?
                String.format("%d/%d/%d", monthOfYear, dayOfMonth, year) :
                String.format("%d/%d/%d", dayOfMonth, monthOfYear, year);
        } else {
            String monthName = gc.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
            formattedDate = dateFormat == FORMAT_US ?
                String.format("%s %d, %d", monthName, dayOfMonth, year) :
                String.format("%d %s %d", dayOfMonth, monthName, year);
        }
        return dateDayName ? getDayName() + ", " + formattedDate : formattedDate;
    }

    // Handle deserialization to ensure 'gc' is valid
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        in.defaultReadObject(); // Reads non-transient fields
        // Re-initialize transient fields or fields that need setup post-deserialization
        reinitializeCalendar();
    }
}