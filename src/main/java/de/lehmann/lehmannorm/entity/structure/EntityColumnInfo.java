package de.lehmann.lehmannorm.entity.structure;

import java.util.Objects;

/**
 * @author Tim Lehmann
 *
 * @param <ECVT>
 *            type of entity column value which is not stored in this class
 */
public class EntityColumnInfo<ECVT> {

    public final String      columnName;
    public final Class<ECVT> columnType;

    public EntityColumnInfo(final String columnName, final Class<ECVT> columnType) {
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

            final EntityColumnInfo<?> other = (EntityColumnInfo<?>) obj;

            return Objects.equals(this.columnName, other.columnName)
                    && Objects.equals(this.columnType, other.columnType);
        }

        return false;
    }
}