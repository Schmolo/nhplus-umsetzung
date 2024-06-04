package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.Treatment;
import de.hitec.nhplus.utils.DateConverter;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the Interface <code>DaoImp</code>. Overrides methods to generate specific <code>PreparedStatements</code>,
 * to execute the specific SQL Statements.
 */
public class TreatmentDao extends DaoImp<Treatment> {

    /**
     * The constructor initiates an object of <code>TreatmentDao</code> and passes the connection to its super class.
     *
     * @param connection Object of <code>Connection</code> to execute the SQL-statements.
     */
    public TreatmentDao(Connection connection) {
        super(connection);
    }

    /**
     * Generates a <code>PreparedStatement</code> to persist the given object of <code>Treatment</code>.
     *
     * @param treatment Object of <code>Treatment</code> to persist.
     * @return <code>PreparedStatement</code> to insert the given patient.
     */
    @Override
    protected PreparedStatement getCreateStatement(Treatment treatment) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "INSERT INTO treatment (pid, treatment_date, begin, end, description, remark) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, treatment.getPid());
            preparedStatement.setString(2, treatment.getDate());
            preparedStatement.setString(3, treatment.getBegin());
            preparedStatement.setString(4, treatment.getEnd());
            preparedStatement.setString(5, treatment.getDescription());
            preparedStatement.setString(6, treatment.getRemarks());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    /**
     * Generates a <code>PreparedStatement</code> to query a treatment by a given treatment id (tid).
     *
     * @param tid Treatment id to query.
     * @return <code>PreparedStatement</code> to query the treatment.
     */
    @Override
    protected PreparedStatement getReadByIDStatement(long tid) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "SELECT * FROM treatment WHERE tid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, tid);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    /**
     * Maps a <code>ResultSet</code> of one treatment to an object of <code>Treatment</code>.
     *
     * @param result ResultSet with a single row. Columns will be mapped to an object of class <code>Treatment</code>.
     * @return Object of class <code>Treatment</code> with the data from the resultSet.
     */
    @Override
    protected Treatment getInstanceFromResultSet(ResultSet result) throws SQLException {
        LocalDate date = DateConverter.convertStringToLocalDate(result.getString(3));
        LocalTime begin = DateConverter.convertStringToLocalTime(result.getString(4));
        LocalTime end = DateConverter.convertStringToLocalTime(result.getString(5));
        return new Treatment(result.getLong(1), result.getLong(2),
                date, begin, end, result.getString(6), result.getString(7), result.getBoolean(8), result.getString(9));
    }

    /**
     * Generates a <code>PreparedStatement</code> to query all treatments.
     *
     * @return <code>PreparedStatement</code> to query all treatments.
     */
    @Override
    protected PreparedStatement getReadAllStatement() {
        PreparedStatement statement = null;
        try {
            final String SQL = "SELECT * FROM treatment WHERE locked is not true";
            statement = this.connection.prepareStatement(SQL);

            // Debug

            /* ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                System.out.println("Treatment ID: " + resultSet.getLong("tid"));
                System.out.println("Patient ID: " + resultSet.getLong("pid"));
                System.out.println("Treatment Date: " + resultSet.getString("treatment_date"));
                System.out.println("Begin: " + resultSet.getString("begin"));
                System.out.println("End: " + resultSet.getString("end"));
                System.out.println("Description: " + resultSet.getString("description"));
                System.out.println("Remark: " + resultSet.getString("remark"));
                System.out.println("Locked: " + resultSet.getBoolean("locked"));
                System.out.println("Lock Reason: " + resultSet.getString("lockedDate"));
                System.out.println("-----------------------------");
            }
            */

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return statement;
    }

    /**
     * Maps a <code>ResultSet</code> of all treatments to an <code>ArrayList</code> with objects of class
     * <code>Treatment</code>.
     *
     * @param result ResultSet with all rows. The columns will be mapped to objects of class <code>Treatment</code>.
     * @return <code>ArrayList</code> with objects of class <code>Treatment</code> of all rows in the
     * <code>ResultSet</code>.
     */
    @Override
    protected ArrayList<Treatment> getListFromResultSet(ResultSet result) throws SQLException {
        ArrayList<Treatment> list = new ArrayList<Treatment>();
        while (result.next()) {
            LocalDate date = DateConverter.convertStringToLocalDate(result.getString(3));
            LocalTime begin = DateConverter.convertStringToLocalTime(result.getString(4));
            LocalTime end = DateConverter.convertStringToLocalTime(result.getString(5));
            Treatment treatment = new Treatment(result.getLong(1), result.getLong(2),
                    date, begin, end, result.getString(6), result.getString(7), false, null);
            list.add(treatment);
        }
        return list;
    }

    /**
     * Generates a <code>PreparedStatement</code> to query all treatments of a patient with a given patient id (pid).
     *
     * @param pid Patient id to query all treatments referencing this id.
     * @return <code>PreparedStatement</code> to query all treatments of the given patient id (pid).
     */
    private PreparedStatement getReadAllTreatmentsOfOnePatientByPid(long pid) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "SELECT * FROM treatment WHERE pid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, pid);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    /**
     * Queries all treatments of a given patient id (pid) and maps the results to an <code>ArrayList</code> with
     * objects of class <code>Treatment</code>.
     *
     * @param pid Patient id to query all treatments referencing this id.
     * @return <code>ArrayList</code> with objects of class <code>Treatment</code> of all rows in the
     * <code>ResultSet</code>.
     */
    public List<Treatment> readTreatmentsByPid(long pid) throws SQLException {
        ResultSet result = getReadAllTreatmentsOfOnePatientByPid(pid).executeQuery();
        return getListFromResultSet(result);
    }

    /**
     * Generates a <code>PreparedStatement</code> to update the given treatment, identified
     * by the id of the treatment (tid).
     *
     * @param treatment Treatment object to update.
     * @return <code>PreparedStatement</code> to update the given treatment.
     */
    @Override
    protected PreparedStatement getUpdateStatement(Treatment treatment) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL =
                    "UPDATE treatment SET " +
                            "pid = ?, " +
                            "treatment_date = ?, " +
                            "begin = ?, " +
                            "end = ?, " +
                            "description = ?, " +
                            "remark = ? " +
                            "WHERE tid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, treatment.getPid());
            preparedStatement.setString(2, treatment.getDate());
            preparedStatement.setString(3, treatment.getBegin());
            preparedStatement.setString(4, treatment.getEnd());
            preparedStatement.setString(5, treatment.getDescription());
            preparedStatement.setString(6, treatment.getRemarks());
            preparedStatement.setLong(7, treatment.getTid());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    /**
     * Generates a <code>PreparedStatement</code> to delete a treatment with the given id.
     *
     * @param tid Id of the Treatment to delete.
     * @return <code>PreparedStatement</code> to delete treatment with the given id.
     */
    @Override
    protected PreparedStatement getDeleteStatement(long tid) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL =
                    "DELETE FROM treatment WHERE tid = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, tid);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }



    /**
     * This method locks a treatment for a specified period of time.
     * It updates the 'locked' field of the treatment record in the database to 1, indicating that the treatment is locked.
     * It also sets the 'lockedDate' field to the current date plus 10 years, indicating the date when the lock will expire.
     *
     * @param tid The ID of the treatment to be locked.
     * @throws SQLException if a database error occurs.
     */
    public void lockTreatment(long tid) throws SQLException {
        // Convert the treatment ID to an integer. This is a temporary solution and may need to be revised.
        int intValue = (int) tid;

        // Calculate the lock date as the current date plus 10 years.
        LocalDate lockDate = LocalDate.now().plusYears(10);

        // Prepare the SQL query to update the 'locked' and 'lockedDate' fields of the treatment record.
        String sql = "UPDATE treatment SET locked = 1, lockedDate = ? WHERE tid = ?";

        // Use a try-with-resources statement to automatically close the PreparedStatement object after use.
        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            // Set the 'lockedDate' parameter in the SQL query.
            pstmt.setDate(1, Date.valueOf(lockDate));

            // Set the 'tid' parameter in the SQL query.
            pstmt.setLong(2, tid);

            // Execute the SQL update query.
            pstmt.executeUpdate();
        }
    }



    /**
     * This method deletes all treatments whose lock date has expired.
     * It goes through all locked treatments in the database and checks if the current date is after the lock date.
     * If this is the case, the treatment is deleted.
     *
     * @throws SQLException if a database error occurs
     */
    public void deleteExpiredLocks() throws SQLException {
        // Debug
        // System.out.println("deleteExpiredLocks");

        // Retrieve the current date
        LocalDate currentDate = LocalDate.now();

        // Create SQL query to retrieve all locked treatments
        String sql = "SELECT * FROM treatment WHERE locked = 1";

        // Try-with-resources statement to ensure resources are properly released
        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            // Execute the query and retrieve the result
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                // Retrieve the lock date as a long value (milliseconds since the Unix epoch)
                long lockedDateMillis = resultSet.getLong("lockedDate");

                // Convert the long value to a LocalDate object
                LocalDate lockedDate = Instant.ofEpochMilli(lockedDateMillis).atZone(ZoneId.systemDefault()).toLocalDate();

                // Check if the current date is after the lock date
                if (currentDate.isAfter(lockedDate)) {
                    // Retrieve the treatment ID
                    long tid = resultSet.getLong("tid");

                    // Delete the treatment
                    deleteById(tid);
                }
            }
        }
    }
}