package com.and.netease;

import java.util.Map;

import com.and.netease.utils.ConnectWeb;
import com.and.netease.utils.DBAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class jutixinwen extends Activity {
	private static final String TAG = "Demo";
	DBAdapter dbadapter;

	TextView tv_title;
	TextView tv_source;
	TextView tv_date;
	TextView tv_text;
	
	ProgressBar progressBar;
	String title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_jutixinwen);
		tv_title = (TextView) findViewById(R.id.textView_newstitle);
		tv_source = (TextView) findViewById(R.id.textView_media);
		//少一个ICON地方 EV_BUG
		tv_date = (TextView) findViewById(R.id.textView_time);
		tv_text = (TextView) findViewById(R.id.textView_newsText);
		progressBar = (ProgressBar)findViewById(R.id.progressBar_jutixinwen);
		
		dbadapter = new DBAdapter(this);
		dbadapter.open();
		Bundle bundle = this.getIntent().getExtras();
		title = bundle.getString("url");
		
		new getData().execute(title);
		
	}

	//其中    
    //参数1：向后台任务的执行方法传递参数的类型；    
    //参数2：在后台任务执行过程中，要求主UI线程处理中间状态，通常是一些UI处理中传递的参数类型；    
    //参数3：后台任务执行完返回时的参数类型 。    	
	public class getData extends AsyncTask<String, Void, Map<String,String>>{
		@Override
		protected void onPreExecute() {
			Log.d(TAG, "getData AsyncTask Pre");
		}

		@Override
		protected void onPostExecute(Map<String, String> map) {
			tv_title.setText((String) map.get("title"));
			tv_source.setText((String) map.get("source"));
			tv_date.setText((String) map.get("date"));
			tv_text.setText((String) map.get("text"));
			progressBar.setVisibility(View.GONE);
		}


		@Override
		protected Map<String, String> doInBackground(String... params) {
			return ConnectWeb.getjutixinwen(params[0]);
		}

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