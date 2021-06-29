package com.lvrenyang.pdf417;

import java.util.regex.Pattern;

public class TextSequenceMatcher extends AbstractSequenceMatcher {

    private static final String SEQUENCE_PATTERN = "([\\x09\\x0a\\x0d\\x20-\\x7e]{5,})";

    private static final Pattern PATTERN = Pattern.compile(SEQUENCE_PATTERN);

    @Override
    Pattern getPattern() {
        return PATTERN;
    }
}
