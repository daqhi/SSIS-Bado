package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
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
    

    //REGISTRATION FORM
    @FXML private TextField idNum;
    @FXML private TextField lastName;
    @FXML private TextField firstName;
    @FXML private TextField middleName;

    @FXML private ComboBox<String> sex;
    @FXML private ComboBox<String> yearLevel;
    @FXML private ComboBox<String> collegeName;
    @FXML private ComboBox<String> collegeCode; 
    @FXML private ComboBox<String> programName;
    @FXML private ComboBox<String> programCode; 

    @FXML private Button returnToMain;
    @FXML private Button registerStudentButton;
    @FXML private Button registerProgramButton;
    

    //INITIALIZATION
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        if(idNum != null){
            idnumFormat(idNum);
        }

        if (sex != null) {
            sex.getItems().addAll("Male", "Female");
        }

        if (yearLevel != null) {
            yearLevel.getItems().addAll("1", "2", "3", "4");
        }
        
        if (programName != null) {
            populateProgramNameComboBox();
        } 
    }

    private void populateProgramNameComboBox() {
        ObservableList<String> programNames = FXCollections.observableArrayList();

        try {
            File file = new File("C:/Users/eliab/Documents/ELIA FILES/SIS/demo/src/main/java/com/csv files/Program.csv");

            if (!file.exists()) {
                showAlert(Alert.AlertType.ERROR, "ERROR", "Program.csv not found at: " + file.getAbsolutePath());
                return;
            } else if (file.length() == 0) {
                showAlert(Alert.AlertType.WARNING, "WARNING", "Program.csv is empty. Please add data.");
                return;
            }

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 1 && !values[1].isEmpty()) {
                    if (firstLine) {
                        firstLine = false;
                        continue; // Skip header
                    }
                    programNames.add(values[1].trim()); // Add program name
                }
            }
            br.close();

        } catch (IOException e) {
            System.out.println("Error loading CSV: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "ERROR", "Failed to read Program.csv: " + e.getMessage());
            return;
        }

        Platform.runLater(() -> {
            programName.setItems(programNames);
        });
    }


    private void clearForm() {
        idNum.clear();
        lastName.clear();
        firstName.clear();
        middleName.clear();
        sex.setValue(null);
        yearLevel.setValue(null);
        programCode.setValue(null);
        //collegeCode.setValue(null);
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
 
    @FXML private void registerStudent(){
        String id = idNum.getText().trim();
        String last = lastName.getText().trim();
        String first = firstName.getText().trim();
        String middle = middleName.getText().trim();
        String gender = sex.getValue();
        String year = yearLevel.getValue();
        String program = programCode.getValue();
        //String college = collegeCode.getValue();


        if (!id.matches("\\d{0,4}(-\\d{0,4})?")){
            showAlert(Alert.AlertType.ERROR, "Error", "Please follow ID Number format. \nFormat: YYYY-NNNN");
            return;
        } else if (id.isEmpty() || last.isEmpty() || first.isEmpty() || gender == null || year == null || program == null) {
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

        clearForm(); // Clear form fields after submission
    }

}