package com.lvrenyang.barcode;

public class UPCABarcode extends Barcode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8585067230308202967L;

	public UPCABarcode(int maxbits, int height, int unitwidth, String strText) {
		super(maxbits, height, unitwidth, strText);
		// TODO Auto-generated constructor stub
		this.type = BarcodeType.UPCA.getIntValue();
	}

	/**
	 * UPCA 固定12个数字，1个系统码，5个左侧数据，5个右侧数据，一个检查码 检查码随便填，最后校验的时候会覆盖。
	 */
	@Override
	public boolean isDataValid() {
		// TODO Auto-generated method stub
		if (12 != strText.length())
			return false;

		for (int i = 0; i < strText.length(); ++i) {
			if (!Character.isDigit(strText.charAt(i)))
				return false;
		}

		return true;
	}

	@Override
	public boolean genCheckSum() {
		// TODO Auto-generated method stub
		strText = strText.substring(0, 11);
		strText += calculateCheckDigit(strText);
		return true;
	}

	public static int calculateCheckDigit(String strText) {
		byte[] data = strText.getBytes();
		for (int i = 0; i < data.length; ++i)
			data[i] -= '0';
		int c1 = data[0] + data[2] + data[4] + data[6] + data[8] + data[10];
		int c2 = c1 * 3;
		int c3 = data[1] + data[3] + data[5] + data[7] + data[9];
		int c4 = (c2 + c3) % 10;
		int c = 10 - c4;
		return c == 10 ? 0 : c;
	}

	boolean[] lchar_0 = { false, false, false, true, true, false, true };
	boolean[] lchar_1 = { false, false, true, true, false, false, true };
	boolean[] lchar_2 = { false, false, true, false, false, true, true };
	boolean[] lchar_3 = { false, true, true, true, true, false, true };
	boolean[] lchar_4 = { false, true, false, false, false, true, true };
	boolean[] lchar_5 = { false, true, true, false, false, false, true };
	boolean[] lchar_6 = { false, true, false, true, true, true, true };
	boolean[] lchar_7 = { false, true, true, true, false, true, true };
	boolean[] lchar_8 = { false, true, true, false, true, true, true };
	boolean[] lchar_9 = { false, false, false, true, false, true, true };
	boolean[][] lchar_tbl = { lchar_0, lchar_1, lchar_2, lchar_3, lchar_4,
			lchar_5, lchar_6, lchar_7, lchar_8, lchar_9 };
	boolean[] char_start = { true, false, true };
	boolean[] char_mid = { false, true, false, true, false };
	boolean[] char_stop = { true, false, true };

	boolean[] getRch(boolean[] lch) {
		boolean[] rch = new boolean[lch.length];
		for (int i = 0; i < lch.length; ++i)
			rch[i] = !lch[i];
		return rch;
	}

	@Override
	public boolean make() {
		// TODO Auto-generated method stub
		clear();

		boolean[] lch, rch;
		setDots(char_start);
		for (int i = 0; i < 6; ++i) {
			lch = lchar_tbl[strText.charAt(i) - '0'];
			setDots(lch);
		}
		setDots(char_mid);
		for (int i = 6; i < 12; ++i) {
			lch = lchar_tbl[strText.charAt(i) - '0'];
			rch = getRch(lch);
			setDots(rch);
		}
		setDots(char_stop);
		return true;
	}
}
