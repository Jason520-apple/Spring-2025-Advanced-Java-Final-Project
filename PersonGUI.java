// OCCC Spring 2025
// Advanced Java
// Final Project - Person GUI Application
// 
// This class provides a full-featured GUI application that allows the user to manage a list of Persons
// and their subtypes (RegisteredPerson, OCCCPerson). It includes menu-based file operations (New, Open, Save, Save As),
// GUI forms for data entry and editing, validation (including custom OCCCDate validation), and persistent storage using serialization.
// 
// The GUI allows users to:
// - Add, edit, and delete persons
// - Save/load lists to/from .dat files
// - Prevent saving while a dialog is open
// - Display custom-rendered person summaries in a JList
// - Prompt the user before losing unsaved changes on new/load/exit

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window; // Added for explicit Window type
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class PersonGUI extends JFrame implements ActionListener, ListCellRenderer<Person> {

    private ArrayList<Person> pList = new ArrayList<>();
    private boolean dataChanged = false;

    private JMenuItem newItem, openItem, saveItem, saveAsItem, exitItem, helpDialogItem;
    private JFileChooser fileChooser;
    private File currentFile;
    private int selectedPersonIndex = -1;

    private JButton createButton, editButton, deleteButton;
    private DefaultListModel<Person> listModel;
    private JList<Person> personListDisplay;
	private JComboBox<Integer> dayComboBox, monthComboBox, yearComboBox; // NEW for DOB dropdowns

    public Color cardColor = UIManager.getColor("List.background");
    public Color textColor = UIManager.getColor("List.foreground");
    public Color listSelectionBackgroundColor = UIManager.getColor("List.selectionBackground");
    public Color listSelectionForegroundColor = UIManager.getColor("List.selectionForeground");
    private static final Color SECONDARY_TEXT_COLOR = new Color(80, 80, 80);
    private static final Font NAME_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font DETAIL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    private JTextField dlgFirstNameField, dlgLastNameField, dlgDobField, dlgGovIdField, dlgStudentIdField;
    private JComboBox<String> dlgPersonTypeComboBox;
    private JLabel dlgGovIdLabel, dlgStudentIdLabel;
    private boolean dialogConfirmed = false;
    private Person dialogPerson = null;


    Font appFontLarge = new Font("Arial", Font.BOLD, 24);

     public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // fallback to default
        }
        SwingUtilities.invokeLater(() -> new PersonGUI().setVisible(true));
    }



     public PersonGUI() {
        setTitle("Person Management GUI");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });

        setupMenuBar();
        setupMainPanel();
        updateGUIStates();
        pack();
        setMinimumSize(new Dimension(600, 500));
        setLocationRelativeTo(null);
    }


	//This method builds and attaches the menu bar to the current JFrame using setJMenuBar

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        newItem = new JMenuItem("New");
        newItem.setMnemonic(KeyEvent.VK_N);
        openItem = new JMenuItem("Open...");
        openItem.setMnemonic(KeyEvent.VK_O);
        saveItem = new JMenuItem("Save");
        saveItem.setMnemonic(KeyEvent.VK_S);
        saveAsItem = new JMenuItem("Save As...");
        saveAsItem.setMnemonic(KeyEvent.VK_A);
        exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic(KeyEvent.VK_X);

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        helpDialogItem = new JMenuItem("About");
        helpMenu.add(helpDialogItem);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        newItem.addActionListener(this);
        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        saveAsItem.addActionListener(this);
        exitItem.addActionListener(this);
        helpDialogItem.addActionListener(this);
    }


//Builds Main Panel
    private void setupMainPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Person List Manager", SwingConstants.CENTER);
        titleLabel.setFont(appFontLarge);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        topPanel.add(titleLabel, BorderLayout.NORTH);
        add(topPanel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        personListDisplay = new JList<>(listModel);
        personListDisplay.setCellRenderer(this);
        personListDisplay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        personListDisplay.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedPersonIndex = personListDisplay.getSelectedIndex();
                updateGUIStates();
            }
        });

        JScrollPane listScrollPane = new JScrollPane(personListDisplay);
        listScrollPane.setPreferredSize(new Dimension(550, 350));
        add(listScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        createButton = new JButton("Add Person");
        editButton = new JButton("Edit Selected");
        deleteButton = new JButton("Delete Selected");
        buttonPanel.add(createButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        createButton.addActionListener(this);
        editButton.addActionListener(this);
        deleteButton.addActionListener(this);
    }

    private JPanel rendererPanel;
    private JLabel nameLabel, dobLabel, govIdLabel, studentIdLabel;


	/**
 * Initializes the UI components used for rendering a person record.
 * 
 * This method creates a bordered panel that displays person information,
 * including name, date of birth, government ID, and student ID. Labels are
 * styled using predefined fonts and arranged vertically within a transparent panel.
 * The entire structure is placed in the center of the renderer panel using BorderLayout.
 */
    private void initRendererComponents() {
        rendererPanel = new JPanel(new BorderLayout());
        rendererPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

        nameLabel = new JLabel(); nameLabel.setFont(NAME_FONT);
        dobLabel = new JLabel(); dobLabel.setFont(DETAIL_FONT);
        govIdLabel = new JLabel(); govIdLabel.setFont(DETAIL_FONT);
        studentIdLabel = new JLabel(); studentIdLabel.setFont(DETAIL_FONT);

        detailsPanel.add(nameLabel);
        detailsPanel.add(dobLabel);
        detailsPanel.add(govIdLabel);
        detailsPanel.add(studentIdLabel);
        rendererPanel.add(detailsPanel, BorderLayout.CENTER);
    }


	/**
 * Configures and returns the component used to render a Person in a JList.
 *
 * Depending on the runtime type of the Person (e.g., RegisteredPerson, OCCCPerson),
 * this method populates and styles the display labels accordingly. It also updates the visual appearance
 * based on selection state.
 */

    @Override
    public Component getListCellRendererComponent(JList<? extends Person> list, Person person,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        if (rendererPanel == null) initRendererComponents();

        String personType = person.getClass().getSimpleName();
        nameLabel.setText(personType + ": " + person.getFirstName() + " " + person.getLastName());
        dobLabel.setText("DOB: " + (person.getDOB() != null ? person.getDOB().toString() : "N/A"));

        govIdLabel.setVisible(false);
        studentIdLabel.setVisible(false);

        if (person instanceof RegisteredPerson rp) {
            govIdLabel.setText("Gov ID: " + rp.getGovernmentID());
            govIdLabel.setVisible(true);
            if (person instanceof OCCCPerson op) {
                studentIdLabel.setText("Student ID: " + op.getStudentID());
                studentIdLabel.setVisible(true);
            }
        }

        if (isSelected) {
            rendererPanel.setBackground(listSelectionBackgroundColor);
            nameLabel.setForeground(listSelectionForegroundColor);
            dobLabel.setForeground(listSelectionForegroundColor);
            govIdLabel.setForeground(listSelectionForegroundColor);
            studentIdLabel.setForeground(listSelectionForegroundColor);
        } else {
            rendererPanel.setBackground(cardColor);
            nameLabel.setForeground(textColor);
            dobLabel.setForeground(SECONDARY_TEXT_COLOR);
            govIdLabel.setForeground(SECONDARY_TEXT_COLOR);
            studentIdLabel.setForeground(SECONDARY_TEXT_COLOR);
        }
        return rendererPanel;
    }

/**
 * Refreshes the person list display by reloading the data from pList
 * into the list model and restoring the previously selected index if valid.
 * 
 * If no valid selection exists, it selects the first item (if any), or clears selection.
 * Also updates the state of relevant GUI controls.
 */
	
    private void refreshPersonDisplay() {
        listModel.clear();
        for (Person p : pList) {
            listModel.addElement(p);
        }
        if (selectedPersonIndex >= 0 && selectedPersonIndex < pList.size()) {
            personListDisplay.setSelectedIndex(selectedPersonIndex);
            personListDisplay.ensureIndexIsVisible(selectedPersonIndex);
        } else if (!pList.isEmpty()){
            personListDisplay.setSelectedIndex(0);
            selectedPersonIndex = 0;
            personListDisplay.ensureIndexIsVisible(selectedPersonIndex);
        } else {
            selectedPersonIndex = -1;
        }
        updateGUIStates();
    }


	/**
 * Updates the enabled/disabled states of GUI controls based on the current application state.
 * 
 * - Enables or disables the Save and Save As menu items depending on file presence and data state.
 * - Enables or disables Edit and Delete buttons based on whether a valid selection exists.
 * - Updates the window title to reflect the current file name and unsaved changes (marked with *).
 */

    private void updateGUIStates() {
        boolean hasData = !pList.isEmpty();
        boolean selectionExists = selectedPersonIndex != -1 && hasData;

        saveItem.setEnabled(currentFile != null && hasData && dataChanged);
        saveAsItem.setEnabled(hasData);
        editButton.setEnabled(selectionExists);
        deleteButton.setEnabled(selectionExists);

        String title = "Person Management GUI";
        if (currentFile != null) title += " - " + currentFile.getName();
        if (dataChanged) title += "*";
        setTitle(title);
    }


		/**
     * Controls enable/disable state of main buttons and menu items
     * during modal operations like input dialogs.
     */
    private void setOperationInProgress(boolean inProgress) {
        boolean enableControls = !inProgress;
        newItem.setEnabled(enableControls);
        openItem.setEnabled(enableControls);
        exitItem.setEnabled(enableControls);
        createButton.setEnabled(enableControls);
        personListDisplay.setEnabled(enableControls);

        if (inProgress) {
            saveItem.setEnabled(false);
            saveAsItem.setEnabled(false);
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
        } else {
            updateGUIStates();
        }
    }
    


    /**
     * Creates the input form panel for a person.
     * Supports both create and edit modes.
     */
    private JPanel createPersonInputPanel(Person personToEdit) {
        initializeDobDropdowns();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        dlgPersonTypeComboBox = new JComboBox<>(new String[]{"Person", "RegisteredPerson", "OCCCPerson"});
        dlgPersonTypeComboBox.setPreferredSize(new Dimension(200, dlgPersonTypeComboBox.getPreferredSize().height));

        dlgFirstNameField = new JTextField(20);
        dlgLastNameField = new JTextField(20);
        dlgGovIdField = new JTextField(15);
        dlgStudentIdField = new JTextField(15);

        dlgGovIdLabel = new JLabel("Government ID:");
        dlgStudentIdLabel = new JLabel("Student ID:");

        // Row 0: Person Type
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Person Type:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; panel.add(dlgPersonTypeComboBox, gbc);
        gbc.weightx = 0.0;

        // Row 1: First Name
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; panel.add(dlgFirstNameField, gbc);
        gbc.weightx = 0.0;

        // Row 2: Last Name
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0; panel.add(dlgLastNameField, gbc);
        gbc.weightx = 0.0;

        // Row 3: DOB Dropdowns
        JPanel dobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dobPanel.add(new JLabel("DOB:"));
        dobPanel.add(dayComboBox);
        dobPanel.add(monthComboBox);
        dobPanel.add(yearComboBox);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("DOB (DD MM YYYY):"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0; panel.add(dobPanel, gbc);
        gbc.weightx = 0.0;

        // Row 4: Gov ID
        gbc.gridx = 0; gbc.gridy = 4; panel.add(dlgGovIdLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1.0; panel.add(dlgGovIdField, gbc);
        gbc.weightx = 0.0;

        // Row 5: Student ID
        gbc.gridx = 0; gbc.gridy = 5; panel.add(dlgStudentIdLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 1.0; panel.add(dlgStudentIdField, gbc);
        gbc.weightx = 0.0;

        dlgPersonTypeComboBox.addActionListener(e -> updateDialogFieldsVisibility());

        if (personToEdit != null) {
            dlgFirstNameField.setText(personToEdit.getFirstName());
            dlgLastNameField.setText(personToEdit.getLastName());
            if (personToEdit.getDOB() != null) {
                OCCCDate dob = personToEdit.getDOB();
                dayComboBox.setSelectedItem(dob.getDayOfMonth());
                monthComboBox.setSelectedItem(dob.getMonthNumber());
                yearComboBox.setSelectedItem(dob.getYear());
            }
            if (personToEdit instanceof OCCCPerson op) {
                dlgPersonTypeComboBox.setSelectedItem("OCCCPerson");
                dlgGovIdField.setText(op.getGovernmentID());
                dlgStudentIdField.setText(op.getStudentID());
            } else if (personToEdit instanceof RegisteredPerson rp) {
                dlgPersonTypeComboBox.setSelectedItem("RegisteredPerson");
                dlgGovIdField.setText(rp.getGovernmentID());
            } else {
                dlgPersonTypeComboBox.setSelectedItem("Person");
            }
            dlgPersonTypeComboBox.setEnabled(false);
        } else {
            dlgPersonTypeComboBox.setSelectedIndex(0);
        }

        updateDialogFieldsVisibilityInternalLogic();
        return panel;
    }


	/**
 * Initializes the day, month, and year dropdowns (JComboBoxes) used for selecting a date of birth.
 * 
 * - Days: 1 through 31
 * - Months: 1 through 12
 * - Years: From current year down to 1900
 * 
 * These combo boxes are used for user-friendly input of DOB components.
 */
	
    private void initializeDobDropdowns() {
        dayComboBox = new JComboBox<>();
        for (int i = 1; i <= 31; i++) dayComboBox.addItem(i);

        monthComboBox = new JComboBox<>();
        for (int i = 1; i <= 12; i++) monthComboBox.addItem(i);

        yearComboBox = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int y = currentYear; y >= 1900; y--) yearComboBox.addItem(y);
    }



	    // Separated logic from pack() for initial setup
    private void updateDialogFieldsVisibilityInternalLogic() {
        String type = (String) dlgPersonTypeComboBox.getSelectedItem();
        if (type == null) type = "Person"; // Default

        boolean showGovId = "RegisteredPerson".equals(type) || "OCCCPerson".equals(type);
        boolean showStudentId = "OCCCPerson".equals(type);

        if (dlgGovIdLabel != null) dlgGovIdLabel.setVisible(showGovId);
        if (dlgGovIdField != null) dlgGovIdField.setVisible(showGovId);
        if (dlgStudentIdLabel != null) dlgStudentIdLabel.setVisible(showStudentId);
        if (dlgStudentIdField != null) dlgStudentIdField.setVisible(showStudentId);
    }


    private void updateDialogFieldsVisibility() {
        updateDialogFieldsVisibilityInternalLogic(); // Update visibility of fields

        // Repack dialog only if it's part of a visible window hierarchy
        if (dlgPersonTypeComboBox != null) {
            Window ancestorWindow = SwingUtilities.getWindowAncestor(dlgPersonTypeComboBox);
            if (ancestorWindow != null && ancestorWindow.isVisible()) { // Check isVisible()
                ancestorWindow.pack();
            }
        }
    }

  

	    /**
     * Displays a dialog for creating or editing a Person.
     * Returns true if user input was confirmed and valid.
     */
    private boolean showPersonInputDialog(Person personToEdit) {
        dialogConfirmed = false;
        dialogPerson = null;

        JPanel inputPanel = createPersonInputPanel(personToEdit);
        String title = (personToEdit == null) ? "Add New Person" : "Edit Person";

        boolean validInput = false;
        while (!validInput) {
            int result = JOptionPane.showConfirmDialog(this, inputPanel, title,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String firstName = dlgFirstNameField.getText().trim();
                String lastName = dlgLastNameField.getText().trim();
                String type = (String) dlgPersonTypeComboBox.getSelectedItem();
                String govId = dlgGovIdField.getText().trim();
                String studentId = dlgStudentIdField.getText().trim();

                if (!validatePersonInput(firstName, lastName,  type, govId, studentId)) {
                    continue;
                }

                try {
                    OCCCDate dob = parseDateFromDropdowns();
                    dialogPerson = buildPersonObject(type, firstName, lastName, dob, govId, studentId, personToEdit);
                    dialogConfirmed = true;
                    validInput = true;
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Invalid Date: " + e.getMessage(), "Date Error", JOptionPane.ERROR_MESSAGE);
                    //dlgDobField.requestFocusInWindow();
                    //dlgDobField.selectAll();
                }
            } else {
                dialogConfirmed = false;
                validInput = true;
            }
        }
        return dialogConfirmed;
    }




  /**
 * Parses the selected values from the day, month, and year combo boxes
 * and constructs an OCCCDate object.
 *
 *  an  OCCCDate representing the selected date
 * InvalidOCCCDateException if the constructed date is invalid (e.g., February 30)
 */
    private OCCCDate parseDateFromDropdowns() throws InvalidOCCCDateException {
        int day = (Integer) dayComboBox.getSelectedItem();
        int month = (Integer) monthComboBox.getSelectedItem();
        int year = (Integer) yearComboBox.getSelectedItem();
        return new OCCCDate(day, month, year);
    }





	/**
 * Validates the input fields required to create or update a  Person object.
 *
 * Checks for the presence of required fields based on the selected person type:
 * - First name, last name, and DOB must always be provided.
 * - Government ID is required for  RegisteredPerson and OCCCPerson.
 * - Student ID is additionally required for  OCCCPerson.
 *
 * Displays error messages in a dialog if validation fails.
 */
    private boolean validatePersonInput(String first, String last,  String type, String govId, String studentId) {
      if (first.isEmpty() || last.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First name, last name, and DOB are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (("RegisteredPerson".equals(type) || "OCCCPerson".equals(type)) && govId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Government ID is required for this person type.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if ("OCCCPerson".equals(type) && studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID is required for OCCCPerson.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }


	    /**
     * Builds a new or updated Person object from input fields.
     */
    private Person buildPersonObject(String type, String first, String last, OCCCDate dob, String govId, String studentId, Person original) {
        if (original == null) {
            if ("OCCCPerson".equals(type)) {
                return new OCCCPerson(new RegisteredPerson(first, last, dob, govId), studentId);
            } else if ("RegisteredPerson".equals(type)) {
                return new RegisteredPerson(first, last, dob, govId);
            } else {
                return new Person(first, last, dob);
            }
        } else {
            original.setFirstName(first);
            original.setLastName(last);
            original.setDOB(dob);
            if (original instanceof OCCCPerson op) {
                op.setGovernmentID(govId);
                op.setStudentID(studentId);
            } else if (original instanceof RegisteredPerson rp) {
                rp.setGovernmentID(govId);
            }
            return original;
        }
    }


/**
 * Handles all ActionEvents triggered by UI components such as menu items and buttons.
 * 
 * Dispatches actions based on the source of the event:
 * - File menu items: New, Open, Save, Save As, Exit
 * - Help menu item: About dialog
 * - Main UI buttons: Create, Edit, Delete person
 *
 * the ActionEvent triggered by the user interaction
 */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == newItem) handleNewFile();
        else if (source == openItem) handleOpenFile();
        else if (source == saveItem) handleSave();
        else if (source == saveAsItem) handleSaveAsFile();
        else if (source == exitItem) handleExit();
        else if (source == helpDialogItem) showHelpDialog();
        else if (source == createButton) handleCreateNewPerson();
        else if (source == editButton) handleEditPerson();
        else if (source == deleteButton) handleDeletePerson();
    }



    // Handles Add button - creates new Person via dialog
    private void handleCreateNewPerson() {
        setOperationInProgress(true);
        if (showPersonInputDialog(null)) {
            if (dialogPerson != null) {
                pList.add(dialogPerson);
                dataChanged = true;
                selectedPersonIndex = pList.size() - 1;
                refreshPersonDisplay();
            }
        }
        setOperationInProgress(false);
    }

  // Handles Edit button - allows editing selected person
    public void handleEditPerson() {
        if (selectedPersonIndex < 0 || selectedPersonIndex >= pList.size()) {
            JOptionPane.showMessageDialog(this, "No person selected.", "Edit Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        setOperationInProgress(true);
        Person personToEdit = pList.get(selectedPersonIndex);
        if (showPersonInputDialog(personToEdit)) {
            if (dialogPerson != null) {
                pList.set(selectedPersonIndex, dialogPerson);
                dataChanged = true;
                refreshPersonDisplay();
            }
        }
        setOperationInProgress(false);
    }
    
    // Handles Delete button - removes selected person
    private void handleDeletePerson() {
        if (selectedPersonIndex < 0 || selectedPersonIndex >= pList.size()) {
            JOptionPane.showMessageDialog(this, "No person selected.", "Delete Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Person p = pList.get(selectedPersonIndex);
        if (JOptionPane.showConfirmDialog(this, "Delete " + p.getFirstName() + " " + p.getLastName() + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            pList.remove(selectedPersonIndex);
            dataChanged = true;
            if (selectedPersonIndex >= pList.size() && !pList.isEmpty()) selectedPersonIndex = pList.size() - 1;
            else if (pList.isEmpty()) selectedPersonIndex = -1;
            refreshPersonDisplay();
        }
    }


	// Handles File > New
    private void handleNewFile() {
        if (!confirmSaveIfNeeded()) return;
        pList.clear();
        currentFile = null;
        dataChanged = false;
        selectedPersonIndex = -1;
        refreshPersonDisplay();
    }

    // Handles File > Open
    @SuppressWarnings("unchecked")
    private void handleOpenFile() {
        if (!confirmSaveIfNeeded()) return;
        if (fileChooser == null) fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(currentFile != null ? currentFile.getParentFile() : new File(System.getProperty("user.home")));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File sf = fileChooser.getSelectedFile();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(sf))) {
                pList.clear();
                pList.addAll((List<Person>) ois.readObject());
                currentFile = sf;
                dataChanged = false;
                selectedPersonIndex = -1;
                refreshPersonDisplay();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

 
    // Handles File > Save
    private boolean handleSave() {
        if (currentFile == null) return handleSaveAsFile();
        if (!dataChanged) return true;
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(currentFile))) {
            oos.writeObject(pList);
            dataChanged = false;
            updateGUIStates();
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }


    // Handles File > Save As
    private boolean handleSaveAsFile() {
        if (fileChooser == null) fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(currentFile != null ? currentFile : new File("persons.dat"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File sf = fileChooser.getSelectedFile();
            String fp = sf.getAbsolutePath();
            if (!fp.toLowerCase().endsWith(".dat")) sf = new File(fp + ".dat");
            if (sf.exists() && JOptionPane.showConfirmDialog(this, "Overwrite " + sf.getName() + "?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return false;
            currentFile = sf;
            return handleSave();
        }
        return false;
    }


	   // Handles File > Exit
    private void handleExit() {
        if (!confirmSaveIfNeeded()) return;
        System.exit(0);
    }


	
	   private void showHelpDialog() {
        String helpMessage = "Person GUI Application - About\n\n" +
                             "File Menu:\n New, Open, Save, Save As, Exit.\n\n" +
                             "Controls:\n Add, Edit, Delete persons.\n\n" +
                             "Date Format for input: DD MM YYYY (e.g., 25 12 2023)";
        JOptionPane.showMessageDialog(this, helpMessage, "About Person GUI", JOptionPane.INFORMATION_MESSAGE);
    }

		    // Centralized prompt for save-before-action logic
    private boolean confirmSaveIfNeeded() {
        if (!dataChanged) return true;
        int result = JOptionPane.showConfirmDialog(this,
            "Save changes?", "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.CANCEL_OPTION) return false;
        if (result == JOptionPane.YES_OPTION) return handleSave();
        return true;
    }



}

 



