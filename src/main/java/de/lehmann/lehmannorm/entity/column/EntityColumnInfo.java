package de.lehmann.lehmannorm.entity.column;

/**
 * @author Tim Lehmann
 *
 * @param <ECV>
 *            type of the entity column value
 */
public class EntityColumnInfo<ECV> implements IEntityColumnInfo<ECV> {

    public final EntityColumn<ECV> entityColumn;
    private ECV                    entityColumnValue;

    public EntityColumnInfo(final EntityColumn<ECV> entityColumn, final ECV entityColumnValue) {
        super();
        this.entityColumn = entityColumn;
        this.entityColumnValue = entityColumnValue;
    }

    @Override
    public EntityColumn<ECV> getEntityColumn() {
        return entityColumn;
    }

    @Override
    public ECV getEntityColumnValue() {
        return entityColumnValue;
    }

    @Override
    public void setEntityColumnValue(final ECV entityColumnValue) {
        this.entityColumnValue = entityColumnValue;
    }
}