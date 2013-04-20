package com.and.netease;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.and.netease.utils.CheckNetwork;
import com.and.netease.utils.ConnectWeb;
import com.and.netease.utils.DBAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class search extends Activity {
	String url;// 获取请求的URL
	private static final String TAG = "EV_DEBUG";
	private ArrayList<Map<String, Object>> listItem;// 用来显示listview
	private List<Map<String, Object>> list;// 取数据
	private ListView myListView;
	private int MaxItem;
	private View moreView;
	private Handler handler;
	private SimpleAdapter listItemAdapter;
	private int lastVisibleIndex;
	private Button bt;
	private ProgressBar pg;
	private int MaxDataNum;
	private int mYear;
	private int mMonth;
	private int mDay;
	String keyword;
	String dateF;
	String dateT;
	String month;
	String day;
	int flagLoadMoreData = 0;
	private DBAdapter dbadapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_search_result);
		Log.d(TAG, "search_result_Activity_start");
		MaxItem = 30;
		getdate();
		Bundle bundle = this.getIntent().getExtras();
		keyword = bundle.getString("keyword");
		getMaxDataNum();
		myListView = (ListView) findViewById(R.id.listView_searchresult);

		handler = new Handler();
		// 生成动态数组，加入数据
		listItem = new ArrayList<Map<String, Object>>();
		// 取数据
		getData();
		if (listItem.size() != 0) {
			// 生成适配器的Item和动态数组对应的元素
			listItemAdapter = new SimpleAdapter(this, listItem,
					R.layout.zuijinxinwen_item, new String[] { "ItemTitle",
							"ItemText" }, new int[] { R.id.ItemTitle,
							R.id.ItemText });
			// load more data
			if (flagLoadMoreData != 5) {
				moreView = getLayoutInflater().inflate(R.layout.moredata, null);
				bt = (Button) moreView.findViewById(R.id.bt_load);
				pg = (ProgressBar) moreView.findViewById(R.id.pg);
			}
			// 添加并且显示
			myListView.addFooterView(moreView);
			myListView.setAdapter(listItemAdapter);
			bt.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pg.setVisibility(View.VISIBLE);
					bt.setVisibility(View.GONE);
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							loadMoreData();
							// if(flagLoadMoreData!=5){
							bt.setVisibility(View.VISIBLE);
							pg.setVisibility(View.GONE);
							// }
							listItemAdapter.notifyDataSetChanged();
						}

					}, 1000);
				}
			});
		} else {
			Toast.makeText(search.this, "NO DATA!", Toast.LENGTH_SHORT).show();
			search.this.finish();
		}

		// 添加点击
		myListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				CheckNetwork net = new CheckNetwork(search.this);
				boolean net_conn = net.check();
				if (net_conn) {
					String url = listItem.get(arg2).get("url").toString();
					Log.d("wwwwwwww", url);
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
				// 计算最后可见条目的索引
				// Log.d(TAG,
				// "firstVisi"+firstVisibleItem+" visibleItemCount:"+visibleItemCount+" total:"+totalItemCount);
				lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
				// 所有的条目已经和最大条数相等，则移除底部的View
				Log.d(TAG, "totalItemCount:" + totalItemCount + " MaxDataNum"
						+ MaxDataNum);
				if (totalItemCount >= MaxDataNum) {
					myListView.removeFooterView(moreView);
					Toast.makeText(search.this, "数据全部加载完成，没有更多数据！",
							Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 滑到底部后自动加载，判断listview已经停止滚动并且最后可视的条目等于adapter的条目
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						&& lastVisibleIndex == listItemAdapter.getCount()) {
					// 当滑到底部时自动加载
					pg.setVisibility(View.VISIBLE);
					bt.setVisibility(View.GONE);
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							search.this.loadMoreData();
							bt.setVisibility(View.VISIBLE);
							pg.setVisibility(View.GONE);
							listItemAdapter.notifyDataSetChanged();
						}

					}, 2000);
				}
			}
		});

		// 跳转定制界面
		Button btnButton = (Button) findViewById(R.id.button_dingzhi);
		btnButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 將定制的內容傳送到定制界面,中間還要經過高級設置界面

				Intent intent = new Intent();
				intent.setClass(search.this, dingzhi.class);
				Bundle bundle = new Bundle();
				bundle.putString("keyword", keyword);
				intent.putExtras(bundle);
				startActivity(intent);
				search.this.finish();
			}
		});

	}

	/**
	 * 取日期
	 */
	public void getdate() {
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR); // 获取当前年份
		mMonth = c.get(Calendar.MONTH) + 1;// 获取当前月份
		mDay = c.get(Calendar.DAY_OF_MONTH);// 获取当前月份的日期号码
		String year = mYear + "";
		if (mMonth < 9) {
			month = "0" + mMonth;
		} else {
			month = "" + mMonth;

		}
		if (mDay < 9) {
			day = "0" + mDay;
		} else {
			day = mDay + "";
		}
		dateT = year + month + day;
		dateF = year + "01" + day + "";
		Log.d("Date", dateF + ":" + dateT + ":" + mMonth);
	}

	/**
	 * 
	 * 取最大条数
	 * 
	 */
	public void getMaxDataNum() {
		// ConnectWeb conn;
		// conn = new ConnectWeb();
		MaxDataNum = 50;
		// try {
		// MaxDataNum = ConnectWeb.getsearchcount(keyword, dateF, dateT);
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		TextView text;
		text = (TextView) findViewById(R.id.textView_newstitle);
		String te = "共有" + MaxDataNum + "条";
		text.setText(te);
		// text = "共有"+MaxDataNum+"条新闻";
	}

	/**
	 * 第一次取搜索结果
	 */
	public List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = ConnectWeb.getsearch(keyword, dateF,
				dateT, "F", 0, 30);
		if (list.size() != 0) {
			for (int i = 0; i < list.size(); i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				Map<String, Object> map1 = list.get(i);
				map.put("ItemTitle", map1.get("title"));
				map.put("ItemText", (String) map1.get("description") + i);
				map.put("url", map1.get("url"));
				listItem.add(map);
			}
			if (list.size() < 30) {
				flagLoadMoreData = 5;
			}
		} else {
			Toast.makeText(search.this, "NO DATA!", Toast.LENGTH_SHORT).show();

		}
		return listItem;
	}

	/**
	 * 加载更多
	 * 
	 * @return
	 */
	public List<Map<String, Object>> loadMoreData() {
		List<Map<String, Object>> list = ConnectWeb.getsearch(keyword, dateF,
				dateT, "T", MaxItem, MaxItem + 20);

		int count = listItemAdapter.getCount();

		// if (count+20<MaxDataNum) {
		if (list.size() == 20) {
			for (int i = count; i < count + 20; i++) {

				HashMap<String, Object> map = new HashMap<String, Object>();
				Map<String, Object> map1 = list.get(i);

				map.put("ItemTitle", map1.get("title"));
				map.put("ItemText", (String) map1.get("description") + i);
				map.put("url", map1.get("url"));
				listItem.add(map);
			}
		} else {
			// 数据已经不足5条
			for (int i = count; i < list.size(); i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				Map<String, Object> map1 = list.get(i);
				map.put("ItemTitle", map1.get("title"));
				map.put("ItemText", (String) map1.get("description") + i);
				map.put("url", map1.get("url"));
				listItem.add(map);
				flagLoadMoreData = 5;
			}

		}
		return listItem;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return true;
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