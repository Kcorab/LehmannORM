package de.lehmann.lehmannorm.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Set;

import de.lehmann.lehmannorm.entity.structure.EntityColumn;

public interface IStatementBuilder {

    PreparedStatement buildStatement(String tableName, Set<EntityColumn<?>> entityColumns, final Connection connection)
            throws SQLException;

    public final static IStatementBuilder defaultInsertStatement = (tableName, entityColumns, connection) -> {

        final int columnCount = entityColumns.size();

        if (columnCount > 0) {
            final Iterator<EntityColumn<?>> it = entityColumns.iterator();

            // Put all column names in a string.

            final StringBuilder columnsBuilder = new StringBuilder(it.next().columnName);
            while (it.hasNext()) {
                final EntityColumn<?> entityColumn = it.next();
                columnsBuilder.append(",").append(entityColumn.columnName);
            }
            final String columnNames = columnsBuilder.toString();

            // Build string that respresented an insert query.

            final StringBuilder insertQueryBuilder;
            insertQueryBuilder = new StringBuilder("INSERT INTO ").append(tableName).append("(").append(columnNames)
                    .append(") VALUES(?");

            for (int i = 1; i < columnCount; i++)
                insertQueryBuilder.append(",?");

            insertQueryBuilder.append(");");

            final PreparedStatement prepareStatement = connection.prepareStatement(insertQueryBuilder.toString(),
                    Statement.RETURN_GENERATED_KEYS);

            return prepareStatement;
        }

        return null;
    };

    public final static IStatementBuilder defaultSelectStatement = (tableName, entityColumns, connection) -> {

        final int columnCount = entityColumns.size();

        if (columnCount > 0) {
            final Iterator<EntityColumn<?>> it = entityColumns.iterator();

            // Put all column names in a string.

            final EntityColumn<?> primaryKey = it.next();
            final StringBuilder columnsBuilder = new StringBuilder(primaryKey.columnName);
            while (it.hasNext()) {
                final EntityColumn<?> entityColumn = it.next();
                columnsBuilder.append(",").append(entityColumn.columnName);
            }
            final String columnNames = columnsBuilder.toString();

            // Build string that respresented an select query.

            final StringBuilder selectQueryBuilder;
            selectQueryBuilder = new StringBuilder("SELECT ").append(columnNames).append(" FROM ").append(tableName)
                    .append(" WHERE ").append(primaryKey.columnName).append("=?;");

            final PreparedStatement prepareStatement = connection.prepareStatement(selectQueryBuilder.toString());

            return prepareStatement;
        }

        return null;
    };
}
