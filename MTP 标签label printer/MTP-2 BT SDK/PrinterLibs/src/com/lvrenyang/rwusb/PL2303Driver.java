package com.lvrenyang.rwusb;

import android.os.Parcel;
import android.os.Parcelable;

import com.lvrenyang.rwusb.PL2303Driver.TTYTermios.FlowControl;
import com.lvrenyang.rwusb.USBDriver;
import com.lvrenyang.utils.FileUtils;

public class PL2303Driver extends USBDriver {

	String description = "Vitural PL2303 Device.";

	public static USBDeviceId id[] = { new USBDeviceId(0x067b, 0x2303) };

	private static final int SET_LINE_REQUEST_TYPE = 0x21;
	private static final int SET_LINE_REQUEST = 0x20;

	private static final int SET_CONTROL_REQUEST_TYPE = 0x21;
	private static final int SET_CONTROL_REQUEST = 0x22;
	private static final int CONTROL_DTR = 0x01;
	private static final int CONTROL_RTS = 0x02;

	private static final int GET_LINE_REQUEST_TYPE = 0xa1;
	private static final int GET_LINE_REQUEST = 0x21;

	private static final int VENDOR_WRITE_REQUEST_TYPE = 0x40;
	private static final int VENDOR_WRITE_REQUEST = 0x01;

	private static final int VENDOR_READ_REQUEST_TYPE = 0xc0;
	private static final int VENDOR_READ_REQUEST = 0x01;

	private enum pl2303_type {
		type_0, /* don't know the difference between type 0 and */
		type_1, /* type 1, until someone from prolific tells us... */
		HX, /* HX version of the pl2303 chip */
	};

	private pl2303_type type = pl2303_type.HX;

	private int pl2303_vendor_read(USBPort port, int value, int index,
			byte[] buffer) {
		if (null == port)
			return RTNCode.NULLPOINTER;
		return ctl(port, VENDOR_READ_REQUEST_TYPE, VENDOR_READ_REQUEST, value,
				index, buffer, 1, 100);
	}

	private int pl2303_vendor_write(USBPort port, int value, int index) {
		if (null == port)
			return RTNCode.NULLPOINTER;
		return ctl(port, VENDOR_WRITE_REQUEST_TYPE, VENDOR_WRITE_REQUEST,
				value, index, null, 0, 100);
	}

	private int set_control_lines(USBPort port, int value) {
		if (null == port)
			return RTNCode.NULLPOINTER;
		return ctl(port, SET_CONTROL_REQUEST_TYPE, SET_CONTROL_REQUEST, value,
				0, null, 0, 100);
	}

	/**
	 * attach 和 release配套使用
	 * 
	 * @param port
	 * @return
	 */
	int attach(USBPort port) {
		// TODO Auto-generated method stub
		if (null == port)
			return RTNCode.NULLPOINTER;

		type = pl2303_type.HX;
		byte[] buf = new byte[256];
		int ret = 0;
		if (ret >= 0)
			ret = pl2303_vendor_read(port, 0x8484, 0, buf);

		if (ret >= 0)
			ret = pl2303_vendor_write(port, 0x0404, 0);
		if (ret >= 0)
			ret = pl2303_vendor_read(port, 0x8484, 0, buf);
		if (ret >= 0)
			ret = pl2303_vendor_read(port, 0x8383, 0, buf);
		if (ret >= 0)
			ret = pl2303_vendor_read(port, 0x8484, 0, buf);
		if (ret >= 0)
			ret = pl2303_vendor_write(port, 0x0404, 1);
		if (ret >= 0)
			ret = pl2303_vendor_read(port, 0x8484, 0, buf);
		if (ret >= 0)
			ret = pl2303_vendor_read(port, 0x8383, 0, buf);
		if (ret >= 0)
			ret = pl2303_vendor_write(port, 0, 1);
		if (ret >= 0)
			ret = pl2303_vendor_write(port, 1, 0);
		if (type == pl2303_type.HX) {
			if (ret >= 0)
				ret = pl2303_vendor_write(port, 2, 0x44);
		} else {
			if (ret >= 0)
				ret = pl2303_vendor_write(port, 2, 0x24);
		}

		FileUtils.DebugAddToFile("usb attach "
				+ (ret >= 0 ? "success" : "failed") + " \r\n",
				FileUtils.sdcard_dump_txt);

		return ret >= 0 ? RTNCode.OK : RTNCode.ERROR;
	}

	/**
	 * release和attach配套使用
	 * 
	 * @param port
	 * @return
	 */
	int release(USBPort port) {
		// TODO Auto-generated method stub
		return RTNCode.OK;
	}

	/**
	 * open和close配套使用
	 * 
	 * @param port
	 * @param serial
	 * @return
	 */
	int open(USBPort port, TTYTermios serial) {
		// TODO Auto-generated method stub
		if (null == port)
			return RTNCode.NULLPOINTER;
		if (null == serial)
			return RTNCode.NULLPOINTER;

		int ret = 0;
		if (pl2303_type.HX == type) {
			if (ret >= 0)
				ret = pl2303_vendor_write(port, 8, 0);
			if (ret >= 0)
				ret = pl2303_vendor_write(port, 9, 0);
		}
		if (ret >= 0) {
			ret = set_termios(port, serial);
		} else {
			ret = RTNCode.ERROR;
		}
		FileUtils.DebugAddToFile("usb open  "
				+ (ret == RTNCode.OK ? "success" : "failed") + " \r\n",
				FileUtils.sdcard_dump_txt);
		return ret;
	}

	/**
	 * close和open配套使用
	 * 
	 * @param port
	 * @param serial
	 * @return
	 */
	int close(USBPort port, TTYTermios serial) {
		// TODO Auto-generated method stub

		return 0;
	}

	/**
	 * 如果termiosnew不为空，那么会用termiosnew中的值设置2303 并且将serial中的termios改为termiosnew
	 */
	private int set_termios(USBPort port, TTYTermios termiosnew) {
		int i;
		int baud_sup[] = { 75, 150, 300, 600, 1200, 1800, 2400, 3600, 4800,
				7200, 9600, 14400, 19200, 28800, 38400, 57600, 115200, 230400,
				460800, 614400, 921600, 1228800, 2457600, 3000000, 6000000 };

		if (null == port)
			return RTNCode.NULLPOINTER;

		if (null == termiosnew)
			return RTNCode.NULLPOINTER;

		byte[] buf = new byte[7];

		ctl(port, GET_LINE_REQUEST_TYPE, GET_LINE_REQUEST, 0, 0, buf, 7, 100);

		// 驱动里面会根据得到的数据和本身的选项来填充字段进行修改，这里
		// 我直接根据termios里面的数据进行修改
		switch (termiosnew.dataBits) {
		case 5:
			buf[6] = 5;
			break;
		case 6:
			buf[6] = 6;
			break;
		case 7:
			buf[6] = 7;
			break;
		default:
			buf[6] = 8;
			break;
		}

		for (i = 0; i < baud_sup.length; i++) {
			if (baud_sup[i] == termiosnew.baudrate)
				break;
		}
		if (i == baud_sup.length)
			termiosnew.baudrate = 9600;
		if (termiosnew.baudrate > 1228800) {
			if (type != pl2303_type.HX)
				termiosnew.baudrate = 1228800;
			else if (termiosnew.baudrate > 6000000)
				termiosnew.baudrate = termiosnew.baudrate;
		}
		if (termiosnew.baudrate <= 115200) {
			buf[0] = (byte) (termiosnew.baudrate & 0xff);
			buf[1] = (byte) ((termiosnew.baudrate >> 8) & 0xff);
			buf[2] = (byte) ((termiosnew.baudrate >> 16) & 0xff);
			buf[3] = (byte) ((termiosnew.baudrate >> 24) & 0xff);
		} else {
			long tmp = 12 * 1000 * 1000 * 32 / termiosnew.baudrate;
			buf[3] = (byte) 0x80;
			buf[2] = 0;
			buf[1] = (byte) (tmp >= 256 ? 1 : 0);
			while (tmp >= 256) {
				tmp >>= 2;
				buf[1] = (byte) ((buf[1] & 0xff) << 1);
			}
			if (tmp > 256) {
				tmp %= 256;
			}
			buf[0] = (byte) tmp;
		}
		switch (termiosnew.stopBits) {
		case ONE:
			buf[4] = 0;
			break;
		case ONEPFIVE:
			buf[4] = 1;
			break;
		case TWO:
			buf[4] = 2;
			break;
		}

		switch (termiosnew.parity) {
		case NONE:
			buf[5] = 0;
			break;
		case ODD:
			buf[5] = 1;
			break;
		case EVEN:
			buf[5] = 2;
			break;
		case MARK:
			buf[5] = 3;
			break;
		case SPACE:
			buf[5] = 4;
			break;
		}

		ctl(port, SET_LINE_REQUEST_TYPE, SET_LINE_REQUEST, 0, 0, buf, 7, 100);

		switch (termiosnew.flowControl) {
		case NONE:
			set_control_lines(port, 0);
			break;
		case DTR_RTS:
			set_control_lines(port, CONTROL_DTR | CONTROL_RTS);
			break;
		}

		buf[0] = buf[1] = buf[2] = buf[3] = buf[4] = buf[5] = buf[6] = 0;

		ctl(port, GET_LINE_REQUEST_TYPE, GET_LINE_REQUEST, 0, 0, buf, 7, 100);

		if (termiosnew.flowControl == FlowControl.DTR_RTS) {
			if (type == pl2303_type.HX)
				pl2303_vendor_write(port, 0x0, 0x61);
			else
				pl2303_vendor_write(port, 0x0, 0x41);
		} else {
			pl2303_vendor_write(port, 0x0, 0x0);
		}

		return RTNCode.OK;
	}

	/**
	 * TTY终端参数，包括波特率，流控制，校验位，停止位，数据位等。
	 * 
	 * @author Administrator
	 * 
	 */
	public static class TTYTermios implements Parcelable {
		/**
		 * 波特率，只支持9600,19200,38400,57600,115200 但是，SDK里面并没有做检查，需要客户端程序自行检查
		 */
		public int baudrate = 9600;

		/**
		 * 流控制，NONE或者DTR_RTS
		 */
		public FlowControl flowControl = FlowControl.NONE;

		/**
		 * 校验位，支持无校验，奇校验，偶校验，表奇校验，空白校验
		 */
		public Parity parity = Parity.NONE;

		/**
		 * 停止位，1,1.5,2
		 */
		public StopBits stopBits = StopBits.ONE;
		/**
		 * 数据位，支持5,6,7,8，但是SDK里面并没有做检查，需要客户端程序自行检查
		 */
		public int dataBits = 8;

		public TTYTermios(int baudrate, FlowControl flowControl, Parity parity,
				StopBits stopBits, int dataBits) {
			this.baudrate = baudrate;
			this.flowControl = flowControl;
			this.parity = parity;
			this.stopBits = stopBits;
			this.dataBits = dataBits;
		}

		public enum FlowControl {
			NONE, DTR_RTS
		}

		public enum Parity {
			NONE, ODD, EVEN, SPACE, MARK
		}

		public enum StopBits {
			ONE, ONEPFIVE, TWO
		}

		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void writeToParcel(Parcel out, int flag) {
			// TODO Auto-generated method stub
			out.writeInt(baudrate);
			out.writeValue(flowControl);
			out.writeValue(parity);
			out.writeValue(stopBits);
			out.writeInt(dataBits);
		}

		public static final Parcelable.Creator<TTYTermios> CREATOR = new Creator<TTYTermios>() {

			@Override
			public TTYTermios createFromParcel(Parcel in) {
				return new TTYTermios(in);
			}

			@Override
			public TTYTermios[] newArray(int size) {
				// TODO Auto-generated method stub
				return new TTYTermios[size];
			}
		};

		public TTYTermios(Parcel in) {
			baudrate = in.readInt();
			flowControl = (FlowControl) in.readValue(FlowControl.class
					.getClassLoader());
			parity = (Parity) in.readValue(Parity.class.getClassLoader());
			stopBits = (StopBits) in.readValue(StopBits.class.getClassLoader());
			dataBits = in.readInt();
		}
	}
}
