package com.and.netease;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class zhuanti extends ListActivity {
	private static final String TAG = "Demo";
	SimpleAdapter listItemAdapter;
	private ArrayList<HashMap<String, Object>> listItem;
	private DBAdapter dbadapter;
	private Cursor c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_zhuanti);
		Bundle bundle = this.getIntent().getExtras();
		String title = bundle.getString("title");
		dbadapter = new DBAdapter(this);
		dbadapter.open();
		c = dbadapter.getnews(title);
		Log.d("size", String.valueOf(c.getCount()));

		ImageButton imgbtnButton = (ImageButton) findViewById(R.id.imgbtn_back);
		imgbtnButton.setOnClickListener(new OnClickListener() {
			// 相应返回事件
			@Override
			public void onClick(View v) {
				// Intent intent = new Intent();
				// intent.setClass(zhuanti.this, MainActivity.class);
				// startActivity(intent);
				finish();
			}
		});

		listItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; c.moveToNext(); i++) {
			c.moveToPosition(i);
			String Title = c.getString(1);
			String source = c.getString(2);
			String description = c.getString(3);
			String date = c.getString(4);

			HashMap<String, Object> map = new HashMap<String, Object>();
			//ICON CNN 媒体来源  EV_BUG
			map.put("icon",getIcon("abc"));
			map.put("source","CNN");
			map.put("ItemTime", date);
			map.put("Title", Title);
			map.put("description", description);
			map.put("keywords", "key");
			listItem.add(map);
		}

		listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.zhuanti_item, new String[] { "icon","source","ItemTime", "Title",
						"description", "ItemMedio" }, new int[] {R.id.imageView_icon,R.id.textView_source,
						R.id.textView_ItemTime, R.id.Title, R.id.ItemDes,
						R.id.textView_keywords });
		// 添加并且显示
		setListAdapter(listItemAdapter);

		// 添加点击
		((ListView) getListView())
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						c.moveToPosition(arg2 - 1);
						String url = (String) c.getString(5);
						Bundle bundle = new Bundle();
						Intent intent = new Intent(zhuanti.this, jutixinwen.class);
						bundle.putString("url", url);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});
	}

	/**
	 * 传入资源名字 返回资源id
	 * @param name 资源名字
	 * @return 资源的id
	 */
	protected int getIcon(String name){
		Resources res = getResources();
		int id =res.getIdentifier(name, "drawable", getPackageName());
		if (id == 0) {
			return R.drawable.icon;		//EV_BUG 默认ICON图片
		}else{
			return id;
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
		c.close();
		dbadapter.close();
	}

}
