package de.lehmann.lehmannorm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import de.lehmann.lehmannorm.models.IDatabaseConnectionInformations;

/**
 * @author Tim Lehmann
 */
public abstract class ADatabaseConnectionSystemTest extends AConnectionTest
{
  private static final IDatabaseConnectionInformations CONNECTION_ACCESS_INFO =
      IDatabaseConnectionInformations.MARIA_DB;

  @Override
  protected Connection createConnection()
  {
    final String connectionString =
        CONNECTION_ACCESS_INFO.getDatabaseUrl() + "/" + CONNECTION_ACCESS_INFO.getDatabaseName() + "?" +
            "user=" + CONNECTION_ACCESS_INFO.getDatabaseUserName() + "&" +
            "password=" + CONNECTION_ACCESS_INFO.getDatabasePassword();

    Connection connection = null;

    try
    {
      connection = DriverManager.getConnection(connectionString);
    }
    catch (final SQLException e)
    {
      Assertions.fail(e);
    }

    return connection;
  }

  @Override
  @BeforeEach
  public void doCreateConnection() throws InstantiationException, IllegalAccessException, SQLException
  {
    if (connection == null) super.doCreateConnection();
  }
}