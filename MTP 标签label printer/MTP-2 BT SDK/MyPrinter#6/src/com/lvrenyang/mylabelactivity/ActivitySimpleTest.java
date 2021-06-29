/**
 * 一些函数的命名。
 * ShowDialogAdd*** 当用户点击确认之后，才会将Item添加到Page
 * ShowDialogModify*** 在已有的Item基础上进行修改。
 */

package com.lvrenyang.mylabelactivity;

import java.lang.ref.WeakReference;

import com.lvrenyang.label.*;
import com.lvrenyang.mylabelwork.R;

import com.lvrenyang.barcode.Barcode.BarcodeType;
import com.lvrenyang.labelitem.ItemBarcodeV2;
import com.lvrenyang.labelitem.ItemBitmap;
import com.lvrenyang.labelitem.ItemHardtext;
import com.lvrenyang.labelitem.ItemQrcode;
import com.lvrenyang.labelitem.LabelPage;
import com.lvrenyang.mylabelwork.Global;
import com.lvrenyang.mylabelwork.WorkService;
import com.lvrenyang.pos.Pos;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class ActivitySimpleTest extends Activity implements OnClickListener {

	private static final String TAG = "ActivitySimpleTest";
	private static Handler mHandler = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simpletest);

		findViewById(R.id.buttonPrintQRCode).setOnClickListener(this);
		findViewById(R.id.buttonPrintBarcode).setOnClickListener(this);
		findViewById(R.id.buttonPrintText).setOnClickListener(this);
		findViewById(R.id.buttonPrintPicture).setOnClickListener(this);

		mHandler = new MHandler(this);
		WorkService.addHandler(mHandler);
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
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

		case R.id.buttonPrintQRCode: {
			LabelPage mLabelPage = new LabelPage(0, 0, 384, 600, 0);
			ItemQrcode qrcode = new ItemQrcode(0, 0, "爱上自己");
			mLabelPage.items.add(qrcode);
			if (WorkService.workThread.isConnected()) {
				Bundle data = new Bundle();
				data.putSerializable(Global.PARCE1, mLabelPage);
				data.putInt(Global.INTPARA1, 1);
				WorkService.workThread.handleCmd(Global.CMD_LABEL_PAGEFINISH,
						data);
			} else {
				Toast.makeText(this, "请先连接打印机", Toast.LENGTH_SHORT).show();
			}
			break;
		}
		case R.id.buttonPrintBarcode:{
			LabelPage mLabelPage = new LabelPage(0,0,384,600,0);
			
			ItemBarcodeV2 upca = new ItemBarcodeV2(0,
					0, BarcodeType.UPCA.getIntValue(), "123456789012");
			ItemBarcodeV2 code128 = new ItemBarcodeV2(0,300,BarcodeType.CODE128.getIntValue(),"NO.123456");
			
			mLabelPage.items.add(upca);
			mLabelPage.items.add(code128);
			
			if (WorkService.workThread.isConnected()) {
				Bundle data = new Bundle();
				data.putSerializable(Global.PARCE1, mLabelPage);
				data.putInt(Global.INTPARA1, 1);
				WorkService.workThread.handleCmd(Global.CMD_LABEL_PAGEFINISH,
						data);
			} else {
				Toast.makeText(this, "请先连接打印机", Toast.LENGTH_SHORT).show();
			}
			break;
		}
		case R.id.buttonPrintText: {
			LabelPage mLabelPage = new LabelPage(0, 0, 384, 600, 0);
			ItemHardtext hardtext = new ItemHardtext(0, 0, "爱上自己");
			hardtext.widthTimes = 3; // 设置3倍宽，3倍高
			hardtext.heightTimes = 3;
			hardtext.startx = 48;// 居中 (384 - 24 * 4 * 3) / 2 = 48;
			mLabelPage.items.add(hardtext);
			if (WorkService.workThread.isConnected()) {
				Bundle data = new Bundle();
				data.putSerializable(Global.PARCE1, mLabelPage);
				data.putInt(Global.INTPARA1, 1);
				WorkService.workThread.handleCmd(Global.CMD_LABEL_PAGEFINISH,
						data);
			} else {
				Toast.makeText(this, "请先连接打印机", Toast.LENGTH_SHORT).show();
			}
			break;
		}
		case R.id.buttonPrintPicture:{
			LabelPage mLabelPage = new LabelPage(0, 0, 384, 600, 0);
			
			Bitmap photo = BitmapFactory.decodeResource(getResources(),R.drawable.pic1);
			// 这个bitmap的宽度，已经向8对齐了。
			Bitmap bmp = Pos.getBWedBitmap(photo, photo.getWidth());

			// 添加Bitmap到布局
			ItemBitmap itemBitmap = new ItemBitmap(0, 0,
					bmp);
			itemBitmap.id = mLabelPage.getNextId();
			mLabelPage.items.add(itemBitmap);
			if (WorkService.workThread.isConnected()) {
				Bundle data = new Bundle();
				data.putSerializable(Global.PARCE1, mLabelPage);
				data.putInt(Global.INTPARA1, 1);
				WorkService.workThread.handleCmd(Global.CMD_LABEL_PAGEFINISH,
						data);
			} else {
				Toast.makeText(this, "请先连接打印机", Toast.LENGTH_SHORT).show();
			}
			break;
		}
		default: {
			break;
		}

		}
	}

	static class MHandler extends Handler {

		WeakReference<ActivitySimpleTest> mActivity;

		MHandler(ActivitySimpleTest activity) {
			mActivity = new WeakReference<ActivitySimpleTest>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			ActivitySimpleTest theActivity = mActivity.get();
			switch (msg.what) {

			case Global.CMD_LABEL_PAGEFINSIH_RESULT: {
				int result = msg.arg1;
				Toast.makeText(theActivity, (result == 1) ? "成功" : "失败",
						Toast.LENGTH_SHORT).show();
				Log.v(TAG, "Result: " + result);
				break;
			}

			}
		}
	}
}
