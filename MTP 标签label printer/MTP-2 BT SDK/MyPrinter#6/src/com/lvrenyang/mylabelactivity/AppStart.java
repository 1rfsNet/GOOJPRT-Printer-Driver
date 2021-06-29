package com.lvrenyang.mylabelactivity;

import java.util.ArrayList;

import com.lvrenyang.label.*;
import com.lvrenyang.mylabelwork.R;

import com.lvrenyang.mydata.DataItem;
import com.lvrenyang.mylabelwork.Global;
import com.lvrenyang.utils.FileUtils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class AppStart extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acticity_appstart);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				/* 判断蓝牙是否存在 */
				BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
				if (null != adapter) {
					if (!adapter.isEnabled()) {
						if (adapter.enable()) {
							// while(!adapter.isEnabled());
							Log.v("SearchBTAndConnectActivity",
									"Enable BluetoothAdapter");
						} else {
							finish();
							return;
						}
					}
				}

				/* 打开wifi */
				//WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				//switch (wifiManager.getWifiState()) {
				//case WifiManager.WIFI_STATE_DISABLED:
				//	wifiManager.setWifiEnabled(true);
				//	break;
				//default:
				//	break;
				//}

				//Global.data = FileUtils.ReadFromFile(Global.file,
				//		getApplicationContext());
				//if (null == Global.data)
				//	Global.data = new ArrayList<DataItem>();

				Intent intent = new Intent(AppStart.this, MainActivity.class);
				startActivity(intent);
				AppStart.this.finish();
			}

		}, 1000);
	}

}
