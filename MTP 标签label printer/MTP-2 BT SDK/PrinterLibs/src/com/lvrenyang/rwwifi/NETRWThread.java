package com.lvrenyang.rwwifi;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import com.lvrenyang.callback.RecvCallBack;
import com.lvrenyang.rwbuf.RxBuffer;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * 这只是单纯处理读数据的线程，如果需要执行工作，还需要另外的线程
 * 这里面启动心跳线程，构造函数的时候new，start的时候也start子线程，quit的时候也
 * 
 * @author Administrator
 * 
 */
public class NETRWThread extends Thread {

	//private static final Object SLOCK = new Object(); // 读写互斥锁，内部使用。
	private static volatile NETRWThread netRWThread = null;

	private static final int NETRWHANDLER_READ = 1000;

	private static Handler netrwHandler = null;
	private static Looper mLooper = null;
	private static boolean threadInitOK = false;

	private static Socket s = null;
	public static DataInputStream is = null;
	public static DataOutputStream os = null;
	private static boolean isOpened = false;

	private static RecvCallBack callBack = null;
	private static final Object NULLLOCK = new Object();
	public static RxBuffer NETRXBuffer = new RxBuffer(0x1000);

	private NETRWThread() {
		threadInitOK = false;
	}

	public static NETRWThread InitInstant() {
		if (netRWThread == null) {
			synchronized (NETRWThread.class) {
				if (netRWThread == null) {
					netRWThread = new NETRWThread();
				}
			}
		}
		return netRWThread;
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
		netrwHandler = new NETRWHandler();
		threadInitOK = true;
		Looper.loop();
	}

	private static class NETRWHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NETRWHANDLER_READ: {
				byte[] buffer = new byte[0x1000];
				int rec = 0;

				while (true) {

					try {
						rec = ReadIsAvaliable(buffer, buffer.length);
						if (rec > 0) {
							for (int i = 0; i < rec; i++)
								NETRXBuffer.PutByte(buffer[i]);
							OnRecv(buffer, 0, rec);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}

					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
				}

				Close();
				break;
			}

			}
		}
	}

	public static boolean Open(String IPAddress, int PortNumber) {
		boolean result = false;
		/*synchronized (SLOCK)*/ {
			result = _Open(IPAddress, PortNumber);
		}
		return result;
	}

	public static boolean _Open(String IPAddress, int PortNumber) {
		boolean valid = false;

		try {

			SocketAddress socketAddress = new InetSocketAddress(IPAddress,
					PortNumber);
			Socket socket = new Socket();
			socket.connect(socketAddress, 10000);
			s = socket;

			// s = new Socket(IPAddress, PortNumber);
			s.setKeepAlive(true);
			os = new DataOutputStream(s.getOutputStream());
			is = new DataInputStream(s.getInputStream());
			valid = true;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			valid = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			valid = false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			valid = false;
		}

		// 如果成功了，则发起读命令
		if (valid) {
			isOpened = true;
			Message msg = netrwHandler.obtainMessage(NETRWHANDLER_READ);
			netrwHandler.sendMessage(msg);
		} else {
			isOpened = false;
			s = null;
		}

		return valid;
	}

	public static void Close() {
		/*synchronized (SLOCK)*/ {
			_Close();
		}
	}

	public static void _Close() {
		try {
			if (is != null) {
				is.close();
				is = null;
			}
			if (os != null) {
				os.close();
				os = null;
			}
			if (null != s) {
				s.close();
				s = null;
			}
			Log.v("NETRWThread Close", "Close Socket");
		} catch (Exception e) {
			e.printStackTrace();
		}
		isOpened = false;
	}

	public static boolean IsOpened() {
		boolean ret = false;
		/*synchronized (SLOCK)*/ {
			ret = _IsOpened();
		}
		return ret;
	}

	private static boolean _IsOpened() {
		return isOpened;
	}

	public static int Write(byte[] buffer, int offset, int count) {
		int ret = 0;
		/*synchronized (SLOCK)*/ {
			ret = _Write(buffer, offset, count);
		}
		return ret;
	}

	private static int _Write(byte[] buffer, int offset, int count) {
		int cnt = 0;
		if (null != os) {
			try {
				os.write(buffer, offset, count);
				os.flush();
				cnt = count;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				_Close();
			}
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
		int index = 0;
		long time = System.currentTimeMillis();
		while ((System.currentTimeMillis() - time) < timeout) {
			if (!IsEmpty()) {
				buffer[index++] = GetByte();
			}

			if (index == byteCount)
				break;
		}

		return index;
	}

	private static int ReadIsAvaliable(byte[] buffer, int maxCount)
			throws IOException {
		int ret = 0;
		/*synchronized (SLOCK)*/ {
			ret = _ReadIsAvaliable(buffer, maxCount);
		}
		return ret;
	}

	private static int _ReadIsAvaliable(byte[] buffer, int maxCount)
			throws IOException {
		int available = 0;
		int rec = -1;

		if (null != is) {
			available = is.available();
			if (available > 0) {
				rec = is.read(buffer, 0, available > maxCount ? maxCount
						: available);
			}
		}
		return rec;
	}

	private static void OnRecv(byte[] buffer, int byteOffset, int byteCount) {
		synchronized (NULLLOCK) {
			if (null != callBack)
				callBack.onRecv(buffer, byteOffset, byteCount);
		}
	}

	public static void SetOnRecvCallBack(RecvCallBack callback) {
		synchronized (NULLLOCK) {
			callBack = callback;
		}
	}

	public static boolean Request(byte sendbuf[], int sendlen, int requestlen,
			byte recbuf[], Integer reclen, int timeout) {
		int Retry = 3;

		while ((Retry--) > 0) {
			ClrRec();
			Write(sendbuf, 0, sendlen);
			reclen = Read(recbuf, 0, requestlen, timeout);
			if (requestlen == reclen)
				return true;
		}
		return false;
	}

	public static void ClrRec() {
		NETRXBuffer.ClrRec();
	}

	public static boolean IsEmpty() {
		return NETRXBuffer.IsEmpty();
	}

	public static byte GetByte() {
		return NETRXBuffer.GetByte();
	}

	public static synchronized void Quit() {
		try {
			if (null != mLooper) {
				mLooper.quit();
				mLooper = null;
			}
			netRWThread = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
