package de.lehmann.lehmannorm.logic.sqlbuilder;

import static de.lehmann.lehmannorm.logic.sqlbuilder.IStatementBuilder.processEntityColumns;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import de.lehmann.lehmannorm.entity.structure.EntityColumn;

public class InsertStatementBuilder implements IStatementBuilder {

    @Override
    public PreparedStatement buildStatement(final String tableName, final Set<EntityColumn<?>> entityColumns,
            final Connection connection) throws SQLException {

        final String columnNames = processEntityColumns(entityColumns);
        final String unkownValues = generateValues(entityColumns);

        return buildStatement(tableName, columnNames, unkownValues, connection);
    }

    @Override
    public PreparedStatement buildStatement(final String tableName, final String columnsSeperatedByComma,
            final String values,
            final Connection connection) throws SQLException {

        final String insertQueryBuilder = "INSERT INTO "
                + tableName
                + "("
                + columnsSeperatedByComma
                + ")"
                + " VALUES"
                + values
                + ";";

        final PreparedStatement prepareStatement = connection.prepareStatement(insertQueryBuilder.toString(),
                Statement.RETURN_GENERATED_KEYS);

        return prepareStatement;
    }

    @Override
    public String generateValues(final Set<EntityColumn<?>> entityColumns) {

        final int columnCount = entityColumns.size();

        final StringBuilder valuesBuilder;
        valuesBuilder = new StringBuilder("(?");

        for (int i = 1; i < columnCount; i++)
            valuesBuilder.append(",?");

        valuesBuilder.append(")");

        return valuesBuilder.toString();
    }
}
