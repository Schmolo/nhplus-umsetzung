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

public class CaregiverController {

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

    private AllCaregiverController controller;
    private Caregiver caregiver;
    private Stage stage;

    public void initialize(AllCaregiverController controller, Stage stage, Caregiver caregiver) {
        this.controller = controller;
        this.stage = stage;

        this.caregiver = caregiver;
        showData();
    }

    private void showData(){
        this.textFieldFirstName.setText(caregiver.getFirstName());
        this.textFieldSurname.setText(caregiver.getSurname());
        this.datePickerDateOfBirth.setValue(LocalDate.parse(caregiver.getDateOfBirth()));
        this.textFieldTelephoneNumber.setText(caregiver.getTelephoneNumber());
        this.textFieldUsername.setText(caregiver.getUsername());
        this.checkBoxIsAdmin.setSelected(caregiver.isAdmin());
    }

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

    private void doUpdate() {
        CaregiverDao dao = DaoFactory.getDaoFactory().createCaregiverDAO();
        try {
            dao.update(this.caregiver);
            AuditLog.writeLog(Session.getInstance().getLoggedInCaregiver(), "Caregiver updated: " + caregiver.getUsername());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    public void handleCancel(){
        stage.close();
    }

    private boolean areInputDataInvalid() {
        return this.textFieldFirstName.getText() != null && this.textFieldSurname.getText() != null && this.textFieldTelephoneNumber.getText() != null && this.textFieldUsername.getText() != null;
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