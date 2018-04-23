package com.laile.esf.common.util;

import java.util.Arrays;
import java.util.Random;

public class Hex {
    private static final char[] hexcode = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
            'F' };

    private static final byte[] hexToByte = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13,
            14, 15 };

    public static String toHex(byte[] a) {
        StringBuffer strBuff = new StringBuffer(a.length * 2);

        for (int i = 0; i < a.length; i++) {
            strBuff.append(hexcode[(a[i] >> 4 & 0xF)]);
            strBuff.append(hexcode[(a[i] & 0xF)]);
        }

        return strBuff.toString();
    }

    public static byte[] hexToByteArray(String hexString) {
        char[] a = hexString.toCharArray();
        int byteNum = a.length / 2;

        if (byteNum * 2 != a.length) {
            throw new IllegalArgumentException("String length must be a multiple of two.");
        }

        byte[] result = new byte[byteNum];
        int i = 0;
        try {
            for (; i < a.length; i += 2) {
                byte highByte = hexToByte[a[i]];
                byte lowByte = hexToByte[a[(i + 1)]];

                if (highByte < 0) {
                    throw new IllegalArgumentException("Illegal character: " + a[i]);
                }

                if (lowByte < 0) {
                    throw new IllegalArgumentException("Illegal character: " + a[(i + 1)]);
                }

                result[(i / 2)] = ((byte) (highByte << 4 | lowByte));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal character: " + a[i] + " or " + a[(i + 1)]);
        }

        return result;
    }

    public static void main(String[] args) {
        int numRuns = 1;
        int numBytes = 2000;
        Random rnd = new Random();
        for (int i = 0; i < numRuns; i++) {
            for (int j = 0; j < numBytes; j++) {
                byte[] arr = new byte[j];
                for (int k = 0; k < j; k++) {
                    arr[k] = ((byte) rnd.nextInt());
                }
                String s = toHex(arr);
                System.out.println(s);
                byte[] b = hexToByteArray(s);
                if (!Arrays.equals(arr, b)) {
                    System.out.println("Dismal failure!");
                }
            }
        }
    }
}