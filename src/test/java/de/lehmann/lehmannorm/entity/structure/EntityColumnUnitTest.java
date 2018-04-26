package de.lehmann.lehmannorm.entity.structure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Tim Lehmann
 */
public class EntityColumnUnitTest {

    public Map<EntityToOneColumnInfo<?>, String> map = new LinkedHashMap<>();

    @BeforeEach
    public void beforeTestMap() {

        map.clear();
    }

    @Test
    public void testEquals() {

        assertEquals(new EntityToOneColumnInfo<>("COLUMN", Integer.class), new EntityToOneColumnInfo<>("COLUMN", Integer.class));
        assertNotEquals(new EntityToOneColumnInfo<>("COLUMN", Integer.class), new EntityToOneColumnInfo<>("COLUMN", Long.class));
    }

    @Test
    public void testMap() {

        final String excpectedValue = "assignedValue";
        map.put(new EntityToOneColumnInfo<>("COLUMN", String.class), excpectedValue);

        String actualValue;

        actualValue = map.get(new EntityToOneColumnInfo<>("COLUMN", String.class));
        assertEquals(excpectedValue, actualValue);

        actualValue = map.get(new EntityToOneColumnInfo<>("COLUMN", Integer.class));
        assertNotEquals(excpectedValue, actualValue);
    }
}
