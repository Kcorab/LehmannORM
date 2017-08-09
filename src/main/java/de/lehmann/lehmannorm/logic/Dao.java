package de.lehmann.lehmannorm.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import de.lehmann.lehmannorm.entity.AbstractEntity;
import de.lehmann.lehmannorm.entity.column.EntityColumn;

import java.util.Set;

public class Dao<ENTITY extends AbstractEntity<PRIMARY_KEY>, PRIMARY_KEY> {

    public static <ENTITY extends AbstractEntity<PRIMARY_KEY>, PRIMARY_KEY> Dao<ENTITY, PRIMARY_KEY> createInstance(
            final Connection connection, final Class<ENTITY> entityType)
            throws SQLException, InstantiationException, IllegalAccessException {

        return new Dao<>(connection, entityType);
    }

    // table name, STMNT_CREATE_COLUMNBASE

    private final Connection connection;

    private final PreparedStatement insertStatement;
    private final PreparedStatement selectStatement;

    private Dao(final Connection connection, final ENTITY entity) throws SQLException {

        this.connection = connection;

        final Set<EntityColumn<?>> entityColumns = entity.getAllColumns().keySet();
        final int columnCount = entityColumns.size() + 1; // plus one for primary key;

        final Iterator<EntityColumn<?>> it = entityColumns.iterator();

        // Put all column names in a string.

        final StringBuilder columnsBuilder = new StringBuilder(entity.getPrimaryKeyColumn().columnName);
        while (it.hasNext()) {
            final EntityColumn<?> entityColumn = it.next();
            columnsBuilder.append(",").append(entityColumn.columnName);
        }
        final String columnNames = columnsBuilder.toString();

        // Build string that respresented an insert query.

        final StringBuilder insertQueryBuilder;
        insertQueryBuilder = new StringBuilder("INSERT INTO ").append(entity.getTableName()).append("(")
                .append(columnNames).append(") VALUES(?");

        for (int i = 1; i < columnCount; i++)
            insertQueryBuilder.append(",?");

        insertQueryBuilder.append(");");

        insertStatement = connection.prepareStatement(insertQueryBuilder.toString());

        // Build string that respresented an get query.

        final StringBuilder selectQueryBuilder;
        selectQueryBuilder = new StringBuilder("SELECT ").append(columnNames).append(" FROM ")
                .append(entity.getTableName()).append(" WHERE ").append(entity.getPrimaryKeyColumn().columnName)
                .append(" == ?;");

        selectStatement = connection.prepareStatement(selectQueryBuilder.toString());

    }

    private Dao(final Connection connection, final Class<ENTITY> entityType)
            throws InstantiationException, IllegalAccessException, SQLException {

        this(connection, entityType.newInstance());
    }

    public boolean insert(final ENTITY entity) {

        final Map<EntityColumn<?>, Object> map = entity.getAllColumns();

        final Set<Entry<EntityColumn<?>, Object>> entrySet = map.entrySet();

        int i = 0;
        try {
            for (final Object element : entrySet)
                insertStatement.setObject(i++, element);
        } catch (final SQLException e) {

            e.printStackTrace();
        }

        try {
            final int id = insertStatement.executeUpdate();

        } catch (final SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

    public ENTITY getById(final PRIMARY_KEY primary_KEY) {

        return null;
    }
}
