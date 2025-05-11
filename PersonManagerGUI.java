

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * PersonManagerGUI provides a graphical interface to manage Person records.
 * Supports add, edit, delete, sort, search, reset, and full file operations via menu.
 */
public class PersonManagerGUI extends JFrame {

    // UI and data model components
    private DefaultListModel<Person> model;
    private JList<Person> personList;
    private JTextField searchField;

    // Current file being used for loading/saving
    private File currentFile = new File("people.dat");

    // Tracks whether changes were made since last save
    private boolean isDirty = false;

    // Theme colors
    public Color cardColor = new Color(245, 245, 245);
    public Color textColor = Color.BLACK;
    private final Color buttonColor = new Color(66, 133, 244);
    private final Font buttonFont = new Font("Segoe UI", Font.PLAIN, 13);

    /**
     * Constructor: sets up the GUI window and components.
     */
    public PersonManagerGUI() {
        super("Person Manager");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Model and list for displaying people
        model = new DefaultListModel<>();
        personList = new JList<>(model);
        personList.setCellRenderer(new PersonListCellRenderer(this));
        personList.setFixedCellHeight(-1); // Enable dynamic height per card
        JScrollPane scrollPane = new JScrollPane(personList);

        // Top control panel with buttons and fields
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Buttons and dropdown
        JButton addButton = createStyledButton("Add");
        JButton editButton = createStyledButton("Edit");
        JButton deleteButton = createStyledButton("Delete");

        String[] sortOptions = { "Sort by Last Name", "Sort by Date of Birth", "Sort by Student ID" };
        JComboBox<String> sortComboBox = new JComboBox<>(sortOptions);
        sortComboBox.setFont(buttonFont);
        sortComboBox.setBackground(buttonColor);
        sortComboBox.setForeground(Color.WHITE);

        searchField = new JTextField(15);
        JButton searchButton = createStyledButton("Search");
        JButton resetButton = createStyledButton("Reset");

        // Add all controls to the control panel
        int x = 0;
        gbc.gridx = x++; controlPanel.add(addButton, gbc);
        gbc.gridx = x++; controlPanel.add(editButton, gbc);
        gbc.gridx = x++; controlPanel.add(deleteButton, gbc);
        gbc.gridx = x++; controlPanel.add(sortComboBox, gbc);
        gbc.gridx = x++; controlPanel.add(searchField, gbc);
        gbc.gridx = x++; controlPanel.add(searchButton, gbc);
        gbc.gridx = x;   controlPanel.add(resetButton, gbc);

        // Add panels to frame
        add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        setJMenuBar(createMenuBar());

        // Attach button functionality
        addButton.addActionListener(e -> addPerson());
        editButton.addActionListener(e -> editPerson());
        deleteButton.addActionListener(e -> deletePerson());
        searchButton.addActionListener(e -> searchPeople());
        resetButton.addActionListener(e -> resetView());

        // Handle dropdown sort options
        sortComboBox.addActionListener(e -> {
            String selection = (String) sortComboBox.getSelectedItem();
            switch (selection) {
                case "Sort by Last Name" -> sortByLastName();
                case "Sort by Date of Birth" -> sortByDOB();
                case "Sort by Student ID" -> sortByStudentID();
            }
        });

        // Try loading people from default file on startup
        autoLoadPeopleFile();
    }

    /**
     * Creates a styled JButton with consistent look and feel.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(buttonFont);
        button.setBackground(buttonColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        return button;
    }

    /**
     * Tries to load from the default "people.dat" file at startup.
     */
    private void autoLoadPeopleFile() {
        if (!currentFile.exists()) {
            JOptionPane.showMessageDialog(this, "No saved data found. Start by adding persons.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        loadPeople();
    }

    /**
     * Clears search and reloads original people list from file.
     */
    private void resetView() {
        searchField.setText("");
        loadPeople();
        isDirty = false;
    }

    /**
     * Launches dialog to create a new Person.
     */
    private void addPerson() {
        PersonDialog dialog = new PersonDialog(this, null);
        dialog.setVisible(true);
    
        if (dialog.isConfirmed()) {
            model.addElement(dialog.getPerson());
            savePeople(); // persist new person
            isDirty = false;
        }
    }
    

    /**
     * Opens selected person for editing.
     */
    private void editPerson() {
        Person selected = personList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a person to edit.");
            return;
        }
    
        PersonDialog dialog = new PersonDialog(this, selected);
        dialog.setVisible(true);
    
        if (dialog.isConfirmed()) {
            int index = personList.getSelectedIndex();
            model.set(index, dialog.getPerson()); // update in list
            savePeople(); // persist to current file
            isDirty = false; // optional, since we've just saved
        }
    }
    
    /**
     * Deletes selected people from the list.
     */
    private void deletePerson() {
        List<Person> selected = personList.getSelectedValuesList();
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one person to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete selected person(s)?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            selected.forEach(model::removeElement);
            savePeople(); // immediately persist changes to currentFile
            isDirty = false;
        }
    }
    

    /**
     * Loads person data from currentFile into the model.
     */
    private void loadPeople() {
        try {
            List<Person> people = PersonFileUtil.loadFromFile(currentFile.getAbsolutePath());
            model.clear();
            people.forEach(model::addElement);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading: " + e.getMessage());
        }
    }

    /**
     * Saves current person list to the currentFile.
     */
    private void savePeople() {
        try {
            List<Person> people = Collections.list(model.elements());
            PersonFileUtil.saveToFile(people, currentFile.getAbsolutePath());
            JOptionPane.showMessageDialog(this, "Saved to " + currentFile.getName());
            isDirty = false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving: " + e.getMessage());
        }
    }

    /**
     * Sorts people by last name alphabetically.
     */
    private void sortByLastName() {
        List<Person> people = Collections.list(model.elements());
        Collections.sort(people);
        model.clear();
        people.forEach(model::addElement);
        isDirty = true;
    }

    /**
     * Sorts people by date of birth using a comparator.
     */
    private void sortByDOB() {
        List<Person> people = Collections.list(model.elements());
        people.sort(new PersonDateOfBirthComparator());
        model.clear();
        people.forEach(model::addElement);
        isDirty = true;
    }

    /**
     * Sorts people by student ID (works only if OCCCPerson).
     */
    private void sortByStudentID() {
        List<Person> people = Collections.list(model.elements());
        people.sort(new PersonIDComparator());
        model.clear();
        people.forEach(model::addElement);
        isDirty = true;
    }

    /**
     * Searches for matching names or student IDs.
     */
    private void searchPeople() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) return;

        List<Person> matches = Collections.list(model.elements()).stream()
                .filter(p -> p.getFirstName().toLowerCase().contains(query)
                        || p.getLastName().toLowerCase().contains(query)
                        || (p instanceof OCCCPerson op && op.getStudentID().toLowerCase().contains(query)))
                .toList();

        model.clear();
        matches.forEach(model::addElement);
    }

    /**
     * Creates the File and Help menu bar.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu and options
        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New");
        JMenuItem openItem = new JMenuItem("Open...");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem saveAsItem = new JMenuItem("Save As...");
        JMenuItem exitItem = new JMenuItem("Exit");

        newItem.addActionListener(e -> clearPeople());
        openItem.addActionListener(e -> openFile());
        saveItem.addActionListener(e -> savePeople());
        saveAsItem.addActionListener(e -> savePeopleAs());
        exitItem.addActionListener(e -> exitApp());

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpItem = new JMenuItem("Help");
        helpItem.addActionListener(e -> showHelp());
        helpMenu.add(helpItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    /**
     * Clears all data from the list.
     */
    private void clearPeople() {
        int result = JOptionPane.showConfirmDialog(this, "Clear current list?", "New", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            model.clear();
            currentFile = new File("people.dat");
            isDirty = true;
        }
    }

    /**
     * Opens and loads a .dat file using a file chooser.
     */
    private void openFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            currentFile = chooser.getSelectedFile();
            loadPeople();
        }
    }

    /**
     * Saves person data to a new file selected by user.
     */
    private void savePeopleAs() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            currentFile = chooser.getSelectedFile();
            savePeople();
        }
    }

    /**
     * Exits the application, prompting to save if needed.
     */
    private void exitApp() {
        if (isDirty) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "You have unsaved changes. Exit anyway?",
                    "Exit Confirmation", JOptionPane.YES_NO_OPTION);
            if (choice != JOptionPane.YES_OPTION) return;
        }
        dispose();
    }

    /**
     * Displays a basic help dialog.
     */
    private void showHelp() {
        JOptionPane.showMessageDialog(this,
                "Person Manager Help:\n\n" +
                        "- Add/Edit/Delete: Manage people records\n" +
                        "- Save/Open: Save or load people data\n" +
                        "- Search: Match by name or student ID\n" +
                        "- Sort: Use dropdown to sort by name, date, or ID\n" +
                        "- Reset: Clears search and reloads all people",
                "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Entry point for the application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PersonManagerGUI().setVisible(true));
    }
}
