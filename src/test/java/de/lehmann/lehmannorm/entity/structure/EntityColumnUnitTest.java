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

    public Map<EntityColumnInfo<?>, String> map = new LinkedHashMap<>();

    @BeforeEach
    public void beforeTestMap() {

        map.clear();
    }

    @Test
    public void testEquals() {

        assertEquals(new EntityColumnInfo<>("COLUMN", Integer.class), new EntityColumnInfo<>("COLUMN", Integer.class));
        assertNotEquals(new EntityColumnInfo<>("COLUMN", Integer.class), new EntityColumnInfo<>("COLUMN", Long.class));
    }

    @Test
    public void testMap() {

        final String excpectedValue = "assignedValue";
        map.put(new EntityColumnInfo<>("COLUMN", String.class), excpectedValue);

        String actualValue;

        actualValue = map.get(new EntityColumnInfo<>("COLUMN", String.class));
        assertEquals(excpectedValue, actualValue);

        actualValue = map.get(new EntityColumnInfo<>("COLUMN", Integer.class));
        assertNotEquals(excpectedValue, actualValue);
    }
}
