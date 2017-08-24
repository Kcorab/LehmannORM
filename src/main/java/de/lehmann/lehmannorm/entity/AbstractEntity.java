package de.lehmann.lehmannorm.entity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.lehmann.lehmannorm.entity.column.EntityColumn;
import de.lehmann.lehmannorm.entity.column.EntityColumnInfo;
import de.lehmann.lehmannorm.entity.column.IEntityColumnInfo;

/**
 * @author Tim Lehmann
 *
 * @param <PK>
 *            type of primary key (value)
 */
public abstract class AbstractEntity<PK> {

    private final IEntityColumnInfo<PK> primaryKey;

    private final Map<EntityColumn<?>, Object> entityColumnsWithValue = new LinkedHashMap<>();

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

        primaryKey = new EntityColumnInfo<>(entityColumn, primaryKeyColumnValue);

        initColumns(entityColumns);
    }

    // copy constructor

    public AbstractEntity(final EntityColumn<PK> entityColumn, final PK primaryKeyColumnValue,
            final AbstractEntity<?> sourceEntity) {

        primaryKey = new EntityColumnInfo<>(entityColumn, primaryKeyColumnValue);

        final Set<Entry<EntityColumn<?>, Object>> entrySet = sourceEntity.entityColumnsWithValue.entrySet();

        for (final Map.Entry<EntityColumn<?>, Object> element : entrySet)
            this.entityColumnsWithValue.put(element.getKey(), null);
    }

    // constructor methods

    private void initColumns(final EntityColumn<?>... entityColumns) {

        boolean fillForeignMap = true;

        for (final EntityColumn<?> entityColumn : entityColumns)
            if (fillForeignMap && entityColumn.columnType.isAssignableFrom(AbstractEntity.class))
                ;
            else {
                entityColumnsWithValue.put(entityColumn, null);
                fillForeignMap = false;
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
        return primaryKey.getEntityColumn();
    }

    public PK getPrimaryKeyValue() {
        return primaryKey.getEntityColumnValue();
    }

    public void setPrimaryKeyValue(final PK primaryKeyValue) {
        primaryKey.setEntityColumnValue(primaryKeyValue);
    }

    public abstract String getTableName();
}