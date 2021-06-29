package com.lvrenyang.barcode;

public class UPCEBarcode extends Barcode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6637694769999004526L;

	public UPCEBarcode(int maxbits, int height, int unitwidth, String strText) {
		super(maxbits, height, unitwidth, strText);
		// TODO Auto-generated constructor stub
		this.type = BarcodeType.UPCE.getIntValue();
	}

	/**
	 * 8个数字。第一个数字固定为零。最后一个数字是校验位。可编码的数据，只有中间6个。 传入的数字，第一个数字必须是零。否则认为不合法。
	 * 最后一个数字，在校验的时候会覆盖掉，可以随便填。
	 */
	@Override
	public boolean isDataValid() {
		// TODO Auto-generated method stub
		if (8 != strText.length())
			return false;

		for (int i = 0; i < strText.length(); ++i) {
			if (!Character.isDigit(strText.charAt(i)))
				return false;
		}

		if (strText.charAt(0) != '0')
			return false;

		return true;
	}

	public static String UPCEToUPCA(String strText) {
		String str = strText.substring(1, 7);
		char c = str.charAt(5);
		switch (c) {
		// XXNNN0 0XX000-00NNN-C
		case '0':
		case '1':
		case '2':
			str = '0' + str.substring(0, 2) + c + "00" + "00"
					+ str.substring(2, 5);
			break;

		case '3':
			str = '0' + str.substring(0, 3) + "00" + "000"
					+ str.substring(3, 5);
			break;
		case '4':
			str = '0' + str.substring(0, 4) + "0" + "0000"
					+ str.substring(4, 5);
			break;
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			str = '0' + str.substring(0, 5) + "0000" + c;
			break;
		}
		str += UPCABarcode.calculateCheckDigit(str);
		return str;
	}

	@Override
	public boolean genCheckSum() {
		// TODO Auto-generated method stub
		String strUPCA = UPCEToUPCA(strText);
		strText = strText.substring(1, 7);
		strText = '0' + strText + strUPCA.charAt(11);
		return true;
	}

	boolean[] ochar_0 = { false, false, false, true, true, false, true };
	boolean[] ochar_1 = { false, false, true, true, false, false, true };
	boolean[] ochar_2 = { false, false, true, false, false, true, true };
	boolean[] ochar_3 = { false, true, true, true, true, false, true };
	boolean[] ochar_4 = { false, true, false, false, false, true, true };
	boolean[] ochar_5 = { false, true, true, false, false, false, true };
	boolean[] ochar_6 = { false, true, false, true, true, true, true };
	boolean[] ochar_7 = { false, true, true, true, false, true, true };
	boolean[] ochar_8 = { false, true, true, false, true, true, true };
	boolean[] ochar_9 = { false, false, false, true, false, true, true };
	boolean[][] ochar_tbl = { ochar_0, ochar_1, ochar_2, ochar_3, ochar_4,
			ochar_5, ochar_6, ochar_7, ochar_8, ochar_9 };
	boolean[] echar_0 = { false, true, false, false, true, true, true };
	boolean[] echar_1 = { false, true, true, false, false, true, true };
	boolean[] echar_2 = { false, false, true, true, false, true, true };
	boolean[] echar_3 = { false, true, false, false, false, false, true };
	boolean[] echar_4 = { false, false, true, true, true, false, true };
	boolean[] echar_5 = { false, true, true, true, false, false, true };
	boolean[] echar_6 = { false, false, false, false, true, false, true };
	boolean[] echar_7 = { false, false, true, false, false, false, true };
	boolean[] echar_8 = { false, false, false, true, false, false, true };
	boolean[] echar_9 = { false, false, true, false, true, true, true };
	boolean[][] echar_tbl = { echar_0, echar_1, echar_2, echar_3, echar_4,
			echar_5, echar_6, echar_7, echar_8, echar_9 };
	boolean[] char_start = { true, false, true };
	boolean[] char_mid = { false, true, false, true, false };
	boolean[] char_stop = { true };

	String ceochar_0 = "EEEOOO";
	String ceochar_1 = "EEOEOO";
	String ceochar_2 = "EEOOEO";
	String ceochar_3 = "EEOOOE";
	String ceochar_4 = "EOEEOO";
	String ceochar_5 = "EOOEEO";
	String ceochar_6 = "EOOOEE";
	String ceochar_7 = "EOEOEO";
	String ceochar_8 = "EOEOOE";
	String ceochar_9 = "EOOEOE";
	String[] ceochar_tbl = { ceochar_0, ceochar_1, ceochar_2, ceochar_3,
			ceochar_4, ceochar_5, ceochar_6, ceochar_7, ceochar_8, ceochar_9 };

	@Override
	public boolean make() {
		// TODO Auto-generated method stub
		this.clear();
		String str = strText.substring(1, 8);

		setDots(char_start);
		char c = str.charAt(6);
		String ceochar = ceochar_tbl[c - '0'];
		for (int i = 0; i < 6; ++i) {
			char ch = str.charAt(i);
			int idx = ch - '0';
			if ('E' == ceochar.charAt(i)) {
				boolean[] echar = echar_tbl[idx];
				setDots(echar);
			} else {
				boolean[] ochar = ochar_tbl[idx];
				setDots(ochar);
			}
		}
		setDots(char_mid);
		setDots(char_stop);
		return true;
	}
}
