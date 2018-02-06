package de.lehmann.lehmannorm.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.lehmann.lehmannorm.AConnectionTest;
import de.lehmann.lehmannorm.entity.AbstractEntity;
import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo;
import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo.ForeignKeyHolder;
import de.lehmann.lehmannorm.stubs.ConnectionStub;
import de.lehmann.lehmannorm.stubs.PreparedStatementStub;

/**
 * @author Tim Lehmann
 */
public class DaoUnitTest extends AConnectionTest {

    // TEST STATIC METHODS

    @Test
    public void getOrCreateCachedDao() throws InstantiationException, IllegalAccessException, SQLException {

        Connection connection;

        connection = new ConnectionStub();
        final TestEntityA_1A testEntityA = new TestEntityA_1A();

        Assertions.assertEquals(
                Dao.getOrCreateCachedDao(connection, testEntityA.getClass()),
                Dao.getOrCreateCachedDao(connection, testEntityA.getClass()));

        connection = new ConnectionStub();
        final TestEntityB_1A testEntityB = new TestEntityB_1A();

        Assertions.assertEquals(
                Dao.getOrCreateCachedDao(connection, testEntityB.getClass()),
                Dao.getOrCreateCachedDao(connection, testEntityB.getClass()));
    }

    /*
     * CASE 1A
     *
     * A(aId, refB) [B have to exist]
     * B(bId, refC) [C have to exist]
     * C(cId)
     *
     * insert order: C, B, A
     */
    @Test
    public void insert1A() throws InstantiationException, IllegalAccessException, SQLException {

        final Dao<TestEntityA_1A, Integer> unitToTest =
                Dao.getOrCreateCachedDao(connection, TestEntityA_1A.class);

        final TestEntityC_1A entityC = new TestEntityC_1A();
        entityC.setPrimaryKeyValue(2);

        final TestEntityB_1A entityB = new TestEntityB_1A();
        entityB.setPrimaryKeyValue(1);
        entityB.setColumnValue(TestEntityB_1A.ID_C, entityC);

        final TestEntityA_1A entityA = new TestEntityA_1A();
        entityA.setPrimaryKeyValue(0);
        entityA.setColumnValue(TestEntityA_1A.ID_B, entityB);

        assertEquals(true, unitToTest.insert(entityA));

        final String expected = 2 + "," + 1 + "," + 0;

        assertEquals(expected, connection.toString());
    }

    /*
     * CASE 1B
     *
     * A(aId, refB, refC) [B and C have to exist]
     * B(bId)
     * C(cId)
     *
     * insert order: B, C, A
     */
    @Test
    public void insert1B() throws InstantiationException, IllegalAccessException, SQLException {

        final Dao<TestEntityA_1B, Integer> unitToTest =
                Dao.getOrCreateCachedDao(connection, TestEntityA_1B.class);

        final TestEntityC_1B entityC = new TestEntityC_1B();
        entityC.setPrimaryKeyValue(2);

        final TestEntityB_1B entityB = new TestEntityB_1B();
        entityB.setPrimaryKeyValue(1);

        final TestEntityA_1B entityA = new TestEntityA_1B();
        entityA.setPrimaryKeyValue(0);
        entityA.setColumnValue(TestEntityA_1B.ID_B, entityB);
        entityA.setColumnValue(TestEntityA_1B.ID_C, entityC);

        assertEquals(true, unitToTest.insert(entityA));

        final String expected = 1 + "," + 2 + "," + 0;

        assertEquals(expected, connection.toString());
    }

    @Override
    protected Connection createConnection() throws SQLException {

        return new ConnectionMock();
    }

    // # MOCKS

    // ## BEHAVIOR MOCKS

    private static class StatementMock extends PreparedStatementStub {

        private final ArrayList<Integer> entityInstanceCodes = new ArrayList<>();

        @Override
        public void setObject(final int parameterIndex, final Object x) throws SQLException {

            if (parameterIndex == 1)
                entityInstanceCodes.add((Integer) x);
        }

        @Override
        public String toString() {

            final StringBuilder sb = new StringBuilder();

            final Iterator<Integer> iterator = entityInstanceCodes.iterator();

            if (iterator.hasNext()) {

                sb.append(iterator.next());

                while (iterator.hasNext())
                    sb.append("," + iterator.next());
            }

            return sb.toString();
        }
    }

    private static class ConnectionMock extends ConnectionStub {

        private final PreparedStatement statement = new StatementMock();

        @Override
        public PreparedStatement prepareStatement(final String sql) throws SQLException {

            return statement;
        }

        @Override
        public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {

            return statement;
        }

        @Override
        public String toString() {

            return statement.toString();
        }
    }

    // ## DATA MOCKS

    // #### CASE 1A

    /*
     * A(aId, refB) [B have to exist]
     * B(bId, refC) [C have to exist]
     * C(cId)
     */

    private static class TestEntityA_1A extends AbstractEntity<Integer> {

        public final static EntityColumnInfo<Integer>        ID   = new EntityColumnInfo<>("ID", Integer.class);
        public final static EntityColumnInfo<TestEntityB_1A> ID_B =
                new EntityColumnInfo<>("ID_B", TestEntityB_1A.class, ForeignKeyHolder.THIS_ENTITY_TYPE);

        protected TestEntityA_1A() {
            super(ID, ID_B);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }

    private static class TestEntityB_1A extends AbstractEntity<Integer> {

        public final static EntityColumnInfo<Integer>        ID   = new EntityColumnInfo<>("ID", Integer.class);
        public final static EntityColumnInfo<TestEntityC_1A> ID_C =
                new EntityColumnInfo<>("ID_C", TestEntityC_1A.class, ForeignKeyHolder.THIS_ENTITY_TYPE);

        protected TestEntityB_1A() {
            super(ID, ID_C);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }

    private static class TestEntityC_1A extends AbstractEntity<Integer> {

        public final static EntityColumnInfo<Integer> ID = new EntityColumnInfo<>("ID", Integer.class);

        protected TestEntityC_1A() {
            super(ID);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }

    // #### CASE 1B

    /*
     * A(aId, refB, refC) [B and C have to exist]
     * B(bId)
     * C(cId)
     */

    private static class TestEntityA_1B extends AbstractEntity<Integer> {

        public final static EntityColumnInfo<Integer>        ID   = new EntityColumnInfo<>("ID", Integer.class);
        public final static EntityColumnInfo<TestEntityB_1B> ID_B =
                new EntityColumnInfo<>("ID_B", TestEntityB_1B.class, ForeignKeyHolder.THIS_ENTITY_TYPE);
        public final static EntityColumnInfo<TestEntityC_1B> ID_C =
                new EntityColumnInfo<>("ID_C", TestEntityC_1B.class, ForeignKeyHolder.THIS_ENTITY_TYPE);

        protected TestEntityA_1B() {
            super(ID, ID_B, ID_C);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }

    private static class TestEntityB_1B extends AbstractEntity<Integer> {

        public final static EntityColumnInfo<Integer> ID = new EntityColumnInfo<>("ID", Integer.class);

        protected TestEntityB_1B() {
            super(ID);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }

    private static class TestEntityC_1B extends AbstractEntity<Integer> {

        public final static EntityColumnInfo<Integer> ID = new EntityColumnInfo<>("ID", Integer.class);

        protected TestEntityC_1B() {
            super(ID);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }
}
