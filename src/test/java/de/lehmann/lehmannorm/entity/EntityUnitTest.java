package de.lehmann.lehmannorm.entity;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import de.lehmann.lehmannorm.entity.structure.EntityToManyColumnInfo;
import de.lehmann.lehmannorm.entity.structure.EntityToOneColumnInfo;

/**
 * @author Tim Lehmann
 */
public class EntityUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityUnitTest.class);

    private TestEntityA unitToTestA1;
    private TestEntityA unitToTestA2;
    private TestEntityB unitToTestB1;
    private TestEntityB unitToTestB2;
    private TestEntityC unitToTestC1;

    @BeforeEach
    public void init() {

        unitToTestA1 = new TestEntityA();
        unitToTestA2 = new TestEntityA();
        unitToTestB1 = new TestEntityB();
        unitToTestB2 = new TestEntityB();
        unitToTestC1 = new TestEntityC();
    }

    @Test
    public void constructorCall() {

        unitToTestC1.setPrimaryKeyValue(0);
        unitToTestC1.addRefA(unitToTestA1);
        unitToTestC1.addRefA(unitToTestA2);
        unitToTestC1.setRefB(unitToTestB1);
        unitToTestC1.setFloatingNumber(-1d);

        assertEquals(0, unitToTestC1.getPrimaryKeyValue().intValue());
        assertEquals(unitToTestA1, unitToTestC1.getRefA().get(0));
        assertEquals(unitToTestA2, unitToTestC1.getRefA().get(1));
        assertEquals(unitToTestB1, unitToTestC1.getRefB());
        assertEquals(-1d, unitToTestC1.getFloatingNumber().doubleValue());

        // copy constructor

        final TestEntityC copyEntity = new TestEntityC(unitToTestC1);

        assertEquals(TestEntityC.ID, copyEntity.getAllColumns().getKeyByIndex(0));
        assertEquals(TestEntityC.REF_ID_A, copyEntity.getAllColumns().getKeyByIndex(1));
        assertEquals(TestEntityC.REF_ID_B, copyEntity.getAllColumns().getKeyByIndex(2));
        assertEquals(TestEntityC.FLOATING_NUMBER, copyEntity.getAllColumns().getKeyByIndex(3));
        assertNotEquals(unitToTestC1.getRefA(), copyEntity.getRefA());
    }

    @Test
    public void setPrimitiveColumnValue() {

        final Integer newId = 0;
        final String newDescription = "mock entity";

        assertNull(unitToTestA1.setColumnValue(TestEntityA.ID, newId));

        final String oldDescription = unitToTestA1.setColumnValue(TestEntityA.DESCRIPTION, newDescription);
        assertNull(oldDescription);

        // Check the new values are inserted correctly.

        assertEquals(newId, unitToTestA1.getColumnValue(TestEntityA.ID));
        assertEquals(newDescription, unitToTestA1.getColumnValue(TestEntityA.DESCRIPTION));
    };

    @Test
    public void setToOneReferenceEntity() {

        // # A1 and B1
        assertNull(unitToTestB1.getColumnValue(TestEntityB.REF_ID));

        assertNull(unitToTestA1.setColumnValue(TestEntityA.REF_ID, unitToTestB1));

        assertEquals(unitToTestB1, unitToTestA1.getColumnValue(TestEntityA.REF_ID));

        assertEquals(unitToTestA1, unitToTestB1.getColumnValue(TestEntityB.REF_ID));

        // # A2 and B2

        assertNull(unitToTestA2.getColumnValue(TestEntityA.REF_ID));

        assertNull(unitToTestB2.setColumnValue(TestEntityB.REF_ID, unitToTestA2));

        assertEquals(unitToTestA2, unitToTestB2.getColumnValue(TestEntityB.REF_ID));

        assertEquals(unitToTestB2, unitToTestA2.getColumnValue(TestEntityA.REF_ID));

        // # binds A1 to B2

        assertEquals(unitToTestA2, unitToTestB2.setColumnValue(TestEntityB.REF_ID, unitToTestA1));

        // # check, the old bindings are resolved

        // ## B1 doesn't have a binding anymore
        assertNull(unitToTestB1.getColumnValue(TestEntityB.REF_ID));

        // ## A2 doesn't have a binding anymore
        assertNull(unitToTestA2.getColumnValue(TestEntityA.REF_ID));

    }

    @Test
    public void setToManyReferenceEntity() {

        unitToTestC1.setPrimaryKeyValue(0);
        unitToTestC1.setColumnValue(TestEntityC.REF_ID_A, unitToTestA1);
        unitToTestC1.setColumnValue(TestEntityC.REF_ID_B, unitToTestB1);

    }

    @Test
    public void setColumnValueNegativ() {

        final EntityToOneColumnInfo<Double> notExistingColumn =
                new EntityToOneColumnInfo<>("NOT_EXISTING_COLUMN", Double.class);

        assertThrows(IllegalArgumentException.class, () -> unitToTestA1.setColumnValue(notExistingColumn, 2.4));
    };

    // ## DATA MOCKS

    private static class TestEntityA extends AbstractEntity<Integer> {

        public static final EntityToOneColumnInfo<Integer>     ID          =
                new EntityToOneColumnInfo<>("ID", Integer.class);
        public static final EntityToOneColumnInfo<TestEntityB> REF_ID      =
                new EntityToOneColumnInfo<>(TestEntityB.class);
        public static final EntityToOneColumnInfo<String>      DESCRIPTION =
                new EntityToOneColumnInfo<>("DESCRIPTION", String.class);

        protected TestEntityA() {
            super(ID, REF_ID, DESCRIPTION);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }

    private static class TestEntityB extends AbstractEntity<Integer> {

        public static final EntityToOneColumnInfo<Integer>     ID     =
                new EntityToOneColumnInfo<>("ID", Integer.class);
        public static final EntityToOneColumnInfo<TestEntityA> REF_ID =
                new EntityToOneColumnInfo<>("REF_ID", TestEntityA.class);

        protected TestEntityB() {
            super(ID, REF_ID);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }

    private static class TestEntityC extends AbstractEntity<Integer> {

        public static final EntityToOneColumnInfo<Integer>      ID              =
                new EntityToOneColumnInfo<>("ID", Integer.class);
        public static final EntityToManyColumnInfo<TestEntityA> REF_ID_A        =
                new EntityToManyColumnInfo<>("REF_ID_A", TestEntityA.class);
        public static final EntityToOneColumnInfo<TestEntityB>  REF_ID_B        =
                new EntityToOneColumnInfo<>("REF_ID_B", TestEntityB.class);
        public static final EntityToOneColumnInfo<Double>       FLOATING_NUMBER =
                new EntityToOneColumnInfo<>("FLOATING_NUMBER", Double.class);

        protected TestEntityC() {
            super(ID, REF_ID_A, REF_ID_B, FLOATING_NUMBER);
        }

        public TestEntityC(final AbstractEntity<Integer> sourceEntity) {
            super(sourceEntity);
        }

        @SuppressWarnings("unchecked")
        public List<TestEntityA> getRefA() {

            return (List<TestEntityA>) getAllColumns().getValueByIndex(1);
        }

        public void addRefA(final TestEntityA refA) {

            getRefA().add(refA);
        }

        public TestEntityB getRefB() {

            return (TestEntityB) getAllColumns().getValueByIndex(2);
        }

        public void setRefB(final TestEntityB refB) {

            setColumnValue(REF_ID_B, refB);
        }

        public Double getFloatingNumber() {

            return (Double) getAllColumns().getValueByIndex(3);
        }

        public void setFloatingNumber(final Double floatingNumber) {

            setColumnValue(FLOATING_NUMBER, floatingNumber);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }
}
