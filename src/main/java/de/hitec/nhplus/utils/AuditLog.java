package de.hitec.nhplus.utils;

import de.hitec.nhplus.model.Caregiver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This class is responsible for writing audit logs.
 * It contains two methods: writeLog and formatLogMessage.
 */
public class AuditLog {

    /**
     * This method writes a log message to a file.
     * The log message is formatted using the formatLogMessage method.
     * The file is named "AuditLog_" followed by the current date in the format "dd-MM-dd".
     * The file is located in the "logs" directory.
     * If the file does not exist, it is created. If it does exist, the log message is appended to the file.
     *
     * @param caregiver The caregiver who performed the action.
     * @param action The action that was performed.
     */
    public static void writeLog(Caregiver caregiver, String action) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-dd");
        String fileName = "logs/AuditLog_" + dtf.format(LocalDate.now()) + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(formatLogMessage(caregiver, action));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method formats a log message.
     * The message includes the current date and time, the caregiver's PID and username, and the action that was performed.
     * The date and time are formatted as "yyyy-MM-dd HH:mm:ss".
     *
     * @param caregiver The caregiver who performed the action.
     * @param action The action that was performed.
     * @return The formatted log message.
     */
    private static String formatLogMessage(Caregiver caregiver, String action) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = dtf.format(LocalDateTime.now());

        return String.format("[%s] Caretaker: %s %s; Performed Action: %s%n",
                currentDateTime, caregiver.getPid(), caregiver.getUsername(), action);
    }
}