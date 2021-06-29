package com.lvrenyang.utils;

import java.util.Locale;

public class StringUtils {

	public static String AutoIncrease(String src) {
		if (null == src)
			return null;

		int len = 0;
		for (int idx = src.length() - 1; idx >= 0; --idx) {
			if (src.charAt(idx) >= '0' && src.charAt(idx) <= '9') {
				len++;
				continue;
			} else
				break;
		}
		if (len == 0)
			return src;
		else if (len > 18)
			len = 18;

		String strhdr = src.substring(0, src.length() - len);
		String strnum = src.substring(src.length() - len);
		int strlen = strnum.length();
		Long strval = Long.parseLong(strnum);
		strval++;

		String dststr = String.format(Locale.US, strhdr + "%0" + strlen + "d",
				strval);
		return dststr;
	}
}
