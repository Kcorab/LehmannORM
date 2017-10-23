package de.lehmann.lehmannorm;

import java.sql.Connection;
import java.sql.SQLException;

import de.lehmann.lehmannorm.mocks.ConnectionMock;

public class AConnectionMockUnitTest extends AConnectionTemplate {

    @Override
    protected Connection createConnection() throws SQLException {

        return new ConnectionMock();
    }
}
