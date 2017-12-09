package de.lehmann.lehmannorm.entity.structure;

import java.util.Map;
import java.util.Objects;

/**
 * @author Tim Lehmann
 *
 * @param <ECVT>
 *            entity column value type
 */
public class EntityColumn<ECVT> implements Map.Entry<EntityColumnInfo<ECVT>, ECVT> {

    public final EntityColumnInfo<ECVT> entityColumnInfo;
    private ECVT                        entityColumnValue;

    public EntityColumn(final EntityColumnInfo<ECVT> entityColumnInfo, final ECVT entityColumnValue) {
        super();
        this.entityColumnInfo = entityColumnInfo;
        this.entityColumnValue = entityColumnValue;
    }

    @Override
    public EntityColumnInfo<ECVT> getKey() {
        return entityColumnInfo;
    }

    @Override
    public ECVT getValue() {
        return entityColumnValue;
    }

    @Override
    public ECVT setValue(final ECVT value) {

        final ECVT oldValue = this.entityColumnValue;
        this.entityColumnValue = value;

        return oldValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (entityColumnInfo == null ? 0 : entityColumnInfo.hashCode());
        result = prime * result + (entityColumnValue == null ? 0 : entityColumnValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj)
            return true;

        if (obj != null && getClass().equals(obj.getClass())) {

            final EntityColumn<?> other = (EntityColumn<?>) obj;

            return Objects.equals(entityColumnInfo, other.entityColumnInfo)
                    && Objects.equals(entityColumnValue, other.entityColumnValue);
        }

        return false;
    }
}