package de.lehmann.lehmannorm.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class Technique {

    @Test
    public void doSomething() {

        final One one = new One();

        final Schnittstelle s = one::getSomething;

        ((Schnittstelle) one::getSomething).getString();

        assertEquals("", "something", s.getString());

        final List<Integer> list = new ArrayList<>();

        list.add(3);
        list.add(5);

        final Stream<Integer> stream = list.stream();
    }

    public void doSomething(final String param) {

    }

    /* Inner classes */

    public static interface Schnittstelle {

        public String getString();
    }

    // ## One

    public static class One {

        private String something = "something";

        public String getSomething() {
            return something;
        }

        public void setSomething(final String something) {
            this.something = something;
        }

    }

    // ## Two

    public static class Two {

    }
}
