package de.lehmann.lehmannorm.entity;

import java.util.LinkedHashMap;
import java.util.Map;

import de.lehmann.lehmannorm.entity.structure.EntityColumn;

/**
 * @author Tim Lehmann
 *
 * @param <PK>
 *            type of primary key (value)
 */
public abstract class AbstractEntity<PK> {

    private final EntityColumn<PK>             primaryKeyColumn;
    private final Map<EntityColumn<?>, Object> entityColumnsWithValue;

    // Constructors

    protected AbstractEntity(final Class<PK> primaryKeyType, final EntityColumn<?>... entityColumns) {
        this(new EntityColumn<>("ID", primaryKeyType), entityColumns);
    }

    protected AbstractEntity(final Class<PK> primaryKeyType, final String primaryKeyColumnName,
            final EntityColumn<?>... entityColumns) {
        this(primaryKeyType, primaryKeyColumnName, null, entityColumns);
    }

    protected AbstractEntity(final Class<PK> primaryKeyType, final String primaryKeyColumnName,
            final PK primaryKeyValue, final EntityColumn<?>... entityColumns) {
        this(new EntityColumn<>(primaryKeyColumnName, primaryKeyType), primaryKeyValue);
    }

    protected AbstractEntity(final EntityColumn<PK> entityColumn, final EntityColumn<?>... entityColumns) {
        this(entityColumn, null, entityColumns);
    }

    protected AbstractEntity(final EntityColumn<PK> entityColumn, final PK primaryKeyColumnValue,
            final EntityColumn<?>... entityColumns) {

        this.primaryKeyColumn = entityColumn;
        this.entityColumnsWithValue = new LinkedHashMap<>(1 + entityColumns.length);

        entityColumnsWithValue.put(entityColumn, primaryKeyColumnValue);

        initColumns(entityColumns);
    }

    // copy constructor

    public AbstractEntity(final AbstractEntity<PK> sourceEntity) {

        this.primaryKeyColumn = sourceEntity.primaryKeyColumn;
        this.entityColumnsWithValue = new LinkedHashMap<>(sourceEntity.entityColumnsWithValue.size());

        // copy all immutable values of type EntityColumn to this entryset.
        sourceEntity.entityColumnsWithValue.forEach((k, v) -> {
            this.entityColumnsWithValue.put(k, null);
        });
    }

    // constructor methods

    private void initColumns(final EntityColumn<?>... entityColumns) {

        boolean fillForeignMap = true;

        for (final EntityColumn<?> entityColumn : entityColumns)
            if (fillForeignMap && entityColumn.columnType.isAssignableFrom(AbstractEntity.class))
                ;// TODO: handle entity references
            else {
                fillForeignMap = false;
                entityColumnsWithValue.put(entityColumn, null);
            }
    }

    // Getter / Setter

    public <T> T getColumnValue(final EntityColumn<T> entityColumn) {

        return entityColumn.columnType.cast(entityColumnsWithValue.get(entityColumn));
    }

    public <T> boolean setColumnValue(final EntityColumn<T> entityColumn, final T value) {

        final boolean success = this.entityColumnsWithValue.containsKey(entityColumn);

        if (success)
            this.entityColumnsWithValue.put(entityColumn, value);

        return success;
    }

    public Map<EntityColumn<?>, Object> getAllColumns() {

        return this.entityColumnsWithValue;
    }

    public EntityColumn<PK> getPrimaryKeyColumn() {

        return primaryKeyColumn;
    }

    public PK getPrimaryKeyValue() {
        return this.primaryKeyColumn.columnType.cast(entityColumnsWithValue.get(primaryKeyColumn));
    }

    public void setPrimaryKeyValue(final PK primaryKeyValue) {
        this.entityColumnsWithValue.put(primaryKeyColumn, primaryKeyValue);
    }

    public abstract String getTableName();
}