package de.lehmann.lehmannorm.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import de.lehmann.lehmannorm.AConnectionTest;
import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo;
import de.lehmann.lehmannorm.entity.structure.EntityToOneColumnInfo;
import de.lehmann.lehmannorm.logic.mocks.ConnectionMock;
import de.lehmann.lehmannorm.logic.mocks.EntityTestClassCollection.TestEntityA_1A;
import de.lehmann.lehmannorm.logic.mocks.EntityTestClassCollection.TestEntityA_1B;
import de.lehmann.lehmannorm.logic.mocks.EntityTestClassCollection.TestEntityB_1A;
import de.lehmann.lehmannorm.logic.mocks.EntityTestClassCollection.TestEntityB_1B;
import de.lehmann.lehmannorm.logic.mocks.EntityTestClassCollection.TestEntityC_1A;
import de.lehmann.lehmannorm.logic.mocks.EntityTestClassCollection.TestEntityC_1B;
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

    final EntityColumnInfo<Integer> id = TestEntityA_1A.ID;

    final EntityColumnInfo<?> idUnkown = id;

    @SuppressWarnings("unchecked")
    final EntityColumnInfo<Object> idObject = (EntityToOneColumnInfo<Object>) idUnkown;

    final Class<Object> columnType = idObject.getColumnType();

    LOGGER.debug(columnType::getName);

    connection = new ConnectionStub();
    final TestEntityA_1A testEntityA = new TestEntityA_1A();

    Assertions.assertEquals(
        Dao.getOrCreateCachedDao(connection, testEntityA.getClass()),
        Dao.getOrCreateCachedDao(connection, testEntityA.getClass()));

    connection = new ConnectionStub();
    final TestEntityB_1A testEntityB = new TestEntityB_1A();

    Assertions.assertEquals(
        Dao.getOrCreateCachedDao(connection, testEntityB.getClass()),
        Dao.getOrCreateCachedDao(connection, testEntityB.getClass()));
  }

  /*
   * CASE 1A
   *
   * A(aId, refB) [B have to exist]
   * B(bId, refC) [C have to exist]
   * C(cId)
   *
   * insert order: C, B, A
   */
  @Test
  public void insert1A() throws InstantiationException, IllegalAccessException, SQLException
  {
    final Dao<TestEntityA_1A, Integer> unitToTest =
        Dao.getOrCreateCachedDao(connection, TestEntityA_1A.class);

    final TestEntityC_1A entityC = new TestEntityC_1A();
    entityC.setPrimaryKeyValue(2);

    final TestEntityB_1A entityB = new TestEntityB_1A();
    entityB.setPrimaryKeyValue(1);
    entityB.setColumnValue(TestEntityB_1A.ID_C, entityC);

    final TestEntityA_1A entityA = new TestEntityA_1A();
    entityA.setPrimaryKeyValue(0);
    entityA.setColumnValue(TestEntityA_1A.ID_B, entityB);

    unitToTest.insert(entityA);

    final String expected = 2 + "," + 1 + "," + 0;

    assertEquals(expected, connection.toString());
  }

  /*
   * CASE 1B
   *
   * A(aId, refB, refC) [B and C have to exist]
   * B(bId)
   * C(cId)
   *
   * insert order: B, C, A
   */
  @Test
  public void insert1B() throws InstantiationException, IllegalAccessException, SQLException
  {
    final Dao<TestEntityA_1B, Integer> unitToTest =
        Dao.getOrCreateCachedDao(connection, TestEntityA_1B.class);

    final TestEntityC_1B entityC = new TestEntityC_1B();
    entityC.setPrimaryKeyValue(2);

    final TestEntityB_1B entityB = new TestEntityB_1B();
    entityB.setPrimaryKeyValue(1);

    final TestEntityA_1B entityA = new TestEntityA_1B();
    entityA.setPrimaryKeyValue(0);
    entityA.setColumnValue(TestEntityA_1B.ID_B, entityB);
    entityA.setColumnValue(TestEntityA_1B.ID_C, entityC);

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