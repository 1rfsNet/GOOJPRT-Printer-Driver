package com.lvrenyang.pdf417;

import java.util.regex.Pattern;

public class NumberSequenceMatcher extends AbstractSequenceMatcher {

    private static final String SEQUENCE_PATTERN = "([0-9]{13,44})";
    private static final Pattern PATTERN = Pattern.compile(SEQUENCE_PATTERN);

    @Override
    Pattern getPattern() {
        return PATTERN;
    }
}
