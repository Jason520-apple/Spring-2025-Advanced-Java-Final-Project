import javax.swing.*;
import java.awt.*;

/**
 * PersonListCellRenderer.java
 *
 * A custom Swing ListCellRenderer for displaying Person objects within a JList.
 * This renderer presents each Person's information in a styled card-like layout,
 * showing the person's type, full name, date of birth, and conditionally displaying
 * Government ID or Student ID based on the specific subclass of Person.
 *
 * It adapts its appearance based on selection status and uses theme colors
 * provided by the parent PersonManagerGUI instance.
 *
 * Demonstrates:
 * - Custom cell rendering in JList
 * - Use of multiple JLabels and JPanel for complex cell structure
 * - Dynamic visibility of components based on data
 * - Styling cells for selection and focus states
 * - Accessing theme properties from a parent component
 *
 * Author: Amida Fombutu
 * Course: CS2463 Advanced Java – Spring 2025
 */
public class PersonListCellRenderer extends JPanel implements ListCellRenderer<Person> {

    // --- UI Components for the cell ---
    private final JLabel nameLabel;
    private final JLabel dobLabel;
    private final JLabel govIdLabel;
    private final JLabel studentIdLabel;
    private final JPanel contentPanel; // Main panel for styling individual cells

    // --- Parent Reference ---
    private final PersonManagerGUI parentGUI; // Used to access shared theme colors

    // --- Styling Constants ---
    private static final Color SECONDARY_TEXT_COLOR = new Color(80, 80, 80);
    private static final Font NAME_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font DETAIL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    /**
     * Constructs the PersonListCellRenderer.
     * Initializes the UI components used to display person data within each cell.
     *
     * @param parentGUI A reference to the main {@link PersonManagerGUI} instance,
     * used here to access shared theme colors (e.g., cardColor, textColor).
     */
    public PersonListCellRenderer(PersonManagerGUI parentGUI) {
        this.parentGUI = parentGUI;

        setLayout(new BorderLayout()); // Main renderer panel layout
        setOpaque(false); // Renderer itself is transparent; contentPanel will handle background

        nameLabel = new JLabel();
        nameLabel.setFont(NAME_FONT);

        dobLabel = new JLabel();
        dobLabel.setFont(DETAIL_FONT);

        govIdLabel = new JLabel();
        govIdLabel.setFont(DETAIL_FONT);

        studentIdLabel = new JLabel();
        studentIdLabel.setFont(DETAIL_FONT);

        // Panel to hold the labels in a vertical stack
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        labelPanel.setOpaque(false); // Transparent, as contentPanel handles the background
        labelPanel.add(nameLabel);
        labelPanel.add(dobLabel);
        labelPanel.add(govIdLabel);
        labelPanel.add(studentIdLabel);

        // Content panel for each cell with border and padding
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true), // Rounded border
                BorderFactory.createEmptyBorder(10, 12, 10, 12)    // Padding
        ));
        contentPanel.setOpaque(true); // This panel will show the background color
        contentPanel.add(labelPanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER); // Add styled content panel to the renderer
    }

    /**
     * Configures and returns the component used to draw an item in the JList.
     * This method is called by JList for each item to be rendered.
     *
     * @param list The JList we're painting.
     * @param person The Person object to be rendered.
     * @param index The cell's index in the list.
     * @param isSelected True if the specified cell was selected.
     * @param cellHasFocus True if the specified cell has the focus.
     * @return A Component (this JPanel instance) whose paint() method will render the cell.
     */
    @Override
    public Component getListCellRendererComponent(
            JList<? extends Person> list,
            Person person,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        // Populate common fields
        String personType = person.getClass().getSimpleName();
        nameLabel.setText(personType + ": " + person.getFirstName() + " " + person.getLastName());
        nameLabel.setVisible(true);

        if (person.getDateOfBirth() != null) {
            dobLabel.setText("DOB: " + person.getDateOfBirth().toString());
        } else {
            dobLabel.setText("DOB: N/A");
        }
        dobLabel.setVisible(true);

        // Reset and hide optional fields by default
        govIdLabel.setText("");
        govIdLabel.setVisible(false);
        studentIdLabel.setText("");
        studentIdLabel.setVisible(false);

        // Populate and show fields specific to RegisteredPerson or OCCCPerson
        if (person instanceof RegisteredPerson rp) {
            govIdLabel.setText("Government ID: " + rp.getGovernmentID());
            govIdLabel.setVisible(true);
        }

        if (person instanceof OCCCPerson op) {
            // If it's an OCCCPerson, it's also a RegisteredPerson, so govIdLabel might already be set.
            // This will specifically add/override the student ID.
            studentIdLabel.setText("Student ID: " + op.getStudentID());
            studentIdLabel.setVisible(true);
        }

        // Ensure the cell content panel tries to fill the width of the list.
        // This helps in making the cards look consistent, especially with variable height.
        contentPanel.setPreferredSize(new Dimension(list.getWidth() - (list.getInsets().left + list.getInsets().right), getPreferredSize().height));


        // Apply styling based on selection state
        if (isSelected) {
            contentPanel.setBackground(list.getSelectionBackground());
            nameLabel.setForeground(list.getSelectionForeground());
            dobLabel.setForeground(list.getSelectionForeground());
            govIdLabel.setForeground(list.getSelectionForeground());
            studentIdLabel.setForeground(list.getSelectionForeground());
        } else {
            contentPanel.setBackground(parentGUI.cardColor); // Use themed card color
            nameLabel.setForeground(parentGUI.textColor);   // Use themed text color
            dobLabel.setForeground(SECONDARY_TEXT_COLOR);   // Specific color for secondary details
            govIdLabel.setForeground(SECONDARY_TEXT_COLOR);
            studentIdLabel.setForeground(SECONDARY_TEXT_COLOR);
        }
        
        // The JList needs this panel (the renderer itself) to determine the cell bounds.
        // For variable height cells, ensure JList.fixedCellHeight is -1.
        // The actual height will be determined by this panel's preferred size after layout.
        return this;
    }
}