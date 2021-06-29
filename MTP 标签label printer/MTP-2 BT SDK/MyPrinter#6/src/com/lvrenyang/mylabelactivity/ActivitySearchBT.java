package com.lvrenyang.mylabelactivity;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lvrenyang.label.*;
import com.lvrenyang.mylabelwork.R;

import com.lvrenyang.mylabelwork.Global;
import com.lvrenyang.mylabelwork.WorkService;

public class ActivitySearchBT extends Activity implements OnClickListener {

	private LinearLayout linearlayoutdevices;
	private ProgressBar progressBarSearchStatus;
	private ProgressDialog dialog;

	private BroadcastReceiver broadcastReceiver = null;
	private IntentFilter intentFilter = null;

	private static Handler mHandler = null;
	private static String TAG = "SearchBTActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchbt);

		findViewById(R.id.buttonSearch).setOnClickListener(this);
		progressBarSearchStatus = (ProgressBar) findViewById(R.id.progressBarSearchStatus);
		linearlayoutdevices = (LinearLayout) findViewById(R.id.linearlayoutdevices);
		dialog = new ProgressDialog(this);

		initBroadcast();

		mHandler = new MHandler(this);
		WorkService.addHandler(mHandler);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		WorkService.delHandler(mHandler);
		mHandler = null;
		uninitBroadcast();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.buttonSearch: {
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			if (null == adapter) {
				finish();
				break;
			}

			if (!adapter.isEnabled()) {
				if (adapter.enable()) {
					while (!adapter.isEnabled())
						;
					Log.v(TAG, "Enable BluetoothAdapter");
				} else {
					finish();
					break;
				}
			}

			adapter.cancelDiscovery();
			linearlayoutdevices.removeAllViews();
			adapter.startDiscovery();
			break;
		}
		}
	}

	private void initBroadcast() {
		broadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action = intent.getAction();
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					if (device == null)
						return;
					final String address = device.getAddress();
					String name = device.getName();
					if (name == null)
						name = "蓝牙设备";
					else if (name.equals(address))
						name = "蓝牙设备";
					Button button = new Button(context);
					button.setText(name + ": " + address);
					button.setGravity(android.view.Gravity.CENTER_VERTICAL
							| Gravity.LEFT);
					button.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							// 只有没有连接且没有在用，这个才能改变状态
							dialog.setMessage("正在连接 " + address);
							dialog.setIndeterminate(true);
							dialog.setCancelable(false);
							dialog.show();
							WorkService.workThread.connectBt(address);
						}
					});
					button.getBackground().setAlpha(100);
					linearlayoutdevices.addView(button);
				} else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED
						.equals(action)) {
					progressBarSearchStatus.setVisibility(View.VISIBLE);
					progressBarSearchStatus.setIndeterminate(true);
				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
						.equals(action)) {
					progressBarSearchStatus.setVisibility(View.INVISIBLE);
					progressBarSearchStatus.setIndeterminate(false);
				}

			}

		};
		intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(broadcastReceiver, intentFilter);
	}

	private void uninitBroadcast() {
		if (broadcastReceiver != null)
			unregisterReceiver(broadcastReceiver);
	}

	static class MHandler extends Handler {

		WeakReference<ActivitySearchBT> mActivity;

		MHandler(ActivitySearchBT activity) {
			mActivity = new WeakReference<ActivitySearchBT>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			ActivitySearchBT theActivity = mActivity.get();
			switch (msg.what) {
			/**
			 * DrawerService 的 onStartCommand会发送这个消息
			 */

			case Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT: {
				int result = msg.arg1;
				Toast.makeText(theActivity, (result == 1) ? "连接成功" : "连接失败",
						Toast.LENGTH_SHORT).show();
				Log.v(TAG, "Connect Result: " + result);

				theActivity.dialog.cancel();

				if (result == 1)
					theActivity.finish();
				break;
			}

			}
		}
	}

}
