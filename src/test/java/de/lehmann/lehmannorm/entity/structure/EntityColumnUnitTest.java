package de.lehmann.lehmannorm.entity.structure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.lehmann.lehmannorm.entity.structure.EntityColumn;

public class EntityColumnUnitTest {

    public Map<EntityColumn<?>, String> map = new LinkedHashMap<>();

    @BeforeEach
    public void beforeTestMap() {

        map.clear();
    }

    @Test
    public void testEquals() {

        assertEquals(new EntityColumn<>("COLUMN", Integer.class), new EntityColumn<>("COLUMN", Integer.class));
        assertNotEquals(new EntityColumn<>("COLUMN", Integer.class), new EntityColumn<>("COLUMN", Long.class));
    }

    @Test
    public void testMap() {

        final String excpectedValue = "assignedValue";
        map.put(new EntityColumn<>("COLUMN", String.class), excpectedValue);

        String actualValue;

        actualValue = map.get(new EntityColumn<>("COLUMN", String.class));
        assertEquals(excpectedValue, actualValue);

        actualValue = map.get(new EntityColumn<>("COLUMN", Integer.class));
        assertNotEquals(excpectedValue, actualValue);
    }
}
