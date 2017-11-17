package de.lehmann.lehmannorm.entity.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Tim Lehmann
 *
 * @param <ECVT>
 *            entity column value type
 */
public class ColumnMap<ECVT> implements IColumnMap<ECVT> {

    private final Map<EntityColumnInfo<ECVT>, ECVT>             columnMap;
    private final List<Map.Entry<EntityColumnInfo<ECVT>, ECVT>> columnList;

    public ColumnMap() {
        columnMap = new LinkedHashMap<>();
        columnList = new ArrayList<>();
    }

    @Override
    public int size() {
        return columnMap.size();
    }

    @Override
    public boolean isEmpty() {
        return columnMap.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return columnMap.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return columnMap.containsValue(value);
    }

    @Override
    public ECVT get(final Object key) {
        return columnMap.get(key);
    }

    @Override
    public ECVT put(final EntityColumnInfo<ECVT> key, final ECVT value) {

        final Map.Entry<EntityColumnInfo<ECVT>, ECVT> entry;

        return columnMap.putIfAbsent(key, value);
    }

    @Override
    public ECVT remove(final Object key) {
        return columnMap.remove(key);
    }

    @Override
    public void putAll(final Map<? extends EntityColumnInfo<ECVT>, ? extends ECVT> m) {
        columnMap.putAll(m);
    }

    @Override
    public void clear() {
        columnMap.clear();
        columnList.clear();
    }

    @Override
    public Set<EntityColumnInfo<ECVT>> keySet() {
        return columnMap.keySet();
    }

    @Override
    public Collection<ECVT> values() {
        return columnMap.values();
    }

    @Override
    public Set<Entry<EntityColumnInfo<ECVT>, ECVT>> entrySet() {
        return columnMap.entrySet();
    }

    @Override
    public Entry<EntityColumnInfo<ECVT>, ECVT> get(final int index) {
        return columnList.get(index);
    }
}
