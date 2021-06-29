package com.lvrenyang.pos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import com.lvrenyang.encryp.DES2;
import com.lvrenyang.pos.Cmd.ESCCmd;
import com.lvrenyang.rwbt.BTRWThread;
import com.lvrenyang.utils.DataUtils;
import com.lvrenyang.utils.FileUtils;

public class Pos {

	/**
	 * 打印图片的时候，如果端口为蓝牙，那么这个设置值可以设置图片是否使用软件流控。
	 * 
	 */
	public static boolean pictureUseFlowControl = true;
	public static boolean pictureWriteWithRTQuery = true;
	private static final String TAG = "Pos";
	public static final String external_sd_dump_bin = "sdcard" + File.separator
			+ "dump.bin";

	/**
	 * 使用软件流控，当收到0x13则停止发送，当收到0x11，则继续发送。
	 * 
	 * 只有蓝牙才会使用软件流控。对于网口和USB，该方法无效。
	 * 
	 * @param buffer
	 * @param offset
	 * @param count
	 * @param percount
	 * @return
	 */
	public static int POS_Write_FlowControl(byte[] buffer, int offset,
			int count, int percount) {

		FileUtils.DebugAddToFile(buffer, offset, count, external_sd_dump_bin);

		int idx = 0;
		int curcount = 0;
		byte ch;
		boolean canSend = true;
		BTRWThread.PauseRead();
		DataInputStream is = BTRWThread.GetInputStream();
		DataOutputStream os = BTRWThread.GetOutputStream();
		Log.i(TAG, "POS_Write_FlowControl");
		try {

			if (null != is) {
				is.skipBytes(is.available());
			}

			while (idx < count) {

				if ((null == is) || (null == os))
					break;

				if (is.available() > 0) {

					ch = is.readByte();

					Log.w(TAG, "Receive char: " + ch);

					if (0x13 == ch)
						canSend = false;
					else if (0x11 == ch)
						canSend = true;
					else
						continue;
				}

				if (canSend) // 如果没有收到任何字节，继续发送
				{
					if (count - idx > percount)
						curcount = percount;
					else
						curcount = count - idx;

					os.write(buffer, offset + idx, curcount);
					os.flush();
					idx += curcount;
				}
				Log.i(TAG, "idx:" + idx);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		BTRWThread.ResumeRead();
		return idx;
	}

	private static synchronized int POS_Write_WithRTQuery(byte[] buffer,
			int offset, int count, int percount) {
		int idx = 0;
		int curcount = 0;
		byte[] precbuf = new byte[1];
		int timeout = 5000;
		if (POS_RTQueryStatus(precbuf, timeout)) {

			while (idx < count) {
				if (count - idx > percount)
					curcount = percount;
				else
					curcount = count - idx;

				IO.Write(buffer, offset + idx, curcount);

				if (!POS_RTQueryStatus(precbuf, timeout))
					break;

				idx += curcount;
			}
		}
		return idx;
	}

	private static synchronized int POS_Write_WithQuery(byte[] buffer,
			int offset, int count, int percount) {
		int idx = 0;
		int curcount = 0;
		byte[] precbuf = new byte[1];
		int timeout = 5000;
		if (POS_QueryStatus(precbuf, timeout)) {

			while (idx < count) {
				if (count - idx > percount)
					curcount = percount;
				else
					curcount = count - idx;

				IO.Write(buffer, offset + idx, curcount);

				if (!POS_QueryStatus(precbuf, timeout))
					break;

				idx += curcount;
			}
		}
		return idx;
	}

	public static void saveDataToBin(String fileName, byte[] data) {
		File f = new File(Environment.getExternalStorageDirectory().getPath(),
				fileName);
		try {
			f.createNewFile();
		} catch (IOException e) {
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
			fOut.write(data, 0, data.length);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	// nWidth必须为8的倍数,这个只需在上层控制即可
	// 之所以弄成一维数组，是因为一维数组速度会快一点
	private static int[] p0 = { 0, 0x80 };
	private static int[] p1 = { 0, 0x40 };
	private static int[] p2 = { 0, 0x20 };
	private static int[] p3 = { 0, 0x10 };
	private static int[] p4 = { 0, 0x08 };
	private static int[] p5 = { 0, 0x04 };
	private static int[] p6 = { 0, 0x02 };

	// 1行作为1个图片，这样打印不会乱
	@SuppressWarnings("unused")
	private static byte[] pixToCmd(byte[] src, int nWidth, int nMode) {
		// nWidth = 384; nHeight = 582;
		int nHeight = src.length / nWidth;
		byte[] data = new byte[8 + (src.length / 8)];
		data[0] = 0x1d;
		data[1] = 0x76;
		data[2] = 0x30;
		data[3] = (byte) (nMode & 0x01);
		data[4] = (byte) ((nWidth / 8) % 0x100);// (xl+xh*256)*8 = nWidth
		data[5] = (byte) ((nWidth / 8) / 0x100);
		data[6] = (byte) ((nHeight) % 0x100);// (yl+yh*256) = nHeight
		data[7] = (byte) ((nHeight) / 0x100);
		int k = 0;
		for (int i = 8; i < data.length; i++) {
			// 不行，没有加权
			data[i] = (byte) (p0[src[k]] + p1[src[k + 1]] + p2[src[k + 2]]
					+ p3[src[k + 3]] + p4[src[k + 4]] + p5[src[k + 5]]
					+ p6[src[k + 6]] + src[k + 7]);
			k = k + 8;
		}
		return data;

	}

	private static byte[] eachLinePixToCmd(byte[] src, int nWidth, int nMode) {
		int nHeight = src.length / nWidth;
		int nBytesPerLine = nWidth / 8;
		byte[] data = new byte[nHeight * (8 + nBytesPerLine)];
		int offset = 0;
		int k = 0;
		for (int i = 0; i < nHeight; i++) {
			offset = i * (8 + nBytesPerLine);
			data[offset + 0] = 0x1d;
			data[offset + 1] = 0x76;
			data[offset + 2] = 0x30;
			data[offset + 3] = (byte) (nMode & 0x01);
			data[offset + 4] = (byte) (nBytesPerLine % 0x100);
			data[offset + 5] = (byte) (nBytesPerLine / 0x100);
			data[offset + 6] = 0x01;
			data[offset + 7] = 0x00;
			for (int j = 0; j < nBytesPerLine; j++) {
				data[offset + 8 + j] = (byte) (p0[src[k]] + p1[src[k + 1]]
						+ p2[src[k + 2]] + p3[src[k + 3]] + p4[src[k + 4]]
						+ p5[src[k + 5]] + p6[src[k + 6]] + src[k + 7]);
				k = k + 8;
			}
		}

		return data;
	}

	/**
	 * 将ARGB图转换为二值图，0代表黑，1代表白
	 * 
	 * @param mBitmap
	 * @return
	 */
	public static byte[] bitmapToBWPix(Bitmap mBitmap) {

		int[] pixels = new int[mBitmap.getWidth() * mBitmap.getHeight()];
		byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight()];

		mBitmap.getPixels(pixels, 0, mBitmap.getWidth(), 0, 0,
				mBitmap.getWidth(), mBitmap.getHeight());

		// for the toGrayscale, we need to select a red or green or blue color
		ImageProcessing.format_K_dither16x16(pixels, mBitmap.getWidth(),
				mBitmap.getHeight(), data);

		return data;
	}

	@SuppressWarnings("unused")
	private static void saveDataToBin(byte[] data) {
		File f = new File(Environment.getExternalStorageDirectory().getPath(),
				"Btatotest.bin");
		try {
			f.createNewFile();
		} catch (IOException e) {
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
			fOut.write(data, 0, data.length);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	public static void saveMyBitmap(Bitmap mBitmap, String name) {
		File f = new File(Environment.getExternalStorageDirectory().getPath(),
				name);
		try {
			f.createNewFile();
		} catch (IOException e) {
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

	}

	private static byte[] pixToRaster(byte[] src) {

		byte[] data = new byte[src.length / 8];
		int k = 0;
		for (int i = 0; i < data.length; i++) {
			// 不行，没有加权
			data[i] = (byte) (p0[src[k]] + p1[src[k + 1]] + p2[src[k + 2]]
					+ p3[src[k + 3]] + p4[src[k + 4]] + p5[src[k + 5]]
					+ p6[src[k + 6]] + src[k + 7]);
			k = k + 8;
		}
		return data;
	}

	public static byte[] genRasterData(Bitmap mBitmap) {

		Bitmap grayBitmap = ImageProcessing.toGrayscale(mBitmap);
		// byte[] dithered = bitmapToBWPix(grayBitmap); // 抖动算法
		byte[] dithered = thresholdToBWPic(grayBitmap); // 阀值算法
		byte[] data = pixToRaster(dithered);
		return data;

	}

	public static void POS_PrintPicture(Bitmap mBitmap, int nWidth, int nMode) {

		// 先转黑白，再调用函数缩放位图
		// 不转黑白
		int width = ((nWidth + 7) / 8) * 8;
		int height = mBitmap.getHeight() * width / mBitmap.getWidth();
		height = ((height + 7) / 8) * 8;
		Bitmap rszBitmap = ImageProcessing.resizeImage(mBitmap, width, height);
		Bitmap grayBitmap = ImageProcessing.toGrayscale(rszBitmap);
		byte[] dithered = bitmapToBWPix(grayBitmap);

		byte[] data = eachLinePixToCmd(dithered, width, nMode);

		if (IO.PORT_BT == IO.GetCurPort() && pictureUseFlowControl) {
			// 基本超时1000ms 40k的数据4000ms超时
			POS_Write_FlowControl(data, 0, data.length, 64);
		} else if (IO.PORT_USB == IO.GetCurPort()) {
			IO.Write(data, 0, data.length);
		} else {
			// 当width = 384时，一行数据占48个字节，加上8个字节头，总共width/8+8个字节。
			int nBytesPerLine = width / 8 + 8;
			int nLinesPerTest = 4;
			if (IO.PORT_BT == IO.GetCurPort())
				nLinesPerTest = 5;
			else if (IO.PORT_NET == IO.GetCurPort())
				nLinesPerTest = 5;
			else if (IO.PORT_USB == IO.GetCurPort())
				nLinesPerTest = 60;

			if (pictureWriteWithRTQuery)
				POS_Write_WithRTQuery(data, 0, data.length, nBytesPerLine
						* nLinesPerTest);
			else
				POS_Write_WithQuery(data, 0, data.length, nBytesPerLine
						* nLinesPerTest);
		}
	}

	/**
	 * 这个打印灰度图片的算法，会使用平均灰度值作为阀值，将灰度图片二值化。
	 * 
	 * @param mBitmap
	 *            灰度图
	 * @param nWidth
	 * @param nMode
	 */
	public static void POS_PrintBWPic(Bitmap mBitmap, int nWidth, int nMode) {
		// 先转黑白，再调用函数缩放位图
		// 不转黑白
		int width = ((nWidth + 7) / 8) * 8;
		int height = mBitmap.getHeight() * width / mBitmap.getWidth();
		height = ((height + 7) / 8) * 8;

		saveMyBitmap(mBitmap, "origin.png");

		Bitmap rszBitmap = mBitmap;
		if (mBitmap.getWidth() != width)
			rszBitmap = ImageProcessing.resizeImage(mBitmap, width, height);
		saveMyBitmap(rszBitmap, "rsz.png");

		Bitmap grayBitmap = ImageProcessing.toGrayscale(rszBitmap);
		saveMyBitmap(grayBitmap, "gray.png");
		byte[] dithered = thresholdToBWPic(grayBitmap);

		// 将转换过的图片，保存到文件，以下图片，仅作调试使用。
		overWriteBitmap(grayBitmap, dithered);
		saveMyBitmap(grayBitmap, "dithered.png");

		byte[] data = eachLinePixToCmd(dithered, width, nMode);

		// 将data保存到文件，使用电脑打印
		// FileUtils.AddToFile(data, 0, data.length, FileUtils.dump_bin);

		if (IO.PORT_BT == IO.GetCurPort() && pictureUseFlowControl) {
			// 基本超时1000ms 40k的数据4000ms超时
			POS_Write_FlowControl(data, 0, data.length, 64);
		} else if (IO.PORT_USB == IO.GetCurPort()) {
			IO.Write(data, 0, data.length);
		} else {
			// 当width = 384时，一行数据占48个字节，加上8个字节头，总共width/8+8个字节。
			int nBytesPerLine = width / 8 + 8;
			int nLinesPerTest = 1;
			if (IO.PORT_BT == IO.GetCurPort())
				nLinesPerTest = 5;
			else if (IO.PORT_NET == IO.GetCurPort())
				nLinesPerTest = 5;
			else if (IO.PORT_USB == IO.GetCurPort())
				nLinesPerTest = 60;

			if (pictureWriteWithRTQuery)
				POS_Write_WithRTQuery(data, 0, data.length, nBytesPerLine
						* nLinesPerTest);
			else
				POS_Write_WithQuery(data, 0, data.length, nBytesPerLine
						* nLinesPerTest);
		}

	}

	private static byte[] thresholdToBWPic(Bitmap mBitmap) {
		int[] pixels = new int[mBitmap.getWidth() * mBitmap.getHeight()];
		byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight()];

		mBitmap.getPixels(pixels, 0, mBitmap.getWidth(), 0, 0,
				mBitmap.getWidth(), mBitmap.getHeight());

		// for the toGrayscale, we need to select a red or green or blue color
		ImageProcessing.format_K_threshold(pixels, mBitmap.getWidth(),
				mBitmap.getHeight(), data);

		return data;
	}

	private static void overWriteBitmap(Bitmap mBitmap, byte[] dithered) {
		int ysize = mBitmap.getHeight();
		int xsize = mBitmap.getWidth();

		int k = 0;
		for (int i = 0; i < ysize; i++) {

			for (int j = 0; j < xsize; j++) {

				if (dithered[k] == 0) {
					mBitmap.setPixel(j, i, Color.WHITE);
				} else {
					mBitmap.setPixel(j, i, Color.BLACK);
				}
				k++;
			}
		}
	}

	public static Bitmap getBWedBitmap(Bitmap mBitmap, int nWidth) {
		// 先转黑白，再调用函数缩放位图
		// 不转黑白
		int width = ((nWidth + 7) / 8) * 8;
		int height = mBitmap.getHeight() * width / mBitmap.getWidth();
		height = ((height + 7) / 8) * 8;

		Bitmap rszBitmap = mBitmap;
		if (mBitmap.getWidth() != width)
			rszBitmap = ImageProcessing.resizeImage(mBitmap, width, height);

		Bitmap grayBitmap = ImageProcessing.toGrayscale(rszBitmap);

		byte[] dithered = thresholdToBWPic(grayBitmap);
		// byte[] dithered = bitmapToBWPix(grayBitmap);

		// 将转换过的图片，保存到文件，以下图片，仅作调试使用。
		overWriteBitmap(grayBitmap, dithered);

		return grayBitmap;
	}

	// nFontType 0 标准 1 压缩 其他不指定
	public static void POS_S_TextOut(String pszString, String encoding,
			int nOrgx, int nWidthTimes, int nHeightTimes, int nFontType,
			int nFontStyle) {
		if (nOrgx > 65535 | nOrgx < 0 | nWidthTimes > 7 | nWidthTimes < 0
				| nHeightTimes > 7 | nHeightTimes < 0 | nFontType < 0
				| nFontType > 4 | (pszString.length() == 0))
			return;

		Cmd.ESCCmd.ESC_dollors_nL_nH[2] = (byte) (nOrgx % 0x100);
		Cmd.ESCCmd.ESC_dollors_nL_nH[3] = (byte) (nOrgx / 0x100);

		byte[] intToWidth = { 0x00, 0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70 };
		byte[] intToHeight = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };
		Cmd.ESCCmd.GS_exclamationmark_n[2] = (byte) (intToWidth[nWidthTimes] + intToHeight[nHeightTimes]);

		byte[] tmp_ESC_M_n = Cmd.ESCCmd.ESC_M_n;
		if ((nFontType == 0) || (nFontType == 1))
			tmp_ESC_M_n[2] = (byte) nFontType;
		else
			tmp_ESC_M_n = new byte[0];

		// 字体风格
		// 暂不支持平滑处理
		Cmd.ESCCmd.GS_E_n[2] = (byte) ((nFontStyle >> 3) & 0x01);

		Cmd.ESCCmd.ESC_line_n[2] = (byte) ((nFontStyle >> 7) & 0x03);
		Cmd.ESCCmd.FS_line_n[2] = (byte) ((nFontStyle >> 7) & 0x03);

		Cmd.ESCCmd.ESC_lbracket_n[2] = (byte) ((nFontStyle >> 9) & 0x01);

		Cmd.ESCCmd.GS_B_n[2] = (byte) ((nFontStyle >> 10) & 0x01);

		Cmd.ESCCmd.ESC_V_n[2] = (byte) ((nFontStyle >> 12) & 0x01);

		byte[] pbString = null;
		try {
			pbString = pszString.getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			return;
		}

		byte[] data = DataUtils.byteArraysToBytes(new byte[][] {
				Cmd.ESCCmd.ESC_dollors_nL_nH, Cmd.ESCCmd.GS_exclamationmark_n,
				tmp_ESC_M_n, Cmd.ESCCmd.GS_E_n, Cmd.ESCCmd.ESC_line_n,
				Cmd.ESCCmd.FS_line_n, Cmd.ESCCmd.ESC_lbracket_n,
				Cmd.ESCCmd.GS_B_n, Cmd.ESCCmd.ESC_V_n, pbString });

		IO.Write(data, 0, data.length);

	}

	public static void POS_FeedLine() {
		byte[] data = DataUtils.byteArraysToBytes(new byte[][] { Cmd.ESCCmd.CR,
				Cmd.ESCCmd.LF });
		// byte[] data = Cmd.ESCCmd.LF;
		IO.Write(data, 0, data.length);
	}

	public static void POS_S_Align(int align) {
		if (align < 0 || align > 2)
			return;
		byte[] data = Cmd.ESCCmd.ESC_a_n;
		data[2] = (byte) align;
		IO.Write(data, 0, data.length);
	}

	public static void POS_SetLineHeight(int nHeight) {
		if (nHeight < 0 || nHeight > 255)
			return;
		byte[] data = Cmd.ESCCmd.ESC_3_n;
		data[2] = (byte) nHeight;
		IO.Write(data, 0, data.length);
	}

	public static void POS_S_SetBarcode(String strCodedata, int nOrgx,
			int nType, int nWidthX, int nHeight, int nHriFontType,
			int nHriFontPosition) {
		if (nOrgx < 0 | nOrgx > 65535 | nType < 0x41 | nType > 0x49
				| nWidthX < 2 | nWidthX > 6 | nHeight < 1 | nHeight > 255)
			return;

		byte[] bCodeData = null;
		try {
			bCodeData = strCodedata.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			return;
		}
		;

		Cmd.ESCCmd.ESC_dollors_nL_nH[2] = (byte) (nOrgx % 0x100);
		Cmd.ESCCmd.ESC_dollors_nL_nH[3] = (byte) (nOrgx / 0x100);
		Cmd.ESCCmd.GS_w_n[2] = (byte) nWidthX;
		Cmd.ESCCmd.GS_h_n[2] = (byte) nHeight;
		Cmd.ESCCmd.GS_f_n[2] = (byte) (nHriFontType & 0x01);
		Cmd.ESCCmd.GS_H_n[2] = (byte) (nHriFontPosition & 0x03);
		Cmd.ESCCmd.GS_k_m_n_[2] = (byte) nType;
		Cmd.ESCCmd.GS_k_m_n_[3] = (byte) bCodeData.length;

		byte[] data = DataUtils.byteArraysToBytes(new byte[][] {
				Cmd.ESCCmd.ESC_dollors_nL_nH, Cmd.ESCCmd.GS_w_n,
				Cmd.ESCCmd.GS_h_n, Cmd.ESCCmd.GS_f_n, Cmd.ESCCmd.GS_H_n,
				Cmd.ESCCmd.GS_k_m_n_, bCodeData });
		IO.Write(data, 0, data.length);

	}

	public static void POS_S_SetQRcode(String strCodedata, int nWidthX,
			int nErrorCorrectionLevel) {

		if (nWidthX < 2 | nWidthX > 6 | nErrorCorrectionLevel < 1
				| nErrorCorrectionLevel > 4)
			return;

		byte[] bCodeData = null;
		try {
			bCodeData = strCodedata.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			return;
		}
		;

		Cmd.ESCCmd.GS_w_n[2] = (byte) nWidthX;
		Cmd.ESCCmd.GS_k_m_v_r_nL_nH[4] = (byte) nErrorCorrectionLevel;
		Cmd.ESCCmd.GS_k_m_v_r_nL_nH[5] = (byte) (bCodeData.length & 0xff);
		Cmd.ESCCmd.GS_k_m_v_r_nL_nH[6] = (byte) ((bCodeData.length & 0xff00) >> 8);

		byte[] data = DataUtils.byteArraysToBytes(new byte[][] {
				Cmd.ESCCmd.GS_w_n, Cmd.ESCCmd.GS_k_m_v_r_nL_nH, bCodeData });
		IO.Write(data, 0, data.length);

	}

	/**
	 * 相比POS_S_SetQRCode,多了一个控制二维码的大小参数。
	 * 
	 * @param strCodedata
	 * @param nWidthX
	 * @param nVersion
	 * @param nErrorCorrectionLevel
	 */
	public static void POS_S_SetQRcodeV2(String strCodedata, int nWidthX,
			int nVersion, int nErrorCorrectionLevel) {

		if (nWidthX < 2 | nWidthX > 6 | nErrorCorrectionLevel < 1
				| nErrorCorrectionLevel > 4)
			return;

		if (nVersion < 0 || nVersion > 16)
			return;

		byte[] bCodeData = null;
		try {
			bCodeData = strCodedata.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			return;
		}
		;

		Cmd.ESCCmd.GS_w_n[2] = (byte) nWidthX;
		Cmd.ESCCmd.GS_k_m_v_r_nL_nH[3] = (byte) nVersion;
		Cmd.ESCCmd.GS_k_m_v_r_nL_nH[4] = (byte) nErrorCorrectionLevel;
		Cmd.ESCCmd.GS_k_m_v_r_nL_nH[5] = (byte) (bCodeData.length & 0xff);
		Cmd.ESCCmd.GS_k_m_v_r_nL_nH[6] = (byte) ((bCodeData.length & 0xff00) >> 8);

		byte[] data = DataUtils.byteArraysToBytes(new byte[][] {
				Cmd.ESCCmd.GS_w_n, Cmd.ESCCmd.GS_k_m_v_r_nL_nH, bCodeData });
		IO.Write(data, 0, data.length);

	}

	public static void POS_EPSON_SetQRCode(String strCodedata, int nWidthX,
			int nErrorCorrectionLevel) {
		if (nWidthX < 2 | nWidthX > 6 | nErrorCorrectionLevel < 1
				| nErrorCorrectionLevel > 4)
			return;

		byte[] bCodeData = null;
		try {
			bCodeData = strCodedata.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			return;
		}
		;

		Cmd.ESCCmd.GS_w_n[2] = (byte) nWidthX;
		Cmd.ESCCmd.GS_leftbracket_k_pL_pH_cn_69_n[7] = (byte) (47 + nErrorCorrectionLevel);
		Cmd.ESCCmd.GS_leftbracket_k_pL_pH_cn_80_m__d1dk[3] = (byte) ((bCodeData.length + 3) & 0xff);
		Cmd.ESCCmd.GS_leftbracket_k_pL_pH_cn_80_m__d1dk[4] = (byte) (((bCodeData.length + 3) & 0xff00) >> 8);

		byte[] data = DataUtils.byteArraysToBytes(new byte[][] {
				Cmd.ESCCmd.GS_w_n, Cmd.ESCCmd.GS_leftbracket_k_pL_pH_cn_67_n,
				Cmd.ESCCmd.GS_leftbracket_k_pL_pH_cn_69_n,
				Cmd.ESCCmd.GS_leftbracket_k_pL_pH_cn_80_m__d1dk, bCodeData,
				Cmd.ESCCmd.GS_leftbracket_k_pL_pH_cn_fn_m });
		IO.Write(data, 0, data.length);

	}

	public static void POS_EPSON_SetQRCodeV2(String strCodedata, int nVersion,
			int nErrorCorrectionLevel) {
		if (nVersion < 0 || nVersion > 16)
			return;

		byte[] bCodeData = null;
		try {
			bCodeData = strCodedata.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			return;
		}

		Cmd.ESCCmd.GS_leftbracket_k_pL_pH_cn_67_n[7] = (byte) nVersion;
		Cmd.ESCCmd.GS_leftbracket_k_pL_pH_cn_69_n[7] = (byte) (47 + nErrorCorrectionLevel);
		Cmd.ESCCmd.GS_leftbracket_k_pL_pH_cn_80_m__d1dk[3] = (byte) ((bCodeData.length + 3) & 0xff);
		Cmd.ESCCmd.GS_leftbracket_k_pL_pH_cn_80_m__d1dk[4] = (byte) (((bCodeData.length + 3) & 0xff00) >> 8);

		byte[] data = DataUtils.byteArraysToBytes(new byte[][] {
				Cmd.ESCCmd.GS_leftbracket_k_pL_pH_cn_67_n,
				Cmd.ESCCmd.GS_leftbracket_k_pL_pH_cn_69_n,
				Cmd.ESCCmd.GS_leftbracket_k_pL_pH_cn_80_m__d1dk, bCodeData,
				Cmd.ESCCmd.GS_leftbracket_k_pL_pH_cn_fn_m });
		IO.Write(data, 0, data.length);

	}

	public static void POS_SetKey(byte[] key) {
		byte[] data = Cmd.ESCCmd.DES_SETKEY;
		for (int i = 0; i < key.length; i++) {
			data[i + 5] = key[i];
		}
		// 设置DES密钥，打印机不会返回，需要发送命令设置
		IO.Write(data, 0, data.length);
	}

	public static boolean POS_CheckKey(byte[] key, byte[] random) {
		boolean result = false;
		final int HeaderSize = 5;
		byte[] recHeader = new byte[HeaderSize];
		byte[] recData = null;
		int rec = 0;
		int recDataLen = 0;
		byte[] randomlen = new byte[2];
		randomlen[0] = (byte) (random.length & 0xff);
		randomlen[1] = (byte) ((random.length >> 8) & 0xff);
		byte[] data = DataUtils.byteArraysToBytes(new byte[][] {
				Cmd.ESCCmd.DES_ENCRYPT, randomlen, random });
		IO.Write(data, 0, data.length);
		rec = IO.Read(recHeader, 0, HeaderSize, 1000);
		if (rec != HeaderSize)
			return false;
		recDataLen = (recHeader[3] & 0xff) + ((recHeader[4] << 8) & 0xff);
		recData = new byte[recDataLen];
		rec = IO.Read(recData, 0, recDataLen, 1000);
		if (rec != recDataLen)
			return false;

		byte[] encrypted = recData;
		byte[] decrypted = new byte[encrypted.length + 1];
		/**
		 * 对数据进行解密
		 */
		DES2 des2 = new DES2();
		// 初始化密钥
		des2.yxyDES2_InitializeKey(key);
		des2.yxyDES2_DecryptAnyLength(encrypted, decrypted, encrypted.length);
		result = DataUtils.bytesEquals(random, 0, decrypted, 0, random.length);
		if (!result) {
			Log.v(TAG + " random", DataUtils.bytesToStr(random));
			Log.v(TAG + " decryp", DataUtils.bytesToStr(decrypted));
		}
		return result;
	}

	/**
	 * 复位打印机
	 */
	public static void POS_Reset() {
		byte[] data = Cmd.ESCCmd.ESC_ALT;
		IO.Write(data, 0, data.length);
	}

	/**
	 * 设置移动单位
	 * 
	 * @param nHorizontalMU
	 * @param nVerticalMU
	 */
	public static void POS_SetMotionUnit(int nHorizontalMU, int nVerticalMU) {
		if (nHorizontalMU < 0 || nHorizontalMU > 255 || nVerticalMU < 0
				|| nVerticalMU > 255)
			return;

		byte[] data = Cmd.ESCCmd.GS_P_x_y;
		data[2] = (byte) nHorizontalMU;
		data[3] = (byte) nVerticalMU;
		IO.Write(data, 0, data.length);
	}

	/**
	 * 设置字符集和代码页
	 * 
	 * @param nCharSet
	 * @param nCodePage
	 */
	public static void POS_SetCharSetAndCodePage(int nCharSet, int nCodePage) {
		if (nCharSet < 0 | nCharSet > 15 | nCodePage < 0 | nCodePage > 19
				| (nCodePage > 10 & nCodePage < 16))
			return;

		Cmd.ESCCmd.ESC_R_n[2] = (byte) nCharSet;
		Cmd.ESCCmd.ESC_t_n[2] = (byte) nCodePage;
		byte[] data = DataUtils.byteArraysToBytes(new byte[][] {
				ESCCmd.ESC_R_n, ESCCmd.ESC_t_n });
		IO.Write(data, 0, data.length);
	}

	/**
	 * 设置字符右间距
	 * 
	 * @param nDistance
	 */
	public static void POS_SetRightSpacing(int nDistance) {
		if (nDistance < 0 | nDistance > 255)
			return;

		Cmd.ESCCmd.ESC_SP_n[2] = (byte) nDistance;
		byte[] data = Cmd.ESCCmd.ESC_SP_n;
		IO.Write(data, 0, data.length);
	}

	/**
	 * 设置打印区域宽度
	 * 
	 * @param nWidth
	 */
	public static void POS_S_SetAreaWidth(int nWidth) {
		if (nWidth < 0 | nWidth > 65535)
			return;

		byte nL = (byte) (nWidth % 0x100);
		byte nH = (byte) (nWidth / 0x100);
		Cmd.ESCCmd.GS_W_nL_nH[2] = nL;
		Cmd.ESCCmd.GS_W_nL_nH[3] = nH;
		byte[] data = Cmd.ESCCmd.GS_W_nL_nH;
		IO.Write(data, 0, data.length);
	}

	public static void POS_FillZero(int nCount) {
		byte[] data = new byte[nCount];
		IO.Write(data, 0, data.length);
	}

	/**
	 * 使用1D 72 01这个命令，获取打印机状态。
	 * 
	 * @param precbuf
	 *            长度为1的字节数组，存储返回的状态。
	 * @param timeout
	 * @return
	 */
	public static boolean POS_QueryStatus(byte precbuf[], int timeout) {

		int retry;
		byte pcmdbuf[] = { 0x1D, 0x72, 0x01 };

		retry = 3;
		while (retry > 0) {
			retry--;

			/**
			 * 数据接收归零
			 */
			IO.ClrRec();
			IO.Write(pcmdbuf, 0, pcmdbuf.length);
			if (IO.Read(precbuf, 0, 1, timeout) == 1)
				return true;
		}

		return false;
	}

	public static boolean POS_RTQueryStatus(byte precbuf[], int timeout) {

		int retry;
		byte pcmdbuf[] = { 0x10, 0x04, 0x01 };

		retry = 3;
		while (retry > 0) {
			retry--;

			/**
			 * 数据接收归零
			 */
			IO.ClrRec();
			IO.Write(pcmdbuf, 0, pcmdbuf.length);
			if (IO.Read(precbuf, 0, 1, timeout) == 1)
				return true;
		}

		return false;
	}

	/**
	 * 只对特定的嵌入式蓝牙打印机有效
	 * 
	 * @param buffer
	 * @param offset
	 * @param count
	 */
	public static boolean EMBEDDED_WriteToUart(byte[] buffer, int offset,
			int count) {

		if (IO.PORT_BT == IO.GetCurPort()) {
			byte[] header = { 0x1F, 'R', 0x00, 0x00 };
			header[2] = (byte) ((count >> 8) & 0xFF);
			header[3] = (byte) (count & 0xff);

			byte[] data = new byte[header.length + count];
			DataUtils.copyBytes(header, 0, data, 0, header.length);
			DataUtils.copyBytes(buffer, offset, data, header.length, count);
			if (IO.Write(data, 0, data.length) == data.length)
				return true;
		}

		return false;
	}

}
