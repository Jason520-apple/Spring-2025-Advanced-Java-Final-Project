import java.io.*;
import java.util.List;

/**
 * PersonFileUtil.java
 *
 * A utility class providing static methods for serializing and deserializing
 * lists of {@link Person} objects to and from files. This class uses Java's
 * built-in object serialization mechanism.
 *
 * Demonstrates:
 * - Java Object Serialization for saving and loading collections of objects
 * - Proper use of try-with-resources for stream management
 * - Handling of IOExceptions and ClassNotFoundExceptions during file operations
 *
 * Author: Amida Fombutu
 * Course: CS2463 Advanced Java – Spring 2025
 */
public class PersonFileUtil {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * All methods are static and should be accessed via the class name.
     */
    private PersonFileUtil() {
       
    }

   
    public static void saveToFile(List<Person> people, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(people);
        }
    }

    /**
     * Loads a list of {@link Person} objects (or its subclasses) from a specified file
     * that was previously saved using Java object serialization.
    
     */
    @SuppressWarnings("unchecked") // Suppressing warning for the cast from Object to List<Person>
    public static List<Person> loadFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
         
            return (List<Person>) ois.readObject();
        }
    }
}