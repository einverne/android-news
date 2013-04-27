package com.and.netease;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.and.netease.utils.ConnectWeb;
import com.and.netease.utils.DBAdapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class TabSearchActivity extends Activity {
	private static final String TAG = "Demo";
	protected String keyword;

	ArrayList<HashMap<String, Object>> listItem;
		private DBAdapter dbadapter;
		private Cursor c;
		private Cursor cp;
		private Cursor cd;
		ConnectWeb conn;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_search);
		Log.d(TAG, "Search Start");
				dbadapter = new DBAdapter(this);
				conn.getpeoples(dbadapter);
				c = dbadapter.getpeople(0, 3);
				conn.getplaces(dbadapter);
				cp = dbadapter.getplace(0,3);
				
				conn.getdivisions(dbadapter);
				cd=dbadapter.getdivision(0,3);
			
		Button advanceSearchButton = (Button) findViewById(R.id.button_advanced_search);
		advanceSearchButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText keyword = (EditText) findViewById(R.id.editText_search);
				TabSearchActivity.this.keyword = keyword.getText().toString();
				jumptoAdvancedSearch();
			}
		});
		//锟斤拷通锟斤拷锟斤拷锟斤拷钮
		Button searchButton = (Button) findViewById(R.id.search);
		searchButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText keyword = (EditText) findViewById(R.id.editText_search);
				TabSearchActivity.this.keyword = keyword.getText().toString();
				if (TabSearchActivity.this.keyword.length() > 0) {
					jumptoSearchResult();
				} else {
					Toast.makeText(TabSearchActivity.this, "请输入关键字",Toast.LENGTH_SHORT).show();
				}

			}
		});

		/*
		 * 
		 * 
		 * 锟斤拷取锟饺碉拷丶锟斤拷
		 * 3锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
		 * 
		 * 
		 */
		
		GridView lvhot = (GridView)findViewById(R.id.gridView1);
	
		 listItem = new ArrayList<HashMap<String, Object>>();
		getdata();
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.search_hot_item, new String[] { "HotWord" },
				new int[] { R.id.textView1 });
		lvhot.setAdapter(listItemAdapter);
		//click item
		lvhot.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(TabSearchActivity.this, search.class);
				keyword = (String) listItem.get(arg2).get("HotWord");
				Log.d("wwwwkey", keyword);
				Bundle bundle = new Bundle();
				bundle.putString("keyword", keyword);
				intent.putExtras(bundle);
				startActivity(intent);
			
//				Toast.makeText(TabSearchActivity.this, "锟斤拷锟絚锟斤拷锟斤拷锟斤拷", Toast.LENGTH_LONG).show();
//				startActivity(intent);
//				TabSearchActivity.this.finish();
			}
		});

	}

	
	/**
	 * 
	 * 锟斤拷转锟竭硷拷锟斤拷锟斤拷
	 */
	public void jumptoAdvancedSearch() {
		Intent intent = new Intent(TabSearchActivity.this, advanced_search.class);
		Bundle bundle = new Bundle();
		bundle.putString("keyword", keyword);
		intent.putExtras(bundle);
		startActivity(intent);
	}
	/**
	 * 
	 * 锟斤拷转锟斤拷通锟斤拷锟斤拷
	 */
	public void jumptoSearchResult() {
		Intent intent = new Intent(TabSearchActivity.this, search.class);
		Bundle bundle = new Bundle();
		bundle.putString("keyword", keyword);
		intent.putExtras(bundle);
		startActivity(intent);
	}
	/**
	 * 
	 * 锟斤拷锟轿憋拷锟斤拷取hot锟叫憋拷锟斤拷锟斤拷
	 * 
	 */
		public void getdata(){
			
			for (int i = 0; i<3; i++) {
				//name
				c.moveToPosition(i);
				String text = c.getString(c.getColumnIndex("title"));
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("HotWord", text);
			//	Log.d("wwwwwhotname", text);		
				//place
				cp.moveToPosition(i);
				String textp = cp.getString(cp.getColumnIndex("title"));
				HashMap<String, Object> mapp = new HashMap<String, Object>();
				mapp.put("HotWord", textp);		
				//divide
				cd.moveToPosition(i);
				String textd = cd.getString(cd.getColumnIndex("title"));
				HashMap<String, Object> mapd = new HashMap<String, Object>();
				mapd.put("HotWord", textd);
				if(i==0){
				listItem.add(mapd);
				listItem.add(map);
				listItem.add(mapp);
				}
				if(i==1){
					listItem.add(mapp);
					listItem.add(mapd);
					listItem.add(map);
					}
				if(i==2){
					listItem.add(map);
					listItem.add(mapp);
					listItem.add(mapd);
					
					}
			}
			
		}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

}
