# Person Manager Application
## Spring 2025 Advanced Java Final Project - Unit 8 Person GUI

### Overview

The **Person Manager GUI** is a Java Swing application developed for managing records of different person types: **Person**, **RegisteredPerson**, and **OCCCPerson**. It offers a clean interface to create, view, edit, and delete people, with full support for file saving/loading using Java serialization.

### Person Types

The application supports three types of people, each with increasing levels of detail:

- **Person**: Includes First Name, Last Name, and Date of Birth (DOB).
- **RegisteredPerson**: Inherits from Person and adds a Government ID.
- **OCCCPerson**: Inherits from RegisteredPerson and adds a Student ID.

### Core Features

#### 🧍‍♂️ Create & Edit

- A reusable modal dialog provides an intuitive form for adding or editing people.
- The form dynamically updates its fields depending on the selected person type.
- Input validation is enforced:
  - Required fields must be filled.
  - Invalid DOBs trigger an `InvalidOCCCDateException`.
- DOB is selected using dropdowns (day/month/year) to ensure valid and easy date entry.

#### 🧾 Display

- Persons are shown in a scrollable list with a custom-rendered layout.
- Each list entry displays key attributes like name, DOB, and applicable IDs.
- The styling respects platform look-and-feel and adjusts for selection focus.

#### 🗃️ File Operations

The **File** menu supports the full document lifecycle:

- **New**: Clears the current list after prompting to save unsaved changes.
- **Open...**: Loads a `.dat` file containing serialized person data.
- **Save**: Saves to the last used file (only available when changes exist).
- **Save As...**: Prompts for a new file name and saves the data.
- **Exit**: Confirms unsaved changes before closing the application.

#### 📁 Persistence

- Uses Java Object Serialization to store the list of persons.
- All person types are serializable through a shared hierarchy.
- File I/O is integrated with `JFileChooser` for platform-native dialogs.

#### 💡 Smart UX

- “Save” and “Save As” options are disabled while the add/edit dialog is open to prevent incomplete saves.
- If changes are made and the user closes the app or opens a new file, a “Save before exiting?” prompt appears.
- The window title reflects the current file name and shows an asterisk (*) when unsaved changes exist.

### Technical Infrastructure

- **Person Class Hierarchy**: `Person` → `RegisteredPerson` → `OCCCPerson`
- **OCCCDate**: A custom class wrapping `GregorianCalendar` with validation logic.
- **Custom ListCellRenderer**: Used to display formatted entries in the GUI list.
- **ComboBox-based DOB Input**: Prevents invalid dates through constrained selection.

---

### Team

- Travis Bauman
- Amida Fombutu
- Jason Vo