package com.and.netease;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

import com.and.netease.utils.ConnectWeb;
import com.and.netease.utils.DBAdapter;

import android.app.AlertDialog;
import android.app.ListActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.ContextMenu;

import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.ListView;
import android.widget.SimpleAdapter;

public class dingzhi extends ListActivity {
	private static final String List = null;

	private static String TAG = "Demo";
	private SharedPreferences sharedPreferences;
	
	boolean visflag = false;
	ListView lv;
	String user_name;
	String job_delete;
	DBAdapter dbAdapter;
	List<Map<String, String>> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_dingzhi);
		// Person person = (Person)getApplication();
		// user_name = person.getUsername();
		sharedPreferences = this.getSharedPreferences("userinfo", MODE_PRIVATE);
		
		Bundle bundle = this.getIntent().getExtras();
		user_name = bundle.getString("name");
		list = ConnectWeb.getAllJobsOfUser(dbAdapter, user_name);

		lv = this.getListView();
		SimpleAdapter adapter = new SimpleAdapter(this, list,
				R.layout.dingzhi_zhuanti, new String[] { "name", "description",
						"createtime", "endtime" }, new int[] {
						R.id.textView_title, R.id.textView_description,
						R.id.textView_startTime, R.id.textView_endTime });
		lv.setAdapter(adapter);
		lv.setScrollBarStyle(1);

		findViewById(R.id.button_adddingzhi).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						submit_job();
					}
				});
		findViewById(R.id.Button_signout).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sharedPreferences.edit().clear().commit();
				dingzhi.this.finish();
			}
		});
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HashMap<String, String> map = (HashMap<String, String>) lv
						.getItemAtPosition(position);
				String task_dingzhi = map.get("name");
				Intent intent = new Intent();
				intent.putExtra("username", user_name);
				intent.putExtra("task", task_dingzhi);
				intent.setClass(dingzhi.this, Dingzhi_zhuanti.class);
				startActivity(intent);

			}
		});

		ItemOnLongClick();
	}

	// ����ĳһ��Item
	public void ItemOnLongClick() {

		lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				// TODO Auto-generated method stub

				menu.add(0, 0, 0, "删除");
				menu.add(0, 1, 0, "取消");

			}
		});
	}

	public boolean onContextItemSelected(MenuItem item) {
		ContextMenuInfo info = item.getMenuInfo();
		AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) info;
		int position = contextMenuInfo.position;
		job_delete = list.get(position).get("name");

		switch (item.getItemId()) {

		case 0: {
			delete_Data();
			break;
		}
		case 1: {

			break;
		}
		default:
			break;

		}

		return super.onContextItemSelected(item);
	}

	public void delete_Data() {

		AlertDialog.Builder builder = new AlertDialog.Builder(dingzhi.this);
		builder.setTitle("提示")
				.setMessage("确定删除")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								ConnectWeb.deleteJob(user_name, job_delete);
							}
						}).setNegativeButton("No", null).create();
		builder.show();
	}

	public void submit_job() {
		Bundle bundle = new Bundle();
		Intent intent = new Intent();
		bundle.putString("keyword", "news");
		intent.putExtras(bundle);
		intent.setClass(dingzhi.this, advanced_search.class);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(this,MainActivity.class);
			startActivity(intent);
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
