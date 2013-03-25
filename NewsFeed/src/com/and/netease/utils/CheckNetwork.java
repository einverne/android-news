package com.and.netease.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * ≈–∂œÕ¯¬Á «∑Ò¡¨Ω”
 * 
 * @author einverne
 * 
 */
public class CheckNetwork {
	private static Activity activity;

	public CheckNetwork(Activity activity) {
		this.activity = activity;
	}

	public boolean check() {
		ConnectivityManager CM = (ConnectivityManager) activity
				.getApplicationContext().getSystemService(
						Context.CONNECTIVITY_SERVICE);
		if (CM == null) {
			return false;
		}
		NetworkInfo NI = CM.getActiveNetworkInfo();
		if (NI == null || !NI.isAvailable()) {
			return false;
		}
		return true;
	}
}
