package com.lvrenyang.rwusb;

import com.lvrenyang.rwusb.PL2303Driver.TTYTermios;
import com.lvrenyang.rwusb.USBDriver.RTNCode;
import com.lvrenyang.rwusb.USBDriver.USBPort;
import com.lvrenyang.utils.FileUtils;
import com.lvrenyang.utils.TimeUtils;

import android.os.Looper;
import android.util.Log;

/**
 * 这只是单纯处理读数据的线程，如果需要执行工作，还需要另外的线程
 * 这里面启动心跳线程，构造函数的时候new，start的时候也start子线程，quit的时候也
 * 
 * @author Administrator
 * 
 */
public class USBRWThread extends Thread {

	private static final String TAG = "USBRWThread";

	private static volatile USBRWThread usbrwThread = null;

	private static Looper mLooper = null;
	private static boolean threadInitOK = false;

	private static PL2303Driver pl2303 = new PL2303Driver();
	private static USBPort port = null;
	private static TTYTermios serial = null;
	private static boolean isOpened = false;

	private USBRWThread() {
		threadInitOK = false;
	}

	public static USBRWThread InitInstant() {
		if (usbrwThread == null) {
			synchronized (USBRWThread.class) {
				if (usbrwThread == null) {
					usbrwThread = new USBRWThread();
				}
			}
		}
		return usbrwThread;
	}

	@Override
	public void start() {
		super.start();
		while (!threadInitOK)
			;
	}

	@Override
	public void run() {
		Looper.prepare();
		mLooper = Looper.myLooper();
		threadInitOK = true;
		Looper.loop();
	}

	public static boolean Open(USBPort port, TTYTermios serial) {
		boolean valid = false;
		try {
			if (pl2303.probe(port, PL2303Driver.id) == RTNCode.OK) {
				if (pl2303.attach(port) == RTNCode.OK) {
					if (pl2303.open(port, serial) == RTNCode.OK) {
						USBRWThread.port = port;
						USBRWThread.serial = serial;
						valid = true;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			valid = false;
		}

		// 如果成功了，则发起读命令

		if (valid) {
			TimeUtils.WaitMs(200);
			isOpened = true;
		} else {
			isOpened = false;
		}

		return valid;
	}

	public static void Close() {
		try {
			pl2303.close(port, serial);
			pl2303.release(port);
			pl2303.disconnect(port);
			port = null;
			serial = null;
			Log.v("USBRWThread Close", "Close Socket");
			FileUtils.DebugAddToFile("USBRWThread Close\r\n",
					FileUtils.sdcard_dump_txt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		isOpened = false;
	}

	public static boolean IsOpened() {
		return isOpened;
	}

	public static int Write(byte[] buffer, int offset, int count) {
		int cnt = 0;
		int curcnt = 0;
		try {
			while (cnt < count) {
				curcnt = pl2303.write(port, buffer, offset + cnt, count - cnt,
						2000);
				if (curcnt < 0) {
					throw new Exception("write error");
				} else {
					cnt += curcnt;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Close();
		}

		return cnt;
	}

	/**
	 * 
	 * @param buffer
	 * @param byteOffset
	 * @param byteCount
	 * @param timeout
	 * @return 返回实际读取的字节数
	 */
	public static synchronized int Read(byte[] buffer, int byteOffset,
			int byteCount, int timeout) {
		return pl2303.read(port, buffer, byteOffset, byteCount, timeout);
	}

	public static boolean Request(byte sendbuf[], int sendlen, int requestlen,
			byte recbuf[], Integer reclen, int timeout) {
		int Retry = 3;

		while ((Retry--) > 0) {
			Write(sendbuf, 0, sendlen);
			reclen = Read(recbuf, 0, requestlen, timeout);
			if (requestlen == reclen)
				return true;
		}
		return false;
	}

	public static synchronized void Quit() {
		try {
			if (null != mLooper) {
				mLooper.quit();
				mLooper = null;
			}
			usbrwThread = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
