package com.lvrenyang.pdf417;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


abstract class AbstractSequenceMatcher implements SequenceMatcher {

    @Override
    public List<SequencePosition> getSequencePositions(String input) {
        ArrayList<SequencePosition> sequencePositions = new ArrayList<SequencePosition>();
        Matcher sequencesMatcher = getPattern().matcher(input);
        if (sequencesMatcher.matches()) {
            for (int i = 1; i <= sequencesMatcher.groupCount(); i++) {
                String group = sequencesMatcher.group(i);
                int offset = input.indexOf(group);
                sequencePositions.add(new SequencePosition(group, offset));
            }
        }
        sequencePositions.add(new SequencePosition("", input.length()));
        return sequencePositions;
    }

    abstract Pattern getPattern();
}
