package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public abstract class MainController {

    protected Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    protected void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected boolean inputExists(String textfieldString, String resourcePath, int columnIndex) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.out.println("Resource not found: " + resourcePath);
                return false;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (firstLine) {
                    firstLine = false; // Skip header
                    continue;
                }
                if (values.length > columnIndex && values[columnIndex].trim().equalsIgnoreCase(textfieldString)) {
                    return true; // Program exists
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // Program not found
    }

    // to populate textfields for edition
    protected  void populateTextField(TextField textField, String resourcePath, int columnIndex) {
        //System.out.println("read into a list of items the csv file: "+resourcePath);
        List<String> originalItems = new ArrayList<>();

        try (InputStream resource = getClass().getResourceAsStream(resourcePath)) {
            if (resource == null) {
                System.out.println("Resource not found: " + resourcePath);
                return;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(resource));
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > columnIndex && !values[columnIndex].isEmpty()) {
                    if (firstLine) {
                        firstLine = false; // Skip header line
                        continue;
                    }
                    originalItems.add(values[columnIndex].trim());
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "ERROR", "Failed to read " + resourcePath + ": " + e.getMessage());
            return;
        }

        // Create a ContextMenu for suggestions.
        ContextMenu suggestions = new ContextMenu();

        // Add a listener to the text property of the TextField.
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                suggestions.hide();
            } else {
                // Filter original items based on the text input (case insensitive).
                List<String> filteredList = originalItems.stream()
                    .filter(item -> item.toLowerCase().contains(newValue.toLowerCase()))
                    .collect(Collectors.toList());

                if (filteredList.isEmpty()) {
                    suggestions.hide();
                } else {
                    // Build a list of menu items for the filtered results.
                    List<MenuItem> menuItems = new ArrayList<>();
                    for (String item : filteredList) {
                        MenuItem menuItem = new MenuItem(item);
                        menuItem.setOnAction(e -> {
                            textField.setText(item);
                            suggestions.hide();
                        });
                        menuItems.add(menuItem);
                    }
                    suggestions.getItems().clear();
                    suggestions.getItems().addAll(menuItems);
                    if (!suggestions.isShowing()) {
                        suggestions.show(textField, Side.BOTTOM, 0, 0);
                    }
                }
            }
        });
    }

    // to load suggestions from a CSV file
    protected List<String> loadSuggestionsFromCSV(String filePath, int columnIndex) {
        List<String> suggestions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
    
                String[] split = line.split(",");
                if (split.length > columnIndex) {
                    suggestions.add(split[columnIndex]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return suggestions;
    }


    //for searching and displaying results in a tableview
    protected void searchAndDisplayCSVResults(String filePath, TableView<ObservableList<String>> table, String searchTerm, int... searchableColumns) {
        ObservableList<ObservableList<String>> results = FXCollections.observableArrayList();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(",");
                ObservableList<String> row = FXCollections.observableArrayList(Arrays.asList(data));

                // If search is empty, include all rows (reset)
                if (searchTerm == null || searchTerm.isBlank()) {
                    results.add(row);
                    continue;
                }

                // Otherwise, filter based on search columns
                String loweredTerm = searchTerm.toLowerCase();
                for (int col : searchableColumns) {
                    if (col < data.length && data[col].toLowerCase().contains(loweredTerm)) {
                        results.add(row);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "ERROR", "Failed to search " + filePath + ": " + e.getMessage());
        }
        table.setItems(results);
    }

    //alert for registering data
    protected void showAlertAndRegister(
        String csvRow,
        String filePath,
        String successMessage,
        Runnable clearFormFunction
    ) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Registration");
        confirmationAlert.setHeaderText("Are you sure you want to proceed?");
        confirmationAlert.setContentText("Click OK to proceed or Cancel to abort.");
    
        confirmationAlert.showAndWait().ifPresent(response -> {
                if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
                        writer.append(csvRow);
                        showAlert(Alert.AlertType.CONFIRMATION, "Success", successMessage);
                        System.out.println(successMessage);
                    } catch (IOException e) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Could not save data: " + e.getMessage());
                        System.out.println(e.getMessage());
                    }
                } else {
                    if (clearFormFunction != null) {
                        clearFormFunction.run();
                        System.out.println("form cleared");
                    }
                }
        });
    }


    //if delete is clicked, irename ra ang old name into the none
    protected void updateCsvReferenceToNone(String csvFile, int columnIndex, String valueToReplace) {
        try {
            Path path = Paths.get(csvFile);
            List<String> lines = Files.readAllLines(path);
            List<String> updatedLines = new ArrayList<>();
            
            boolean changed = false;
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length > columnIndex && parts[columnIndex].trim().equals(valueToReplace)) {
                    parts[columnIndex] = "None"; // Update the value in the specified column
                    changed = true;
                }
                updatedLines.add(String.join(",", parts));
            }
            
            if (changed) {
                Files.write(path, updatedLines);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("File Update Error");
            errorAlert.setContentText("Failed to update references in " + csvFile);
            errorAlert.showAndWait();
        }
    }


    protected void updateCsvReferenceToNew(String csvFile, int columnIndex, String valueToReplace, String newValue) {
        try {
            Path path = Paths.get(csvFile);
            List<String> lines = Files.readAllLines(path);
            List<String> updatedLines = new ArrayList<>();
            
            boolean changed = false;
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length > columnIndex && parts[columnIndex].trim().equals(valueToReplace)) {
                    parts[columnIndex] = newValue; // Update the value in the specified column
                    changed = true;
                }
                updatedLines.add(String.join(",", parts));
            }
            
            if (changed) {
                Files.write(path, updatedLines);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("File Update Error");
            errorAlert.setContentText("Failed to update references in " + csvFile);
            errorAlert.showAndWait();
        }
    }


    protected Map<String, String> loadProgramMap(String programFilePath) {
        Map<String, String> programMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(programFilePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip header line
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 2) {
                    String code = values[0].trim();
                    String name = values[1].trim();
                    programMap.put(code, name);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return programMap;
    }


    private List<String> loadUniqueColumnItems(String filePath, int columnIndex) {
        Set<String> uniqueItems = new LinkedHashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] split = line.split(",");
                if (split.length > columnIndex) {
                    uniqueItems.add(split[columnIndex].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(uniqueItems);
    }

    protected void updateComboBoxItems(ComboBox<String> comboBox, String filePath, int columnIndex) {
        List<String> items = loadUniqueColumnItems(filePath, columnIndex);
        comboBox.getItems().setAll(items);
    }


    protected void populateComboBox(ComboBox<String> comboBox, String filePath, int columnIndex) {
        // Just populate once on startup
        List<String> items = loadUniqueColumnItems(filePath, columnIndex);
        comboBox.setItems(FXCollections.observableArrayList(items));
    }
}
