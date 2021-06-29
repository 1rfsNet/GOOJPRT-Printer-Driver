package com.lvrenyang.labelitem;

import com.lvrenyang.label.Label1;
import com.lvrenyang.pos.ImageProcessing;
import com.lvrenyang.pos.Pos;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

// 文本，但是按照图片发下去。
// strText是为了序列化的时候，能够通过strText来恢复EditText，以便获取Bitmap。
// bmp 和 strText 要一起设置
public class ItemPlaintext extends LabelItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3812200845165729847L;

	private static final String TAG = "ItemPlaintext";

	public int startx;
	public int starty;
	public int style = 0x1100;

	public transient Image32 image;
	public String strText;
	public String fontType;
	public int fontSize = 24;
	public boolean autoinc = false;

	public ItemPlaintext(int startx, int starty, String str) {
		super();
		this.startx = startx;
		this.starty = starty;
		this.strText = str;
		image = new Image32();
		this.type = LabelItemType.PLAINTEXT;
	}

	@Override
	public void Write() {
		// TODO Auto-generated method stub
		if(null == image)
			return;
		
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
