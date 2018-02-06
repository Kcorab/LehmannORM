package de.lehmann.lehmannorm.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.lehmann.lehmannorm.ADatabaseConnectionSystemTest;
import de.lehmann.lehmannorm.entity.AbstractEntity;
import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo;

public class DaoSystemTest extends ADatabaseConnectionSystemTest {

    private Dao<AbstractEntity<Integer>, Integer> unitToTest;

    // HELPER METHODS

    /**
     * Delete the entity if exist manually.
     *
     * @param entity
     *            that should remove from database
     * @return affected rows
     * @throws SQLException
     */
    private int deleteEntityManuelly(final AbstractEntity<?> entity) throws SQLException {

        try (final Statement deleteStatement = connection.createStatement()) {

            return deleteStatement.executeUpdate(
                    String.format("DELETE FROM %s WHERE %s = %s",
                            entity.getTableName(),
                            entity.getPrimaryKeyInfo().columnName,
                            entity.getPrimaryKeyValue()));
        }
    }

    /**
     * @param entity
     *            with primary key value
     * @param columnCount
     *            count of wished column values
     * @return entity column values from database
     * @throws SQLException
     */
    private Object[] findEntityManuelly(final AbstractEntity<?> entity, final int columnCount) throws SQLException {

        try (final Statement selectStatement = connection.createStatement();
                ResultSet cursor = selectStatement.executeQuery(
                        String.format("SELECT * FROM %s WHERE %s = %s",
                                entity.getTableName(),
                                entity.getPrimaryKeyInfo().columnName,
                                entity.getPrimaryKeyValue()));) {

            final Object[] columnValues;

            int i = 0;

            if (cursor.next()) {
                columnValues = new Object[columnCount];

                while (i < columnCount)
                    columnValues[i++] = cursor.getObject(i);
            } else
                columnValues = null;

            return columnValues;
        }
    }

    private Object[] findEntityManuelly(final AbstractEntity<?> entity) throws SQLException {

        return findEntityManuelly(entity, 1);
    }

    // TEST PRIVATE METHODS

    @Test
    public void insertEntity() throws SQLException, InstantiationException, IllegalAccessException {

        // preprocessing

        final Dao<TestEntityA, Integer> unitToTest = Dao.getOrCreateCachedDao(connection, TestEntityA.class);
        final TestEntityA testEntityA = new TestEntityA();

        // Make the private method public for the test.
        Method method = null;

        try {
            method = Dao.class.getDeclaredMethod("insertEntity", AbstractEntity.class);
        } catch (NoSuchMethodException | SecurityException e) {

            Assertions.fail(e);
        }
        method.setAccessible(true);

        try {

            // test

            final Integer primaryKeyValue = 1;
            final String description = "A test entity.";
            testEntityA.setPrimaryKeyValue(primaryKeyValue);
            testEntityA.setColumnValue(TestEntityA.DESCRIPTION, description);

            // Delete the entity if exist manually.
            deleteEntityManuelly(testEntityA);

            // Be sure there is no entity with defined id.
            assertNull(findEntityManuelly(testEntityA));

            // Insert the entity by dao (run the insert algorithm).
            method.invoke(unitToTest, testEntityA);

            // Be sure there is entity with defined id now.
            final Object[] columnValues = findEntityManuelly(testEntityA, 2);

            assertEquals(columnValues[0], primaryKeyValue);
            assertEquals(columnValues[1], description);

        } catch (final Exception e) {

            Assertions.fail(e);
        }
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