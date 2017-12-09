package de.lehmann.lehmannorm.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.lehmann.lehmannorm.entity.AbstractEntity;
import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo;
import de.lehmann.lehmannorm.logic.sqlbuilder.IStatementBuilder;
import de.lehmann.lehmannorm.logic.sqlbuilder.IStatementBuilder.DefaultBuilderBundle;

/**
 *
 * @author Tim Lehmann
 *
 * @param <E>
 *            entity
 * @param <PK>
 *            primary key
 */
public class Dao<E extends AbstractEntity<PK>, PK> {

    private final Map<Class<? extends AbstractEntity<?>>, Dao<?, ?>> daoCache = new HashMap<>();

    /**
     *
     * @param connection
     * @param entityType
     * @return
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <E extends AbstractEntity<PK>, PK> Dao<E, PK> createInstance(
            final Connection connection, final Class<E> entityType)
            throws SQLException, InstantiationException, IllegalAccessException {
        return new Dao<>(connection, entityType);
    }

    /**
     *
     * @param connection
     * @param entityType
     * @return
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private static <E extends AbstractEntity<PK>, PK> Dao<E, PK> createInstance(
            final Connection connection, final E entityType)
            throws SQLException, InstantiationException, IllegalAccessException {
        return new Dao<>(connection, entityType);
    }

    private final PreparedStatement insertStatement;
    private final PreparedStatement selectStatement;

    private Dao(final Connection connection, final E entity) throws SQLException {

        final Set<EntityColumnInfo<?>> entityColumns = entity.getAllColumns().keySet();

        final String columns = IStatementBuilder.processEntityColumns(entityColumns);

        IStatementBuilder sb;

        sb = DefaultBuilderBundle.DEFAULT_INSERT_STATEMENT_BUILDER.getStatementBuilder();
        insertStatement =
                sb.buildStatement(entity.getTableName(), columns, sb.generateStatementTail(entityColumns), connection);

        sb = DefaultBuilderBundle.DEFAULT_SELECT_STATEMENT_BUILDER.getStatementBuilder();
        selectStatement =
                sb.buildStatement(entity.getTableName(), columns, sb.generateStatementTail(entityColumns), connection);
    }

    private Dao(final Connection connection, final Class<E> entityType)
            throws InstantiationException, IllegalAccessException, SQLException {

        this(connection, entityType.newInstance());
    }

    public boolean insert(final E entity) {

        final boolean wasSuccess = false;

        final Deque<AbstractEntity<?>> deque = new ArrayDeque<>();

        final Map<EntityColumnInfo<?>, Object> map = entity.getAllColumns();
        final Set<Entry<EntityColumnInfo<?>, Object>> entrySet = map.entrySet();

        final int size = entrySet.size();
        for (int i = 0; i < size; i++) {

        }

        return wasSuccess;
    }

    private boolean doInsert(final Set<AbstractEntity<Object>> insertOrder) {

        for (final AbstractEntity<Object> entity : insertOrder) {

            final Map<EntityColumnInfo<?>, Object> map = entity.getAllColumns();

            final Set<Entry<EntityColumnInfo<?>, Object>> entrySet = map.entrySet();

            int i = 0;
            try {

                for (final Entry<EntityColumnInfo<?>, Object> entry : entrySet) {

                    final Class<?> columnType = entry.getKey().columnType;
                    if (AbstractEntity.class.isAssignableFrom(columnType)) {

                        // Todo: introduction of column options
                    } else
                        this.insertStatement.setObject(++i, entry.getValue());

                }

            } catch (final SQLException e) {

                e.printStackTrace();
            }

            try {
                this.insertStatement.executeUpdate();
                final ResultSet generatedKeys = this.insertStatement.getGeneratedKeys();

                if (generatedKeys != null)
                    if (generatedKeys.next()) {

                        final Object primaryKey = generatedKeys.getObject(1, entity.getPrimaryKeyInfo().columnType);
                        entity.setPrimaryKeyValue(primaryKey);

                    }
            } catch (final SQLException e) {
            }

        }

        return false;
    }

    public boolean getEntityByPk(final E entity) {

        boolean wasSuccess = false;
        ResultSet resultSet = null;

        try {
            this.selectStatement.setObject(1, entity.getPrimaryKeyValue());

            resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {

                final Set<Entry<EntityColumnInfo<?>, Object>> entrySet = entity.getAllColumns().entrySet();

                int i = 1; // primary key already set; jump over

                // for loop instead of forEach method to avoid an extra try catch block
                for (final Entry<EntityColumnInfo<?>, Object> entry : entrySet) {

                    final Object object = resultSet.getObject(++i, entry.getKey().columnType);
                    entry.setValue(object);
                }

                wasSuccess = true;
            }

        } catch (final SQLException e) {

        } finally {
            if (resultSet != null)
                try {
                    resultSet.close();
                } catch (final SQLException e) {
                }
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

    private Dao<?, ?> getDao(final Connection connection, final E entity) {

        final Map<EntityColumnInfo<?>, Object> allColumns = entity.getAllColumns();
        final Set<Entry<EntityColumnInfo<?>, Object>> entrySet = allColumns.entrySet();

        Dao newDao = null;

        for (final Entry<EntityColumnInfo<?>, Object> entry : entrySet) {

            final EntityColumnInfo<?> key = entry.getKey();

            final Class<?> columnType = key.columnType;

            if (AbstractEntity.class.isAssignableFrom(columnType)) {

                final Class<? extends AbstractEntity<?>> clazz =
                        (Class<? extends AbstractEntity<?>>) columnType;

                if (!this.daoCache.containsKey(clazz)) {

                    final AbstractEntity value = (AbstractEntity) entry.getValue();
                    try {
                        newDao = Dao.createInstance(connection, value);
                        this.daoCache.put(clazz, newDao);
                    } catch (InstantiationException | IllegalAccessException | SQLException e) {
                        e.printStackTrace();
                    }
                } else
                    newDao = this.daoCache.get(clazz);
            }
        }

        return newDao;
    }

    private class AbstractEntityToIndex {

        public final AbstractEntity<Object> entity;
        public final Integer                index;

        public AbstractEntityToIndex(final AbstractEntity<Object> entity, final Integer index) {
            super();
            this.entity = entity;
            this.index = index;
        }
    }
}