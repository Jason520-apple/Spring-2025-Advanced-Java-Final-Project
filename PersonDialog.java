

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Dialog for creating or editing a Person, RegisteredPerson, or OCCCPerson.
 * Dynamically rebuilds the form UI to only show fields relevant to the selected type.
 */
public class PersonDialog extends JDialog {

    private JTextField firstNameField, lastNameField, dobField, governmentIdField, studentIdField;
    private JComboBox<String> personTypeComboBox;
    private JButton saveButton, cancelButton;
    private boolean confirmed;
    private Person person;
    private final boolean isEditing;
    private final PersonManagerGUI parent;
    private JPanel formPanel;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US);

    public PersonDialog(PersonManagerGUI parent, Person person) {
        super(parent, "Add/Edit Person", true);
        this.parent = parent;
        this.person = person;
        this.isEditing = (person != null);
        this.confirmed = false;

        setupUI();
        setupListeners();
        initializeFields();

        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Constructs the basic layout including top panel, form, and buttons.
     */
    private void setupUI() {
        setLayout(new BorderLayout());

        // Dropdown for person type
        personTypeComboBox = new JComboBox<>(new String[]{"Person", "RegisteredPerson", "OCCCPerson"});
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Person Type:"));
        topPanel.add(personTypeComboBox);

        // Dynamic panel for form inputs
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(topPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Sets up event listeners for controls.
     */
    private void setupListeners() {
        saveButton.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                createPerson();
                dispose();
            }
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        personTypeComboBox.addActionListener(e -> updateFieldVisibility());
    }

    /**
     * Initializes field data for edit mode and triggers form rebuild.
     */
    private void initializeFields() {
        updateFieldVisibility();
        if (isEditing) {
            firstNameField.setText(person.getFirstName());
            lastNameField.setText(person.getLastName());
            dobField.setText(person.getDateOfBirth().toString());

            if (person instanceof RegisteredPerson rp) {
                governmentIdField.setText(rp.getGovernmentID());
                if (person instanceof OCCCPerson op) {
                    studentIdField.setText(op.getStudentID());
                    personTypeComboBox.setSelectedItem("OCCCPerson");
                } else {
                    personTypeComboBox.setSelectedItem("RegisteredPerson");
                }
            } else {
                personTypeComboBox.setSelectedItem("Person");
            }
        }
    }

    /**
     * Dynamically builds form components based on selected person type.
     */
    private void updateFieldVisibility() {
        formPanel.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        firstNameField = (firstNameField != null) ? firstNameField : new JTextField(20);
        lastNameField = (lastNameField != null) ? lastNameField : new JTextField(20);
        dobField = (dobField != null) ? dobField : new JTextField(20);
        governmentIdField = (governmentIdField != null) ? governmentIdField : new JTextField(20);
        studentIdField = (studentIdField != null) ? studentIdField : new JTextField(20);

        int y = 0;

        gbc.gridx = 0; gbc.gridy = y; formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1; formPanel.add(firstNameField, gbc);

        gbc.gridx = 0; gbc.gridy = ++y; formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1; formPanel.add(lastNameField, gbc);

        gbc.gridx = 0; gbc.gridy = ++y; formPanel.add(new JLabel("Date of Birth (MM/DD/YYYY):"), gbc);
        gbc.gridx = 1; formPanel.add(dobField, gbc);

        String type = (String) personTypeComboBox.getSelectedItem();

        if ("RegisteredPerson".equals(type) || "OCCCPerson".equals(type)) {
            gbc.gridx = 0; gbc.gridy = ++y; formPanel.add(new JLabel("Government ID:"), gbc);
            gbc.gridx = 1; formPanel.add(governmentIdField, gbc);
        }

        if ("OCCCPerson".equals(type)) {
            gbc.gridx = 0; gbc.gridy = ++y; formPanel.add(new JLabel("Student ID:"), gbc);
            gbc.gridx = 1; formPanel.add(studentIdField, gbc);
        }

        formPanel.revalidate();
        formPanel.repaint();
        pack();
    }

    /**
     * Validates the required fields before saving.
     */
    private boolean validateInput() {
        if (firstNameField.getText().trim().isEmpty() ||
            lastNameField.getText().trim().isEmpty() ||
            dobField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "First name, last name, and date of birth are required.");
            return false;
        }

        String type = (String) personTypeComboBox.getSelectedItem();

        if ("RegisteredPerson".equals(type) && governmentIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Government ID is required.");
            return false;
        }

        if ("OCCCPerson".equals(type) &&
            (governmentIdField.getText().trim().isEmpty() || studentIdField.getText().trim().isEmpty())) {
            JOptionPane.showMessageDialog(this, "Government and Student ID are required.");
            return false;
        }

        return true;
    }

    /**
     * Constructs the final Person object based on selected type and inputs.
     */
    private void createPerson() {
        try {
            String[] parts = dobField.getText().trim().split("/");
            int month = Integer.parseInt(parts[0]);
            int day = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);
            OCCCDate dob = new OCCCDate(day, month, year);

            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String governmentID = governmentIdField.getText().trim();
            String studentID = studentIdField.getText().trim();
            String type = (String) personTypeComboBox.getSelectedItem();

            switch (type) {
                case "Person" -> person = new Person(firstName, lastName, dob);
                case "RegisteredPerson" -> person = new RegisteredPerson(firstName, lastName, dob, governmentID);
                case "OCCCPerson" -> person = new OCCCPerson(firstName, lastName, dob, governmentID, studentID);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use MM/DD/YYYY.");
            confirmed = false;
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Person getPerson() {
        return person;
    }
}
