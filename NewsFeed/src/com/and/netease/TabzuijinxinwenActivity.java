package com.and.netease;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.and.netease.utils.CheckNetwork;
import com.and.netease.utils.ConnectWeb;
import com.and.netease.utils.DBAdapter;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class TabzuijinxinwenActivity extends ListActivity implements
		OnScrollListener {

	private final int MaxDataNum = 50;
	private View moreView;
	private Handler handler;
	private SimpleAdapter listItemAdapter;
	private int lastVisibleIndex;
	private Button bt;
	private ProgressBar pg;
	private int flag = 0;

	private DBAdapter dbadapter;
	private Cursor c;
	private ConnectWeb conn;

	ArrayList<HashMap<String, String>> listItem;
	
	private static final String TAG = "EV_Debug";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_zuijinxinwen);
		Log.d(TAG, "Tabzuijinxinwen onCreate");
		
		moreView = getLayoutInflater().inflate(R.layout.moredata, null);
		bt = (Button) moreView.findViewById(R.id.bt_load);
		pg = (ProgressBar) moreView.findViewById(R.id.pg);
		handler = new Handler();

		dbadapter = new DBAdapter(this);
		dbadapter.open();

		CheckNetwork checknet = new CheckNetwork(this);
		if (checknet.check()) {
			Toast.makeText(this, "网络可用", Toast.LENGTH_SHORT).show();
			conn = new ConnectWeb(dbadapter);
			conn.getzuijinxinwen();
		} else {
			Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
		}

		c = dbadapter.getzuijinxinwen(0, MaxDataNum);
		listItem = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < 10 && c.moveToNext(); i++) {
			c.moveToPosition(i);
			String title = c.getString(1);
			String keywords = c.getString(2);
//			String date = c.getString(3);
//			String counts = c.getString(4);
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("date", "date");
			map.put("counts", "counts");
			map.put("ItemTitle", title);
			map.put("ItemText", "keywords");
			listItem.add(map);
		}

		listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.zuijinxinwen_item, new String[] { "date", "counts",
						"ItemTitle", "ItemText" }, new int[] {
						R.id.textView_datedistribution, R.id.textView_count,
						R.id.ItemTitle, R.id.ItemText });
		// 添加并且显示
		((PullToRefreshListView) getListView()).addFooterView(moreView);
		setListAdapter(listItemAdapter);
		// 加载更多
		((PullToRefreshListView) getListView()).setOnScrollListener(this);
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
						bt.setVisibility(View.VISIBLE);
						pg.setVisibility(View.GONE);
						listItemAdapter.notifyDataSetChanged();
					}
				}, 1000);
			}
		});

		// 添加点击
		((PullToRefreshListView) getListView())
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// EV_BUG /这里的BUG在下拉刷新之后 数据库没有刷新，所以点击新生成的专题列表就会FC
						String title = (String) listItem.get(arg2 - 1).get(
								"ItemTitle");
						Bundle bundle = new Bundle();
						Intent intent = new Intent();
						intent.setClass(TabzuijinxinwenActivity.this,
								zhuanti.class);
						bundle.putString("title", title);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});

		// 下拉刷新
		((PullToRefreshListView) getListView())
				.setOnRefreshListener(new OnRefreshListener() {
					@Override
					public void onRefresh() {
						// Do work to refresh the list here.
						CheckNetwork checknet = new CheckNetwork(
								TabzuijinxinwenActivity.this);
						if (checknet.check()) {
							// Toast.makeText(TabzuijinxinwenActivity.this,
							// "网络可用", Toast.LENGTH_SHORT).show();
							new GetDataTask().execute();
						} else {
							Toast.makeText(TabzuijinxinwenActivity.this,
									"网络不可用", Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	private void loadMoreData() {
		int count = listItemAdapter.getCount();
		if (count + 5 < MaxDataNum) {
			{
				for (int i = count; i < count + 5 && c.moveToNext(); i++) {
					c.moveToPosition(i);
					String text = c.getString(1);
					String time = c.getString(2);

					HashMap<String, String> map = new HashMap<String, String>();
					map.put("ItemTitle", text);
					map.put("ItemText", time);
					listItem.add(map);
				}
			}
		}

		else {
			// 数据已经不足5条
			for (int i = count; i < MaxDataNum && c.moveToNext(); i++) {
				c.moveToPosition(i);
				String text = c.getString(1);
				String time = c.getString(2);

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("ItemTitle", text);
				map.put("ItemText", time);
				listItem.add(map);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		c.close();
		dbadapter.close();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// 计算最后可见条目的索引
		lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
		// 所有的条目已经和最大条数相等，则移除底部的View
		if ((totalItemCount >= MaxDataNum || (c.isAfterLast() == true))
				&& flag == 0) {
			((PullToRefreshListView) getListView()).removeFooterView(moreView);
			Toast.makeText(this, "数据全部加载完成，没有更多数据！", Toast.LENGTH_LONG).show();
			flag = 1;
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
					loadMoreData();
					bt.setVisibility(View.VISIBLE);
					pg.setVisibility(View.GONE);
					listItemAdapter.notifyDataSetChanged();
				}

			}, 1000);
		}
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {

			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {

			for (int j = 0; j < 3; j++) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("ItemTitle", "新闻标题 (刷新后)");
				map.put("ItemText", "新闻专题摘要");
				listItem.add(j, map);
			}
			// listItem.Insert(2, "新闻");

			// Call onRefreshComplete when the list has been refreshed.
			((PullToRefreshListView) getListView()).onRefreshComplete();

			super.onPostExecute(result);
		}
	}

}
