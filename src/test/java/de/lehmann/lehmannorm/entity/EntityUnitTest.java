package de.lehmann.lehmannorm.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo;

/**
 * @author Tim Lehmann
 */
public class EntityUnitTest {

    private EntityMock unitToTest;

    @BeforeEach
    public void init() {

        unitToTest = new EntityMock();
    }

    @Test
    public void setColumnValue() {

        assertEquals(null, unitToTest.getColumnValue(EntityMock.ID));
        assertEquals(null, unitToTest.getColumnValue(EntityMock.DESCRIPTION));

        unitToTest.setColumnValue(EntityMock.ID, 0);
        unitToTest.setColumnValue(EntityMock.DESCRIPTION, "mock entity");

        assertEquals(0, (int) unitToTest.getColumnValue(EntityMock.ID));
        assertEquals("mock entity", unitToTest.getColumnValue(EntityMock.DESCRIPTION));
    };

    @Test
    public void setColumnValueNegativ() {

        final EntityColumnInfo<Double> notExistingColumn = new EntityColumnInfo<>("NOT_EXISTING_COLUMN", Double.class);

        assertThrows(IllegalArgumentException.class, () -> unitToTest.setColumnValue(notExistingColumn, 2.4));
    };

    private static class EntityMock extends AbstractEntity<Integer> {

        public static final EntityColumnInfo<Integer> ID          = new EntityColumnInfo<>("ID", Integer.class);
        public static final EntityColumnInfo<String>  DESCRIPTION = new EntityColumnInfo<>("DESCRIPTION", String.class);

        protected EntityMock() {
            super(ID, DESCRIPTION);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }
}
