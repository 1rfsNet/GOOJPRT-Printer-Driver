package com.lvrenyang.mydata;

import java.io.UnsupportedEncodingException;

public class LayoutUtils {

	public class ParaItems
	{
		public String Header = "小财神";
		public String Remark = "收款凭证";
		public String MerchantName = "老毕肥牛"; // 商户名称
		public String MerchantNo = "88888888"; // 商户号
		public String Cashier = "蔡万生"; // 收款员
		public String VoucherNo = "00008"; // 凭证号
		public String TotalPrice = "300.00元"; // 消费金额
		public String Discount = "85折"; // 折扣
		public String RealPrice = "255.00"; // 实收金额
		public String DateTime = "2015/3/31 7:59:28"; // 日期时间
		public String Other = "";
	}
	/**
	 * 没有考虑到很详细，请参考命令集进行排版。
	 * @param p
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public byte[] Layout(ParaItems p) throws UnsupportedEncodingException
	{
		byte[] CMD_RESET = {0x1b, 0x40};
		byte[] CMD_CENTER = {0x1b, 0x61, 0x1};
		byte[] CMD_LEFT = {0x1b, 0x61, 0x0};
		byte[] CMD_TWOTIMES = {0x1d, 0x21, 0x11};
		byte[] CMD_ONETIMES = {0x1d, 0x21, 0x00};
		byte[] CMD_NEWLINE = {0x0d, 0x0a};
		byte[] bHeader,bRemark,bMerchantName,bMerchantNo,bCashier,bVoucherNo,bTotalPrice,bDiscount,bRealPrice,bDateTime,bOther;
		
		bHeader = p.Header.getBytes("GBK");
		bRemark = p.Remark.getBytes("GBK");
		bMerchantName = p.MerchantName.getBytes("GBK");
		bMerchantNo = p.Remark.getBytes("GBK");
		bCashier = p.Remark.getBytes("GBK");
		bVoucherNo = p.Remark.getBytes("GBK");
		bTotalPrice = p.Remark.getBytes("GBK");
		bDiscount = p.Remark.getBytes("GBK");
		bRealPrice = p.Remark.getBytes("GBK");
		bDateTime = p.Remark.getBytes("GBK");
		bOther = p.Remark.getBytes("GBK");
		
		byte[] data = byteArraysToBytes(new byte[][]{CMD_RESET,
				CMD_CENTER,CMD_TWOTIMES,bHeader,CMD_NEWLINE,
				CMD_CENTER,CMD_ONETIMES,bRemark,CMD_NEWLINE,
				CMD_LEFT,
				"商户名称：".getBytes("GBK"),bMerchantName,CMD_NEWLINE,
				"商 户 号：".getBytes("GBK"),bMerchantNo,CMD_NEWLINE,
				"收 款 员：".getBytes("GBK"),bCashier,CMD_NEWLINE,
				"凭 证 号：".getBytes("GBK"),bVoucherNo,CMD_NEWLINE,
				"消费金额：".getBytes("GBK"),bTotalPrice,CMD_NEWLINE,
				"折　　扣：".getBytes("GBK"),bDiscount,CMD_NEWLINE,
				"实收金额：".getBytes("GBK"),bRealPrice,CMD_NEWLINE,
				"日期时间：".getBytes("GBK"),bDateTime,CMD_NEWLINE,
				"其　　他：".getBytes("GBK"),bOther,CMD_NEWLINE,
				CMD_NEWLINE,CMD_NEWLINE,CMD_NEWLINE,
				});
		return data;
	}
	
	/**
	 * 将多个字节数组按顺序合并
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] byteArraysToBytes(byte[][] data) {

		int length = 0;
		for (int i = 0; i < data.length; i++)
			length += data[i].length;
		byte[] send = new byte[length];
		int k = 0;
		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < data[i].length; j++)
				send[k++] = data[i][j];
		return send;
	}
}
