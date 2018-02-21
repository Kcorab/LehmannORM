package de.lehmann.lehmannorm.entity;

import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import de.lehmann.lehmannorm.entity.structure.ColumnMap;
import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo;
import de.lehmann.lehmannorm.entity.structure.IBoundedColumnMap;

/**
 * @author Tim Lehmann
 *
 * @param <PK>
 *            type of primary key (value)
 */
public abstract class AbstractEntity<PK> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEntity.class);

    private final EntityColumnInfo<PK>      primaryKeyInfo;
    private final IBoundedColumnMap<Object> entityColumns;

    // Constructors

    protected AbstractEntity(final Class<PK> primaryKeyType, final EntityColumnInfo<?>... entityColumnInfos) {
        this(new EntityColumnInfo<>("ID", primaryKeyType), entityColumnInfos);
    }

    protected AbstractEntity(final Class<PK> primaryKeyType, final String primaryKeyName,
            final EntityColumnInfo<?>... entityColumnInfos) {
        this(primaryKeyType, primaryKeyName, null, entityColumnInfos);
    }

    protected AbstractEntity(final Class<PK> primaryKeyType, final String primaryKeyName,
            final PK primaryKeyValue, final EntityColumnInfo<?>... entityColumnInfos) {
        this(new EntityColumnInfo<>(primaryKeyName, primaryKeyType), primaryKeyValue);
    }

    protected AbstractEntity(final EntityColumnInfo<PK> primaryKeyInfo,
            final EntityColumnInfo<?>... entityColumnInfos) {
        this(primaryKeyInfo, null, entityColumnInfos);
    }

    @SuppressWarnings("unchecked")
    protected AbstractEntity(final EntityColumnInfo<PK> primaryKeyColumnInfo, final PK primaryKeyValue,
            final EntityColumnInfo<?>... entityColumnInfos) {

        this.primaryKeyInfo = primaryKeyColumnInfo;
        this.entityColumns = new ColumnMap<>(1 + entityColumnInfos.length);

        entityColumns.put((EntityColumnInfo<Object>) primaryKeyColumnInfo, primaryKeyValue);

        for (final EntityColumnInfo<?> entityColumn : entityColumnInfos)
            entityColumns.put((EntityColumnInfo<Object>) entityColumn, null);

    }

    // copy constructor

    public AbstractEntity(final AbstractEntity<PK> sourceEntity) {

        this.primaryKeyInfo = sourceEntity.primaryKeyInfo;
        this.entityColumns = new ColumnMap<>(sourceEntity.entityColumns.size());

        // Copy all immutable values of type EntityColumn to this entryset.
        sourceEntity.entityColumns.forEach((k, v) -> this.entityColumns.put(k, null));
    }

    // Getter / Setter

    public <T> T getColumnValue(final EntityColumnInfo<T> entityColumnInfo) {

        return entityColumnInfo.columnType.cast(entityColumns.get(entityColumnInfo));
    }

    @SuppressWarnings("unchecked")
    public <T> T setColumnValue(final EntityColumnInfo<T> entityColumnInfo, final T entityColumnValue) {

        LOGGER.debug(() -> "Column value is a primitive.");

        return (T) setColumnValueGeneric(entityColumnInfo, entityColumnValue);
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractEntity<?>> T setColumnValue(final EntityColumnInfo<T> entityColumnInfo,
            final T newRefEntity, final boolean removeOldEntity) {

        LOGGER.debug(() -> "Column value is a reference entity.");

        /*
         * If the EntityColumnInfo stores a type that extends AbstractEntity then the
         * columnName will ignore by hashCode() and equals(...). So, we are able to
         * override the old value (AbstractEntity) by the key (EntityColumnInfo) without
         * knowing the contingent columName.
         */
        final EntityColumnInfo<?> eci = new EntityColumnInfo<>(this.getClass());

        final T oldRefEntity = (T) setColumnValueGeneric(entityColumnInfo, newRefEntity);

        if (oldRefEntity != null && removeOldEntity)
            ((AbstractEntity<?>) oldRefEntity).setColumnValueGeneric(eci, null);

        if (newRefEntity != null)
            ((AbstractEntity<?>) newRefEntity).setColumnValueGeneric(eci, this);

        return oldRefEntity;
    }

    public <T extends AbstractEntity<?>> T setColumnValue(final EntityColumnInfo<T> entityColumnInfo,
            final T newRefEntity) {

        return setColumnValue(entityColumnInfo, newRefEntity, false);
    }

    @SuppressWarnings("unchecked")
    private Object setColumnValueGeneric(final EntityColumnInfo<?> entityColumnInfo, final Object entityColumnValue) {

        if (!this.entityColumns.containsKey(entityColumnInfo))
            throw new IllegalArgumentException(
                    "There is no column with name \"" + entityColumnInfo.columnName + "\" and type "
                            + entityColumnInfo.columnType.getSimpleName() + " for entity type "
                            + this.getClass().getSimpleName() + ". You may forgot to add the information about "
                            + entityColumnInfo.columnType.getSimpleName() + ".");

        return entityColumns.put((EntityColumnInfo<Object>) entityColumnInfo, entityColumnValue);
    }

    public IBoundedColumnMap<Object> getAllColumns() {

        return this.entityColumns;
    }

    public EntityColumnInfo<PK> getPrimaryKeyInfo() {

        return primaryKeyInfo;
    }

    public PK getPrimaryKeyValue() {
        return this.primaryKeyInfo.columnType.cast(entityColumns.get(primaryKeyInfo));
    }

    @SuppressWarnings("unchecked")
    public void setPrimaryKeyValue(final PK primaryKeyValue) {
        this.entityColumns.put((EntityColumnInfo<Object>) primaryKeyInfo, primaryKeyValue);
    }

    public abstract String getTableName();
}