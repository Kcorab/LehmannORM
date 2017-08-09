package de.lehmann.lehmannorm.entity.column;

import java.util.Objects;

/**
 * @author Tim Lehmann
 *
 * @param <EC>
 *            type of entity column
 */
public class EntityColumn<EC> {

    public final String    columnName;
    public final Class<EC> columnType;

    public EntityColumn(final String columnName, final Class<EC> columnType) {
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