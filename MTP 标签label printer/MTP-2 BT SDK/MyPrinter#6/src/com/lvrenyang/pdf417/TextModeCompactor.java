package com.lvrenyang.pdf417;


import java.util.ArrayList;
import java.util.List;


public class TextModeCompactor extends AbstractCompactor {

    @Override
    public List<Integer> generateCodewords(Sequence sequence) {
        List<Integer> codewords = new ArrayList<Integer>();
        int submode = 0;
        List<Integer> textArray = new ArrayList<Integer>();
        int codeLength = sequence.getCode().length();
        for (int i = 0; i < codeLength; i++) {
            int charVal = (int) sequence.getCode().charAt(i);
            int indexOfCharacter = ArrayUtil.search(TextSubmodes.TEXT_SUBMODES[submode], charVal);
            if (indexOfCharacter != -1) {
                // same submode
                textArray.add(indexOfCharacter);
            } else {
                // submode changed
                for (int s = 0; s < 4; s++) {
                    // search for new submode
                    indexOfCharacter = ArrayUtil.search(TextSubmodes.TEXT_SUBMODES[s], charVal);
                    if (s != submode && indexOfCharacter != -1) {
                        // s is new submode
                        if (
                                (
                                        ((i + 1) == codeLength) ||
                                                (
                                                        ((i + 1) < codeLength) &&
                                                                (ArrayUtil.search(TextSubmodes.TEXT_SUBMODES[submode], (int) sequence.getCode().charAt(i + 1)) != -1)
                                                ) &&
                                                        (
                                                                (s == 3) ||
                                                                        ((s == 0) && (submode == 1))
                                                        )
                                )
                                ) {
                            if (s == 3) {
                                // punctuate
                                textArray.add(29);
                            } else {
                                // lower to alpha
                                textArray.add(27);
                            }

                        } else {
                            // latch
                            textArray.addAll(TextLatches.TEXT_LATCH.get(String.format("%d%d", submode, s)));
                            submode = s;
                        }

                        textArray.add(indexOfCharacter);
                        break;
                    }
                }
            }
        }

        int textArrayLength = textArray.size();
        if (textArrayLength % 2 != 0) {
            textArray.add(29);
            textArrayLength++;
        }

        for (int i = 0; i < textArrayLength; i+=2) {
            if (i < textArrayLength - 1) {
                codewords.add((30 * textArray.get(i) + textArray.get(i + 1)));
            } else {
                codewords.add((30 * textArray.get(i)));
            }
        }

        return addModeToCodeWords(codewords, sequence.getMode());
    }
}
