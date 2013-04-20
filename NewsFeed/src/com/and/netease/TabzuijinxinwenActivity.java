package com.and.netease;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

	ArrayList<HashMap<String, String>> listItem;

	private static final String TAG = "EV_DEBUG";

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

		c = dbadapter.getzuijinxinwen(0, MaxDataNum);
		listItem = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < 10 && c.moveToNext(); i++) {
			c.moveToPosition(i);
			String title = c.getString(c.getColumnIndex("title"));
			String words = c.getString(c.getColumnIndex("words"));
			String date = c.getString(c.getColumnIndex("date"));
			String counts = c.getString(c.getColumnIndex("count"));

			HashMap<String, String> map = new HashMap<String, String>();
			map.put("date", date);
			map.put("counts", counts);
			map.put("ItemTitle", title);
			map.put("ItemText", words);
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
						loadMoreData();
						bt.setVisibility(View.VISIBLE);
						pg.setVisibility(View.GONE);
						listItemAdapter.notifyDataSetChanged();
					}
				}, 100);
			}
		});

		// 添加点击
		((PullToRefreshListView) getListView())
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						// EV_BUG /这里的BUG在下拉刷新之后 数据库没有刷新，所以点击新生成的专题列表就会FC
						Log.d(TAG, "zuijinxinwen中点击Item序号:"+position);
						String title = (String)listItem.get(position-1).get("ItemTitle");
						Bundle bundle = new Bundle();
						Intent intent = new Intent(
								TabzuijinxinwenActivity.this, zhuanti.class);
						Log.d(TAG, "传递到专题数据id:"  +"title:"+title);
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
							new GetDataTask().execute("fresh");
						} else {
							Toast.makeText(TabzuijinxinwenActivity.this,
									"网络不可用,请检查联网状态", Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	private void loadMoreData() {
		int count = listItemAdapter.getCount();
		if (count + 10 < MaxDataNum) {
			{
				for (int i = count; i < count + 10 && c.moveToNext(); i++) {
					c.moveToPosition(i);
					String title = c.getString(c.getColumnIndex("title"));
					String words = c.getString(c.getColumnIndex("words"));
					String date = c.getString(c.getColumnIndex("date"));
					String counts = c.getString(c.getColumnIndex("count"));

					HashMap<String, String> map = new HashMap<String, String>();
					map.put("date", date);
					map.put("counts", counts);
					map.put("ItemTitle", title);
					map.put("ItemText", words);
					listItem.add(map);
				}
			}
		} else {
			// 数据已经不足10条
			for (int i = count; i < MaxDataNum && c.moveToNext(); i++) {
				c.moveToPosition(i);
				String title = c.getString(c.getColumnIndex("title"));
				String words = c.getString(c.getColumnIndex("words"));
				String date = c.getString(c.getColumnIndex("date"));
				String counts = c.getString(c.getColumnIndex("count"));

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("date", date);
				map.put("counts", counts);
				map.put("ItemTitle", title);
				map.put("ItemText", words);
				listItem.add(map);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	// view 报告滑动状态的视图
	// firstVisibleItem 可视的第一个列表项的索引
	// visibleItemCount 可视的列表项个数
	// totalItemCount 总共的列表项个数
	// 计算最后可见条目的索引
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
		//所有的条目已经和最大条数相等，则移除底部的View
		if ((totalItemCount >= MaxDataNum || (c.isAfterLast() == true))
				&& flag == 0) {
			((PullToRefreshListView) getListView()).removeFooterView(moreView);
			Toast.makeText(this, "数据全部加载完成，没有更多数据！", Toast.LENGTH_LONG).show();
			flag = 1;
		}
	}

	// view 报告滑动状态的视图
	// scrollState 滑动状态
	// 滑动状态包括
	// SCROLL_STATE_IDLE : 0 视图没有滑动
	// SCROLL_STATE_TOUCH_SCROLL : 1 用户正在触摸滑动，手指仍在屏幕上
	// SCROLL_STATE_FLING : 2 用户之前触摸滑动，现在正在滑行，直到停止
	// 滑到底部后自动加载，判断listview已经停止滚动并且最后可视的条目等于adapter的条目
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// OnScrollListener.SCROLL_STATE_IDLE 表示ListView不动
		Log.d(TAG, "lastVisibleIndex:" + lastVisibleIndex + "getCount:"
				+ listItemAdapter.getCount());
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& lastVisibleIndex >= listItemAdapter.getCount()) {
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
			}, 500);
		}
	}

	private class GetDataTask extends AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(String... params) {
			return new Integer(ConnectWeb.getzuijinxinwen(dbadapter)); // 后台请求最近新闻
		}

		@Override
		protected void onPostExecute(Integer result) {
			Log.d(TAG, "TabzuijinxinwenActivity GetDataTask PostExecute");
			c = dbadapter.getzuijinxinwen(0, MaxDataNum);
			listItem = new ArrayList<HashMap<String, String>>();
			for (int i = 0; i < 10 && c.moveToNext(); i++) {
				c.moveToPosition(i);
				String title = c.getString(c.getColumnIndex("title"));
				String words = c.getString(c.getColumnIndex("words"));
				String date = c.getString(c.getColumnIndex("date"));
				String counts = c.getString(c.getColumnIndex("count"));

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("date", date);
				map.put("counts", counts);
				map.put("ItemTitle", title);
				map.put("ItemText", words);
				listItem.add(map);
			}

			listItemAdapter.notifyDataSetChanged();

			((PullToRefreshListView) getListView()).onRefreshComplete();
			Toast.makeText(TabzuijinxinwenActivity.this, "更新了"+result.toString()+"条", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		c.close();
	}
}
