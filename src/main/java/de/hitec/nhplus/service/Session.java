package de.hitec.nhplus.service;

import de.hitec.nhplus.model.Caregiver;

/**
 * This class represents a session in the application.
 * It uses the Singleton design pattern to ensure that only one session exists at any time.
 * The session stores the caregiver who is currently logged in.
 */
public class Session {
    // The single instance of the Session class
    private static Session instance;

    // The caregiver who is currently logged in
    private Caregiver loggedInCaregiver;

    /**
     * The private constructor prevents other classes from creating new instances of the Session class.
     */
    private Session() {}

    /**
     * This method returns the single instance of the Session class.
     * If the instance does not exist, it is created.
     *
     * @return The single instance of the Session class.
     */
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    /**
     * This method returns the caregiver who is currently logged in.
     *
     * @return The caregiver who is currently logged in.
     */
    public Caregiver getLoggedInCaregiver() {
        return loggedInCaregiver;
    }

    /**
     * This method sets the caregiver who is currently logged in.
     *
     * @param loggedInCaregiver The caregiver who is currently logged in.
     */
    public void setLoggedInCaregiver(Caregiver loggedInCaregiver) {
        this.loggedInCaregiver = loggedInCaregiver;
    }

    /**
     * This method logs out the current caregiver by setting the loggedInCaregiver to null.
     */
    public void logout() {
        loggedInCaregiver = null;
    }
}