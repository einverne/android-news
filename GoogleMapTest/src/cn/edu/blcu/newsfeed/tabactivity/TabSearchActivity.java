package cn.edu.blcu.newsfeed.tabactivity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import cn.edu.blcu.newsfeed.R;
import cn.edu.blcu.newsfeed.search.advanced_search;
import cn.edu.blcu.newsfeed.search.search;
import cn.edu.blcu.newsfeed.utils.ConnectWeb;
import cn.edu.blcu.newsfeed.utils.DBAdapter;


public class TabSearchActivity extends Activity {
	private static final String TAG = "Demo";
	protected String keyword;

	ArrayList<HashMap<String, Object>> listItem;
	private DBAdapter dbadapter;
	private Cursor c;
	ConnectWeb conn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_search);
		Log.d(TAG, "Search Start");
		dbadapter = new DBAdapter(this);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

			}
		}, 2000);

		c = dbadapter.getHotWords();

		Button advanceSearchButton = (Button) findViewById(R.id.button_advanced_search);
		advanceSearchButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText keyword = (EditText) findViewById(R.id.editText_search);
				TabSearchActivity.this.keyword = keyword.getText().toString();
				jumptoAdvancedSearch();
			}
		});
		Button searchButton = (Button) findViewById(R.id.search);
		searchButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText keyword = (EditText) findViewById(R.id.editText_search);
				TabSearchActivity.this.keyword = keyword.getText().toString();
				if (TabSearchActivity.this.keyword.length() > 0) {
					jumptoSearchResult();
				} else {
					Toast.makeText(TabSearchActivity.this, "请输入关键词",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		GridView lvhot = (GridView) findViewById(R.id.gridView1);

		listItem = new ArrayList<HashMap<String, Object>>();
		getdata();
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.search_hot_item, new String[] { "HotWord" },
				new int[] { R.id.textView1 });
		lvhot.setAdapter(listItemAdapter);
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

			}
		});

	}

	public void jumptoAdvancedSearch() {
		Intent intent = new Intent(TabSearchActivity.this,
				advanced_search.class);
		Bundle bundle = new Bundle();
		bundle.putString("keyword", keyword);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	public void jumptoSearchResult() {
		Intent intent = new Intent(TabSearchActivity.this, search.class);
		Bundle bundle = new Bundle();
		bundle.putString("keyword", keyword);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	public void getdata() {
		//
		// for (int i = 0; i < 3; i++) {
		// // name
		// c.moveToPosition(i);
		// String text = c.getString(c.getColumnIndex("title"));
		// HashMap<String, Object> map = new HashMap<String, Object>();
		// map.put("HotWord", text);
		// // Log.d("wwwwwhotname", text);
		// // place
		// cp.moveToPosition(i);
		// String textp = cp.getString(cp.getColumnIndex("title"));
		// HashMap<String, Object> mapp = new HashMap<String, Object>();
		// mapp.put("HotWord", textp);
		// // divide
		// cd.moveToPosition(i);
		// String textd = cd.getString(cd.getColumnIndex("title"));
		// HashMap<String, Object> mapd = new HashMap<String, Object>();
		// mapd.put("HotWord", textd);
		// if (i == 0) {
		// listItem.add(mapd);
		// listItem.add(map);
		// listItem.add(mapp);
		// }
		// if (i == 1) {
		// listItem.add(mapp);
		// listItem.add(mapd);
		// listItem.add(map);
		// }
		// if (i == 2) {
		// listItem.add(map);
		// listItem.add(mapp);
		// listItem.add(mapd);
		//
		// }
		// }

		String text = c.getString(c.getColumnIndex("words"));
		String[] str = text.split(" ");
		for (String st : str) {
			HashMap<String, Object> mapd = new HashMap<String, Object>();
			mapd.put("HotWord", st);
			if (listItem.size() < 9) {
				listItem.add(mapd);
			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

}
