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

/**
 * This class is the controller for the NewCaregiver view.
 * It handles the creation of new caregivers in the system.
 */
public class NewCaregiverController {

    @FXML
    // TextField for the first name of the new caregiver
    private TextField textFieldFirstName;

    @FXML
    // TextField for the surname of the new caregiver
    private TextField textFieldSurname;

    @FXML
    // DatePicker for the date of birth of the new caregiver
    private DatePicker datePickerDateOfBirth;

    @FXML
    // TextField for the telephone number of the new caregiver
    private TextField textFieldTelephoneNumber;

    @FXML
    // TextField for the username of the new caregiver
    private TextField textFieldUsername;

    @FXML
    // PasswordField for the password of the new caregiver
    private PasswordField passwordFieldPassword;

    @FXML
    // CheckBox for the admin status of the new caregiver
    private CheckBox checkBoxIsAdmin;

    @FXML
    // Button for adding the new caregiver
    private Button buttonAdd;

    // The controller for the AllCaregiver view
    private AllCaregiverController controller;
    // The stage of the NewCaregiver view
    private Stage stage;

    /**
     * This method initializes the NewCaregiverController with the given AllCaregiverController and stage.
     *
     * @param controller The AllCaregiverController.
     * @param stage The stage.
     */
    public void initialize(AllCaregiverController controller, Stage stage) {
        this.controller= controller;
        this.stage = stage;
    }

    /**
     * This method handles the addition of a new caregiver.
     * It validates the input data and creates a new caregiver if the data is valid.
     */
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

    /**
     * This method creates a new caregiver in the database.
     *
     * @param caregiver The caregiver to be created.
     */
    private void createCaregiver(Caregiver caregiver) {
        CaregiverDao dao = DaoFactory.getDaoFactory().createCaregiverDAO();
        try {
            dao.create(caregiver);
            AuditLog.writeLog(Session.getInstance().getLoggedInCaregiver(), "Caregiver created: " + caregiver.getUsername());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * This method handles the cancellation of the creation of a new caregiver.
     * It closes the stage.
     */
    @FXML
    public void handleCancel(){
        stage.close();
    }

    /**
     * This method checks if the input data for the new caregiver is invalid.
     *
     * @return True if the input data is invalid, false otherwise.
     */
    private boolean areInputDataInvalid() {
        return this.textFieldFirstName.getText() != null && this.textFieldSurname.getText() != null && this.textFieldTelephoneNumber.getText() != null && this.textFieldUsername.getText() != null && this.passwordFieldPassword.getText() != null;
    }

    /**
     * This method checks if the telephone number of the new caregiver is valid.
     *
     * @return True if the telephone number is valid, false otherwise.
     */
    private boolean isTelephoneValid() {
        return this.textFieldTelephoneNumber.getText().matches("^[0-9\\-\\s]{4,15}$");
    }

    /**
     * This method shows an error alert if the input data for the new caregiver is invalid.
     */
    private void invalidData() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText("Invalid input data");
        alert.setContentText("Please fill in all fields.");
        alert.showAndWait();
    }

    /**
     * This method shows an error alert if the telephone number of the new caregiver is invalid.
     */
    private void invalidTelephone() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText("Invalid telephone number");
        alert.setContentText("Please enter a valid telephone number. (4-15 digits)");
        alert.showAndWait();
    }
}