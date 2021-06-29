package com.lvrenyang.labelitem;

import java.io.UnsupportedEncodingException;

import com.lvrenyang.label.Label1;
import com.lvrenyang.utils.DataUtils;

import android.util.Log;


/**
 * 不完整的类别，可以生成数据并打印，但是无法显示出来。
 * 
 * 
 * @author lry
 *
 */
public class ItemBarcodeV2 extends LabelItem{

	/**
	 * 
	 */ 
	private static final long serialVersionUID = -5084588910260365401L;
	
	private static final String TAG = "ItemBarcodeV2";
	
	public int startx = 0;
	public int starty = 0;
	public int barocdetype = 0;
	public int height = 48;
	public int unitwidth = 2;
	public int rotate = 0;

	public String codepage = "GBK";
	public String strText = "";
	
	public ItemBarcodeV2(int startx, int starty, int barcodeType, String text)
	{
		this.startx = startx;
		this.starty = starty;
		this.barocdetype = barcodeType;
		this.strText = text;
		this.type = LabelItemType.BARCODEV2;
	}
	
	@Override
	public void Write() {
		// TODO Auto-generated method stub
		byte[] strbytes = null;
		try {
			strbytes = strText.getBytes(codepage);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		byte[] str = DataUtils
				.byteArraysToBytes(new byte[][] { strbytes, { 0 } });

		Label1.DrawBarcode(startx, starty, barocdetype, height, unitwidth,
				rotate, str);
		
		Log.i(TAG, "" + startx + ", " + starty + ", " + barocdetype + ", "
				+ height + ", " + unitwidth + ", " + rotate + ", " + str);
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

}
