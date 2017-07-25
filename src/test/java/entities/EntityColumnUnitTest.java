package entities;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.lehmann.lehmannorm.entities.EntityColumn;

public class EntityColumnUnitTest {

	public Map<EntityColumn<?>, String> map = new LinkedHashMap<>();

	@Test
	public void testEquals() {

		Assert.assertEquals(new EntityColumn<>("COLUMN", Integer.class), new EntityColumn<>("COLUMN", Integer.class));
		Assert.assertNotEquals(new EntityColumn<>("COLUMN", Integer.class), new EntityColumn<>("COLUMN", Long.class));

	}

	@Before
	public void beforeTestMap() {

		map.clear();
	}

	@Test
	public void testMap() {

		final String excpectedValue = "assignedValue";
		map.put(new EntityColumn<>("COLUMN", String.class), excpectedValue);

		String actualValue;

		actualValue = map.get(new EntityColumn<>("COLUMN", String.class));
		Assert.assertEquals(excpectedValue, actualValue);

		actualValue = map.get(new EntityColumn<>("COLUMN", Integer.class));
		Assert.assertEquals(excpectedValue, actualValue);
	}
}
