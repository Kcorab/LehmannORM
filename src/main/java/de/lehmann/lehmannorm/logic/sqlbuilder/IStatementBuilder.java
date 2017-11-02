package de.lehmann.lehmannorm.logic.sqlbuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

import de.lehmann.lehmannorm.entity.structure.EntityColumn;

public interface IStatementBuilder {

    PreparedStatement buildStatement(final String tableName, final Set<EntityColumn<?>> entityColumns,
            final Connection connection)
            throws SQLException;

    PreparedStatement buildStatement(final String tableName, final String columnsSeperatedByComma, final String values,
            final Connection connection)
            throws SQLException;

    String generateValues(final Set<EntityColumn<?>> entityColumns);

    static final IStatementBuilder DEFAULT_INSERT_STATEMENT_BUILDER = new InsertStatementBuilder();

    static final IStatementBuilder DEFAULT_SELECT_STATEMENT_BUILDER = new SelectStatementBuilder();

    /**
     * @param entityColumns
     * @return column names seperated by ',' in brackets
     */
    static String processEntityColumns(final Set<EntityColumn<?>> entityColumns) {

        final StringBuilder columnsBuilder;
        final Iterator<EntityColumn<?>> it = entityColumns.iterator();

        // Put all column names in a string.

        columnsBuilder = new StringBuilder(it.next().columnName);
        while (it.hasNext()) {
            final EntityColumn<?> entityColumn = it.next();
            columnsBuilder.append(",").append(entityColumn.columnName);
        }

        return columnsBuilder.toString();
    }

    public static enum DefaultBuilderBundle {

        DEFAULT_INSERT_STATEMENT_BUILDER(new InsertStatementBuilder()),

        DEFAULT_SELECT_STATEMENT_BUILDER(new SelectStatementBuilder());

        private final IStatementBuilder param;

        private DefaultBuilderBundle(final IStatementBuilder param) {
            this.param = param;
        }

        public IStatementBuilder getStatementBuilder() {
            return param;
        }
    }
}