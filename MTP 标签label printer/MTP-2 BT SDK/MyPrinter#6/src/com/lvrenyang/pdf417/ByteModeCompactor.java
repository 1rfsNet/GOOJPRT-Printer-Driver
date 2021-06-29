package com.lvrenyang.pdf417;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class ByteModeCompactor extends AbstractCompactor {

    @Override
    public List<Integer> generateCodewords(Sequence sequence) {
        List<Integer> codewords = new ArrayList<Integer>();

        String rest;
        int subLength;
        String code = sequence.getCode();

        while (code.length() > 0) {
            if (code.length() > 6) {
                rest = code.substring(6);
                code = code.substring(0, 6);
                subLength = 6;
            } else {
                rest = "";
                subLength = code.length();
            }

            if (subLength == 6) {
                BigInteger t = BigInteger.valueOf(0L);
                t = t.add(BigInteger.valueOf(1099511627776L).multiply(BigInteger.valueOf(((int) code.charAt(0)))));
                t = t.add(BigInteger.valueOf(4294967296L)).multiply(BigInteger.valueOf(((int) code.charAt(1))));
                t = t.add(BigInteger.valueOf(16777216L)).multiply(BigInteger.valueOf(((int) code.charAt(2))));
                t = t.add(BigInteger.valueOf(65536L)).multiply(BigInteger.valueOf(((int) code.charAt(3))));
                t = t.add(BigInteger.valueOf(256L)).multiply(BigInteger.valueOf(((int) code.charAt(4))));
                t = t.add(BigInteger.valueOf((int) code.charAt(5)));
                do {
                    BigInteger d = t.mod(BigInteger.valueOf(900));
                    t = t.divide(BigInteger.valueOf(900));
                    codewords.add(0, d.intValue());
                } while (t.intValue() != 0);
            } else {
                for (int i = 0; i < subLength; i++) {
                    codewords.add((int) code.charAt(i));
                }
            }
            code = rest;
        }
        return addModeToCodeWords(codewords, sequence.getMode());
    }
}
