

import javax.swing.*;
import java.awt.*;

/**
 * Custom cell renderer that displays Person data in a styled card layout.
 * Shows name, DOB, and optional government/student IDs on separate lines.
 */
public class PersonListCellRenderer extends JPanel implements ListCellRenderer<Person> {

    private final JLabel nameLabel;
    private final JLabel dobLabel;
    private final JLabel govIdLabel;
    private final JLabel studentIdLabel;
    private final JPanel contentPanel;
    private final PersonManagerGUI parent;

    // Consistent secondary text color for non-primary labels
    private static final Color SECONDARY_TEXT_COLOR = new Color(80, 80, 80);

    /**
     * Constructs the cell renderer with visual components.
     *
     * @param parent Reference to the main GUI for shared color styling.
     */
    public PersonListCellRenderer(PersonManagerGUI parent) {
        this.parent = parent;

        setLayout(new BorderLayout());
        setOpaque(false);

        // Create labels
        nameLabel = new JLabel();
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        dobLabel = new JLabel();
        dobLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        govIdLabel = new JLabel();
        govIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        studentIdLabel = new JLabel();
        studentIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Stack labels vertically
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        labelPanel.setOpaque(false);

        labelPanel.add(nameLabel);
        labelPanel.add(dobLabel);
        labelPanel.add(govIdLabel);
        labelPanel.add(studentIdLabel);

        // Container with padding and rounded border
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        contentPanel.setOpaque(true);
        contentPanel.add(labelPanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends Person> list,
            Person person,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        // Set base fields
        String type = person.getClass().getSimpleName();
        nameLabel.setText(type + ": " + person.getFirstName() + " " + person.getLastName());
        nameLabel.setVisible(true);

        // Null-safe DOB display
        if (person.getDateOfBirth() != null) {
            dobLabel.setText("DOB: " + person.getDateOfBirth().toString());
        } else {
            dobLabel.setText("DOB: N/A");
        }
        dobLabel.setVisible(true);

        // Clear optional fields first
        govIdLabel.setText("");
        govIdLabel.setVisible(false);
        studentIdLabel.setText("");
        studentIdLabel.setVisible(false);

        // Set and show optional fields if present
        if (person instanceof RegisteredPerson rp) {
            govIdLabel.setText("Government ID: " + rp.getGovernmentID());
            govIdLabel.setVisible(true);
        }

        if (person instanceof OCCCPerson op) {
            studentIdLabel.setText("Student ID: " + op.getStudentID());
            studentIdLabel.setVisible(true);
        }

        // Ensure consistent width
        contentPanel.setMaximumSize(new Dimension(list.getWidth(), Integer.MAX_VALUE));
        setPreferredSize(null); // allow dynamic layout

        // Selection-based styling
        if (isSelected) {
            contentPanel.setBackground(list.getSelectionBackground());
            nameLabel.setForeground(list.getSelectionForeground());
            dobLabel.setForeground(list.getSelectionForeground());
            govIdLabel.setForeground(list.getSelectionForeground());
            studentIdLabel.setForeground(list.getSelectionForeground());
        } else {
            contentPanel.setBackground(parent.cardColor);
            nameLabel.setForeground(parent.textColor);
            dobLabel.setForeground(new Color(60, 60, 60));
            govIdLabel.setForeground(SECONDARY_TEXT_COLOR);
            studentIdLabel.setForeground(SECONDARY_TEXT_COLOR);
        }

        return this;
    }
}
