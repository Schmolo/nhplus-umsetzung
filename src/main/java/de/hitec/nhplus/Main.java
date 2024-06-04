package de.hitec.nhplus;

import de.hitec.nhplus.datastorage.CaregiverDao;
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

    // The primary stage of the application
    private Stage primaryStage;

    // A ScheduledExecutorService to schedule tasks to be executed periodically
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    /**
     * This method is the entry point of the application.
     * It is called when the application is launched.
     * It sets the primary stage of the application and calls the methods to display the login window and schedule tasks.
     *
     * @param primaryStage The primary stage for this application, onto which the application scene can be set.
     */
    @Override
    public void start(Stage primaryStage) {
        // Set the primary stage of the application
        this.primaryStage = primaryStage;
        // Display the login window
        loginWindow();
        // Schedule tasks to be executed periodically
        scheduleTask();
    }

    /**
     * This method is used to display the login window of the application.
     * It first creates a new FXMLLoader and loads the FXML file for the Login view.
     * It then creates a new Scene with the loaded parent root and sets the scene for the primary stage.
     * The title of the primary stage is set to "NHPlus" and its resizable property is set to false.
     * The primary stage is then displayed.
     * An event handler is set for the close request of the primary stage, which closes the database connection and exits the application.
     * If an Exception occurs during this process, it is caught and its stack trace is printed.
     */
    public void loginWindow() {
        try {
            // Create a new FXMLLoader and load the FXML file for the Login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/hitec/nhplus/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Set the scene for the primary stage
            this.primaryStage.setScene(scene);

            // Set the title of the primary stage
            this.primaryStage.setTitle("NHPlus");

            // Set the resizable property of the primary stage to false
            this.primaryStage.setResizable(false);

            // Display the primary stage
            this.primaryStage.show();

            // Set an event handler for the close request of the primary stage
            this.primaryStage.setOnCloseRequest(e -> {
                // Close the database connection
                ConnectionBuilder.closeConnection();

                // Exit the application
                Platform.exit();
                System.exit(0);
            });
        } catch(Exception e) {
            // If an Exception occurs, print its stack trace
            e.printStackTrace();
        }
    }


    /**
     * This method creates a task that is executed every 24 hours.
     * The task is to call the deleteExpiredLocks() method of the TreatmentDao class,
     * which deletes all expired locks from the database.
     * The method uses a ScheduledExecutorService to schedule and execute the task.
     */
    private void scheduleTask() {
        // Create an instance of TreatmentDao
        TreatmentDao treatmentDao = new TreatmentDao(ConnectionBuilder.getConnection());
        PatientDao patientDao = new PatientDao(ConnectionBuilder.getConnection());
        CaregiverDao caregiverDao = new CaregiverDao(ConnectionBuilder.getConnection());

        // Define the task to be executed
        Runnable task = () -> {
            try {
                // Call the deleteExpiredLocks() method
                treatmentDao.deleteExpiredLocks();
                patientDao.deleteExpiredPatientLocks();
                caregiverDao.deleteExpiredCaregiverLocks();

            } catch (SQLException e) {
                // Error handling
                e.printStackTrace();
            }
        };

        // Schedule the task for immediate execution and then every 24 hours
        scheduler.scheduleAtFixedRate(task, 0, 24, TimeUnit.HOURS);
    }

    public static void main(String[] args) {launch(args);}
}