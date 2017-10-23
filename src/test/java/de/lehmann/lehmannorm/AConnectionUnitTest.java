package de.lehmann.lehmannorm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;

import de.lehmann.lehmannorm.models.IDatabaseConnectionInformations;

public abstract class AConnectionUnitTest extends AConnectionTemplate {

    private static final IDatabaseConnectionInformations CONNECTION_ACCESS_INFO =
            IDatabaseConnectionInformations.MARIA_DB;

    @Override
    protected Connection createConnection() throws SQLException {

        final String databaseUrl = CONNECTION_ACCESS_INFO.getDatabaseUrl()
                + "/"
                + CONNECTION_ACCESS_INFO.getDatabaseName()
                + "?useLegacyDatetimeCode=false&serverTimezone=UTC";

        final Connection connection = DriverManager.getConnection(databaseUrl,
                CONNECTION_ACCESS_INFO.getDatabaseUserName(), CONNECTION_ACCESS_INFO.getDatabasePassword());

        return connection;
    }

    @AfterEach
    public void doDestroyConnection() {

        if (connection != null)
            try {
                connection.close();
            } catch (final SQLException e) {

                connection = null;
            }
    }
}