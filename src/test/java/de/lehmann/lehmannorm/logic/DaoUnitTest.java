package de.lehmann.lehmannorm.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import de.lehmann.lehmannorm.AConnectionTest;
import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo;
import de.lehmann.lehmannorm.entity.structure.EntityToOneColumnInfo;
import de.lehmann.lehmannorm.logic.mocks.ConnectionMock;
import de.lehmann.lehmannorm.logic.mocks.EntityTestClassCollection.TestEntity_1_A;
import de.lehmann.lehmannorm.logic.mocks.EntityTestClassCollection.TestEntity_1_B;
import de.lehmann.lehmannorm.logic.mocks.EntityTestClassCollection.TestEntity_1_C;
import de.lehmann.lehmannorm.logic.mocks.EntityTestClassCollection.TestEntity_2_A;
import de.lehmann.lehmannorm.logic.mocks.EntityTestClassCollection.TestEntity_2_B;
import de.lehmann.lehmannorm.logic.mocks.EntityTestClassCollection.TestEntity_2_C;
import de.lehmann.lehmannorm.stubs.ConnectionStub;

/**
 * @author Tim Lehmann
 */
public class DaoUnitTest extends AConnectionTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DaoUnitTest.class);

  // TEST STATIC METHODS

  @Test
  public void getOrCreateCachedDao() throws InstantiationException, IllegalAccessException, SQLException
  {
    Connection connection;

    final EntityColumnInfo<Integer> id = TestEntity_1_A.ID;

    final EntityColumnInfo<?> idUnkown = id;

    @SuppressWarnings("unchecked")
    final EntityColumnInfo<Object> idObject = (EntityToOneColumnInfo<Object>) idUnkown;

    final Class<Object> columnType = idObject.getColumnType();

    LOGGER.debug(columnType::getName);

    connection = new ConnectionStub();
    final TestEntity_1_A testEntityA = new TestEntity_1_A();

    Assertions.assertEquals(
        Dao.getOrCreateCachedDao(connection, testEntityA.getClass()),
        Dao.getOrCreateCachedDao(connection, testEntityA.getClass()));

    connection = new ConnectionStub();
    final TestEntity_1_B testEntityB = new TestEntity_1_B();

    Assertions.assertEquals(
        Dao.getOrCreateCachedDao(connection, testEntityB.getClass()),
        Dao.getOrCreateCachedDao(connection, testEntityB.getClass()));
  }

  // # CONSTELLATION 1

  public static Object[][] dataproviderConstellation1()
  {
    final Object[][] data = new Object[1][];

    TestEntity_1_A entityA;
    TestEntity_1_B entityB;
    TestEntity_1_C entityC;

    /*
     * A(aId, refB) [B have to exist]
     * B(bId, refC) [C have to exist]
     * C(cId)
     *
     * insert order: C, B, A
     */
    entityC = new TestEntity_1_C();
    entityC.setPrimaryKeyValue(2);

    entityB = new TestEntity_1_B();
    entityB.setPrimaryKeyValue(1);
    entityB.setColumnValue(TestEntity_1_B.ID_C, entityC);

    entityA = new TestEntity_1_A();
    entityA.setPrimaryKeyValue(0);
    entityA.setColumnValue(TestEntity_1_A.ID_B, entityB);

    data[0] = new Object[] { entityA, 2 + "," + 1 + "," + 0 };

    /*
     * A(aId, refB) [B have to exist]
     * B(bId, refC) [C have to exist]
     * C(cId)
     *
     * insert order: C, B, A
     */

    return data;
  }

  @MethodSource(value = "dataproviderConstellation1")
  @ParameterizedTest
  public void insert1A(final TestEntity_1_A entityA, final String expectedInsertOrder)
      throws InstantiationException, IllegalAccessException, SQLException
  {
    final Dao<TestEntity_1_A, Integer> unitToTest =
        Dao.getOrCreateCachedDao(connection, TestEntity_1_A.class);

    unitToTest.insert(entityA);

    assertEquals(expectedInsertOrder, connection.toString());
  }

  // # CONSTELLATION 2

  /*
   * CASE 2
   *
   * A(aId, refB, refC) [B and C have to exist]
   * B(bId)
   * C(cId)
   *
   * insert order: B, C, A
   */
  @Test
  public void insert2A() throws InstantiationException, IllegalAccessException, SQLException
  {
    final Dao<TestEntity_2_A, Integer> unitToTest =
        Dao.getOrCreateCachedDao(connection, TestEntity_2_A.class);

    final TestEntity_2_C entityC = new TestEntity_2_C();
    entityC.setPrimaryKeyValue(2);

    final TestEntity_2_B entityB = new TestEntity_2_B();
    entityB.setPrimaryKeyValue(1);

    final TestEntity_2_A entityA = new TestEntity_2_A();
    entityA.setPrimaryKeyValue(0);
    entityA.setColumnValue(TestEntity_2_A.ID_B, entityB);
    entityA.setColumnValue(TestEntity_2_A.ID_C, entityC);

    unitToTest.insert(entityA);

    final String expected = 1 + "," + 2 + "," + 0;

    assertEquals(expected, connection.toString());
  }

  @Override
  protected Connection createConnection() throws SQLException
  {
    return new ConnectionMock();
  }
}