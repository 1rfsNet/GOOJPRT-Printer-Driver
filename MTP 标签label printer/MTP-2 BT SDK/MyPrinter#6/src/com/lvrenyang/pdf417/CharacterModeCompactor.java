package com.lvrenyang.pdf417;


import java.util.ArrayList;
import java.util.List;


public class CharacterModeCompactor extends AbstractCompactor {

    @Override
    public List<Integer> generateCodewords(Sequence sequence) {
        List<Integer> codewords = new ArrayList<Integer>();
        codewords.add((int) sequence.getCode().charAt(0));
        return addModeToCodeWords(codewords, sequence.getMode());
    }
}
