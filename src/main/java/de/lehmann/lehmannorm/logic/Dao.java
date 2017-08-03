package de.lehmann.lehmannorm.logic;

import java.sql.Connection;
import java.util.Iterator;
import java.util.Set;

import de.lehmann.lehmannorm.entities.AbstractEntity;
import de.lehmann.lehmannorm.entities.EntityColumn;

public class Dao<ENTITY extends AbstractEntity<PRIMARY_KEY>, PRIMARY_KEY> {

    public static <ENTITY extends AbstractEntity<PRIMARY_KEY>, PRIMARY_KEY> Dao<ENTITY, PRIMARY_KEY> createInstance(
            final Connection connection, final Class<ENTITY> entityType) {

        try {
            return new Dao<>(connection, entityType);
        } catch (final InstantiationException e) {

            e.printStackTrace();
        } catch (final IllegalAccessException e) {

            e.printStackTrace();
        }

        return null;
    }

    // table name, STMNT_CREATE_COLUMNBASE

    private final Connection connection;

    private final String insertQuery;
    private final String selectQuery;

    private Dao(final Connection connection, final ENTITY entity) {

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

        insertQuery = insertQueryBuilder.toString();

        // Build string that respresented an get query.

        final StringBuilder selectQueryBuilder;
        selectQueryBuilder = new StringBuilder("SELECT ").append(columnNames).append(" FROM ")
                .append(entity.getTableName()).append(" WHERE ").append(entity.getPrimaryKeyColumn().columnName)
                .append(" == ?;");

        selectQuery = selectQueryBuilder.toString();

    }

    private Dao(final Connection connection, final Class<ENTITY> entityType)
            throws InstantiationException, IllegalAccessException {

        this(connection, entityType.newInstance());
    }

    public boolean insert(final ENTITY entity) {

        return false;
    }

    public ENTITY getById(final PRIMARY_KEY primary_KEY) {

        return null;
    }
}
