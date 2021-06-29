package com.lvrenyang.labelitem;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.Config;

import com.lvrenyang.label.Label1;

public class ItemRect extends LabelItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5777293528852718227L;

	public int startx;
	public int starty;
	public int width;
	public int height;
	public int color = 1;

	public ItemRect(int startx, int starty, int width, int height) {
		super();
		this.startx = startx;
		this.starty = starty;
		this.width = width;
		this.height = height;
		this.type = LabelItemType.RECTANGLE;
	}

	@Override
	public void Write() {
		// TODO Auto-generated method stub
		Label1.DrawRectangel(startx, starty, startx + width, starty + height,
				color);
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return width;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return height;
	}

	public Bitmap getBitmap() {

		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);

		if (1 == color) {
			// 矩形是黑色
			canvas.drawColor(Color.BLACK);
		} else if (0 == color) {
			canvas.drawColor(Color.WHITE);
		}

		return bitmap;
	}

}
