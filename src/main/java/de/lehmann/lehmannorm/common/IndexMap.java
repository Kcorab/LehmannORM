package de.lehmann.lehmannorm.common;

import java.util.Map;

/**
 * @author Tim Lehmann
 *
 * @param <K>
 *            key
 * @param <V>
 *            value
 */
public interface IndexMap<K, V> extends Map<K, V> {

    /**
     * @param index
     * @return key value pair at index position
     */
    Map.Entry<K, V> get(final int index);

    /**
     * @param index
     * @return key at index position
     */
    K getKeyByIndex(final int index);

    /**
     * @param index
     * @return value of the key at index position
     */
    V getValueByIndex(final int index);
}
