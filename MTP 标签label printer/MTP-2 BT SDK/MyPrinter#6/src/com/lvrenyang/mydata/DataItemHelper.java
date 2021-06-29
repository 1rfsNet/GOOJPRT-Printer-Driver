package com.lvrenyang.mydata;

import java.util.List;

public class DataItemHelper {

	public static boolean AddDataItemToList(DataItem item, List<DataItem> items) {
		for (DataItem i : items) {
			if (i.Name.equals(item.Name))
				return false;
		}
		items.add(item);
		return true;
	}
}
