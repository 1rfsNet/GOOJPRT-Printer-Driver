package com.lvrenyang.pdf417;


import java.util.ArrayList;
import java.util.List;

public class BarcodeRowBuilder {
    private static final String START_PATTERN = "11111111010101000";
    private static final String STOP_PATTERN = "111111101000101001";

    private int quietZoneHorizontal;
    private int leftIndicator;
    private int rightIndicator;
    private List<Integer> dataList = new ArrayList<Integer>();

    public BarcodeRowBuilder setQuietZoneHorizontal(int quietZoneHorizontal) {
        this.quietZoneHorizontal = quietZoneHorizontal;
        return this;
    }

    public BarcodeRowBuilder addLeftIndicator(int leftIndicator) {
        this.leftIndicator = leftIndicator;
        return this;
    }

    public BarcodeRowBuilder addRightIndicator(int rightIndicator) {
        this.rightIndicator = rightIndicator;
        return this;
    }

    public BarcodeRowBuilder addData(int data) {
        dataList.add(data);
        return this;
    }

    public int[] build() {
        // add horizontal quiet zones
        String pstart = fillString("0", quietZoneHorizontal) + START_PATTERN;
        String pstop = STOP_PATTERN + "" + fillString("0", quietZoneHorizontal);

        StringBuilder rowBuffer = new StringBuilder();
        rowBuffer.append(pstart);
        rowBuffer.append(toBinaryString(leftIndicator));
        for (Integer dataItem : dataList) {
            rowBuffer.append(toBinaryString(dataItem));
        }
        rowBuffer.append(toBinaryString(rightIndicator));
        rowBuffer.append(pstop);
        return ArrayUtil.toIntegerArray(rowBuffer.toString());
    }

    private String toBinaryString(int leftIndicator) {
        return String.format("%7s", Integer.toBinaryString(leftIndicator)).replaceAll(" ", "1");
    }

    private String fillString(String s, int size) {
        return String.format("%" + size + "s", " ").replaceAll(" ", s);
    }
}
