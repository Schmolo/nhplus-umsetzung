package de.hitec.nhplus.utils;

import de.hitec.nhplus.model.Patient;
import javafx.collections.ObservableList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class exportUtil {

    public static void exportToCSV(ObservableList<Patient> patients) throws IOException {
        // Create a temporary file for CSV export
        File tempFile = File.createTempFile("export", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String[] header = { "ID", "First Name", "Surname", "Date of Birth", "Care Level", "Room Number" };

            // Write the header to the CSV file
            for (int i = 0; i < header.length; i++) {
                writer.write(header[i] + ", ");
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

                for (int i = 0; i < data.length; i++) {
                    writer.write(data[i] + ", ");
                }
                writer.newLine();
            }
        }
        // Open the temporary file
        openFile(tempFile);
    }

    // Method to open a file using the default system application
    private static void openFile(File file) throws IOException {
        if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop.getDesktop().open(file);
        }
    }
}