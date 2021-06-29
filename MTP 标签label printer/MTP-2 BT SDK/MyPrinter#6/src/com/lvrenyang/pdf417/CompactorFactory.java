package com.lvrenyang.pdf417;


public class CompactorFactory {

    public Compactor getCompactor(SequenceMode sequenceMode) {
        switch(sequenceMode) {
            case BYTE_MODE_ONE:
            case BYTE_MODE_TWO:
                return new ByteModeCompactor();
            case BYTE_MODE_THREE:
                return new CharacterModeCompactor();
            case NUMBER_MODE:
                return new NumberModeCompactor();
            default:
                return new TextModeCompactor();
        }
    }
}
