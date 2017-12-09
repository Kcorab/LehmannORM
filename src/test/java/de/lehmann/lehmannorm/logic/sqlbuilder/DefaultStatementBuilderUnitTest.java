package de.lehmann.lehmannorm.logic.sqlbuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.lehmann.lehmannorm.AConnectionMockUnitTest;
import de.lehmann.lehmannorm.models.TestTableEntity;

public class DefaultStatementBuilderUnitTest extends AConnectionMockUnitTest {

    private static TestTableEntity entity;

    @BeforeAll
    public static void createExampleEntity() throws InstantiationException, IllegalAccessException, SQLException {

        entity = new TestTableEntity();
        entity.setPrimaryKeyValue(6);
        entity.setColumnValue(TestTableEntity.NUMBER, 1d);
        entity.setColumnValue(TestTableEntity.DESCRIPTION, "Eine andere Beschreibung.");
    }

    public static Object[][] parameters() {

        return new Object[][] { {
                IStatementBuilder.DEFAULT_INSERT_STATEMENT_BUILDER,
                "INSERT INTO TEST_TABLE(ID,NUMBER,DESCRIPTION) VALUES(?,?,?);"
        }, {
                IStatementBuilder.DEFAULT_SELECT_STATEMENT_BUILDER,
                "SELECT ID,NUMBER,DESCRIPTION FROM TEST_TABLE WHERE ID=?;"
        } };
    }

    @ParameterizedTest
    @MethodSource(value = "parameters")
    public void testString(final IStatementBuilder toTest, final String expected) throws SQLException {

        final PreparedStatement preparedStatement =
                toTest.buildStatement(entity.getTableName(), entity.getAllColumns().keySet(), connection);

        final String actual = preparedStatement.toString();

        assertEquals(expected, actual);
    }
}
