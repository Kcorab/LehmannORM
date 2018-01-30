package de.lehmann.lehmannorm.logic;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.lehmann.lehmannorm.AConnectionUnitTest;
import de.lehmann.lehmannorm.entity.AbstractEntity;
import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo;
import de.lehmann.lehmannorm.models.IDatabaseConnectionInformations;

public class DaoSystemTest extends AConnectionUnitTest {

    private Dao<AbstractEntity<Integer>, Integer> unitToTest;

    // TEST PRIVATE METHODS

    @Test
    public void insertEntity() throws SQLException {

        // Make the private method public for the test.
        Method method = null;

        try {
            method = Dao.class.getDeclaredMethod("insertEntity", AbstractEntity.class);
        } catch (NoSuchMethodException | SecurityException e) {

            Assertions.fail(e);
        }
        method.setAccessible(true);

        try {

            final Integer primaryKeyValue = 1;
            ResultSet cursor;
            // preprocessing

            // Delete the entity if exist manually.
            connection.createStatement().executeUpdate(
                    String.format("DELETE FROM %s WHERE %s = %s",
                            TestEntityA.TABLE_NAME,
                            TestEntityA.ID.columnName,
                            primaryKeyValue));

            // Be sure there is no entity with defined id.
            cursor = connection.createStatement().executeQuery(
                    String.format("SELECT * FROM %s WHERE %s = %s",
                            TestEntityA.TABLE_NAME,
                            TestEntityA.ID.columnName,
                            primaryKeyValue));

            assertFalse(cursor.next());
            cursor.close();

            // Insert the entity by dao.

            final Dao<TestEntityA, Integer> dao = Dao.getOrCreateCachedDao(connection, TestEntityA.class);
            final TestEntityA testEntityA = new TestEntityA();

            testEntityA.setPrimaryKeyValue(primaryKeyValue);
            testEntityA.setColumnValue(TestEntityA.DESCRIPTION, "A test entity.");

            method.invoke(dao, testEntityA);

            // postrocessing

            // Be sure there is entity with defined id now.
            cursor = connection.createStatement().executeQuery(
                    String.format("SELECT * FROM %s WHERE %s = %s",
                            TestEntityA.TABLE_NAME,
                            TestEntityA.ID.columnName,
                            primaryKeyValue));

            assertTrue(cursor.next());
            assertEquals(cursor.getObject(1), primaryKeyValue);
            assertEquals(cursor.getObject(2), "A test entity.");

            cursor.close();

        } catch (final Exception e) {

            Assertions.fail(e);
        } finally {

            try {
                connection.close();
            } catch (final SQLException e) {
                Assertions.fail(e);
            }
        }
    }

    @Override
    protected Connection createConnection() {

        final IDatabaseConnectionInformations idci = IDatabaseConnectionInformations.MARIA_DB;

        final String connectionString = idci.getDatabaseUrl() + "/" + idci.getDatabaseName() + "?" +
                "user=" + idci.getDatabaseUserName() + "&" +
                "password=" + idci.getDatabasePassword();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(connectionString);
        } catch (final SQLException e) {
            Assertions.fail(e);
        }

        return connection;
    }

    // # MOCKS

    // ## DATA MOCKS

    private static class TestEntityA extends AbstractEntity<Integer> {

        public static final String                    TABLE_NAME  = "TEST_TABLE_A";
        public static final EntityColumnInfo<Integer> ID          =
                new EntityColumnInfo<>("ID", Integer.class);
        public static final EntityColumnInfo<String>  DESCRIPTION =
                new EntityColumnInfo<>("DESCRIPTION", String.class);

        protected TestEntityA() {
            super(ID, DESCRIPTION);
        }

        @Override
        public String getTableName() {
            return TestEntityA.TABLE_NAME;
        }
    }
}