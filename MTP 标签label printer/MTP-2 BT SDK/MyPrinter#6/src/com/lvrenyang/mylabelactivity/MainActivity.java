package com.lvrenyang.mylabelactivity;

import java.lang.ref.WeakReference;

import com.lvrenyang.mylabelwork.R;

import com.lvrenyang.mylabelwork.Global;
import com.lvrenyang.mylabelwork.WorkService;
import com.lvrenyang.rwbt.BTHeartBeatThread;
import com.lvrenyang.rwusb.USBHeartBeatThread;
import com.lvrenyang.rwwifi.NETHeartBeatThread;
import com.lvrenyang.utils.DataUtils;
import com.lvrenyang.utils.FileUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private ProgressBar progressBar;
	private static Handler mHandler = null;
	private static String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ManageActivity.getInstance().add(this);

		progressBar = (ProgressBar) findViewById(R.id.progressBarStatus);
		progressBar.setMax(100);

		findViewById(R.id.linearLayoutMainMenuAdvanceLabel).setOnClickListener(
				this);
		findViewById(R.id.linearLayoutMainMenuBluetooth).setOnClickListener(
				this);
		findViewById(R.id.linearLayoutMainMenuMore).setOnClickListener(this);
		findViewById(R.id.linearLayoutMainMenuUSB).setOnClickListener(this);
		FileUtils.debug = true;
		mHandler = new MHandler(this);
		WorkService.addHandler(mHandler);

		Intent intent = new Intent(this, WorkService.class);
		startService(intent);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		WorkService.delHandler(mHandler);
		mHandler = null;
		ManageActivity.getInstance().remove(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		if (WorkService.workThread.isConnecting()) {
			Toast.makeText(this, "please waiting for connecting finished!",
					Toast.LENGTH_SHORT).show();
			return true;
		}

		int id = item.getItemId();
		if (id == R.id.menu_exit) {
			stopService(new Intent(this, WorkService.class));
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.linearLayoutMainMenuAdvanceLabel:
			startActivity(new Intent(this, ActivityLable.class));
			break;
		case R.id.linearLayoutMainMenuBluetooth:
			startActivity(new Intent(this, ActivitySearchBT.class));
			break;
		case R.id.linearLayoutMainMenuMore:
			startActivity(new Intent(this, ActivityMore.class));
			break;
		case R.id.linearLayoutMainMenuUSB:
			startActivity(new Intent(this, ConnectUSBActivity.class));
			break;
		}

	}

	static class MHandler extends Handler {

		WeakReference<MainActivity> mActivity;

		MHandler(MainActivity activity) {
			mActivity = new WeakReference<MainActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			MainActivity theActivity = mActivity.get();
			switch (msg.what) {
			case BTHeartBeatThread.MSG_BTHEARTBEATTHREAD_UPDATESTATUS:
			case NETHeartBeatThread.MSG_NETHEARTBEATTHREAD_UPDATESTATUS:
			case USBHeartBeatThread.MSG_USBHEARTBEATTHREAD_UPDATESTATUS: {
				int statusOK = msg.arg1;
				int status = msg.arg2;
				Log.v(TAG,
						"statusOK: " + statusOK + " status: "
								+ DataUtils.byteToStr((byte) status));
				theActivity.progressBar.setIndeterminate(false);
				if (statusOK == 1)
					theActivity.progressBar.setProgress(100);
				else
					theActivity.progressBar.setProgress(0);
				FileUtils.DebugAddToFile("statusOK: " + statusOK + " status: "
						+ DataUtils.byteToStr((byte) status) + "\r\n",
						FileUtils.sdcard_dump_txt);
				break;
			}

			case Global.CMD_POS_PRINTPICTURERESULT: {
				int result = msg.arg1;
				Log.v(TAG, "Result: " + result);
				break;
			}
			}
		}
	}

}
