package de.lehmann.lehmannorm.test;

import java.util.function.Function;

public class EnumUtils {

    public static <A, B> A findAbyB(final Class<A> clazzA, final EnumPair<A, B> enumPair,
            final Function<EnumPair<A, B>, A> function) {

        return function.apply(enumPair);
    }

    public void doSomething() {

    }
}
