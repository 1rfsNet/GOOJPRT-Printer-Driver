package com.lvrenyang.utils;

public class ErrorCode {
	/**
	 * 其他错误
	 */
	public static final int ERROR = -1000;
	
	/**
	 * 空指针错误
	 */
	public static final int NULLPOINTER = -1001;
	
	/**
	 * 没有权限
	 */
	public static final int NOPERMISSION = -1002;

	/**
	 * 参数不符合要求，如n需要大于等于0但给出的是小于0
	 */
	public static final int INVALPARAM = -1003;
	
	/**
	 * 出现异常，捕捉之后返回该值
	 */
	public static final int EXCEPTION = -1004;
	
	/**
	 * 未连接
	 */
	public static final int NOTCONNECTED = -1005;
	
	/**
	 * 未打开
	 */
	public static final int NOTOPENED = -1006;
	
	/**
	 * 未实现，这是框架层返回的，如果实现了自己的函数将其覆盖，则不会返回该错误
	 */
	public static final int NOTIMPLEMENTED = -1007;
	
	/**
	 * 机器没有蓝牙
	 */
	public static final int NOBLUETOOTH = -1008;
}
