package com.lvrenyang.pdf417;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextLatches {

    public static final Map<String, List<Integer>> TEXT_LATCH = new HashMap<String, List<Integer>>();

    private TextLatches() {}

    static {
        TextLatches.TEXT_LATCH.put("01", listOf(27));
        TextLatches.TEXT_LATCH.put("02", listOf(28));
        TextLatches.TEXT_LATCH.put("03", listOf(28,25));
        TextLatches.TEXT_LATCH.put("10", listOf(28,28));
        TextLatches.TEXT_LATCH.put("12", listOf(28));
        TextLatches.TEXT_LATCH.put("13", listOf(28,25));
        TextLatches.TEXT_LATCH.put("20", listOf(28));
        TextLatches.TEXT_LATCH.put("21", listOf(27));
        TextLatches.TEXT_LATCH.put("23", listOf(25));
        TextLatches.TEXT_LATCH.put("30", listOf(29));
        TextLatches.TEXT_LATCH.put("31", listOf(29,27));
        TextLatches.TEXT_LATCH.put("32", listOf(29,28));
    }

    private static List<Integer> listOf(int... values) {
        List<Integer> valuesList = new ArrayList<Integer>();
        for (int i = 0; i < values.length; i++) {
            valuesList.add(values[i]);
        }
        return valuesList;
    }
}
