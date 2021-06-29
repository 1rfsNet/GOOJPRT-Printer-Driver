package com.lvrenyang.pdf417;

public class Sequence {
    private final SequenceMode mode;
    private final String code;

    public Sequence(SequenceMode mode, String code) {
        this.mode = mode;
        this.code = code;
    }

    public SequenceMode getMode() {
        return mode;
    }

    public String getCode() {
        return code;
    }
}
