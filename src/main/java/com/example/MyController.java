package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
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
import javafx.util.StringConverter;


public class MyController implements Initializable{
    // FOR SWITCHING STAGE
    private Stage stage;
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML private void toStudentRegistrationPage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/StudentRegistration.fxml"));
        Parent root = loader.load();

        MyController controller = loader.getController();
        controller.setStage(stage); // Pass the stage

        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML private void toWelcomePage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/WelcomePage.fxml"));
        Parent root = loader.load();

        MyController controller = loader.getController();
        controller.setStage(stage);

        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML private void toProgramRegistration() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ProgramRegistration.fxml"));
        Parent root = loader.load();

        MyController controller = loader.getController();
        controller.setStage(stage);

        stage.setScene(new Scene(root));
        stage.show();
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

    @FXML private Button registerButton;
    @FXML private Button returnToMain;

    @FXML private ComboBox<String> sex;
    @FXML private ComboBox<String> yearLevel;
    @FXML private ComboBox<String> programCode; 
    @FXML private ComboBox<String> programName;

    

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
        
        if (programCode != null) { // Only populate if ComboBox exists
            populateComboBox();
        } 
    }


    private void populateComboBox() {
        ObservableList<String> programCodes = FXCollections.observableArrayList();
        HashMap<String,String> programs = new HashMap<String,String>();

        if (programCode == null) {
            System.out.println("programCode ComboBox is NULL. Skipping population.");
            return;
        }

        try {
            File file = new File("C:/Users/eliab/Documents/ELIA FILES/SIS/demo/src/main/java/com/example/Program.csv");
            
            if (!file.exists()) { 
                showAlert("ERROR", "Program.csv not found at: " + file.getAbsolutePath());
                return;
            } else if (file.length() == 0) {
                showAlert("WARNING", "Program.csv is empty. Please add data.");
                return;
            }
    
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            boolean firstLine = true;
    
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 0 && !values[0].isEmpty()) {
                    if (firstLine) { 
                        firstLine = false;
                        continue; // Skip header
                    }
                    String code = values[0].trim();
                    String name = values[1].trim();

                    programCodes.add(code); // Add program code
                    programs.put(code, name);
                }
            }
            br.close();
    
        } catch (IOException e) {
            System.out.println("Error loading CSV: " + e.getMessage());
            e.printStackTrace();  
            showAlert("ERROR", "Failed to read Program.csv: " + e.getMessage());
            return;
        }
    
        // Update the ComboBox on the JavaFX thread
        Platform.runLater(() -> {
            programCode.setItems(programCodes);
            programCode.setVisibleRowCount(5);
            programCode.setConverter(new StringConverter<String>() {
                @Override
                public String toString(String code) {
                    return programs.get(code); 
                }
    
                @Override
                public String fromString(String code) {
                    return programs.get(code); 
                }
            });
        
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
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