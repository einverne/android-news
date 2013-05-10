package com.and.netease;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.and.netease.utils.CheckNetwork;
import com.and.netease.utils.ConnectWeb;
import com.and.netease.utils.DBAdapter;

public class SplashActivity extends Activity {

	private final int SPLASH_DISPLAY_LENGHT = 4000;
	protected DBAdapter dbadapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		/*
		 * New Handler to start the Menu-Activity and close this Splash-Screen
		 * after some seconds.
		 */
		dbadapter = new DBAdapter(this);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				CheckNetwork cn = new CheckNetwork(SplashActivity.this);
				if (cn.check()) {
					ConnectWeb.getzuijinxinwen(dbadapter);
					ConnectWeb.getpeoples(dbadapter);
					ConnectWeb.getplaces(dbadapter);
					ConnectWeb.getdivisions(dbadapter);
				}
				/* Create an Intent that will start the Menu-Activity. */
				Intent mainIntent = new Intent(SplashActivity.this,
						MainActivity.class);
				SplashActivity.this.startActivity(mainIntent);
				SplashActivity.this.finish();
			}
		}, SPLASH_DISPLAY_LENGHT);

		// Toast.makeText(SplashActivity.this, "......", Toast.LENGTH_LONG);
		//
		// TimerTask task = new TimerTask() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// finish();
		// Intent mainIntent = new Intent().setClass(SplashActivity.this,
		// MainActivity.class);
		// startActivity(mainIntent);
		// }
		// };
		// Timer timer = new Timer();
		// timer.schedule(task, SPLASH_DISPLAY_LENGHT);
	}
}