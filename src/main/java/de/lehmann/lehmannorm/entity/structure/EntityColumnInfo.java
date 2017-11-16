package de.lehmann.lehmannorm.entity.structure;

import java.util.Map;

/**
 * @author Tim Lehmann
 *
 * @param <ECVT>
 *            entity column value type
 */
public class EntityColumnInfo<ECVT> implements Map.Entry<EntityColumn<ECVT>, ECVT> {

    public final EntityColumn<ECVT> entityColumn;
    private ECVT                    entityColumnValue;

    public EntityColumnInfo(final EntityColumn<ECVT> entityColumn, final ECVT entityColumnValue) {
        super();
        this.entityColumn = entityColumn;
        this.entityColumnValue = entityColumnValue;
    }

    @Override
    public EntityColumn<ECVT> getKey() {
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