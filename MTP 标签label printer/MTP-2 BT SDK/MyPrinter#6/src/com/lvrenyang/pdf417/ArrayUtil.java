package com.lvrenyang.pdf417;

import java.util.Arrays;

public class ArrayUtil {

    private ArrayUtil() {}

    /**
     * Finds a needle in an (unsorted) haystack.
     * @param haystack
     * @param needle
     * @return the key for the needle, or -1 if the needle doesn't exist in the haystack
     */
    public static int search(int[] haystack, int needle) {
        if (haystack == null) {
            throw new IllegalArgumentException("Haystack is null and cannot be searched");
        }

        for (int i = 0; i < haystack.length; i++) {
            if (haystack[i] == needle) {
                return i;
            }
        }
        return -1;
    }


    public static int[] reverse(int[] input) {
        int[] reversed = new int[input.length];
        for (int i = input.length-1, j=0; i >= 0; i--, j++) {
            reversed[j] = input[i];
        }
        return reversed;
    }

    public static int[] fill(int size, int i) {
        int[] array = new int[size];
        Arrays.fill(array, i);
        return array;
    }

    public static int[] toIntegerArray(String row) {
        char[] chars = row.toCharArray();
        int[] ints = new int[chars.length];
        for (int i = 0; i < chars.length; i++) {
            ints[i] = Integer.parseInt("" + chars[i]);
        }
        return ints;
    }
}
