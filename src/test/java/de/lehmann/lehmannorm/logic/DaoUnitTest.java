package de.lehmann.lehmannorm.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import de.lehmann.lehmannorm.AConnectionTest;
import de.lehmann.lehmannorm.entity.AbstractEntity;
import de.lehmann.lehmannorm.entity.structure.EntityToOneColumnInfo;
import de.lehmann.lehmannorm.stubs.ConnectionStub;
import de.lehmann.lehmannorm.stubs.PreparedStatementStub;

/**
 * @author Tim Lehmann
 */
public class DaoUnitTest extends AConnectionTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DaoUnitTest.class);

    // TEST STATIC METHODS

    @Test
    public void getOrCreateCachedDao() throws InstantiationException, IllegalAccessException, SQLException {

        Connection connection;

        final EntityToOneColumnInfo<Integer> id = TestEntityA_1A.ID;

        final EntityToOneColumnInfo<?> idUnkown = id;

        @SuppressWarnings("unchecked")
        final EntityToOneColumnInfo<Object> idObject = (EntityToOneColumnInfo<Object>) idUnkown;

        final Class<Object> columnType = idObject.columnType;

        LOGGER.debug(columnType::getName);

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

        unitToTest.insert(entityA);

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

        unitToTest.insert(entityA);

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

        private final List<Object>     columnValues = new ArrayList<>(5);
        private final Consumer<Object> callback;

        public StatementMock(final Consumer<Object> callback) {
            super();
            this.callback = callback;
        }

        @Override
        public void setObject(final int parameterIndex, final Object x) throws SQLException {

            columnValues.add(x);
        }

        @Override
        public boolean execute() throws SQLException {

            callback.accept(columnValues.get(0));

            return true;
        }
    }

    private static class ConnectionMock extends ConnectionStub {

        private final List<Object> executeOrder = new ArrayList<>();

        @Override
        public PreparedStatement prepareStatement(final String sql) throws SQLException {

            return new StatementMock((final Object o) -> {
                executeOrder.add(o);
            });
        }

        @Override
        public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {

            return new StatementMock((final Object o) -> {
                executeOrder.add(o);
            });
        }

        @Override
        public String toString() {

            final Iterator<Object> iterator = executeOrder.iterator();
            final StringBuilder order = new StringBuilder();

            if (iterator.hasNext())
                order.append(iterator.next());

            while (iterator.hasNext())
                order.append(",").append(iterator.next());

            return order.toString();
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

        public final static EntityToOneColumnInfo<Integer>        ID   = new EntityToOneColumnInfo<>("ID", Integer.class);
        public final static EntityToOneColumnInfo<TestEntityB_1A> ID_B =
                new EntityToOneColumnInfo<>("ID_B", TestEntityB_1A.class);

        protected TestEntityA_1A() {
            super(ID, ID_B);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }

    private static class TestEntityB_1A extends AbstractEntity<Integer> {

        public final static EntityToOneColumnInfo<Integer>        ID    = new EntityToOneColumnInfo<>("ID", Integer.class);
        public final static EntityToOneColumnInfo<TestEntityA_1A> REF_A = new EntityToOneColumnInfo<>(TestEntityA_1A.class);
        public final static EntityToOneColumnInfo<TestEntityC_1A> ID_C  =
                new EntityToOneColumnInfo<>("ID_C", TestEntityC_1A.class);

        protected TestEntityB_1A() {
            super(ID, REF_A, ID_C);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }

    private static class TestEntityC_1A extends AbstractEntity<Integer> {

        public final static EntityToOneColumnInfo<Integer>        ID    = new EntityToOneColumnInfo<>("ID", Integer.class);
        public final static EntityToOneColumnInfo<TestEntityB_1A> REF_B = new EntityToOneColumnInfo<>(TestEntityB_1A.class);

        protected TestEntityC_1A() {
            super(ID, REF_B);
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

        public final static EntityToOneColumnInfo<Integer>        ID   = new EntityToOneColumnInfo<>("ID", Integer.class);
        public final static EntityToOneColumnInfo<TestEntityB_1B> ID_B =
                new EntityToOneColumnInfo<>("ID_B", TestEntityB_1B.class);
        public final static EntityToOneColumnInfo<TestEntityC_1B> ID_C =
                new EntityToOneColumnInfo<>("ID_C", TestEntityC_1B.class);

        protected TestEntityA_1B() {
            super(ID, ID_B, ID_C);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }

    private static class TestEntityB_1B extends AbstractEntity<Integer> {

        public final static EntityToOneColumnInfo<Integer>        ID    = new EntityToOneColumnInfo<>("ID", Integer.class);
        public final static EntityToOneColumnInfo<TestEntityA_1B> REF_A = new EntityToOneColumnInfo<>(TestEntityA_1B.class);

        protected TestEntityB_1B() {
            super(ID, REF_A);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }

    private static class TestEntityC_1B extends AbstractEntity<Integer> {

        public final static EntityToOneColumnInfo<Integer>        ID    = new EntityToOneColumnInfo<>("ID", Integer.class);
        public final static EntityToOneColumnInfo<TestEntityA_1B> REF_A = new EntityToOneColumnInfo<>(TestEntityA_1B.class);

        protected TestEntityC_1B() {
            super(ID, REF_A);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }
}
