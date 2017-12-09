package de.lehmann.lehmannorm.entity.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is a {@link Map} {@link List} hybrid. It works like a simple {@link Map}
 * with very few methods of a list. This is only able to work because of the
 * intern usage of an {@link LinkedHashMap}.
 *
 * @author Tim Lehmann
 *
 * @param <ECVT>
 *            entity column value type
 */
public class ColumnMap<ECVT> implements IColumnMap<ECVT> {

    private final LinkedHashMap<EntityColumnInfo<ECVT>, ECVT> mapForColumns;
    private final List<EntityColumnInfo<ECVT>>                listForColumns;

    /**
     * Constructs an empty list map hybrid with an initial capacity of ten.
     */
    public ColumnMap(final int size) {
        mapForColumns = new LinkedHashMap<>();
        listForColumns = new ArrayList<>(size);
    }

    /**
     * Constructs an empty list map hybrid with an initial capacity of ten.
     */
    public ColumnMap() {
        mapForColumns = new LinkedHashMap<>();
        listForColumns = new ArrayList<>();
    }

    @Override
    public int size() {
        return mapForColumns.size();
    }

    @Override
    public boolean isEmpty() {
        return mapForColumns.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return mapForColumns.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return mapForColumns.containsValue(value);
    }

    @Override
    public ECVT get(final Object key) {
        return mapForColumns.get(key);
    }

    /**
     * Returns the element at the specified position in the intern
     * {@link LinkedHashMap}.
     *
     * @param index
     *            index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException
     *             if the index is out of range
     *             (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
    @Override
    public Entry<EntityColumnInfo<ECVT>, ECVT> get(final int index) {

        final EntityColumnInfo<ECVT> entityColumnInfo = listForColumns.get(index);
        final ECVT entityColumnValue = mapForColumns.get(entityColumnInfo);

        return new EntityColumn<>(entityColumnInfo, entityColumnValue);
    }

    @Override
    public EntityColumnInfo<ECVT> getColumnInfo(final int index) {

        return listForColumns.get(index);
    }

    @Override
    public ECVT getColumnValue(final int index) {

        final EntityColumnInfo<ECVT> entityColumnInfo = listForColumns.get(index);
        return mapForColumns.get(entityColumnInfo);
    }

    /**
     * Associates the specified value with the specified key in this map. If the map
     * previously contained a mapping for the key and the old isn't equals to the
     * new value, the old value is replaced by the new value.
     *
     * @param key
     *            key with which the specified value is to be associated
     * @param value
     *            value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or <tt>null</tt> if
     *         there was no mapping for <tt>key</tt>. (A <tt>null</tt> return can
     *         also indicate that the map previously associated <tt>null</tt> with
     *         <tt>key</tt>.)
     */
    @Override
    public ECVT put(final EntityColumnInfo<ECVT> key, final ECVT value) {

        final ECVT oldValue;

        if (!this.mapForColumns.containsKey(key)) {

            // Do an insert, if the key doesn't exist yet.
            insert(key, value);

            oldValue = null;

        } else {

            oldValue = this.mapForColumns.get(key);

            // Do an update, if the new value isn't euqals the old value.
            if (oldValue != null) {

                if (!oldValue.equals(value))
                    update(key, value);

            } else if (value != null)
                update(key, value);
        }

        return oldValue;
    }

    private void insert(final EntityColumnInfo<ECVT> key, final ECVT value) {

        this.listForColumns.add(key);
        this.mapForColumns.put(key, value);
    }

    private void update(final EntityColumnInfo<ECVT> key, final ECVT newValue) {

        this.mapForColumns.put(key, newValue);
    }

    /**
     * @throws IllegalArgumentException
     *             if <tt>key</tt> isn't of type {@link EntityColumn}
     *
     * @see Map#remove(Object)
     */
    @Override
    public ECVT remove(final Object key) {

        if (!(key instanceof EntityColumnInfo))

            throw new IllegalArgumentException(
                    "The key value have to be an instance of " + EntityColumnInfo.class.getSimpleName());

        listForColumns.remove(key);
        return mapForColumns.remove(key);
    }

    @Override
    public void putAll(final Map<? extends EntityColumnInfo<ECVT>, ? extends ECVT> m) {

        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        mapForColumns.clear();
        listForColumns.clear();
    }

    @Override
    public Set<EntityColumnInfo<ECVT>> keySet() {
        return mapForColumns.keySet();
    }

    @Override
    public Collection<ECVT> values() {
        return mapForColumns.values();
    }

    @Override
    public Set<Entry<EntityColumnInfo<ECVT>, ECVT>> entrySet() {
        return mapForColumns.entrySet();
    }
}
