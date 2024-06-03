package de.hitec.nhplus;

import de.hitec.nhplus.datastorage.ConnectionBuilder;
import de.hitec.nhplus.datastorage.PatientDao;
import de.hitec.nhplus.datastorage.TreatmentDao;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Main extends Application {

    private Stage primaryStage;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        loginWindow();
        scheduleTask();
    }

    public void loginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/hitec/nhplus/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            this.primaryStage.setScene(scene);
            this.primaryStage.setTitle("NHPlus");
            this.primaryStage.setResizable(false);
            this.primaryStage.show();

            this.primaryStage.setOnCloseRequest(e -> {
                ConnectionBuilder.closeConnection();
                Platform.exit();
                System.exit(0);
            });
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Diese Methode erstellt eine Aufgabe, die alle 24 Stunden ausgeführt wird.
     * Die Aufgabe ist, die Methode deleteExpiredLocks() der Klasse TreatmentDao aufzurufen,
     * die alle abgelaufenen Sperren aus der Datenbank löscht.
     * Die Methode verwendet einen ScheduledExecutorService, um die Aufgabe zu planen und auszuführen.
     */
    private void scheduleTask() {
        // Erstellen einer Instanz von TreatmentDao
        TreatmentDao treatmentDao = new TreatmentDao(ConnectionBuilder.getConnection());
        PatientDao patientDao = new PatientDao(ConnectionBuilder.getConnection());



        // Definieren der Aufgabe, die ausgeführt werden soll
        Runnable task = () -> {
            try {
                // Aufrufen der Methode deleteExpiredLocks()
                treatmentDao.deleteExpiredLocks();
                patientDao.deleteExpiredPatientLocks();

            } catch (SQLException e) {
                // Fehlerbehandlung
                e.printStackTrace();
            }
        };

        // Planen der Aufgabe zur sofortigen Ausführung und danach alle 24 Stunden
        scheduler.scheduleAtFixedRate(task, 0, 24, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {launch(args);}
}