package com.lvrenyang.rwbuf;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 为了防止在读取或写入的时候，调用了清除，所以需要锁
 * 
 * @author Administrator
 * 
 */
public class RxBuffer {

	private final Lock BUFLOCK = new ReentrantLock();
	int RxSize;
	int Read, Write;
	byte Buffer[];

	public RxBuffer(int RX_SIZE) {
		Read = Write = 0;
		RxSize = RX_SIZE;
		Buffer = new byte[RxSize];
	}

	public byte GetByte() {
		byte ch;
		BUFLOCK.lock();
		ch = _GetByte();
		BUFLOCK.unlock();
		return ch;
	}

	public byte _GetByte() {
		byte ch;
		if (Read > (RxSize - 1))
			Read = 0;
		ch = Buffer[Read++];
		return (ch);
	}

	public void PutByte(byte ch) {
		BUFLOCK.lock();
		_PutByte(ch);
		BUFLOCK.unlock();
	}

	public void _PutByte(byte ch) {
		if (Write > RxSize - 1)
			Write = 0;
		Buffer[Write++] = ch;
	}

	public void ClrRec() {
		BUFLOCK.lock();
		_ClrRec();
		BUFLOCK.unlock();
	}

	public void _ClrRec() {
		Write = Read = 0;
	}

	public boolean IsEmpty() {
		boolean result;
		BUFLOCK.lock();
		result = _IsEmpty();
		BUFLOCK.unlock();
		return result;
	}

	public boolean _IsEmpty() {
		return (Read == Write);
	}
}
