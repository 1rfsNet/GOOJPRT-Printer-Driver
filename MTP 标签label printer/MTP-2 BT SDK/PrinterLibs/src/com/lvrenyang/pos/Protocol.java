package com.lvrenyang.pos;

import android.util.Log;

import com.lvrenyang.rwbuf.ProtocolHandler;
import com.lvrenyang.utils.DataUtils;

/**
 * 协议包
 * 
 * @author Administrator
 * 
 */
public class Protocol {

	private static final String TAG = "Protocol";

	private static ProtocolHandler KCBuffer = new ProtocolHandler(0x1000);

	/**
	 * 
	 * @param cmd
	 *            0x20
	 * @param para
	 *            0x00
	 * @param sendlen
	 *            0x08
	 * @param psendbuf
	 *            "DEVICE??"
	 * @param reclen
	 * @param precbuf
	 * @param timeout
	 *            1000
	 * @return
	 */
	public static synchronized boolean ProtoPackage(int cmd, // {0x2e, 0x2e}
			int para, // {0x0, 0x4}
			int sendlen, // {0x100, 0x100}
			byte psendbuf[], // {buf0, buf1}
			int reclen[], // {0x0, 0x100} 返回的数据
			byte precbuf[], // {null, buf2} 返回的数据
			int timeout) {
		int j;
		int retry;
		long time;
		byte pcmdbuf[] = new byte[12 + sendlen];

		pcmdbuf[0] = (byte) (KCBuffer.ProtoHeaderOut >> 8);
		pcmdbuf[1] = (byte) (KCBuffer.ProtoHeaderOut);
		pcmdbuf[2] = (byte) (cmd);
		pcmdbuf[3] = (byte) (cmd >> 8);
		pcmdbuf[4] = (byte) (para);
		pcmdbuf[5] = (byte) (para >> 8);
		pcmdbuf[6] = (byte) (para >> 16);
		pcmdbuf[7] = (byte) (para >> 24);
		pcmdbuf[8] = (byte) (sendlen);
		pcmdbuf[9] = (byte) (sendlen >> 8);
		pcmdbuf[10] = 0;
		pcmdbuf[11] = 0;
		for (j = 0; j < 10; j++)
			pcmdbuf[10] ^= pcmdbuf[j];
		for (j = 0; j < sendlen; j++) {
			pcmdbuf[11] ^= psendbuf[j];
			pcmdbuf[12 + j] = psendbuf[j];
		}
		reclen[0] = 0;

		retry = 3;
		while (retry > 0) {
			retry--;

			/**
			 * 数据接收和协议处理归零
			 */
			KCBuffer.Count = 0;
			IO.ClrRec();

			if (!IO.IsOpened()) {
				Log.v(TAG, "Socket is null pointer");
				return false;
			}

			if (IO.Write(pcmdbuf, 0, 12 + sendlen) != 12 + sendlen) {
				Log.v(TAG, "Socket not connected");
				return false;
			}
			Log.v(TAG + " Send", DataUtils.bytesToStr(pcmdbuf, 0, 12)
					.toString());
			if (sendlen > 0)
				Log.v(TAG + " Send", DataUtils.bytesToStr(pcmdbuf, 12, sendlen)
						.toString());
			/* 准备接收数据 */
			time = System.currentTimeMillis();
			while (true) {
				if ((System.currentTimeMillis() - time) > timeout)
					break;

				KCBuffer.KcCmd = 0;
				KCBuffer.KcPara = 0;
				if (!IO.IsEmpty()) {
					byte ch = IO.GetByte();
					KCBuffer.HandleKcUartChar(ch);

					if ((KCBuffer.KcCmd == cmd) && (KCBuffer.KcPara == para)) {
						reclen[0] = (KCBuffer.Buffer[8] & 0xFF)
								+ (KCBuffer.Buffer[9] & 0xFF) * 0x100;
						DataUtils.copyBytes(KCBuffer.Buffer, 12, precbuf, 0,
								reclen[0]);

						Log.v(TAG, "recv: cmd=" + cmd + " para=" + para);
						Log.v(TAG + " Recv",
								DataUtils.bytesToStr(KCBuffer.Buffer, 0, 12)
										.toString());
						if (reclen[0] > 0)
							Log.v(TAG + " Recv",
									DataUtils.bytesToStr(KCBuffer.Buffer, 12,
											reclen[0]).toString());

						KCBuffer.KcCmd = 0;
						KCBuffer.KcPara = 0;
						return true;
					}
				}
			}
		}

		return false;
	}

	/* 通讯测试使用标准EPSON命令。1D 72 n，请参考Pos */
	/*
	 * public static synchronized boolean Test() {
	 * 
	 * String strTest = "DEVICE??"; int cmd = 0x20; int para = 0x00; byte[]
	 * psendbuf = strTest.getBytes(); int sendlen = psendbuf.length; int[]
	 * reclen = new int[1]; byte[] precbuf = new byte[512]; int timeout = 1000;
	 * 
	 * return ProtoPackage(cmd, para, sendlen, psendbuf, reclen, precbuf,
	 * timeout);
	 * 
	 * //return true; }
	 */
}
