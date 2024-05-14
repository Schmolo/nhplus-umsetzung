package de.hitec.nhplus.model;

import de.hitec.nhplus.utils.DateConverter;
import de.hitec.nhplus.utils.PasswordUtil;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public class Pflegekraft extends Person {
    private SimpleLongProperty pid;
    private final SimpleStringProperty dateOfBirth;
    private final SimpleStringProperty password_hash;

    /**
     * This is a constructor for the Pflegekraft class.
     *
     * @param firstName The first name of the Pflegekraft.
     * @param surname The surname of the Pflegekraft.
     * @param dateOfBirth The date of birth of the Pflegekraft. It is a LocalDate object.
     * @param password The password for the Pflegekraft. This is not the actual password, but a string that will be hashed to create the password.
     */
    public Pflegekraft(String firstName, String surname, LocalDate dateOfBirth, String password) {
        super(firstName, surname);
        this.dateOfBirth = new SimpleStringProperty(DateConverter.convertLocalDateToString(dateOfBirth));
        this.password_hash = new SimpleStringProperty(PasswordUtil.generatePassword(password));
    }

    /**
     * This is an overloaded constructor for the Pflegekraft class.
     *
     * @param pid The unique identifier for the Pflegekraft.
     * @param firstName The first name of the Pflegekraft.
     * @param surname The surname of the Pflegekraft.
     * @param dateOfBirth The date of birth of the Pflegekraft. It is a LocalDate object.
     * @param password_hash The password hash for the Pflegekraft.
     */
    public Pflegekraft(long pid, String firstName, String surname, LocalDate dateOfBirth, String password_hash) {
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
        return "Pflegekraft\n" +
                "pid: " + pid.get() + "\n" +
                "firstName: " + getFirstName() + "\n" +
                "surname: " + getSurname() + "\n" +
                "dateOfBirth: " + dateOfBirth.get() + "\n" +
                "\n";
    }
}
