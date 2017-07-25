package de.lehmann.lehmannorm.examples.entities;

import de.lehmann.lehmannorm.entities.AbstractEntity;
import de.lehmann.lehmannorm.entities.EntityColumn;

public class ExampleEntity extends AbstractEntity<Integer> {

    public final static String                TABLE_NAME  = "EXAMPLE";
    public final static EntityColumn<Integer> ID          = new EntityColumn<>("ID", Integer.class);
    public final static EntityColumn<String>  DESCRIPTION = new EntityColumn<>("DESCRIPTION", String.class);

    public ExampleEntity() {
        super(ID, DESCRIPTION);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
