package de.lehmann.lehmannorm.examples.entities;

import de.lehmann.lehmannorm.entity.AbstractEntity;
import de.lehmann.lehmannorm.entity.column.EntityColumn;

public class TestTableEntity extends AbstractEntity<Integer> {

    public final static String                TABLE_NAME  = "TEST_TABLE";
    public final static EntityColumn<Integer> ID          = new EntityColumn<>("ID", Integer.class);
    public final static EntityColumn<Double>  NUMBER      = new EntityColumn<>("NUMBER", Double.class);
    public final static EntityColumn<String>  DESCRIPTION = new EntityColumn<>("DESCRIPTION", String.class);

    public TestTableEntity() {
        super(ID, NUMBER, DESCRIPTION);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}