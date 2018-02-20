package de.lehmann.lehmannorm.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo;

/**
 * @author Tim Lehmann
 */
public class EntityUnitTest {

    private TestEntityA unitToTestA;
    private TestEntityB unitToTestB;

    @BeforeEach
    public void init() {

        unitToTestA = new TestEntityA();
        unitToTestB = new TestEntityB();
    }

    @Test
    public void setColumnValue() {

        TestEntityB oldRefEntity;

        final Integer newId = 0;
        final String newDescription = "mock entity";

        // Insert new values and check that the old value are null.

        final Integer oldId = unitToTestA.setColumnValue(TestEntityA.ID, newId);
        assertNull(oldId);

        oldRefEntity = unitToTestA.setColumnValue(TestEntityA.REF_ID, unitToTestB);
        assertNull(oldRefEntity);

        final String oldDescription = unitToTestA.setColumnValue(TestEntityA.DESCRIPTION, newDescription);
        assertNull(oldDescription);

        // Check the new values are inserted correctly.

        assertEquals(newId, unitToTestA.getColumnValue(TestEntityA.ID));
        assertEquals(unitToTestB, unitToTestA.getColumnValue(TestEntityA.REF_ID));
        assertEquals(newDescription, unitToTestA.getColumnValue(TestEntityA.DESCRIPTION));

        // Check reverse set of refEntity.

        assertEquals(unitToTestA, unitToTestB.getColumnValue(TestEntityB.REF_ID));

        // Create a new instance of the refEntity

        final TestEntityB newUnitToTestB = new TestEntityB();

        // Check that the old reference entity is unitToTestB.

        oldRefEntity = unitToTestA.setColumnValue(TestEntityA.REF_ID, newUnitToTestB, true);
        assertEquals(unitToTestB, oldRefEntity);

        // Check that reference entity was removed from unitToTestB.

        assertNull(oldRefEntity.getColumnValue(TestEntityB.REF_ID));

        // Check that this entity setting works also in the other way.

        // Set unitToTestA to unitToTestB again. //

        assertNull(unitToTestB.setColumnValue(TestEntityB.REF_ID, unitToTestA));

        // Create new instance of TestEntityA.

        final TestEntityA newUnitToTestA = new TestEntityA();

        // Check that old entity reference is the unitToTestA.

        assertEquals(unitToTestA, unitToTestB.setColumnValue(TestEntityB.REF_ID, newUnitToTestA, true));

        // Check that unitToTestA holds no reference to unitToTestB any more.

        assertNull(unitToTestA.getColumnValue(TestEntityA.REF_ID));

        // Check that the newUnitToTestA holds a reference to unitToTestB.

        assertEquals(unitToTestB, newUnitToTestA.getColumnValue(TestEntityA.REF_ID));
    };

    @Test
    public void setColumnValueNegativ() {

        final EntityColumnInfo<Double> notExistingColumn = new EntityColumnInfo<>("NOT_EXISTING_COLUMN", Double.class);

        assertThrows(IllegalArgumentException.class, () -> unitToTestA.setColumnValue(notExistingColumn, 2.4));
    };

    // ## DATA MOCKS

    private static class TestEntityA extends AbstractEntity<Integer> {

        public static final EntityColumnInfo<Integer>     ID          = new EntityColumnInfo<>("ID", Integer.class);
        public static final EntityColumnInfo<TestEntityB> REF_ID      = new EntityColumnInfo<>(TestEntityB.class);
        public static final EntityColumnInfo<String>      DESCRIPTION =
                new EntityColumnInfo<>("DESCRIPTION", String.class);

        protected TestEntityA() {
            super(ID, REF_ID, DESCRIPTION);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }

    private static class TestEntityB extends AbstractEntity<Integer> {

        public static final EntityColumnInfo<Integer>     ID     = new EntityColumnInfo<>("ID", Integer.class);
        public static final EntityColumnInfo<TestEntityA> REF_ID = new EntityColumnInfo<>("REF_ID", TestEntityA.class);

        protected TestEntityB() {
            super(ID, REF_ID);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }
}
