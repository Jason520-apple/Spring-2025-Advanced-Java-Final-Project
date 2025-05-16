import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PersonManagerGUI.java
 *
 * A Swing-based desktop application for managing a list of Person objects.
 * Allows users to create, edit, delete, save, and load person data.
 * Features include sorting, searching by various criteria (including a specific
 * binary search for OCCCPerson by ID), and a themable user interface.
 *
 * Demonstrates:
 * - Complex GUI layout with Swing (BorderLayout, GridBagLayout)
 * - Event handling for menu items, buttons, combo boxes, and text fields
 * - File I/O operations for saving and loading serialized object data
 * - Custom JList cell rendering for rich display of Person objects
 * - Use of modal dialogs (PersonDialog, JOptionPane) for user interaction
 * - Data management including ArrayLists, DefaultListModel, and stream operations
 * - Sorting and searching algorithms (Collections.sort, custom comparators, binary search)
 * - Basic UI theming and styling
 *
 * Author: Amida Fombutu
 * Course: CS2463 Advanced Java – Spring 2025
 */
public class PersonManagerGUI extends JFrame {

    // --- Constants ---
    private static final String DATA_FILE_EXTENSION = "dat";
    private static final String DATA_FILE_DESCRIPTION = "Person Data Files (*.dat)";

    // --- UI Components ---
    private JList<Person> personListDisplay;
    private JButton addButton, deleteButton, editButton, searchButton;
    private JTextField searchField;
    private JComboBox<String> sortComboBox;

    // Menu Items
    private JMenuItem newItem, openItem, saveItem, saveAsItem, exitItem;
    private JMenuItem helpAboutItem;
    private JMenuItem sortByNameMenu, sortByDOBMenu, sortByIDMenu; // Menu items for sorting accessibility
    private JMenuItem searchByIDMenu; // For specific OCCCPerson search

    // --- Data Models & State ---
    private DefaultListModel<Person> listModel;
    private List<Person> persons; // Master list of persons
    private File currentFile;
    private boolean dataChanged; // Flag to track unsaved changes

    // --- Styling ---
    public Color cardColor = new Color(250, 250, 250); // Public for PersonListCellRenderer
    public Color textColor = Color.DARK_GRAY;       // Public for PersonListCellRenderer
    private final Color accentColor = new Color(0, 120, 215);
    private final Color buttonTextColor = Color.WHITE;
    private final Font generalFont = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);
    // private final Font titleFont = new Font("Segoe UI Semibold", Font.PLAIN, 16); // Not currently used explicitly

    /**
     * Constructs the PersonManagerGUI, initializing all UI components,
     * setting up layouts, listeners, and default window properties.
     */
    public PersonManagerGUI() {
        super("Person Manager");
        persons = new ArrayList<>();
        listModel = new DefaultListModel<>();
        dataChanged = false;
        currentFile = null;

        applyGlobalUISettings();
        initComponents();
        setupLayout();
        setupListeners();
        setupWindowListener();

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Custom exit handling
        setSize(800, 600);
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null); // Center on screen

        updateTitle();
        updateSaveItemState();
    }

    /**
     * Applies global UI settings like default fonts for various Swing components.
     */
    private void applyGlobalUISettings() {
        UIManager.put("Label.font", generalFont);
        UIManager.put("TextField.font", generalFont);
        UIManager.put("ComboBox.font", generalFont);
        UIManager.put("List.font", generalFont);
        // UIManager.put("Menu.font", generalFont); // Can make menu font too large
        // UIManager.put("MenuItem.font", generalFont);
    }

    /**
     * Initializes all UI components, including menus, buttons, text fields, and the main list display.
     */
    private void initComponents() {
        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createEmptyBorder(0,0,2,0));

        JMenu fileMenu = new JMenu("File");
        newItem = new JMenuItem("New");
        openItem = new JMenuItem("Open...");
        saveItem = new JMenuItem("Save");
        saveAsItem = new JMenuItem("Save As...");
        exitItem = new JMenuItem("Exit");
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        JMenu sortMenu = new JMenu("Sort");
        sortByNameMenu = new JMenuItem("Sort by Last Name");
        sortByDOBMenu = new JMenuItem("Sort by Date of Birth");
        sortByIDMenu = new JMenuItem("Sort by Student ID");
        sortMenu.add(sortByNameMenu);
        sortMenu.add(sortByDOBMenu);
        sortMenu.add(sortByIDMenu);
        menuBar.add(sortMenu);

        JMenu actionsMenu = new JMenu("Actions");
        searchByIDMenu = new JMenuItem("Find OCCCPerson by ID...");
        actionsMenu.add(searchByIDMenu);
        menuBar.add(actionsMenu);

        JMenu helpMenu = new JMenu("Help");
        helpAboutItem = new JMenuItem("About");
        helpMenu.add(helpAboutItem);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        // Main Control Buttons
        addButton = createStyledButton("Add Person", buttonFont, accentColor, buttonTextColor);
        editButton = createStyledButton("Edit Selected", buttonFont, new Color(240, 173, 78), Color.WHITE); // Orange
        deleteButton = createStyledButton("Delete Selected", buttonFont, new Color(217, 83, 79), Color.WHITE); // Red

        // Sort ComboBox
        String[] sortOptions = {"Sort by Last Name", "Sort by Date of Birth", "Sort by Student ID"};
        sortComboBox = new JComboBox<>(sortOptions);
        sortComboBox.setFont(generalFont);
        // sortComboBox.setPreferredSize(new Dimension(180, sortComboBox.getPreferredSize().height)); // Size managed by GridBagLayout

        // Search Field and Button
        searchField = new JTextField(20); // Preferred column width
        searchField.setFont(generalFont);
        searchField.putClientProperty("JTextField.placeholderText", "Search by name, ID, DOB..."); // Placeholder text

        searchButton = createStyledButton("Search", buttonFont, new Color(92, 184, 92), Color.WHITE); // Green

        // Person List Display
        personListDisplay = new JList<>(listModel);
        personListDisplay.setCellRenderer(new PersonListCellRenderer(this)); // Custom renderer
        personListDisplay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        personListDisplay.setBackground(new Color(235,240,245));
        personListDisplay.setFixedCellHeight(-1); // Essential for variable height cells
    }

    /**
     * Sets up the main layout of the GUI using BorderLayout and GridBagLayout for the top control panel.
     */
    private void setupLayout() {
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10)); // Overall padding
        setLayout(new BorderLayout(10, 10)); // Spacing between BorderLayout regions

        // Top Control Panel
        JPanel topControlsPanel = new JPanel(new GridBagLayout());
        topControlsPanel.setBorder(new EmptyBorder(5, 0, 10, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 3, 5, 3);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        topControlsPanel.add(addButton, gbc);
        gbc.gridx = 1;
        topControlsPanel.add(editButton, gbc);
        gbc.gridx = 2;
        topControlsPanel.add(deleteButton, gbc);

        gbc.gridx = 3; gbc.anchor = GridBagConstraints.EAST; gbc.insets = new Insets(5, 10, 5, 0);
        JLabel sortLabel = new JLabel("Sort by:");
        sortLabel.setFont(generalFont);
        topControlsPanel.add(sortLabel, gbc);
        gbc.insets = new Insets(5, 3, 5, 3); gbc.anchor = GridBagConstraints.WEST; // Reset

        gbc.gridx = 4; gbc.weightx = 0.1; gbc.fill = GridBagConstraints.HORIZONTAL;
        topControlsPanel.add(sortComboBox, gbc);

        gbc.gridx = 5; gbc.weightx = 1.0; // Search field takes most extra space
        topControlsPanel.add(searchField, gbc);

        gbc.gridx = 6; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        topControlsPanel.add(searchButton, gbc);

        add(topControlsPanel, BorderLayout.NORTH);

        // Center JList with ScrollPane
        JScrollPane scrollPane = new JScrollPane(personListDisplay);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Creates a JButton with custom styling including font, background/foreground colors,
     * border, and a mouse hover effect.
     * @param text The text to display on the button.
     * @param font The font for the button text.
     * @param bgColor The background color of the button.
     * @param fgColor The foreground (text) color of the button.
     * @return A styled JButton.
     */
    private JButton createStyledButton(String text, Font font, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    /**
     * Sets up action listeners for all interactive UI components (menus, buttons, etc.).
     */
    private void setupListeners() {
        // File menu actions
        newItem.addActionListener(e -> handleNew());
        openItem.addActionListener(e -> handleOpen());
        saveItem.addActionListener(e -> handleSave());
        saveAsItem.addActionListener(e -> handleSaveAs());
        exitItem.addActionListener(e -> handleExit());

        // Help menu
        helpAboutItem.addActionListener(e -> showHelpDialog());

        // Main control buttons
        addButton.addActionListener(e -> handleAddPerson());
        editButton.addActionListener(e -> handleEditPerson());
        deleteButton.addActionListener(e -> handleDeletePerson());

        // Sort menu actions (updates ComboBox selection for consistency)
        sortByNameMenu.addActionListener(e -> { sortPersons(null); sortComboBox.setSelectedIndex(0); });
        sortByDOBMenu.addActionListener(e -> { sortPersons(new PersonDateOfBirthComparator()); sortComboBox.setSelectedIndex(1); });
        sortByIDMenu.addActionListener(e -> { sortPersons(new PersonIDComparator()); sortComboBox.setSelectedIndex(2); });

        // Sort ComboBox action
        sortComboBox.addActionListener(e -> {
            int selected = sortComboBox.getSelectedIndex();
            if (selected == 0) sortPersons(null); // Sort by Name (Person's natural order)
            else if (selected == 1) sortPersons(new PersonDateOfBirthComparator());
            else if (selected == 2) sortPersons(new PersonIDComparator());
        });
        
        // Search actions
        searchByIDMenu.addActionListener(e -> handleSearchByID()); // Specific OCCCPerson ID search
        searchButton.addActionListener(e -> handleGeneralSearch()); // Inline search button
        searchField.addActionListener(e -> handleGeneralSearch()); // Allow Enter key in search field
    }

    /**
     * Sets up a window listener to handle the window closing event,
     * typically prompting to save changes.
     */
    private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });
    }

    // --- Action Handlers & Core Logic ---

    /**
     * Handles the "New" action. Prompts to save unsaved changes, then clears the current
     * list of persons and resets the application state.
     */
    private void handleNew() {
        if (!promptToSaveChanges()) {
            return;
        }
        persons.clear();
        currentFile = null;
        setDataChanged(false); // Resets title and save item state
        refreshListDisplay();
        // updateTitle() is called by setDataChanged
    }

    /**
     * Handles the "Open" action. Prompts to save unsaved changes, then displays a
     * file chooser to load person data from a file.
     */
    private void handleOpen() {
        if (!promptToSaveChanges()) {
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open Person File");
        fileChooser.setFileFilter(new FileNameExtensionFilter(DATA_FILE_DESCRIPTION, DATA_FILE_EXTENSION));
        if (currentFile != null) {
            fileChooser.setCurrentDirectory(currentFile.getParentFile());
        } else {
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        }

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            try {
                List<Person> loadedPersons = PersonFileUtil.loadFromFile(fileToLoad.getAbsolutePath());
                persons.clear();
                persons.addAll(loadedPersons);
                currentFile = fileToLoad;
                setDataChanged(false);
                refreshListDisplay();
                // updateTitle() called by setDataChanged
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
                // Consider resetting state if load fails completely
                // currentFile = null;
                // persons.clear();
                // setDataChanged(false);
                // refreshListDisplay();
            }
        }
    }

    /**
     * Handles the "Save" action. If a file is already associated with the current data,
     * it saves to that file. Otherwise, it behaves like "Save As".
     * @return {@code true} if the save was successful or no changes needed saving, {@code false} otherwise.
     */
    private boolean handleSave() {
        if (currentFile == null) {
            return handleSaveAs();
        }
        // Save item should be disabled if !dataChanged, but double-check
        if (!dataChanged) {
            return true; // No changes to save
        }

        try {
            PersonFileUtil.saveToFile(persons, currentFile.getAbsolutePath());
            setDataChanged(false); // Resets title and save item state
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Handles the "Save As" action. Displays a file chooser to specify a new file
     * location to save the current person data.
     * @return {@code true} if the save was successful, {@code false} otherwise.
     */
    private boolean handleSaveAs() {
        if (persons.isEmpty()) {
            JOptionPane.showMessageDialog(this, "There is no data to save.", "Save As", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Person File As...");
        fileChooser.setFileFilter(new FileNameExtensionFilter(DATA_FILE_DESCRIPTION, DATA_FILE_EXTENSION));
        if (currentFile != null) {
            fileChooser.setSelectedFile(new File(currentFile.getParent(), currentFile.getName()));
        } else {
            fileChooser.setSelectedFile(new File("persons." + DATA_FILE_EXTENSION));
        }

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith("." + DATA_FILE_EXTENSION)) {
                fileToSave = new File(filePath + "." + DATA_FILE_EXTENSION);
            }

            if (fileToSave.exists()) {
                int response = JOptionPane.showConfirmDialog(this,
                        "File '" + fileToSave.getName() + "' already exists. Do you want to overwrite it?",
                        "Confirm Overwrite", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (response == JOptionPane.NO_OPTION) {
                    return false; // User chose not to overwrite
                }
            }
            currentFile = fileToSave;
            // After setting new currentFile, call handleSave to perform the actual save operation
            // handleSave will also set dataChanged to false.
            return handleSave();
        }
        return false; // User cancelled Save As dialog
    }

    /**
     * Handles the "Exit" action. Prompts to save unsaved changes, then closes the application.
     */
    private void handleExit() {
        if (promptToSaveChanges()) {
            dispose();
            System.exit(0);
        }
    }

    /**
     * Prompts the user to save changes if {@code dataChanged} is true.
     * @return {@code false} if the user cancels the operation, {@code true} otherwise (saved or chose not to save).
     */
    private boolean promptToSaveChanges() {
        if (!dataChanged) {
            return true;
        }
        int response = JOptionPane.showConfirmDialog(this,
                "The current list has unsaved changes. Save before proceeding?",
                "Unsaved Changes",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            return handleSave(); // Returns true if save successful
        } else if (response == JOptionPane.NO_OPTION) {
            return true; // User chose not to save
        } else { // CANCEL_OPTION or dialog closed
            return false; // User cancelled the action
        }
    }

    /**
     * Handles adding a new person. Opens the {@link PersonDialog} for data entry.
     * If confirmed, adds the new person to the list and updates the display.
     */
    private void handleAddPerson() {
        setPersonDialogInProgress(true);
        PersonDialog dialog = new PersonDialog(this, null); // null for adding a new person
        dialog.setVisible(true);
        setPersonDialogInProgress(false);

        if (dialog.isConfirmed()) {
            Person newPerson = dialog.getPerson();
            if (newPerson != null) {
                persons.add(newPerson);
                setDataChanged(true);
                refreshListDisplay();
                // Select the newly added person in the list
                int newIndex = listModel.getSize() - 1;
                personListDisplay.setSelectedIndex(newIndex);
                personListDisplay.ensureIndexIsVisible(newIndex);
            }
        }
    }

    /**
     * Handles editing an existing person. Opens the {@link PersonDialog} pre-filled
     * with the selected person's data. If confirmed, updates the person in the list.
     */
    private void handleEditPerson() {
        int selectedDisplayIndex = personListDisplay.getSelectedIndex();
        if (selectedDisplayIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a person to edit.", "Edit Person", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Get the Person object from the listModel, which should correspond to an object in the 'persons' list
        Person personToEdit = listModel.getElementAt(selectedDisplayIndex);

        // Ensure this person object exists in our master 'persons' list before proceeding
        // This is a safeguard, especially if filtering/sorting complexities arise.
        if (!persons.contains(personToEdit)) {
             JOptionPane.showMessageDialog(this, "Selected person not found in the master list. Please refresh or try again.", "Error", JOptionPane.ERROR_MESSAGE);
             return;
        }

        setPersonDialogInProgress(true);
        PersonDialog dialog = new PersonDialog(this, personToEdit);
        dialog.setVisible(true);
        setPersonDialogInProgress(false);

        if (dialog.isConfirmed()) {
            Person updatedPerson = dialog.getPerson(); // This might be the same instance or a new one
            if (updatedPerson != null) {
                int originalListIndex = persons.indexOf(personToEdit); // Find original by object reference
                if (originalListIndex != -1) {
                    persons.set(originalListIndex, updatedPerson); // Replace with the (potentially new) updatedPerson instance
                } else {
                    // Fallback: if somehow the original reference was lost, remove old and add new
                    // This case should ideally not happen if personToEdit was correctly sourced from 'persons'.
                    persons.remove(personToEdit);
                    persons.add(updatedPerson);
                }
                setDataChanged(true);
                refreshListDisplay();

                // Try to re-select the edited item
                int newDisplayIndex = listModel.indexOf(updatedPerson);
                if (newDisplayIndex != -1) {
                    personListDisplay.setSelectedIndex(newDisplayIndex);
                    personListDisplay.ensureIndexIsVisible(newDisplayIndex);
                } else if (selectedDisplayIndex < listModel.getSize()){ // Fallback to original display index if still valid
                    personListDisplay.setSelectedIndex(selectedDisplayIndex);
                    personListDisplay.ensureIndexIsVisible(selectedDisplayIndex);
                }
            }
        }
    }

    /**
     * Handles deleting the selected person from the list after confirmation.
     */
    private void handleDeletePerson() {
        int selectedIndex = personListDisplay.getSelectedIndex();
        if (selectedIndex != -1) {
            Person personToDelete = listModel.getElementAt(selectedIndex);
            int response = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete " + personToDelete.getFirstName() + " " + personToDelete.getLastName() + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                persons.remove(personToDelete); // Remove from master list
                setDataChanged(true);
                refreshListDisplay(); // Re-populates listModel from master list
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a person to delete.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Sorts the main list of persons based on the provided comparator.
     * If the comparator is null, sorts by the natural order of {@link Person} (e.g., by name).
     * Clears any active search filter before sorting.
     * @param comparator The {@link Comparator} to use for sorting.
     */
    private void sortPersons(Comparator<Person> comparator) {
        if (persons.isEmpty()) return; // Nothing to sort

        // Sorting clears any active search filter to avoid confusion
        if (!searchField.getText().trim().isEmpty()) {
            searchField.setText("");
        }

        if (comparator == null) {
            Collections.sort(persons); // Assumes Person implements Comparable
        } else {
            persons.sort(comparator);
        }
        setDataChanged(true); // Sorting changes the order, which is considered a savable change
        refreshListDisplay();
    }

    /**
     * Handles the general search functionality. Filters the displayed list based on
     * the query in the search field, matching against name, DOB, and relevant IDs.
     */
    private void handleGeneralSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            refreshListDisplay(); // Show all if query is empty
            return;
        }
        List<Person> filteredPersons = persons.stream()
            .filter(p -> {
                boolean matches = p.getFirstName().toLowerCase().contains(query) ||
                                  p.getLastName().toLowerCase().contains(query) ||
                                  p.getDateOfBirth().toString().toLowerCase().contains(query);
                if (p instanceof RegisteredPerson rp) {
                    matches = matches || rp.getGovernmentID().toLowerCase().contains(query);
                }
                if (p instanceof OCCCPerson op) {
                    matches = matches || op.getStudentID().toLowerCase().contains(query);
                }
                return matches;
            })
            .collect(Collectors.toList());

        listModel.clear();
        for (Person p : filteredPersons) {
            listModel.addElement(p);
        }
        // Note: General search does not set dataChanged, as it's a view filter.
    }

    /**
     * Handles the specific search for an {@link OCCCPerson} by Student ID using binary search.
     * Prompts for the ID, performs the search, and highlights the found person.
     */
    private void handleSearchByID() {
        List<OCCCPerson> occcPersons = persons.stream()
            .filter(OCCCPerson.class::isInstance)
            .map(OCCCPerson.class::cast)
            .sorted(Comparator.comparing(OCCCPerson::getStudentID, String.CASE_INSENSITIVE_ORDER)) // Must be sorted for binary search
            .collect(Collectors.toList());

        if (occcPersons.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No OCCCPersons in the list to search.", "Search by ID", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String studentIDToSearch = JOptionPane.showInputDialog(this, "Enter Student ID to search for:", "Find OCCCPerson by ID", JOptionPane.PLAIN_MESSAGE);
        if (studentIDToSearch == null || studentIDToSearch.trim().isEmpty()) {
            return; // User cancelled or entered empty ID
        }
        studentIDToSearch = studentIDToSearch.trim();

        // Assumes PersonSearchUtil.binarySearchByID exists and is implemented correctly
        OCCCPerson foundPerson = PersonSearchUtil.binarySearchByID(occcPersons, studentIDToSearch, 0, occcPersons.size() - 1);

        if (foundPerson != null) {
            // Attempt to find and select the 'foundPerson' in the current personListDisplay (JList)
            int displayIndex = -1;
            for (int i = 0; i < listModel.getSize(); i++) {
                if (listModel.getElementAt(i) == foundPerson) { // Compare by object reference
                    displayIndex = i;
                    break;
                }
            }

            if (displayIndex != -1) {
                personListDisplay.setSelectedIndex(displayIndex);
                personListDisplay.ensureIndexIsVisible(displayIndex);
                JOptionPane.showMessageDialog(this, "Found: " + foundPerson.toString(), "Search Result", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // If not found by reference (e.g., if search results were from a copy or listModel is filtered differently)
                JOptionPane.showMessageDialog(this, "Found data: " + foundPerson.toString() +
                        "\n(Could not auto-select in the current view if list is filtered or sorted differently).",
                        "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Student ID '" + studentIDToSearch + "' not found among OCCCPersons.", "Search Result", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Displays a help dialog with information about the application's features.
     */
    private void showHelpDialog() {
        JTextArea helpTextArea = new JTextArea(20, 50);
        helpTextArea.setText(
            "Person GUI Application - Final Project\n\n" +
            "File Menu:\n" +
            "  New: Clears the current list of persons. Prompts to save if data has changed.\n" +
            "  Open...: Loads a list of persons from a .dat file.\n" +
            "  Save: Saves the current list to the currently open file. Enabled if there are unsaved changes.\n" +
            "  Save As...: Saves the current list to a new .dat file.\n" +
            "  Exit: Exits the application. Prompts to save if data has changed.\n\n" +
            "Main Controls (Top Panel):\n" +
            "  Add Person: Opens a dialog to create a new Person, RegisteredPerson, or OCCCPerson.\n" +
            "  Edit Selected: Opens a dialog to modify the selected person in the list.\n" +
            "  Delete Selected: Removes the selected person from the list.\n" +
            "  Sort by [Dropdown]: Select sorting criteria (Last Name, DOB, Student ID).\n" +
            "  Search [Text Field + Button]: Type to search by name, ID, or DOB. Press Enter or click Search.\n\n" +
            "Actions Menu:\n" +
            "  Find OCCCPerson by ID: Performs a specific binary search for an OCCCPerson by their student ID.\n\n" +
            "General Notes:\n" +
            "  - An asterisk (*) in the title bar indicates unsaved changes.\n" +
            "  - Save/Save As menu items and main controls are disabled while adding or editing a person.\n" +
            "  - Date input requires MM/DD/YYYY format and must be a valid calendar date."
        );
        helpTextArea.setWrapStyleWord(true);
        helpTextArea.setLineWrap(true);
        helpTextArea.setEditable(false);
        helpTextArea.setCaretPosition(0); // Scroll to top
        helpTextArea.setFont(generalFont.deriveFont(Font.PLAIN, 13));

        JScrollPane helpScrollPane = new JScrollPane(helpTextArea);
        helpScrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, helpScrollPane, "About Person Manager", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- UI & State Update Methods ---

    /**
     * Refreshes the {@link JList} display ({@code personListDisplay}) by clearing its model
     * and re-populating it from the master {@code persons} list.
     * This should be called after any modification to the {@code persons} list that needs
     * to be reflected in the UI, or when a search filter is cleared.
     */
    private void refreshListDisplay() {
        listModel.clear();
        for (Person p : persons) {
            listModel.addElement(p);
        }
        // updateSaveItemState(); // Often called by setDataChanged or other specific ops
    }

    /**
     * Updates the main window title to reflect the current file name and unsaved changes status.
     * An asterisk (*) indicates unsaved changes.
     */
    private void updateTitle() {
        String titleText = "Person Manager";
        if (currentFile != null) {
            titleText += " - " + currentFile.getName();
        }
        if (dataChanged) {
            titleText += "*";
        }
        setTitle(titleText);
    }

    /**
     * Sets the {@code dataChanged} flag and updates the window title and save item state accordingly.
     * @param changed {@code true} if there are unsaved changes, {@code false} otherwise.
     */
    private void setDataChanged(boolean changed) {
        if (this.dataChanged != changed) {
            this.dataChanged = changed;
            updateTitle();
        }
        updateSaveItemState(); // Always update save item state when dataChanged status might have changed
    }

    /**
     * Updates the enabled state of the "Save" and "Save As" menu items
     * based on whether there is data, a current file, and unsaved changes.
     */
    private void updateSaveItemState() {
        boolean hasData = !persons.isEmpty();
        saveItem.setEnabled(currentFile != null && hasData && dataChanged);
        saveAsItem.setEnabled(hasData);
    }

    /**
     * Enables or disables main UI controls and menu items when a modal dialog
     * (like PersonDialog) is active or dismissed. This prevents concurrent modifications.
     * @param inProgress {@code true} if a dialog is in progress, {@code false} otherwise.
     */
    private void setPersonDialogInProgress(boolean inProgress) {
        boolean enableControls = !inProgress;

        // Update save items based on normal logic, but also consider inProgress
        updateSaveItemState(); // Recalculate base enablement
        if (inProgress) { // If dialog is up, disable save options regardless
             saveItem.setEnabled(false);
             saveAsItem.setEnabled(false);
        }


        addButton.setEnabled(enableControls);
        editButton.setEnabled(enableControls);
        deleteButton.setEnabled(enableControls);
        sortComboBox.setEnabled(enableControls);
        searchField.setEnabled(enableControls);
        searchButton.setEnabled(enableControls);
        
        // Disable all menus except "Help" when a dialog is active
        JMenuBar mb = getJMenuBar();
        if (mb != null) {
            for (int i = 0; i < mb.getMenuCount(); i++) {
                JMenu menu = mb.getMenu(i);
                if (!"Help".equals(menu.getText())) { // Keep Help menu enabled
                    menu.setEnabled(enableControls);
                }
            }
        }
    }

    /**
     * The main entry point for the PersonManagerGUI application.
     * Sets the Look and Feel and launches the GUI on the Event Dispatch Thread.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            // Attempt to set Nimbus Look and Feel for a modern appearance
            boolean nimbusFound = false;
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    nimbusFound = true;
                    break;
                }
            }
            if (!nimbusFound) {
                // Fallback to system L&F if Nimbus is not available
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (Exception e) {
            System.err.println("Look and Feel setting error: " + e.getMessage());
            // Application will continue with the default Java L&F
        }

        SwingUtilities.invokeLater(() -> {
            PersonManagerGUI gui = new PersonManagerGUI();
            gui.setVisible(true);
        });
    }
}