package com.lvrenyang.labelitem;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

/**
 * 用来代替Bitmap，因为Bitmap无法序列化，也无法用json保存。
 * 
 * @author lvrenyang
 * 
 */
public class Image32 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5874950063707934285L;

	public int width;
	public int height;
	public int[] pixels;

	public Image32() {
		this.width = 0;
		this.height = 0;
		this.pixels = new int[width * height];
	};

	public Image32(Bitmap bitmap) {
		setBitmap(bitmap);
	}

	public void setBitmap(Bitmap bitmap) {
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
	}

	public Bitmap getBitmap() {
		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
