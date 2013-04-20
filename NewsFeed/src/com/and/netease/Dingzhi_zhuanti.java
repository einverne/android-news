package com.and.netease;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.and.netease.utils.ConnectWeb;
import com.and.netease.utils.DBAdapter;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class Dingzhi_zhuanti extends ListActivity {

	private DBAdapter dbadapter;
	ArrayList<HashMap<String, String>> listItem;
	String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_zhuanti);
		
		ListView list = this.getListView();

		dbadapter = new DBAdapter(this);
		Intent intent = getIntent();

		String username = intent.getStringExtra("username");
		String task = intent.getStringExtra("task");
		
		//测试数据
		username = "gaojinping";
		task = "20111226094543606";
		if (ConnectWeb.getJobOfUser(dbadapter, username, task) == false) {
			Toast.makeText(Dingzhi_zhuanti.this, "没有可以显示的内容",
					Toast.LENGTH_SHORT).show();	
		}
		
		Cursor  c= dbadapter.dingzhizhuanti(username, task);
		if(c!=null){
		listItem = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < 10 && c.moveToNext(); i++) {
			c.moveToPosition(i);
			String title = c.getString(c.getColumnIndex("title"));
			String source = c.getString(c.getColumnIndex("source"));
			String date = c.getString(c.getColumnIndex("date"));
			String description = c.getString(c.getColumnIndex("description"));
			url = c.getString(c.getColumnIndex("url")); 
			Log.d("test zhuanti","test zhuanti dingzhi"+title);


			HashMap<String, String> map = new HashMap<String, String>();
			map.put("date", date);
			map.put("description", description);
			map.put("ItemTitle", title);
			map.put("Source", source);
			
			listItem.add(map);
		}
		

		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.zhuanti_item, new String[] { "icon", "Source",
						"date", "ItemTitle", "description" },
				new int[] { R.id.imageView_icon, R.id.textView_source,
						R.id.textView_ItemTime, R.id.Title, R.id.ItemDes, });
		// 添加并且显示
		setListAdapter(listItemAdapter);

	}
		else {
			Toast.makeText(Dingzhi_zhuanti.this, "没有可以显示的内容", 1000);
		}
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("url",url);
				intent.setClass(Dingzhi_zhuanti.this, jutixinwen.class);
				intent.putExtras(bundle);
				startActivity(intent);

			}
		});

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
		
	}

}
