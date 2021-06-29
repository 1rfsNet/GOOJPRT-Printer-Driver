package com.lvrenyang.pdf417;


import java.util.ArrayList;
import java.util.List;

import static com.lvrenyang.pdf417.ErrorCorrection.DEFAULT_LEVEL;
import static com.lvrenyang.pdf417.ErrorCorrection.ErrorCorrectionLevel;
import static com.lvrenyang.pdf417.SequenceMode.*;
import static java.lang.Math.*;

public class PDF417Generator {

    public static final int DEFAULT_ASPECT_RATIO = 2;

    private static final int ROW_HEIGHT = 4;
    public static final int DEFAULT_QUIET_H = 2;
    public static final int DEFAULT_QUIET_V = 2;
    private static final int MAX_CODEWORDS_DATA = 925;

    private final ErrorCorrectionLevel errorCorrection;
    private final float aspectRatio;
    private final int quietV;
    private final int quietH;
    private final CompactorFactory compactorFactory;
    private int codewordIndex;
    private String input;

    public PDF417Generator(String input, ErrorCorrectionLevel errorCorrection, float aspectRatio, int quietV, int quietH) {
        this.errorCorrection = errorCorrection;
        this.aspectRatio = aspectRatio;
        this.quietV = quietV;
        this.quietH = quietH;
        this.compactorFactory = new CompactorFactory();
        this.input = input;
    }

    public PDF417Generator(String input) {
        this(input, DEFAULT_LEVEL, DEFAULT_ASPECT_RATIO, DEFAULT_QUIET_V, DEFAULT_QUIET_H);
    }

    public Barcode encode() throws BarcodeEncodingException {

        List<Sequence> sequences = getInputSequences(input);
        List<Integer> codewords = generateCodewords(sequences);

        ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrection.getErrorCorrectionLevel(errorCorrection, codewords.size());
        int errorSize = errorCorrectionLevel.getErrorSize();

        int nce = (codewords.size() + errorSize + 1);

        // calculate number of columns
        int dataWidth = calculateDataWidth(aspectRatio, ROW_HEIGHT, nce);
        int dataHeight = calculateDataHeight(nce, dataWidth);
        int size = dataWidth * dataHeight;

        dataWidth = adjustDataWidth(dataWidth, aspectRatio, size);
        dataHeight = adjustDataHeight(dataHeight, aspectRatio, size);

        if (size > 928) {
            size = 928;
        }

        // add padding it out
        int padding = (size - nce);
        if (padding > 0) {
            if ((size - dataHeight) == nce) {
                --dataHeight;
                size -= dataHeight;
            } else {
                for (int i = 0; i < padding; i++) {
                    codewords.add(900);
                }
            }
        }

        // add symbol length detection
        int symbolLengthDetection = size - errorSize;
        codewords.add(0, symbolLengthDetection);

        codewords = errorCorrectionLevel.addErrorCorrectionWords(codewords);

        Barcode.Builder barcodeBuilder = new Barcode.Builder();
        barcodeBuilder.setDataWidth(dataWidth);
        barcodeBuilder.setDataHeight(dataHeight);
        barcodeBuilder.setQuietZoneHorizonal(quietH);
        barcodeBuilder.setQuietZoneVertical(quietV);
        barcodeBuilder.setRowHeight(ROW_HEIGHT);

        int barCodeRow = 0;
        codewordIndex = 0;
        int clusterIndex = 0;

        for (int rowIndex = 0; rowIndex < dataHeight; rowIndex++) {
            int[] rowData = buildRow(rowIndex, clusterIndex, dataHeight, dataWidth, errorCorrectionLevel, codewords);
            int currentRow = barCodeRow;

            for (; barCodeRow < ROW_HEIGHT + currentRow; barCodeRow++) {
                barcodeBuilder.addRow(rowData);
            }

            ++clusterIndex;
            if (clusterIndex > 2) {
                clusterIndex = 0;
            }
        }

        return barcodeBuilder.build();
    }

    private int[] buildRow(int rowIndex, int clusterIndex, int dataHeight, int dataWidth, ErrorCorrectionLevel errorCorrectionLevel, List<Integer> codewords) {
        BarcodeRowBuilder rowBuilder = new BarcodeRowBuilder();
        rowBuilder.setQuietZoneHorizontal(quietH);

        int l;
        switch (clusterIndex) {
            case 0:
                l = ((30 * ((int) floor(rowIndex / 3))) + ((int) floor((dataHeight - 1) / 3)));
                break;
            case 1:
                l = ((30 * ((int) floor(rowIndex / 3))) + (errorCorrectionLevel.getErrorCorrectionLevel() * 3) + ((dataHeight - 1) % 3));
                break;
            default:
                l = ((30 * ((int) floor(rowIndex / 3))) + (dataWidth - 1));
                break;
        }

        rowBuilder.addLeftIndicator(Clusters.CLUSTERS[clusterIndex][l]);

        // for each column
        for (int colIndex = 0; colIndex < dataWidth; colIndex++) {
            rowBuilder.addData(Clusters.CLUSTERS[clusterIndex][codewords.get(codewordIndex)]);
            ++codewordIndex;
        }

        switch (clusterIndex) {
            case 0:
                l = ((30 * ((int) floor(rowIndex / 3))) + (dataWidth - 1));
                break;
            case 1:
                l = ((30 * ((int) floor(rowIndex / 3))) + ((int) floor((dataHeight - 1) / 3)));
                break;
            default:
                l = ((30 * ((int) floor(rowIndex / 3))) + (errorCorrectionLevel.getErrorCorrectionLevel() * 3) + ((dataHeight - 1) % 3));
                break;
        }

        rowBuilder.addRightIndicator(Clusters.CLUSTERS[clusterIndex][l]);

        return rowBuilder.build();
    }

    private int adjustDataWidth(int dataWidth, float aspectRatio, int size) {
        if (size > 928) {
            if (Math.abs(aspectRatio - (17 * 29 / 32)) < Math.abs(aspectRatio - (17 * 16 / 58))) {
                dataWidth = 29;
            } else {
                dataWidth = 16;
            }
        }
        return dataWidth;
    }

    private int adjustDataHeight(int dataHeight, float aspectRatio, int size) {
        if (size > 928) {
            if (Math.abs(aspectRatio - (17 * 29 / 32)) < Math.abs(aspectRatio - (17 * 16 / 58))) {
                dataHeight = 32;
            } else {
                dataHeight = 58;
            }
        }
        return dataHeight;
    }

    private int calculateDataHeight(int nce, int numberOfColumns) {
        int dataHeight = (int) ceil(nce / numberOfColumns);
        if (dataHeight < 3) {
            dataHeight = 3;
        } else if (dataHeight > 90) {
            dataHeight = 3;
        }
        return dataHeight;
    }

    private int calculateDataWidth(float aspectRatio, int rowHeight, int nce) {
        int dataWidth = (int) round((sqrt(4761 + (68 * aspectRatio * rowHeight * nce)) - 69) / 34);
        // adjust columns
        if (dataWidth < 1) {
            dataWidth = 1;
        } else if (dataWidth > 30) {
            dataWidth = 30;
        }
        return dataWidth;
    }

    private List<Integer> removeFirstCodeWord(List<Integer> codewords) {
        return codewords.subList(1, codewords.size());
    }

    private boolean isTextMode(List<Integer> codewords) {
        return codewords.get(0) == TEXT_MODE.getCode();
    }


    public List<Sequence> getInputSequences(String input) {
        List<Sequence> sequences = new ArrayList<Sequence>();

        List<SequencePosition> numSequencePositions = new NumberSequenceMatcher().getSequencePositions(input);


        int offset = 0;
        for (int i = 0; i < numSequencePositions.size(); i++) {
            SequencePosition seq = numSequencePositions.get(i);
            int seqlen = seq.getSequence().length();
            if (seq.getOffset() > 0) {
                String prevSequence = input.substring(offset, seq.getOffset());
                List<SequencePosition> textSequencePositions = new TextSequenceMatcher().getSequencePositions(prevSequence);

                int textOffset = 0;
                for (int j = 0; j < textSequencePositions.size(); j++) {
                    SequencePosition textSequencePosition = textSequencePositions.get(j);
                    int textSequenceLen = textSequencePosition.getSequence().length();
                    if (textSequencePosition.getOffset() > 0) {
                        String prevTextSequence = prevSequence.substring(textOffset, textSequencePosition.getOffset());
                        if (prevTextSequence.length() > 0) {
                            if ((prevTextSequence.length() == 1) && (sequences.size() > 0) && (sequences.get(sequences.size() - 1).getMode() == TEXT_MODE)) {
                                sequences.add(new Sequence(BYTE_MODE_THREE, prevTextSequence));
                            } else if ((prevTextSequence.length() % 6) == 0) {
                                sequences.add(new Sequence(BYTE_MODE_TWO, prevTextSequence));
                            } else {
                                sequences.add(new Sequence(BYTE_MODE_ONE, prevTextSequence));
                            }
                        }
                    }
                    if (textSequenceLen > 0) {
                        sequences.add(new Sequence(TEXT_MODE, textSequencePosition.getSequence()));
                    }
                    textOffset = textSequencePosition.getOffset() + textSequenceLen;
                }
            }
            if (seqlen > 0) {
                sequences.add(new Sequence(NUMBER_MODE, seq.getSequence()));
            }
            offset = seq.getOffset() + seqlen;
        }
        return sequences;
    }

    private List<Integer> generateCodewords(List<Sequence> sequences) throws BarcodeEncodingException {
        List<Integer> codewords = new ArrayList<Integer>();
        for (Sequence sequence : sequences) {
            Compactor compactor = compactorFactory.getCompactor(sequence.getMode());
            List<Integer> cw = compactor.generateCodewords(sequence);
            codewords.addAll(cw);
        }

        if (isTextMode(codewords)) {
            codewords = removeFirstCodeWord(codewords);
        }

        // too much data to encode
        if (codewords.size() > MAX_CODEWORDS_DATA) {
            throw new BarcodeEncodingException("Too many codewords generated for data. Cannot create barcode.");
        }

        return codewords;
    }

}
