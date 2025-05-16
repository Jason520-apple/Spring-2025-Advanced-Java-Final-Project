// // OCCC Spring 2025
// // Advanced Java
// // Unit 8 Project - Person GUI

// import java.awt.BorderLayout;
// import java.awt.Font;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
// import java.awt.event.KeyEvent;
// import java.awt.event.WindowAdapter;
// import java.awt.event.WindowEvent;
// import java.io.File;
// import java.io.FileInputStream;
// import java.io.FileNotFoundException;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.io.ObjectInputStream;
// import java.io.ObjectOutputStream;
// import java.util.ArrayList;
// import javax.swing.Box;
// import javax.swing.BoxLayout;
// import javax.swing.JButton;
// import javax.swing.JComboBox;
// import javax.swing.JFileChooser;
// import javax.swing.JFrame;
// import javax.swing.JLabel;
// import javax.swing.JMenu;
// import javax.swing.JMenuBar;
// import javax.swing.JMenuItem;
// import javax.swing.JOptionPane;
// import javax.swing.JPanel;
// import java.lang.ClassCastException;


// public class PersonGUI extends JFrame implements ActionListener {

//     // array list to hold Person objects that get created, edited, and deleted
//     // acts as container of objects, will be used to export and import objects to
//     // and from files
//     // Amida Fombutu: Made pList non-static as it's instance-specific for the GUI
//     private ArrayList<Person> pList = new ArrayList<Person>();

//     // String[] objects; // Amida
    
//     // Amida Fombutu: Replaced lastModified logic with a boolean dataChanged flag
//     private boolean dataChanged = false;

//     // --- Amida Fombutu: Removed static Long originalLastModified, currentLastModified; ---

   


//     // A JMenuBar in Java Swing provides a menu bar for a window, typically a
//     // JFrame. It houses JMenu objects, which, when selected, display JMenuItem
//     // options

//     JMenuBar menuBar;
//     JMenu fileMenu;
//     JMenu helpMenu;

//     JMenuItem newItem;
//     JMenuItem openItem; // JFileChooser dialog
//     JMenuItem saveItem;
//     JMenuItem saveAsItem; // JFileChooser dialog
//     JMenuItem exitItem;

//                         // Keeping the helpDialog.

//     JFileChooser fileChooser; // for open and save, will create dialogs
//     File currentFile; // use with JFileChooser, will allow the user to load and save files of the objects

//     // Amida Fombutu: Initialized objectIndex to -1 to indicate no selection initially.
//     int objectIndex = -1; // what index of the ArrayList should be accessed by the mutator methods (edit, delete)

//     // buttons to view, edit, delete
//     JButton createButton, editButton, deleteButton;

//     // drop-down list
//     // Amida Fombutu: Parameterized JComboBox for type safety, though it will use toString()
//     JComboBox<Person> viewObjectsMenu;

//     // JTextField firstName, lastName, govID, studentID;

//     // fonts
//     Font appFontLarge = new Font("Arial", Font.BOLD, 30);
//     Font appFontSmall = new Font("Arial", Font.PLAIN, 18);

//     /*
//      * File menu with New, Open..., Save, Save As... , and Exit.
//      * * The Open... and Save As... should make use of a JFileChooser dialog.
//      * * Help menu with at least one "help" option.
//      * * The application should use a drop-down list to allow the user to select a
//      * Person to view / edit / delete / whatever.
//      * * Note the "serialization" demo, allows you to save and retrieve containers of
//      * objects, and which is provided for that use here.
//      * * The user should be able to use the GUI to load, create, modify, delete, and
//      * save objects of type Person, RegisteredPerson, and OCCCPerson.
//      */

//     public static void main(String[] args) {
//         PersonGUI p = new PersonGUI(); // run the GUI program
//     }

//     public PersonGUI() {

//         setTitle("Person GUI");
//         setSize(800, 800);
//         setLocationRelativeTo(null);
//         setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS)); // evenly space and allow the panels to stack vertically

//         // from ppt
//         setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

//         addWindowListener(new WindowAdapter() {
//             @Override
//             public void windowClosing(WindowEvent e) {
//                 exitMenu();
//             }
//         });

//         menuBar = new JMenuBar();

//         // center label - create a drop down list that will act as a container of Person objects
//         // from ppt 4

//         fileMenu = new JMenu("File");
//         fileMenu.setMnemonic(KeyEvent.VK_F); // hot key or shortcut

//         // when clicked on, the menu will display the options new, open, save, save as, and exit
//         newItem = new JMenuItem("New");
//         newItem.setMnemonic(KeyEvent.VK_N);

//         openItem = new JMenuItem("Open..."); // Amida Fombutu: Added "..." as per spec
//         openItem.setMnemonic(KeyEvent.VK_O);

//         saveItem = new JMenuItem("Save");
//         saveItem.setMnemonic(KeyEvent.VK_V);

//         saveAsItem = new JMenuItem("Save as..."); // Amida Fombutu: Added "..." as per spec
//         // Amida Fombutu: Added mnemonic for Save As for consistency
//         saveAsItem.setMnemonic(KeyEvent.VK_A);


//         exitItem = new JMenuItem("Exit");
//         exitItem.setMnemonic(KeyEvent.VK_X);

//         // adding to the menuBar on the top of the screen
//         fileMenu.add(newItem);
//         fileMenu.add(openItem);
//         fileMenu.add(saveItem);
//         fileMenu.add(saveAsItem);
//         fileMenu.addSeparator();
//         fileMenu.add(exitItem);

//         menuBar.add(fileMenu);
        

//         // now for the helpMenu
//         helpMenu = new JMenu("Help");
//         helpMenu.setMnemonic(KeyEvent.VK_H);
//         // Amida Fombutu: For a more detailed help, a JTextArea in JOptionPane is better.
//         JMenuItem helpDialog = new JMenuItem("Help Topics"); // Amida Fombutu: Changed text to be more generic for an actual help dialog.
//         helpDialog.addActionListener(e -> showHelpDialog()); // Amida Fombutu: Added action listener for help.
//         helpMenu.add(helpDialog);
        
//         // Amida Fombutu: Adding Help menu to menubar here, before createHorizontalGlue if used for right-alignment of subsequent menus
//         menuBar.add(helpMenu); 
//         // menuBar.add(Box.createHorizontalGlue()); // Amida Fombutu: This pushes subsequent menus to the right.
//         setJMenuBar(menuBar); // Amida Fombutu: Set JMenuBar earlier, before adding components to content pane.


//         // title of the program
//         JPanel titlePanel = new JPanel();
//         JLabel titleLabel = new JLabel("Welcome to the PersonGUI!");
//         titleLabel.setFont(appFontLarge);
//         // titleLabel.setLayout(getLayout()); // Amida Fombutu: JLabel doesn't typically have its layout set like this. Its parent panel handles it.
//         titlePanel.add(titleLabel); // Amida Fombutu: BorderLayout not needed if panel uses FlowLayout by default.

//         JPanel descriptionPanel = new JPanel();
//         // Amida Fombutu: Using HTML for basic text wrapping in JLabel.
//         JLabel descriptionLabel = new JLabel(
//                 "<html><body style='width: 550px; text-align: center;'>" +
//                 "This program will allow the user to load, create, modify, delete, and " +
//                 "save objects of type Person, RegisteredPerson, and OCCCPerson." +
//                 "<br>Please view and select your Person object below:</body></html>");
//         descriptionLabel.setFont(appFontSmall);
//         descriptionPanel.add(descriptionLabel);

//         // add to our JFrame
//         add(titlePanel); 
//         add(descriptionPanel); // description goes under the title

//         // in the center of the screen, there will be a JComboBox that will act as a drop-down list
//         JPanel listPanel = new JPanel();
//         viewObjectsMenu = new JComboBox<>(); 
//         listPanel.add(new JLabel("Select Person:")); // Amida Fombutu: Added label for JComboBox
//         listPanel.add(viewObjectsMenu);
//         add(listPanel);

//         // Amida Fombutu: Removed this as JComboBox will be populated by refreshPersonDisplay()
//         // objects = new String[pList.size()]; 
//         // pList.toArray(objects);


//         JPanel buttonPanel = new JPanel();
//         createButton = new JButton("Create Person");
//         editButton = new JButton("Edit Selected"); // Amida Fombutu: Made text more descriptive
//         deleteButton = new JButton("Delete Selected"); // Amida Fombutu: Made text more descriptive

//         buttonPanel.add(createButton);
//         buttonPanel.add(editButton);
//         buttonPanel.add(deleteButton);
//         add(buttonPanel);

//         // add ActionListeners
//         newItem.addActionListener(this);
//         openItem.addActionListener(this);
//         saveItem.addActionListener(this);
//         saveAsItem.addActionListener(this);
//         exitItem.addActionListener(this);

//         viewObjectsMenu.addActionListener(this);

//         createButton.addActionListener(this);
//         editButton.addActionListener(this);
//         deleteButton.addActionListener(this);

//         // Amida Fombutu: Call to update menu and button states initially
//         updateGUIStates();
//         setVisible(true); // Amida Fombutu: Moved setVisible to the end of constructor.
//     }

//     // Amida Fombutu: Added method to display a simple help dialog as per specifications
//     private void showHelpDialog() {
//         String helpMessage = "Person GUI Application - Help\n\n" +
//                              "File Menu:\n" +
//                              "  New: Clears the current list of persons. Prompts to save if data has changed.\n" +
//                              "  Open...: Loads a list of persons from a file.\n" +
//                              "  Save: Saves the current list to the current file.\n" +
//                              "  Save As...: Saves the current list to a new file.\n" +
//                              "  Exit: Exits the application. Prompts to save if data has changed.\n\n" +
//                              "Controls:\n" +
//                              "  Create Person: Add a new Person, RegisteredPerson, or OCCCPerson.\n" +
//                              "  Edit Selected: Modify the person selected in the dropdown list.\n" +
//                              "  Delete Selected: Remove the selected person from the list.\n\n" +
//                              "Date Input: Use format DD MM YYYY (e.g., 25 12 2023).\n" +
//                              "Invalid dates will result in an error and a re-prompt.";
//         JOptionPane.showMessageDialog(this, helpMessage, "Help - Person GUI", JOptionPane.INFORMATION_MESSAGE);
//     }


//     // Amida Fombutu: Renamed and refactored exitMenu to handle dataChanged flag
//     public void exitMenu() {
//         if (dataChanged) {
//             String[] options = {"Save and Exit", "Don't Save and Exit", "Cancel"};
//             int choice = JOptionPane.showOptionDialog(this,
//                     "You have unsaved changes. Save before exiting?",
//                     "Unsaved Changes",
//                     JOptionPane.YES_NO_CANCEL_OPTION, // Amida Fombutu: Changed to YES_NO_CANCEL
//                     JOptionPane.WARNING_MESSAGE,    // Amida Fombutu: Changed to WARNING_MESSAGE
//                     null,
//                     options,
//                     options[0]);

//             switch (choice) {
//                 case JOptionPane.YES_OPTION: // Save and Exit
//                     // Amida Fombutu: Calling a boolean save handler to ensure save was successful before exiting.
//                     if (handleSave()) { // Try to save
//                         System.exit(0);
//                     }
//                     // If save failed or was cancelled, do not exit.
//                     break;
//                 case JOptionPane.NO_OPTION: // Don't Save and Exit
//                     System.exit(0);
//                     break;
//                 case JOptionPane.CANCEL_OPTION: // Cancel
//                 default: // Also for dialog closed
//                     // Do nothing, return to application
//                     break;
//             }
//         } else {
//             System.exit(0); // No changes, exit directly
//         }
//     }


//     @Override
//     public void actionPerformed(ActionEvent e) {
//         Object source = e.getSource(); // Amida Fombutu: Cache the source for cleaner if-else if

//         if (source == newItem) {
//             handleNewFile(); 
//         } else if (source == openItem) {
//             handleOpenFile(); 
//         } else if (source == saveItem) {
//             handleSave(); 
//         } else if (source == saveAsItem) {
//             handleSaveAsFile(); 
//         } else if (source == exitItem) {
//             exitMenu();
//         } else if (source == viewObjectsMenu) {
//             // Amida Fombutu: Update objectIndex and GUI states when selection changes
//             if (viewObjectsMenu.getItemCount() > 0) { // Check if combobox is not empty
//                  objectIndex = viewObjectsMenu.getSelectedIndex();
//             } else {
//                 objectIndex = -1; // No items, so no selection
//             }
//             updateGUIStates(); // Update button states based on selection
//         } else if (source == createButton) {
//             handleCreateNewPerson(); 
//             handleEditPerson(); 
//         } else if (source == deleteButton) {
//             handleDeletePerson(); 
//         }
//     }

//     // Amida Fombutu: Added helper to refresh person list display in JComboBox
//     private void refreshPersonDisplay() {
//         Person selectedPerson = null;
//         if (objectIndex != -1 && objectIndex < viewObjectsMenu.getItemCount()) {
           
//              if(objectIndex < pList.size()){
//                 selectedPerson = pList.get(objectIndex);
//              } else if (viewObjectsMenu.getItemCount() > 0) { // Fallback if pList is smaller
//                 selectedPerson = viewObjectsMenu.getItemAt(objectIndex);
//              }
//         }


//         viewObjectsMenu.removeAllItems();
//         for (Person p : pList) {
//             viewObjectsMenu.addItem(p); // JComboBox will use p.toString()
//         }

//         if (selectedPerson != null && pList.contains(selectedPerson)) {
//             viewObjectsMenu.setSelectedItem(selectedPerson);
//             objectIndex = pList.indexOf(selectedPerson);
//         } else if (!pList.isEmpty()) {
//             viewObjectsMenu.setSelectedIndex(0);
//             objectIndex = 0;
//         } else {
//             objectIndex = -1; // No selection
//         }
//         updateGUIStates(); // Update states after refresh
//     }

//     // Amida Fombutu: Method to update enabled state of menu items and buttons
//     private void updateGUIStates() {
//         boolean hasData = !pList.isEmpty();
//         boolean selectionExists = objectIndex != -1 && hasData;

//         // File menu items
//         // Amida Fombutu: Logic from promptToSaveChanges incorporated here for 'New' and 'Open'
//         saveItem.setEnabled(currentFile != null && hasData && dataChanged);
//         saveAsItem.setEnabled(hasData);

//         // Action buttons
//         editButton.setEnabled(selectionExists);
//         deleteButton.setEnabled(selectionExists);
        
//         // Amida Fombutu: Update window title if needed (e.g., to show '*' for unsaved changes)
//         String title = "Person GUI";
//         if (currentFile != null) {
//             title += " - " + currentFile.getName();
//         }
//         if (dataChanged) {
//             title += "*";
//         }
//         setTitle(title);
//     }
    
//     // Amida Fombutu: Added method to disable/enable controls during modal operations (like create/edit)
//     private void setOperationInProgress(boolean inProgress) {
//         boolean enableControls = !inProgress;
//         newItem.setEnabled(enableControls);
//         openItem.setEnabled(enableControls);
//         // Save and Save As are handled by updateGUIStates, but also consider inProgress
//         if (inProgress) {
//             saveItem.setEnabled(false);
//             saveAsItem.setEnabled(false);
//         } else {
//             updateGUIStates(); // Re-evaluate save/saveAs based on current state
//         }
//         exitItem.setEnabled(enableControls); // Usually exit should always be available or handled by its own logic

//         createButton.setEnabled(enableControls);
//         editButton.setEnabled(enableControls && objectIndex != -1 && !pList.isEmpty());
//         deleteButton.setEnabled(enableControls && objectIndex != -1 && !pList.isEmpty());
//         viewObjectsMenu.setEnabled(enableControls);
        
//         // Disable other menus if necessary
//         // helpMenu.setEnabled(enableControls); // Help can usually stay enabled
//     }


//     public void handleNewFile() {
//         if (dataChanged) {
//             // Amida Fombutu: Simplified prompt logic
//             int response = JOptionPane.showConfirmDialog(this,
//                     "You have unsaved changes. Save before creating a new file?",
//                     "Unsaved Changes",
//                     JOptionPane.YES_NO_CANCEL_OPTION,
//                     JOptionPane.WARNING_MESSAGE);
//             if (response == JOptionPane.YES_OPTION) {
//                 if (!handleSave()) { // Try to save; if save is cancelled or fails, don't proceed with New.
//                     return;
//                 }
//             } else if (response == JOptionPane.CANCEL_OPTION || response == JOptionPane.CLOSED_OPTION) {
//                 return; // User cancelled New operation
//             }
//             // If NO_OPTION, proceed without saving.
//         }

//         pList.clear();
//         currentFile = null; // Amida Fombutu: Clear current file reference
//         dataChanged = false;
//         refreshPersonDisplay(); // Updates JComboBox
//         updateGUIStates();      // Updates menu/button states and title
//     }

//     // Amida Fombutu: Renamed and refactored openFile
//     @SuppressWarnings("unchecked") // For the cast to ArrayList<Person>
//     public void handleOpenFile() {
//         if (dataChanged) {
//              int response = JOptionPane.showConfirmDialog(this,
//                     "You have unsaved changes. Save before opening a new file?",
//                     "Unsaved Changes",
//                     JOptionPane.YES_NO_CANCEL_OPTION,
//                     JOptionPane.WARNING_MESSAGE);
//             if (response == JOptionPane.YES_OPTION) {
//                 if (!handleSave()) { 
//                     return; 
//                 }
//             } else if (response == JOptionPane.CANCEL_OPTION || response == JOptionPane.CLOSED_OPTION) {
//                 return; 
//             }
//         }

//         if (fileChooser == null) { // Amida Fombutu: Initialize file chooser if null
//              fileChooser = new JFileChooser();
//         }
//         // Amida Fombutu: Suggest current directory or user's home
//         if (currentFile != null) {
//             fileChooser.setCurrentDirectory(currentFile.getParentFile());
//         } else {
//             fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
//         }

//         int response = fileChooser.showOpenDialog(this); 

//         if (response == JFileChooser.APPROVE_OPTION) {
//             File selectedFile = fileChooser.getSelectedFile(); 
//             try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(selectedFile))) {
//                 Object data = objectInputStream.readObject();
//                 if (data instanceof ArrayList) { // Amida Fombutu: Check type before casting
//                     pList.clear();
//                     pList.addAll((ArrayList<Person>) data); // Cast to ArrayList<Person>
//                     currentFile = selectedFile;
//                     dataChanged = false;
//                     refreshPersonDisplay();
//                 } else {
//                     JOptionPane.showMessageDialog(this, "File content is not a valid Person list.", "Load Error", JOptionPane.ERROR_MESSAGE);
//                     // Amida Fombutu: Optionally clear pList or leave as is
//                     // pList.clear(); 
//                     // currentFile = null;
//                 }
//             } catch (FileNotFoundException fnf) { // Amida Fombutu: More specific catch
//                 JOptionPane.showMessageDialog(this, "Error: File not found. " + fnf.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
//             } catch (IOException ioe) {
//                 JOptionPane.showMessageDialog(this, "Error reading file: " + ioe.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
//             } catch (ClassNotFoundException | ClassCastException cnfe) { // Amida Fombutu: Catch ClassCastException too
//                 JOptionPane.showMessageDialog(this, "Error: File contains invalid data. " + cnfe.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
//             } finally {
//                 updateGUIStates();
//             }
//         }
//     }

//     // Amida Fombutu: Renamed and refactored saveFile, returns boolean for success
//     public boolean handleSave() {
//         if (currentFile == null) {
//             return handleSaveAsFile(); // If no current file, invoke Save As
//         }
//         if (pList.isEmpty() && !dataChanged) { // Amida Fombutu: Don't save if empty and no intent (e.g. new empty file just saved)
//             // return true; // Considered "successful" as there's nothing to do.
//         }
//          if (!dataChanged && currentFile != null) { // Amida Fombutu: If no changes, no need to save.
//             return true;
//         }


//         try (ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(currentFile))) {
//             objOut.writeObject(pList); // Amida Fombutu: Write the whole ArrayList
//             dataChanged = false;
//             updateGUIStates();
//             return true; // Save successful
//         } catch (IOException e1) {
//             JOptionPane.showMessageDialog(this, "Error saving file: " + e1.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
//             return false; // Save failed
//         }
//     }

//     public boolean handleSaveAsFile() {
//         if (pList.isEmpty()) {
//             JOptionPane.showMessageDialog(this,
//                     "There are no Person objects to save.", "Save As File",
//                     JOptionPane.INFORMATION_MESSAGE);
//             return false; // Nothing to save, or user might not want to save an empty file.
//         }

//         if (fileChooser == null) { // Amida Fombutu: Initialize file chooser
//             fileChooser = new JFileChooser();
//         }
//         // Amida Fombutu: Suggest current directory or filename
//         if (currentFile != null) {
//             fileChooser.setSelectedFile(currentFile);
//         } else {
//             fileChooser.setSelectedFile(new File("persons.dat")); // Default filename
//         }
        
//         int response = fileChooser.showSaveDialog(this); 

//         if (response == JFileChooser.APPROVE_OPTION) {
//             File selectedFile = fileChooser.getSelectedFile();
//             // Amida Fombutu: Add .dat extension if not present
//             String filePath = selectedFile.getAbsolutePath();
//             if (!filePath.toLowerCase().endsWith(".dat")) {
//                 selectedFile = new File(filePath + ".dat");
//             }

//             // Amida Fombutu: Confirm overwrite
//             if (selectedFile.exists()) {
//                 int overwriteResponse = JOptionPane.showConfirmDialog(this,
//                         "File '" + selectedFile.getName() + "' already exists.\nDo you want to overwrite it?",
//                         "Confirm Overwrite", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
//                 if (overwriteResponse == JOptionPane.NO_OPTION) {
//                     return false; // User chose not to overwrite
//                 }
//             }
            
//             currentFile = selectedFile; // Set the new file as the current file
//             return handleSave(); // Call the regular save method to perform the save operation
//                                  // handleSave will set dataChanged = false and updateGUIStates
//         }
//         return false; // User cancelled Save As dialog
//     }

//     public void handleCreateNewPerson() {
//         setOperationInProgress(true); // Amida Fombutu: Disable controls
//         try {
//             // Amida Fombutu: Added explicit person type selection
//             String[] personTypes = {"Person", "RegisteredPerson", "OCCCPerson"};
//             String selectedType = (String) JOptionPane.showInputDialog(this,
//                     "Select Person Type:", "Create New Person",
//                     JOptionPane.QUESTION_MESSAGE, null,
//                     personTypes, personTypes[0]);

//             if (selectedType == null) { // User cancelled type selection
//                 return;
//             }

//             String fnInput = JOptionPane.showInputDialog(this, "Enter the first name: ");
//             if (fnInput == null || fnInput.trim().isEmpty()) {  /* Amida Fombutu: Basic validation */ return; }

//             String lnInput = JOptionPane.showInputDialog(this, "Enter the last name: ");
//             if (lnInput == null || lnInput.trim().isEmpty()) { /* Amida Fombutu: Basic validation */ return; }

//             OCCCDate dob = null;
//             boolean isValidDate = false;
//             while (!isValidDate) {
//                 String dobInputStr = JOptionPane.showInputDialog(this, "Enter Date of Birth (DD MM YYYY):");
//                 if (dobInputStr == null) { /* Amida Fombutu: User cancelled DOB input */ return; }

//                 String[] dateParts = dobInputStr.trim().split("\\s+"); // Split by one or more spaces
//                 if (dateParts.length == 3) {
//                     try {
//                         int day = Integer.parseInt(dateParts[0]);
//                         int month = Integer.parseInt(dateParts[1]);
//                         int year = Integer.parseInt(dateParts[2]);
//                         dob = new OCCCDate(day, month, year); // This constructor should throw InvalidOCCCDateException
//                         isValidDate = true;
//                     } catch (NumberFormatException nfe) {
//                         JOptionPane.showMessageDialog(this, "Invalid number format for date parts. Use DD MM YYYY.", "Date Input Error", JOptionPane.ERROR_MESSAGE);
//                     } catch (InvalidOCCCDateException idoEx) {
//                         JOptionPane.showMessageDialog(this, "Invalid Date: " + idoEx.getMessage() + "\nPlease re-enter (DD MM YYYY).", "Date Input Error", JOptionPane.ERROR_MESSAGE);
//                         // Loop continues, date field will be re-asked
//                     }
//                 } else {
//                     JOptionPane.showMessageDialog(this, "Invalid date format. Please use DD MM YYYY.", "Date Input Error", JOptionPane.ERROR_MESSAGE);
//                 }
//             }

//             String govInput = null;
//             if (selectedType.equals("RegisteredPerson") || selectedType.equals("OCCCPerson")) {
//                 govInput = JOptionPane.showInputDialog(this, "Enter Government ID:");
//                 if (govInput == null) { /* Amida Fombutu: User cancelled gov ID */ return; }
//                  // Amida Fombutu: Add check for empty if required by spec, though RegisteredPerson constructor doesn't prevent empty.
//                 if (govInput.trim().isEmpty() && selectedType.equals("RegisteredPerson")) {
//                 }
//             }

//             String studentInput = null;
//             if (selectedType.equals("OCCCPerson")) {
//                 studentInput = JOptionPane.showInputDialog(this, "Enter Student ID:");
//                 if (studentInput == null) { /* Amida Fombutu: User cancelled student ID */ return; }
//                  // Amida Fombutu: Add check for empty if required
//                 if (studentInput.trim().isEmpty()) {
//                 }
//             }

//             // Create person object based on the input
//             Person newPerson = null;
//             switch (selectedType) {
//                 case "Person":
//                     newPerson = new Person(fnInput.trim(), lnInput.trim(), dob);
//                     break;
//                 case "RegisteredPerson":
//                     newPerson = new RegisteredPerson(fnInput.trim(), lnInput.trim(), dob, govInput.trim());
//                     break;
//                 case "OCCCPerson":
                    
//                     newPerson = new OCCCPerson(new RegisteredPerson(fnInput.trim(), lnInput.trim(), dob, govInput.trim()), studentInput.trim());

//                     break;
//             }
            
//             if (newPerson != null) {
//                 pList.add(newPerson);
//                 dataChanged = true;
//                 objectIndex = pList.size() -1; // Amida Fombutu: Select the newly added person
//                 refreshPersonDisplay();
//             }

//         } finally {
//             setOperationInProgress(false); // Amida Fombutu: Re-enable controls
//             updateGUIStates(); // Ensure states are correct after operation
//         }
//     }

//     // Amida Fombutu: Renamed deletePerson for clarity
//     public void handleDeletePerson() {
//         if (objectIndex < 0 || objectIndex >= pList.size()) {
//             JOptionPane.showMessageDialog(this, "Please select a person to delete.", "Delete Person", JOptionPane.INFORMATION_MESSAGE);
//             return;
//         }
        
//         Person personToDelete = pList.get(objectIndex);
//         int confirm = JOptionPane.showConfirmDialog(this,
//                 "Are you sure you want to delete " + personToDelete.getFirstName() + " " + personToDelete.getLastName() + "?",
//                 "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

//         if (confirm == JOptionPane.YES_OPTION) {
//             pList.remove(objectIndex);
//             dataChanged = true;
//             // Amida Fombutu: Adjust objectIndex if the last item was deleted or list becomes empty
//             if (objectIndex >= pList.size() && !pList.isEmpty()) {
//                 objectIndex = pList.size() - 1;
//             } else if (pList.isEmpty()) {
//                 objectIndex = -1;
//             }
//             refreshPersonDisplay();
//         }
//         updateGUIStates();
//     }

//     public void handleEditPerson() {
//         if (objectIndex < 0 || objectIndex >= pList.size()) {
//             JOptionPane.showMessageDialog(this, "Please select a person to edit.", "Edit Person", JOptionPane.INFORMATION_MESSAGE);
//             return;
//         }
        
//         setOperationInProgress(true); // Amida Fombutu: Disable controls
//         try {
//             Person personToEdit = pList.get(objectIndex);
//             String personTypeDisplay;
//             if (personToEdit instanceof OCCCPerson) personTypeDisplay = "OCCCPerson";
//             else if (personToEdit instanceof RegisteredPerson) personTypeDisplay = "RegisteredPerson";
//             else personTypeDisplay = "Person";

//             // Amida Fombutu: Pre-fill with existing data. Cannot change person type during edit with this simple JOptionPane flow easily.
//             JOptionPane.showMessageDialog(this, "Editing " + personTypeDisplay + ". Type cannot be changed in this edit mode.", "Edit Person", JOptionPane.INFORMATION_MESSAGE);

//             String fnInput = JOptionPane.showInputDialog(this, "Enter the first name:", personToEdit.getFirstName());
//             if (fnInput == null) { /* User cancelled */ return; } // Amida Fombutu: Handle cancellation
//             if (fnInput.trim().isEmpty()) { /* Basic validation */ JOptionPane.showMessageDialog(this, "First name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE); return; }


//             String lnInput = JOptionPane.showInputDialog(this, "Enter the last name:", personToEdit.getLastName());
//             if (lnInput == null) { /* User cancelled */ return; }
//             if (lnInput.trim().isEmpty()) { /* Basic validation */ JOptionPane.showMessageDialog(this, "Last name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE); return; }


//             OCCCDate dob = personToEdit.getDOB(); // Keep existing DOB by default
//             boolean isValidDate = false; // Will become true if user enters new valid date, or skips (keeps old)
            
//             String currentDobStr = String.format("%02d %02d %04d", dob.getDayOfMonth(), dob.getMonthNumber(), dob.getYear());
//             String dobInputStr = JOptionPane.showInputDialog(this, "Enter Date of Birth (DD MM YYYY) or leave blank to keep [" + currentDobStr + "]:", currentDobStr);

//             if (dobInputStr == null) { /* User cancelled */ return; }
            
//             if (!dobInputStr.trim().isEmpty() && !dobInputStr.trim().equals(currentDobStr) ) { // Only parse if new date entered and it's different
//                 boolean rePromptDate = true;
//                 while(rePromptDate) {
//                     String[] dateParts = dobInputStr.trim().split("\\s+");
//                     if (dateParts.length == 3) {
//                         try {
//                             int day = Integer.parseInt(dateParts[0]);
//                             int month = Integer.parseInt(dateParts[1]);
//                             int year = Integer.parseInt(dateParts[2]);
//                             dob = new OCCCDate(day, month, year);
//                             isValidDate = true;
//                             rePromptDate = false; // Date is valid, exit loop
//                         } catch (NumberFormatException nfe) {
//                             dobInputStr = JOptionPane.showInputDialog(this, "Invalid number format for date parts. Use DD MM YYYY or cancel to keep old:", currentDobStr);
//                             if (dobInputStr == null || dobInputStr.trim().isEmpty() || dobInputStr.trim().equals(currentDobStr)) { rePromptDate = false; isValidDate = true; /* User chose to keep old or cancelled */ }

//                         } catch (InvalidOCCCDateException idoEx) {
//                              dobInputStr = JOptionPane.showInputDialog(this, "Invalid Date: " + idoEx.getMessage() + "\Re-enter (DD MM YYYY) or cancel to keep old:", currentDobStr);
//                             if (dobInputStr == null || dobInputStr.trim().isEmpty() || dobInputStr.trim().equals(currentDobStr)) { rePromptDate = false; isValidDate = true; /* User chose to keep old or cancelled */ }
//                         }
//                     } else {
//                         dobInputStr = JOptionPane.showInputDialog(this, "Invalid date format. Use DD MM YYYY or cancel to keep old:", currentDobStr);
//                         if (dobInputStr == null || dobInputStr.trim().isEmpty() || dobInputStr.trim().equals(currentDobStr)) { rePromptDate = false; isValidDate = true; /* User chose to keep old or cancelled */ }
//                     }
//                 }
//             } else {
//                  isValidDate = true; // Kept old date or entered same
//             }
//              if (!isValidDate) return; // Should not happen if loop logic is correct, but safety.


//             // Create the updated person object
//             Person updatedPerson = null;
//             if (personToEdit instanceof OCCCPerson) {
//                 OCCCPerson op = (OCCCPerson) personToEdit;
//                 String govInput = JOptionPane.showInputDialog(this, "Enter Government ID:", op.getGovernmentID());
//                 if (govInput == null) { return; } // User cancelled
//                 String studentInput = JOptionPane.showInputDialog(this, "Enter Student ID:", op.getStudentID());
//                 if (studentInput == null) { return; } // User cancelled
//                 // Amida Fombutu: Assuming OCCCPerson constructor takes RegisteredPerson then studentID
//                 updatedPerson = new OCCCPerson(new RegisteredPerson(fnInput.trim(), lnInput.trim(), dob, govInput.trim()), studentInput.trim());
//             } else if (personToEdit instanceof RegisteredPerson) {
//                 RegisteredPerson rp = (RegisteredPerson) personToEdit;
//                 String govInput = JOptionPane.showInputDialog(this, "Enter Government ID:", rp.getGovernmentID());
//                 if (govInput == null) { return; } // User cancelled
//                 updatedPerson = new RegisteredPerson(fnInput.trim(), lnInput.trim(), dob, govInput.trim());
//             } else { // Plain Person
//                 updatedPerson = new Person(fnInput.trim(), lnInput.trim(), dob);
//             }

//             if (updatedPerson != null) {
//                 pList.set(objectIndex, updatedPerson);
//                 dataChanged = true;
//                 refreshPersonDisplay(); // Will reselect and update GUI
//             }
//         } finally {
//             setOperationInProgress(false); // Amida Fombutu: Re-enable controls
//             updateGUIStates();
//         }
//     }
// }