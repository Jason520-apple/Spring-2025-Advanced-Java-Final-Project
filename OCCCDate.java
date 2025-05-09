// Jason Vo
// OCCC Spring 2025
// Advanced Java
// Unit 6 Homework - OCCCDate with Exceptions

//import GregorianCalendar and Calendar to use 

import java.util.GregorianCalendar;
import java.util.Calendar;

public class OCCCDate implements Comparable {

	// private variables
	private int dayOfMonth;
	private int monthOfYear;
	private int year;

	// private helper object of type GregorianCalendar
	private GregorianCalendar gc;

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
		// constructor will create a GregorianCalendar object and use that to set the
		// day
		// month and year fields

		gc = new GregorianCalendar();
		this.dayOfMonth = gc.get(Calendar.DAY_OF_MONTH);
		this.monthOfYear = gc.get(Calendar.MONTH) + 1; // adding 1 because the gregorian calendar goes from 0-11, but we
														// want to use 1-12 in OCCCDate
		this.year = gc.get(Calendar.YEAR);

	}

	//instead of using try-catch block, make the constructor throw an exception that the GUI will have to catch and reprompt the user
	public OCCCDate(int day, int month, int year) throws InvalidOCCCDateException {

		// create a GregorianCalendar object using the data from constructor
		this.dayOfMonth = day;
		this.monthOfYear = month;
		this.year = year;

		gc = new GregorianCalendar(this.year, this.monthOfYear - 1, this.dayOfMonth);

		// use gc methods to create imaginary days
		this.dayOfMonth = gc.get(Calendar.DAY_OF_MONTH);
		this.monthOfYear = gc.get(Calendar.MONTH) + 1; // add one since gc goes from 0-11 and we want 1-12 for
														// OCCCDate months
		this.year = gc.get(Calendar.YEAR);

		gc = new GregorianCalendar(this.year, this.monthOfYear - 1, this.dayOfMonth);

		// calling the boolean method to check
		if (!dateCheckAlgorithm(day, month, year)) {
			throw new InvalidOCCCDateException();
		}
		// note: removed / disabled the try-catch block from the OCCCDate class, since
		// the catch
		// block did not allow the GUI program to catch the error, therefore not
		// reprompting the user if an invalid overflowed date was entered

		// catch (InvalidOCCCDateException e) {
		// System.out.println(e);
		// System.out.println("Date entered was: [" + day + " " + month + " " + year +
		// "]");
		// System.out.println("Adjusted OCCCDate is: " + toString());
		// System.out.println("These dates do not match, therefore this is an invalid
		// date! :(\n");
	}


	public OCCCDate(GregorianCalendar gc) {

		// extracting from the gc that is passed in this constructor
		this.dayOfMonth = gc.get(Calendar.DAY_OF_MONTH);
		this.monthOfYear = gc.get(Calendar.MONTH) + 1; // adding 1 because the gregorian calendar goes from 0-11, but we
														// want to use 1-12 in OCCCDate
		this.year = gc.get(Calendar.YEAR);

	}

	// copy constructor
	public OCCCDate(OCCCDate d) {
		this.dayOfMonth = d.dayOfMonth;
		this.monthOfYear = d.monthOfYear;
		this.year = d.year;

		// subtract one from the month for the GregorianCalendar constructor
		gc = new GregorianCalendar(this.year, this.monthOfYear - 1, this.dayOfMonth);
	}

	// Put the date-check algorithm in a boolean method

	public boolean dateCheckAlgorithm(int day, int month, int year) {

		/*
		 * To see if a date is valid or invalid:
		 * 
		 * Create the private GregorianCalendar object using the given day, month, and
		 * year. Extract the day, month, and year from the GregorianCalendar object.
		 * Compare what was given with what the GregorianCalendar object provides. If
		 * they are a match, the given information was valid. If they do not match, the
		 * given information was not valid so throw an exception.
		 */

		gc = new GregorianCalendar(this.year, this.monthOfYear - 1, this.dayOfMonth); // subtract 1 from the month since
																						// gc goes 0-11, +1 below to
																						// account for this

		// invalid
		if (gc.get(Calendar.DAY_OF_MONTH) != day || gc.get(Calendar.MONTH) + 1 != month
				|| gc.get(Calendar.YEAR) != year) {
			return false;
		} else {
			return true;
		}

	}

	// getters and setters to access and modify variables/data fields from the main
	// program

	// 1, 2, 3…
	public int getDayOfMonth() {
		return dayOfMonth;
	}

	// Sunday, Monday, Tuesday.. // GregorianCalender does this
	public String getDayName() {
		// list of all the days
		String possibleDays[] = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
		int dayNumber = gc.get(Calendar.DAY_OF_WEEK); // will return the day number

		String day = possibleDays[dayNumber - 1];// will have to - 1, since the dayNumber will be from 1-7, however
													// array goes from 0-6
		return day;
	}

	// 1, 2, 3…
	public int getMonthNumber() {
		return monthOfYear;
	}

	// January, February, March…
	public String getMonthName() {
		// like the getDayName method, create a string array with all the months
		String[] possibleMonths = { "January", "February", "March", "April", "May", "June", "July", "August",
				"September", "October", "November", "December" };

		// monthOfYear is the int of the month 1-12
		String actualMonth = possibleMonths[this.monthOfYear - 1]; // -1 to account for the arrays index

		return actualMonth;
	}

	// Gregorian year, e.g. 2020
	public int getYear() {
		return year;
	}

	public void setDateFormat(boolean df) {
		this.dateFormat = df; // update the boolean value
	}

	public void setStyleFormat(boolean sf) {
		this.dateStyle = sf;
	}

	public void setDayName(boolean nf) {
		this.dateDayName = nf;
	}

	// difference in years between this OCCCDate and now
	public int getDifferenceInYears() {
		// get OCCC year, get current year, subtract current year from the OCCC's
		// getYear
		return getDifferenceInYears(new OCCCDate());
	}

	// difference in years between this date and d
	// The one with the parameter will do all of the work. The other will just call
	// it (above)
	public int getDifferenceInYears(OCCCDate d) {
		int yearDifference = Math.abs(d.year - this.year); // the year of the passed in OCCCDate object
		return yearDifference;
	}

	// compare only day, month, and year
	public boolean equals(OCCCDate dob) {
		if ((this.dayOfMonth == dob.dayOfMonth) && (this.monthOfYear == dob.monthOfYear) && (this.year == dob.year)) {
			return true;
		} else {
			return false;
		}
	} // instead of using equalsIgnoreCase like the person class homework, use ==
		// since this is the int data type (careful, not = which is the assignment
		// operator, use == which compares them)

	public String toString() {

		// US format mm/dd/yyyy or monthName dd, yyyy

		if (dateFormat == FORMAT_US) { // remember to use == for comparison, = is for assignment

			// for numbers
			if (dateStyle == STYLE_NUMBERS) { // default is STYLE_NUMBERS which is true

				String numStrUS = (monthOfYear + " / " + dayOfMonth + " / " + year);

				// if SHOW_DAY_NAME is set to true then return the String with the day name
				// appended to the beginning of the number string (default)
				if (dateDayName == SHOW_DAY_NAME) {
					numStrUS = getDayName() + ", " + numStrUS;
				}

				return numStrUS;
			}

			// for names
			else { // this means that the date style is is not numbers, meaning STYLE_NAMES

				String namesStrUS = (getMonthName() + " " + dayOfMonth + ", " + year);

				if (dateDayName == SHOW_DAY_NAME) {
					// modify and add getDayName() to beginning of the String that will be returned
					namesStrUS = getDayName() + ", " + namesStrUS;
				}

				return namesStrUS;
			}
		}

		// Euro format dd/mm/yyyy or dd monthName yyyy

		else if (dateFormat == FORMAT_EURO) { // remember to use == for comparison, = is for assignment

			// for numbers
			if (dateStyle == STYLE_NUMBERS) { // default is STYLE_NUMBERS which is true

				String numStrEURO = (dayOfMonth + " / " + monthOfYear + " / " + year);

				// if SHOW_DAY_NAME is set to true then return the String with the day name
				// appended to the beginning of the number string (default)
				if (dateDayName == SHOW_DAY_NAME) {
					numStrEURO = getDayName() + ", " + numStrEURO;
				}

				return numStrEURO;
			}

			// for names
			else { // this means that the date style is is not numbers, meaning STYLE_NAMES

				String numStrEURO = (dayOfMonth + " " + getMonthName() + " " + year);

				if (dateDayName == SHOW_DAY_NAME) {
					// modify and add getDayName() to beginning of the String that will be returned
					numStrEURO = getDayName() + ", " + numStrEURO;
				}

				return numStrEURO;
			}
		}

		// Return a default or error string if dateFormat is neither FORMAT_US nor
		// FORMAT_EURO
		else {
			return "Invalid date format";
		}

	}

	// Compare dates in the order year - month - day as always.
	@Override
	public int compareTo(Object o) {

		OCCCDate compare = (OCCCDate) o;

		// year
		if (this.getYear() < compare.getYear()) {
			return -1; // goes before
		} else if (this.getYear() > compare.getYear()) {
			return 1; // goes after
		}

		// month
		else {

			if (this.getMonthNumber() < compare.getMonthNumber()) {
				return -1;
			} else if (this.getMonthNumber() > compare.getMonthNumber()) {
				return 1;
			}

			// day
			else {

				if (this.getDayOfMonth() < compare.getDayOfMonth()) {
					return -1;
				} else if (this.getDayOfMonth() > compare.getDayOfMonth()) {
					return 1;
				}

				// if after all of this the days are equal
				else {
					return 0;
				}

			}

		}
	}

}
