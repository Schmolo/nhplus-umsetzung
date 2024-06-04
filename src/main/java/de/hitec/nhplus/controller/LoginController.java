package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.service.CaregiverService;
import de.hitec.nhplus.service.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class LoginController {

    public BorderPane mainBorderPane;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private final CaregiverService caregiverService = new CaregiverService();



    /**
     * This method handles the login process when the login button is clicked.
     * It retrieves the username and password from the input fields and attempts to authenticate the caregiver.
     * If the authentication is successful, it sets the logged in caregiver in the session and loads the main window view.
     * If the authentication fails, it displays an error message.
     *
     * @param event The ActionEvent triggered by clicking the login button.
     */
    @FXML
    private void login(ActionEvent event) {
        // Retrieve the username and password from the input fields
        String username = usernameField.getText();
        String password = passwordField.getText();
        Caregiver caregiver = null;

        // Attempt to authenticate the caregiver
        Caregiver caregiver = caregiverService.authenticate(username, password);
        // Check if the authentication was successful
        try {
            caregiver = caregiverService.authenticate(username, password);
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Login failed. Please check your credentials.");
        }
        if (caregiver != null) {
            // Set the logged in caregiver in the session
            Session.getInstance().setLoggedInCaregiver(caregiver);

            // Load the main window view
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/MainWindowView.fxml"));
            try {
                mainBorderPane.setCenter(loader.load());
            } catch (IOException exception) {
                exception.printStackTrace();
                errorLabel.setText("Login failed. Please check your credentials.");
            }
        } else {
            // Display an error message
            errorLabel.setText("Anmeldung fehlgeschlagen. Bitte überprüfen Sie Ihre Anmeldedaten.");
        }
    }

    /**
     * This method handles the event when the Enter key is pressed.
     * It triggers the login process when the Enter key is pressed.
     *
     * @param keyEvent The KeyEvent triggered by pressing a key.
     */
    public void handleEnterPressed(KeyEvent keyEvent) {
        // Check if the Enter key was pressed
        if (keyEvent.getCode().toString().equals("ENTER")) {
            // Trigger the login process
            login(new ActionEvent());
        }
    }
}