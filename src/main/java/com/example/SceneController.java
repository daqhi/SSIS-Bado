package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;


public class SceneController extends MainController implements Initializable{

    private String StudentFilePath = "C:/Users/eliab/Documents/ELIA FILES/SIS/demo/src/main/resources/csv_files/Students.csv";
    private String ProgramFilePath = "C:/Users/eliab/Documents/ELIA FILES/SIS/demo/src/main/resources/csv_files/Program.csv";
    private String CollegeFilePath = "C:/Users/eliab/Documents/ELIA FILES/SIS/demo/src/main/resources/csv_files/College.csv";

    //--------------------------REGISTRATION FORMS--------------------------//

    //TABPANE FOR REGISTRATION
    @FXML private TabPane registrationForms;
        @FXML private Tab studentReg;
            @FXML private TextField idNum;
            @FXML private TextField firstName;
            @FXML private TextField lastName;
            @FXML private ComboBox<String> year;
            @FXML private ComboBox<String> sex;
            @FXML private ComboBox<String> studentProgramName;
            @FXML private Button regStudent;
            @FXML private Button updtStudent;
            @FXML private Button clearStudentFormButton;
            private String editingStudentId;
        @FXML private Tab programReg;
            @FXML private ComboBox<String> progCollege;
            @FXML private TextField programCode;
            @FXML private TextField programName;
            @FXML private Button regProgram;
            @FXML private Button updtProgram;
            @FXML private Button clearProgramFormButton;
            private String editingProgramId;
        @FXML private Tab collegeReg;
            @FXML private TextField collegeCode;
            @FXML private TextField collegeName;
            @FXML private Button regCollege;
            @FXML private Button updtCollege;
            @FXML private Button clearCollegeFormButton;
            private String editingCollegeId;

    @FXML private void registerStudent() {
        String id = idNum.getText().trim();
        String last = lastName.getText().trim();
        String first = firstName.getText().trim();
        String gender = sex.getValue();
        String yearLvl = year.getValue();
        String program = studentProgramName.getValue();
        
        if (isEmptyField(id, last, first, gender, yearLvl, program)) { 
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
        } else if (!id.matches("\\d{4}-\\d{4}")) { 
            showAlert(Alert.AlertType.ERROR, "Error", "Please follow ID Number format.\nFormat: YYYY-NNNN");
        } else if (!inputExists(program, "/csv_files/Program.csv", 0)){
            showAlert(Alert.AlertType.ERROR, "Error", "Program '" + program + "' does not exist. Please register it first.");
        } else if (isValueTakenInCSV(id, 0, StudentFilePath)){ // checks for duplicates
            showAlert(Alert.AlertType.ERROR, "Error", "ID Number already exists. \nFailed to register student.");
        } else { // asks user for confirmation before saving
            
            showAlertAndRegister(
                String.format("%s,%s,%s,%s,%s,%s\n", idNum.getText().trim(), lastName.getText().trim(), firstName.getText().trim(), sex.getValue(), year.getValue(), studentProgramName.getValue()), //dapat program code ang mudisplay sa table
                StudentFilePath,
                "Student registered successfully!",
                this::clearStudentForm);
            setupStudentTable();
        }              
    }

    @FXML private void registerProgram() {
        String college = progCollege.getValue();
        String progName = programName.getText();
        String progCode = programCode.getText();

        if (isEmptyField(college, progName, progCode)) { //shows alert if save is clicked and program fields are empty
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
            System.out.println("request to fill required fields");
        } else if (!inputExists(college, "/csv_files/College.csv", 0)) { //shows alert if college do not exist 
            showAlert(Alert.AlertType.ERROR, "Error", "College code " + college + " does not exist. Please register it first.");
            System.out.println("college do not exist");   
        } else if ((isValueTakenInCSV(progCode, 0, ProgramFilePath)) &&
                   (isValueTakenInCSV(progCode, 0, ProgramFilePath))){ // checks for duplicates in progCode
            showAlert(Alert.AlertType.ERROR, "Error", "Program Code and Program Name already exists.\nFailed to register program code and program name.");
        } else if (isValueTakenInCSV(progCode, 0, ProgramFilePath)){ // checks for duplicates in progCode
            showAlert(Alert.AlertType.ERROR, "Error", "Program Code already exists.\nFailed to register program code.");
        } else if (isValueTakenInCSV(progName, 1, ProgramFilePath)){ // checks for duplicates in progName
            showAlert(Alert.AlertType.ERROR, "Error", "Program Name already exists.\nFailed to register program name.");
        } else { //shows confirmation alert to save data
            showAlertAndRegister(
                String.format("%s,%s,%s\n", programCode.getText(), programName.getText(), progCollege.getValue()),
                ProgramFilePath,
                "Program registered successfully!",
                this::clearProgramForm);
            setupProgramTable();
        }
    }

    @FXML private void registerCollege() {
        String colCode = collegeCode.getText();
        String colName = collegeName.getText();

        if (isEmptyField(colName, colCode)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
        } else if ((isValueTakenInCSV(colCode, 0, CollegeFilePath)) && (isValueTakenInCSV(colName, 1, CollegeFilePath))){
            showAlert(Alert.AlertType.ERROR, "Error", "College code already exists.\nFailed to register college code.");
        } else if (isValueTakenInCSV(colCode, 0, CollegeFilePath)){
            showAlert(Alert.AlertType.ERROR, "Error", "College code and college name already exists.\nFailed to register college code and college name.");
        } else if (isValueTakenInCSV(colName, 1, CollegeFilePath)){
            showAlert(Alert.AlertType.ERROR, "Error", "College Name already exists.\nFailed to register college name.");
        } else {
            showAlertAndRegister(
                String.format("%s,%s\n", collegeCode.getText(), collegeName.getText()),
                CollegeFilePath,
                "College registered successfully!",
                this::clearCollegeForm);
                setupCollegeTable();
        }        
    }

    @FXML private void updateStudent(){
        String id = idNum.getText().trim();
        String last = lastName.getText().trim();
        String first = firstName.getText().trim();
        String gender = sex.getValue();
        String yearLvl = year.getValue();
        String program = studentProgramName.getValue();
        
        if (isEmptyField(id, last, first, gender, yearLvl, program)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
            return;
        } else if (!id.matches("\\d{4}-\\d{4}")) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please follow ID Number format.\nFormat: YYYY-NNNN");
            return;
        } 

        String oldId = editingStudentId;

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Edit");
        confirmationAlert.setHeaderText("Are you sure you want to edit this student?");
        confirmationAlert.setContentText("Click OK to proceed or Cancel to abort.");
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                boolean updated = updateStudentInCSV(oldId, id, last, first, gender, yearLvl, program, StudentFilePath);
                //
                if (updated) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Student updated successfully!");
                    clearStudentForm();
                    editingStudentId = null;
                    updtStudent.setDisable(true);
                    updtStudent.setVisible(false);
                    regStudent.setDisable(false);
                    regStudent.setVisible(true);
                    setupStudentTable(); // reload student table
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update student.");
                } 
            }
        });
    }

    @FXML private void updateProgram() {
        String college = progCollege.getValue().trim();
        String code = programCode.getText().trim();
        String name = programName.getText().trim();

        if (isEmptyField(college, code, name)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
            return;
        } 
        String oldCode = editingProgramId;

        if(inputExists(code, ProgramFilePath, 0)){
            showAlert(Alert.AlertType.ERROR, "Error", "Program code '" + oldCode + "' already exist.");
        } else if (inputExists(name, ProgramFilePath, 1)){
            showAlert(Alert.AlertType.ERROR, "Error", "Program name '" + name + "' already exist");
        } else {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Edit");
            confirmationAlert.setHeaderText("Are you sure you want to edit this program?");
            confirmationAlert.setContentText("Click OK to proceed or Cancel to abort.");
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    if (!inputExists(college, "/csv_files/College.csv", 0)) {
                        showAlert(Alert.AlertType.ERROR, "Error", "College code " + college + " does not exist. Please register it first.");
                        clearCollegeForm();
                    } else {
                        boolean programUpdated = updateProgramInCSV(oldCode, college, code, name, ProgramFilePath);
                        if (programUpdated) {
                            displayNewValueOnProgramEdit(oldCode);
                            updateStudentColleges(StudentFilePath, code, college);
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Program updated successfully!");
                            clearProgramForm();
                            editingProgramId = null;
                            updtProgram.setDisable(true);
                            updtProgram.setVisible(false);
                            regProgram.setDisable(false);
                            regProgram.setVisible(true);
                            setupProgramTable();
                            setupStudentTable();
                            updateComboBoxItems(studentProgramName, ProgramFilePath, 1);
                            populateComboBox(studentProgramName, ProgramFilePath, 1);
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update program.");
                        }
                    }
                } else {
                    System.out.println("Update canceled by user.");
                }        
            });
        }
    }

    @FXML private void updateCollege() {
        String newCode = collegeCode.getText().trim();
        String name = collegeName.getText().trim();

        if (isEmptyField(newCode, name)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
        }

        String oldCode = editingCollegeId;

        if(inputExists(newCode, CollegeFilePath, 0)){
            showAlert(Alert.AlertType.ERROR, "Error", "College code '" + oldCode + "' already exist.");
        } else if (inputExists(newCode, CollegeFilePath, 1)){
            showAlert(Alert.AlertType.ERROR, "Error", "College name '" + name + "' already exist");
        } else {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Edit");
            confirmationAlert.setHeaderText("Are you sure you want to edit this college?");
            confirmationAlert.setContentText("Click OK to proceed or Cancel to abort.");
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    boolean updated = updateCollegeInCSV(oldCode, newCode, name, CollegeFilePath);{
                        if (updated) {
                            displayNewValueOnCollegeEdit(oldCode);
                            showAlert(Alert.AlertType.INFORMATION, "Success", "College updated successfully!");
                            clearCollegeForm();
                            editingCollegeId = null;
                            updtCollege.setDisable(true);
                            updtCollege.setVisible(false);
                            regCollege.setDisable(false);
                            regCollege.setVisible(true);
                            setupCollegeTable();
                            updateComboBoxItems(progCollege, CollegeFilePath, 0);
                            populateComboBox(progCollege, CollegeFilePath, 0);
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update college.");
                        }
                    }
                }
            });   
        }
    }

    private boolean updateStudentInCSV(String oldId, String newId, String lastName, String firstName, String sex, String year, String programName, String filePath) {
        List<String> lines = new ArrayList<>();
        boolean found = false;
        String collegeCode = null;
        String programCode = null;

        try (BufferedReader br = new BufferedReader(new FileReader(ProgramFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 0) {
                    if (programName.equals(values[1].trim())) { 
                        String college = values[2].trim();
                        collegeCode = college; 
                        programCode = values[0].trim(); 
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 0) {
                    String currentId = values[0].trim();
                    
                    if (currentId.equals(oldId)) { // replaces the line with the updated info
                        lines.add(String.format("%s,%s,%s,%s,%s,%s,%s", newId, lastName, firstName, sex, year, programCode,collegeCode));
                        found = true;
                    } else {
                        lines.add(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    
        // only rewrite the file if the student was found
        if (found) {
            try (FileWriter writer = new FileWriter(filePath)) {
                for (String l : lines) {
                    writer.write(l + "\n");
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private boolean updateProgramInCSV(String oldCode, String college, String code, String name, String filePath) {
        List<String> lines = new ArrayList<>();
        boolean found = false;
        String newProgName = programName.getText().trim();

        String programName = getProgramName(oldCode); // get program name from csv file
        
        if(programName == null){
            showAlert(Alert.AlertType.ERROR, "Error", "Program name for code '" + oldCode + "' could not be found.");
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 0) {
                    String currentId = values[0].trim();
    
                    if (currentId.equals(code) && !currentId.equals(oldCode)) {
                        showAlert(Alert.AlertType.ERROR, "Duplicate ID", "Program Code already exists\nFailed to update program.");
                        return false;
                    }
    
                    if (currentId.equals(oldCode)) {
                        // Replace with updated info, using programName instead of code
                        lines.add(String.format("%s,%s,%s", code, newProgName, college));
                        found = true;
                    } else {
                        lines.add(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (found) {
            try (FileWriter writer = new FileWriter(filePath)) {
                for (String l : lines) {
                    writer.write(l + "\n");
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    //updates college in csv file
    private boolean updateCollegeInCSV(String oldCode, String code, String name, String filePath) {
        List<String> lines = new ArrayList<>();
        boolean found = false;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 0 && values[0].trim().equals(oldCode)) { //dili apilon ang column 1 sa csv
                    lines.add(String.format("%s,%s", code, name)); 
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        }  catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (found) {
            try (FileWriter writer = new FileWriter(filePath)) {
                for (String l : lines) {
                    writer.write(l + "\n");
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private void updateStudentColleges(String studentFilePath, String programCode, String newCollegeCode) {
        try {
            Path path = Paths.get(studentFilePath);
            List<String> lines = Files.readAllLines(path);
            List<String> updatedLines = new ArrayList<>();
            boolean changed = false;

            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    if (parts[5].trim().equals(programCode)) { // column 5 = Program
                        parts[6] = newCollegeCode;             // column 6 = College
                        changed = true;
                    }
                }
                updatedLines.add(String.join(",", parts));
            }

            if (changed) {
                Files.write(path, updatedLines);
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update student college references.");
        }
    }

    private String getProgramName(String programCode) {
        try (InputStream is = getClass().getResourceAsStream("/csv_files/Program.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 2 && values[0].trim().equals(programCode)) {
                    return values[1].trim(); // return program name
                }
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }
    


    private void clearStudentForm() {
        idNum.clear();
        lastName.clear();
        firstName.clear();
        sex.setValue(null);
        year.setValue(null);
        studentProgramName.setValue(null);
    }

    private void clearProgramForm() {
        collegeCode.clear();
        programCode.clear();
        programName.clear();
    }

    private void clearCollegeForm() {
        collegeCode.clear();
        collegeName.clear();
    }

    private void idnumFormat(TextField textfield) {
        textfield.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty() || !newValue.matches("\\d{4}-\\d{4}")) {
                textfield.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 2px;");
            } else {
                textfield.setStyle("");
            }
        });
    }

    // checks for duplicates
    private boolean isValueTakenInCSV(String valueToCheck, int columnIndex, String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > columnIndex && values[columnIndex].trim().equalsIgnoreCase(valueToCheck.trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // checks if fields are empty
    protected boolean isEmptyField(String... fields) {
        for (String field : fields) {
            if (field == null || field.isBlank()) return true;
        }
        return false;
    }

    //for clear button
    @FXML private void clearSForm() {
        clearStudentForm();
    }

    @FXML private void clearPForm() {
        clearProgramForm();
    }

    @FXML private void clearCForm() {
        clearCollegeForm();
    }





    //--------------------------DIRECTORIES--------------------------//

    //TABPANE FOR TABLES
    @FXML private TabPane directories;
        @FXML private TableView<ObservableList<String>> studentTable;
        @FXML private Tab studentDir;
            @FXML private TableColumn<ObservableList<String>, String> idColumn;
            @FXML private TableColumn<ObservableList<String>, String> firstNameColumn;
            @FXML private TableColumn<ObservableList<String>, String> lastNameColumn;
            @FXML private TableColumn<ObservableList<String>, String> yearColumn;
            @FXML private TableColumn<ObservableList<String>, String> sexColumn;
            @FXML private TableColumn<ObservableList<String>, String> studCollegeCodeColumn;
            @FXML private TableColumn<ObservableList<String>, String> studProgramCodeColumn;
            @FXML private TableColumn<ObservableList<String>, HBox> studentActionColumn;
        @FXML private TableView<ObservableList<String>> programTable;
        @FXML private Tab programDir;
            @FXML private TableColumn<ObservableList<String>, String> progCollegeColumn;
            @FXML private TableColumn<ObservableList<String>, String> progProgramCodeColumn;
            @FXML private TableColumn<ObservableList<String>, String> progProgramNameColumn;
            @FXML private TableColumn<ObservableList<String>, HBox> programActionColumn;
        @FXML private TableView<ObservableList<String>> collegeTable;
        @FXML private Tab collegeDir;
            @FXML private TableColumn<ObservableList<String>, String> colCollegeCodeColumn;
            @FXML private TableColumn<ObservableList<String>, String> colCollegeNameColumn;
            @FXML private TableColumn<ObservableList<String>, HBox> collegeActionColumn;

    //for reading csv files and displaying them in the table
    private ObservableList<ObservableList<String>> loadCSV(String filePath) {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            
            boolean isFirstLine = true; // Skip the first line (header)

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                   isFirstLine = false;
                   continue; // skip the header row
                }

                if (line.trim().isEmpty()) continue;

                String[] split = line.split(",");

                if (split.length < 7 && filePath.contains("Students")) continue;
                if (split.length < 3 && filePath.contains("Program")) continue;
                if (split.length < 2 && filePath.contains("College")) continue;

                ObservableList<String> row = FXCollections.observableArrayList(Arrays.asList(split));
                data.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private void setupActionButton(
        TableColumn<ObservableList<String>, HBox> actionColumn, 
        int rowSize, 
        String filePath, 
        Consumer<ObservableList<String>> editAction, 
        Consumer<ObservableList<String>> deleteAction,
        String tableType) { 
    
        actionColumn.setCellFactory(col -> new TableCell<ObservableList<String>, HBox>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox box = new HBox(3, editBtn, deleteBtn);

            {
                box.setPadding(new Insets(0, 5, 0, 5)); //spacing

                editBtn.setOnAction(event -> {
                    ObservableList<String> selectedRow = getTableView().getItems().get(getIndex());
                    System.out.println("Selected row: " + selectedRow); //pag click sa edit button, i print ang selected row
                    if (tableType.equals("STUDENT")) {
                        populateStudentForm(selectedRow);
                    } else if (tableType.equals("COLLEGE")) {
                        populateCollegeForm(selectedRow);
                    } else if (tableType.equals("PROGRAM")) {
                        populateProgramForm(selectedRow);   
                    }
                });

                deleteBtn.setOnAction(event -> {
                    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmationAlert.setTitle("Confirm Delete");
                    confirmationAlert.setHeaderText("Are you sure you want to delete this item?");
                    confirmationAlert.setContentText("Click OK to delete or Cancel to abort.");
                    confirmationAlert.showAndWait().ifPresent(response -> {
                        if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                            ObservableList<String> selectedRow = getTableView().getItems().get(getIndex());
                            getTableView().getItems().remove(getIndex());
                            
                            if (tableType.equals("STUDENT")) {
                                deleteStudentRowFromCSV(selectedRow);
                            } else if (tableType.equals("PROGRAM")) {
                                deleteProgramRowFromCSV(selectedRow);
                            } else if (tableType.equals("COLLEGE")) {
                                deleteCollegeRowFromCSV(selectedRow);
                            }                            
                            deleteAction.accept(selectedRow);
                        }
                    });
                });
            }

            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    return;
                }
                ObservableList<String> row = getTableView().getItems().get(getIndex());
                if (row.size() >= rowSize && row.stream().allMatch(cell -> !cell.isBlank())) {
                    setGraphic(box);
                } else {
                    setGraphic(null);
                }
            }
        });
    }

    // Updates all references to this college in STUDENT and PROGRAM tables to "None"
    private void displayNoneOnCollegeDeletion(String collegeId) {
        updateCsvReferenceToNone(StudentFilePath, 6, collegeId);
        updateCsvReferenceToNone(ProgramFilePath, 2, collegeId);
        setupStudentTable();
        setupProgramTable();
    }

    private void displayNoneOnProgramDeletion(String programId) {
        updateCsvReferenceToNone(StudentFilePath, 5, programId);
        setupStudentTable();
    }

    private void displayNewValueOnCollegeEdit(String collegeId){
        String newValue = collegeCode.getText().trim();
        updateCsvReferenceToNew(StudentFilePath, 6, collegeId, newValue); //if maedit and college, maedit pod and student collegeid
        updateCsvReferenceToNew(ProgramFilePath, 2, collegeId, newValue); //if 
        setupStudentTable();
        setupProgramTable();
    }

    private void displayNewValueOnProgramEdit(String programId){
        String newValue = programCode.getText().trim();
        updateCsvReferenceToNew(StudentFilePath, 5, programId, newValue);
        setupStudentTable();
    }

   
    //populate student form for editing
    private void populateStudentForm(ObservableList<String> selectedRow){
        if (selectedRow.size() >= 7) {
            System.out.println("Edit Student: "+selectedRow);
            editingStudentId = selectedRow.get(0);
            idNum.setText(selectedRow.get(0));
            lastName.setText(selectedRow.get(1));
            firstName.setText(selectedRow.get(2));
            sex.setValue(selectedRow.get(3));
            year.setValue(selectedRow.get(4));
            studentProgramName.setValue(selectedRow.get(5));

            updtStudent.setDisable(false);
            updtStudent.setVisible(true);
            regStudent.setDisable(true);
            regStudent.setVisible(false);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Selected student row does not have enough data.");
        }
    }

    private void populateProgramForm(ObservableList<String> selectedRow){
        if (selectedRow.size() >= 3) {
            System.out.println("Edit Program: " + selectedRow);
            editingProgramId = selectedRow.get(0); 
            programCode.setText(selectedRow.get(0));
            programName.setText(selectedRow.get(1));
            progCollege.setValue(selectedRow.get(2));

            updtProgram.setDisable(false);
            updtProgram.setVisible(true);
            regProgram.setDisable(true);
            regProgram.setVisible(false);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Selected program row does not have enough data.");
        }
    }

    private void populateCollegeForm(ObservableList<String> selectedRow) {
        if (selectedRow.size() >= 2) {
            System.out.println("Edit College: " + selectedRow);
            editingCollegeId = selectedRow.get(0); 
            collegeCode.setText(selectedRow.get(0));
            collegeName.setText(selectedRow.get(1));

            updtCollege.setDisable(false);
            updtCollege.setVisible(true);
            regCollege.setDisable(true);
            regCollege.setVisible(false);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Selected college row does not have enough data.");
        }
    }

    private void deleteStudentRowFromCSV(ObservableList<String> rowToDelete) {
        if (!rowToDelete.isEmpty()) {
            deleteRowFromCSV(rowToDelete.get(0), StudentFilePath);
        }
    }

    private void deleteProgramRowFromCSV(ObservableList<String> rowToDelete) {
        if (rowToDelete.size() >= 2) {
            deleteRowFromCSV(rowToDelete.get(0), ProgramFilePath);
        }
    }

    private void deleteCollegeRowFromCSV(ObservableList<String> rowToDelete) {
        if (!rowToDelete.isEmpty()) {
            deleteRowFromCSV(rowToDelete.get(0), CollegeFilePath);
        }
    }

    private void deleteRowFromCSV(String idToDelete, String filePath) {
        List<String> lines = new ArrayList<>();
        boolean found = false;
    
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 0 && values[0].trim().equals(idToDelete)) { 
                    found = true;
                    continue; // Skip the line to be deleted
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error reading the CSV file for deletion.");
            return;
        }
    
        if (found) {
            try (FileWriter writer = new FileWriter(filePath)) {
                for (String line : lines) {
                    writer.write(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Error writing to the CSV file after deletion.");
                return;
            }
    
            // Handle cascading update if a college was deleted
            if (filePath == CollegeFilePath) {
                updateCsvReferenceToNone(StudentFilePath, 6, idToDelete);
                updateCsvReferenceToNone(ProgramFilePath, 2, idToDelete);  
                setupCollegeTable();
                setupProgramTable();
                setupStudentTable();
            } else if (filePath == ProgramFilePath) {
                updateCsvReferenceToNone(StudentFilePath, 5, idToDelete);
                setupProgramTable();
                setupStudentTable();
            }
            showAlert(Alert.AlertType.INFORMATION, "Success", "Item deleted successfully!");
        } 
    }
    

    //for displaying csv files on tableview
    // can be used to reload tables
    //program name mugawas sa register
    //program code mudisplay sa table
    private void setupStudentTable() {
        ObservableList<ObservableList<String>> studentData = loadCSV(StudentFilePath);
        studentTable.setItems(studentData);

        Map<String, String> programMap = loadProgramMap(ProgramFilePath);

        idColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        lastNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        firstNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        sexColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
        yearColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));

        
        studProgramCodeColumn.setCellValueFactory(data -> {
            String code = data.getValue().get(5);
            String name = programMap.getOrDefault(code, code); 
            return new SimpleStringProperty(name);
        });

        studCollegeCodeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(6)));

        setupActionButton(studentActionColumn, 7, StudentFilePath, this::populateStudentForm, this::deleteStudentRowFromCSV, "STUDENT");
    }


    private void setupProgramTable() {
        programTable.setItems(loadCSV(ProgramFilePath));
 
        progProgramCodeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        progProgramNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        progCollegeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        
        setupActionButton(programActionColumn, 3, ProgramFilePath, this::populateProgramForm, this::deleteProgramRowFromCSV, "PROGRAM");
    }

    private void setupCollegeTable() {
        collegeTable.setItems(loadCSV(CollegeFilePath));
 
        colCollegeCodeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        colCollegeNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        
        setupActionButton(collegeActionColumn, 2, CollegeFilePath, this::populateCollegeForm, this::deleteCollegeRowFromCSV, "COLLEGE");
    }
 





    //--------------------------FOR SEARCH--------------------------//

    @FXML private TextField searchStudent;
    @FXML private TextField searchProgram;
    @FXML private TextField searchCollege;
    @FXML private Button searchStudentButton;
    @FXML private Button searchProgramButton;
    @FXML private Button searchCollegeButton;

    @FXML private void searchStudentDir(){
        searchAndDisplayCSVResults(StudentFilePath, studentTable, searchStudent.getText(),0, 1, 2, 3, 4, 5, 6);
    }

    @FXML private void searchProgramDir() {
        searchAndDisplayCSVResults(ProgramFilePath, programTable, searchProgram.getText(), 0, 1, 2);
    }

    @FXML private void searchCollegeDir() {
        searchAndDisplayCSVResults(CollegeFilePath, collegeTable, searchCollege.getText(), 0, 1);
    }
    







//------------------------------INITIALIZE HEEREEE------------------------------//        

    @Override
    public void initialize (URL url, ResourceBundle rb){
        idnumFormat(idNum);
        sex.getItems().addAll("Male", "Female");
        year.getItems().addAll("1", "2", "3", "4");

        populateComboBox(studentProgramName, ProgramFilePath, 1);
        //updateComboBoxItems(studentProgramName, ProgramFilePath, 1);

        populateTextField(programCode, "/csv_files/Program.csv", 0);
        populateTextField(programName, "/csv_files/Program.csv", 1);
        populateComboBox(progCollege, CollegeFilePath, 0);

        populateTextField(collegeCode, "/csv_files/College.csv", 0);
        populateTextField(collegeName, "/csv_files/College.csv", 1);

        setupStudentTable();
        setupProgramTable();
        setupCollegeTable();

        // Sync from registrationForms to directories
        registrationForms.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == studentReg) {
                directories.getSelectionModel().select(studentDir);
            } else if (newTab == programReg) {
                directories.getSelectionModel().select(programDir);
            } else if (newTab == collegeReg) {
                directories.getSelectionModel().select(collegeDir);
            }
        });

        // Sync from directories to registrationForms
        directories.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == studentDir) {
                registrationForms.getSelectionModel().select(studentReg);
            } else if (newTab == programDir) {
                registrationForms.getSelectionModel().select(programReg);
            } else if (newTab == collegeDir) {
                registrationForms.getSelectionModel().select(collegeReg);
            }
        });
    }
}


