package com.lvrenyang.pdf417;


import java.util.List;

import static com.lvrenyang.pdf417.ArrayUtil.*;
import static com.lvrenyang.pdf417.RSFactors.RS_FACTORS;

public class ErrorCorrection {
    public static final ErrorCorrectionLevel LEVEL_ZERO = new ErrorCorrectionLevel(0, RS_FACTORS[0]);
    public static final ErrorCorrectionLevel LEVEL_ONE = new ErrorCorrectionLevel(1, RS_FACTORS[1]);
    public static final ErrorCorrectionLevel LEVEL_TWO = new ErrorCorrectionLevel(2, RS_FACTORS[2]);
    public static final ErrorCorrectionLevel LEVEL_THREE = new ErrorCorrectionLevel(3, RS_FACTORS[3]);
    public static final ErrorCorrectionLevel LEVEL_FOUR = new ErrorCorrectionLevel(4, RS_FACTORS[4]);
    public static final ErrorCorrectionLevel LEVEL_FIVE = new ErrorCorrectionLevel(5, RS_FACTORS[5]);
    public static final ErrorCorrectionLevel LEVEL_SIX = new ErrorCorrectionLevel(6, RS_FACTORS[6]);
    public static final ErrorCorrectionLevel LEVEL_SEVEN = new ErrorCorrectionLevel(7, RS_FACTORS[7]);
    public static final ErrorCorrectionLevel LEVEL_EIGHT = new ErrorCorrectionLevel(8, RS_FACTORS[8]);

    private static final ErrorCorrectionLevel[] errorCorrectionOptions = new ErrorCorrectionLevel[] {
            LEVEL_EIGHT,
            LEVEL_SEVEN,
            LEVEL_SIX,
            LEVEL_FIVE,
            LEVEL_FOUR,
            LEVEL_THREE,
            LEVEL_TWO,
            LEVEL_ONE,
            LEVEL_ZERO
    };

    public static final ErrorCorrectionLevel DEFAULT_LEVEL = LEVEL_TWO;

    private ErrorCorrection() {};

    public static ErrorCorrectionLevel getErrorCorrectionLevel(ErrorCorrectionLevel errorCorrection, int size) {
        ErrorCorrectionLevel chosenErrorCorrection = errorCorrection;
        int maxErrorCorrectionLevel = 8;
        int maxErrorSize = (928 - size);

        while (maxErrorCorrectionLevel > 0) {
            int errorSize = (2 << errorCorrection.getErrorCorrectionLevel());
            if (maxErrorSize > errorSize) {
                break;
            }
            --maxErrorCorrectionLevel;
        }

        if ((errorCorrection.getErrorCorrectionLevel() < 0) || (errorCorrection.getErrorCorrectionLevel() > 8)) {
            if (size < 41) {
                chosenErrorCorrection = LEVEL_TWO;
            } else if (size < 161) {
                chosenErrorCorrection = LEVEL_THREE;
            } else if (size < 321) {
                chosenErrorCorrection = LEVEL_FOUR;
            } else if (size < 864) {
                chosenErrorCorrection = LEVEL_FIVE;
            } else {
                chosenErrorCorrection = findErrorCorrectionLevel(maxErrorCorrectionLevel);
            }
        }

        return chosenErrorCorrection;
    }

    private static ErrorCorrectionLevel findErrorCorrectionLevel(int level) {
        for (ErrorCorrectionLevel errorCorrectionLevel : errorCorrectionOptions) {
            if (errorCorrectionLevel.getErrorCorrectionLevel() == level) {
                return errorCorrectionLevel;
            }
        }
        throw new IllegalArgumentException("Invald level specified");
    }

    public static class ErrorCorrectionLevel {

        private final int errorCorrectionLevel;
        private final int[] errorCorrectionCoefficients;

        public ErrorCorrectionLevel(int errorCorrectionLevel, int[] errorCorrectionCoefficients) {
            this.errorCorrectionLevel = errorCorrectionLevel;
            this.errorCorrectionCoefficients = errorCorrectionCoefficients;
        }

        public int[] getErrorCorrection(List<Integer> codewords) {
            int errorCorrectionSize = (2 << errorCorrectionLevel);
            int errorCorrectionLevelMaxId = (errorCorrectionSize - 1);
            int[] errorCodeWords = fill(errorCorrectionSize, 0);

            for (int i = 0; i < codewords.size(); i++) {
                int t1 = (codewords.get(i) + errorCodeWords[errorCorrectionLevelMaxId] % 929);
                for (int j = errorCorrectionLevelMaxId; j > 0; j--) {
                    int t2 = (t1 * errorCorrectionCoefficients[j]) % 929;
                    int t3 = 929 - t2;
                    errorCodeWords[j] = (errorCodeWords[j -1] + t3) % 929;
                }
                int t2 = (t1 * errorCorrectionCoefficients[0]) % 929;
                int t3 = 929 - t2;
                errorCodeWords[0] = t3 % 929;
            }
            for (int i = 0; i < errorCodeWords.length; i++) {
                if (errorCodeWords[i] != 0) {
                    errorCodeWords[i] = 929 - errorCodeWords[i];
                }
            }
            return reverse(errorCodeWords);
        }

        public int getErrorCorrectionLevel() {
            return errorCorrectionLevel;
        }

        public int getErrorSize() {
            return (2 << errorCorrectionLevel);
        }

        public List<Integer> addErrorCorrectionWords(List<Integer> codewords) {
            int[] errorCorrectionWords = getErrorCorrection(codewords);

            // add error correction
            for (int errorCorrectionWord : errorCorrectionWords) {
                codewords.add(errorCorrectionWord);
            }
            return codewords;
        }
    }


}
