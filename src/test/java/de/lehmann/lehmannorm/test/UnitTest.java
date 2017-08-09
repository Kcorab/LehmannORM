package de.lehmann.lehmannorm.test;

public class UnitTest {

    public static class SpecialClass {

        private final GenericHolder<String>[] concretGericHolders;
        private final GenericHolder<?>[]      someGericHolders;

        public SpecialClass(final GenericHolder<String>[] concretGericHolders,
                final GenericHolder<?>... someGericHolders) {
            super();
            this.concretGericHolders = concretGericHolders;
            this.someGericHolders = someGericHolders;
        }
    }

    public static class GenericHolder<T> {

        private final T value;

        public GenericHolder(final T value) {
            super();
            this.value = value;
        }

        public T getValue() {
            return value;
        }
    }
}
