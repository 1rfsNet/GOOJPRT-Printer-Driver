package com.lvrenyang.pdf417;

import java.util.ArrayList;
import java.util.List;

import static com.lvrenyang.pdf417.ArrayUtil.fill;

public class Barcode {
    private final int numRows;
    private final int numCols;
    private final int[][] barcode;

    protected Barcode(int numRows, int numCols, int[][] barcode) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.barcode = barcode;
    }

    public int getNumCols() {
        return numCols;
    }

    public int getNumRows() {
        return numRows;
    }

    public int[][] getBarcode() {
        return barcode;
    }


    public static class Builder {

        private int numRows;
        private int numCols;
        private List<int[]> rows;
        private int quietZoneHorizonal;
        private int quietZoneVertical;
        private int rowHeight;
        private int dataWidth;
        private int dataHeight;

        public Builder() {
            this.rows = new ArrayList<int[]>();
        }

        public Builder setQuietZoneHorizonal(int quietZoneHorizonal) {
            this.quietZoneHorizonal = quietZoneHorizonal;
            return this;
        }

        public Builder setQuietZoneVertical(int quietZoneVertical) {
            this.quietZoneVertical = quietZoneVertical;
            return this;
        }

        public Builder addRow(int[] rowData) {
            this.rows.add(rowData);
            return this;
        }

        public Builder setRows(List<int[]> rows) {
            this.rows = rows;
            return this;
        }

        public Barcode build() {
            this.numRows = calculateNumberOfRows();
            this.numCols = calculateNumberOfColumns();

            int[][] barcodeData = new int[numRows][numCols];

            int rowNum = 0;
            addHorizontalQuietZone(barcodeData, rowNum, quietZoneHorizonal);

            int i = 0;
            for (rowNum = quietZoneHorizonal; rowNum < numRows - quietZoneHorizonal; rowNum++, i++) {
                barcodeData[rowNum] = rows.get(i);
            }

            addHorizontalQuietZone(barcodeData, rowNum, quietZoneHorizonal);

            return new Barcode(numRows, numCols, barcodeData);
        }

        private void addHorizontalQuietZone(int[][] barcodeData, int startRow, int numRows) {
            for (int i = 0; i < numRows; i++) {
                barcodeData[i+startRow] = fill(numCols, 0);
            }
        }

        private int calculateNumberOfColumns() {
            return ((dataWidth + 2) * 17) + 35 + (2 * quietZoneHorizonal);
        }

        private int calculateNumberOfRows() {
            return (dataHeight * rowHeight) + (2 * quietZoneVertical);
        }

        public void setRowHeight(int rowHeight) {
            this.rowHeight = rowHeight;
        }

        public void setDataWidth(int dataWidth) {
            this.dataWidth = dataWidth;
        }

        public void setDataHeight(int dataHeight) {
            this.dataHeight = dataHeight;
        }

    }
}
