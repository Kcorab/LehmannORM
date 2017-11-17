package de.lehmann.lehmannorm.entity;

import java.util.LinkedHashMap;
import java.util.Map;

import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo;

/**
 * @author Tim Lehmann
 *
 * @param <PK>
 *            type of primary key (value)
 */
public abstract class AbstractEntity<PK> {

    private final EntityColumnInfo<PK>             primaryKeyInfo;
    private final Map<EntityColumnInfo<?>, Object> entityColumns;

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

    protected AbstractEntity(final EntityColumnInfo<PK> primaryKeyColumnInfo, final PK primaryKeyValue,
            final EntityColumnInfo<?>... entityColumnInfos) {

        this.primaryKeyInfo = primaryKeyColumnInfo;
        this.entityColumns = new LinkedHashMap<>(1 + entityColumnInfos.length);

        entityColumns.put(primaryKeyColumnInfo, primaryKeyValue);

        initColumns(entityColumnInfos);
    }

    // copy constructor

    public AbstractEntity(final AbstractEntity<PK> sourceEntity) {

        this.primaryKeyInfo = sourceEntity.primaryKeyInfo;
        this.entityColumns = new LinkedHashMap<>(sourceEntity.entityColumns.size());

        // copy all immutable values of type EntityColumn to this entryset.
        sourceEntity.entityColumns.forEach((k, v) -> {
            this.entityColumns.put(k, null);
        });
    }

    // constructor methods

    private void initColumns(final EntityColumnInfo<?>... entityColumnInfos) {

        boolean fillForeignMap = true;

        for (final EntityColumnInfo<?> entityColumn : entityColumnInfos)
            if (fillForeignMap && entityColumn.columnType.isAssignableFrom(AbstractEntity.class))
                ;// TODO: handle entity references
            else {
                fillForeignMap = false;
                entityColumns.put(entityColumn, null);
            }
    }

    // Getter / Setter

    public <T> T getColumnValue(final EntityColumnInfo<T> entityColumnInfo) {

        return entityColumnInfo.columnType.cast(entityColumns.get(entityColumnInfo));
    }

    public <T> boolean setColumnValue(final EntityColumnInfo<T> entityColumnInfo, final T entityColumnValue) {

        final boolean success = this.entityColumns.containsKey(entityColumnInfo);

        if (success)
            this.entityColumns.put(entityColumnInfo, entityColumnValue);

        return success;
    }

    public Map<EntityColumnInfo<?>, Object> getAllColumns() {

        return this.entityColumns;
    }

    public EntityColumnInfo<PK> getPrimaryKeyInfo() {

        return primaryKeyInfo;
    }

    public PK getPrimaryKeyValue() {
        return this.primaryKeyInfo.columnType.cast(entityColumns.get(primaryKeyInfo));
    }

    public void setPrimaryKeyValue(final PK primaryKeyValue) {
        this.entityColumns.put(primaryKeyInfo, primaryKeyValue);
    }

    public abstract String getTableName();
}