package com.stone.panoramaschool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

/**
 *com.stone.panoramaschool
 *
 * @author stone
 *
 * 2014年10月26日/下午1:52:22
 */
@SuppressLint("HandlerLeak")
public class WelcomeActivity extends Activity {
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Intent intent=new Intent(WelcomeActivity.this, NoteActivity.class);
			startActivity(intent);
			WelcomeActivity.this.finish();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		handler.sendEmptyMessageDelayed(2, 1000);
	}
}