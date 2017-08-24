package logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.lehmann.lehmannorm.examples.entities.TestTableEntity;
import de.lehmann.lehmannorm.logic.Dao;

public class DaoUnitTest {

    private static final IDatabaseConnectionInformations CONN = IDatabaseConnectionInformations.MARIA_DB;

    TestTableEntity               entity;
    Dao<TestTableEntity, Integer> unitToTest;

    @Before
    public void createExampleEntity() throws InstantiationException, IllegalAccessException, SQLException {

        final String databaseUrl = CONN.getDatabaseUrl() + "/" + CONN.getDatabaseName()
                + "?useLegacyDatetimeCode=false&serverTimezone=UTC";
        final Connection connection = DriverManager.getConnection(databaseUrl, CONN.getDatabaseUserName(),
                CONN.getDatabasePassword());

        unitToTest = Dao.createInstance(connection, TestTableEntity.class);
        entity = new TestTableEntity();
        entity.setColumnValue(TestTableEntity.NUMBER, 43d);
        entity.setColumnValue(TestTableEntity.DESCRIPTION, "Eine Beschreibung.");
    }

    @Test
    public void testInsert() {

        Assert.assertTrue(unitToTest.insert(entity));
    }

    @Test
    public void testGet() {

        final TestTableEntity testTableEntity = new TestTableEntity();
        testTableEntity.setPrimaryKeyValue(1);

        Assert.assertTrue(unitToTest.getEntityByPk(testTableEntity));
    }
}
