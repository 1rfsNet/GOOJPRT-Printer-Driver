package com.lvrenyang.pdf417;


import java.util.List;


public interface SequenceMatcher {

    List<SequencePosition> getSequencePositions(String input);
}
