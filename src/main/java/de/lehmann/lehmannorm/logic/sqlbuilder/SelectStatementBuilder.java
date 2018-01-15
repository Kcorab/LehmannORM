package de.lehmann.lehmannorm.logic.sqlbuilder;

import static de.lehmann.lehmannorm.logic.sqlbuilder.IStatementBuilder.processEntityColumns;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo;

public class SelectStatementBuilder implements IStatementBuilder {

    @Override
    public PreparedStatement buildStatement(final String tableName,
            final Set<EntityColumnInfo<Object>> entityColumnInfos,
            final Connection connection) throws SQLException {

        final String columnNames = processEntityColumns(entityColumnInfos);
        final String primaryKeyName = generateStatementTail(entityColumnInfos);

        return buildStatement(tableName, columnNames, primaryKeyName, connection);
    }

    @Override
    public PreparedStatement buildStatement(final String tableName, final String columnsSeperatedByComma,
            final String tail,
            final Connection connection) throws SQLException {

        final StringBuilder selectQueryBuilder;
        selectQueryBuilder =
                new StringBuilder("SELECT ").append(columnsSeperatedByComma).append(" FROM ").append(tableName)
                        .append(" WHERE ").append(tail).append("=?;");

        return connection.prepareStatement(selectQueryBuilder.toString());
    }

    @Override
    public String generateStatementTail(final Set<EntityColumnInfo<Object>> entityColumnInfos) {

        return entityColumnInfos.iterator().next().columnName;
    }
}
