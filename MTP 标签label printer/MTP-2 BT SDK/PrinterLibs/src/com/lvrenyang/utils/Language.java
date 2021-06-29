package com.lvrenyang.utils;

import java.util.Locale;

public class Language {

	// 获取语言索引
	// 使用的时候，一般配合数组来使用。
	// 比如 正在打印
	// String strPrinting[] = {"Printing", "正在打印"};
	// GetIndex获取索引
	public static int GetIndex() {
		String language = Locale.getDefault().getLanguage();
		if (language.endsWith("en"))
			return 0;
		else if (language.endsWith("zh"))
			return 1;
		else
			return 0;
	}
}
