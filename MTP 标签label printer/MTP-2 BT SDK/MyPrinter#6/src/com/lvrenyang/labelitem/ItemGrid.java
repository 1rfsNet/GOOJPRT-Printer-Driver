package com.lvrenyang.labelitem;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;

import com.lvrenyang.label.Label1;
import com.lvrenyang.pos.ImageProcessing;
import com.lvrenyang.pos.Pos;

public class ItemGrid extends LabelItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1326856882953754283L;
	public int startx;
	public int starty;
	public int style = 0x1100;
	public String row;
	public String col;
	public int linewidth;
	public Image32 image = new Image32();

	public ItemGrid(int startx, int starty, String row, String col,
			int linewidth) {
		super();
		this.startx = startx;
		this.starty = starty;
		this.row = row;
		this.col = col;
		this.linewidth = linewidth;
		this.type = LabelItemType.GRID;
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
			int[] rows;
			int[] cols;

			String[] strRows = row.split(",");
			String[] strCols = col.split(",");
			rows = new int[strRows.length];
			cols = new int[strCols.length];

			for (int i = 0; i < strRows.length; ++i)
				rows[i] = Integer.parseInt(strRows[i]);
			for (int i = 0; i < strCols.length; ++i)
				cols[i] = Integer.parseInt(strCols[i]);

			int width = cols[0];
			int height = rows[0];

			for (int i = 0; i < cols.length; ++i)
				if (width < cols[i])
					width = cols[i];

			for (int i = 0; i < rows.length; ++i)
				if (height < rows[i])
					height = rows[i];

			Bitmap bitmap = Bitmap.createBitmap(width + linewidth, height
					+ linewidth, Config.ARGB_8888);
			Canvas cv = new Canvas(bitmap);
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);

			// 开始画线
			for (int i = 0; i < cols.length; ++i)
				cv.drawLine(cols[i], 0, cols[i], height, paint);
			for (int i = 0; i < rows.length; ++i)
				cv.drawLine(0, rows[i], width, rows[i], paint);

			Pos.saveMyBitmap(bitmap, "grid.png");
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
