package com.and.netease;

import java.util.ArrayList;
import java.util.HashMap;

import com.and.netease.R.string;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class search extends Activity {

	private static final String TAG = "Demo";
	String keyword;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_search_result);
		Log.d(TAG, "search_result_start");

		Bundle bundle = this.getIntent().getExtras();
		keyword = bundle.getString("keyword");
		Toast.makeText(this, keyword, Toast.LENGTH_SHORT).show();
		
		// 绑定Layout里面的ListView
		ListView myListView = (ListView) findViewById(R.id.listView_searchresult);
		// 生成动态数组，加入数据
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 10; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemTitle", "搜索結果" + i+"    "+ keyword);
			map.put("ItemText", "搜索結果摘要");
			listItem.add(map);
		}

		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.zuijinxinwen_item, new String[] {  
						"ItemTitle", "ItemText" }, new int[] {  
						R.id.ItemTitle, R.id.ItemText });
		// 添加并且显示
		myListView.setAdapter(listItemAdapter);

		myListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// Intent intent =new Intent();
				// intent.setClass(search.this, zhuanti.class);
				// startActivity(intent);
				// TabzuijinxinwenActivity.this.finish();
			}
		});

		Button btnButton = (Button)findViewById(R.id.button_dingzhi);
		btnButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 將定制的內容傳送到定制界面,中間還要經過高級設置界面
				
//				Intent intent = new Intent();
//				intent.setClass(search.this, dingzhi.class);
//				Bundle bundle = new Bundle();
//				bundle.putString("keyword", keyword);
//				intent.putExtras(bundle);
//				startActivity(intent);
				Toast.makeText(search.this, "定制還沒做", Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return true;
	}
}