package de.lehmann.lehmannorm.models;

import de.lehmann.lehmannorm.entity.AbstractEntity;
import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo;

public class TestTableEntity extends AbstractEntity<Integer> {

    public final static String                TABLE_NAME  = "TEST_TABLE";
    public final static EntityColumnInfo<Integer> ID          = new EntityColumnInfo<>("ID", Integer.class);
    public final static EntityColumnInfo<Double>  NUMBER      = new EntityColumnInfo<>("NUMBER", Double.class);
    public final static EntityColumnInfo<String>  DESCRIPTION = new EntityColumnInfo<>("DESCRIPTION", String.class);

    public TestTableEntity() {
        super(ID, NUMBER, DESCRIPTION);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}