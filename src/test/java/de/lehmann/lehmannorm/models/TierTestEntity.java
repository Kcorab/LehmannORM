package de.lehmann.lehmannorm.models;

import de.lehmann.lehmannorm.entity.AbstractEntity;
import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo;

public class TierTestEntity extends AbstractEntity<Integer> {

    public final static String                         TABLE_NAME = "TIER_TEST";
    public final static EntityColumnInfo<Integer>          ID         = new EntityColumnInfo<>("ID", Integer.class);
    public final static EntityColumnInfo<String>           NAME       = new EntityColumnInfo<>("NAME", String.class);
    public final static EntityColumnInfo<PersonTestEntity> BESITZER   =
            new EntityColumnInfo<>("BESITZER", PersonTestEntity.class);

    public TierTestEntity() {
        super(ID, NAME, BESITZER);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}