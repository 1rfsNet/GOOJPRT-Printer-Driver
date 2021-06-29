package com.lvrenyang.labelitem;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.lvrenyang.label.Label1;

public class ItemBox extends LabelItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1361472779842448518L;
	public int startx;
	public int starty;
	public int width;
	public int height;
	public int borderwidth = 5;
	public int bordercolor = 1;

	public ItemBox(int startx, int starty, int width, int height) {
		this.startx = startx;
		this.starty = starty;
		this.width = width;
		this.height = height;
		this.type = LabelItemType.BOX;
	}

	@Override
	public void Write() {
		// TODO Auto-generated method stub
		Label1.DrawBox(startx, starty, startx + width, starty + height,
				borderwidth, bordercolor);
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
		Paint paint = new Paint();

		if (1 == bordercolor) {
			// 边框是黑色
			paint.setColor(Color.BLACK);
			canvas.drawRect(0, 0, width, height, paint);
			paint.setColor(Color.WHITE);
			canvas.drawRect(borderwidth, borderwidth, width - borderwidth,
					height - borderwidth, paint);
		} else if (0 == bordercolor) {
			paint.setColor(Color.GRAY);
			canvas.drawRect(0, 0, width, height, paint);
			paint.setColor(Color.TRANSPARENT);
			canvas.drawRect(borderwidth, borderwidth, width - borderwidth,
					height - borderwidth, paint);
		}

		return bitmap;
	}

}
