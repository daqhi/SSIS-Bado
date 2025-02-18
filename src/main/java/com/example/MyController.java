package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class MyController implements Initializable{
    // FOR SWITCHING STAGE
    private Stage stage;
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML private void toWelcomePage() throws IOException {
        loadScene("C:/Users/eliab/Documents/ELIA FILES/SIS/demo/src/main/java/com/fxml files/WelcomePage.fxml");
    }
    @FXML private void toRegistrationPage() throws IOException {
        loadScene("/com/fxml files/RegistrationPage.fxml");
    }
    @FXML private void toStudentDirectory() throws IOException {
        loadScene("/com/fxml files/StudentDirectory.fxml");
    }
    //edit pani create programm directory @ scenebuilder
    @FXML private void toProgramDirectory() throws IOException {
        loadScene("/com/fxml files/WelcomePage.fxml");
    }


    //WELCOME PAGE
    @FXML private Button registerNewStudent;
    @FXML private Button studentDirectory;
    @FXML private Button registerNewProgram;
    

    //STUDENT REGISTRATION FORM
    @FXML private TextField idNum;
    @FXML private TextField lastName;
    @FXML private TextField firstName;
    @FXML private TextField middleName;

    @FXML private ComboBox<String> sex;
    @FXML private ComboBox<String> yearLevel;
    @FXML private ComboBox<String> studentProgramName;
    @FXML private ComboBox<String> studentProgramCode; 

    //PROGRAM REGISTRATION FORM
    @FXML private ComboBox<String> programName;
    @FXML private ComboBox<String> programCode; 
    @FXML private ComboBox<String> collegeName;
    @FXML private ComboBox<String> collegeCode; 

    //COLLEGE REGISTRATION FORM
    @FXML private ComboBox<String> colCollegeName;
    @FXML private ComboBox<String> colCollegeCode;


    @FXML private Button returnToMain;
    @FXML private Button registerStudentButton;
    @FXML private Button registerProgramButton;
    @FXML private Button registerCollegeButton;
    

    //INITIALIZATION
    @FXML public void initialize(URL url, ResourceBundle rb) {
        if(idNum != null){
            //fix yyy-nnnn bug
            idnumFormat(idNum);
        }

        if (sex != null) {
            sex.getItems().addAll("Male", "Female");
        }

        if (yearLevel != null) {
            yearLevel.getItems().addAll("1", "2", "3", "4");
        }
        
        if (studentProgramName != null) {
            populateComboBoxFromCSV(studentProgramName, "src/main/java/com/csv files/Program.csv", 1);
        } 

        if (programName != null){
            //make it automatic mo select sa pikas
            //fix ngano di maka use ug space
            //fix filtering
            populateComboBoxFromCSV(programName, "src/main/java/com/csv files/Program.csv", 1);
            populateComboBoxFromCSV(programCode, "src/main/java/com/csv files/Program.csv", 0);
            populateComboBoxFromCSV(collegeName, "src/main/java/com/csv files/College.csv", 1);
            populateComboBoxFromCSV(collegeCode, "src/main/java/com/csv files/College.csv", 0);
        }

        if(collegeName != null){
            populateComboBoxFromCSV(colCollegeCode, "src/main/java/com/csv files/College.csv", 0);
            populateComboBoxFromCSV(colCollegeName, "src/main/java/com/csv files/College.csv", 1);
        }
    }

    private void populateComboBoxFromCSV(ComboBox<String> comboBox, String filePath, int columnIndex) {
        ObservableList<String> originalItems = FXCollections.observableArrayList();
        Map<String, String> itemMap = new HashMap<>(); // Store for faster lookup if needed.

        comboBox.setEditable(true);

        try {
            File file = new File(filePath);
            if (!file.exists() || file.length() == 0) {
                return;
            }

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > columnIndex && !values[columnIndex].isEmpty()) {
                    if (firstLine) {
                        firstLine = false;
                        continue; // Skip header
                    }
                    String item = values[columnIndex].trim();
                    originalItems.add(item);
                    if(columnIndex == 0 && values.length > 1){
                        itemMap.put(item, values[1].trim());
                    }
                }
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "ERROR", "Failed to read " + filePath + ": " + e.getMessage());
            return;
        }

        comboBox.setItems(originalItems);
        TextField editor = comboBox.getEditor();
        editor.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                comboBox.setItems(originalItems);
            } else {
                ObservableList<String> filteredList = FXCollections.observableArrayList();
                for (String item : originalItems) {
                    if (item.toLowerCase().contains(newValue.toLowerCase())) {
                        filteredList.add(item);
                    }
                }
                comboBox.setItems(filteredList);
            }
        });

        editor.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                comboBox.setItems(originalItems);
            } else {
                ObservableList<String> filteredList = FXCollections.observableArrayList();
                for (String item : originalItems) {
                    if (item.toLowerCase().contains(newValue.toLowerCase())) {
                        filteredList.add(item);
                    }
                }
                comboBox.setItems(filteredList);
                if (!comboBox.isShowing()) { // Add this line
                    comboBox.show();
                }
            }
        });
    }


    @FXML private void registerStudent(){
        String id = idNum.getText().trim();
        String last = lastName.getText().trim();
        String first = firstName.getText().trim();
        String middle = middleName.getText().trim();
        String gender = sex.getValue();
        String year = yearLevel.getValue();
        String program = studentProgramName.getValue();


        if (!id.matches("\\d{0,4}(-\\d{0,4})?")){
            showAlert(Alert.AlertType.ERROR, "Error", "Please follow ID Number format. \nFormat: YYYY-NNNN");
            return;
        } else if (id.isEmpty() || last.isEmpty() || first.isEmpty() || middle.isEmpty() || gender == null || year == null || program == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
            return;
        } 
        
        String csvRow = String.format("%s,%s,%s,%s,%s,%s,%s\n", id, last, first, middle, gender, year, program);

        try (PrintWriter writer = new PrintWriter(new FileWriter("C:/Users/eliab/Documents/ELIA FILES/SIS/demo/src/main/java/com/csv files/students.csv", true))) {
            writer.append(csvRow);
            showAlert(Alert.AlertType.CONFIRMATION, "Success", "Student registered successfully!");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not save data: " + e.getMessage());
        }

        clearStudentForm();
    }

    @FXML private void registerProgram(){
        String colName = collegeName.getValue();
        String colCode = collegeCode.getValue();
        String progName = programName.getValue();
        String progCode = programCode.getValue();


        if (colName == null || colCode == null || progName == null || progCode == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
            return;
        } 
        
        String csvRow = String.format("%s,%s,%s\n", colName, colCode, progName, progCode);

        try (PrintWriter writer = new PrintWriter(new FileWriter("C:/Users/eliab/Documents/ELIA FILES/SIS/demo/src/main/java/com/csv files/Program.csv", true))) {
            writer.append(csvRow);
            showAlert(Alert.AlertType.CONFIRMATION, "Success", "Program registered successfully!");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not save data: " + e.getMessage());
        }

        clearProgramForm(); // Clear form fields after submission
    }

    @FXML private void registerCollege(){
        String colName = colCollegeName.getValue();
        String colCode = colCollegeCode.getValue();


        if (colName == null || colCode == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
            return;
        } 
        
        String csvRow = String.format("%s,%s\n", colName, colCode);

        try (PrintWriter writer = new PrintWriter(new FileWriter("C:/Users/eliab/Documents/ELIA FILES/SIS/demo/src/main/java/com/csv files/College.csv", true))) {
            writer.append(csvRow);
            showAlert(Alert.AlertType.CONFIRMATION, "Success", "College registered successfully!");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not save data: " + e.getMessage());
        }

        clearCollegeForm(); // Clear form fields after submission
    }

    private void clearStudentForm() {
        idNum.clear();
        lastName.clear();
        firstName.clear();
        middleName.clear();
        sex.setValue(null);
        yearLevel.setValue(null);
        studentProgramCode.setValue(null);
    }

    private void clearProgramForm() {
        collegeCode.setValue(null);
        collegeName.setValue(null);
        programCode.setValue(null);
        programName.setValue(null);
    }

    private void clearCollegeForm() {
        colCollegeCode.setValue(null);
        colCollegeName.setValue(null);
    }

    private void loadScene(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        MyController controller = loader.getController();
        controller.setStage(stage);
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void idnumFormat(TextField textfield) {
        textfield.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty() || !newValue.matches("\\d{0,4}(-\\d{0,4})?")) {
                textfield.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            } else {
                textfield.setStyle(""); // Reset style if input is valid
            }
        });
    }
 
    
}