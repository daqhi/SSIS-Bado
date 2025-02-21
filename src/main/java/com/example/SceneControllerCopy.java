// package com.example;

// import java.io.BufferedReader;
// import java.io.FileReader;
// import java.io.FileWriter;
// import java.io.IOException;
// import java.net.URL;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;
// import java.util.ResourceBundle;
// import java.util.function.Consumer;

// import javafx.beans.property.SimpleStringProperty;
// import javafx.collections.FXCollections;
// import javafx.collections.ObservableList;
// import javafx.fxml.FXML;
// import javafx.fxml.Initializable;
// import javafx.geometry.Insets;
// import javafx.scene.control.Alert;
// import javafx.scene.control.Button;
// import javafx.scene.control.ButtonBar;
// import javafx.scene.control.ComboBox;
// import javafx.scene.control.Tab;
// import javafx.scene.control.TabPane;
// import javafx.scene.control.TableCell;
// import javafx.scene.control.TableColumn;
// import javafx.scene.control.TableView;
// import javafx.scene.control.TextField;
// import javafx.scene.layout.HBox;


// public class SceneControllerCopy extends MainController implements Initializable{

//     private String StudentFilePath = "C:/Users/eliab/Documents/ELIA FILES/SIS/demo/src/main/resources/csv_files/Students.csv";
//     private String ProgramFilePath = "C:/Users/eliab/Documents/ELIA FILES/SIS/demo/src/main/resources/csv_files/Program.csv";
//     private String CollegeFilePath = "C:/Users/eliab/Documents/ELIA FILES/SIS/demo/src/main/resources/csv_files/College.csv";

//     //--------------------------REGISTRATION FORMS--------------------------//

//     //TABPANE FOR REGISTRATION
//     @FXML private TabPane registrationForms;
//         @FXML private Tab studentReg;
//             @FXML private TextField idNum;
//             @FXML private TextField firstName;
//             @FXML private TextField lastName;
//             @FXML private ComboBox<String> year;
//             @FXML private ComboBox<String> sex;
//             @FXML private TextField studentCollege;
//             @FXML private TextField studentProgram;
//             @FXML private Button regStudent;
//             @FXML private Button updtStudent;
//             @FXML private Button clearStudentFormButton;
//             private String editingStudentId;
//         @FXML private Tab programReg;
//             @FXML private TextField progCollege;
//             @FXML private TextField programCode;
//             @FXML private TextField programName;
//             @FXML private Button regProgram;
//             @FXML private Button updtProgram;
//             @FXML private Button clearProgramFormButton;
//             private String editingProgramId;
//         @FXML private Tab collegeReg;
//             @FXML private TextField collegeCode;
//             @FXML private TextField collegeName;
//             @FXML private Button regCollege;
//             @FXML private Button updtCollege;
//             @FXML private Button clearCollegeFormButton;
//             private String editingCollegeId;

//     @FXML private void registerStudent() {
//         String id = idNum.getText().trim();
//         String last = lastName.getText().trim();
//         String first = firstName.getText().trim();
//         String gender = sex.getValue();
//         String yearLvl = year.getValue();
//         String program = studentProgram.getText().trim();
//         String college = studentCollege.getText().trim();
        
//         if (isEmptyField(id, last, first, gender, yearLvl, program, college)) { 
//             showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
//         } else if (!id.matches("\\d{4}-\\d{4}")) { 
//             showAlert(Alert.AlertType.ERROR, "Error", "Please follow ID Number format.\nFormat: YYYY-NNNN");
//         } else if (!inputExists(program, "/csv_files/Program.csv", 1) && !inputExists(college, "/csv_files/College.csv", 0)) {
//             showAlert(Alert.AlertType.ERROR, "Error", "Program '" + college + " and " + program + "' does not exist. Please register it first.");
//         } else if (!inputExists(college, "/csv_files/College.csv", 0)) {
//             showAlert(Alert.AlertType.ERROR, "Error", "College '" + college + "' does not exist. Please register it first.");
//         } else if (!inputExists(program, "/csv_files/Program.csv", 1)){
//             showAlert(Alert.AlertType.ERROR, "Error", "Program '" + program + "' does not exist. Please register it first.");
//         } else if (isValueTakenInCSV(id, 0, StudentFilePath)){ // checks for duplicates
//             showAlert(Alert.AlertType.ERROR, "Error", "ID Number already exists. \nFailed to register student.");
//         } else { // asks user for confirmation before saving
//             showAlertAndRegister(
//                 String.format("%s,%s,%s,%s,%s,%s,%s\n", id, last, first, gender, yearLvl, program, college),
//                 StudentFilePath,
//                 "Student registered successfully!",
//                 this::clearStudentForm);
//             setupStudentTable();
//         }              
//     }

//     @FXML private void updateStudent(){
//         String id = idNum.getText().trim();
//         String last = lastName.getText().trim();
//         String first = firstName.getText().trim();
//         String gender = sex.getValue();
//         String yearLvl = year.getValue();
//         String program = studentProgram.getText().trim();
//         String college = studentCollege.getText().trim();

//         if (isEmptyField(id, last, first, gender, yearLvl, program, college)) {
//             showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
//             return;
//         } else if (!id.matches("\\d{4}-\\d{4}")) {
//             showAlert(Alert.AlertType.ERROR, "Error", "Please follow ID Number format.\nFormat: YYYY-NNNN");
//             return;
//         }

//         boolean updated = updateStudentInCSV(editingStudentId, id, last, first, gender, yearLvl, program, college, StudentFilePath);
//         if (updated) {
//             showAlert(Alert.AlertType.INFORMATION, "Success", "Student updated successfully!");
//             clearStudentForm();
//             editingStudentId = null;
//             updtStudent.setDisable(true);
//             updtStudent.setVisible(false);
//             regStudent.setDisable(false);
//             regStudent.setVisible(true);
//             setupStudentTable(); // reload student table
//         } else {
//             showAlert(Alert.AlertType.ERROR, "Error", "Failed to update student.");
//         } 
//     }

//     @FXML private void registerProgram() {
//         String college = progCollege.getText();
//         String progName = programName.getText();
//         String progCode = programCode.getText();

//         if (isEmptyField(college, progName, progCode)) { //shows alert if save is clicked and program fields are empty
//             showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
//             System.out.println("request to fill required fields");
//         } else if (!inputExists(college, "/csv_files/College.csv", 0)) { //shows alert if college do not exist 
//             showAlert(Alert.AlertType.ERROR, "Error", "College code " + college + " does not exist. Please register it first.");
//             System.out.println("college do not exist");   
//         } else if ((isValueTakenInCSV(progCode, 0, ProgramFilePath)) &&
//                    (isValueTakenInCSV(progCode, 0, ProgramFilePath))){ // checks for duplicates in progCode
//             showAlert(Alert.AlertType.ERROR, "Error", "Program Code and Program Name already exists.\nFailed to register program code and program name.");
//         } else if (isValueTakenInCSV(progCode, 0, ProgramFilePath)){ // checks for duplicates in progCode
//             showAlert(Alert.AlertType.ERROR, "Error", "Program Code already exists.\nFailed to register program code.");
//         } else if (isValueTakenInCSV(progName, 1, ProgramFilePath)){ // checks for duplicates in progName
//             showAlert(Alert.AlertType.ERROR, "Error", "Program Name already exists.\nFailed to register program name.");
//         } else { //shows confirmation alert to save data
//             showAlertAndRegister(
//                 String.format("%s,%s,%s\n", progCode, progName, college),
//                 ProgramFilePath,
//                 "Program registered successfully!",
//                 this::clearProgramForm);
//             setupProgramTable();
//         }
//     }

//     @FXML private void updateProgram() {
//         String college = progCollege.getText().trim();
//         String code = programCode.getText().trim();
//         String name = programName.getText().trim();

//         if (isEmptyField(college, code, name)) {
//             showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
//             return;
//         }
//         String oldCode = editingProgramId;
//         Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
//         confirmationAlert.setTitle("Confirm Edit");
//         confirmationAlert.setHeaderText("Are you sure you want to edit this item?");
//         confirmationAlert.setContentText("Click OK to proceed or Cancel to abort.");
//         confirmationAlert.showAndWait().ifPresent(response -> {
//             if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
//                 boolean updated = updateProgramInCSV(oldCode, college, code, name, ProgramFilePath);
//                 if (updated) {
//                     displayNewValueOnProgramEdit(oldCode);
//                     showAlert(Alert.AlertType.INFORMATION, "Success", "Program updated successfully!");
//                     clearProgramForm();
//                     editingProgramId = null;
//                     updtProgram.setDisable(true);
//                     updtProgram.setVisible(false);
//                     regProgram.setDisable(false);
//                     regProgram.setVisible(true);
//                     setupProgramTable();
//                 } else {
//                     showAlert(Alert.AlertType.ERROR, "Error", "Failed to update program.");
//                 }
//             } else {
//                 // User canceled the action
//                 System.out.println("Update canceled by user.");
//                 return;
//             }
//         });
//     }

//     @FXML private void registerCollege() {
//         String colCode = collegeCode.getText();
//         String colName = collegeName.getText();

//         if (isEmptyField(colName, colCode)) {
//             showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
//         } else if ((isValueTakenInCSV(colCode, 0, CollegeFilePath)) &&
//                    (isValueTakenInCSV(colName, 1, CollegeFilePath))){
//             showAlert(Alert.AlertType.ERROR, "Error", "College code already exists.\nFailed to register college code.");
//         } else if (isValueTakenInCSV(colCode, 0, CollegeFilePath)){
//             showAlert(Alert.AlertType.ERROR, "Error", "College code and college name already exists.\nFailed to register college code and college name.");
//         } else if (isValueTakenInCSV(colName, 1, CollegeFilePath)){
//             showAlert(Alert.AlertType.ERROR, "Error", "College Name already exists.\nFailed to register college name.");
//         } else {
//             showAlertAndRegister(
//                 String.format("%s,%s\n", collegeCode.getText(), collegeName.getText()),
//                 CollegeFilePath,
//                 "College registered successfully!",
//                 this::clearCollegeForm);
//             setupCollegeTable();
//         }        
//     }

//     @FXML private void updateCollege() {
//         String newCode = collegeCode.getText().trim();
//         String name = collegeName.getText().trim();
//         //i need the old college code to be passed here so that it can be updated in the csv file


//         if (isEmptyField(newCode, name)) {
//             showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
//             //return;
//         }

//         String oldCode = editingCollegeId;
//         Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
//         confirmationAlert.setTitle("Confirm Edit");
//         confirmationAlert.setHeaderText("Are you sure you want to edit this item?");
//         confirmationAlert.setContentText("Click OK to proceed or Cancel to abort.");
//         confirmationAlert.showAndWait().ifPresent(response -> {
//             if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
//                 boolean updated = updateCollegeInCSV(oldCode, newCode, name, CollegeFilePath);
//                 if (updated) {
//                     displayNewValueOnCollegeEdit(oldCode);
//                     showAlert(Alert.AlertType.INFORMATION, "Success", "College updated successfully!");
//                     clearCollegeForm();
//                     editingCollegeId = null;
//                     updtCollege.setDisable(true);
//                     updtCollege.setVisible(false);
//                     regCollege.setDisable(false);
//                     regCollege.setVisible(true);
//                     setupCollegeTable();
//                 } else {
//                     showAlert(Alert.AlertType.ERROR, "Error", "Failed to update college.");
                    
//                 }
//             }
//         });     
//     }

//     private boolean updateStudentInCSV(String oldId, String newId, String lastName, String firstName, String sex, String year, String program, String college, String filePath) {
//         List<String> lines = new ArrayList<>();
//         boolean found = false;
    
//         try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//             String line;
//             while ((line = br.readLine()) != null) {
//                 String[] values = line.split(",");
//                 if (values.length > 0) {
//                     String currentId = values[0].trim();
                
//                     // checks if newId already exists (and it's not the oldId being updated)
//                     if (currentId.equals(newId) && !currentId.equals(oldId)) {
//                         showAlert(Alert.AlertType.ERROR, "Duplicate ID", "ID Number already exists\nFailed to update student.");
//                         return false;
//                     }
                    
//                     if (currentId.equals(oldId)) { // replaces the line with the updated info
//                         lines.add(String.format("%s,%s,%s,%s,%s,%s,%s", newId, lastName, firstName, sex, year, program, college));
//                         found = true;
//                     } else {
//                         lines.add(line);
//                     }
//                 }
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//             return false;
//         }
    
//         // Only rewrite the file if the student was found
//         if (found) {
//             try (FileWriter writer = new FileWriter(filePath)) {
//                 for (String l : lines) {
//                     writer.write(l + "\n");
//                 }
//                 return true;
//             } catch (IOException e) {
//                 e.printStackTrace();
//                 return false;
//             }
//         }
//         return false; // Student with oldId wasn't found
//     }

//     private boolean updateProgramInCSV(String oldCode, String college, String code, String name, String filePath) {
//         List<String> lines = new ArrayList<>();
//         boolean found = false;
//         try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//             String line;
//             while ((line = br.readLine()) != null) {
//                 String[] values = line.split(",");
//                 if (values.length > 1 && values[0].trim().equals(oldCode)) { 
//                     lines.add(String.format("%s,%s,%s", code, name, college));
//                     found = true;
//                 } else {
//                     lines.add(line);
//                 }
//             }
            
//         } catch (IOException e) {
//             e.printStackTrace();
//             return false;
//         }

//         if (found) {
//             try (FileWriter writer = new FileWriter(filePath)) {
//                 for (String l : lines) {
//                     writer.write(l + "\n");
//                 }
//                 return true;
//             } catch (IOException e) {
//                 e.printStackTrace();
//                 return false;
//             }
//         }
//         return false;
//     }

//     //updates college in csv file
//     private boolean updateCollegeInCSV(String oldCode, String code, String name, String filePath) {
//         List<String> lines = new ArrayList<>();
//         boolean found = false;
//         try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//             String line;
//             while ((line = br.readLine()) != null) {
//                 String[] values = line.split(",");
//                 if (values.length > 0 && values[0].trim().equals(oldCode)) { //dili apilon ang column 1 sa csv
//                     lines.add(String.format("%s,%s", code, name)); 
//                     found = true;
//                 } else {
//                     lines.add(line);
//                 }
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//             return false;
//         }

//         if (found) {
//             try (FileWriter writer = new FileWriter(filePath)) {
//                 for (String l : lines) {
//                     writer.write(l + "\n");
//                 }
//                 return true;
//             } catch (IOException e) {
//                 e.printStackTrace();
//                 return false;
//             }
//         }
//         return false;
//     }


//     private void clearStudentForm() {
//         idNum.clear();
//         lastName.clear();
//         firstName.clear();
//         sex.setValue(null);
//         year.setValue(null);
//         studentProgram.clear();
//         studentCollege.clear();
//     }

//     private void clearProgramForm() {
//         collegeCode.clear();
//         programCode.clear();
//         programName.clear();
//     }

//     private void clearCollegeForm() {
//         collegeCode.clear();
//         collegeName.clear();
//     }

//     private void idnumFormat(TextField textfield) {
//         textfield.textProperty().addListener((observable, oldValue, newValue) -> {
//             if (newValue.isEmpty() || !newValue.matches("\\d{4}-\\d{4}")) {
//                 textfield.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 2px;");
//             } else {
//                 textfield.setStyle("");
//             }
//         });
//     }

//     // checks for duplicates
//     private boolean isValueTakenInCSV(String valueToCheck, int columnIndex, String filePath) {
//         try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//             String line;
//             while ((line = br.readLine()) != null) {
//                 String[] values = line.split(",");
//                 if (values.length > columnIndex && values[columnIndex].trim().equalsIgnoreCase(valueToCheck.trim())) {
//                     return true;
//                 }
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//         return false;
//     }

//     // checks if fields are empty
//     protected boolean isEmptyField(String... fields) {
//         for (String field : fields) {
//            if (field == null || field.isBlank()) return true;
//         }
//         return false;
//     }

//     //for clear button
//     @FXML private void clearSForm() {
//         clearStudentForm();
//     }

//     @FXML private void clearPForm() {
//         clearProgramForm();
//     }

//     @FXML private void clearCForm() {
//         clearCollegeForm();
//     }





//     //--------------------------DIRECTORIES--------------------------//

//     //TABPANE FOR TABLES
//     @FXML private TabPane directories;
//         @FXML private TableView<ObservableList<String>> studentTable;
//         @FXML private Tab studentDir;
//             @FXML private TableColumn<ObservableList<String>, String> idColumn;
//             @FXML private TableColumn<ObservableList<String>, String> firstNameColumn;
//             @FXML private TableColumn<ObservableList<String>, String> lastNameColumn;
//             @FXML private TableColumn<ObservableList<String>, String> yearColumn;
//             @FXML private TableColumn<ObservableList<String>, String> sexColumn;
//             @FXML private TableColumn<ObservableList<String>, String> studCollegeCodeColumn;
//             @FXML private TableColumn<ObservableList<String>, String> studProgramCodeColumn;
//             @FXML private TableColumn<ObservableList<String>, HBox> studentActionColumn;
//         @FXML private TableView<ObservableList<String>> programTable;
//         @FXML private Tab programDir;
//             @FXML private TableColumn<ObservableList<String>, String> progCollegeColumn;
//             @FXML private TableColumn<ObservableList<String>, String> progProgramCodeColumn;
//             @FXML private TableColumn<ObservableList<String>, String> progProgramNameColumn;
//             @FXML private TableColumn<ObservableList<String>, HBox> programActionColumn;
//         @FXML private TableView<ObservableList<String>> collegeTable;
//         @FXML private Tab collegeDir;
//             @FXML private TableColumn<ObservableList<String>, String> colCollegeCodeColumn;
//             @FXML private TableColumn<ObservableList<String>, String> colCollegeNameColumn;
//             @FXML private TableColumn<ObservableList<String>, HBox> collegeActionColumn;

//     //for reading csv files and displaying them in the table
//     private ObservableList<ObservableList<String>> loadCSV(String filePath) {
//         ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

//         try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//             String line;
            
//             boolean isFirstLine = true; // Skip the first line (header)

//             while ((line = br.readLine()) != null) {
//                 if (isFirstLine) {
//                    isFirstLine = false;
//                    continue; // skip the header row
//                 }

//                 if (line.trim().isEmpty()) continue;

//                 String[] split = line.split(",");

//                 // Validate expected column count
//                 //UNDERSTAND WATH THIS IS FOOOOOORRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
//                 if (split.length < 7 && filePath.contains("Students")) continue;
//                 if (split.length < 3 && filePath.contains("Program")) continue;
//                 if (split.length < 2 && filePath.contains("College")) continue;

//                 ObservableList<String> row = FXCollections.observableArrayList(Arrays.asList(split));
//                 data.add(row);
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//         return data;
//     }

//     private void setupActionButton(
//         TableColumn<ObservableList<String>, HBox> actionColumn, 
//         int rowSize, 
//         String filePath, 
//         Consumer<ObservableList<String>> editAction, 
//         Consumer<ObservableList<String>> deleteAction,
//         String tableType) { 
    
//         actionColumn.setCellFactory(col -> new TableCell<ObservableList<String>, HBox>() {
//             private final Button editBtn = new Button("Edit");
//             private final Button deleteBtn = new Button("Delete");
//             private final HBox box = new HBox(3, editBtn, deleteBtn);

//             {
//                 box.setPadding(new Insets(0, 5, 0, 5)); //spacing

//                 editBtn.setOnAction(event -> {
//                     ObservableList<String> selectedRow = getTableView().getItems().get(getIndex());
//                     System.out.println("Selected row: " + selectedRow); //pag click sa edit button, i print ang selected row
//                     if (tableType.equals("COLLEGE")) {
//                         populateCollegeForm(selectedRow);
//                     } else if (tableType.equals("PROGRAM")) {
//                         populateProgramForm(selectedRow);   
//                     }
//                 });

//                 deleteBtn.setOnAction(event -> {
//                     Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
//                     confirmationAlert.setTitle("Confirm Delete");
//                     confirmationAlert.setHeaderText("Are you sure you want to delete this item?");
//                     confirmationAlert.setContentText("Click OK to delete or Cancel to abort.");
//                     confirmationAlert.showAndWait().ifPresent(response -> {
//                         if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
//                             ObservableList<String> selectedRow = getTableView().getItems().get(getIndex());
//                             getTableView().getItems().remove(getIndex());
                            
//                             if (tableType.equals("COLLEGE")) {
//                                 String collegeId = selectedRow.get(0); 
//                                 displayNoneOnCollegeDeletion(collegeId);
//                             } else if (tableType.equals("PROGRAM")) {
//                                 String programId = selectedRow.get(0); 
//                                 displayNoneOnProgramDeletion(programId);
//                             }
                            
//                             deleteAction.accept(selectedRow);
//                         }
//                     });
//                 });
//             }

//             @Override
//             protected void updateItem(HBox item, boolean empty) {
//                 super.updateItem(item, empty);
//                 if (empty || getIndex() >= getTableView().getItems().size()) {
//                     setGraphic(null);
//                     return;
//                 }
//                 ObservableList<String> row = getTableView().getItems().get(getIndex());
//                 if (row.size() >= rowSize && row.stream().allMatch(cell -> !cell.isBlank())) {
//                     setGraphic(box);
//                 } else {
//                     setGraphic(null);
//                 }
//             }
//         });
//     }

//     // Updates all references to this college in STUDENT and PROGRAM tables to "None"
//     private void displayNoneOnCollegeDeletion(String collegeId) {
//         updateCsvReferenceToNone(StudentFilePath, 6, collegeId);
//         updateCsvReferenceToNone(ProgramFilePath, 2, collegeId);
//         setupStudentTable();
//         setupProgramTable();
//     }

//     private void displayNoneOnProgramDeletion(String programId) {
//         updateCsvReferenceToNone(StudentFilePath, 5, programId);
//         setupStudentTable();
//     }

//     private void displayNewValueOnCollegeEdit(String collegeId){
//         String newValue = collegeCode.getText().trim();
//         updateCsvReferenceToNew(StudentFilePath, 6, collegeId, newValue);
//         updateCsvReferenceToNew(ProgramFilePath, 2, collegeId, newValue);

//         setupStudentTable();
//         setupProgramTable();
//     }

//     private void displayNewValueOnProgramEdit(String programId){
//         String newValue = programCode.getText().trim();
//         updateCsvReferenceToNew(StudentFilePath, 5, programId, newValue);
//         setupStudentTable();
//     }









//     //populate student form for editing
//     private void populateStudentForm(ObservableList<String> selectedRow){
//         if (selectedRow.size() >= 7) {
//             System.out.println("Edit Student: "+selectedRow);
//             editingStudentId = selectedRow.get(0);
//             idNum.setText(selectedRow.get(0));
//             lastName.setText(selectedRow.get(1));
//             firstName.setText(selectedRow.get(2));
//             sex.setValue(selectedRow.get(3));
//             year.setValue(selectedRow.get(4));
//             studentProgram.setText(selectedRow.get(5));
//             studentCollege.setText(selectedRow.get(6));

//             updtStudent.setDisable(false);
//             updtStudent.setVisible(true);
//             regStudent.setDisable(true);
//             regStudent.setVisible(false);
//         } else {
//             showAlert(Alert.AlertType.ERROR, "Error", "Selected student row does not have enough data.");
//         }
//     }

//     private void deleteStudentRowFromCSV(ObservableList<String> rowToDelete) {
//         if (!rowToDelete.isEmpty()) {
//             deleteRowFromCSV(rowToDelete.get(0), StudentFilePath);
//         }
//     }

//     private void populateProgramForm(ObservableList<String> selectedRow){
//         if (selectedRow.size() >= 3) {
//             System.out.println("Edit Program: " + selectedRow);
//             editingProgramId = selectedRow.get(0); 
//             programCode.setText(selectedRow.get(0));
//             programName.setText(selectedRow.get(1));
//             progCollege.setText(selectedRow.get(2));

//             updtProgram.setDisable(false);
//             updtProgram.setVisible(true);
//             regProgram.setDisable(true);
//             regProgram.setVisible(false);
//         } else {
//             showAlert(Alert.AlertType.ERROR, "Error", "Selected program row does not have enough data.");
//         }
//     }

//     private void deleteProgramRowFromCSV(ObservableList<String> rowToDelete) {
//         if (rowToDelete.size() >= 2) {
//             deleteRowFromCSV(rowToDelete.get(1), ProgramFilePath);
//         }
//     }

//     private void populateCollegeForm(ObservableList<String> selectedRow) {
//         if (selectedRow.size() >= 2) {
//             System.out.println("Edit College: " + selectedRow);
//             editingCollegeId = selectedRow.get(0); 
//             collegeCode.setText(selectedRow.get(0));
//             collegeName.setText(selectedRow.get(1));

//             updtCollege.setDisable(false);
//             updtCollege.setVisible(true);
//             regCollege.setDisable(true);
//             regCollege.setVisible(false);
//         } else {
//             showAlert(Alert.AlertType.ERROR, "Error", "Selected college row does not have enough data.");
//         }
//     }

//     private void deleteCollegeRowFromCSV(ObservableList<String> rowToDelete) {
//         if (!rowToDelete.isEmpty()) {
//             deleteRowFromCSV(rowToDelete.get(0), CollegeFilePath);
//         }
//     }

//     private void deleteRowFromCSV(String idToDelete, String filePath) {
//         List<String> lines = new ArrayList<>();
//         boolean found = false;
//         try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//             String line;
//             while ((line = br.readLine()) != null) {
//                 String[] values = line.split(",");
//                 if (values.length > 0 && values[0].trim().equals(idToDelete)) { 
//                     found = true;
//                     continue; // Skip the line to be deleted
//                 }
//                 lines.add(line);
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//             showAlert(Alert.AlertType.ERROR, "Error", "Error reading the CSV file for deletion.");
//             return;
//         }

//         if (found) {
//             try (FileWriter writer = new FileWriter(filePath)) {
//                 for (String line : lines) {
//                     writer.write(line + "\n");
//                 }
//                 showAlert(Alert.AlertType.INFORMATION, "Success", "Item deleted successfully!");
//                 if (filePath.contains("Students.csv")) setupStudentTable();
//                 else if (filePath.contains("Program.csv")) setupProgramTable();
//                 else if (filePath.contains("College.csv")) setupCollegeTable();
//             } catch (IOException e) {
//                 e.printStackTrace();
//                 showAlert(Alert.AlertType.ERROR, "Error", "Error writing to the CSV file after deletion.");
//             }
//         } else {
//             showAlert(Alert.AlertType.WARNING, "Warning", "Item with ID/Code '" + idToDelete + "' not found for deletion.");
//         }
//     }

//     //for displaying csv files on tableview
//     // can be used to reload tables
//     private void setupStudentTable() {
//         studentTable.setItems(loadCSV(StudentFilePath));

//         idColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
//         lastNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
//         firstNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
//         sexColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
//         yearColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));
//         studProgramCodeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(5)));
//         studCollegeCodeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(6)));

//         setupActionButton(studentActionColumn, 7, StudentFilePath, this::populateStudentForm, this::deleteStudentRowFromCSV, "STUDENT");
//     }

//     private void setupProgramTable() {
//         programTable.setItems(loadCSV(ProgramFilePath));
 
//         progProgramCodeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
//         progProgramNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
//         progCollegeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        
//         setupActionButton(programActionColumn, 3, ProgramFilePath, this::populateProgramForm, this::deleteProgramRowFromCSV, "PROGRAM");
//     }

//     private void setupCollegeTable() {
//         collegeTable.setItems(loadCSV(CollegeFilePath));
 
//         colCollegeCodeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
//         colCollegeNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        
//         setupActionButton(collegeActionColumn, 2, CollegeFilePath, this::populateCollegeForm, this::deleteCollegeRowFromCSV, "COLLEGE");
//     }
 





//     //--------------------------FOR SEARCH--------------------------//

//     @FXML private TextField searchStudent;
//     @FXML private TextField searchProgram;
//     @FXML private TextField searchCollege;
//     @FXML private Button searchStudentButton;
//     @FXML private Button searchProgramButton;
//     @FXML private Button searchCollegeButton;

//     @FXML 
//     private void searchStudentDir(){
//         searchAndDisplayCSVResults(
//             StudentFilePath,
//             studentTable,
//             searchStudent.getText(),
//             0, 1, 2  // idNum, lastName, firstName
//         );
//     }

//     @FXML
//     private void searchProgramDir() {
//             searchAndDisplayCSVResults(
//             ProgramFilePath,
//             programTable,
//             searchProgram.getText(),
//             0, 1 // programCode, programName
//         );
//     }

//     @FXML
//     private void searchCollegeDir() {
//         searchAndDisplayCSVResults(
//             CollegeFilePath,
//             collegeTable,
//             searchCollege.getText(),
//             0, 1 // collegeCode, collegeName
//         );
//     }
    
















// //------------------------------INITIALIZE HEEREEE------------------------------//        

//     @Override
//     public void initialize (URL url, ResourceBundle rb){
//         idnumFormat(idNum);
//         sex.getItems().addAll("Male", "Female");
//         year.getItems().addAll("1", "2", "3", "4");

//         populateTextField(studentCollege, "/csv_files/College.csv", 0);
//         populateTextField(studentProgram, "/csv_files/Program.csv", 1);

//         populateTextField(programCode, "/csv_files/Program.csv", 0);
//         populateTextField(programName, "/csv_files/Program.csv", 1);
//         populateTextField(progCollege, "/csv_files/College.csv", 0);

//         populateTextField(collegeCode, "/csv_files/College.csv", 0);
//         populateTextField(collegeName, "/csv_files/College.csv", 1);

//         setupStudentTable();
//         setupProgramTable();
//         setupCollegeTable();

//         // Sync from registrationForms to directories
//         registrationForms.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
//             if (newTab == studentReg) {
//                 directories.getSelectionModel().select(studentDir);
//             } else if (newTab == programReg) {
//                 directories.getSelectionModel().select(programDir);
//             } else if (newTab == collegeReg) {
//                 directories.getSelectionModel().select(collegeDir);
//             }
//         });

//         // Sync from directories to registrationForms
//         directories.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
//             if (newTab == studentDir) {
//                 registrationForms.getSelectionModel().select(studentReg);
//             } else if (newTab == programDir) {
//                 registrationForms.getSelectionModel().select(programReg);
//             } else if (newTab == collegeDir) {
//                 registrationForms.getSelectionModel().select(collegeReg);
//             }
//         });

        
//     }
// }


