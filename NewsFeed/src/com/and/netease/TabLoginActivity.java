package com.and.netease;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class TabLoginActivity extends Activity {
	private static final String TAG = "Demo";  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_login);
		
		Log.d(TAG, "Login start");
		
		Button loginButton = (Button)findViewById(R.id.button_denglu);
		loginButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				//EV_BUG
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(TabLoginActivity.this, dingzhi.class);
				startActivity(intent);
				
			}
		});
		
		Button quxiaoButton = (Button)findViewById(R.id.button_quxiao);
		quxiaoButton.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
}
