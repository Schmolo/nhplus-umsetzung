package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.CaregiverDao;
import de.hitec.nhplus.service.Session;
import de.hitec.nhplus.utils.AuditLog;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.utils.DateConverter;

import java.sql.SQLException;
import java.time.LocalDate;

public class NewCaregiverController {

    @FXML
    private TextField textFieldFirstName;

    @FXML
    private TextField textFieldSurname;

    @FXML
    private DatePicker datePickerDateOfBirth;

    @FXML
    private TextField textFieldTelephoneNumber;

    @FXML
    private TextField textFieldUsername;

    @FXML
    private PasswordField passwordFieldPassword;

    @FXML
    private CheckBox checkBoxIsAdmin;

    @FXML
    private Button buttonAdd;

    private AllCaregiverController controller;
    private Stage stage;

    public void initialize(AllCaregiverController controller, Stage stage) {
        this.controller= controller;
        this.stage = stage;
    }

    @FXML
    public void handleAdd(){
        if (!areInputDataInvalid()) {
            invalidData();
            return;
        }
        if (!isTelephoneValid()) {
            invalidTelephone();
            return;
        }
        String username = textFieldUsername.getText();
        String firstName = textFieldFirstName.getText();
        String surname = textFieldSurname.getText();
        LocalDate dateOfBirth = datePickerDateOfBirth.getValue();
        String telephoneNumber = textFieldTelephoneNumber.getText();
        String password = passwordFieldPassword.getText();
        boolean isAdmin = checkBoxIsAdmin.isSelected();
        boolean isLocked = false;
        String lockedDate = null;
        Caregiver caregiver = new Caregiver(username, firstName, surname, dateOfBirth, telephoneNumber, password, isAdmin, isLocked, lockedDate);
        createCaregiver(caregiver);
        controller.readAllAndShowInTableView();
        stage.close();
    }

    private void createCaregiver(Caregiver caregiver) {
        CaregiverDao dao = DaoFactory.getDaoFactory().createCaregiverDAO();
        try {
            dao.create(caregiver);
            AuditLog.writeLog(Session.getInstance().getLoggedInCaregiver(), "Caregiver created: " + caregiver.getUsername());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    public void handleCancel(){
        stage.close();
    }

    private boolean areInputDataInvalid() {
        return this.textFieldFirstName.getText() != null && this.textFieldSurname.getText() != null && this.textFieldTelephoneNumber.getText() != null && this.textFieldUsername.getText() != null && this.passwordFieldPassword.getText() != null;
    }

    private boolean isTelephoneValid() {
        return this.textFieldTelephoneNumber.getText().matches("^[0-9\\-\\s]{4,15}$");
    }

    private void invalidData() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText("Invalid input data");
        alert.setContentText("Please fill in all fields.");
        alert.showAndWait();
    }

    private void invalidTelephone() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText("Invalid telephone number");
        alert.setContentText("Please enter a valid telephone number. (4-15 digits)");
        alert.showAndWait();
    }
}