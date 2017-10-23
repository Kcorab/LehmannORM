package de.lehmann.lehmannorm.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Tim Lehmann
 */
public class InitUnitTest {

    private final String user       = "root";
    private final String password   = "+";
    private final String dbName     = "SYNTHETIC_HEART";
    private final String dbms       = "mysql";
    private final String serverName = "localhost";
    private final String portNumber = "3306";

    private final String select = "SELECT `NUMBER` FROM `TEST_TABLE`;";

    private Connection connection = null;

    @BeforeAll
    public static void initDriver() throws ClassNotFoundException {

    }

    private Connection getConnection() throws SQLException {

        Connection conn = null;
        final Properties connectionProps = new Properties();
        connectionProps.put("user", user);
        connectionProps.put("password", password);

        if (dbms.equals("mysql"))
            conn = DriverManager.getConnection("jdbc:" + dbms + "://" + serverName + ":" + portNumber + "/" + dbName
                    + "?useLegacyDatetimeCode=false&serverTimezone=UTC", connectionProps);

        System.out.println("Connected to database");
        return conn;
    }

    @BeforeEach
    public void init() throws SQLException {

        connection = getConnection();

    }

    @Test
    public List<Integer> helloWorld() {

        final List<Integer> values = new ArrayList<>();

        ResultSet resultSet = null;

        try {
            resultSet = connection.createStatement().executeQuery(select);
        } catch (final SQLException e) {
            Assertions.fail("");
        }

        if (resultSet != null)
            try {
                while (resultSet.next())
                    values.add(resultSet.getObject(1, Integer.class));
            } catch (final SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    resultSet.close();
                } catch (final SQLException e) {
                    e.printStackTrace();
                } finally {
                    resultSet = null;
                }
            }

        return values;

    }

    @AfterEach
    public void finish() throws SQLException {

        if (connection != null && !connection.isClosed())
            connection.close();

    }
}
