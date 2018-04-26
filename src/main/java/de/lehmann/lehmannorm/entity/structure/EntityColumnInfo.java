package de.lehmann.lehmannorm.entity.structure;

public interface EntityColumnInfo<ECVT> {

    String getColumnName();

    Class<ECVT> getColumnType();
}