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

        this.buttonAdd.setDisable(true);
        ChangeListener<String> inputNewCaregiverListener = (observableValue, oldText, newText) ->
                NewCaregiverController.this.buttonAdd.setDisable(NewCaregiverController.this.areInputDataInvalid());
        this.textFieldFirstName.textProperty().addListener(inputNewCaregiverListener);
        this.textFieldSurname.textProperty().addListener(inputNewCaregiverListener);
        this.datePickerDateOfBirth.valueProperty().addListener((observableValue, localDate, t1) -> NewCaregiverController.this.buttonAdd.setDisable(NewCaregiverController.this.areInputDataInvalid()));
        this.textFieldTelephoneNumber.textProperty().addListener(inputNewCaregiverListener);
        this.textFieldUsername.textProperty().addListener(inputNewCaregiverListener);
        this.passwordFieldPassword.textProperty().addListener(inputNewCaregiverListener);
    }

    @FXML
    public void handleAdd(){
        String username = textFieldUsername.getText();
        String firstName = textFieldFirstName.getText();
        String surname = textFieldSurname.getText();
        LocalDate dateOfBirth = datePickerDateOfBirth.getValue();
        String telephoneNumber = textFieldTelephoneNumber.getText();
        String password = passwordFieldPassword.getText();
        boolean isAdmin = checkBoxIsAdmin.isSelected();
        Caregiver caregiver = new Caregiver(username, firstName, surname, dateOfBirth, telephoneNumber, password, isAdmin);
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
        return this.textFieldFirstName.getText().isBlank() || this.textFieldSurname.getText().isBlank()
                || this.datePickerDateOfBirth.getValue() == null || this.textFieldTelephoneNumber.getText().isBlank()
                || this.textFieldUsername.getText().isBlank() || this.passwordFieldPassword.getText().isBlank();
    }
}