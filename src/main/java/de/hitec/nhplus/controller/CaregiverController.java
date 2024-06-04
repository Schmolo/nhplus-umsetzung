package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.CaregiverDao;
import de.hitec.nhplus.service.Session;
import de.hitec.nhplus.utils.AuditLog;
import de.hitec.nhplus.utils.PasswordUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import de.hitec.nhplus.model.Caregiver;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * This class is the controller for the Caregiver view.
 * It handles the interaction between the user and the UI for managing caregivers.
 */
public class CaregiverController {

    @FXML
    // TextField for the first name of the caregiver
    private TextField textFieldFirstName;

    @FXML
    // TextField for the surname of the caregiver
    private TextField textFieldSurname;

    @FXML
    // DatePicker for the date of birth of the caregiver
    private DatePicker datePickerDateOfBirth;

    @FXML
    // TextField for the telephone number of the caregiver
    private TextField textFieldTelephoneNumber;

    @FXML
    // TextField for the username of the caregiver
    private TextField textFieldUsername;

    @FXML
    // PasswordField for the password of the caregiver
    private PasswordField passwordFieldPassword;

    @FXML
    // CheckBox for the admin status of the caregiver
    private CheckBox checkBoxIsAdmin;

    // The controller for the AllCaregiver view
    private AllCaregiverController controller;
    // The caregiver being managed
    private Caregiver caregiver;
    // The stage of the Caregiver view
    private Stage stage;

    /**
     * This method initializes the CaregiverController with the given AllCaregiverController, stage, and caregiver.
     *
     * @param controller The AllCaregiverController.
     * @param stage The stage.
     * @param caregiver The caregiver being managed.
     */
    public void initialize(AllCaregiverController controller, Stage stage, Caregiver caregiver) {
        this.controller = controller;
        this.stage = stage;

        this.caregiver = caregiver;
        showData();
    }

    /**
     * This method shows the data of the caregiver in the UI.
     */
    private void showData(){
        this.textFieldFirstName.setText(caregiver.getFirstName());
        this.textFieldSurname.setText(caregiver.getSurname());
        this.datePickerDateOfBirth.setValue(LocalDate.parse(caregiver.getDateOfBirth()));
        this.textFieldTelephoneNumber.setText(caregiver.getTelephoneNumber());
        this.textFieldUsername.setText(caregiver.getUsername());
        this.checkBoxIsAdmin.setSelected(caregiver.isAdmin());
    }

    /**
     * This method handles the change of the caregiver data.
     * It validates the input data and updates the caregiver if the data is valid.
     */
    @FXML
    public void handleChange(){
        if (!areInputDataInvalid()) {
            invalidData();
            return;
        }
        if (!isTelephoneValid()) {
            invalidTelephone();
            return;
        }
        this.caregiver.setDateOfBirth(this.datePickerDateOfBirth.getValue().toString());
        this.caregiver.setFirstName(this.textFieldFirstName.getText());
        this.caregiver.setSurname(this.textFieldSurname.getText());
        this.caregiver.setTelephoneNumber(this.textFieldTelephoneNumber.getText());
        this.caregiver.setUsername(this.textFieldUsername.getText());
        this.caregiver.setIsAdmin(this.checkBoxIsAdmin.isSelected());
        if (!this.passwordFieldPassword.getText().isEmpty()) {
            this.caregiver.setPassword_hash(PasswordUtil.generatePassword(this.passwordFieldPassword.getText()));
        } else {
            this.caregiver.setPassword_hash(this.caregiver.getPassword_hash());
        }
        doUpdate();
        controller.readAllAndShowInTableView();
        stage.close();
    }

    /**
     * This method updates the caregiver in the database.
     */
    private void doUpdate() {
        CaregiverDao dao = DaoFactory.getDaoFactory().createCaregiverDAO();
        try {
            dao.update(this.caregiver);
            AuditLog.writeLog(Session.getInstance().getLoggedInCaregiver(), "Caregiver updated: " + caregiver.getUsername());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * This method handles the cancellation of the caregiver data change.
     * It closes the stage.
     */
    @FXML
    public void handleCancel(){
        stage.close();
    }

    /**
     * This method checks if the input data for the caregiver is invalid.
     *
     * @return True if the input data is invalid, false otherwise.
     */
    private boolean areInputDataInvalid() {
        return this.textFieldFirstName.getText() != null && this.textFieldSurname.getText() != null && this.textFieldTelephoneNumber.getText() != null && this.textFieldUsername.getText() != null;
    }

    /**
     * This method checks if the telephone number of the caregiver is valid.
     *
     * @return True if the telephone number is valid, false otherwise.
     */
    private boolean isTelephoneValid() {
        return this.textFieldTelephoneNumber.getText().matches("^[0-9\\-\\s]{4,15}$");
    }

    /**
     * This method shows an error alert if the input data for the caregiver is invalid.
     */
    private void invalidData() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText("Invalid input data");
        alert.setContentText("Please fill in all fields.");
        alert.showAndWait();
    }

    /**
     * This method shows an error alert if the telephone number of the caregiver is invalid.
     */
    private void invalidTelephone() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText("Invalid telephone number");
        alert.setContentText("Please enter a valid telephone number. (4-15 digits)");
        alert.showAndWait();
    }
}