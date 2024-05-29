package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.service.CaregiverService;
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
    private TextField firstnameField;

    @FXML
    private TextField surnameField;

    @FXML
    private PasswordField passwordField;

    private final CaregiverService caregiverService = new CaregiverService();

    @FXML
    private void login(ActionEvent event) {
        String firstname = firstnameField.getText();
        String surname = surnameField.getText();
        String password = passwordField.getText();


        Caregiver caregiver = caregiverService.authenticate(firstname, surname, password);
        if (caregiver != null) {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/MainWindowView.fxml"));
            try {
                mainBorderPane.setCenter(loader.load());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } else {
            errorLabel.setText("Login failed. Please check your credentials.");
        }
    }

    public void handleEnterPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().toString().equals("ENTER")) {
            login(new ActionEvent());
        }
    }
}