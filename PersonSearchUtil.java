import java.util.List;

/**
 * PersonSearchUtil.java
 *
 * A utility class that provides a method for performing a recursive binary search.
 * Specifically, it's designed to search for {@link OCCCPerson} objects within a list,
 * based on their student ID.
 *
 * Demonstrates:
 * - Implementation of a recursive binary search algorithm
 * - String comparison for searching (case-insensitive)
 * - Utility class design with static methods
 *
 * Author: Amida Fombutu
 * Course: CS2463 Advanced Java – Spring 2025
 */
public class PersonSearchUtil {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * All methods are static and should be accessed via the class name.
     */
    private PersonSearchUtil() {
    }

    /**
     * Performs a recursive binary search on a list of {@link OCCCPerson} objects,
     * looking for a person with a matching student ID. The search is case-insensitive.
    
     */
    public static OCCCPerson binarySearchByID(List<OCCCPerson> list, String studentID, int low, int high) {
        // Base case: If the search range is invalid (low index has crossed high index),
        // the element is not in the list.
        if (low > high) {
            return null;
        }

    
        int mid = low + (high - low) / 2; // More robust way to calculate mid
        OCCCPerson midPerson = list.get(mid);

        // Compare the student ID of the middle person with the target student ID (case-insensitive).
        int cmp = midPerson.getStudentID().compareToIgnoreCase(studentID);

        if (cmp == 0) {
            // Found the person.
            return midPerson;
        } else if (cmp < 0) {
            // The middle person's ID is less than the target ID, so search the right half.
            return binarySearchByID(list, studentID, mid + 1, high);
        } else {
            // The middle person's ID is greater than the target ID, so search the left half.
            return binarySearchByID(list, studentID, low, mid - 1);
        }
    }
}