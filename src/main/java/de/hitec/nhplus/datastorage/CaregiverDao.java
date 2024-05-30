package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.utils.DateConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CaregiverDao extends DaoImp<Caregiver>{
    public CaregiverDao(Connection connection) {
        super(connection);
    }

    @Override
    protected PreparedStatement getCreateStatement(Caregiver caregiver) {
        final String SQL = "INSERT INTO caregiver (username, firstname, surname, dateOfBirth, telephoneNumber, password_hash, isAdmin) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        return prepareStatement(SQL, caregiver);
    }

    private PreparedStatement prepareStatement(String sql, Caregiver caregiver) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, caregiver.getUsername());
            preparedStatement.setString(2, caregiver.getFirstName());
            preparedStatement.setString(3, caregiver.getSurname());
            preparedStatement.setString(4, caregiver.getDateOfBirth());
            preparedStatement.setString(5, caregiver.getTelephoneNumber());
            preparedStatement.setString(6, caregiver.getPassword_hash());
            preparedStatement.setBoolean(7, caregiver.isAdmin());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected PreparedStatement getReadByIDStatement(long key) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "SELECT * FROM caregiver WHERE pid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, key);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    @Override
    protected Caregiver getInstanceFromResultSet(ResultSet set) throws SQLException {
        return new Caregiver(
                set.getLong("pid"),
                set.getString("username"),
                set.getString("firstname"),
                set.getString("surname"),
                DateConverter.convertStringToLocalDate(set.getString("dateOfBirth")),
                set.getString("telephoneNumber"),
                set.getString("password_hash"),
                set.getBoolean("isAdmin")
        );
    }

    @Override
    protected PreparedStatement getReadAllStatement() {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "SELECT * FROM caregiver";
            preparedStatement = this.connection.prepareStatement(SQL);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected ArrayList<Caregiver> getListFromResultSet(ResultSet set) throws SQLException {
        ArrayList<Caregiver> caregivers = new ArrayList<>();
        while (set.next()) {
            caregivers.add(getInstanceFromResultSet(set));
        }
        return caregivers;
    }

    @Override
    protected PreparedStatement getUpdateStatement(Caregiver caregiver) {
        final String SQL = "UPDATE caregiver SET " +
                "username = ?, " +
                "firstname = ?, " +
                "surname = ?, " +
                "dateOfBirth = ?, " +
                "telephoneNumber = ?, " +
                "password_hash = ?, " +
                "isAdmin = ? " +
                "WHERE pid = ?";
        return prepareStatement(SQL, caregiver);
    }

    @Override
    protected PreparedStatement getDeleteStatement(long key) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "DELETE FROM caregiver WHERE pid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, key);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }
}
