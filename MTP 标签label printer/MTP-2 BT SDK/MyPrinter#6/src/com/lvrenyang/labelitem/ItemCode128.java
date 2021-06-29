package com.lvrenyang.labelitem;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.lvrenyang.code128.Code128Rendering;
import com.lvrenyang.label.Label1;
import com.lvrenyang.pos.ImageProcessing;
import com.lvrenyang.pos.Pos;

public class ItemCode128 extends LabelItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5911200889220083418L;

	public int startx;
	public int starty;
	public int style = 0x1100;
	public int height = 40;
	public String strText;
	public transient Image32 image;
	public boolean autoinc = false;

	public ItemCode128(int startx, int starty, String strText) {
		super();
		this.startx = startx;
		this.starty = starty;
		this.strText = strText;
		this.image = new Image32();
		this.type = LabelItemType.MYCODE128;
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

	/**
	 * 如果数据超出最大数据，会返回false。
	 * 
	 * @return
	 */
	public boolean make() {
		try {
			Bitmap bitmap = Code128Rendering.MakeBarcodeImage(strText, 2,
					height, true);
			image = new Image32();
			image.setBitmap(bitmap);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Bitmap getBitmap() {
		return image.getBitmap();
	}
}
