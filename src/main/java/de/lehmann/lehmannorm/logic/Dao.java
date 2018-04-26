package de.lehmann.lehmannorm.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Collection;
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
import de.lehmann.lehmannorm.logic.sqlbuilder.IStatementBuilder;
import de.lehmann.lehmannorm.logic.sqlbuilder.IStatementBuilder.DefaultBuilderBundle;

/**
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

    public boolean insert(final E entity) {

        return insert(entity, null);
    }

    private boolean insert(final E entity, final AbstractEntity<?> triggerdEntity) {

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

        try {
            insertEntity(entity, triggerdEntity);
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

        return false;

    }

    @SuppressWarnings({ "unchecked" })
    private void insertEntity(final E entity, final AbstractEntity<?> triggerdEntity)
            throws InstantiationException, IllegalAccessException, SQLException {

        final Map<EntityColumnInfo<Object>, Object> allColumns = entity.getAllColumns();
        final Set<Entry<EntityColumnInfo<Object>, Object>> columnKeyValue = allColumns.entrySet();

        Iterator<Entry<EntityColumnInfo<Object>, Object>> columnIteratorOfCurrentEntity;
        Entry<EntityColumnInfo<Object>, Object> columnInfoAndValue;

        columnIteratorOfCurrentEntity = columnKeyValue.iterator();

        // Recursion stack to remember the last data.
        final Deque<AbstractEntity<?>> recStack = new ArrayDeque<>();

        final int[] paramIndex = { 1 };
        // The first column represents already the primary key and have to be exist.
        columnInfoAndValue = columnIteratorOfCurrentEntity.next();

        this.insertStatement.setObject(paramIndex[0]++, columnInfoAndValue.getValue());

        while (columnIteratorOfCurrentEntity.hasNext()) {

            columnInfoAndValue = columnIteratorOfCurrentEntity.next();

            // As already mentioned the true type is unknown.
            final EntityColumnInfo<?> columnInfo = columnInfoAndValue.getKey();

            // Is there a reference entity at least?

            // ONE TO ONE
            if (AbstractEntity.class.isAssignableFrom(columnInfo.getColumnType()))

                this.insertOneToOne(entity, triggerdEntity, columnInfoAndValue, columnInfo, paramIndex, recStack);

            // ONE TO MANY
            else if (Collection.class.isAssignableFrom(columnInfo.getColumnType()))

                this.insertOneToMany();

            else
                while (columnIteratorOfCurrentEntity.hasNext())
                    this.insertStatement.setObject(paramIndex[0]++, columnIteratorOfCurrentEntity.next().getValue());
        }

        while (!recStack.isEmpty()) {

            final AbstractEntity<?> pop = recStack.pop();

            getOrCreateCachedDao(connection, pop.getClass()).insert(pop, entity);
        }

        this.insertStatement.execute();
    }

    private void insertOneToOne(
            final E entity,
            final AbstractEntity<?> triggerdEntity,
            final Entry<EntityColumnInfo<Object>, Object> columnInfoAndValue,
            final EntityColumnInfo<?> columnInfo,
            final int[] paramIndex,
            final Deque<AbstractEntity<?>> recStack)
            throws InstantiationException, IllegalAccessException, SQLException {

        final AbstractEntity<?> refEntity = (AbstractEntity<?>) columnInfoAndValue.getValue();

        /*
         * Holds the current entity the foreign key? =>
         * Is it needed to insert the reference entity before current entity?
         */
        if (columnInfo.getColumnName() != null) { // Yes.

            // Insert the reference entity firstly.
            if (refEntity != null) {

                if (refEntity != triggerdEntity)
                    getOrCreateCachedDao(connection, refEntity.getClass()).insert(refEntity, entity);

                this.insertStatement.setObject(paramIndex[0]++, refEntity.getPrimaryKeyValue());

            } else
                this.insertStatement.setObject(paramIndex[0]++, null);

        } else if (refEntity != triggerdEntity) // No, so check that we don't come from the refEntity.
            // Remember to insert it after the current entity.
            recStack.push(refEntity);

    }

    private void insertManyToOne() {

    }

    private void insertOneToMany() {

    }

}