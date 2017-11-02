package de.lehmann.lehmannorm.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Technique {

    @Test
    public void doSomething() {

        final int hc = int.class.hashCode();
        final Integer i_0 = 5;
        final Class<Integer> iC_0 = Integer.class;
        final Class<?> iC_1 = Integer.class;

        Assertions.assertEquals(i_0.getClass(), iC_1);
        Assertions.assertEquals(iC_0, iC_1);

        final Integer hashCodeValueFromIntegerClass_0 = iC_0.hashCode();
        final Integer hashCodeValueFromIntegerClass_1 = iC_0.hashCode();

        Assertions.assertEquals(hashCodeValueFromIntegerClass_0, hashCodeValueFromIntegerClass_1);
        final int a = String.class.hashCode();
        final int b = "".getClass().hashCode();
        Assertions.assertEquals(a, b);

    }
}
