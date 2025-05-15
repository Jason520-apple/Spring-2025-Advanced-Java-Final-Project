// Jason Vo
// OCCC Spring 2025
// Advanced Java
// Unit 5 Homework - Person Application

import java.awt.BorderLayout;
import java.awt.Font;
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
import java.util.Objects;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PersonGUI extends JFrame implements ActionListener {

	// global variables, what the program will need to implement
	// by declaring globally, it allows these variables to be easily manipulated and
	// accessed from other methods within the program

	// array list to hold Person objects that get created, edited, and deleted
	// acts as container of objects, will be used to export and import objects to
	// and from files
	static ArrayList<Person> pList = new ArrayList<Person>();

	String[] objects; // array used to display Person objects in the JComboBox, updated alongside
						// ArrayList

	// creating global variables for last modified so any portion of the GUI
	// is able to use them.
	static Long originalLastModified, currentLastModified;
	
	// A JMenuBar in Java Swing provides a menu bar for a window, typically a
	// JFrame. It houses JMenu objects, which, when selected, display JMenuItem
	// options

	JMenuBar menuBar;
	JMenu fileMenu;
	JMenu helpMenu;

	JMenuItem newItem;
	JMenuItem openItem; // JFileChooser dialog
	JMenuItem saveItem;
	JMenuItem saveAsItem; // JFileChooser dialog
	JMenuItem exitItem;

	JMenuItem helpItem; // display tip on using the program

	JFileChooser fileChooser; // for open and save, will create dialogs
	File currentFile; // use with JFileChooser, will allow the user to load and save files of the
						// objects

	int objectIndex; // what index of the ArrayList should be accessed by the mutator methods (edit,
						// delete), will be used to determine which Person object

	// buttons to view, edit, delete
	JButton createButton, viewButton, editButton, deleteButton;

	// drop-down list
	JComboBox viewObjectsMenu;

	// user inputs of a person object
	JTextField firstName, lastName, govID, studentID;

	// fonts
	Font appFontLarge = new Font("Arial", Font.BOLD, 30);
	Font appFontSmall = new Font("Arial", Font.PLAIN, 18);

	/*
	 * File menu with New, Open..., Save, Save As... , and Exit.
	 * 
	 * The Open... and Save As... should make use of a JFileChooser dialog.
	 * 
	 * Help menu with at least one "help" option.
	 * 
	 * The application should use a drop-down list to allow the user to select a
	 * Person to view / edit / delete / whatever.
	 * 
	 * Note the "serialization" demo, allows you to save and retrieve containers of
	 * objects, and which is provided for that use here.
	 * 
	 * The user should be able to use the GUI to load, create, modify, delete, and
	 * save objects of type Person, RegisteredPerson, and OCCCPerson.
	 */

	public static void main(String[] args) {
		PersonGUI p = new PersonGUI(); // run the GUI program
	}

	public PersonGUI() {

		setTitle("Person GUI");
		setSize(800, 800);
		setLocationRelativeTo(null);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS)); // evenly space and allow the panels to stack
																		// vertically

		// from ppt
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exitMenu();
			}
		});

		menuBar = new JMenuBar();

		// center label - create a drop down list that will act as a container of Person
		// objects

		// from ppt 4

		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F); // hot key or shortcut

		// when clicked on, the menu will display the options new, open, save, save as,
		// and exit
		newItem = new JMenuItem("New");
		newItem.setMnemonic(KeyEvent.VK_N);

		openItem = new JMenuItem("Open");
		openItem.setMnemonic(KeyEvent.VK_O);

		saveItem = new JMenuItem("Save");
		saveItem.setMnemonic(KeyEvent.VK_V);

		saveAsItem = new JMenuItem("Save as...");

		exitItem = new JMenuItem("Exit");
		exitItem.setMnemonic(KeyEvent.VK_X);

		// adding to the menuBar on the top of the screen
		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);

		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
		setVisible(true);

		// now for the helpMenu
		helpMenu = new JMenu("Help");

		helpMenu.setMnemonic(KeyEvent.VK_H);
		JMenuItem helpDialog = new JMenuItem("For more help, please contact Jason Vo, jason.t.vo@my.occc.edu :)");
		helpMenu.add(helpDialog);
		menuBar.add(Box.createHorizontalGlue());

		menuBar.add(helpMenu);

		// title of the program
		JPanel titlePanel = new JPanel();

		JLabel titleLabel = new JLabel("Welcome to the PersonGUI!");
		titleLabel.setFont(appFontLarge);
		titleLabel.setLayout(getLayout());

		JPanel descriptionPanel = new JPanel();
		JLabel descriptionLabel = new JLabel(
				"This program will allow the user to load, create, modify, delete, and save objects of type Person, RegisteredPerson, and OCCCPerson."
						+ " Please view and select your Person object below: ");
		descriptionLabel.setFont(appFontSmall);

		descriptionPanel.add(descriptionLabel);

		titlePanel.add(titleLabel, BorderLayout.NORTH);

		// add to our JFrame
		add(titlePanel, BorderLayout.NORTH);
		add(descriptionPanel); // description goes under the title

		// in the center of the screen, there will be a JComboBox that will act as a
		// drop-down list to allow the
		// user to select a Person to view, edit, or delete

		JPanel listPanel = new JPanel();
		viewObjectsMenu = new JComboBox();
		viewObjectsMenu.setVisible(true);

		objects = new String[pList.size()];
		pList.toArray(objects);

		listPanel.add(viewObjectsMenu);

		this.add(listPanel); // currently empty, objects will appear when they are created

		JPanel buttonPanel = new JPanel();

		createButton = new JButton("Create Person");
		editButton = new JButton("Edit");
		deleteButton = new JButton("Delete");

		buttonPanel.add(createButton);
		buttonPanel.add(editButton);
		buttonPanel.add(deleteButton);

		add(listPanel);
		add(buttonPanel); // centerPanel goes below the title and description panels

		// add ActionListeners for where the user would click (JMenu, JMenuItems,
		// JButtons)
		newItem.addActionListener(this);
		openItem.addActionListener(this);
		saveItem.addActionListener(this);
		saveAsItem.addActionListener(this);
		exitItem.addActionListener(this);

		viewObjectsMenu.addActionListener(this);

		createButton.addActionListener(this);
		editButton.addActionListener(this);
		deleteButton.addActionListener(this);

	}

	
	
	// when the user hits the close button, runs our own exit code, can handle
	// whatever needs to be taken care of on exit
	public void exitMenu() {
		//System.out.println("EXIT FUNCTION CALLED...");
		
		int choice = 0;
		String[] options = {"Save and Exit", "Don't Save and Exit", "Cancel"};
		
		// If no original last modified then no file has be created, ask to save
		if(Objects.isNull(originalLastModified)) {
			choice = JOptionPane.showOptionDialog(null,
					"You have yet to save this file.", 
					"Exit Menu",
					JOptionPane.DEFAULT_OPTION,
					JOptionPane.INFORMATION_MESSAGE,
					null,
					options,
					options[0]);
		}
		
		// If current last modified is null then no changes have been made free to exit
		// however, if the current last modified does exist but it is less than or equal to original last modified
		// then changes have been made but the user has saved the file since then.
		else if (Objects.isNull(currentLastModified) || currentLastModified.compareTo(originalLastModified) < 0) {
			// close program
			System.exit(0);
		}
		
		// otherwise an edit has been made and the file has not been saved since changes were made
		else {
			choice = JOptionPane.showOptionDialog(null,
					"You have made changes since your last save.", 
					"Exit Menu",
					JOptionPane.DEFAULT_OPTION,
					JOptionPane.INFORMATION_MESSAGE,
					null,
					options,
					options[0]);
		}
		
		
		switch (choice) {
			case 0:
				try {
					saveFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.exit(0);
			case 1:
				System.exit(0);
			case 2:
				break;
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		// menu items
		if (e.getSource() == newItem) {
			createNewFile();
		}
		if (e.getSource() == openItem) {
			try {
				openFile();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if (e.getSource() == saveItem) {
			try {
				saveFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (e.getSource() == saveAsItem) {
			try {
				saveAsFile();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if (e.getSource() == exitItem) {
			exitMenu();
		}

		// drop down menu
		if (e.getSource() == viewObjectsMenu) {
			int indexOfList = viewObjectsMenu.getSelectedIndex(); // will get the index to determine which Person object
																	// has been selected and will be used

			// update the global variable that will be used by all the other programs, get
			// the object at that index
			objectIndex = indexOfList;
		}

		// buttons
		if (e.getSource() == createButton) {
			createNewPerson();
		}
		if (e.getSource() == editButton) {
			editPerson();
		}
		if (e.getSource() == deleteButton) {
			deletePerson();
		}

	}

	public void createNewFile() {
		
		// clearing pList and the drop down menu (JComboBox)
		pList.clear();
		viewObjectsMenu.removeAllItems();

	}

	public void openFile() throws IOException { // de-serialization

		// prompt the user for a file to choose using JFileChooser
		fileChooser = new JFileChooser();

//		//default directory is our java project
//		fileChooser.setCurrentDirectory(new File("."));

		// open dialog menu, select file to open
		int response = fileChooser.showOpenDialog(fileChooser);

		if (response == JFileChooser.APPROVE_OPTION) { // if the user selects a valid file instead of clicking the x or
														// cancel button

			// the file that the user selected, get its path to be used in the
			File selectedFile = new File(fileChooser.getSelectedFile().getAbsolutePath());

			// object input stream to a new file input stream; reading in the file
			ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(selectedFile));

			// fill our data to the array list using the readObject() method, cast our
			// object's name
			while (true) {
				try {

					Object o = objectInputStream.readObject();

					// we will want to read from children to the parent in this case, since children
					// are more specific; if read Person first, then it would always stop at person
					// object and use the
					// Person constructor which would remove the govID and studentID

					// if it is a OCCCPerson
					if (o instanceof OCCCPerson) {
						OCCCPerson occc = new OCCCPerson((OCCCPerson) o);
						pList.add(occc);

					}

					// if it is a RegisteredPerson
					else if (o instanceof RegisteredPerson) {
						RegisteredPerson reg = new RegisteredPerson((RegisteredPerson) o);
						pList.add(reg);
					}

					// else it is a Person
					else {
						Person person = new Person((Person) o);
						pList.add(person);
					}

				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					break;
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}

			objectInputStream.close();

			// now fill the JComboBox for the user's view with the objects held in the
			// ArrayList pList
			for (int i = 0; i < pList.size(); i++) {
				viewObjectsMenu.addItem(pList.get(i));

			}
			
			// for use with save file when overriding its data after edits
			currentFile = selectedFile;
			
			//recording the opening of the file and it's last modified date.
			originalLastModified = currentFile.lastModified();

		}

	}

	public void saveFile() throws IOException { // serialization

		// if there is currently no data in the array list to save and export
		if (pList.isEmpty()) {
			JOptionPane.showMessageDialog(null,
					"User has not created any Person objects, therefore unable to create and save to an empty file.",
					"Note", JOptionPane.INFORMATION_MESSAGE); // null to center, message, title, type of message
		}

		if (currentFile == null) { // this is a new file

			saveAsFile();

		}

		// in other cases when user does create objects
		else {

			ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(currentFile)); // create a stream
			
			//recording the time the file was saved
			originalLastModified = currentFile.lastModified();
			for (int i = 0; i < pList.size(); i++) {
				objOut.writeObject(pList.get(i));
			}

		}

	}

	public void saveAsFile() throws IOException {

		// if there is currently no data in the array list to save and export
		if (pList.isEmpty()) {
			JOptionPane.showMessageDialog(null,
					"User has not created any Person objects, therefore unable to save an empty file.", "Note",
					JOptionPane.INFORMATION_MESSAGE); // null to center, message, title, type of message
		}

		// in other cases when user does create objects
		else {
			
			fileChooser = new JFileChooser(); // initialize

			// prompting for save dialog
			int response = fileChooser.showSaveDialog(null);

			if (response == fileChooser.APPROVE_OPTION) {

				File saveFile = new File(fileChooser.getSelectedFile().getAbsolutePath());
				
				//recording the time the file was created.
				originalLastModified = saveFile.lastModified();
				
				// object output stream, outputs to a file output to a file named
				// Person_GUI_List.ser; copying our objects into a file (.bin)
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(saveFile));

				for (int i = 0; i < pList.size(); i++) {

					// write to the output file every index of the ArrayList of Person objects
					objectOutputStream.writeObject(pList.get(i));

				}

				objectOutputStream.close();

			}

		}

	}

	public void createNewPerson() {
		
		if (currentFile != null) {
			currentLastModified = currentFile.lastModified();
		}
		// prompt for the JTextFields for firstName, lastName, govID, and student ID if
		// applicable
		String fnInput = JOptionPane.showInputDialog(this, "Enter the first name: ");
		String lnInput = JOptionPane.showInputDialog(this, "Enter the last name: ");
		String govInput = JOptionPane.showInputDialog(this, "Enter the government ID (if applicable): ");
		String studentInput = JOptionPane.showInputDialog(this, "Enter the student ID (if applicable): ");

		// create person object based on the input, work from the children to parent
		// (most to least parameters)
		// first student, then registered, then person
		if (govInput != null && studentInput != null) {
			RegisteredPerson r = new RegisteredPerson(fnInput, lnInput, govInput);

			// use registeredPerson constructor
			OCCCPerson o = new OCCCPerson(r, studentInput);

			viewObjectsMenu.addItem(o);// add to view menu
			pList.add(o);// add to the array list that will be saved and loaded
		}

		else if (govInput != null) {
			RegisteredPerson r = new RegisteredPerson(fnInput, lnInput, govInput);

			viewObjectsMenu.addItem(r);
			pList.add(r);
		}

		else {
			Person p = new Person(fnInput, lnInput);

			viewObjectsMenu.addItem(p);
			pList.add(p);
		}

	}

	public void deletePerson() {
		if (currentFile != null) {
			currentLastModified = currentFile.lastModified();
		}
		// get the selected index of the Person that the user wants to delete
		int a = objectIndex;

		// remove the object at that particular index
		viewObjectsMenu.removeItem(pList.get(a));
		pList.remove(a);// remove from array list

	}

	public void editPerson() {
		
		if (currentFile != null) {
			currentLastModified = currentFile.lastModified();
		}
		// in case of empty list
		if (pList.isEmpty() || objectIndex < 0 || objectIndex >= pList.size()) {
			JOptionPane.showMessageDialog(this, "No person selected or list is empty.");
			return;
		}

		// get the selected index of the Person that the user wants to edit
		int a = objectIndex;

		// similar to the createPerson method, however delete and replace/overwrite the
		// person at that specifc index

		// prompt for the JTextFields for firstName, lastName, govID, and student ID if
		// applicable
		String fnInput = JOptionPane.showInputDialog(this, "Enter the first name: ");
		String lnInput = JOptionPane.showInputDialog(this, "Enter the last name: ");
		String govInput = JOptionPane.showInputDialog(this, "Enter the government ID (if applicable): ");
		String studentInput = JOptionPane.showInputDialog(this, "Enter the student ID (if applicable): ");

		// create person object based on the input, work from the children to parent
		// (most to least parameters)
		// first student, then registered, then person
		if (govInput != "" && studentInput != "") {
			RegisteredPerson r = new RegisteredPerson(fnInput, lnInput, govInput);

			// use registeredPerson constructor
			OCCCPerson o = new OCCCPerson(r, studentInput);

			viewObjectsMenu.removeItem(pList.get(a)); // remove object from the view menu AND the pList
			viewObjectsMenu.insertItemAt(o, a); // add new edited one in place of old one
			pList.set(a, o);
		}

		else if (govInput != "" && studentInput != "") {
			RegisteredPerson r = new RegisteredPerson(fnInput, lnInput, govInput);

			viewObjectsMenu.remove(a);

			viewObjectsMenu.removeItem(pList.get(a));
			viewObjectsMenu.insertItemAt(r, a);
			pList.set(a, r);
		}

		else {
			Person p = new Person(fnInput, lnInput);

			viewObjectsMenu.remove(a);

			viewObjectsMenu.removeItem(pList.get(a));
			viewObjectsMenu.insertItemAt(p, a);
			pList.set(a, p);
		}

	}

}
