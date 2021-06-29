package com.lvrenyang.labelitem;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;

import com.lvrenyang.label.Label1;
import com.lvrenyang.qrcode.ErrorCorrectLevel;
import com.lvrenyang.qrcode.QRCode;

public class ItemQrcode extends LabelItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1304949863184912956L;

	public int startx;
	public int starty;
	public int version = 0;
	private int ecc = 1;
	public int unitwidth = 4;
	public int rotate = 0;
	public String strText;

	public Boolean[][] modules;

	public ItemQrcode(int startx, int starty, String strText) {
		super();
		this.startx = startx;
		this.starty = starty;
		this.strText = strText;
		this.type = LabelItemType.QRCODE;
	}

	@Override
	public void Write() {
		// TODO Auto-generated method stub
		Label1.DrawQRCode(startx, starty, version, ecc, unitwidth, rotate,
				strText.getBytes());
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return modules.length * unitwidth;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return modules.length * unitwidth;
	}

	/**
	 * 如果数据超出最大数据，会返回false。
	 * 
	 * @return
	 */
	public boolean make() {
		try {
			QRCode x = QRCode.getMinimumQRCode(strText, ErrorCorrectLevel.M);
			modules = x.getModules();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Bitmap getBitmap() {

		int width = getWidth();
		int height = getHeight();
		int[] pixels = new int[width * height];

		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				pixels[width * y + x] = modules[y / unitwidth][x / unitwidth] ? Color.BLACK
						: Color.WHITE;

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

}
