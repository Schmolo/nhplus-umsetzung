package de.hitec.nhplus.service;

import de.hitec.nhplus.model.Caregiver;

public class Session {
    private static Session instance;

    private Caregiver loggedInCaregiver;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public Caregiver getLoggedInCaregiver() {
        return loggedInCaregiver;
    }

    public void setLoggedInCaregiver(Caregiver loggedInCaregiver) {
        this.loggedInCaregiver = loggedInCaregiver;
    }

    public void logout() {
        loggedInCaregiver = null;
    }
}