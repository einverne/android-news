package com.and.netease;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.and.netease.utils.CheckNetwork;
import com.and.netease.utils.ConnectWeb;

public class search extends Activity {
	/**
	 * Data
	 */
	public class getData extends
			AsyncTask<Void, Void, ArrayList<HashMap<String, Object>>> {
		private String aboutChina;
		private String dateF;
		private String dateT;
		private String keyword;
		private int max;
		private int start;

		public getData(String keyword, String dateF, String dateT,
				String aboutChina, int start, int max) {
			this.keyword = keyword;
			this.dateF = dateF;
			this.dateT = dateT;
			this.aboutChina = aboutChina;
			this.start = start;
			this.max = max;
		}

		@Override
		protected ArrayList<HashMap<String, Object>> doInBackground(
				Void... params) {
			Map<String, Object> searchmap = ConnectWeb.getsearch(keyword,
					dateF, dateT, aboutChina, start, max);
			List<Map<String, Object>> list = (List<Map<String, Object>>) searchmap
					.get("first");
			Log.d("dateF", dateF);
			Log.d("dateT", dateT);
			numberOfSearchResult = (Integer) searchmap.get("second");
			for (int i = 0; i < list.size(); i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				Map<String, Object> map1 = (Map<String, Object>) list.get(i);
				String source = (String) map1.get("source");
				map.put("Title", map1.get("title"));
				map.put("source", source);
				map.put("icon", getIcon(source));
				map.put("ItemTime", map1.get("date"));
				map.put("description", (String) map1.get("description"));
				map.put("url", map1.get("url"));
				listItem.add(map);
			}
			if (list.size() < 30) {
				flagLoadMoreData = 5;
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, Object>> result) {
			String te = "共有" + numberOfSearchResult + "条";
			text.setText(te);
			if (numberOfSearchResult == 0) {
				Toast.makeText(search.this, "没有符合关键词的结果,请调整关键词",
						Toast.LENGTH_SHORT).show();
			}
			listItemAdapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			Toast.makeText(search.this, "正在后台加载数据,请稍等", Toast.LENGTH_SHORT)
					.show();
			super.onPreExecute();
		}

	}
	private static final String TAG = "EV_DEBUG";
	boolean aboutChina = false;
	private Button bt;
	String china = "F";
	String dateF;
	String dateT;
	String day;
	int flagLoadMoreData = 0;
	String keyword;
	private int lastVisibleIndex;
	private ArrayList<Map<String, Object>> listItem;
	private SimpleAdapter listItemAdapter;
	private int mDay;
	private int mMonth;
	String month;
	private View moreView;
	private int mYear;
	private ListView myListView;
	private int numberOfSearchResult = 0;
	private ProgressBar pg;
	private int resultOnceQuery = 30;
	TextView text;

	String url;

	/**
	 * get date for search
	 */
	public void getdate() {
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH) + 1;// 锟斤拷取锟斤拷前锟铰凤拷
		mDay = c.get(Calendar.DAY_OF_MONTH);// 锟斤拷取锟斤拷前锟铰份碉拷锟斤拷锟节猴拷锟斤拷
		String year = mYear + "";
		int year1 = mYear - 1;
		String yearf = year1 + "";
		if (mMonth <= 9) {
			month = "0" + mMonth;
		} else {
			month = "" + mMonth;

		}
		if (mDay <= 9) {
			day = "0" + mDay;
		} else {
			day = mDay + "";
		}
		dateT = year + month + day;
		dateF = yearf + month + day;
		Log.d("Date", dateF + ":" + dateT + ":" + mMonth);
	}

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_search_result);
		Log.d(TAG, "search_result_Activity_start");

		text = (TextView) findViewById(R.id.textView_newstitle);
		myListView = (ListView) findViewById(R.id.listView_searchresult);
		listItem = new ArrayList<Map<String, Object>>();

		Bundle bundle = this.getIntent().getExtras();
		if (bundle.getString("dateF") == null) {
			getdate();
			keyword = bundle.getString("keyword");
		} else {
			dateF = bundle.getString("dateF");
			dateT = bundle.getString("dateT");
			keyword = bundle.getString("keyword");
		}
		if (bundle.getBoolean("aboutChina") != false) {
			aboutChina = bundle.getBoolean("aboutChina");
			Log.d(TAG, "search aboutChina:" + aboutChina);
		}
		if (aboutChina) {
			china = "T";
		}
		new getData(keyword, dateF, dateT, china, 0, resultOnceQuery).execute();

		listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.zhuanti_item, new String[] { "icon", "source",
						"ItemTime", "Title", "description" }, new int[] {
						R.id.imageView_icon, R.id.textView_source,
						R.id.textView_ItemTime, R.id.Title, R.id.ItemDes });
		// load more data
		if (flagLoadMoreData != 5) {

		}
		moreView = getLayoutInflater().inflate(R.layout.moredata, null);
		bt = (Button) moreView.findViewById(R.id.bt_load);
		pg = (ProgressBar) moreView.findViewById(R.id.pg);
		myListView.addFooterView(moreView);
		myListView.setAdapter(listItemAdapter);
		/**
		 * 响应Button事件
		 */
		bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pg.setVisibility(View.VISIBLE);
				bt.setVisibility(View.GONE);
				new getData(keyword, dateF, dateT, china, lastVisibleIndex,
						resultOnceQuery).execute();
			}
		});

		/**
		 * 响应List中每一个Item事件
		 */
		myListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				CheckNetwork net = new CheckNetwork(search.this);
				boolean net_conn = net.check();
				if (net_conn) {
					String url = listItem.get(arg2).get("url").toString();
					Bundle bundle = new Bundle();
					Intent intent = new Intent(search.this, jutixinwen.class);
					bundle.putString("url", url);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});

		myListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastVisibleIndex = firstVisibleItem + visibleItemCount;
				Log.d(TAG, "totalItemCount:" + totalItemCount
						+ " lastVisibleIndex:" + lastVisibleIndex
						+ "listItemApdater:" + listItemAdapter.getCount()
						+ "numberOfSearchResult" + numberOfSearchResult);
				// if (totalItemCount >= numberOfSearchResult) {
				// myListView.removeFooterView(moreView);
				// }
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						&& lastVisibleIndex == listItemAdapter.getCount() + 1
						&& lastVisibleIndex < numberOfSearchResult) {
					pg.setVisibility(View.VISIBLE);
					bt.setVisibility(View.GONE);
					new getData(keyword, dateF, dateT, china, lastVisibleIndex,
							resultOnceQuery).execute();
				}
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						&& lastVisibleIndex >= numberOfSearchResult) {
					Toast.makeText(search.this, "已经是最后一条了", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		Button btnButton = (Button) findViewById(R.id.button_dingzhi);
		btnButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(search.this, dingzhi.class);
				Bundle bundle = new Bundle();
				bundle.putString("keyword", keyword);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	/**
	 * 
	 * @return listItem
	 */
	// public List<Map<String, Object>> loadMoreData() {
	// Map<String, Object> searchmap = ConnectWeb.getsearch(keyword, dateF,
	// dateT, "F", 30, resultOnceQuery);
	// List<Map<String, Object>> list = (List<Map<String, Object>>) searchmap
	// .get("first");
	// int count = listItemAdapter.getCount();
	//
	// // if (count+20<MaxDataNum) {
	// if (list.size() == 20) {
	// for (int i = count; i < count + 20; i++) {
	// HashMap<String, Object> map = new HashMap<String, Object>();
	// Map<String, Object> map1 = list.get(i);
	// String source = (String) map1.get("source");
	// map.put("Title", map1.get("title"));
	// map.put("source", source);
	// map.put("icon", getIcon(source));
	// map.put("ItemTime", map1.get("date"));
	// map.put("description", (String) map1.get("description") + i);
	// map.put("url", map1.get("url"));
	// listItem.add(map);
	// }
	// } else {
	// for (int i = count; i < list.size(); i++) {
	// HashMap<String, Object> map = new HashMap<String, Object>();
	// Map<String, Object> map1 = list.get(i);
	// String source = (String) map1.get("source");
	// map.put("Title", map1.get("title"));
	// map.put("source", source);
	// map.put("icon", getIcon(source));
	// map.put("ItemTime", map1.get("date"));
	// map.put("description", (String) map1.get("description") + i);
	// map.put("url", map1.get("url"));
	// listItem.add(map);
	// flagLoadMoreData = 5;
	// }
	//
	// }
	// return listItem;
	// }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return true;
	}

	@Override
	protected void onRestart() {
		Log.d(TAG, "search onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "search onResume");
		super.onResume();
	}

	@Override
	protected void onStart() {
		Log.d(TAG, "search onstart");
		super.onStart();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "search onStop");
		super.onStop();
	}

	// public List<Map<String, Object>> testData()
	// {
	//
	// Map<String,Object> mapt = null;
	// List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
	// for(int i = 0;i<10;i++){
	// mapt = new HashMap<String,Object>();
	// mapt.put("title", i+"");
	// mapt.put("description", i+"hh");
	// list.add(mapt);
	// }
	// return list;
	//
	// }

}