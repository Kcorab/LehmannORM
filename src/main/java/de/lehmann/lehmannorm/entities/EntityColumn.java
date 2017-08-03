package de.lehmann.lehmannorm.entities;

import java.util.Objects;

public class EntityColumn<T> {

    public final String   columnName;
    public final Class<T> columnType;

    public EntityColumn(final String columnName, final Class<T> columnType) {
        super();
        if (columnType == null || "".equals(columnName))
            throw new IllegalArgumentException("The constructors parameter haven't to be null or empty!");

        this.columnName = columnName;
        this.columnType = columnType;
    }

    @Override
    public int hashCode() {
        return columnName.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj)
            return true;

        if (obj != null && getClass().equals(obj.getClass())) {

            final EntityColumn<?> other = (EntityColumn<?>) obj;

            return Objects.equals(this.columnName, other.columnName)
                    && Objects.equals(this.columnType, other.columnType);
        }

        return false;
    }
}