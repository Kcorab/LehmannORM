package de.lehmann.lehmannorm.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.lehmann.lehmannorm.entity.AbstractEntity;
import de.lehmann.lehmannorm.entity.column.EntityColumn;

public class Dao<ENTITY extends AbstractEntity<PRIMARY_KEY>, PRIMARY_KEY> {

    public static <ENTITY extends AbstractEntity<PRIMARY_KEY>, PRIMARY_KEY> Dao<ENTITY, PRIMARY_KEY> createInstance(
            final Connection connection, final Class<ENTITY> entityType)
            throws SQLException, InstantiationException, IllegalAccessException {
        return new Dao<>(connection, entityType);
    }

    private final PreparedStatement insertStatement;
    private final PreparedStatement selectStatement;

    private Dao(final Connection connection, final ENTITY entity) throws SQLException {

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

        insertStatement = connection.prepareStatement(insertQueryBuilder.toString(), Statement.RETURN_GENERATED_KEYS);

        // Build string that respresented an get query.

        final StringBuilder selectQueryBuilder;
        selectQueryBuilder = new StringBuilder("SELECT ").append(columnNames).append(" FROM ")
                .append(entity.getTableName()).append(" WHERE ").append(entity.getPrimaryKeyColumn().columnName)
                .append(" = ?;");

        selectStatement = connection.prepareStatement(selectQueryBuilder.toString());

    }

    private Dao(final Connection connection, final Class<ENTITY> entityType)
            throws InstantiationException, IllegalAccessException, SQLException {

        this(connection, entityType.newInstance());
    }

    public boolean insert(final ENTITY entity) {

        boolean wasSuccess = false;

        final Map<EntityColumn<?>, Object> map = entity.getAllColumns();

        final Set<Entry<EntityColumn<?>, Object>> entrySet = map.entrySet();

        int i = 1;
        try {
            insertStatement.setObject(1, entity.getPrimaryKeyValue());
            for (final Entry<EntityColumn<?>, Object> entry : entrySet)
                insertStatement.setObject(++i, entry.getValue());
        } catch (final SQLException e) {

            e.printStackTrace();
        }

        try {
            insertStatement.executeUpdate();
            final ResultSet generatedKeys = insertStatement.getGeneratedKeys();

            if (generatedKeys != null && generatedKeys.next()) {

                final PRIMARY_KEY primaryKey = generatedKeys.getObject(1, entity.getPrimaryKeyColumn().columnType);
                entity.setPrimaryKeyValue(primaryKey);

                wasSuccess = true;
            }
        } catch (final SQLException e) {
        }

        return wasSuccess;
    }

    public boolean getEntityByPk(final ENTITY entity) {

        boolean wasSuccess = false;

        try {
            selectStatement.setObject(1, entity.getPrimaryKeyValue());
            final ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet != null && resultSet.next()) {

                final Set<Entry<EntityColumn<?>, Object>> entrySet = entity.getAllColumns().entrySet();

                int i = 1; // primary key already set; jump over
                for (final Entry<EntityColumn<?>, Object> entry : entrySet) {

                    final Object object = resultSet.getObject(++i, entry.getKey().columnType);
                    entry.setValue(object);
                }

                wasSuccess = true;
            }

        } catch (final SQLException e) {
        }

        return wasSuccess;
    }
}