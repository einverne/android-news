package com.and.netease;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

	private final int SPLASH_DISPLAY_LENGHT = 200;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		/*
		 * New Handler to start the Menu-Activity and close this Splash-Screen
		 * after some seconds.
		 */
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				/* Create an Intent that will start the Menu-Activity. */
				Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
				SplashActivity.this.startActivity(mainIntent);
				SplashActivity.this.finish();
			}
		}, SPLASH_DISPLAY_LENGHT);

//		Toast.makeText(SplashActivity.this, "......", Toast.LENGTH_LONG);
//		
//		TimerTask task = new TimerTask() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				finish();
//				Intent mainIntent = new Intent().setClass(SplashActivity.this, MainActivity.class);
//				startActivity(mainIntent);
//			}
//		};
//		Timer timer = new Timer();
//		timer.schedule(task, SPLASH_DISPLAY_LENGHT);
	}
}