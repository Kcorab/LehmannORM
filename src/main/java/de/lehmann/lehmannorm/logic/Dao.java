package de.lehmann.lehmannorm.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

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
}