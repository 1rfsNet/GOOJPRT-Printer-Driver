package com.lvrenyang.labelitem;

import java.io.Serializable;

public abstract class LabelItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6405430374058222866L;

	public enum LabelItemType {
		HARDTEXT(0), LINE(1), BOX(2), RECTANGLE(3), BARCODE(4), QRCODE(5), PDF417(
				6), BITMAP(7), PLAINTEXT(8), BARCODEV2(9), MYCODE128(10), GRID(
				11);

		private final int value;

		LabelItemType(int value) {
			this.value = value;
		}

		public int getIntValue() {
			return value;
		}
	}

	/**
	 * 每一个元素，都要复写Write函数。 实际使用中，当Page调用PageBegin之后， 顺序调用所有的Item的Write函数。
	 */
	public abstract void Write();

	/**
	 * 获取LabelItem在Page页占用的宽度，以便布局。
	 */
	public abstract int getWidth();

	/**
	 * 获取LabelItem在Page页占用的高度，以便布局。
	 */
	public abstract int getHeight();

	/**
	 * 标签元素的type
	 */
	public LabelItemType type;

	/**
	 * id是为了便于查找
	 */
	public int id;

	/**
	 * lock记录是否锁定
	 */
	public boolean lock;
}