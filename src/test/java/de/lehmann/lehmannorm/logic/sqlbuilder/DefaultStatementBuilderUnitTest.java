package de.lehmann.lehmannorm.logic.sqlbuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.lehmann.lehmannorm.AConnectionTest;
import de.lehmann.lehmannorm.entity.AbstractEntity;
import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo;
import de.lehmann.lehmannorm.stubs.ConnectionStub;
import de.lehmann.lehmannorm.stubs.PreparedStatementStub;

/**
 * @author Tim Lehmann
 */
public class DefaultStatementBuilderUnitTest extends AConnectionTest {

    private static TestEntityA entity;

    @BeforeAll
    public static void createExampleEntity() throws InstantiationException, IllegalAccessException, SQLException {

        entity = new TestEntityA();
        entity.setPrimaryKeyValue(6);
        entity.setColumnValue(TestEntityA.NUMBER, 1d);
        entity.setColumnValue(TestEntityA.DESCRIPTION, "desciption");
    }

    public static Object[][] parameters() {

        final IStatementBuilder insertStatementBuilder =
                IStatementBuilder.DefaultBuilderBundle.DEFAULT_INSERT_STATEMENT_BUILDER.getStatementBuilder();
        final IStatementBuilder selectStatementBuilder =
                IStatementBuilder.DefaultBuilderBundle.DEFAULT_SELECT_STATEMENT_BUILDER.getStatementBuilder();

        return new Object[][] { {
                insertStatementBuilder,
                "INSERT INTO TEST_ENTITY_A(ID,NUMBER,DESCRIPTION) VALUES(?,?,?);"
        }, {
                selectStatementBuilder,
                "SELECT ID,NUMBER,DESCRIPTION FROM TEST_ENTITY_A WHERE ID=?;"
        } };
    }

    @MethodSource(value = "parameters")
    @ParameterizedTest
    public void testString(final IStatementBuilder toTest, final String expected) throws SQLException {

        final PreparedStatement preparedStatement =
                toTest.buildStatement(entity.getTableName(), entity.getAllColumns().keySet(), connection);

        final String actual = preparedStatement.toString(); // toString returns the sqlString

        assertEquals(expected, actual);
    }

    @Override
    protected Connection createConnection() throws SQLException {

        return new ConnectionMock();
    }

    // # MOCKS

    // ## BEHAVIOR MOCKS

    private static class ConnectionMock extends ConnectionStub {

        @Override
        public PreparedStatement prepareStatement(final String sql) throws SQLException {

            return new PreparedStatementMock(sql);
        }

        @Override
        public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {

            return prepareStatement(sql);
        }
    }

    private static class PreparedStatementMock extends PreparedStatementStub {

        private final String sqlString;

        public PreparedStatementMock(final String sqlString) {

            this.sqlString = sqlString;
        }

        @Override
        public String toString() {

            return sqlString;
        }
    }

    // ## DATA MOCKS

    private static class TestEntityA extends AbstractEntity<Integer> {

        public final static String                    TABLE_NAME = "TEST_ENTITY_A";
        public final static EntityColumnInfo<Integer> ID         = new EntityColumnInfo<>("ID", Integer.class);
        // The next value has no column name because the foreign key is stored by the
        // referenced entity.
        public final static EntityColumnInfo<TestEntityB> REF_ID      = new EntityColumnInfo<>(TestEntityB.class);
        public final static EntityColumnInfo<Double>      NUMBER      = new EntityColumnInfo<>("NUMBER", Double.class);
        public final static EntityColumnInfo<String>      DESCRIPTION =
                new EntityColumnInfo<>("DESCRIPTION", String.class);

        public TestEntityA() {
            super(ID, REF_ID, NUMBER, DESCRIPTION);
        }

        @Override
        public String getTableName() {
            return TABLE_NAME;
        }
    }

    private static class TestEntityB extends AbstractEntity<Integer> {

        public final static String                        TABLE_NAME = "TEST_ENTITY_B";
        public final static EntityColumnInfo<Integer>     ID         = new EntityColumnInfo<>("ID", Integer.class);
        public final static EntityColumnInfo<TestEntityA> REF_ID     =
                new EntityColumnInfo<>("REF_ID", TestEntityA.class);

        public TestEntityB() {
            super(ID, REF_ID);
        }

        @Override
        public String getTableName() {
            return TABLE_NAME;
        }
    }
}
