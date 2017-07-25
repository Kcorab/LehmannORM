package de.lehmann.lehmannorm.entities;

import java.util.LinkedHashMap;
import java.util.Map;

import de.lehmann.lehmannorm.exceptions.EntityInitException;

public abstract class AbstractEntity<PRIMATY_KEY> {

    private final IEntityColumnInfo<PRIMATY_KEY> primaryKey;

    private final Map<EntityColumn<?>, ?> columns;

    protected AbstractEntity(final Class<PRIMATY_KEY> primaryKeyColumnType, final EntityColumn<?>... entityColumns) {
        this(new EntityColumn<>("ID", primaryKeyColumnType), entityColumns);
    }

    protected AbstractEntity(final Class<PRIMATY_KEY> primaryKeyColumnType, final String primaryKeyColumnName,
            final EntityColumn<?>... entityColumns) {
        this(primaryKeyColumnType, primaryKeyColumnName, null, entityColumns);
    }

    protected AbstractEntity(final Class<PRIMATY_KEY> primaryKeyColumnType, final String primaryKeyColumnName,
            final PRIMATY_KEY primaryKeyColumnValue, final EntityColumn<?>... entityColumns) {
        this(new EntityColumn<>(primaryKeyColumnName, primaryKeyColumnType), primaryKeyColumnValue);
    }

    protected AbstractEntity(final EntityColumn<PRIMATY_KEY> entityColumn, final EntityColumn<?>... entityColumns) {
        this(entityColumn, null, entityColumns);
    }

    protected AbstractEntity(final EntityColumn<PRIMATY_KEY> entityColumn, final PRIMATY_KEY primaryKeyColumnValue,
            final EntityColumn<?>... entityColumns) {

        primaryKey = new IEntityColumnInfo<PRIMATY_KEY>() {

            @Override
            public EntityColumn<PRIMATY_KEY> getFst() {
                return entityColumn;
            }

            @Override
            public PRIMATY_KEY getSnd() {
                return primaryKeyColumnValue;
            }
        };

        columns = new LinkedHashMap<>();
        initColumns(entityColumn);
    }

    private void initColumns(final EntityColumn<?>... entityColumns) {

        for (final EntityColumn<?> entityColumn : entityColumns)
            if (columns.put(entityColumn, null) == null)
                throw new EntityInitException(String.format("Couldn't init column with name \"%s\" of type \"%s\".",
                        entityColumn.columnName, entityColumn.columnType));
    }

    public <T> T getColumnValue(final EntityColumn<T> entityColumn) {

        return entityColumn.columnType.cast(columns.get(entityColumn));
    }

    public <T> boolean setColumnValue(final EntityColumn<T> entityColumn, final T value) {

        return true;
    }

    public PRIMATY_KEY getPrimaryKeyValue() {
        return primaryKey.getSnd();
    }

    public abstract String getTableName();
}