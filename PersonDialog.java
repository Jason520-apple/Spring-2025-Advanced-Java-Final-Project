/**
 * PersonDialog.java
 *
 * A modal Swing dialog for creating or editing Person, RegisteredPerson, or OCCCPerson instances.
 * 
 * Dynamically adjusts input fields based on the selected person type. Validates all input,
 * collects user data, and returns a constructed Person object if confirmed.
 *
 * Demonstrates:
 * - Dynamic UI layout with GridBagLayout
 * - Event handling and validation in Swing
 * - Object-oriented GUI design with polymorphism
 *
 * Author: Amida Fombutu
 * Course: CS2463 Advanced Java – Spring 2025

 */




import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PersonDialog extends JDialog {

    // UI Components
    private JTextField firstNameField, lastNameField, dobField, governmentIdField, studentIdField;
    private JComboBox<String> personTypeComboBox;
    private JButton saveButton, cancelButton;
    private JPanel formPanel;

    // Flags and state
    private boolean confirmed;
    private Person person;
    private final boolean isEditing;

    // Styling constants
    private final Font generalFont = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);
    private final Color accentColor = new Color(0, 120, 215);
    private final Color buttonTextColor = Color.WHITE;

    // Constructor to create the dialog
    public PersonDialog(Frame parent, Person personToEdit) {
        super(parent, (personToEdit == null ? "Add Person" : "Edit Person"), true);
        this.person = personToEdit;
        this.isEditing = (personToEdit != null);
        this.confirmed = false;

        // Apply consistent font to all components
        UIManager.put("Label.font", generalFont);
        UIManager.put("TextField.font", generalFont);
        UIManager.put("ComboBox.font", generalFont);

        setupUI();
        setupListeners();
        initializeFields();

        pack();
        setMinimumSize(new Dimension(450, getPreferredSize().height));
        setLocationRelativeTo(parent);
    }

    // Factory method to create a custom-styled button
    private JButton createStyledButton(String text, Font font, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    // Constructs the main layout and adds fields
    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        personTypeComboBox = new JComboBox<>(new String[]{"Person", "RegisteredPerson", "OCCCPerson"});
        personTypeComboBox.setFont(generalFont);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        topPanel.add(new JLabel("Person Type:"));
        topPanel.add(personTypeComboBox);
        topPanel.setBorder(new EmptyBorder(0, 0, 5, 0));

        formPanel = new JPanel(new GridBagLayout());

        // Initialize input fields
        firstNameField = new JTextField(25);
        lastNameField = new JTextField(25);
        dobField = new JTextField(12);
        governmentIdField = new JTextField(25);
        studentIdField = new JTextField(25);

        // Apply font
        firstNameField.setFont(generalFont);
        lastNameField.setFont(generalFont);
        dobField.setFont(generalFont);
        governmentIdField.setFont(generalFont);
        studentIdField.setFont(generalFont);

        updateFieldVisibility();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        saveButton = createStyledButton("Save", buttonFont, accentColor, buttonTextColor);
        cancelButton = createStyledButton("Cancel", buttonFont, new Color(108, 117, 125), buttonTextColor);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(topPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Controls which fields are visible based on person type
    private void updateFieldVisibility() {
        formPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        // First Name
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1; formPanel.add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1; formPanel.add(lastNameField, gbc);

        // DOB
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Date of Birth (MM/DD/YYYY):"), gbc);
        gbc.gridx = 1; formPanel.add(dobField, gbc);

        String type = (String) personTypeComboBox.getSelectedItem();
        int row = 3;

        if ("RegisteredPerson".equals(type) || "OCCCPerson".equals(type)) {
            JLabel govLabel = new JLabel("Government ID:");
            govLabel.setName("govIdLabel");
            gbc.gridx = 0; gbc.gridy = row; formPanel.add(govLabel, gbc);
            gbc.gridx = 1; formPanel.add(governmentIdField, gbc);
            row++;
        }

        if ("OCCCPerson".equals(type)) {
            JLabel stuLabel = new JLabel("Student ID:");
            stuLabel.setName("stuIdLabel");
            gbc.gridx = 0; gbc.gridy = row; formPanel.add(stuLabel, gbc);
            gbc.gridx = 1; formPanel.add(studentIdField, gbc);
        }

        formPanel.revalidate();
        formPanel.repaint();

        if (this.isVisible()) {
            pack();
            setMinimumSize(new Dimension(450, getPreferredSize().height));
        }
    }

    // Adds all button and combo box listeners
    private void setupListeners() {
        saveButton.addActionListener(e -> {
            if (validateInput() && createOrUpdatePerson()) {
                confirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            person = null;
            dispose();
        });

        personTypeComboBox.addActionListener(e -> updateFieldVisibility());
    }

    // Pre-fills the dialog if in edit mode
    private void initializeFields() {
        if (isEditing && person != null) {
            personTypeComboBox.setEnabled(false);
            firstNameField.setText(person.getFirstName());
            lastNameField.setText(person.getLastName());

            if (person.getDateOfBirth() != null) {
                OCCCDate dob = person.getDateOfBirth();
                dobField.setText(String.format("%02d/%02d/%04d", dob.getMonthNumber(), dob.getDayOfMonth(), dob.getYear()));
            }

            if (person instanceof RegisteredPerson rp) {
                personTypeComboBox.setSelectedItem("RegisteredPerson");
                governmentIdField.setText(rp.getGovernmentID());
                if (person instanceof OCCCPerson op) {
                    personTypeComboBox.setSelectedItem("OCCCPerson");
                    studentIdField.setText(op.getStudentID());
                }
            }
        } else {
            personTypeComboBox.setSelectedIndex(0);
        }
        updateFieldVisibility();
    }

    // Validates all user input before processing
    private boolean validateInput() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String dobStr = dobField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || dobStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All required fields must be filled.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String[] parts = dobStr.split("/");
        if (parts.length != 3 || !parts[0].matches("\\d+") || !parts[1].matches("\\d+") || !parts[2].matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Use MM/DD/YYYY for the date format.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String type = (String) personTypeComboBox.getSelectedItem();
        if (("RegisteredPerson".equals(type) || "OCCCPerson".equals(type)) && governmentIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Government ID is required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if ("OCCCPerson".equals(type) && studentIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID is required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    // Creates or updates a Person object depending on the mode
    private boolean createOrUpdatePerson() {
        try {
            String[] parts = dobField.getText().trim().split("/");
            OCCCDate dob = new OCCCDate(Integer.parseInt(parts[1]), Integer.parseInt(parts[0]), Integer.parseInt(parts[2]));

            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String govId = governmentIdField.getText().trim();
            String stuId = studentIdField.getText().trim();
            String type = (String) personTypeComboBox.getSelectedItem();

            if (isEditing && person != null) {
                person.firstName = firstName;
                person.lastName = lastName;
                person.setDateOfBirth(dob);

                if (person instanceof RegisteredPerson rp) {
                    rp.governmentID = govId;
                    if (person instanceof OCCCPerson op) {
                        op.studentID = stuId;
                    }
                }
            } else {
                switch (type) {
                    case "Person" -> person = new Person(firstName, lastName, dob);
                    case "RegisteredPerson" -> person = new RegisteredPerson(firstName, lastName, dob, govId);
                    case "OCCCPerson" -> person = new OCCCPerson(firstName, lastName, dob, govId, stuId);
                    default -> {
                        JOptionPane.showMessageDialog(this, "Unknown person type.", "Error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Person getPerson() {
        return person;
    }
}
