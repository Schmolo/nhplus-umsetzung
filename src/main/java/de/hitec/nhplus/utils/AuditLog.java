package de.hitec.nhplus.utils;

import de.hitec.nhplus.model.Caregiver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLog {

    public static void writeLog(Caregiver caregiver, String action) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-dd");
        String fileName = "logs/AuditLog_" + dtf.format(LocalDate.now()) + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(formatLogMessage(caregiver, action));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String formatLogMessage(Caregiver caregiver, String action) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = dtf.format(LocalDateTime.now());

        return String.format("[%s] Caretaker: %s %s; Performed Action: %s%n",
                currentDateTime, caregiver.getPid(), caregiver.getUsername(), action);
    }
}