package cn.edu.blcu.newsfeed.activity;

import java.util.Map;

import android.app.ActionBar;
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
import cn.edu.blcu.newsfeed.R;
import cn.edu.blcu.newsfeed.utils.ConnectWeb;
import cn.edu.blcu.newsfeed.utils.DBAdapter;


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

		ActionBar actionBar = this.getActionBar();
		actionBar.setTitle(getText(R.string.actionbar_new_text));

		tv_title = (TextView) findViewById(R.id.textView_newstitle);
		tv_source = (TextView) findViewById(R.id.textView_media);
		tv_date = (TextView) findViewById(R.id.textView_time);
		tv_text = (TextView) findViewById(R.id.textView_newsText);
		progressBar = (ProgressBar) findViewById(R.id.progressBar_jutixinwen);

		dbadapter = new DBAdapter(this);
		Bundle bundle = this.getIntent().getExtras();
		title = bundle.getString("url");

		new getData().execute(title);

	}

	public class getData extends AsyncTask<String, Void, Map<String, String>> {
		@Override
		protected void onPreExecute() {
			Log.d(TAG, "getData AsyncTask Pre");
		}

		@Override
		protected void onPostExecute(Map<String, String> map) {
			tv_title.setText(map.get("title"));
			tv_source.setText(map.get("source"));
			tv_date.setText(map.get("date"));
			tv_text.setText(map.get("text"));
			progressBar.setVisibility(View.GONE);
		}

		@Override
		protected Map<String, String> doInBackground(String... params) {
			return ConnectWeb.getjutixinwen(params[0]);
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Toast.makeText(this, resultCode, Toast.LENGTH_LONG).show();
	}

}