package de.lehmann.lehmannorm.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.lehmann.lehmannorm.entity.AbstractEntity;
import de.lehmann.lehmannorm.entity.structure.EntityColumn;
import de.lehmann.lehmannorm.logic.sqlbuilder.IStatementBuilder;
import de.lehmann.lehmannorm.logic.sqlbuilder.IStatementBuilder.DefaultBuilderBundle;

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

        final String columns = IStatementBuilder.processEntityColumns(entityColumns);

        IStatementBuilder sb;

        sb = DefaultBuilderBundle.DEFAULT_INSERT_STATEMENT_BUILDER.getStatementBuilder();
        insertStatement =
                sb.buildStatement(entity.getTableName(), columns, sb.generateValues(entityColumns), connection);

        sb = DefaultBuilderBundle.DEFAULT_SELECT_STATEMENT_BUILDER.getStatementBuilder();
        selectStatement =
                sb.buildStatement(entity.getTableName(), columns, sb.generateValues(entityColumns), connection);
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

    public static boolean insert(final AbstractEntity<Integer> entity, final Connection connection) {

        boolean wasSuccess = false;

        try {
            IStatementBuilder.DEFAULT_INSERT_STATEMENT_BUILDER.buildStatement(entity.getTableName(),
                    entity.getAllColumns().keySet(), connection);
            wasSuccess = true;
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return wasSuccess;
    }
}