package com.and.netease;

import java.util.Map;

import com.and.netease.utils.ConnectWeb;
import com.and.netease.utils.DBAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

public class jutixinwen extends Activity {
	private static final String TAG = "Demo";
	ConnectWeb conn;
	DBAdapter dbadapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_jutixinwen);
		TextView tv_title = (TextView) findViewById(R.id.textView_newstitle);
		TextView tv_source = (TextView) findViewById(R.id.textView_media);
		//少一个ICON地方 EV_BUG
		TextView tv_date = (TextView) findViewById(R.id.textView_time);
		TextView tv_text = (TextView) findViewById(R.id.textView_newsText);
		dbadapter = new DBAdapter(this);
		dbadapter.open();
		conn = new ConnectWeb(dbadapter);
		Bundle bundle = this.getIntent().getExtras();
		String title = bundle.getString("url");
		Map<String, String> map = conn.getjutixinwen(title);
		tv_title.setText((String) map.get("title"));
		tv_source.setText((String) map.get("source"));
		tv_date.setText((String) map.get("date"));
		tv_text.setText((String) map.get("text"));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 如果是返回键,直接返回到桌面
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbadapter.close();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Toast.makeText(this, resultCode, Toast.LENGTH_LONG).show();
	}

}