package de.lehmann.lehmannorm.models;

import de.lehmann.lehmannorm.entity.AbstractEntity;
import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo;

public class PersonTestEntity extends AbstractEntity<Integer> {

    public final static String TABLE_NAME = "PERSON_TEST";

    public final static EntityColumnInfo<Integer> ID =
            new EntityColumnInfo<>("ID", Integer.class);

    public final static EntityColumnInfo<String> NAME =
            new EntityColumnInfo<>("NAME", String.class);

    public final static EntityColumnInfo<String> VORNAME =
            new EntityColumnInfo<>("VORNAME", String.class);

    public final static EntityColumnInfo<String> GEBURTSDATUM =
            new EntityColumnInfo<>("GEBURTSDATUM", String.class);

    public final static EntityColumnInfo<ReligionTestEntity> RELIGION =
            new EntityColumnInfo<>("RELIGION", ReligionTestEntity.class);

    public PersonTestEntity() {
        super(ID, RELIGION, NAME, VORNAME, GEBURTSDATUM);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}