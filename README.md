
## Person Manager Application
## Spring 2025 Advanced Java Final Project - Unit 8 Person GUI
### Overview

The Person Manager is a user-friendly Java Swing GUI application designed to efficiently manage information about different types of people: basic **Person**, **RegisteredPerson**, and **OCCCPerson**. This application allows you to easily add, edit, delete, search, sort, and persistently store person data.

### Person Types

The application caters to three distinct types of individuals, each requiring specific information:

* **Person**: Requires the individual's **First Name**, **Last Name**, and **Date of Birth**.
* **RegisteredPerson** (inherits from Person): In addition to the base Person information, it also requires a **Government ID**.
* **OCCCPerson** (inherits from Person): Besides the basic Person details, it necessitates a **Student ID**.

### Core Features

#### Create & Edit Person

* A dynamic modal dialog adapts its form based on the selected person type, ensuring only relevant fields are displayed.
* Robust input validation is implemented for all fields to maintain data integrity.
* Date handling is streamlined using the custom **OCCCDate** class.
* The "Save" button triggers both the creation/modification of the person object and its persistence to storage.

#### List Display

* People are visually represented using a custom-rendered card layout for an organized and readable display.
* Each person's information is presented on a card with a fixed width and dynamic height.
* Key details such as name, date of birth, and relevant IDs are displayed on separate lines for clarity.
* The card styling dynamically adjusts to the application's light or dark color theme, configurable within the PersonManagerGUI settings.

#### Search

* A case-insensitive search functionality allows you to find individuals using keyword input.
* You can filter your search based on:
    * First Name
    * Last Name
    * Student ID (specific to OCCCPerson)

#### Sorting Options

* A convenient dropdown selector provides options to sort the displayed list of people by:
    * Last Name
    * Date of Birth
    * Student ID
* The sorting functionality utilizes dedicated comparator classes:
    * `PersonDateOfBirthComparator`
    * `PersonIDComparator`

#### Delete Functionality

* The application enables users to select and delete multiple people simultaneously.
* Deletions are immediately reflected in the application's data and the underlying storage.

#### Reset Functionality

* A "Reset" button provides a quick way to clear any active search or sort filters.
* Clicking "Reset" reloads the data from the currently loaded file and restores the default list view.

#### File Management

##### Auto-Load

* Upon the application's first launch, it automatically attempts to load person data from the designated "People's Dataset" file if it exists.

##### File Menu

* **New**: Creates a new, empty list of people, discarding the current list without prompting for confirmation.
* **Open**: Allows you to load person data from a different file using a standard `JFileChooser`.
* **Save**: Saves the current list of people to the currently active file path.
* **Save As**: Enables you to save the current list to a new file location. This action also updates the application's active file path.
* **Exit**: Prompts for confirmation if there are unsaved changes before closing the application.

#### Persistence

* Person data is stored and retrieved using Java object serialization, managed by the `PersonFileUtil` class.
* Every save operation persists the entire current list of people.

### Utility Infrastructure

#### Custom Classes

* **Person - RegisteredPerson - OCCCPerson Class Hierarchy**: Defines the structure and inheritance relationships for the different person types.
* **OCCDate**: A custom class that wraps Java's `GregorianCalendar` to provide specific date formatting and handling.
* **PersonFileUtils**: A utility class responsible for handling the saving and loading of serialized person lists.
* **PersonListCellRenderer**: A custom Swing `ListCellRenderer` that provides the stylish card-based display of person information.
* **Dynamic creation/editing mode (Person-to-Person)**: Enables the application to dynamically adjust the input form based on the selected person type during creation and editing.
* **Comparators for sorting logic**: Includes `PersonDateOfBirthComparator` and `PersonIDComparator` to facilitate the different sorting options.
Group - Travis Bauman, Amida Fombutu, Jason Vo
