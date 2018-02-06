package de.lehmann.lehmannorm.logic.sqlbuilder;

import static de.lehmann.lehmannorm.logic.sqlbuilder.IStatementBuilder.processEntityColumns;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo;

/**
 * @author Tim Lehmann
 */
public class InsertStatementBuilder implements IStatementBuilder {

    @Override
    public PreparedStatement buildStatement(final String tableName,
            final Set<EntityColumnInfo<Object>> entityColumnInfos,
            final Connection connection) throws SQLException {

        final String columnNames = processEntityColumns(entityColumnInfos);
        final String unkownValues = generateStatementTail(entityColumnInfos);

        return buildStatement(tableName, columnNames, unkownValues, connection);
    }

    @Override
    public PreparedStatement buildStatement(final String tableName, final String columnsSeperatedByComma,
            final String tail,
            final Connection connection) throws SQLException {

        final String insertQuery = "INSERT INTO "
                + tableName
                + "("
                + columnsSeperatedByComma
                + ")"
                + " VALUES"
                + tail
                + ";";

        return connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
    }

    @Override
    public String generateStatementTail(final Set<EntityColumnInfo<Object>> entityColumnInfos) {

        final int columnCount = entityColumnInfos.size();

        final StringBuilder valuesBuilder;
        valuesBuilder = new StringBuilder("(?");

        for (int i = 1; i < columnCount; i++)
            valuesBuilder.append(",?");

        valuesBuilder.append(")");

        return valuesBuilder.toString();
    }
}
