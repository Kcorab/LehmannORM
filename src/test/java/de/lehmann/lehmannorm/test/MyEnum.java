package de.lehmann.lehmannorm.test;

public enum MyEnum implements EnumPair<String, Integer> {

    ONE("1", 1), TWO("2", 2);

    private final String  s;
    private final Integer i;

    private MyEnum(final String s, final Integer i) {
        this.s = s;
        this.i = i;
    }

    @Override
    public String getFirstValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getSecondValue() {
        // TODO Auto-generated method stub
        return null;
    }
}
