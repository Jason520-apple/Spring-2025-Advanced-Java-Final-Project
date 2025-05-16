// OCCC Spring 2025
// Advanced Java
// Unit 8 Project - Person GUI

//import GregorianCalendar and Calendar to use 

import java.util.GregorianCalendar;
import java.io.IOException;
import java.io.Serializable; // Required for serialization
import java.util.Calendar;
import java.util.Locale; // Amida Fombutu: Added for getDisplayName



public class OCCCDate implements Comparable<OCCCDate>, Serializable { // Amida Fombutu: Parameterized Comparable

    // private variables
    private static final long serialVersionUID = 1L; //added by Amida
    private int dayOfMonth;
    private int monthOfYear; // 1-indexed (January is 1 as per comments)
    private int year;

    // private helper object of type GregorianCalendar
    // or might carry too much state. It should be reconstructed.
    private transient GregorianCalendar gc;

    // boolean variables
    private boolean dateFormat = FORMAT_US; // default is DATE_FORMAT_US
    private boolean dateStyle = STYLE_NUMBERS; // default is DATE_STYLE_NUMBERS
    private boolean dateDayName = SHOW_DAY_NAME; // default is SHOW_DAY_NAME

    // static variables (constants)
    public static final boolean FORMAT_US = true; // true bc of default
    public static final boolean FORMAT_EURO = false;
    public static final boolean STYLE_NUMBERS = true;// default
    public static final boolean STYLE_NAMES = false;
    public static final boolean SHOW_DAY_NAME = true;// default
    public static final boolean HIDE_DAY_NAME = false;

    // default constructor, use current date and time
    public OCCCDate() {
        // constructor will create a GregorianCalendar object and use that to set the day, month and year fields
        this.gc = new GregorianCalendar(); 
        this.dayOfMonth = gc.get(Calendar.DAY_OF_MONTH);
        this.monthOfYear = gc.get(Calendar.MONTH) + 1; // adding 1 because the gregorian calendar goes from 0-11
        this.year = gc.get(Calendar.YEAR);
    }

    //instead of using try-catch block, make the constructor throw an exception that the GUI will have to catch and reprompt the user
    public OCCCDate(int day, int month, int year) throws InvalidOCCCDateException {

        GregorianCalendar tempCal = new GregorianCalendar();
        tempCal.setLenient(false); // Crucial for strict validation

        try {
            // Calendar.MONTH is 0-indexed (0 for January)
            tempCal.set(year, month - 1, day);
            // Force validation by trying to get a field.
            // This will throw IllegalArgumentException if date is invalid with lenient set to false.
            tempCal.getTimeInMillis(); // Accessing the time forces validation of day, month, year combination
        } catch (IllegalArgumentException e) {
            // Amida Fombutu: Provide a more descriptive message for the exception.
            throw new InvalidOCCCDateException("Invalid date component: " + month + "/" + day + "/" + year + ". Not a valid calendar day (e.g., Feb 30 or April 31). Caused by: " + e.getMessage());
        }

        // If validation passed, assign to the instance's fields
        this.year = year;
        this.monthOfYear = month; // month is 1-indexed
        this.dayOfMonth = day;

        // Initialize the instance's gc field based on the now validated date
        this.gc = new GregorianCalendar(this.year, this.monthOfYear - 1, this.dayOfMonth);


        if (this.gc.get(Calendar.YEAR) != this.year ||
            (this.gc.get(Calendar.MONTH) + 1) != this.monthOfYear || // gc.MONTH is 0-indexed
            this.gc.get(Calendar.DAY_OF_MONTH) != this.dayOfMonth) {
            throw new InvalidOCCCDateException("Date interpretation mismatch for " + month + "/" + day + "/" + year +
                                            ". Calendar interpreted as: " +
                                            (this.gc.get(Calendar.MONTH) + 1) + "/" + 
                                            this.gc.get(Calendar.DAY_OF_MONTH) + "/" + 
                                            this.gc.get(Calendar.YEAR));
        }
    }


    public OCCCDate(GregorianCalendar gcInput) { 
        if (gcInput == null) { // Amida Fombutu: Added null check
            throw new IllegalArgumentException("GregorianCalendar input cannot be null.");
        }
        // extracting from the gc that is passed in this constructor
        this.dayOfMonth = gcInput.get(Calendar.DAY_OF_MONTH);
        this.monthOfYear = gcInput.get(Calendar.MONTH) + 1; // adding 1
        this.year = gcInput.get(Calendar.YEAR);
        this.gc = new GregorianCalendar(this.year, this.monthOfYear - 1, this.dayOfMonth);
    }

    // copy constructor
    public OCCCDate(OCCCDate d) {
        if (d == null) { // Amida Fombutu: Added null check
            throw new IllegalArgumentException("OCCCDate to copy from cannot be null.");
        }
        this.dayOfMonth = d.dayOfMonth;
        this.monthOfYear = d.monthOfYear;
        this.year = d.year;
        
        this.dateFormat = d.dateFormat;
        this.dateStyle = d.dateStyle;
        this.dateDayName = d.dateDayName;

        // Initialize gc for the new object
        this.gc = new GregorianCalendar(this.year, this.monthOfYear - 1, this.dayOfMonth);
    }




    // getters and setters 
    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public String getDayName() {
        if (gc == null) {
            reinitializeCalendar();
        }
        return gc.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
    }

    public int getMonthNumber() {
        return monthOfYear; // Already 1-indexed
    }

    public String getMonthName() {
        // Amida Fombutu: Ensure gc is initialized
        if (gc == null) {
            reinitializeCalendar();
        }
        // Amida Fombutu: Using GregorianCalendar's built-in display name.
        return gc.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
    }

    public int getYear() {
        return year;
    }

    public void setDateFormat(boolean df) {
        this.dateFormat = df;
    }

    public void setStyleFormat(boolean sf) { // Amida Fombutu: Renamed parameter for clarity
        this.dateStyle = sf;
    }

    public void setDayName(boolean nf) { // Amida Fombutu: Renamed parameter for clarity
        this.dateDayName = nf;
    }

    public int getDifferenceInYears() {
        return getDifferenceInYears(new OCCCDate()); // Uses current date
    }

    public int getDifferenceInYears(OCCCDate d) {
        if (d == null) { // Amida Fombutu: Added null check
            throw new IllegalArgumentException("Cannot compare with a null OCCCDate.");
        }
        // Amida Fombutu: More precise age calculation (similar to Person.getAge())
        int age = this.year - d.year;
        if (this.monthOfYear < d.monthOfYear ||
            (this.monthOfYear == d.monthOfYear && this.dayOfMonth < d.dayOfMonth)) {
            age--;
        }

        return Math.abs(this.year - d.year); // Matches original's likely intent of simple year span
    }

    // Amida Fombutu: equals method should override Object.equals
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        OCCCDate other = (OCCCDate) obj;
        return this.dayOfMonth == other.dayOfMonth &&
               this.monthOfYear == other.monthOfYear &&
               this.year == other.year;
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(dayOfMonth);
        result = 31 * result + Integer.hashCode(monthOfYear);
        result = 31 * result + Integer.hashCode(year);
        return result;
    }

    @Override
    public String toString() {
        // Amida Fombutu: Ensure gc is initialized for getDayName/getMonthName if used.
        if (gc == null) {
            reinitializeCalendar();
        }

        String formattedDate;
        if (dateStyle == STYLE_NUMBERS) {
            // Using String.format for cleaner formatting, especially with leading zeros if desired (not implemented here)
            if (dateFormat == FORMAT_US) { // MM/DD/YYYY
                formattedDate = String.format("%d/%d/%d", monthOfYear, dayOfMonth, year);
            } else { // DD/MM/YYYY (FORMAT_EURO)
                formattedDate = String.format("%d/%d/%d", dayOfMonth, monthOfYear, year);
            }
        } else { // STYLE_NAMES
            String monthNameStr = getMonthName(); // Uses gc.getDisplayName
            if (dateFormat == FORMAT_US) { // MonthName DD, YYYY
                formattedDate = String.format("%s %d, %d", monthNameStr, dayOfMonth, year);
            } else { // DD MonthName YYYY (FORMAT_EURO)
                formattedDate = String.format("%d %s %d", dayOfMonth, monthNameStr, year);
            }
        }

        if (dateDayName == SHOW_DAY_NAME) {
            return getDayName() + ", " + formattedDate; // getDayName uses gc.getDisplayName
        } else {
            return formattedDate;
        }
    }

    // Compare dates in the order year - month - day as always.
    @Override
    public int compareTo(OCCCDate other) { // Amida Fombutu: Parameterized type
        if (other == null) { // Amida Fombutu: Added null check
  
            throw new NullPointerException("Cannot compare OCCCDate to null.");
        }

        if (this.year < other.year) return -1;
        if (this.year > other.year) return 1;

        // Years are equal, compare months
        if (this.monthOfYear < other.monthOfYear) return -1;
        if (this.monthOfYear > other.monthOfYear) return 1;

        // Months are equal, compare days
        if (this.dayOfMonth < other.dayOfMonth) return -1;
        if (this.dayOfMonth > other.dayOfMonth) return 1;

        return 0; // Dates are equal
    }

    // Amida Fombutu: Method to reinitialize transient GregorianCalendar after deserialization
    private void reinitializeCalendar() {
        // Check if year, month, day are valid before creating;
        // they should be if deserialized from a valid OCCCDate object.
        if (this.year != 0 && this.monthOfYear != 0 && this.dayOfMonth != 0) {
             this.gc = new GregorianCalendar(this.year, this.monthOfYear - 1, this.dayOfMonth);
        } else {

            this.gc = new GregorianCalendar();
           
        }
    }

    // Amida Fombutu: Custom deserialization method to handle transient fields
    private void readObject(java.io.ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        in.defaultReadObject(); // Reads non-transient fields (dayOfMonth, monthOfYear, year, booleans)
        reinitializeCalendar(); // Re-initialize transient gc based on deserialized date components
    }
}