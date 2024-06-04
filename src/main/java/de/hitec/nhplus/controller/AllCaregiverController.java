package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import de.hitec.nhplus.datastorage.CaregiverDao;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.service.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class AllCaregiverController {

    @FXML
    private TableView<Caregiver> tableView;

    @FXML
    private TableColumn<Caregiver, Integer> colId;

    @FXML
    private TableColumn<Caregiver, String> colFirstName;

    @FXML
    private TableColumn<Caregiver, String> colSurname;

    @FXML
    private TableColumn<Caregiver, String> colTelephoneNumber;

    private final ObservableList<Caregiver> caregivers = FXCollections.observableArrayList();

    public void initialize() {
        this.readAllAndShowInTableView();

        this.colId.setCellValueFactory(new PropertyValueFactory<>("pid"));

        this.colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        this.colSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));

        this.colTelephoneNumber.setCellValueFactory(new PropertyValueFactory<>("telephoneNumber"));

        this.tableView.setItems(this.caregivers);
    }

    /**
     * This method is used to read all caregivers from the database and display them in the table view.
     * It first clears the existing list of caregivers, then creates a new CaregiverDao to interact with the database.
     * It then tries to read all caregivers from the database and add them to the list of caregivers.
     * If an SQLException occurs during this process, it is caught and its stack trace is printed.
     */
    public void readAllAndShowInTableView() {
        // Clear the existing list of caregivers
        this.caregivers.clear();

        // Create a new CaregiverDao to interact with the database
        CaregiverDao dao = DaoFactory.getDaoFactory().createCaregiverDAO();

        try {
            // Try to read all caregivers from the database and add them to the list of caregivers
            this.caregivers.addAll(dao.readAll());
        } catch (SQLException exception) {
            // If an SQLException occurs, print its stack trace
            exception.printStackTrace();
        }
    }

    /**
     * This method is used to handle the deletion of a caregiver from the table view.
     * It first checks if a caregiver is selected, if not it returns without doing anything.
     * Then it checks if the selected caregiver is an admin or the currently logged in caregiver,
     * if so it shows an error message and returns without deleting the caregiver.
     * If the selected caregiver is not an admin and not the currently logged in caregiver,
     * it deletes the caregiver from the database and removes it from the table view.
     */
    @FXML
    public void handleDelete() {
        // Get the selected caregiver from the table view
        Caregiver selectedCaregiver = this.tableView.getSelectionModel().getSelectedItem();

        // If no caregiver is selected, return without doing anything
        if (selectedCaregiver == null) {
            return;
        }

        // If the selected caregiver is an admin, show an error message and return without deleting the caregiver
        if (selectedCaregiver.isAdmin()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText("Der Nutzer kann nicht gelöscht werden");
            alert.setContentText("Du kannst keinen Admin löschen.");
            alert.showAndWait();
            return;
        }

        // If the selected caregiver is the currently logged in caregiver, show an error message and return without deleting the caregiver
        if (selectedCaregiver.getPid() == Session.getInstance().getLoggedInCaregiver().getPid()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText("Der Nutzer kann nicht gelöscht werden");
            alert.setContentText("Du kannst nicht den Nutzer löschen, der du gerade bist.");
            alert.showAndWait();
            return;
        }

        // Create a CaregiverDao to interact with the database
        CaregiverDao dao = DaoFactory.getDaoFactory().createCaregiverDAO();

        try {
            // Delete the selected caregiver from the database
            dao.deleteById(selectedCaregiver.getPid());

            // Remove the selected caregiver from the table view
            this.caregivers.remove(selectedCaregiver);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * This method is triggered when the "New Caregiver" button is clicked.
     * It calls the method to open a new window for creating a new caregiver.
     */
    @FXML
    public void handleNewCaregiver() {
        newCaregiverWindow();
    }

    /**
     * This method is triggered when a mouse click event occurs on the table view.
     * It sets an event handler for the mouse click event on the table view.
     * If a caregiver is selected and the mouse is double-clicked, it opens a new window displaying the details of the selected caregiver.
     */
    @FXML
    public void handleMouseClick() {
        tableView.setOnMouseClicked(event -> {
            Caregiver caregiver = tableView.getSelectionModel().getSelectedItem();
            if (event.getClickCount() == 2 && (caregiver != null)) {
                CaregiverWindow(caregiver);
            }
        });
    }

    /**
     * This method is used to open a new window for creating a new caregiver.
     * It first creates a new FXMLLoader and loads the FXML file for the NewCaregiverView.
     * It then creates a new Scene with the loaded pane and a new Stage for the window.
     * The NewCaregiverController for the new window is retrieved and initialized with the current controller and the new stage.
     * The new stage is then set with the created scene, its resizable property is set to false, and it is displayed.
     * If an IOException occurs during this process, it is caught and its stack trace is printed.
     */
    public void newCaregiverWindow() {
        try {
            // Create a new FXMLLoader and load the FXML file for the NewCaregiverView
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/NewCaregiverView.fxml"));
            AnchorPane pane = loader.load();
            Scene scene = new Scene(pane);

            // Create a new Stage for the window
            Stage stage = new Stage();

            // Retrieve the NewCaregiverController for the new window and initialize it with the current controller and the new stage
            NewCaregiverController controller = loader.getController();
            controller.initialize(this, stage);

            // Set the new stage with the created scene, set its resizable property to false, and display it
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException exception) {
            // If an IOException occurs, print its stack trace
            exception.printStackTrace();
        }
    }

    /**
     * This method is used to open a new window displaying the details of a specific caregiver.
     * It first creates a new FXMLLoader and loads the FXML file for the CaregiverView.
     * It then creates a new Scene with the loaded pane and a new Stage for the window.
     * The CaregiverController for the new window is retrieved and initialized with the current controller, the new stage, and the specific caregiver.
     * The new stage is then set with the created scene, its resizable property is set to false, and it is displayed.
     * If an IOException occurs during this process, it is caught and its stack trace is printed.
     *
     * @param caregiver The specific caregiver whose details are to be displayed in the new window.
     */
    public void CaregiverWindow(Caregiver caregiver) {
        try {
            // Create a new FXMLLoader and load the FXML file for the CaregiverView
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/CaregiverView.fxml"));
            AnchorPane pane = loader.load();

            // Create a new Scene with the loaded pane
            Scene scene = new Scene(pane);

            // Create a new Stage for the window
            Stage stage = new Stage();

            // Retrieve the CaregiverController for the new window and initialize it with the current controller, the new stage, and the specific caregiver
            CaregiverController controller = loader.getController();
            controller.initialize(this, stage, caregiver);

            // Set the new stage with the created scene, set its resizable property to false, and display it
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException exception) {
            // If an IOException occurs, print its stack trace
            exception.printStackTrace();
        }
    }
}