package com.lvrenyang.mylabelactivity;

import com.lvrenyang.label.*;
import com.lvrenyang.mylabelwork.R;

import com.lvrenyang.mylabelwork.WorkService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class ActivityMore extends Activity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more);

		findViewById(R.id.linearLayoutHelp).setOnClickListener(this);
		findViewById(R.id.linearLayoutExit).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.linearLayoutHelp:
			startActivity(new Intent(this, ActivityJS.class));
			break;
		case R.id.linearLayoutExit:
			stopService(new Intent(this, WorkService.class));
			finish();
			ManageActivity.getInstance().exit();
			break;
		default:
			break;
		}
	}
}
