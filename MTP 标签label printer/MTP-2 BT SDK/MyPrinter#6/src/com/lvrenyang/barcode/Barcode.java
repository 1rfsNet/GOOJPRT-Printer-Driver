package com.lvrenyang.barcode;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;

// 生成条码
public abstract class Barcode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2466304586312050733L;

	public enum BarcodeType {

		UPCA(0), UPCE(1), EAN13(2), EAN8(3), CODE39(4), I25(5), CODEBAR(6), CODE93(
				7), CODE128(8), CODE11(9), MSI(10), _128m(11), EAN128(12), _25C(
				13), _39C(14), _39(15), EAN13_2(16), EAN13_5(17), EAN8_2(18), EAN8_5(
				19), POST(20), UPCA_2(21), UPCA_5(22), UPCE_2(23), UPCE_5(24), CPOST(
				25), MSIC(26), PLESSEY(27), ITF14(28), EAN14(29);

		private final int value;

		BarcodeType(int value) {
			this.value = value;
		}

		public int getIntValue() {
			return value;
		}
	}

	/**
	 * 使用id来识别
	 */
	public int id;

	/**
	 * 使用type来选择算法
	 */
	public int type;

	/**
	 * maxbits就是最大点数，填充条码的点位置，不能超过该点数。 按照maxbits的值，开缓冲区
	 */
	protected int maxbits;
	protected boolean[] buffer;
	protected int idx;

	/**
	 * unitwidth 单元宽度
	 */
	public int unitwidth;
	public int height;

	/**
	 * 字符串文本
	 */
	public String strText;

	/**
	 * 默认构造函数，初始化一些变量，避免空指针 调用Encode函数，填充缓冲区。之后就不能再改变了
	 * 
	 */
	public Barcode(int maxbits, int height, int unitwidth, String strText) {
		this.maxbits = maxbits;
		this.buffer = new boolean[maxbits];
		this.idx = 0;
		this.height = height;
		this.unitwidth = unitwidth;
		this.strText = strText;
	}

	public void setDots(boolean[] srcDots) {
		for (int i = 0; i < srcDots.length; ++i)
			buffer[idx++] = srcDots[i];
	}

	public void setDot(boolean srcDot) {
		buffer[idx++] = srcDot;
	}

	/**
	 * 不同的条码类型，对数据的要求不同，需要判断数据是否合法，能否编码。 如果合法，调用genCheckSum
	 * 
	 * @return
	 */
	public abstract boolean isDataValid();

	/**
	 * 生成校验
	 * 
	 * @return
	 */
	public abstract boolean genCheckSum();

	public void clear() {
		idx = 0;
	}

	/**
	 * 编码
	 * 
	 * @return
	 */
	public abstract boolean make();

	/**
	 * 必须先调用Encode，进行编码之后，才能使用该函数获取宽度。
	 * 
	 * @return
	 */
	public int getWidth() {
		return idx * unitwidth;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * 根据Encode的结果，将buffer变成图片。
	 * 
	 * @return
	 */
	public Bitmap generateBitmap() {
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Config.ARGB_8888);

		int pixels_first_line[] = new int[getWidth()];
		int pixels[] = new int[getWidth() * getHeight()];
		int color;
		for (int i = 0; i < idx; ++i) {

			if (buffer[i])
				color = Color.BLACK;
			else
				color = Color.WHITE;

			for (int j = 0; j < unitwidth; ++j)
				pixels_first_line[i * unitwidth + j] = color;

		}

		for (int i = 0; i < getHeight(); ++i) {
			System.arraycopy(pixels_first_line, 0, pixels, i
					* pixels_first_line.length, pixels_first_line.length);
		}

		bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(),
				bitmap.getHeight());

		return bitmap;
	}
}
