package de.lehmann.lehmannorm.entity;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import de.lehmann.lehmannorm.entity.structure.ColumnMap;
import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo;
import de.lehmann.lehmannorm.entity.structure.EntityToManyColumnInfo;
import de.lehmann.lehmannorm.entity.structure.EntityToOneColumnInfo;
import de.lehmann.lehmannorm.entity.structure.IBoundedColumnMap;

/**
 * @author Tim Lehmann
 *
 * @param <PK>
 *            type of primary key (value)
 */
public abstract class AbstractEntity<PK> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEntity.class);

    private final EntityColumnInfo<PK>      primaryKeyInfo;
    private final IBoundedColumnMap<Object> entityColumns;

    // delegate constructors

    /**
     * Construct an entity instance with default name "ID" for primary key column
     * and without a default value for the primary key.
     *
     * @param primaryKeyType
     *            type of the primary key
     * @param entityColumnInfos
     *            columns of this entity
     */
    protected AbstractEntity(final Class<PK> primaryKeyType, final EntityColumnInfo<?>... entityColumnInfos) {
        this(primaryKeyType, "ID", entityColumnInfos);
    }

    /**
     * Construct an entity instance without a default value for the primary key.
     *
     * @param primaryKeyType
     *            type of the primary key
     * @param primaryKeyName
     *            name of primary key column
     * @param entityColumnInfos
     *            columns of this entity
     */
    protected AbstractEntity(final Class<PK> primaryKeyType, final String primaryKeyName,
            final EntityColumnInfo<?>... entityColumnInfos) {
        this(primaryKeyType, primaryKeyName, null, entityColumnInfos);
    }

    /**
     * Construct an entity instance.
     *
     * @param primaryKeyType
     *            type of the primary key
     * @param primaryKeyName
     *            name of primary key column
     * @param primaryKeyValue
     *            value of primary key
     * @param entityColumnInfos
     *            columns of this entity
     */
    protected AbstractEntity(final Class<PK> primaryKeyType, final String primaryKeyName,
            final PK primaryKeyValue, final EntityColumnInfo<?>... entityColumnInfos) {
        this(new EntityToOneColumnInfo<>(primaryKeyName, primaryKeyType), primaryKeyValue, entityColumnInfos);
    }

    /**
     * Construct an entity instance without a default value for the primary key.
     *
     * @param primaryKeyInfo
     *            encapsulated variant for name of primary key column and for type
     *            of the primary key
     * @param entityColumnInfos
     *            columns of this entity
     */
    protected AbstractEntity(final EntityColumnInfo<PK> primaryKeyInfo,
            final EntityColumnInfo<?>... entityColumnInfos) {
        this(primaryKeyInfo, null, entityColumnInfos);
    }

    // master constructor

    /**
     * Construct an entity instance.
     *
     * @param primaryKeyInfo
     *            encapsulated variant for name of primary key column and for type
     *            of the primary key
     * @param primaryKeyValue
     *            value of primary key
     * @param entityColumnInfos
     *            columns of this entity
     */
    @SuppressWarnings("unchecked")
    protected AbstractEntity(final EntityColumnInfo<PK> primaryKeyColumnInfo, final PK primaryKeyValue,
            final EntityColumnInfo<?>... entityColumnInfos) {

        this.primaryKeyInfo = primaryKeyColumnInfo;
        this.entityColumns = new ColumnMap<>(1 + entityColumnInfos.length);

        entityColumns.put((EntityColumnInfo<Object>) primaryKeyColumnInfo, primaryKeyValue);

        int index = -1;
        while (index < entityColumnInfos.length) {

            final EntityColumnInfo<?> entityColumnInfo = entityColumnInfos[++index];

            if (!EntityToManyColumnInfo.class.isAssignableFrom(entityColumnInfo.getClass()))
                break;
            else
                this.entityColumns.put((EntityColumnInfo<Object>) entityColumnInfo, new ArrayList<>());
        }

        while (index < entityColumnInfos.length)
            entityColumns.put((EntityColumnInfo<Object>) entityColumnInfos[index++], null);
    }

    // copy constructor

    /**
     * Construct an entity instance by existing instance. Cautions: Column values
     * aren't applied.
     *
     * @param sourceEntity
     *            entity instance copy from
     */
    @SuppressWarnings("unchecked")
    public AbstractEntity(final AbstractEntity<PK> sourceEntity) {

        this.primaryKeyInfo = sourceEntity.primaryKeyInfo;
        this.entityColumns = new ColumnMap<>(sourceEntity.entityColumns.size());

        // Copy all immutable values of type EntityColumn to this entry set.

        this.entityColumns.put((EntityColumnInfo<Object>) sourceEntity.primaryKeyInfo, null);

        final int size = sourceEntity.entityColumns.size();
        int index = 0;
        while (index < size) {

            final EntityColumnInfo<Object> entityColumnInfo = sourceEntity.entityColumns.getKeyByIndex(++index);

            if (!EntityToManyColumnInfo.class.isAssignableFrom(entityColumnInfo.getClass()))
                break;
            else
                this.entityColumns.put(entityColumnInfo, new ArrayList<>());
        }

        while (index < size)
            entityColumns.put(sourceEntity.entityColumns.getKeyByIndex(index++), null);
    }

    // Getter / Setter

    /**
     * @param entityColumnInfo
     *            column information for the column whose value you are looking for
     * @return
     *         value of column that is declared by given parameter
     */
    public <T> T getColumnValue(final EntityColumnInfo<T> entityColumnInfo) {

        return entityColumnInfo.getColumnType().cast(entityColumns.get(entityColumnInfo));
    }

    /**
     * @param entityColumnInfo
     *            column information for the column whose value you are want to set
     * @param entityColumnValue
     *            the new value
     * @return
     *         old value of column that is declared by given parameter
     */
    @SuppressWarnings("unchecked")
    public <T> T setColumnValue(final EntityColumnInfo<T> entityColumnInfo, final T entityColumnValue) {

        checkThatColumnExists(entityColumnInfo);

        LOGGER.debug(() -> "Column value is a primitive.");

        return (T) setColumnValueGeneric(entityColumnInfo, entityColumnValue);
    }

    /**
     * @param entityColumnInfo
     *            column information for the column that holds the foreign entity
     *            you are want to set
     * @param newRefEntity
     *            the new entity that will be associated with this entity
     * @return
     *         the entity that was associated before with this entity
     */
    public <T extends AbstractEntity<?>> T setColumnValue(final EntityColumnInfo<T> entityColumnInfo,
            final T newRefEntity) {

        return setColumnValue(entityColumnInfo, newRefEntity, true);
    }

    /**
     * @param entityColumnInfo
     *            column information for the column that holds the foreign entity
     *            you are want to set
     * @param newRefEntity
     *            the new entity that is now associated with this entity
     * @param removeOldEntity
     *            Should the entity that was associated before with this entity be
     *            removed?
     * @return
     *         the entity that was associated before with this entity
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractEntity<?>> T setColumnValue(final EntityColumnInfo<T> entityColumnInfo,
            final T newRefEntity, final boolean removeOldEntity) {

        checkThatColumnExists(entityColumnInfo);

        LOGGER.debug(() -> "Column value is a reference entity.");

        final T oldRefEntity;

        if (EntityToManyColumnInfo.class.isAssignableFrom(entityColumnInfo.getClass())) {

            ((Collection<Object>) this.entityColumns.get(entityColumnInfo)).add(newRefEntity);
            oldRefEntity = null;

        } else {
            oldRefEntity = (T) setColumnValueGeneric(entityColumnInfo, newRefEntity);

            /*
             * If the EntityColumnInfo stores a type that extends AbstractEntity then the
             * columnName will ignore by hashCode() and equals(...). So, we are able to
             * override the old value (AbstractEntity) by the key (EntityColumnInfo) without
             * knowing the contingent columName.
             */
            final EntityColumnInfo<?> eci = new EntityToOneColumnInfo<>(this.getClass());

            if (oldRefEntity != null && removeOldEntity)
                ((AbstractEntity<?>) oldRefEntity).setColumnValueGeneric(eci, null);

            if (newRefEntity != null) {
                final AbstractEntity<?> oldRefEntity2 =
                        (AbstractEntity<?>) ((AbstractEntity<?>) newRefEntity).setColumnValueGeneric(eci, this);

                if (oldRefEntity2 != null && removeOldEntity) {

                    final EntityColumnInfo<?> eci2 = new EntityToOneColumnInfo<>(newRefEntity.getClass());
                    oldRefEntity2.setColumnValueGeneric(eci2, null);
                }
            }
        }

        return oldRefEntity;
    }

    /**
     *
     * @param entityColumnInfo
     * @param entityColumnValue
     * @return
     */
    @SuppressWarnings("unchecked")
    private Object setColumnValueGeneric(final EntityColumnInfo<?> entityColumnInfo, final Object entityColumnValue) {

        return entityColumns.put((EntityColumnInfo<Object>) entityColumnInfo, entityColumnValue);
    }

    private void checkThatColumnExists(final EntityColumnInfo<?> entityColumnInfo) {

        if (!this.entityColumns.containsKey(entityColumnInfo)) {

            final CharSequence columnName =
                    AbstractEntity.class.isAssignableFrom(entityColumnInfo.getColumnType()) ? ""
                            : "' and name '" + entityColumnInfo.getColumnName();

            throw new IllegalArgumentException(
                    "The entity '" +
                            this.getClass().getSimpleName() +
                            "' hasn't got a column for type '" +
                            entityColumnInfo.getColumnType().getSimpleName() +
                            columnName +
                            ". May you forgot to add the information?");
        }
    }

    public IBoundedColumnMap<Object> getAllColumns() {

        return this.entityColumns;
    }

    public EntityColumnInfo<PK> getPrimaryKeyInfo() {

        return primaryKeyInfo;
    }

    public PK getPrimaryKeyValue() {
        return this.primaryKeyInfo.getColumnType().cast(entityColumns.get(primaryKeyInfo));
    }

    @SuppressWarnings("unchecked")
    public void setPrimaryKeyValue(final PK primaryKeyValue) {
        this.entityColumns.put((EntityColumnInfo<Object>) primaryKeyInfo, primaryKeyValue);
    }

    public abstract String getTableName();
}