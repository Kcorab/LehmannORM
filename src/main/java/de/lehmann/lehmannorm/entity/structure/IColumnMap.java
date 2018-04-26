package de.lehmann.lehmannorm.entity.structure;

import java.util.Map;

/**
 * @author Tim Lehmann
 *
 * @param <ECVT>
 *            entity column value type
 */
public interface IColumnMap<ECVT> extends Map<EntityToOneColumnInfo<ECVT>, ECVT> {

    Map.Entry<EntityToOneColumnInfo<ECVT>, ECVT> get(final int index);

    EntityToOneColumnInfo<ECVT> getColumnInfo(final int index);

    ECVT getColumnValue(final int index);
}
