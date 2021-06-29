package com.lvrenyang.pdf417;

public class SequencePosition {

    private final String sequence;
    private final int offset;

    public SequencePosition(String sequence, int offset) {
        this.sequence = sequence;
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public String getSequence() {
        return sequence;
    }
}
