package de.lehmann.lehmannorm.entity.structure;

import java.util.Map;

/**
 * @author Tim Lehmann
 *
 * @param <ECVT>
 *            entity column value type
 */
public interface IColumnMap<ECVT> extends Map<EntityColumnInfo<ECVT>, ECVT> {

    Map.Entry<EntityColumnInfo<ECVT>, ECVT> get(final int index);

    EntityColumnInfo<ECVT> getColumnInfo(final int index);

    ECVT getColumnValue(final int index);
}
