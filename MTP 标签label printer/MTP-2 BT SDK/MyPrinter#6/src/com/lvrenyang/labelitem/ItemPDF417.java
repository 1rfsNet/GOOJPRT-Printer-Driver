package com.lvrenyang.labelitem;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;

import com.lvrenyang.label.Label1;
import com.lvrenyang.pdf417.PDF417Generator;
import com.lvrenyang.pdf417.Pdf417lib;
import com.lvrenyang.pos.ImageProcessing;
import com.lvrenyang.pos.Pos;

public class ItemPDF417 extends LabelItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5806526593691254103L;

	public int startx;
	public int starty;
	public int colnum = 4;
	public int lwratio = 3;
	private int ecc = 0;
	public int unitwidth = 3;
	public int rotate = 0;
	public String strText;

	// PDF417生成方式有两种。
	public int[][] barcode;

	public ItemPDF417(int startx, int starty, String strText) {
		super();
		this.startx = startx;
		this.starty = starty;
		this.strText = strText;
		this.type = LabelItemType.PDF417;
	}

	@Override
	public void Write() {
		// TODO Auto-generated method stub
		// Label1.DrawPDF417(startx, starty, colnum, lwratio, ecc, unitwidth,
		// rotate, strText.getBytes());

		byte[] pdata;

		Bitmap bmp = getBitmap();
		// Pos.saveMyBitmap(bmp, "PDF417.png");
		Bitmap alignedBmp = ImageProcessing.alignBitmap(bmp, 8, 1, Color.WHITE);
		// Pos.saveMyBitmap(alignedBmp, "PDF417_ALIGNED.png");
		int width = alignedBmp.getWidth();
		int height = alignedBmp.getHeight();
		pdata = Pos.genRasterData(alignedBmp);

		Label1.DrawBitmap(startx, starty, width, height, 0x1100, pdata);
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return barcode[0].length * unitwidth;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return barcode.length * unitwidth;
	}

	/**
	 * 如果数据超出最大数据，会返回false。
	 * 
	 * @return
	 */

	public boolean make() {
		return make1();
	}

	public boolean make1() {
		try {
			PDF417Generator x = new PDF417Generator(strText);
			barcode = x.encode().getBarcode();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean make2() {
		try {
			Pdf417lib pd = new Pdf417lib();
			pd.setText(strText);
			pd.setOptions(Pdf417lib.PDF417_INVERT_BITMAP);
			pd.setErrorLevel(ecc);
			pd.paintCode();

			byte out[] = pd.getOutBits();
			int cols = (pd.getBitColumns() - 1) / 8 + 1;
			int rows = out.length / cols;
			barcode = new int[rows][cols * 8];

			for (int k = 0; k < out.length; ++k) {
				for (int i = 0; i < 8; ++i) {
					barcode[k / cols][(k % cols) * 8 + i] = (out[k] & (1 << (7 - i))) == 0 ? 1
							: 0;
				}
			}

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
				pixels[width * y + x] = (barcode[y / unitwidth][x / unitwidth] != 0) ? Color.BLACK
						: Color.WHITE;

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

}
