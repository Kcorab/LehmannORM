package de.lehmann.lehmannorm.entity.structure;

import java.util.Objects;

import de.lehmann.lehmannorm.entity.AbstractEntity;

/**
 * @author Tim Lehmann
 *
 * @param <ECVT>
 *            type of entity column value which is not stored in this class
 */
public class EntityColumnInfo<ECVT> {

    public final String           columnName;
    public final Class<ECVT>      columnType;
    public final ForeignKeyHolder foreignKeyHolder;

    /**
     * Use this constructor for referenced entities.
     *
     * @param columnName
     * @param columnType
     * @param foreignKeyHolder
     */
    public EntityColumnInfo(final String columnName, final Class<ECVT> columnType,
            final ForeignKeyHolder foreignKeyHolder) {
        super();

        if (columnName.length() == 0)
            throw new IllegalArgumentException("An empty column name isn't allowed!");

        if (AbstractEntity.class.isAssignableFrom(columnType)) {

            if (foreignKeyHolder == null)
                throw new IllegalArgumentException(
                        "There have to be the information about the entity which holds the foreign key.");

        } else if (foreignKeyHolder != null)
            throw new IllegalArgumentException(
                    "It is not allowed to add an foreign key information for a primitive column value type because it makes no sense.");

        this.columnName = columnName;
        this.columnType = columnType;
        this.foreignKeyHolder = foreignKeyHolder;
    }

    /**
     * Use this constructor for primitive column value types.
     *
     * @param columnName
     * @param columnType
     */
    public EntityColumnInfo(final String columnName, final Class<ECVT> columnType) {
        this(columnName, columnType, null);
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
                    && Objects.equals(this.columnType, other.columnType)
                    && Objects.equals(this.foreignKeyHolder, other.foreignKeyHolder);
        }

        return false;
    }

    /**
     * Information about the entity type which holds the foreign key information.
     *
     * @author barock
     */
    public enum ForeignKeyHolder {

        THIS_ENTITY_TYPE, // 1 to 1
        REFERENCED_ENTITY_TYPE, // 1 to 1, 1 to many or many to 1
        THIRD_ENTITY_TYPE // many to many
    }
}