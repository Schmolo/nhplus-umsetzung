package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.CaregiverDao;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.service.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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

    private void readAllAndShowInTableView() {
        this.caregivers.clear();
        CaregiverDao dao = DaoFactory.getDaoFactory().createCaregiverDAO();
        try {
            this.caregivers.addAll(dao.readAll());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    public void handleDelete() {
        Caregiver selectedCaregiver = this.tableView.getSelectionModel().getSelectedItem();
        if (selectedCaregiver == null) {
            return;
        }
        if (selectedCaregiver.isAdmin()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot delete the user");
            alert.setContentText("You cannot delete an admin user.");
            alert.showAndWait();
            return;
        }
        if (selectedCaregiver.getPid() == Session.getInstance().getLoggedInCaregiver().getPid()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot delete the user");
            alert.setContentText("You cannot delete the user you are Logged in to.");
            alert.showAndWait();
            return;
        }
        CaregiverDao dao = DaoFactory.getDaoFactory().createCaregiverDAO();
        try {
            dao.deleteById(selectedCaregiver.getPid());
            this.caregivers.remove(selectedCaregiver);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
