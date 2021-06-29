package com.lvrenyang.labelitem;

import java.io.UnsupportedEncodingException;

import android.graphics.Bitmap;
import android.util.Log;

import com.lvrenyang.barcode.Barcode;
import com.lvrenyang.label.Label1;
import com.lvrenyang.utils.DataUtils;

public class ItemBarcode extends LabelItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1657204154491930173L;

	private static final String TAG = "ItemBarcode";
	public int startx = 0;
	public int starty = 0;
	public int barocdetype = 0;
	public int height = 48;
	public int unitwidth = 2;
	public int rotate = 0;

	public String codepage = "GBK";
	public String strText = "";

	public Image32 image = new Image32();
	public Barcode barcode = null;

	public ItemBarcode(int startx, int starty, Barcode barcode, Bitmap bitmap) {
		this.startx = startx;
		this.starty = starty;
		setBarcode(barcode, bitmap);
		this.type = LabelItemType.BARCODE;

	}

	public void setBarcode(Barcode barcode, Bitmap bitmap) {
		this.barocdetype = barcode.type;
		this.height = barcode.height;
		this.unitwidth = barcode.unitwidth;
		this.strText = barcode.strText;
		this.barcode = barcode;
		this.image.setBitmap(bitmap);
	}

	@Override
	public void Write() {

		byte[] strbytes = null;
		try {
			strbytes = strText.getBytes(codepage);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		byte[] str = DataUtils
				.byteArraysToBytes(new byte[][] { strbytes, { 0 } });

		Label1.DrawBarcode(startx, starty, barocdetype, height, unitwidth,
				rotate, str);
		Log.i(TAG, "" + startx + ", " + starty + ", " + barocdetype + ", "
				+ height + ", " + unitwidth + ", " + rotate + ", " + str);
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return barcode.getWidth();
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return barcode.getHeight();
	}
}
