package de.lehmann.lehmannorm.entity.structure;

/**
 * @author barock
 *
 * @param <ECVT>
 *          type of entity column value
 */
public interface EntityColumnInfo<ECVT>
{
  String getColumnName();

  Class<ECVT> getColumnType();
}