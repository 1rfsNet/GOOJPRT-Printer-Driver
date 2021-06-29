package com.lvrenyang.barcode;

public class I25Barcode extends Barcode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8802358985287021751L;

	public I25Barcode(int maxbits, int height, int unitwidth, String strText) {
		super(maxbits, height, unitwidth, strText);
		// TODO Auto-generated constructor stub
		type = BarcodeType.I25.getIntValue();
	}

	/**
	 * 25码要求数据的位数必须是偶数
	 */
	@Override
	public boolean isDataValid() {
		// TODO Auto-generated method stub
		if (0 != (strText.length() % 2))
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
		return true;
	}

	boolean[] char_start = { true, false, true, false };
	boolean[] char_stop = { true, true, false, true };

	enum NW {
		N, W;
	}

	NW[] char_0 = { NW.N, NW.N, NW.W, NW.W, NW.N };
	NW[] char_1 = { NW.W, NW.N, NW.N, NW.N, NW.W };
	NW[] char_2 = { NW.N, NW.W, NW.N, NW.N, NW.W };
	NW[] char_3 = { NW.W, NW.W, NW.N, NW.N, NW.N };
	NW[] char_4 = { NW.N, NW.N, NW.W, NW.N, NW.W };
	NW[] char_5 = { NW.W, NW.N, NW.W, NW.N, NW.N };
	NW[] char_6 = { NW.N, NW.W, NW.W, NW.N, NW.N };
	NW[] char_7 = { NW.N, NW.N, NW.N, NW.W, NW.W };
	NW[] char_8 = { NW.W, NW.N, NW.N, NW.W, NW.N };
	NW[] char_9 = { NW.N, NW.W, NW.N, NW.W, NW.N };

	NW[][] char_tbl = { char_0, char_1, char_2, char_3, char_4, char_5, char_6,
			char_7, char_8, char_9 };

	void fillTwoChar(char ch1, char ch2) {
		int idx1 = ch1 - '0';
		int idx2 = ch2 - '0';
		NW[] nwch1 = char_tbl[idx1];
		NW[] nwch2 = char_tbl[idx2];

		boolean[] dots = new boolean[14];
		int pos = 0;
		for (int i = 0; i < 5; ++i) {
			if (NW.N == nwch1[i]) {
				dots[pos++] = true;
			} else {
				dots[pos++] = true;
				dots[pos++] = true;
			}
			if (NW.N == nwch2[i]) {
				dots[pos++] = false;
			} else {
				dots[pos++] = false;
				dots[pos++] = false;
			}
		}

		setDots(dots);
	}

	@Override
	public boolean make() {
		// TODO Auto-generated method stub
		clear();
		setDots(char_start);
		for (int i = 0; i < strText.length(); i += 2) {
			fillTwoChar(strText.charAt(i), strText.charAt(i + 1));
		}
		setDots(char_stop);
		return true;
	}

}
