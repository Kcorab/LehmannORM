package de.lehmann.lehmannorm.logic;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.lehmann.lehmannorm.models.TestTableEntity;

public class DaoSystemTest { // extends AConnectionUnitTest {

    TestTableEntity               entity;
    Dao<TestTableEntity, Integer> unitToTest;

    /*
     * @BeforeEach public void createExampleEntity() throws InstantiationException,
     * IllegalAccessException, SQLException {
     * 
     * unitToTest = Dao.createInstance(connection, TestTableEntity.class); entity =
     * new TestTableEntity(); entity.setPrimaryKeyValue(6);
     * entity.setColumnValue(TestTableEntity.NUMBER, 1d);
     * entity.setColumnValue(TestTableEntity.DESCRIPTION,
     * "Eine andere Beschreibung."); }
     */
    // @Test
    public void testInsert() {

        assertTrue(unitToTest.insert(entity));
    }

    @Test
    public void testGet() {
        /*
         * final TestTableEntity testTableEntity = new TestTableEntity();
         * testTableEntity.setPrimaryKeyValue(6);
         *
         * Assert.assertTrue(unitToTest.getEntityByPk(testTableEntity));
         *
         * Assert.assertEquals("", entity.getColumnValue(TestTableEntity.DESCRIPTION),
         * testTableEntity.getColumnValue(TestTableEntity.DESCRIPTION));
         * Assert.assertEquals("", entity.getColumnValue(TestTableEntity.NUMBER),
         * testTableEntity.getColumnValue(TestTableEntity.NUMBER));
         */
    }
}
