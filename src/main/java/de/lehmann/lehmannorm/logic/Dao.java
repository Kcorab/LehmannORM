package de.lehmann.lehmannorm.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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