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
public class EntityTestClassCollection
{
  /*
   * # CONSTELLATION 1
   *
   * A(aId, refB) [B have to exist]
   * B(bId, refC) [C have to exist]
   * C(cId)
   */

  public static class TestEntity_1_A extends AbstractEntity<Integer>
  {
    public final static EntityColumnInfo<Integer>        ID   = new EntityToOneColumnInfo<>("ID", Integer.class);
    public final static EntityColumnInfo<TestEntity_1_B> ID_B =
        new EntityToOneColumnInfo<>("ID_B", TestEntity_1_B.class);

    public TestEntity_1_A()
    {
      super(ID, ID_B);
    }

    @Override
    public String getTableName()
    {
      return null;
    }
  }

  public static class TestEntity_1_B extends AbstractEntity<Integer>
  {
    public final static EntityColumnInfo<Integer>        ID    = new EntityToOneColumnInfo<>("ID", Integer.class);
    public final static EntityColumnInfo<TestEntity_1_A> REF_A = new EntityToOneColumnInfo<>(TestEntity_1_A.class);
    public final static EntityColumnInfo<TestEntity_1_C> ID_C  =
        new EntityToOneColumnInfo<>("ID_C", TestEntity_1_C.class);

    public TestEntity_1_B()
    {
      super(ID, REF_A, ID_C);
    }

    @Override
    public String getTableName()
    {
      return null;
    }
  }

  public static class TestEntity_1_C extends AbstractEntity<Integer>
  {
    public final static EntityColumnInfo<Integer>        ID    = new EntityToOneColumnInfo<>("ID", Integer.class);
    public final static EntityColumnInfo<TestEntity_1_B> REF_B = new EntityToOneColumnInfo<>(TestEntity_1_B.class);

    public TestEntity_1_C()
    {
      super(ID, REF_B);
    }

    @Override
    public String getTableName()
    {
      return null;
    }
  }

  /*
   * # CONSTELLATION 2
   *
   * A(aId, refB, refC) [B and C have to exist]
   * B(bId)
   * C(cId)
   */

  public static class TestEntity_2_A extends AbstractEntity<Integer>
  {
    public final static EntityColumnInfo<Integer>        ID   = new EntityToOneColumnInfo<>("ID", Integer.class);
    public final static EntityColumnInfo<TestEntity_2_B> ID_B =
        new EntityToOneColumnInfo<>("ID_B", TestEntity_2_B.class);
    public final static EntityColumnInfo<TestEntity_2_C> ID_C =
        new EntityToOneColumnInfo<>("ID_C", TestEntity_2_C.class);

    public TestEntity_2_A()
    {
      super(ID, ID_B, ID_C);
    }

    @Override
    public String getTableName()
    {
      return null;
    }
  }

  public static class TestEntity_2_B extends AbstractEntity<Integer>
  {
    public final static EntityColumnInfo<Integer>        ID    = new EntityToOneColumnInfo<>("ID", Integer.class);
    public final static EntityColumnInfo<TestEntity_2_A> REF_A = new EntityToOneColumnInfo<>(TestEntity_2_A.class);

    public TestEntity_2_B()
    {
      super(ID, REF_A);
    }

    @Override
    public String getTableName()
    {
      return null;
    }
  }

  public static class TestEntity_2_C extends AbstractEntity<Integer>
  {
    public final static EntityColumnInfo<Integer>        ID    = new EntityToOneColumnInfo<>("ID", Integer.class);
    public final static EntityColumnInfo<TestEntity_2_A> REF_A = new EntityToOneColumnInfo<>(TestEntity_2_A.class);

    public TestEntity_2_C()
    {
      super(ID, REF_A);
    }

    @Override
    public String getTableName()
    {
      return null;
    }
  }
}