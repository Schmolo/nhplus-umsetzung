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

    public Caregiver authenticate(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        Caregiver caregiver = null;
        try {
            Connection connection = ConnectionBuilder.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM caregiver WHERE username = ?");
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            String password_hash = resultSet.getString("password_hash");
            boolean password_match = PasswordUtil.checkPassword(password, password_hash);

            if (resultSet.next() && password_match) {
                caregiver = new Caregiver(
                        resultSet.getLong("pid"),
                        resultSet.getString("username"),
                        resultSet.getString("firstname"),
                        resultSet.getString("surname"),
                        DateConverter.convertStringToLocalDate(resultSet.getString("dateOfBirth")),
                        resultSet.getString("telephoneNumber"),
                        password_hash,
                        resultSet.getBoolean("isAdmin")
                );
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return caregiver;
    }
}