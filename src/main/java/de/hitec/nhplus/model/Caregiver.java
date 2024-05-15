package de.hitec.nhplus.model;

import de.hitec.nhplus.utils.DateConverter;
import de.hitec.nhplus.utils.PasswordUtil;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public class Caregiver extends Person {
    private SimpleLongProperty pid;
    private final SimpleStringProperty dateOfBirth;
    private final SimpleStringProperty password_hash;

    /**
     * This is a constructor for the Caregiver class.
     *
     * @param firstName The first name of the Caregiver.
     * @param surname The surname of the Caregiver.
     * @param dateOfBirth The date of birth of the Caregiver. It is a LocalDate object.
     * @param password The password for the Caregiver. This is not the actual password, but a string that will be hashed to create the password.
     */
    public Caregiver(String firstName, String surname, LocalDate dateOfBirth, String password) {
        super(firstName, surname);
        this.dateOfBirth = new SimpleStringProperty(DateConverter.convertLocalDateToString(dateOfBirth));
        this.password_hash = new SimpleStringProperty(PasswordUtil.generatePassword(password));
    }

    /**
     * This is an overloaded constructor for the Caregiver class.
     *
     * @param pid The unique identifier for the Caregiver.
     * @param firstName The first name of the Caregiver.
     * @param surname The surname of the Caregiver.
     * @param dateOfBirth The date of birth of the Caregiver. It is a LocalDate object.
     * @param password_hash The password hash for the Caregiver.
     */
    public Caregiver(long pid, String firstName, String surname, LocalDate dateOfBirth, String password_hash) {
        super(firstName, surname);
        this.pid = new SimpleLongProperty(pid);
        this.dateOfBirth = new SimpleStringProperty(DateConverter.convertLocalDateToString(dateOfBirth));
        this.password_hash = new SimpleStringProperty(password_hash);
    }

    public long getPid() {
        return pid.get();
    }

    public SimpleLongProperty pidProperty() {
        return pid;
    }

    public String getDateOfBirth() {
        return dateOfBirth.get();
    }

    public SimpleStringProperty dateOfBirthProperty() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth.set(dateOfBirth);
    }

    public String getPassword_hash() {
        return password_hash.get();
    }

    public SimpleStringProperty password_hashProperty() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash.set(password_hash);
    }

    @Override
    public String toString() {
        return "Caregiver\n" +
                "pid: " + pid.get() + "\n" +
                "firstName: " + getFirstName() + "\n" +
                "surname: " + getSurname() + "\n" +
                "dateOfBirth: " + dateOfBirth.get() + "\n" +
                "\n";
    }
}
