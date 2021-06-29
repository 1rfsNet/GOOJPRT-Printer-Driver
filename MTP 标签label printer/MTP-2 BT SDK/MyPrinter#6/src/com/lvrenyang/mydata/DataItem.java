package com.lvrenyang.mydata;

import java.io.Serializable;

public class DataItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8470934703013446879L;

	public String Name;
	public String Barcode;
	public String Unit;
	public double Number;
	public double Price;
	public String Remark;

	// 保留
	public String ReservedStr1, ReservedStr2, ReservedStr3, ReservedStr4,
			ReservedStr5;
	public int ReservedInt1, ReservedInt2, ReservedInt3, ReservedInt4,
			ReservedInt5;
	public Boolean ReservedBool1, ReservedBool2, ReservedBool3, ReservedBool4,
			ReservedBool5;
}