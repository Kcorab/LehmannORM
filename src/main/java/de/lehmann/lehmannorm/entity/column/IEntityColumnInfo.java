package de.lehmann.lehmannorm.entity.column;

/**
 * @author Tim Lehmann
 *
 * @param <ECV>
 *            type of entity column value
 */
public interface IEntityColumnInfo<ECV> {

    public EntityColumn<ECV> getEntityColumn();

    public ECV getEntityColumnValue();

    public void setEntityColumnValue(ECV entityColumnValue);
}