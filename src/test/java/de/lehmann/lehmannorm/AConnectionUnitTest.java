package de.lehmann.lehmannorm;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class AConnectionUnitTest {

    protected Connection connection;

    protected abstract Connection createConnection() throws SQLException;

    @BeforeEach
    public void doCreateConnection() throws InstantiationException, IllegalAccessException, SQLException {

        if (connection == null)
            connection = createConnection();
    }

    @AfterEach
    public void doCloseConnection() throws SQLException {

        if (connection != null && !connection.isClosed())
            connection.close();
    }
}
