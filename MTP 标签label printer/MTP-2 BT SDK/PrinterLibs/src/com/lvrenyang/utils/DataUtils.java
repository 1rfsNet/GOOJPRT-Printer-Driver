
package com.lvrenyang.utils;

import java.util.Locale;
import java.util.Random;

public class DataUtils {
	public static boolean bytesEquals(byte[] d1, byte[] d2) {
		if (d1 == null && d2 == null)
			return true;
		else if (d1 == null || d2 == null)
			return false;

		if (d1.length != d2.length)
			return false;

		for (int i = 0; i < d1.length; i++)
			if (d1[i] != d2[i])
				return false;

		return true;
	}

	public static boolean bytesEquals(byte[] d1, int offset1, byte[] d2,
			int offset2, int length) {
		if (d1 == null || d2 == null)
			return false;

		if ((offset1 + length > d1.length) || (offset2 + length > d2.length))
			return false;

		for (int i = 0; i < length; i++)
			if (d1[i + offset1] != d2[i + offset2])
				return false;

		return true;
	}

	public static char[] bytestochars(byte[] data) {
		char[] cdata = new char[data.length];
		for (int i = 0; i < cdata.length; i++)
			cdata[i] = (char) (data[i] & 0xff);
		return cdata;
	}

	public static byte[] getRandomByteArray(int nlength) {
		byte[] data = new byte[nlength];
		Random rmByte = new Random(System.currentTimeMillis());
		for (int i = 0; i < nlength; i++) {
			// 该方法的作用是生成一个随机的int值，该值介于[0,n)的区间，也就是0到n之间的随机int值，包含0而不包含n
			data[i] = (byte) rmByte.nextInt(256);
		}
		return data;
	}

	public static void blackWhiteReverse(byte data[]) {
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) ~(data[i] & 0xff);
		}
	}

	public static byte[] getSubBytes(byte[] org, int start, int length) {
		byte[] ret = new byte[length];
		for (int i = 0; i < length; i++) {
			ret[i] = org[i + start];
		}
		return ret;
	}

	public static String bytesToStr(byte[] rcs) {
		StringBuilder stringBuilder = new StringBuilder();
		String tmp;
		for (int i = 0; i < rcs.length; i++) {
			tmp = Integer.toHexString(rcs[i] & 0xff);
			tmp = tmp.toUpperCase(Locale.getDefault());
			if (tmp.length() == 1) {
				stringBuilder.append("0x0" + tmp);
			} else {
				stringBuilder.append("0x" + tmp);
			}

			if ((i % 16) != 15) {
				stringBuilder.append(" ");
			} else {
				stringBuilder.append("\n");
			}
		}
		return stringBuilder.toString();
	}

	public static byte[] cloneBytes(byte[] data) {
		byte[] ret = new byte[data.length];
		for (int i = 0; i < data.length; i++)
			ret[i] = data[i];
		return ret;
	}

	public static byte bytesToXor(byte[] data, int start, int length) {
		if (length == 0)
			return 0;
		else if (length == 1)
			return data[start];
		else {
			int result = data[start] ^ data[start + 1];
			for (int i = start + 2; i < start + length; i++)
				result ^= data[i];
			return (byte) result;
		}
	}

	/**
	 * 将多个字节数组按顺序合并
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] byteArraysToBytes(byte[][] data) {

		int length = 0;
		for (int i = 0; i < data.length; i++)
			length += data[i].length;
		byte[] send = new byte[length];
		int k = 0;
		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < data[i].length; j++)
				send[k++] = data[i][j];
		return send;
	}

	/**
	 * 
	 * @param orgdata
	 * @param orgstart
	 * @param desdata
	 * @param desstart
	 * @param copylen
	 */
	public static void copyBytes(byte[] orgdata, int orgstart, byte[] desdata,
			int desstart, int copylen) {
		for (int i = 0; i < copylen; i++) {
			desdata[desstart + i] = orgdata[orgstart + i];
		}
	}

	public static String bytesToStr(byte[] rcs, int offset, int count) {
		StringBuilder stringBuilder = new StringBuilder();
		String tmp;
		for (int i = 0; i < count; i++) {
			tmp = Integer.toHexString(rcs[i + offset] & 0xff);
			tmp = tmp.toUpperCase(Locale.getDefault());
			if (tmp.length() == 1) {
				stringBuilder.append("0x0" + tmp);
			} else {
				stringBuilder.append("0x" + tmp);
			}

			if ((i % 16) != 15) {
				stringBuilder.append(" ");
			} else {
				stringBuilder.append("\r\n");
			}
		}
		return stringBuilder.toString();
	}

	private static final byte chartobyte[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0,
			0, 0, 0, 0, 0, 0, 0xA, 0xB, 0xC, 0xD, 0xE, 0xF };
	private static final char bytetochar[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * 必须保证传进来的ch1和ch2都是数字或者大写ABCDEF
	 * 
	 * @param ch1
	 * @param ch2
	 * @return
	 */
	public static byte HexCharsToByte(char ch, char cl) {

		byte b = (byte) (((chartobyte[ch - '0'] << 4) & 0xF0) | ((chartobyte[cl - '0']) & 0xF));

		return b;
	}

	public static char[] ByteToHexChars(byte b) {
		char chs[] = { '0', '0' };
		chs[0] = bytetochar[(b >> 4) & 0xF];
		chs[1] = bytetochar[(b) & 0xF];
		return chs;
	}

	public static boolean IsHexChar(char c) {
		if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f')
				|| (c >= 'A' && c <= 'F'))
			return true;
		else
			return false;
	}

	public static byte[] HexStringToBytes(String str) {
		int count = str.length();
		byte[] data = null;
		if (count % 2 == 0) {
			data = new byte[count / 2];

			for (int i = 0; i < count; i += 2) {
				char ch = str.charAt(i);
				char cl = str.charAt(i + 1);

				if (IsHexChar(ch) && IsHexChar(cl)) {
					if (ch >= 'a')
						ch -= 0x20;
					if (cl >= 'a')
						cl -= 0x20;
					data[i / 2] = HexCharsToByte(ch, cl);
				} else {
					data = null;
					break;
				}
			}
		}
		return data;
	}

	public static StringBuilder BytesToHexStr(byte[] data, int offset, int count) {
		StringBuilder str = new StringBuilder();
		for (int i = offset; i < offset + count; i++) {
			str.append(ByteToHexChars(data[i]));
		}
		return str;
	}

	public static StringBuilder RemoveChar(String str, char c) {
		StringBuilder sb = new StringBuilder();
		int length = str.length();
		char tmp;
		for (int i = 0; i < length; i++) {
			tmp = str.charAt(i);
			if (tmp != c)
				sb.append(tmp);
		}
		return sb;
	}

	public static String byteToStr(byte rc) {
		String rec;
		String tmp = Integer.toHexString(rc & 0xff);
		tmp = tmp.toUpperCase(Locale.getDefault());

		if (tmp.length() == 1) {
			rec = "0x0" + tmp;
		} else {
			rec = "0x" + tmp;
		}

		return rec;
	}

	/**
	 * 获取byte的idx处的位 位和索引对应关系为 10001000 76543210
	 * 
	 * @param ch
	 * @param idx
	 * @return
	 */
	public static final int getBit(byte ch, int idx) {
		return 0x1 & (ch >> idx);
	}

	public static void BytesArrayFill(byte[] data, int offset, int count,
			byte value) {
		for (int i = offset; i < offset + count; ++i)
			data[i] = value;

	}
}
