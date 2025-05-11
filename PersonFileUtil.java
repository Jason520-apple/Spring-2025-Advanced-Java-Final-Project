// --- File: PersonFileUtil.java ---


import java.io.*;
import java.util.List;

/**
 * Utility class for serializing and deserializing lists of Person objects.
 */
public class PersonFileUtil {

    /**
     * Saves a list of Person (or subclasses) objects to a file.
     *
     * @param people   The list of persons to save.
     * @param filename The filename to save to.
     * @throws IOException if an I/O error occurs.
     */
    public static void saveToFile(List<Person> people, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(people);
        }
    }

    /**
     * Loads a list of Person (or subclasses) objects from a file.
     *
     * @param filename The filename to load from.
     * @return The list of persons.
     * @throws IOException            if an I/O error occurs.
     * @throws ClassNotFoundException if deserialization fails.
     */
    @SuppressWarnings("unchecked")
    public static List<Person> loadFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<Person>) ois.readObject();
        }
    }
}