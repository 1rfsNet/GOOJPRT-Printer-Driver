package com.lvrenyang.pdf417;

public enum SequenceMode {

    TEXT_MODE(900),
    BYTE_MODE_ONE(901),
    BYTE_MODE_TWO(924),
    BYTE_MODE_THREE(913),
    NUMBER_MODE(902);

    private final int code;

    private SequenceMode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
