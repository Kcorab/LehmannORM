package de.lehmann.lehmannorm.logic.sqlbuilder;

import static de.lehmann.lehmannorm.logic.sqlbuilder.IStatementBuilder.processEntityColumns;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

import de.lehmann.lehmannorm.entity.structure.EntityColumn;

public class SelectStatementBuilder implements IStatementBuilder {

    @Override
    public PreparedStatement buildStatement(final String tableName, final Set<EntityColumn<?>> entityColumns,
            final Connection connection) throws SQLException {

        final String columnNames = processEntityColumns(entityColumns);
        final String values = generateValues(entityColumns);

        return buildStatement(tableName, columnNames, values, connection);
    }

    @Override
    public PreparedStatement buildStatement(final String tableName, final String columnsSeperatedByComma,
            final String values,
            final Connection connection) throws SQLException {

        final StringBuilder selectQueryBuilder;
        selectQueryBuilder =
                new StringBuilder("SELECT ").append(columnsSeperatedByComma).append(" FROM ").append(tableName)
                        .append(" WHERE ").append(values).append("=?;");

        final PreparedStatement prepareStatement = connection.prepareStatement(selectQueryBuilder.toString());

        return prepareStatement;
    }

    @Override
    public String generateValues(final Set<EntityColumn<?>> entityColumns) {

        return entityColumns.iterator().next().columnName;
    }
}
