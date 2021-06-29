package com.lvrenyang.mylabelactivity;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;

// 完全退出程序的方法
public class ManageActivity {

	private List<Activity> activityList = new LinkedList<Activity>();
	private static ManageActivity instance;

	private ManageActivity() {

	}

	public static ManageActivity getInstance() {
		if (null == instance) {
			instance = new ManageActivity();
		}
		return instance;
	}

	public void add(Activity activity) {
		activityList.add(activity);
	}

	public void remove(Activity activity) {
		activityList.remove(activity);
	}

	public void exit() {
		for (Activity activity : activityList) {
			if (null != activity)
				activity.finish();
		}
	}
}
