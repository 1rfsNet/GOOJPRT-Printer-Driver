package com.lvrenyang.labelitem;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.util.Log;

import com.lvrenyang.label.Label1;

public class ItemLine extends LabelItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4432705845357030011L;

	private static final String TAG = "ItemLine";

	public int startx = 10;
	public int starty = 10;
	public int stopx = 100;
	public int stopy = 100;
	public int linewidth = 5;
	public int linecolor = 1;

	public ItemLine(int startx, int starty, int stopx, int stopy) {
		super();
		this.startx = startx;
		this.starty = starty;
		this.stopx = stopx;
		this.stopy = stopy;
		this.type = LabelItemType.LINE;
	}

	@Override
	public void Write() {
		// TODO Auto-generated method stub
		Label1.DrawLine(startx, starty, stopx, stopy, linewidth, linecolor);
		Log.i(TAG, "" + startx + ", " + starty + ", " + stopx + ", " + stopy
				+ ", " + linewidth + ", " + linecolor);
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return Math.abs(startx - stopx) + linewidth;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return Math.abs(starty - stopy) + linewidth;
	}

	public int getLeft() {
		int left = startx < stopx ? startx : stopx;
		left -= linewidth / 2;
		return left;
	}

	public int getTop() {
		int top = starty < stopy ? starty : stopy;
		top -= linewidth / 2;
		return top;
	}

	public int getRight() {
		int right = startx > stopx ? startx : stopx;
		right += linewidth / 2;
		return right;
	}

	public int getBottom() {
		int bottom = starty > stopy ? starty : stopy;
		bottom += linewidth / 2;
		return bottom;
	}

	public int getPadding() {
		return linewidth / 2;
	}

	public int getLength() {
		int w = getWidth();
		int h = getHeight();
		return (int) Math.sqrt(w * w + h * h);
	}

	public Bitmap getBitmap() {

		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(linewidth);

		if (1 == linecolor) {
			// 边框是黑色
			paint.setColor(Color.BLACK);
		} else if (0 == linecolor) {
			paint.setColor(Color.WHITE);
		}

		canvas.drawLine(startx - getLeft(), starty - getTop(), stopx
				- getLeft(), stopy - getTop(), paint);

		return bitmap;
	}
}
