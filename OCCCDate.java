// --- File: OCCCDate.java ---


import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Custom date class that wraps around GregorianCalendar with formatting options.
 */
public class OCCCDate implements Serializable {
    private static final long serialVersionUID = 1L;

    private int dayOfMonth;
    private int monthOfYear;
    private int year;
    private GregorianCalendar gc;

    private boolean dateFormat = FORMAT_US;
    private boolean dateStyle = STYLE_NUMBERS;
    private boolean dateDayName = SHOW_DAY_NAME;

    public static final boolean FORMAT_US = true;
    public static final boolean FORMAT_EURO = false;
    public static final boolean STYLE_NUMBERS = true;
    public static final boolean STYLE_NAMES = false;
    public static final boolean SHOW_DAY_NAME = true;
    public static final boolean HIDE_DAY_NAME = false;

    // Default constructor initializes to current date
    public OCCCDate() {
        this.gc = new GregorianCalendar();
        this.dayOfMonth = gc.get(Calendar.DAY_OF_MONTH);
        this.monthOfYear = gc.get(Calendar.MONTH) + 1;
        this.year = gc.get(Calendar.YEAR);
    }

    // Constructor with specific date
    public OCCCDate(int day, int month, int year) {
        this.gc = new GregorianCalendar(year, month - 1, day);
        this.dayOfMonth = gc.get(Calendar.DAY_OF_MONTH);
        this.monthOfYear = gc.get(Calendar.MONTH) + 1;
        this.year = gc.get(Calendar.YEAR);
    }

    public String getDayName() {
        return gc.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, java.util.Locale.US);
    }

    public int getDayOfMonth() { return dayOfMonth; }
    public int getMonthNumber() { return monthOfYear; }
    public int getYear() { return year; }

    public void setDateFormat(boolean df) { dateFormat = df; }
    public void setStyleFormat(boolean sf) { dateStyle = sf; }
    public void setDayName(boolean nf) { dateDayName = nf; }

    @Override
    public String toString() {
        String formattedDate;
        if (dateStyle == STYLE_NUMBERS) {
            formattedDate = dateFormat == FORMAT_US ?
                monthOfYear + "/" + dayOfMonth + "/" + year :
                dayOfMonth + "/" + monthOfYear + "/" + year;
        } else {
            String monthName = gc.getDisplayName(Calendar.MONTH, Calendar.LONG, java.util.Locale.US);
            formattedDate = dateFormat == FORMAT_US ?
                monthName + " " + dayOfMonth + ", " + year :
                dayOfMonth + " " + monthName + " " + year;
        }
        return dateDayName ? getDayName() + ", " + formattedDate : formattedDate;
    }
}