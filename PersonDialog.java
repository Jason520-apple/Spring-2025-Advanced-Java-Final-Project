// --- File: PersonDialog.java ---
package personUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
// import java.awt.event.*; // Not strictly needed if only using action listeners via lambda

public class PersonDialog extends JDialog {

    private JTextField firstNameField, lastNameField, dobField, governmentIdField, studentIdField;
    private JComboBox<String> personTypeComboBox;
    private JButton saveButton, cancelButton;
    private boolean confirmed;
    private Person person;
    private final boolean isEditing;
    // private final Frame parentFrame; // Not explicitly used, JDialog handles parent relationship

    private final Font generalFont = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);
    private final Color accentColor = new Color(0, 120, 215); // Same as main GUI
    private final Color buttonTextColor = Color.WHITE;


    public PersonDialog(Frame parent, Person personToEdit) {
        super(parent, (personToEdit == null ? "Add Person" : "Edit Person"), true);
        this.person = personToEdit;
        this.isEditing = (personToEdit != null);
        this.confirmed = false;

        // Apply general font to dialog components
        UIManager.put("Label.font", generalFont);
        UIManager.put("TextField.font", generalFont);
        UIManager.put("ComboBox.font", generalFont);
        // UIManager.put("Button.font", buttonFont); // Handled by createStyledButton approach

        setupUI();
        setupListeners();
        initializeFields();

        pack();
        setMinimumSize(new Dimension(450, getPreferredSize().height)); // Ensure min width, height from pack
        setLocationRelativeTo(parent);
    }

    private JButton createStyledButton(String text, Font font, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
         button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                BorderFactory.createEmptyBorder(7, 12, 7, 12) // Slightly less padding for dialog
        ));
        button.addMouseListener(new java.awt.event.MouseAdapter() { // Specify package for MouseAdapter
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    private JPanel formPanel;

    private void setupUI() {
        setLayout(new BorderLayout(10,10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));


        personTypeComboBox = new JComboBox<>(new String[]{"Person", "RegisteredPerson", "OCCCPerson"});
        personTypeComboBox.setFont(generalFont);
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JLabel typeLabel = new JLabel("Person Type:");
        typeLabel.setFont(generalFont);
        topPanel.add(typeLabel);
        topPanel.add(personTypeComboBox);
        topPanel.setBorder(new EmptyBorder(0,0,5,0));


        formPanel = new JPanel(new GridBagLayout());
        // formPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Padding inside the form panel

        firstNameField = new JTextField(25); // Increased default width
        lastNameField = new JTextField(25);
        dobField = new JTextField(12); // Specific size for date
        governmentIdField = new JTextField(25);
        studentIdField = new JTextField(25);

        // Apply font to text fields
        firstNameField.setFont(generalFont);
        lastNameField.setFont(generalFont);
        dobField.setFont(generalFont);
        governmentIdField.setFont(generalFont);
        studentIdField.setFont(generalFont);


        updateFieldVisibility();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBorder(new EmptyBorder(10,0,0,0)); // Padding above buttons
        saveButton = createStyledButton("Save", buttonFont, accentColor, buttonTextColor);
        cancelButton = createStyledButton("Cancel", buttonFont, new Color(108, 117, 125), buttonTextColor); // Gray
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(topPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }


    private void updateFieldVisibility() {
        formPanel.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4); // Uniform spacing
        gbc.anchor = GridBagConstraints.WEST; // Align labels to left

        // First Name
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(firstNameField, gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0; // Reset

        // Last Name
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(lastNameField, gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;

        // Date of Birth
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Date of Birth (MM/DD/YYYY):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(dobField, gbc); // dobField width set at init
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;


        String type = (String) personTypeComboBox.getSelectedItem();
        if (type == null) type = "Person";

        // Hide fields initially, then show based on type
        governmentIdField.setVisible(false);
        studentIdField.setVisible(false);
        // Detach labels if their fields are hidden to prevent empty space
        Component govLabel = findComponentByName(formPanel, "govIdLabel");
        if (govLabel != null) govLabel.setVisible(false);
        Component stuLabel = findComponentByName(formPanel, "stuIdLabel");
        if (stuLabel != null) stuLabel.setVisible(false);


        int currentRow = 2; // Last row used was for DOB

        if ("RegisteredPerson".equals(type) || "OCCCPerson".equals(type)) {
            currentRow++;
            JLabel gLabel = new JLabel("Government ID:");
            gLabel.setName("govIdLabel");
            gbc.gridx = 0; gbc.gridy = currentRow; formPanel.add(gLabel, gbc);
            gbc.gridx = 1; gbc.gridy = currentRow; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            formPanel.add(governmentIdField, gbc);
            governmentIdField.setVisible(true);
            gLabel.setVisible(true);
            gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        }

        if ("OCCCPerson".equals(type)) {
            currentRow++;
            JLabel sLabel = new JLabel("Student ID:");
            sLabel.setName("stuIdLabel");
            gbc.gridx = 0; gbc.gridy = currentRow; formPanel.add(sLabel, gbc);
            gbc.gridx = 1; gbc.gridy = currentRow; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            formPanel.add(studentIdField, gbc);
            studentIdField.setVisible(true);
            sLabel.setVisible(true);
            gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        }

        formPanel.revalidate();
        formPanel.repaint();
        // Call pack here or rely on dialog.pack() after this method returns.
        // If calling pack() here, ensure dialog is visible or becomes visible soon after for correct sizing.
        // For now, we pack the dialog after initializeFields in constructor.
        // If the dialog is already visible and type changes, pack might be needed.
        if (this.isVisible()) {
            pack();
            setMinimumSize(new Dimension(450, getPreferredSize().height));
        }
    }
    
    // Helper to find component by name (useful if JLabels are added/removed dynamically)
    private Component findComponentByName(Container container, String name) {
        for (Component comp : container.getComponents()) {
            if (name.equals(comp.getName())) {
                return comp;
            }
        }
        return null;
    }


    // ... (setupListeners, initializeFields, validateInput, createOrUpdatePerson, isConfirmed, getPerson remain mostly the same)
    // Ensure initializeFields correctly populates these styled fields.
    // Ensure createOrUpdatePerson correctly retrieves values.

    private void setupListeners() {
        saveButton.addActionListener(e -> {
            if (validateInput()) {
                if (createOrUpdatePerson()) {
                    confirmed = true;
                    dispose();
                }
            }
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            person = null;
            dispose();
        });

        personTypeComboBox.addActionListener(e -> updateFieldVisibility());
    }

    private void initializeFields() {
        if (isEditing && person != null) {
            personTypeComboBox.setEnabled(false);

            firstNameField.setText(person.getFirstName());
            lastNameField.setText(person.getLastName());

            if (person.getDateOfBirth() != null) {
                OCCCDate dob = person.getDateOfBirth();
                dobField.setText(String.format("%02d/%02d/%04d", dob.getMonthNumber(), dob.getDayOfMonth(), dob.getYear()));
            } else {
                dobField.setText("");
            }

            if (person instanceof RegisteredPerson rp) {
                personTypeComboBox.setSelectedItem("RegisteredPerson");
                governmentIdField.setText(rp.getGovernmentID());
                if (person instanceof OCCCPerson op) {
                    personTypeComboBox.setSelectedItem("OCCCPerson");
                    studentIdField.setText(op.getStudentID());
                }
            } else {
                personTypeComboBox.setSelectedItem("Person");
            }
        } else {
            personTypeComboBox.setSelectedIndex(0);
        }
        updateFieldVisibility(); // Ensure correct fields shown based on loaded/default type
    }

    private boolean validateInput() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String dobStr = dobField.getText().trim();

        if (firstName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First name is required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            firstNameField.requestFocus();
            return false;
        }
        if (lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Last name is required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            lastNameField.requestFocus();
            return false;
        }
        if (dobStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Date of birth is required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            dobField.requestFocus();
            return false;
        }

        String[] parts = dobStr.split("/");
        if (parts.length != 3) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use MM/DD/YYYY.", "Input Error", JOptionPane.ERROR_MESSAGE);
            dobField.setText("");
            dobField.requestFocus();
            return false;
        }
        try {
            Integer.parseInt(parts[0]);
            Integer.parseInt(parts[1]);
            Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid date components. Month, day, and year must be numbers (MM/DD/YYYY).", "Input Error", JOptionPane.ERROR_MESSAGE);
            dobField.setText("");
            dobField.requestFocus();
            return false;
        }

        String type = (String) personTypeComboBox.getSelectedItem();
        if ("RegisteredPerson".equals(type) || "OCCCPerson".equals(type)) {
            if (governmentIdField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Government ID is required for Registered and OCCCPerson.", "Input Error", JOptionPane.ERROR_MESSAGE);
                governmentIdField.requestFocus();
                return false;
            }
        }
        if ("OCCCPerson".equals(type)) {
            if (studentIdField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Student ID is required for OCCCPerson.", "Input Error", JOptionPane.ERROR_MESSAGE);
                studentIdField.requestFocus();
                return false;
            }
        }
        return true;
    }

    private boolean createOrUpdatePerson() {
        OCCCDate dob;
        try {
            String[] parts = dobField.getText().trim().split("/");
            int month = Integer.parseInt(parts[0]);
            int day = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);
            dob = new OCCCDate(day, month, year);

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use numeric MM/DD/YYYY.", "Input Error", JOptionPane.ERROR_MESSAGE);
            dobField.setText("");
            dobField.requestFocus();
            return false;
        } catch (OCCCDateException ode) {
            JOptionPane.showMessageDialog(this, "Invalid Date! " + ode.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            dobField.setText("");
            dobField.requestFocus();
            return false;
        }

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String governmentID = governmentIdField.isVisible() ? governmentIdField.getText().trim() : (isEditing && person instanceof RegisteredPerson ? ((RegisteredPerson)person).getGovernmentID() : "");
        String studentID = studentIdField.isVisible() ? studentIdField.getText().trim() : (isEditing && person instanceof OCCCPerson ? ((OCCCPerson)person).getStudentID() : "");
        String type = (String) personTypeComboBox.getSelectedItem();

        // For editing, it's often better to create a new object if the type could change,
        // or if immutability is desired. Here, we modify 'this.person' if it's an edit
        // or create a new one. The spec ("create a new Person (or subtype) from an existing Person or subtype")
        // for "edit" might imply creating a new object based on the old one, especially if type changes.
        // The current PersonDialog doesn't allow changing type during edit, so modifying existing fields is okay.

        if (isEditing && this.person != null) {
            // Update existing person instance
            this.person.firstName = firstName;
            this.person.lastName = lastName;
            this.person.setDateOfBirth(dob);

            if (this.person instanceof RegisteredPerson rp) {
                // rp.governmentID = governmentID; // Direct access if OCCCPerson/RegisteredPerson fields are protected
                // If there were public setters:
                // rp.setGovernmentID(governmentID); 
                // For now, since fields are protected and we are in the same package, direct access is possible.
                // However, if the object type from combo box was different (which it isn't for edit),
                // we would need to create a new object.
                // Let's assume direct modification is fine as type does not change.
                if (governmentIdField.isVisible()) { // only update if field is relevant
                    rp.governmentID = governmentID;
                }

                if (this.person instanceof OCCCPerson op) {
                    if (studentIdField.isVisible()) { // only update if field is relevant
                         op.studentID = studentID;
                    }
                }
            }
            // 'this.person' is the same object instance, but its fields are updated.
        } else {
            // Create new person instance
            switch (type) {
                case "Person":
                    this.person = new Person(firstName, lastName, dob);
                    break;
                case "RegisteredPerson":
                    this.person = new RegisteredPerson(firstName, lastName, dob, governmentID);
                    break;
                case "OCCCPerson":
                    this.person = new OCCCPerson(firstName, lastName, dob, governmentID, studentID);
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Invalid person type selected.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
            }
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