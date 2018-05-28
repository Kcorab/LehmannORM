package de.lehmann.lehmannorm.entity.structure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map.Entry;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author Tim Lehmann
 */
public class ColumnMapUnitTest
{
  private static IBoundedColumnMap<Object> unitToStaticTest;
  private static IBoundedColumnMap<Object> unitToDynamicTest;

  @BeforeAll
  public static void createStaticColumnMap()
  {
    unitToStaticTest = new ColumnMap<>();
    unitToDynamicTest = new ColumnMap<>();
  }

  @BeforeEach
  public void createDynamicColumnMap()
  {
    unitToDynamicTest.clear();
  }

  public static Object[][] provideUnitData()
  {
    final String columnName = "TEST_COLUMN";

    final String[] values = { null, "OLD_VALUE", "NEW_VALUE", "NEW_VALUE", null, null };

    return new Object[][] {
        {
            new EntityColumn<>(new EntityToOneColumnInfo<>(columnName, String.class), values[0]), // data
            null // expected result
        },
        {
            new EntityColumn<>(new EntityToOneColumnInfo<>(columnName, String.class), values[1]), // data
            values[0] // expected result: value of previous inserted value
        },
        {
            new EntityColumn<>(new EntityToOneColumnInfo<>(columnName, String.class), values[2]), // data
            values[1] // expected result: value of previous inserted value
        },
        {
            new EntityColumn<>(new EntityToOneColumnInfo<>(columnName, String.class), values[3]), // data
            values[2] // expected result: value of previous inserted value
        },
        {
            new EntityColumn<>(new EntityToOneColumnInfo<>(columnName, String.class), values[4]), // data
            values[3] // expected result: value of previous inserted value
        },
        {
            new EntityColumn<>(new EntityToOneColumnInfo<>(columnName, String.class), values[5]), // data
            values[4] // expected result: value of previous insert
        }
    };
  }

  @ParameterizedTest
  @MethodSource(value = "provideUnitData")
  public void testPut(final EntityColumn<Object> data, final Object expectedResult)
  {
    final Object actualResult = unitToStaticTest.put(data.getKey(), data.getValue());

    assertEquals(expectedResult, actualResult);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testGetByKey()
  {
    final EntityColumn<?> entityColumn1 =
        new EntityColumn<>(new EntityToOneColumnInfo<>("FIRST", String.class), "first");

    final EntityColumn<?> entityColumn2 =
        new EntityColumn<>(new EntityToOneColumnInfo<>("SECOND", Integer.class), 2);

    final EntityColumn<?> entityColumn3 =
        new EntityColumn<>(new EntityToOneColumnInfo<>("THIRD", Object.class), new Object());

    unitToDynamicTest.put((EntityColumnInfo<Object>) entityColumn1.getKey(), entityColumn1.getValue());
    unitToDynamicTest.put((EntityColumnInfo<Object>) entityColumn2.getKey(), entityColumn2.getValue());
    unitToDynamicTest.put((EntityColumnInfo<Object>) entityColumn3.getKey(), entityColumn3.getValue());

    assertEquals(entityColumn1.getValue(), unitToDynamicTest.get(entityColumn1.getKey()));
    assertEquals(entityColumn2.getValue(), unitToDynamicTest.get(entityColumn2.getKey()));
    assertEquals(entityColumn3.getValue(), unitToDynamicTest.get(entityColumn3.getKey()));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testGetByIndex()
  {
    final EntityColumn<?> expectedEntityColumn0 =
        new EntityColumn<>(new EntityToOneColumnInfo<>("FIRST", String.class), "first");

    final EntityColumn<?> expectedEntityColumn1 =
        new EntityColumn<>(new EntityToOneColumnInfo<>("SECOND", Integer.class), 2);

    final EntityColumn<?> expectedEntityColumn2 =
        new EntityColumn<>(new EntityToOneColumnInfo<>("THIRD", Object.class), new Object());

    unitToDynamicTest.put((EntityColumnInfo<Object>) expectedEntityColumn0.getKey(),
        expectedEntityColumn0.getValue());
    unitToDynamicTest.put((EntityColumnInfo<Object>) expectedEntityColumn1.getKey(),
        expectedEntityColumn1.getValue());
    unitToDynamicTest.put((EntityColumnInfo<Object>) expectedEntityColumn2.getKey(),
        expectedEntityColumn2.getValue());

    final Entry<EntityColumnInfo<Object>, Object> actualEntityColumn0 = unitToDynamicTest.get(0);
    final Entry<EntityColumnInfo<Object>, Object> actualEntityColumn1 = unitToDynamicTest.get(1);
    final Entry<EntityColumnInfo<Object>, Object> actualEntityColumn2 = unitToDynamicTest.get(2);

    assertEquals(expectedEntityColumn0, actualEntityColumn0);
    assertEquals(expectedEntityColumn1, actualEntityColumn1);
    assertEquals(expectedEntityColumn2, actualEntityColumn2);
  }

  @Test
  public void testBreakTypeSavety()
  {}
}