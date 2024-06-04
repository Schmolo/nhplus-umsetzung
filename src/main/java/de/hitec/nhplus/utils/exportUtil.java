package de.hitec.nhplus.utils;

import de.hitec.nhplus.model.Patient;
import javafx.collections.ObservableList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class provides utility methods for exporting data.
 */
public class exportUtil {

    /**
     * This method exports a list of patients to a CSV file.
     * It first creates a temporary file for the CSV export.
     * Then it writes the header to the CSV file.
     * After that, it writes the data of each patient to the CSV file.
     * Finally, it opens the temporary file using the default system application.
     *
     * @param patients The list of patients to be exported.
     * @throws IOException If an I/O error occurs.
     */
    public static void exportToCSV(ObservableList<Patient> patients) throws IOException {
        // Create a temporary file for CSV export
        File tempFile = File.createTempFile("export", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String[] header = { "ID", "First Name", "Surname", "Date of Birth", "Care Level", "Room Number" };

            // Write the header to the CSV file
            for (String s : header) {
                writer.write(s + "; ");
            }
            writer.newLine();

            // Write the data to the CSV file
            for (Patient patient : patients) {
                String[] data = {
                        String.valueOf(patient.getPid()),
                        patient.getFirstName(),
                        patient.getSurname(),
                        patient.getDateOfBirth().toString(),
                        patient.getCareLevel(),
                        patient.getRoomNumber()
                };

                for (String datum : data) {
                    writer.write(datum + "; ");
                }
                writer.newLine();
            }
        }
        // Open the temporary file
        openFile(tempFile);
    }

    /**
     * This method opens a file using the default system application.
     * It first checks if the desktop is supported.
     * If it is, it opens the file.
     *
     * @param file The file to be opened.
     * @throws IOException If an I/O error occurs.
     */
    private static void openFile(File file) throws IOException {
        if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop.getDesktop().open(file);
        }
    }
}