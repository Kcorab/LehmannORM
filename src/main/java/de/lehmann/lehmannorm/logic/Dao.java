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
import de.lehmann.lehmannorm.entity.structure.EntityColumn;

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

        final int columnCount = entityColumns.size();

        if (columnCount <= 0)
            // TODO: concrete this exception
            throw new IllegalArgumentException();

        final Iterator<EntityColumn<?>> it = entityColumns.iterator();

        final String primaryColumnName = it.next().columnName; // first column have to be the primaty key

        /* Put all column names in a string. */

        final StringBuilder columnsBuilder = new StringBuilder(primaryColumnName);
        while (it.hasNext()) {
            final EntityColumn<?> entityColumn = it.next();
            columnsBuilder.append(",").append(entityColumn.columnName);
        }
        final String columnNames = columnsBuilder.toString();

        /* Build string that respresented an insert query. */

        final StringBuilder insertQueryBuilder;
        insertQueryBuilder = new StringBuilder("INSERT INTO ").append(entity.getTableName()).append("(")
                .append(columnNames).append(") VALUES(?");

        for (int i = 1; i < columnCount; i++)
            insertQueryBuilder.append(",?");

        insertQueryBuilder.append(");");

        this.insertStatement = connection.prepareStatement(insertQueryBuilder.toString(),
                Statement.RETURN_GENERATED_KEYS);

        // Build string that respresented an get query.

        final StringBuilder selectQueryBuilder;
        selectQueryBuilder = new StringBuilder("SELECT ").append(columnNames).append(" FROM ")
                .append(entity.getTableName()).append(" WHERE ").append(entity.getPrimaryKeyColumn().columnName)
                .append(" = ?;");

        this.selectStatement = connection.prepareStatement(selectQueryBuilder.toString());

    }

    private Dao(final Connection connection, final Class<ENTITY> entityType)
            throws InstantiationException, IllegalAccessException, SQLException {

        this(connection, entityType.newInstance());
    }

    public boolean insert(final ENTITY entity) {

        boolean wasSuccess = false;

        final Map<EntityColumn<?>, Object> map = entity.getAllColumns();

        final Set<Entry<EntityColumn<?>, Object>> entrySet = map.entrySet();

        int i = 0;
        try {
            for (final Entry<EntityColumn<?>, Object> entry : entrySet)
                this.insertStatement.setObject(++i, entry.getValue());
        } catch (final SQLException e) {

            e.printStackTrace();
        }

        try {
            this.insertStatement.executeUpdate();
            final ResultSet generatedKeys = this.insertStatement.getGeneratedKeys();

            if (generatedKeys != null)
                if (generatedKeys.next()) {

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
            this.selectStatement.setObject(1, entity.getPrimaryKeyValue());
            final ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet != null && resultSet.next()) {

                final Set<Entry<EntityColumn<?>, Object>> entrySet = entity.getAllColumns().entrySet();

                int i = 1; // primary key already set; jump over

                // for loop instead of forEach method to avoid an extra try catch block
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

    public static boolean insert(final AbstractEntity entity, final Connection connection) {

        final boolean wasSuccess = false;

        final StringBuilder insertQueryBuilder;
        insertQueryBuilder = new StringBuilder("INSERT INTO ").append(entity.getTableName()).append("(");

        entity.getAllColumns().forEach((k, v) -> {

        });

        return wasSuccess;
    }
}