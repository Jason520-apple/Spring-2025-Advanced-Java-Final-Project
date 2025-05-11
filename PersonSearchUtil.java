// --- File: PersonSearchUtil.java ---


import java.util.List;

/**
 * Utility class that implements recursive binary search for OCCCPerson objects.
 */
public class PersonSearchUtil {

    /**
     * Performs a recursive binary search on a sorted list of OCCCPerson by student ID.
     *
     * @param list      A list of OCCCPerson, sorted by student ID.
     * @param studentID The student ID to find.
     * @param low       Starting index.
     * @param high      Ending index.
     * @return The matching OCCCPerson, or null if not found.
     */
    public static OCCCPerson binarySearchByID(List<OCCCPerson> list, String studentID, int low, int high) {
        if (low > high) return null;

        int mid = (low + high) / 2;
        OCCCPerson midPerson = list.get(mid);

        int cmp = midPerson.getStudentID().compareToIgnoreCase(studentID);
        if (cmp == 0) return midPerson;
        else if (cmp < 0) return binarySearchByID(list, studentID, mid + 1, high);
        else return binarySearchByID(list, studentID, low, mid - 1);
    }
}
