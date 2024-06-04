package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.PatientDao;
import de.hitec.nhplus.datastorage.TreatmentDao;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Treatment;
import de.hitec.nhplus.service.Session;
import de.hitec.nhplus.utils.AuditLog;
import de.hitec.nhplus.utils.DateConverter;
import de.hitec.nhplus.utils.exportUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;


/**
 * The <code>AllPatientController</code> contains the entire logic of the patient view. It determines which data is displayed and how to react to events.
 */
public class AllPatientController {

    @FXML
    private TableView<Patient> tableView;

    @FXML
    private TableColumn<Patient, Integer> columnId;

    @FXML
    private TableColumn<Patient, String> columnFirstName;

    @FXML
    private TableColumn<Patient, String> columnSurname;

    @FXML
    private TableColumn<Patient, String> columnDateOfBirth;

    @FXML
    private TableColumn<Patient, String> columnCareLevel;

    @FXML
    private TableColumn<Patient, String> columnRoomNumber;


    @FXML
    private Button buttonDelete;

    @FXML
    private Button buttonLock;

    @FXML
    private Button buttonAdd;

    @FXML
    private Button buttonExport;

    @FXML
    private Button buttonSelectAll;

    @FXML
    private TextField textFieldSurname;

    @FXML
    private TextField textFieldFirstName;

    @FXML
    private TextField textFieldDateOfBirth;

    @FXML
    private TextField textFieldCareLevel;

    @FXML
    private TextField textFieldRoomNumber;


    private final ObservableList<Patient> patients = FXCollections.observableArrayList();
    private PatientDao dao;

    /**
     * When <code>initialize()</code> gets called, all fields are already initialized. For example from the FXMLLoader
     * after loading an FXML-File. At this point of the lifecycle of the Controller, the fields can be accessed and
     * configured.
     */
    public void initialize() {
        this.readAllAndShowInTableView();

        this.columnId.setCellValueFactory(new PropertyValueFactory<>("pid"));

        // CellValueFactory to show property values in TableView
        this.columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        // CellFactory to write property values from with in the TableView
        this.columnFirstName.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        this.columnSurname.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnDateOfBirth.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        this.columnDateOfBirth.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnCareLevel.setCellValueFactory(new PropertyValueFactory<>("careLevel"));
        this.columnCareLevel.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnRoomNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        this.columnRoomNumber.setCellFactory(TextFieldTableCell.forTableColumn());

        Tooltip exportTooltip = new Tooltip("To export multiple users data please hold control and select the users then click on export.");
        Tooltip.install(buttonExport, exportTooltip);

        Tooltip selectAllTooltip = new Tooltip("Click to select all patients.");
        Tooltip.install(buttonSelectAll, selectAllTooltip);


        //Anzeigen der Daten
        this.tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.tableView.setItems(this.patients);

        this.buttonDelete.setDisable(true);
        this.tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Patient>() {
            @Override
            public void changed(ObservableValue<? extends Patient> observableValue, Patient oldPatient, Patient newPatient) {
                AllPatientController.this.buttonDelete.setDisable(newPatient == null);
            }
        });


        this.buttonAdd.setDisable(true);
        ChangeListener<String> inputNewPatientListener = (observableValue, oldText, newText) ->
                AllPatientController.this.buttonAdd.setDisable(!AllPatientController.this.areInputDataValid());
        this.textFieldSurname.textProperty().addListener(inputNewPatientListener);
        this.textFieldFirstName.textProperty().addListener(inputNewPatientListener);
        this.textFieldDateOfBirth.textProperty().addListener(inputNewPatientListener);
        this.textFieldCareLevel.textProperty().addListener(inputNewPatientListener);
        this.textFieldRoomNumber.textProperty().addListener(inputNewPatientListener);
    }

    /**
     * When a cell of the column with first names was changed, this method will be called, to persist the change.
     *
     * @param event Event including the changed object and the change.
     */
    @FXML
    public void handleOnEditFirstname(TableColumn.CellEditEvent<Patient, String> event) {
        event.getRowValue().setFirstName(event.getNewValue());
        this.doUpdate(event);
    }

    /**
     * When a cell of the column with surnames was changed, this method will be called, to persist the change.
     *
     * @param event Event including the changed object and the change.
     */
    @FXML
    public void handleOnEditSurname(TableColumn.CellEditEvent<Patient, String> event) {
        event.getRowValue().setSurname(event.getNewValue());
        this.doUpdate(event);
    }

    /**
     * When a cell of the column with dates of birth was changed, this method will be called, to persist the change.
     *
     * @param event Event including the changed object and the change.
     */
    @FXML
    public void handleOnEditDateOfBirth(TableColumn.CellEditEvent<Patient, String> event) {
        event.getRowValue().setDateOfBirth(event.getNewValue());
        this.doUpdate(event);
    }

    /**
     * When a cell of the column with care levels was changed, this method will be called, to persist the change.
     *
     * @param event Event including the changed object and the change.
     */
    @FXML
    public void handleOnEditCareLevel(TableColumn.CellEditEvent<Patient, String> event) {
        event.getRowValue().setCareLevel(event.getNewValue());
        this.doUpdate(event);
    }

    /**
     * When a cell of the column with room numbers was changed, this method will be called, to persist the change.
     *
     * @param event Event including the changed object and the change.
     */
    @FXML
    public void handleOnEditRoomNumber(TableColumn.CellEditEvent<Patient, String> event) {
        event.getRowValue().setRoomNumber(event.getNewValue());
        this.doUpdate(event);
    }


    /**
     * Updates a patient by calling the method <code>update()</code> of {@link PatientDao}.
     *
     * @param event Event including the changed object and the change.
     */
    private void doUpdate(TableColumn.CellEditEvent<Patient, String> event) {
        try {
            this.dao.update(event.getRowValue());
            AuditLog.writeLog(Session.getInstance().getLoggedInCaregiver(), "Updated patient with ID: " + event.getRowValue().getPid());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Reloads all patients to the table by clearing the list of all patients and filling it again by all persisted
     * patients, delivered by {@link PatientDao}.
     */
    private void readAllAndShowInTableView() {
        this.patients.clear();
        this.dao = DaoFactory.getDaoFactory().createPatientDAO();
        try {
            this.patients.addAll(this.dao.readAll());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }


    /**
     * This method handles the locking of a patient. When a patient is locked, they can no longer be edited
     * and will be deleted after 10 years. This action is irreversible.
     * The method first creates a confirmation dialog to ensure that the user really wants to lock the patient.
     * If the user confirms, the selected patient is removed from the list and the 'lockPatient' method
     * of the PatientDao is called with the patient ID as a parameter.
     */
    @FXML
    public void handleLock() {
        // Create a confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Patient sperren");
        alert.setHeaderText("Möchten Sie diesen Patienten wirklich sperren?");
        alert.setContentText("Ein gesperrter Patient kann nicht mehr bearbeitet werden\n" +
                "und wird innerhalb von 10 Jahren gelöscht,\n" +
                "dies ist endgültig!");

        // Create two buttons: Lock and Cancel
        ButtonType buttonTypeLock = new ButtonType("Sperren", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE);

        // Add the buttons to the dialog
        alert.getButtonTypes().setAll(buttonTypeLock, buttonTypeCancel);

        // Display the dialog and wait for the user's selection
        Optional<ButtonType> result = alert.showAndWait();

        // Check if the "Lock" button was selected
        if (result.isPresent() && result.get() == buttonTypeLock) {
            // Determine the index of the selected patient in the table
            int index = this.tableView.getSelectionModel().getSelectedIndex();
            // Remove the selected patient from the list
            Patient p = this.patients.remove(index);

            // Create a DAO (Data Access Object) for the patient
            PatientDao dao = DaoFactory.getDaoFactory().createPatientDAO();
            try {
                dao.lockPatient(p.getPid());
                AuditLog.writeLog(Session.getInstance().getLoggedInCaregiver(), "Locked patient with ID: " + p.getPid());
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        // If "Cancel" was chosen, nothing happens and the method ends
    }

    /**
     * This method handles the events fired by the button to add a patient. It collects the data from the
     * <code>TextField</code>s, creates an object of class <code>Patient</code> of it and passes the object to
     * {@link PatientDao} to persist the data.
     */
    @FXML
    public void handleAdd() {
        String surname = this.textFieldSurname.getText();
        String firstName = this.textFieldFirstName.getText();
        String birthday = this.textFieldDateOfBirth.getText();
        LocalDate date = DateConverter.convertStringToLocalDate(birthday);
        String careLevel = this.textFieldCareLevel.getText();
        String roomNumber = this.textFieldRoomNumber.getText();
        Boolean locked = false;
        String lockedDate = null;
        try {
            this.dao.create(new Patient(firstName, surname, date, careLevel, roomNumber, locked, lockedDate));
            AuditLog.writeLog(Session.getInstance().getLoggedInCaregiver(), "Added patient: " + firstName + " " + surname);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        readAllAndShowInTableView();
        clearTextfields();
    }

    /**
     * This method handles the events fired by the button to export a patient's data. It retrieves the selected
     * <code>Patient</code> from the <code>TableView</code>, and if a patient is selected, it calls the method
     * to export the patient's data. The export format can is "CSV".
     */
    @FXML
    public void handleExport() {
        ObservableList<Patient> selectedPatients = this.tableView.getSelectionModel().getSelectedItems();
        if (selectedPatients != null && !selectedPatients.isEmpty()) {
            try {
                exportPatientData(selectedPatients);
                AuditLog.writeLog(Session.getInstance().getLoggedInCaregiver(), "Exported patient data");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleSelectAll() {
        this.tableView.getSelectionModel().selectAll();
    }


    /**
     * Clears all contents from all <code>TextField</code>s.
     */
    private void clearTextfields() {
        this.textFieldFirstName.clear();
        this.textFieldSurname.clear();
        this.textFieldDateOfBirth.clear();
        this.textFieldCareLevel.clear();
        this.textFieldRoomNumber.clear();
    }

    private boolean areInputDataValid() {
        if (!this.textFieldDateOfBirth.getText().isBlank()) {
            try {
                DateConverter.convertStringToLocalDate(this.textFieldDateOfBirth.getText());
            } catch (Exception exception) {
                return false;
            }
        }

        return !this.textFieldFirstName.getText().isBlank() && !this.textFieldSurname.getText().isBlank() &&
                !this.textFieldDateOfBirth.getText().isBlank() && !this.textFieldCareLevel.getText().isBlank() &&
                !this.textFieldRoomNumber.getText().isBlank();
    }


    private void exportPatientData(ObservableList<Patient> selectedPatients) throws Exception {
        exportUtil.exportToCSV(selectedPatients);
    }
}