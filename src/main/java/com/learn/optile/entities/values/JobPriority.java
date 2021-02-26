package com.learn.optile.entities.values;

public enum JobPriority {

    HIGH(1), MEDIUM(5), LOW(10);

    private final Integer value;

    JobPriority(Integer value) {
        this.value = value;
    }

    public static JobPriority formValue(Integer value) {
        if (value == 1) {
            return HIGH;
        }
        if (value == 5) {
            return MEDIUM;
        }
        if (value == 10) {
            return LOW;
        }
        throw new IllegalArgumentException(String.format("Invalid value <%s> for enum", value));
    }

    public Integer getValue() {
        return value;
    }

}
