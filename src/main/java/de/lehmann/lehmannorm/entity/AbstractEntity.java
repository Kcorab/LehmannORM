package de.lehmann.lehmannorm.entity;

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

        if (!this.entityColumns.containsKey(entityColumnInfo))
            throw new IllegalArgumentException(
                    "There is no " + entityColumnInfo.columnName + " column for entity type " + this.getClass() + " ");

        return (T) this.entityColumns.put((EntityColumnInfo<Object>) entityColumnInfo, entityColumnValue);
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