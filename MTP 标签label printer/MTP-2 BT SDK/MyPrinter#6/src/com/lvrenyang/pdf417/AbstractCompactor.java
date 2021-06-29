package com.lvrenyang.pdf417;


import java.util.List;


abstract class AbstractCompactor implements Compactor {

    protected List<Integer> addModeToCodeWords(List<Integer> codewords, SequenceMode sequenceMode) {
        codewords.add(0, sequenceMode.getCode());
        return codewords;
    }
}
