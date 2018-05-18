package de.lehmann.lehmannorm.logic.mocks;

import de.lehmann.lehmannorm.entity.AbstractEntity;
import de.lehmann.lehmannorm.entity.structure.EntityColumnInfo;
import de.lehmann.lehmannorm.entity.structure.EntityToOneColumnInfo;

/**
 * A collection of static classes, we wound to test in a bundle.
 *
 *
 * @author Tim Lehmann
 */
public class EntityTestClassCollection {

    /*
     * # CASE 1A
     *
     * A(aId, refB) [B have to exist]
     * B(bId, refC) [C have to exist]
     * C(cId)
     */

    public static class TestEntityA_1A extends AbstractEntity<Integer> {

        public final static EntityColumnInfo<Integer>        ID   = new EntityToOneColumnInfo<>("ID", Integer.class);
        public final static EntityColumnInfo<TestEntityB_1A> ID_B =
                new EntityToOneColumnInfo<>("ID_B", TestEntityB_1A.class);

        public TestEntityA_1A() {
            super(ID, ID_B);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }

    public static class TestEntityB_1A extends AbstractEntity<Integer> {

        public final static EntityColumnInfo<Integer>        ID    = new EntityToOneColumnInfo<>("ID", Integer.class);
        public final static EntityColumnInfo<TestEntityA_1A> REF_A = new EntityToOneColumnInfo<>(TestEntityA_1A.class);
        public final static EntityColumnInfo<TestEntityC_1A> ID_C  =
                new EntityToOneColumnInfo<>("ID_C", TestEntityC_1A.class);

        public TestEntityB_1A() {
            super(ID, REF_A, ID_C);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }

    public static class TestEntityC_1A extends AbstractEntity<Integer> {

        public final static EntityColumnInfo<Integer>        ID    = new EntityToOneColumnInfo<>("ID", Integer.class);
        public final static EntityColumnInfo<TestEntityB_1A> REF_B = new EntityToOneColumnInfo<>(TestEntityB_1A.class);

        public TestEntityC_1A() {
            super(ID, REF_B);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }

    /*
     * # CASE 1B
     *
     * A(aId, refB, refC) [B and C have to exist]
     * B(bId)
     * C(cId)
     */

    public static class TestEntityA_1B extends AbstractEntity<Integer> {

        public final static EntityColumnInfo<Integer>        ID   = new EntityToOneColumnInfo<>("ID", Integer.class);
        public final static EntityColumnInfo<TestEntityB_1B> ID_B =
                new EntityToOneColumnInfo<>("ID_B", TestEntityB_1B.class);
        public final static EntityColumnInfo<TestEntityC_1B> ID_C =
                new EntityToOneColumnInfo<>("ID_C", TestEntityC_1B.class);

        public TestEntityA_1B() {
            super(ID, ID_B, ID_C);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }

    public static class TestEntityB_1B extends AbstractEntity<Integer> {

        public final static EntityColumnInfo<Integer>        ID    = new EntityToOneColumnInfo<>("ID", Integer.class);
        public final static EntityColumnInfo<TestEntityA_1B> REF_A = new EntityToOneColumnInfo<>(TestEntityA_1B.class);

        public TestEntityB_1B() {
            super(ID, REF_A);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }

    public static class TestEntityC_1B extends AbstractEntity<Integer> {

        public final static EntityColumnInfo<Integer>        ID    = new EntityToOneColumnInfo<>("ID", Integer.class);
        public final static EntityColumnInfo<TestEntityA_1B> REF_A = new EntityToOneColumnInfo<>(TestEntityA_1B.class);

        public TestEntityC_1B() {
            super(ID, REF_A);
        }

        @Override
        public String getTableName() {
            return null;
        }
    }
}
