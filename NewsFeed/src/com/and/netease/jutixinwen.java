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
		//��һ��ICON�ط� EV_BUG
		tv_date = (TextView) findViewById(R.id.textView_time);
		tv_text = (TextView) findViewById(R.id.textView_newsText);
		progressBar = (ProgressBar)findViewById(R.id.progressBar_jutixinwen);
		
		dbadapter = new DBAdapter(this);
		dbadapter.open();
		Bundle bundle = this.getIntent().getExtras();
		title = bundle.getString("url");
		
		new getData().execute(title);
		
	}

	//����    
    //����1�����̨�����ִ�з������ݲ��������ͣ�    
    //����2���ں�̨����ִ�й����У�Ҫ����UI�̴߳����м�״̬��ͨ����һЩUI�����д��ݵĲ������ͣ�    
    //����3����̨����ִ���귵��ʱ�Ĳ������� ��    	
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
		// ����Ƿ��ؼ�,ֱ�ӷ��ص�����
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