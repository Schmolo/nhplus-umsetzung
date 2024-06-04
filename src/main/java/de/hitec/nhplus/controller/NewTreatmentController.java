package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.TreatmentDao;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Treatment;
import de.hitec.nhplus.utils.DateConverter;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class NewTreatmentController {

    @FXML
    private Label labelFirstName;

    @FXML
    private Label labelSurname;

    @FXML
    private TextField textFieldBegin;

    @FXML
    private TextField textFieldEnd;

    @FXML
    private TextField textFieldDescription;

    @FXML
    private TextArea textAreaRemarks;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button buttonAdd;

    private AllTreatmentController controller;
    private Patient patient;
    private Stage stage;

    /**
     * Initializes the NewTreatmentController with the given AllTreatmentController, Stage, and Patient.
     * It sets up the necessary listeners for the input fields and disables the 'Add' button until valid input is provided.
     * It also sets up a converter for the DatePicker to handle LocalDate objects.
     *
     * @param controller The AllTreatmentController that this NewTreatmentController is associated with.
     * @param stage The Stage that this NewTreatmentController is displayed on.
     * @param patient The Patient that this NewTreatmentController is creating a treatment for.
     */
    public void initialize(AllTreatmentController controller, Stage stage, Patient patient) {
        // Set the controller, stage, and patient
        this.controller= controller;
        this.patient = patient;
        this.stage = stage;

        // Initially disable the 'Add' button
        this.buttonAdd.setDisable(true);

        // Create a listener for the input fields
        ChangeListener<String> inputNewPatientListener = (observableValue, oldText, newText) ->
                // Disable the 'Add' button if the input data is invalid
                NewTreatmentController.this.buttonAdd.setDisable(NewTreatmentController.this.areInputDataInvalid());

        // Add the listener to the input fields
        this.textFieldBegin.textProperty().addListener(inputNewPatientListener);
        this.textFieldEnd.textProperty().addListener(inputNewPatientListener);
        this.textFieldDescription.textProperty().addListener(inputNewPatientListener);
        this.textAreaRemarks.textProperty().addListener(inputNewPatientListener);

        // Add a listener to the DatePicker to disable the 'Add' button if the date is invalid
        this.datePicker.valueProperty().addListener((observableValue, localDate, t1) -> NewTreatmentController.this.buttonAdd.setDisable(NewTreatmentController.this.areInputDataInvalid()));

        // Set a converter for the DatePicker to handle LocalDate objects
        this.datePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate localDate) {
                // Convert the LocalDate to a string, or return an empty string if the date is null
                return (localDate == null) ? "" : DateConverter.convertLocalDateToString(localDate);
            }

            @Override
            public LocalDate fromString(String localDate) {
                // Convert the string to a LocalDate
                return DateConverter.convertStringToLocalDate(localDate);
            }
        });

        // Display the patient's data
        this.showPatientData();
    }

    private void showPatientData(){
        this.labelFirstName.setText(patient.getFirstName());
        this.labelSurname.setText(patient.getSurname());
    }

    @FXML
    public void handleAdd(){
        LocalDate date = this.datePicker.getValue();
        LocalTime begin = DateConverter.convertStringToLocalTime(textFieldBegin.getText());
        LocalTime end = DateConverter.convertStringToLocalTime(textFieldEnd.getText());
        String description = textFieldDescription.getText();
        String remarks = textAreaRemarks.getText();
        Boolean locked = false;
        String lockedDate = null;
        Treatment treatment = new Treatment(patient.getPid(), date, begin, end, description, remarks, locked, lockedDate);
        createTreatment(treatment);
        controller.readAllAndShowInTableView();
        stage.close();
    }

    private void createTreatment(Treatment treatment) {
        TreatmentDao dao = DaoFactory.getDaoFactory().createTreatmentDao();
        try {
            dao.create(treatment);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    public void handleCancel(){
        stage.close();
    }

    private boolean areInputDataInvalid() {
        if (this.textFieldBegin.getText() == null || this.textFieldEnd.getText() == null) {
            return true;
        }
        try {
            LocalTime begin = DateConverter.convertStringToLocalTime(this.textFieldBegin.getText());
            LocalTime end = DateConverter.convertStringToLocalTime(this.textFieldEnd.getText());
            if (!end.isAfter(begin)) {
                return true;
            }
        } catch (Exception exception) {
            return true;
        }
        return this.textFieldDescription.getText().isBlank() || this.datePicker.getValue() == null;
    }
}