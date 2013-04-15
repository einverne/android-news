package com.and.netease;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import android.database.Cursor;
import com.and.netease.utils.ConnectWeb;
import com.and.netease.utils.DBAdapter;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class hot_main extends Activity implements OnScrollListener {

	private ViewPager mPager;
	private List<View> listViews;
	private ImageView cursor;
	private TextView t1, t2, t3;
	private int offset = 0;
	private int currIndex = 0;
	private int bmpW;

	// me
	private ArrayList<HashMap<String, Object>> listItem1, listItem2, listItem0;
	private int MaxDataNum = 30;
	private View moreView;
	private Handler handler;
	private Button bt;
	private ProgressBar pg;
	SimpleAdapter listItemAdapter0, listItemAdapter1, listItemAdapter2;
	int flag = 0;
	// private PullToRefreshListView mylistview;//这里一定要好好处理
	int a;

	// 数据显示
	private DBAdapter dbadapter;
	private Cursor c;
	ConnectWeb conn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hot_main);
		moreView = getLayoutInflater().inflate(R.layout.moredata, null);
		bt = (Button) moreView.findViewById(R.id.bt_load);
		pg = (ProgressBar) moreView.findViewById(R.id.pg);
		handler = new Handler();

		// 显示数据库数据
		dbadapter = new DBAdapter(this);
		dbadapter.open();
		conn.getpeoples(dbadapter);
		c = dbadapter.getpeople(0, MaxDataNum);

		InitImageView();
		InitTextView();
		InitViewPager();
	}

	private void loadMoreData(int x) {
		int count = 0;
		// TODO Auto-generated method stub
		switch (x) {
		case 0:
			count = listItemAdapter0.getCount();
			if (count + 5 < MaxDataNum) {
				for (int i = count; i < count + 5 && c.moveToNext(); i++) {
					c.moveToPosition(i);
					String text = c.getString(1);
					String time = c.getString(2);
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ItemTitle", text);
					map.put("ItemText", time);
					listItem0.add(map);
				}
			} else {
				// 数据已经不足5条
				for (int i = count; i < MaxDataNum && c.moveToNext(); i++) {
					c.moveToPosition(i);
					String text = c.getString(1);
					String time = c.getString(2);
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ItemTitle", text);
					map.put("ItemText", time);
					listItem0.add(map);
				}
			}
			break;
		case 1:
			count = listItemAdapter1.getCount();
			if (count + 5 < MaxDataNum) {
				for (int i = count; i < count + 5; i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ItemTitle", "加载数据    " + i);
					map.put("ItemText", "新闻专题摘要");
					listItem1.add(map);
				}
			} else {
				// 数据已经不足5条
				for (int i = count; i < MaxDataNum; i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ItemTitle", "新闻标题    " + i);
					map.put("ItemText", "新闻专题摘要");
					listItem1.add(map);
				}
			}
			break;
		case 2:
			count = listItemAdapter2.getCount();
			if (count + 5 < MaxDataNum) {
				for (int i = count; i < count + 5; i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ItemTitle", "加载数据    " + i);
					map.put("ItemText", "新闻专题摘要");
					listItem2.add(map);
				}
			} else {
				for (int i = count; i < MaxDataNum; i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ItemTitle", "新闻标题    " + i);
					map.put("ItemText", "新闻专题摘要");
					listItem2.add(map);
				}
			}
			break;
		}
	}

	public void showlistview(int x, PullToRefreshListView mylistview) {
		a = x;
		switch (a) {
		case 0: {
			listItem0 = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < 10 && c.moveToNext(); i++) {
				c.moveToPosition(i);
				String text = c.getString(1);
				String time = c.getString(2);

				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ItemTitle", text);
				map.put("ItemText", time);

				listItem0.add(map);
			}

			// 生成适配器的Item和动态数组对应的元素
			listItemAdapter0 = new SimpleAdapter(this, listItem0,
					R.layout.zuijinxinwen_item, new String[] { "ItemTitle",
							"ItemText" }, new int[] { R.id.ItemTitle,
							R.id.ItemText });
			// 添加并且显示
			// mylistview= (PullToRefreshListView)
			// mPager.findViewById(R.id.hot_people);
			mylistview.addFooterView(moreView);
			mylistview.setAdapter(listItemAdapter0);

			break;
		}
		case 1: {
			listItem1 = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < 10; i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ItemTitle", "地名" + i);
				map.put("ItemText", "新闻专题摘要");
				listItem1.add(map);
			}

			// 生成适配器的Item和动态数组对应的元素
			listItemAdapter1 = new SimpleAdapter(this, listItem1,
					R.layout.zuijinxinwen_item, new String[] { "ItemTitle",
							"ItemText" }, new int[] { R.id.ItemTitle,
							R.id.ItemText });
			// 添加并且显示
			// mylistview= (PullToRefreshListView)
			// mPager.findViewById(R.id.hot_place);
			mylistview.addFooterView(moreView);
			mylistview.setAdapter(listItemAdapter1);

			break;
		}
		case 2: {
			listItem2 = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < 10; i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ItemTitle", "机构名" + i);
				map.put("ItemText", "新闻专题摘要");
				listItem2.add(map);
			}

			// 生成适配器的Item和动态数组对应的元素
			listItemAdapter2 = new SimpleAdapter(this, listItem2,
					R.layout.zuijinxinwen_item, new String[] { "ItemTitle",
							"ItemText" }, new int[] { R.id.ItemTitle,
							R.id.ItemText });
			// 添加并且显示
			// mylistview= (PullToRefreshListView)
			// mPager.findViewById(R.id.hot_division);
			mylistview.addFooterView(moreView);
			mylistview.setAdapter(listItemAdapter2);

			break;

		}
		}

		mylistview.setOnScrollListener(this);
		bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int w = mPager.getCurrentItem();
				final int y = w;
				// TODO Auto-generated method stub
				pg.setVisibility(View.VISIBLE);
				bt.setVisibility(View.GONE);
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {

						Log.d("current pager", String.valueOf(y));
						loadMoreData(y);
						bt.setVisibility(View.VISIBLE);
						pg.setVisibility(View.GONE);
						switch (y) {
						case 0:
							listItemAdapter0.notifyDataSetChanged();
							break;
						case 1:
							listItemAdapter1.notifyDataSetChanged();
							break;
						case 2:
							listItemAdapter2.notifyDataSetChanged();
							break;
						}

					}

				}, 2000);
			}
		});

		mylistview.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// Do work to refresh the list here.
				int y = mPager.getCurrentItem();
				switch (y) {
				case 0:
					new GetDataTask(0).execute();
					break;
				case 1:
					new GetDataTask(1).execute();
					break;
				case 2:
					new GetDataTask(2).execute();
					break;
				}

			}
		});

		mylistview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// Intent intent = new Intent(hot_main.this, zhuanti.class);
				// startActivity(intent);
				Toast.makeText(hot_main.this, "需要傳參數", Toast.LENGTH_LONG)
						.show(); // EV_BUG
			}
		});
	}

	private void InitTextView() {
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		t3 = (TextView) findViewById(R.id.text3);

		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
		t3.setOnClickListener(new MyOnClickListener(2));
	}

	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		listViews.add(getPeopleView(mInflater));
		listViews.add(getPlaceView(mInflater));
		listViews.add(getDivisionView(mInflater));
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a)
				.getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 3 - bmpW) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);
	}

	private View getPeopleView(LayoutInflater mInflater) {
		View view = mInflater.inflate(R.layout.hot_people, null);
		PullToRefreshListView list = (PullToRefreshListView) view
				.findViewById(R.id.hot_people);
		showlistview(0, list);

		return view;
	}

	private View getPlaceView(LayoutInflater mInflater) {
		View view = mInflater.inflate(R.layout.hot_place, null);
		PullToRefreshListView list = (PullToRefreshListView) view
				.findViewById(R.id.hot_place);
		showlistview(1, list);

		return view;
	}

	private View getDivisionView(LayoutInflater mInflater) {
		View view = mInflater.inflate(R.layout.hot_division, null);
		PullToRefreshListView list = (PullToRefreshListView) view
				.findViewById(R.id.hot_division);
		showlistview(2, list);

		return view;
	}

	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {

			index = i;

		}

		@Override
		public void onClick(View v) {

			mPager.setCurrentItem(index);
		}
	};

	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;
		int two = one * 2;

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);

				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			}
			currIndex = arg0;

			animation.setFillAfter(true);
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		// 计算最后可见条目的索引

		// 所有的条目已经和最大条数相等，则移除底部的View
		if (totalItemCount >= MaxDataNum && flag == 0) {
			// 注意这里

			// mylistview1.removeFooterView(moreView);//这里貌似还得进行switch的讨论

			Toast.makeText(this, "数据全部加载完成，没有更多数据！", Toast.LENGTH_LONG).show();
			flag = 1;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
		private int index = 0;

		// private PullToRefreshListView listview;

		public GetDataTask(int i) {
			index = i;

		}

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {

			switch (index) {
			case 0:
				List<Map<String, Object>> list = conn.getpeoplesmore(dbadapter);
				for (int j = 0; j < list.size(); j++) {
					Map<String, Object> map1 = list.get(j);
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ItemTitle", (String) map1.get("title"));
					map.put("ItemText", (String) map1.get("heat"));
					listItem0.add(j, map);
				}

				PullToRefreshListView mylistview1 = (PullToRefreshListView) mPager
						.findViewById(R.id.hot_people);
				mylistview1.onRefreshComplete();
				break;
			case 1:
				for (int j = 0; j < 3; j++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ItemTitle", "刷新内容");
					map.put("ItemText", "新闻专题摘要");
					listItem1.add(j, map);
				}
				PullToRefreshListView mylistview2 = (PullToRefreshListView) mPager
						.findViewById(R.id.hot_place);
				mylistview2.onRefreshComplete();
				break;
			case 2:
				for (int j = 0; j < 3; j++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ItemTitle", "刷新内容");
					map.put("ItemText", "新闻专题摘要");
					listItem2.add(j, map);
				}
				PullToRefreshListView mylistview3 = (PullToRefreshListView) mPager
						.findViewById(R.id.hot_division);
				mylistview3.onRefreshComplete();
				break;

			}

			super.onPostExecute(result);
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

}