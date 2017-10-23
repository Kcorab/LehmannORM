package de.lehmann.lehmannorm;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;

public abstract class AConnectionTemplate {

    protected Connection connection;

    protected abstract Connection createConnection() throws SQLException;

    @BeforeEach
    public void doCreateConnection() throws InstantiationException, IllegalAccessException, SQLException {

        connection = createConnection();
    }
}
