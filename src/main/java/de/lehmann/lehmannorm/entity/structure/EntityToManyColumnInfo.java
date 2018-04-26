package de.lehmann.lehmannorm.entity.structure;

import de.lehmann.lehmannorm.entity.AbstractEntity;

/**
 * Marker class to define a to many relation.
 *
 * @author Tim Lehmann
 *
 * @param <ECVT>
 */
public class EntityToManyColumnInfo<ECVT extends AbstractEntity<?>> extends EntityToOneColumnInfo<ECVT> {

    public EntityToManyColumnInfo(final Class<ECVT> columnType) {
        super(columnType);
    }

    public EntityToManyColumnInfo(final String columnName, final Class<ECVT> columnType) {
        super(columnName, columnType);
    }
}
