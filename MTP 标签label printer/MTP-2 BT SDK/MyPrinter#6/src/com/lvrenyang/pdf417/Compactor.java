package com.lvrenyang.pdf417;


import java.util.List;


public interface Compactor {

    List<Integer> generateCodewords(Sequence sequence);
}

