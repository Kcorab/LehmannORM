package de.lehmann.lehmannorm.models;

import de.lehmann.lehmannorm.entity.AbstractEntity;
import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo;

public class ReligionTestEntity extends AbstractEntity<Integer> {

    public final static String                TABLE_NAME = "RELIGION_TEST";
    public final static EntityColumnInfo<Integer> ID         = new EntityColumnInfo<>("ID", Integer.class);
    public final static EntityColumnInfo<String>  NAME       = new EntityColumnInfo<>("NAME", String.class);
    public final static EntityColumnInfo<String>  SCHRIFT    = new EntityColumnInfo<>("SCHRIFT", String.class);

    public ReligionTestEntity() {
        super(ID, NAME, SCHRIFT);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}