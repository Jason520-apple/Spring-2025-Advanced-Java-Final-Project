// --- File: PersonManagerGUI.java ---


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

public class PersonManagerGUI extends JFrame {

    private JList<Person> personListDisplay;
    private DefaultListModel<Person> listModel;
    private List<Person> persons;

    // UI Components for easier access to style/disable
    private JButton addButton, deleteButton, editButton, searchButton;
    private JTextField searchField;
    private JComboBox<String> sortComboBox;

    private JMenuItem newItem, openItem, saveItem, saveAsItem, exitItem;
    private JMenuItem helpAboutItem;
    // Sort menu items are now handled by sortComboBox, but we can keep menu for accessibility
    private JMenuItem sortByNameMenu, sortByDOBMenu, sortByIDMenu;
    private JMenuItem searchByIDMenu;


    private File currentFile;
    private boolean dataChanged;

    // Theme elements
    public Color cardColor = new Color(250, 250, 250); // Slightly off-white for cards
    public Color textColor = Color.DARK_GRAY; // Softer than pure black
    private final Color accentColor = new Color(0, 120, 215); // A nice blue for accents
    private final Color buttonTextColor = Color.WHITE;
    private final Font generalFont = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);
    private final Font titleFont = new Font("Segoe UI Semibold", Font.PLAIN, 16);


    private static final String DATA_FILE_EXTENSION = "dat";
    private static final String DATA_FILE_DESCRIPTION = "Person Data Files (*.dat)";

    public PersonManagerGUI() {
        super("Person Manager");
        persons = new ArrayList<>();
        listModel = new DefaultListModel<>();
        dataChanged = false;
        currentFile = null;

        initComponents(); // Initialize components first
        setupLayout();    // Then set up the layout
        setupListeners();
        setupWindowListener();

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(800, 600); // Increased size for better spacing
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);

        updateTitle();
        updateSaveItemState();
        // Apply font to all components (or specific ones)
        UIManager.put("Label.font", generalFont);
        UIManager.put("TextField.font", generalFont);
        UIManager.put("ComboBox.font", generalFont);
        UIManager.put("List.font", generalFont);
        // UIManager.put("Menu.font", generalFont); // Can make menu font too large
        // UIManager.put("MenuItem.font", generalFont);
    }

    private JButton createStyledButton(String text, Font font, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1), // Subtle border
                BorderFactory.createEmptyBorder(8, 15, 8, 15) // Padding
        ));
        // Hover effect (optional, requires MouseListener)
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


    private void initComponents() {
        // --- Menu Bar ---
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createEmptyBorder(0,0,2,0)); // Small bottom border

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

        JMenu searchMenuAction = new JMenu("Actions"); // Renamed from "Search" to "Actions" for clarity with inline search
        searchByIDMenu = new JMenuItem("Find OCCCPerson by ID...");
        searchMenuAction.add(searchByIDMenu);
        menuBar.add(searchMenuAction);

        JMenu helpMenu = new JMenu("Help");
        helpAboutItem = new JMenuItem("About");
        helpMenu.add(helpAboutItem);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        // --- Main Controls (will be in top panel) ---
        addButton = createStyledButton("Add Person", buttonFont, accentColor, buttonTextColor);
        editButton = createStyledButton("Edit Selected", buttonFont, new Color(240, 173, 78), Color.WHITE); // Orange
        deleteButton = createStyledButton("Delete Selected", buttonFont, new Color(217, 83, 79), Color.WHITE); // Red

        String[] sortOptions = {"Sort by Last Name", "Sort by Date of Birth", "Sort by Student ID"};
        sortComboBox = new JComboBox<>(sortOptions);
        sortComboBox.setFont(generalFont);
        sortComboBox.setPreferredSize(new Dimension(180, sortComboBox.getPreferredSize().height));


        searchField = new JTextField(20);
        searchField.setFont(generalFont);
        searchField.putClientProperty("JTextField.placeholderText", "Search by name or ID..."); // Placeholder

        searchButton = createStyledButton("Search", buttonFont, new Color(92, 184, 92), Color.WHITE); // Green


        // --- JList ---
        personListDisplay = new JList<>(listModel);
        personListDisplay.setCellRenderer(new PersonListCellRenderer(this));
        personListDisplay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        personListDisplay.setBackground(new Color(235,240,245)); // Light background for the list area
        personListDisplay.setFixedCellHeight(-1); // Crucial for variable height cells from PersonListCellRenderer
    }


private void setupLayout() {
    // Overall content pane padding
    ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
    setLayout(new BorderLayout(10, 10)); // Spacing between BorderLayout regions

    // --- Top Control Panel ---
    JPanel topControlsPanel = new JPanel(new GridBagLayout());
    topControlsPanel.setBorder(new EmptyBorder(5, 0, 10, 0)); // Padding: top, left, bottom, right
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 3, 5, 3); // Reduced horizontal insets for tighter packing if needed
    gbc.anchor = GridBagConstraints.WEST; // Default anchor to the left

    // Add Button
    gbc.gridx = 0; gbc.gridy = 0;
    gbc.weightx = 0; // Does not take extra horizontal space
    gbc.fill = GridBagConstraints.NONE; // Takes its preferred size
    topControlsPanel.add(addButton, gbc);

    // Edit Button
    gbc.gridx = 1; gbc.gridy = 0;
    // weightx and fill are already set from previous
    topControlsPanel.add(editButton, gbc);

    // Delete Button
    gbc.gridx = 2; gbc.gridy = 0;
    topControlsPanel.add(deleteButton, gbc);

    // Sort Label
    gbc.gridx = 3; gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.EAST; // Align "Sort by:" label to the right of its cell
    gbc.insets = new Insets(5, 10, 5, 0); // Add some space before "Sort by:"
    JLabel sortLabel = new JLabel("Sort by:");
    sortLabel.setFont(generalFont);
    topControlsPanel.add(sortLabel, gbc);
    gbc.insets = new Insets(5, 3, 5, 3); // Reset insets
    gbc.anchor = GridBagConstraints.WEST; // Reset anchor

    // Sort ComboBox
    gbc.gridx = 4; gbc.gridy = 0;
    gbc.weightx = 0.1; // Takes a small amount of extra space, helps it not feel too crammed
    gbc.fill = GridBagConstraints.HORIZONTAL; // Allows it to expand horizontally if space is given
    // Make sure you remove sortComboBox.setPreferredSize(...) in initComponents()
    topControlsPanel.add(sortComboBox, gbc);

    // Search Field
    gbc.gridx = 5; gbc.gridy = 0;
    gbc.weightx = 1.0; // Takes the majority of available extra horizontal space
    gbc.fill = GridBagConstraints.HORIZONTAL; // Expands horizontally
    // The JTextField(20) in initComponents gives it a preferred size, but it can shrink/grow
    topControlsPanel.add(searchField, gbc);

    // Search Button
    gbc.gridx = 6; gbc.gridy = 0;
    gbc.weightx = 0; // Does not take extra horizontal space
    gbc.fill = GridBagConstraints.NONE; // Takes its preferred size
    topControlsPanel.add(searchButton, gbc);

    add(topControlsPanel, BorderLayout.NORTH);

    // --- Center JList ---
    JScrollPane scrollPane = new JScrollPane(personListDisplay);
    scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    add(scrollPane, BorderLayout.CENTER);
}
    private void setupListeners() {
        newItem.addActionListener(e -> handleNew());
        openItem.addActionListener(e -> handleOpen());
        saveItem.addActionListener(e -> handleSave());
        saveAsItem.addActionListener(e -> handleSaveAs());
        exitItem.addActionListener(e -> handleExit());

        helpAboutItem.addActionListener(e -> showHelpDialog());

        addButton.addActionListener(e -> handleAddPerson());
        editButton.addActionListener(e -> handleEditPerson());
        deleteButton.addActionListener(e -> handleDeletePerson());

        // Menu sort actions
        sortByNameMenu.addActionListener(e -> { sortPersons(null); sortComboBox.setSelectedIndex(0); });
        sortByDOBMenu.addActionListener(e -> { sortPersons(new PersonDateOfBirthComparator()); sortComboBox.setSelectedIndex(1); });
        sortByIDMenu.addActionListener(e -> { sortPersons(new PersonIDComparator()); sortComboBox.setSelectedIndex(2); });

        // ComboBox sort action
        sortComboBox.addActionListener(e -> {
            int selected = sortComboBox.getSelectedIndex();
            if (selected == 0) sortPersons(null); // Name
            else if (selected == 1) sortPersons(new PersonDateOfBirthComparator()); // DOB
            else if (selected == 2) sortPersons(new PersonIDComparator()); // ID
        });
        
        searchByIDMenu.addActionListener(e -> handleSearchByID()); // Menu item for specific OCCCPerson search
        searchButton.addActionListener(e -> handleGeneralSearch()); // Inline search button
        searchField.addActionListener(e -> handleGeneralSearch()); // Allow Enter key in search field
    }

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
                                  p.getDateOfBirth().toString().toLowerCase().contains(query); // Search DOB string
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
        // Do not set dataChanged for search
    }


    // ... (setupWindowListener, updateTitle, setDataChanged, updateSaveItemState, refreshListDisplay, promptToSaveChanges remain mostly the same)
    // ... (handleNew, handleOpen, handleSave, handleSaveAs, handleExit remain mostly the same)
    // ... (setPersonDialogInProgress, handleAddPerson, handleEditPerson, handleDeletePerson, sortPersons, handleSearchByID remain mostly the same)
    // Make sure showHelpDialog is comprehensive.

     private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });
    }

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

    private void setDataChanged(boolean changed) {
        if (this.dataChanged != changed) {
            this.dataChanged = changed;
            updateTitle();
        }
        updateSaveItemState(); // Always update save state when dataChanged might have changed
    }

    private void updateSaveItemState() {
        boolean hasData = !persons.isEmpty();
        saveItem.setEnabled(currentFile != null && hasData && dataChanged); // Only enable save if file, data, and changes
        saveAsItem.setEnabled(hasData); // Enable Save As if there's any data
    }

    private void refreshListDisplay() {
        listModel.clear();
        for (Person p : persons) {
            listModel.addElement(p);
        }
        // updateSaveItemState(); // Called by setDataChanged or other ops
        // dataChanged flag is managed by operations that modify 'persons' list
    }

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
            return handleSave();
        } else if (response == JOptionPane.NO_OPTION) {
            return true;
        } else {
            return false;
        }
    }

    private void handleNew() {
        if (!promptToSaveChanges()) {
            return;
        }
        persons.clear();
        currentFile = null;
        setDataChanged(false);
        refreshListDisplay();
        updateTitle();
    }

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
                updateTitle();
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
                // currentFile = null; // Keep currentFile as is, or set to null? If load fails, probably safer to null it.
                // persons.clear();    // Clear data if load failed
                // setDataChanged(false);
                // refreshListDisplay();
                // updateTitle();
            }
        }
    }

    private boolean handleSave() {
        if (currentFile == null) {
            return handleSaveAs();
        }
        // Per demo spec: "Save. It should automagically use the current file name with no prompts."
        // And "Save" should be disabled if no changes (dataChanged is false). My updateSaveItemState handles this.
        if (!dataChanged && currentFile != null) { // If save is somehow enabled without changes (should not happen with new logic)
            // JOptionPane.showMessageDialog(this, "No changes to save.", "Save", JOptionPane.INFORMATION_MESSAGE);
            return true; // No changes, consider it a "successful" save operation.
        }

        try {
            PersonFileUtil.saveToFile(persons, currentFile.getAbsolutePath());
            setDataChanged(false); // Crucial: reset after successful save
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

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
                    return false;
                }
            }
            currentFile = fileToSave;
            // After Save As, the data is considered saved to this new file, so mark as unchanged
            // The handleSave() call below will set dataChanged to false.
            return handleSave();
        }
        return false;
    }

    private void handleExit() {
        if (promptToSaveChanges()) {
            dispose();
            System.exit(0);
        }
    }

    private void setPersonDialogInProgress(boolean inProgress) {
        saveItem.setEnabled(!inProgress && currentFile != null && dataChanged && !persons.isEmpty());
        saveAsItem.setEnabled(!inProgress && !persons.isEmpty());

        addButton.setEnabled(!inProgress);
        editButton.setEnabled(!inProgress);
        deleteButton.setEnabled(!inProgress);
        sortComboBox.setEnabled(!inProgress);
        searchField.setEnabled(!inProgress);
        searchButton.setEnabled(!inProgress);
        
        // Disable menus
        for (int i = 0; i < getJMenuBar().getMenuCount(); i++) {
            if (!getJMenuBar().getMenu(i).getText().equals("Help")) { // Keep Help enabled
                 getJMenuBar().getMenu(i).setEnabled(!inProgress);
            }
        }
    }

    private void handleAddPerson() {
        setPersonDialogInProgress(true);
        PersonDialog dialog = new PersonDialog(this, null);
        dialog.setVisible(true);
        setPersonDialogInProgress(false);

        if (dialog.isConfirmed()) {
            Person newPerson = dialog.getPerson();
            if (newPerson != null) {
                persons.add(newPerson);
                setDataChanged(true);
                refreshListDisplay();
                 // Select the newly added person
                personListDisplay.setSelectedIndex(listModel.getSize() - 1);
                personListDisplay.ensureIndexIsVisible(listModel.getSize() - 1);
            }
        }
    }

    private void handleEditPerson() {
        int selectedIndex = personListDisplay.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a person to edit.", "Edit Person", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // Important: Get the actual object from the 'persons' list that corresponds to the selection
        Person personToEdit = null;
        if (selectedIndex < persons.size() && selectedIndex < listModel.getSize()) {
             // Assuming listModel is a direct reflection of persons, or find it carefully
            personToEdit = listModel.getElementAt(selectedIndex); // If model elements are same objects as in persons list
            // Or, if model might be filtered/sorted differently than 'persons' master list,
            // you might need a more robust way to get the original object.
            // For simplicity here, assume listModel's selected element is the one to edit from 'persons'
            if (!persons.contains(personToEdit)) { // Fallback or error
                JOptionPane.showMessageDialog(this, "Error finding person in master list.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
             JOptionPane.showMessageDialog(this, "Selection index out of bounds.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        setPersonDialogInProgress(true);
        PersonDialog dialog = new PersonDialog(this, personToEdit);
        dialog.setVisible(true);
        setPersonDialogInProgress(false);

        if (dialog.isConfirmed()) {
            Person updatedPerson = dialog.getPerson();
            if (updatedPerson != null) {
                // If personToEdit was a direct reference from the 'persons' list,
                // and PersonDialog modifies it in place (isEditing=true path),
                // then the 'persons' list is already updated.
                // However, PersonDialog's createOrUpdatePerson creates a *new* instance if type changes.
                // So, it's safer to replace.
                int originalListIndex = persons.indexOf(personToEdit);
                if (originalListIndex != -1) {
                    persons.set(originalListIndex, updatedPerson);
                } else {
                    // Should not happen if personToEdit was from 'persons'
                    persons.remove(personToEdit); // remove old if not found by index
                    persons.add(updatedPerson); // add updated
                }
                setDataChanged(true);
                refreshListDisplay();
                // Try to re-select
                int newDisplayIndex = listModel.indexOf(updatedPerson);
                if (newDisplayIndex != -1) {
                     personListDisplay.setSelectedIndex(newDisplayIndex);
                     personListDisplay.ensureIndexIsVisible(newDisplayIndex);
                } else { // Fallback: select by original index if it's still valid
                    if (selectedIndex < listModel.getSize()) {
                        personListDisplay.setSelectedIndex(selectedIndex);
                        personListDisplay.ensureIndexIsVisible(selectedIndex);
                    }
                }
            }
        }
    }

    private void handleDeletePerson() {
        int selectedIndex = personListDisplay.getSelectedIndex();
        if (selectedIndex != -1) {
            Person personToDelete = listModel.getElementAt(selectedIndex);
            int response = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete " + personToDelete.getFirstName() + " " + personToDelete.getLastName() + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                persons.remove(personToDelete);
                setDataChanged(true);
                refreshListDisplay();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a person to delete.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void sortPersons(Comparator<Person> comparator) {
        if (persons.isEmpty() && listModel.isEmpty()) return; // Nothing to sort

        // If the listModel is showing a filtered list from search, sorting might be confusing.
        // For now, assume we always sort the master 'persons' list and refresh.
        // If search is active, ideally, clear search or re-apply search to sorted list.
        // Simplest: sorting clears any active search filter.
        if (!searchField.getText().trim().isEmpty()) {
            searchField.setText(""); // Clear search field
            // The refreshListDisplay will show all items from 'persons'
        }

        if (comparator == null) {
            Collections.sort(persons);
        } else {
            persons.sort(comparator);
        }
        setDataChanged(true); // Sorting changes the order, which is a savable change
        refreshListDisplay();
    }
    
    private void showHelpDialog() {
        // Using a JTextArea for better formatting and scrollability if text is long
        JTextArea helpTextArea = new JTextArea(20, 50); // Rows, Columns
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
            "Actions Menu (was Search Menu):\n" +
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
        helpTextArea.setFont(generalFont.deriveFont(Font.PLAIN, 13)); // Slightly smaller for text area

        JScrollPane helpScrollPane = new JScrollPane(helpTextArea);
        helpScrollPane.setPreferredSize(new Dimension(600, 400)); // Good default size for help

        JOptionPane.showMessageDialog(this, helpScrollPane, "About Person Manager", JOptionPane.INFORMATION_MESSAGE);
    }


    private void handleSearchByID() { // This is the specific OCCCPerson binary search
        List<OCCCPerson> occcPersons = persons.stream()
                                           .filter(OCCCPerson.class::isInstance)
                                           .map(OCCCPerson.class::cast)
                                           .sorted(Comparator.comparing(OCCCPerson::getStudentID, String.CASE_INSENSITIVE_ORDER))
                                           .collect(Collectors.toList());

        if (occcPersons.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No OCCCPersons in the list to search.", "Search by ID", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String studentIDToSearch = JOptionPane.showInputDialog(this, "Enter Student ID to search for:", "Find OCCCPerson by ID", JOptionPane.PLAIN_MESSAGE);
        if (studentIDToSearch == null || studentIDToSearch.trim().isEmpty()) {
            return;
        }
        studentIDToSearch = studentIDToSearch.trim();

        OCCCPerson foundPerson = PersonSearchUtil.binarySearchByID(occcPersons, studentIDToSearch, 0, occcPersons.size() - 1);

        if (foundPerson != null) {
            int originalIndex = -1;
            // We need to find this 'foundPerson' (which is from a temporary sorted list)
            // back in the main 'listModel' or 'persons' list to select it.
            for(int i=0; i < listModel.getSize(); i++) {
                if(listModel.getElementAt(i) == foundPerson) { // Direct object comparison
                    originalIndex = i;
                    break;
                }
            }
             if (originalIndex == -1) { // If not found by reference (e.g. if listModel uses copies, or search was on a copy)
                 // Fallback: search by unique properties if needed, but reference is best
                 for(int i=0; i < persons.size(); i++) {
                     if(persons.get(i) == foundPerson) {
                        // We found it in the master list, now find it in the current display model
                        int displayIndex = listModel.indexOf(persons.get(i));
                        if (displayIndex != -1) originalIndex = displayIndex;
                        break;
                     }
                 }
             }


            if(originalIndex != -1){
                personListDisplay.setSelectedIndex(originalIndex);
                personListDisplay.ensureIndexIsVisible(originalIndex);
                JOptionPane.showMessageDialog(this, "Found: " + foundPerson.toString(), "Search Result", JOptionPane.INFORMATION_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "Found data: " + foundPerson.toString() + "\n(Could not auto-select in the current view if list is filtered/sorted differently).", "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Student ID '" + studentIDToSearch + "' not found among OCCCPersons.", "Search Result", JOptionPane.WARNING_MESSAGE);
        }
    }


    public static void main(String[] args) {
        try {
            boolean nimbusFound = false;
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    nimbusFound = true;
                    break;
                }
            }
            if (!nimbusFound) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (Exception e) {
            System.err.println("Look and Feel setting error: " + e.getMessage());
            // Continue with default LAF
        }

        SwingUtilities.invokeLater(() -> {
            PersonManagerGUI gui = new PersonManagerGUI();
            gui.setVisible(true);
        });
    }
}