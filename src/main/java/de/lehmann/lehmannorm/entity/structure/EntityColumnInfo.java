package de.lehmann.lehmannorm.entity.structure;

import java.util.Objects;
import java.util.regex.Pattern;

import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import de.lehmann.lehmannorm.entity.AbstractEntity;

/**
 * @author Tim Lehmann
 *
 * @param <ECVT>
 *            type of entity column value which is not stored in this class
 */
public class EntityColumnInfo<ECVT> {

    private static final Logger  LOGGER         = LoggerFactory.getLogger(EntityColumnInfo.class);
    private static final Pattern COLUMN_PATTERN = Pattern.compile("^[A-Z_]+$");

    public final String      columnName;
    public final Class<ECVT> columnType;

    /**
     * @param columnName
     * @param columnType
     */
    public EntityColumnInfo(final String columnName, final Class<ECVT> columnType) {
        super();

        if (columnType == null)
            throw new IllegalArgumentException(
                    "There have to be a type information. The usage of null is disallowed.");

        if (!AbstractEntity.class.isAssignableFrom(columnType)) {

            if (columnName == null)
                throw new IllegalArgumentException(
                        "There have to be a column name for the column. The usage of null is disallowed. ");

            if (!COLUMN_PATTERN.matcher(columnName).find())
                throw new IllegalArgumentException(
                        "The column name have to match \"^[A-Z_]+$\".");
        }

        this.columnName = columnName;
        this.columnType = columnType;
    }

    /**
     * Use this constructor for a reference entity whose table stores the foreign
     * key for the table behind this entity.
     *
     * @param columnType
     *            assignable from {@link AbstractEntity}
     */
    public EntityColumnInfo(final Class<ECVT> columnType) {
        this(null, columnType);
    }

    @Override
    public int hashCode() {

        if (columnName == null)
            return 0;

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