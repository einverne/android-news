package com.and.netease;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class TabSearchActivity extends Activity {
	private static final String TAG = "Demo";
	protected String keyword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_search);
		Log.d(TAG, "Search Start");

		ImageButton advanceSearchButton = (ImageButton) findViewById(R.id.button_advanced_search);
		advanceSearchButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText keyword = (EditText) findViewById(R.id.editText_search);
				TabSearchActivity.this.keyword = keyword.getText().toString();
				jumptoAdvancedSearch();
			}
		});

		ImageButton searchButton = (ImageButton) findViewById(R.id.search);
		searchButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText keyword = (EditText) findViewById(R.id.editText_search);
				TabSearchActivity.this.keyword = keyword.getText().toString();
				if (TabSearchActivity.this.keyword.length() > 0) {
					jumptoSearchResult();
				} else {
					Toast.makeText(TabSearchActivity.this, "ÇëÊäÈë¹Ø¼ü×Ö",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		ListView lvhot = (ListView) findViewById(R.id.listView_hotword);
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 10; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("HotWord", "Hot" + i);
			listItem.add(map);
		}

		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.list_item, new String[] { "HotWord" },
				new int[] { R.id.textView });
		lvhot.setAdapter(listItemAdapter);

		lvhot.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(TabSearchActivity.this, search.class);
				Toast.makeText(TabSearchActivity.this, "ŸáücµÄËÑË÷", Toast.LENGTH_LONG).show();
//				startActivity(intent);
//				TabSearchActivity.this.finish();
			}
		});

	}

	public void jumptoAdvancedSearch() {
		Intent intent = new Intent(TabSearchActivity.this, advanced_search.class);
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

}
