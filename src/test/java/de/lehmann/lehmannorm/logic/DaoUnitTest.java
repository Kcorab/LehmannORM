package de.lehmann.lehmannorm.logic;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.lehmann.lehmannorm.AConnectionUnitTest;
import de.lehmann.lehmannorm.entity.AbstractEntity;
import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo;
import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo.ForeignKeyHolder;
import de.lehmann.lehmannorm.stubs.ConnectionStub;

public class DaoUnitTest extends AConnectionUnitTest {

    private Dao<AbstractEntity<Integer>, Integer> unitToTest;

    // TEST STATIC METHODS

    @Test
    public void getOrCreateCachedDao() throws InstantiationException, IllegalAccessException, SQLException {

        Connection connection;

        connection = new ConnectionStub();
        final TestEntityA testEntityA = new TestEntityA();

        Assertions.assertEquals(
                Dao.getOrCreateCachedDao(connection, testEntityA.getClass()),
                Dao.getOrCreateCachedDao(connection, testEntityA.getClass()));

        connection = new ConnectionStub();
        final TestEntityB testEntityB = new TestEntityB();

        Assertions.assertEquals(
                Dao.getOrCreateCachedDao(connection, testEntityB.getClass()),
                Dao.getOrCreateCachedDao(connection, testEntityB.getClass()));
    }

    @Override
    protected Connection createConnection() throws SQLException {

        return null;
    }

    // # MOCKS

    // ## DATA MOCKS

    private static class TestEntityA extends AbstractEntity<Integer> {

        public final static EntityColumnInfo<Integer>     ID   = new EntityColumnInfo<>("ID", Integer.class);
        public final static EntityColumnInfo<TestEntityB> ID_B =
                new EntityColumnInfo<>("ID_B", TestEntityB.class, ForeignKeyHolder.THIS_ENTITY_TYPE);

        protected TestEntityA() {
            super(ID, ID_B);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }

    private static class TestEntityB extends AbstractEntity<Integer> {

        public final static EntityColumnInfo<Integer>     ID   = new EntityColumnInfo<>("ID", Integer.class);
        public final static EntityColumnInfo<TestEntityC> ID_C =
                new EntityColumnInfo<>("ID_C", TestEntityC.class, ForeignKeyHolder.REFERENCED_ENTITY_TYPE);

        protected TestEntityB() {
            super(ID, ID_C);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }

    private static class TestEntityC extends AbstractEntity<Integer> {

        public final static EntityColumnInfo<Integer> ID = new EntityColumnInfo<>("ID", Integer.class);

        protected TestEntityC() {
            super(ID);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }
}
