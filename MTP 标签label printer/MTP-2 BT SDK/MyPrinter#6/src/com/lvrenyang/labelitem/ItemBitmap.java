package com.lvrenyang.labelitem;

import com.lvrenyang.label.Label1;
import com.lvrenyang.pos.ImageProcessing;
import com.lvrenyang.pos.Pos;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class ItemBitmap extends LabelItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -458541401566556307L;

	private static final String TAG = "ItemBitmap";

	public int startx;
	public int starty;
	public int style = 0x1100;
	public Image32 image = new Image32();

	public ItemBitmap(int startx, int starty, Bitmap bitmap) {
		this.startx = startx;
		this.starty = starty;
		this.image.setBitmap(bitmap);
		this.type = LabelItemType.BITMAP;
	}

	@Override
	public void Write() {
		// TODO Auto-generated method stub
		byte[] pdata;

		Bitmap bmp = image.getBitmap();
		Bitmap alignedBmp = ImageProcessing.alignBitmap(bmp, 8, 1, Color.WHITE);
		// Pos.saveMyBitmap(alignedBmp, "aligned.png");
		int width = alignedBmp.getWidth();
		int height = alignedBmp.getHeight();
		pdata = Pos.genRasterData(alignedBmp);

		Label1.DrawBitmap(startx, starty, width, height, style, pdata);
		Log.i(TAG, "" + startx + ", " + starty + ", " + width + ", " + height
				+ ", " + style);
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return image.getWidth();
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return image.getHeight();
	}

}
