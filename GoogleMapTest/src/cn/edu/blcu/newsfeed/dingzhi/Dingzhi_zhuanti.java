package cn.edu.blcu.newsfeed.dingzhi;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import cn.edu.blcu.newsfeed.R;
import cn.edu.blcu.newsfeed.utils.ConnectWeb;
import cn.edu.blcu.newsfeed.utils.DBAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;


/**
 * @author Administrator
 * 
 */
@SuppressLint("HandlerLeak")
public class Dingzhi_zhuanti extends SherlockListActivity {

	public static final String TAG = "EV_DEBUG";
	public static final String COUNT = "count";
	public static final String WORDS = "words";
	protected static final int MESSAGE_OK = 0;

	private DBAdapter dbadapter;
	ArrayList<HashMap<String, String>> listItem;
	String url;

	String username;
	String jobname;
	private ProgressDialog progressDialog;
	SimpleAdapter listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_zhuanti);

		ActionBar actionBar = this.getSupportActionBar();
		actionBar.setTitle(getText(R.string.actionbar_dingzhi));

		ListView list = this.getListView();

		dbadapter = new DBAdapter(this);
		Intent intent = this.getIntent();
		username = intent.getStringExtra("username");
		jobname = intent.getStringExtra("jobname");

		progressDialog = ProgressDialog.show(this, "等待", "下载中");
		new Thread() {
			@Override
			public void run() {
				try {
					ConnectWeb.getJobOfUser(dbadapter, username, jobname, 0, 1);
				} catch (Exception e) {

				}
				Message msg_listData = new Message();
				msg_listData.what = MESSAGE_OK;
				handler.sendMessage(msg_listData);
			}
		}.start();

		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(Dingzhi_zhuanti.this,
						dingzhi_xinwen.class);
				Bundle bundle = new Bundle();
				HashMap<String, String> map = listItem.get(position);
				String idd = map.get("_id");
				bundle.putString("ID", idd);
				intent.putExtras(bundle);
				startActivity(intent);

			}
		});

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			switch (message.what) {
			case MESSAGE_OK:
				Cursor c = dbadapter.getuser(username, jobname);
				if (c != null) {
					listItem = new ArrayList<HashMap<String, String>>();
					for (int i = 0; c.moveToNext(); i++) {
						c.moveToPosition(i);
						String _id = c.getString(c.getColumnIndex("_id"));
						String count = c.getString(c
								.getColumnIndex(DBAdapter.userKEY_Count));
						String words = c.getString(c
								.getColumnIndex(DBAdapter.userKEY_words));
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("_id", _id);
						map.put(COUNT, count);
						map.put(WORDS, words);
						listItem.add(map);
					}
					String[] from = new String[] { "count", "words" };
					int[] to = new int[] { R.id.count, R.id.dingzhi_words };
					listAdapter = new SimpleAdapter(Dingzhi_zhuanti.this,
							listItem, R.layout.dingzhi_item, from, to);
					setListAdapter(listAdapter);
				}
				listAdapter.notifyDataSetChanged();
				progressDialog.dismiss();
				break;
			}
		}
	};

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

}
