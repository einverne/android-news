package com.and.netease;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.and.netease.utils.CheckNetwork;
import com.and.netease.utils.DBAdapter;

/**
 * 每一个Jobname对应的好多专题
 * 
 * @author einverne
 * 
 */
public class dingzhi_xinwen extends ListActivity {

	private static final String TAG = "EV_DEBUG";
	private static final String SOURCE = "source";
	private static final String TITLE = "title";
	private static final String WORDS = "words";
	private static final String DESCRIPTION = "description";
	private static final String DATE = "date";
	private static final String URL = "url";
	private ArrayList<HashMap<String, Object>> list;
	DBAdapter dbAdapter;
	SimpleAdapter listAdapter;
	Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_zhuanti);
		list = new ArrayList<HashMap<String, Object>>();
		dbAdapter = new DBAdapter(this);
		Intent intent = this.getIntent();
		String id = intent.getStringExtra("ID");
		Log.d(TAG, "id:"+id);
		cursor = dbAdapter.getUserZhuanti(Long.parseLong(id));
		if (cursor != null) {
			for (int i = 0; cursor.moveToNext(); i++) {
				cursor.moveToPosition(i);
				String title = cursor.getString(cursor.getColumnIndex(TITLE));
				String source = cursor.getString(cursor.getColumnIndex(SOURCE));
				String words = cursor.getString(cursor.getColumnIndex(WORDS));
				String description = cursor.getString(cursor
						.getColumnIndex(DESCRIPTION));
				String date = cursor.getString(cursor.getColumnIndex(DATE));
				String url = cursor.getString(cursor.getColumnIndex(URL));

				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("icon", getIcon(source));
				map.put("source", source);
				map.put("ItemTime", date);
				map.put("Title", title);
				map.put("description", description);
				list.add(map);
			}
		}

		listAdapter = new SimpleAdapter(this, list, R.layout.zhuanti_item,
				new String[] { "icon", "source", "ItemTime", "Title",
						"description", "ItemMedio" }, new int[] {
						R.id.imageView_icon, R.id.textView_source,
						R.id.textView_ItemTime, R.id.Title, R.id.ItemDes });
		// 添加并且显示
		setListAdapter(listAdapter);
		// 添加点击
		((ListView) getListView())
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						Log.d(TAG, "专题中点击的Item标号:" + arg2);

						CheckNetwork net = new CheckNetwork(dingzhi_xinwen.this);
						boolean net_conn = net.check();
						if (net_conn) {
							cursor.moveToPosition(arg2);
							String url = (String) cursor.getString(cursor
									.getColumnIndex(URL));
							Bundle bundle = new Bundle();
							Intent intent = new Intent(dingzhi_xinwen.this,
									jutixinwen.class);
							bundle.putString(URL, url);
							intent.putExtras(bundle);
							startActivity(intent);
						} else {
							Toast.makeText(dingzhi_xinwen.this, "请检查联网状态",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	/**
	 * 传入资源名字 返回资源id
	 * 
	 * @param name
	 *            资源名字
	 * @return 资源的id
	 */
	protected int getIcon(String name) {
		Resources res = getResources();
		int id = res.getIdentifier(name, "drawable", getPackageName());
		if (id == 0) {
			return R.drawable.icon;
		} else {
			return id;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
