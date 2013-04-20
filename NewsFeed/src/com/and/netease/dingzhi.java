package com.and.netease;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.and.netease.utils.ConnectWeb;
import com.and.netease.utils.DBAdapter;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class dingzhi extends ListActivity {
	private static final String List = null;

	private static String TAG = "Demo";
	
	boolean visflag = false;
	ListView lv;
	String user_name;
	String job_delete;
	DBAdapter dbAdapter;
	List<Map<String, String>> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		//
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_dingzhi);
		user_name = (String) this.getIntent().getStringExtra("name");
		list = ConnectWeb.getAllJobsOfUser(dbAdapter,
				user_name);
		show_data();
		lv.setScrollBarStyle(1);

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

	// 长按某一个Item
	public void ItemOnLongClick() {

		lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				// TODO Auto-generated method stub

				menu.add(0, 0, 0, "添加");
				menu.add(0, 1, 0, "删除");
				menu.add(0, 2, 0, "取消");

			}
		});
	}

	public boolean onContextItemSelected(MenuItem item) {
//		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
//				.getMenuInfo();

		ContextMenuInfo info = item.getMenuInfo();
		   AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) info;
		   // 获取选中行位置
		   int position = contextMenuInfo.position;
		   job_delete=list.get(position).get("name");
		 
		
		switch (item.getItemId()) {
		case 0:
		{
			submit_job();
		}
			break;
		case 1: {
			delete_Data();
			break;
		}
		case 2: {
			
			break;
		}
		default:
			break;

		}

		return super.onContextItemSelected(item);
	}

	public void delete_Data() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(dingzhi.this);
		builder.setTitle("删除内容").setMessage("确定删除吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 删除数据
						ConnectWeb.deleteJob(user_name, job_delete);
						// 显示数据
						show_data();
					}
				}).setNegativeButton("取消", null).create();
		builder.show();
	}

	public void show_data() {
		list = ConnectWeb.getAllJobsOfUser(dbAdapter,
				user_name);

		//ladapter = new ListVIewAdapter(this);
		lv = this.getListView();

		SimpleAdapter adapter = new SimpleAdapter(this, list,
				R.layout.dingzhi_zhuanti, new String[] { "name", "description",
						"createtime", "endtime" }, new int[] {
						R.id.textView_title, R.id.textView_description,
						R.id.textView_startTime, R.id.textView_endTime });
		lv.setAdapter(adapter);

	}

	public void submit_job() {

		Intent intent = new Intent();
		intent.putExtra("username", user_name);
		intent.setClass(dingzhi.this, TabSearchActivity.class);
		startActivity(intent);
		this.finish();

	}

}
