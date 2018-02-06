package de.lehmann.lehmannorm.entity.structure;

import de.lehmann.lehmannorm.common.IndexMap;

/**
 * @author Tim Lehmann
 *
 * @param <ECIT>
 *            This is the type that is delegated to the
 *            {@link EntityColumnInfo}. It should be the same as ECVT. For some
 *            generic cases it makes scence to ignore the strong bound and
 *            handle these types seperatly.
 * @param <ECVT>
 *            This is the type of the entity column value.
 */
public interface IUnboundedColumnMap<ECIT, ECVT> extends IndexMap<EntityColumnInfo<ECIT>, ECVT> {

}
