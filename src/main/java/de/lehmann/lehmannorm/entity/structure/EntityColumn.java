package de.lehmann.lehmannorm.entity.structure;

import java.util.Map;

/**
 * @author Tim Lehmann
 *
 * @param <ECVT>
 *            entity column value type
 */
public class EntityColumn<ECVT> implements Map.Entry<EntityColumnInfo<ECVT>, ECVT> {

    public final EntityColumnInfo<ECVT> entityColumn;
    private ECVT                        entityColumnValue;

    public EntityColumn(final EntityColumnInfo<ECVT> entityColumn, final ECVT entityColumnValue) {
        super();
        this.entityColumn = entityColumn;
        this.entityColumnValue = entityColumnValue;
    }

    @Override
    public EntityColumnInfo<ECVT> getKey() {
        return entityColumn;
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
}