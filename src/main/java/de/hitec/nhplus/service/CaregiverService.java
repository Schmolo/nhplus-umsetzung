package de.hitec.nhplus.service;

import de.hitec.nhplus.datastorage.ConnectionBuilder;
import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.utils.DateConverter;
import de.hitec.nhplus.utils.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class CaregiverService {

    public Caregiver authenticate(String firstname, String surname, String password) {
        Caregiver caregiver = null;
        try {
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM caregiver WHERE firstname = ? AND surname = ?");
            statement.setString(1, firstname);
            statement.setString(2, surname);

            ResultSet resultSet = statement.executeQuery();

            String password_hash = resultSet.getString("password_hash");
            boolean password_match = PasswordUtil.checkPassword(password, password_hash);

            if (resultSet.next() && password_match) {
                caregiver = new Caregiver(
                        resultSet.getLong("pid"),
                        resultSet.getString("firstname"),
                        resultSet.getString("surname"),
                        DateConverter.convertStringToLocalDate(resultSet.getString("dateOfBirth")),
                        password_hash
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return caregiver;
    }
}