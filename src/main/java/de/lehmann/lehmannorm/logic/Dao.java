package de.lehmann.lehmannorm.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import de.lehmann.lehmannorm.entity.AbstractEntity;
import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo;
import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo.ForeignKeyHolder;
import de.lehmann.lehmannorm.entity.structure.IBoundedColumnMap;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(Dao.class);

    private static final Map<Connection, Map<Class<AbstractEntity<?>>, Dao<?, ?>>> conndaoCache = new HashMap<>();

    /**
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
     * @param connection
     * @param entity
     * @return
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <E extends AbstractEntity<PK>, PK> Dao<E, PK> createInstance(
            final Connection connection, final E entity)
            throws SQLException {
        return new Dao<>(connection, entity);
    }

    /**
     * @param entity
     * @return never null
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @SuppressWarnings("unchecked")
    public static <E extends AbstractEntity<PK>, PK> Dao<E, PK> getOrCreateCachedDao(final Connection connection,
            final Class<E> entityType) throws SQLException, InstantiationException, IllegalAccessException {

        final Map<Class<AbstractEntity<?>>, Dao<?, ?>> daoForEntityMap;
        final Dao<E, PK> dao;

        if (conndaoCache.containsKey(connection)) {
            daoForEntityMap = conndaoCache.get(connection);

            if (daoForEntityMap.containsKey(entityType))
                dao = (Dao<E, PK>) daoForEntityMap.get(entityType);
            else
                dao = Dao.createInstance(connection, entityType.newInstance());
        } else {
            daoForEntityMap = new HashMap<>();

            dao = Dao.createInstance(connection, entityType.newInstance());
            daoForEntityMap.put((Class<AbstractEntity<?>>) entityType, dao);

            conndaoCache.put(connection, daoForEntityMap);
        }

        return dao;
    }

    private final Connection        connection;
    private final PreparedStatement insertStatement;
    private final PreparedStatement selectStatement;

    private Dao(final Connection connection, final E entity) throws SQLException {

        this.connection = connection;

        final Set<EntityColumnInfo<Object>> entityColumns = entity.getAllColumns().keySet();

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

    @SuppressWarnings("unchecked")
    public boolean insert(final E entity) {

        try {
            connection.setAutoCommit(false);
        } catch (final SQLException e1) {
            LOGGER.error(e1, () -> "");
            try {
                if (!connection.isClosed())
                    connection.close();
            } catch (final SQLException e2) {
                LOGGER.error(e2, () -> "");
            }
            return false;
        }

        final Map<EntityColumnInfo<Object>, Object> allColumns = entity.getAllColumns();
        final Set<Entry<EntityColumnInfo<Object>, Object>> columnKeyValue = allColumns.entrySet();

        // Recursion stack to remember the last data.
        final Deque<EntityToIteratorAndColumn> recStack = new ArrayDeque<>();

        // Declare data fragments.
        AbstractEntity<?> currentEntity;
        Iterator<Entry<EntityColumnInfo<Object>, Object>> columnIteratorOfCurrentEntity;
        Entry<EntityColumnInfo<Object>, Object> columnInfoAndValue;

        // Instantiate data fragments.
        currentEntity = entity;
        columnIteratorOfCurrentEntity = columnKeyValue.iterator();

        // The first column represents already the primary key and have to be exist.
        columnIteratorOfCurrentEntity.next(); // Jump over primary key.

        columnInfoAndValue =
                columnIteratorOfCurrentEntity.hasNext() ? columnIteratorOfCurrentEntity.next() : null;

        try {
            while (columnInfoAndValue != null) { // for every element on stack

                // As already mentioned the true type is unkown.
                final EntityColumnInfo<?> columnInfo = columnInfoAndValue.getKey();

                // Is there a reference entity?
                if (AbstractEntity.class.isAssignableFrom(columnInfo.columnType)) {

                    final AbstractEntity<?> refEntity = (AbstractEntity<?>) columnInfoAndValue.getValue();

                    final IBoundedColumnMap<Object> columns = refEntity.getAllColumns();

                    /*
                     * Holds the current entity the foreign key? (Is it needed to insert the
                     * reference entity before current entity?)
                     */
                    if (ForeignKeyHolder.THIS_ENTITY_TYPE.equals(columnInfo.foreignKeyHolder)) {

                        if (columns.size() <= 1) {
                            // No reference entities. Only a one column (primary key) table.

                            final Dao daoForCurrentEntityType =
                                    getOrCreateCachedDao(connection, refEntity.getClass());

                            // Insert the reference entity.
                            daoForCurrentEntityType.insertEntity(refEntity);
                        } else {
                            // The reference entity

                            // Remember the current entity (and process state) to insert it later.
                            recStack.push(new EntityToIteratorAndColumn(currentEntity, columnIteratorOfCurrentEntity));

                            currentEntity = refEntity;

                            columnIteratorOfCurrentEntity = columns.entrySet().iterator();

                            // First column represents the pk. Jump over.
                            columnIteratorOfCurrentEntity.next();
                        }

                    } else {
                        // Remember the reference entity to insert it after the current entity.
                        final Iterator<Entry<EntityColumnInfo<Object>, Object>> columnIteratorOfRefEntity =
                                columns.entrySet().iterator();

                        if (columns.size() > 1)
                            columnIteratorOfRefEntity.next();

                        recStack.push(new EntityToIteratorAndColumn(refEntity, columnIteratorOfRefEntity));

                    }

                    columnInfoAndValue =
                            columnIteratorOfCurrentEntity.hasNext() ? columnIteratorOfCurrentEntity.next() : null;

                } else
                    columnInfoAndValue = null;

                if (columnInfoAndValue == null) {

                    final Dao daoForCurrentEntityType =
                            getOrCreateCachedDao(connection, currentEntity.getClass());

                    // no reference entities anymore
                    // insert the current entity
                    daoForCurrentEntityType.insertEntity(currentEntity);

                    if (!recStack.isEmpty()) {

                        final EntityToIteratorAndColumn pop = recStack.pop();

                        currentEntity = pop.entity;
                        columnIteratorOfCurrentEntity = pop.iterator;
                        columnInfoAndValue =
                                columnIteratorOfCurrentEntity.hasNext() ? columnIteratorOfCurrentEntity.next() : null;

                        if (columnInfoAndValue == null) {
                            final Dao daoForCurrentEntityType_ =
                                    getOrCreateCachedDao(connection, currentEntity.getClass());

                            // no reference entities anymore
                            // insert the current entity
                            daoForCurrentEntityType.insertEntity(currentEntity);
                        }

                    }
                }
            }

        } catch (final RuntimeException | InstantiationException | IllegalAccessException |

                SQLException e) {

            LOGGER.error(e, () -> "");

            try {
                connection.rollback();
            } catch (final SQLException e1) {

                LOGGER.error(e1, () -> "");

                return false;

            } finally {
                try {
                    if (!connection.isClosed())
                        connection.close();
                } catch (final SQLException e2) {

                    LOGGER.error(e2, () -> "");

                }
            }

            return false;
        }

        try {
            connection.commit();
        } catch (final SQLException e) {

            LOGGER.error(e, () -> "");

            return false;
        } finally {
            try {
                if (!connection.isClosed())
                    connection.close();
            } catch (final SQLException e) {
                LOGGER.error(e, () -> "");
            }
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    private void insertEntity(final E entity) throws SQLException {

        final Set<Entry<EntityColumnInfo<Object>, Object>> entityColumns =
                entity.getAllColumns().entrySet();

        final Iterator<Entry<EntityColumnInfo<Object>, Object>> iterator =
                entityColumns.iterator();

        PK primaryKeyValue;
        Entry<EntityColumnInfo<Object>, ?> entityColumn;

        int columnIndex = 1;

        // PRIMARY KEY

        /*
         * Because of restrictions and sequence prevention the first column have to be
         * exist and have to be the primary key.
         */
        entityColumn = iterator.next();

        primaryKeyValue = (PK) entityColumn.getValue();
        this.insertStatement.setObject(columnIndex++, primaryKeyValue);

        // REFERENCE ENTITIES

        // Columns after the primary key column could be reference columns.

        while (iterator.hasNext()) {

            entityColumn = iterator.next();
            // All generics are calibrated to Object, but the true type is unkown.

            final EntityColumnInfo<?> columnInfo = entityColumn.getKey();

            // Holds the column an reference to another entity?
            if (AbstractEntity.class.isAssignableFrom(columnInfo.columnType)) {

                // Is there a info about the foreign key?
                if (ForeignKeyHolder.THIS_ENTITY_TYPE.equals(columnInfo.foreignKeyHolder)) {
                    // Yes, so the column value have to be a AbstractEntity.

                    final Object refPrimaryKeyValue =
                            ((AbstractEntity<?>) entityColumn.getValue()).getPrimaryKeyValue();

                    this.insertStatement.setObject(columnIndex++, refPrimaryKeyValue);
                }

            } else {
                // It's a primitive column value!

                this.insertStatement.setObject(columnIndex++, entityColumn.getValue());

                /*
                 * Because of the sequence restriction the following column values couldn't be a
                 * reference entity and have to be primitive.
                 */
                break;
            }
        }

        // PRIMITIVE COLUMN VALUES

        while (iterator.hasNext()) {

            entityColumn = iterator.next();

            this.insertStatement.setObject(columnIndex++, entityColumn.getValue());
        }

        insertStatement.executeUpdate();

        if (entity.getPrimaryKeyValue() == null)
            try (final ResultSet generatedKeys = this.insertStatement.getGeneratedKeys()) {

                generatedKeys.next();
                primaryKeyValue = (PK) generatedKeys.getObject(1);
                entity.setPrimaryKeyValue(primaryKeyValue);
            }
    }

    //

    private static class EntityToIteratorAndColumn {

        public final AbstractEntity<?>                                 entity;
        public final Iterator<Entry<EntityColumnInfo<Object>, Object>> iterator;

        public EntityToIteratorAndColumn(
                final AbstractEntity<?> entity,
                final Iterator<Entry<EntityColumnInfo<Object>, Object>> iterator) {

            super();

            this.entity = entity;
            this.iterator = iterator;
        }
    }
}