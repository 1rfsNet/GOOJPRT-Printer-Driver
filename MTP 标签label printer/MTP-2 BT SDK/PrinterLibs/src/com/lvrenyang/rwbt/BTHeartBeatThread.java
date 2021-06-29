package com.lvrenyang.rwbt;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class BTHeartBeatThread extends Thread {

	public static final int MSG_BTHEARTBEATTHREAD_UPDATESTATUS = 200200;
	private static volatile BTHeartBeatThread heartBeatThread = null;

	private static final int HEARTBEATHANDLER_STARTUP = 1000;

	private static Handler targetHandler = null;
	private static Handler heartBeatHandler = null;
	private static Looper mLooper = null;
	private static boolean threadInitOK = false;

	private static boolean pause = false;
	private static final Lock iofinished = new ReentrantLock();

	private static int StatusOK = 0;
	private static int Status = 0;

	public BTHeartBeatThread(Handler mHandler) {
		targetHandler = mHandler;
	}

	public static BTHeartBeatThread InitInstant(Handler mHandler) {
		if (heartBeatThread == null) {
			synchronized (BTHeartBeatThread.class) {
				if (heartBeatThread == null) {
					heartBeatThread = new BTHeartBeatThread(mHandler);
				}
			}
		}
		return heartBeatThread;
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
		heartBeatHandler = new BTHeartBeatHandler();
		threadInitOK = true;
		Looper.loop();
	}

	private static class BTHeartBeatHandler extends Handler {

		private void SendOutStatus() {
			Message smsg = targetHandler
					.obtainMessage(MSG_BTHEARTBEATTHREAD_UPDATESTATUS);
			smsg.arg1 = StatusOK;
			smsg.arg2 = Status;
			targetHandler.sendMessage(smsg);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			/**
			 * BT 做心跳数据包用10 04 01命令
			 */
			case HEARTBEATHANDLER_STARTUP: {
				byte[] buffer = { 0x10, 0x04, 0x01 };
				int buffersize = 3;
				int sendlen = 0;

				byte[] rec = { 0x00 };
				int reclen = 0;
				int timeout = 2000;

				int intertimeout = 2000;
				final int threshold = 3;
				int curUnrespond = 0;
				while (true) {
					try {
						Thread.sleep(intertimeout);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
					if (pause)
						continue;

					iofinished.lock();
					BTRWThread.ClrRec();// 只调用clearrec也需要这个锁，调用read也需要这个锁
					sendlen = BTRWThread.Write(buffer, 0, buffersize);
					reclen = BTRWThread.Read(rec, 0, 1, timeout);
					iofinished.unlock();

					if (sendlen != buffersize) // 写出错，流的问题。直接break
					{
						StatusOK = 0;
						Status = 0;
						SendOutStatus();
						break;
					}

					if (reclen == 1) {
						curUnrespond = 0;
						StatusOK = 1;
						Status = rec[0];
					} else {
						curUnrespond++;
						StatusOK = 0;
						Status = 0;
					}

					SendOutStatus();

					if (curUnrespond >= threshold)
						break;
				}

				break;
			}
			}
		}
	}

	public static void BeginHeartBeat() {
		Message msg = heartBeatHandler.obtainMessage(HEARTBEATHANDLER_STARTUP);
		heartBeatHandler.sendMessage(msg);
	}

	public static void PauseHeartBeat() {
		pause = true;
		iofinished.lock();
		iofinished.unlock();
	}

	public static void ResumeHeartBeat() {
		pause = false;
	}

	public static void Quit() {
		try {
			if (null != mLooper) {
				mLooper.quit();
				mLooper = null;
			}
			heartBeatThread = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
