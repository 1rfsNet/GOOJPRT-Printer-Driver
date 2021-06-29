package com.lvrenyang.pdf417;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class NumberModeCompactor extends AbstractCompactor {

    @Override
    public List<Integer> generateCodewords(Sequence sequence) {
        List<Integer> codewords = new ArrayList<Integer>();
        String rest;
        String code = sequence.getCode();
        while (code.length() > 0) {
            if (code.length() > 44) {
                rest = code.substring(44);
                code = code.substring(0, 44);
            } else {
                rest = "";
            }
            BigInteger t = new BigInteger("1" + code);
            do {
                BigInteger d = t.mod(BigInteger.valueOf(900));
                t = t.divide(BigInteger.valueOf(900));
                codewords.add(0, d.intValue());
            } while (t.intValue() != 0);
            code = rest;
        }
        return addModeToCodeWords(codewords, sequence.getMode());
    }
}
