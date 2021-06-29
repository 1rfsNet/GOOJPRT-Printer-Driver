package com.lvrenyang.labelitem;

import java.io.UnsupportedEncodingException;

import android.util.Log;

import com.lvrenyang.label.Label1;
import com.lvrenyang.utils.DataUtils;

public class ItemHardtext extends LabelItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 73175255506465754L;

	private static final String TAG = "ItemHardtext";

	public int startx = 0;
	public int starty = 0;
	public int font = 24;
	public String strText = "";
	public String codepage = "GBK";
	public boolean bold = false;
	public boolean underline = false;
	public boolean blackwhitereserve = false;
	public boolean deleteline = false;
	/**
	 * 
	 */
	public int turn = 0;
	public int widthTimes = 1;
	public int heightTimes = 1;

	public ItemHardtext(String text) {
		strText = text;
		this.type = LabelItemType.HARDTEXT;
	}

	public ItemHardtext(int startx, int starty, String text) {
		this.startx = startx;
		this.starty = starty;
		strText = text;
		this.type = LabelItemType.HARDTEXT;
	}

	@Override
	public void Write() {
		// TODO Auto-generated method stub
		int style = 0;
		if (bold)
			style |= 1 << 0;
		if (underline)
			style |= 1 << 1;
		if (blackwhitereserve)
			style |= 1 << 2;
		if (deleteline)
			style |= 1 << 3;
		style |= turn << 4;
		style |= widthTimes << 8;
		style |= heightTimes << 12;
		try {
			Label1.DrawPlainText(
					startx,
					starty,
					font,
					style,
					DataUtils.byteArraysToBytes(new byte[][] {
							strText.getBytes(codepage), { 0 } }));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i(TAG, "" + startx + ", " + starty + ", " + font + ", " + style
				+ ", " + strText);
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub

		int width = 0;

		// 字符串只有一行，汉字字体，宽高都是font
		for (int i = 0; i < strText.length(); ++i) {
			int val = strText.charAt(i);
			if (val > 255)
				width += font;
			else
				width += (font / 2);
		}

		return width * widthTimes;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub

		int height = font;

		// 字符串只有一行，高度一定是font。

		return height * heightTimes;
	}
}
