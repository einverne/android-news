package cn.edu.blcu.newsfeed.dingzhi;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import cn.edu.blcu.newsfeed.R;
import cn.edu.blcu.newsfeed.search.advanced_search;
import cn.edu.blcu.newsfeed.utils.ConnectWeb;
import cn.edu.blcu.newsfeed.utils.DBAdapter;


public class dingzhi extends ListActivity {
	private static final String TAG = "EV_DEBUG";
	protected static final int MESSAGE_OK = 0;
	private SharedPreferences sharedPreferences;

	boolean visflag = false;
	ListView lv;
	String user_name;
	String job_delete;
	protected DBAdapter dbAdapter;
	ArrayList<HashMap<String, String>> list;
	private SimpleAdapter listAdapter;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_dingzhi);

		ActionBar actionBar = this.getActionBar();
		actionBar.setTitle(getText(R.string.actionbar_dingzhi));

		sharedPreferences = this.getSharedPreferences("userinfo", MODE_PRIVATE);

		Bundle bundle = this.getIntent().getExtras();
		user_name = bundle.getString("name");

		progressDialog = ProgressDialog.show(this, "等待", "正在下载");
		new Thread() {
			@Override
			public void run() {
				try {
					list = ConnectWeb.getAllJobsOfUser(dbAdapter, user_name);
				} catch (Exception e) {
				}

				Message msg_listData = new Message();
				msg_listData.what = MESSAGE_OK;
				handler.sendMessage(msg_listData);
			}
		}.start();

		lv = this.getListView();

		// findViewById(R.id.button_adddingzhi).setOnClickListener(
		// new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// submit_job();
		// }
		// });
		// findViewById(R.id.Button_signout).setOnClickListener(
		// new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// sharedPreferences.edit().clear().commit();
		// dingzhi.this.finish();
		// }
		// });

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HashMap<String, String> map = list.get(position);
				String jobname = map.get("name");
				Intent intent = new Intent(dingzhi.this, Dingzhi_zhuanti.class);
				intent.putExtra("username", user_name);
				intent.putExtra("jobname", jobname);
				startActivity(intent);

			}
		});

		lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				menu.setHeaderTitle("选项");
				menu.add(0, 0, 0, "删除");
			}
		});
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			switch (message.what) {
			case MESSAGE_OK:
				listAdapter = new SimpleAdapter(
						dingzhi.this,
						list,
						R.layout.dingzhi_zhuanti,
						new String[] { "name", "description", "createtime",
								"endtime" },
						new int[] { R.id.textView_title,
								R.id.textView_description,
								R.id.textView_startTime, R.id.textView_endTime });
				lv.setAdapter(listAdapter);
				lv.setScrollBarStyle(1);
				progressDialog.dismiss();
				break;
			}
		}
	};

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		ContextMenuInfo info = item.getMenuInfo();
		AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) info;
		final int position = contextMenuInfo.position;
		job_delete = list.get(position).get("name");
		switch (item.getItemId()) {
		case 0: {
			AlertDialog.Builder builder = new AlertDialog.Builder(dingzhi.this);
			builder.setTitle("注意")
					.setMessage("你确定要删除？")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (ConnectWeb.deleteJob(user_name,
											job_delete)) {
										Toast.makeText(dingzhi.this, "删除成功",
												Toast.LENGTH_SHORT).show();
										list.remove(list.get(position));
										listAdapter.notifyDataSetChanged();
									}
								}
							}).setNegativeButton("No", null).create();
			builder.show();
			break;
		}
		default:
			break;

		}

		return super.onContextItemSelected(item);
	}

	public void submit_job() {
		Bundle bundle = new Bundle();
		Intent intent = new Intent(dingzhi.this, advanced_search.class);
		bundle.putString("keyword", "news");
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
