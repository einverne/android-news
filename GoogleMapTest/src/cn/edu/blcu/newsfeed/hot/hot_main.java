package cn.edu.blcu.newsfeed.hot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.blcu.newsfeed.R;
import cn.edu.blcu.newsfeed.search.search;
import cn.edu.blcu.newsfeed.utils.ConnectWeb;
import cn.edu.blcu.newsfeed.utils.DBAdapter;

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

	private ArrayList<HashMap<String, Object>> listItem1, listItem2, listItem0;
	private int MaxDataNum = 50;
	private View moreView;
	private Handler handler;
	private Button bt;
	private ProgressBar pg;
	SimpleAdapter listItemAdapter0, listItemAdapter1, listItemAdapter2;

	List<Map<String, Object>> list_People, list_division, list_place;
	int flag = 0;
	int a;

	private DBAdapter dbadapter;
	private Cursor c_people;
	private Cursor c_place;
	private Cursor c_division;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hot_main);
		moreView = getLayoutInflater().inflate(R.layout.moredata, null);
		bt = (Button) moreView.findViewById(R.id.bt_load);
		pg = (ProgressBar) moreView.findViewById(R.id.pg);
		handler = new Handler();

		dbadapter = new DBAdapter(this);
		c_people = dbadapter.getpeople(0, MaxDataNum);
		c_place = dbadapter.getplace(0, MaxDataNum);
		c_division = dbadapter.getdivision(0, MaxDataNum);

		InitImageView();
		InitTextView();
		InitViewPager();
	}

	private void loadMoreData(int x) {
		int count = 0;
		switch (x) {
		case 0:
			count = listItemAdapter0.getCount();
			if (count + 5 < MaxDataNum) {
				for (int i = count; i < count + 5 && c_people.moveToNext(); i++) {
					c_people.moveToPosition(i);
					String text = c_people.getString(c_people
							.getColumnIndex("title"));
					String heat = c_people.getString(c_people
							.getColumnIndex("heat"));
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ItemTitle", text);
					map.put("ItemText", heat);
					listItem0.add(map);
				}
			} else {
				for (int i = count; i < MaxDataNum && c_people.moveToNext(); i++) {
					c_people.moveToPosition(i);
					String text = c_people.getString(c_people
							.getColumnIndex("title"));
					String heat = c_people.getString(c_people
							.getColumnIndex("heat"));
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ItemTitle", text);
					map.put("ItemText", heat);
					listItem0.add(map);
				}
			}
			break;
		case 1:
			count = listItemAdapter1.getCount();
			if (count + 5 < MaxDataNum) {
				for (int i = count; i < count + 5 && c_place.moveToNext(); i++) {
					c_place.moveToPosition(i);
					String text = c_place.getString(c_place
							.getColumnIndex("title"));
					String heat = c_place.getString(c_place
							.getColumnIndex("heat"));
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ItemTitle", text);
					map.put("ItemText", heat);
					listItem1.add(map);
				}
			} else {
				for (int i = count; i < MaxDataNum && c_place.moveToNext(); i++) {
					c_place.moveToPosition(i);
					String text = c_place.getString(c_place
							.getColumnIndex("title"));
					String heat = c_place.getString(c_place
							.getColumnIndex("heat"));
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ItemTitle", text);
					map.put("ItemText", heat);
					listItem1.add(map);
				}
			}
			break;
		case 2:
			count = listItemAdapter2.getCount();
			if (count + 5 < MaxDataNum) {
				for (int i = count; i < count + 5 && c_division.moveToNext(); i++) {
					c_division.moveToPosition(i);
					String text = c_division.getString(c_division
							.getColumnIndex("title"));
					String heat = c_division.getString(c_division
							.getColumnIndex("heat"));
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ItemTitle", text);
					map.put("ItemText", heat);
					listItem2.add(map);
				}
			} else {
				for (int i = count; i < MaxDataNum && c_division.moveToNext(); i++) {
					c_division.moveToPosition(i);
					String text = c_division.getString(c_division
							.getColumnIndex("title"));
					String heat = c_division.getString(c_division
							.getColumnIndex("heat"));
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ItemTitle", text);
					map.put("ItemText", heat);
					listItem2.add(map);
				}
			}
			break;
		}
	}

	public void showlistview(int x, final PullToRefreshListView mylistview) {
		a = x;
		switch (a) {
		case 0: {
			listItem0 = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; c_people.moveToNext(); i++) {
				c_people.moveToPosition(i);
				String text = c_people.getString(c_people
						.getColumnIndex("title"));
				String heat = c_people.getString(c_people
						.getColumnIndex("heat"));

				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ItemTitle", text);
				map.put("ItemText", heat);

				listItem0.add(map);
			}

			listItemAdapter0 = new SimpleAdapter(this, listItem0,
					R.layout.hot_item,
					new String[] { "ItemTitle", "ItemText" }, new int[] {
							R.id.textView_name, R.id.textView_heat });
			// mylistview.addFooterView(moreView);
			mylistview.setAdapter(listItemAdapter0);

			break;
		}
		case 1: {
			listItem1 = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; c_place.moveToNext(); i++) {
				c_place.moveToPosition(i);
				String text = c_place
						.getString(c_place.getColumnIndex("title"));
				String heat = c_place.getString(c_place.getColumnIndex("heat"));

				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ItemTitle", text);
				map.put("ItemText", heat);

				listItem1.add(map);
			}

			listItemAdapter1 = new SimpleAdapter(this, listItem1,
					R.layout.hot_item,
					new String[] { "ItemTitle", "ItemText" }, new int[] {
							R.id.textView_name, R.id.textView_heat });
			// mylistview.addFooterView(moreView);
			mylistview.setAdapter(listItemAdapter1);

			break;
		}
		case 2: {
			listItem2 = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; c_division.moveToNext(); i++) {
				c_division.moveToPosition(i);
				String text = c_division.getString(c_division
						.getColumnIndex("title"));
				String heat = c_division.getString(c_division
						.getColumnIndex("heat"));

				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ItemTitle", text);
				map.put("ItemText", heat);

				listItem2.add(map);
			}

			listItemAdapter2 = new SimpleAdapter(this, listItem2,
					R.layout.hot_item,
					new String[] { "ItemTitle", "ItemText" }, new int[] {
							R.id.textView_name, R.id.textView_heat });
			// mylistview.addFooterView(moreView);
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

				}, 1000);
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
				HashMap<String, Object> map = (HashMap<String, Object>) mylistview
						.getItemAtPosition(arg2);
				String str_title = (String) map.get("ItemTitle");
				Log.d("zjj", str_title);
				Bundle bundle = new Bundle();
				bundle.putSerializable("keyword", str_title);
				Intent intent = new Intent(hot_main.this, search.class);
				intent.putExtras(bundle);
				startActivity(intent);
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
		// if (totalItemCount >= MaxDataNum && flag == 0) {
		// Toast.makeText(this, "û�������", Toast.LENGTH_SHORT).show();
		// flag = 1;
		// }
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
		private int index = 0;

		public GetDataTask(int i) {
			index = i;
		}

		@Override
		protected String[] doInBackground(Void... params) {
			list_People = ConnectWeb.getpeoplesmore(dbadapter);
			list_place = ConnectWeb.getpeoplesmore(dbadapter);
			list_division = ConnectWeb.getpeoplesmore(dbadapter);
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {

			switch (index) {
			case 0:
				c_people = dbadapter.getpeople(0, MaxDataNum);
				Toast.makeText(hot_main.this, "更新了" + list_People.size() + "条",
						Toast.LENGTH_SHORT).show();

				for (int i = 0; c_people.moveToNext(); i++) {
					c_people.moveToPosition(i);
					String text = c_people.getString(c_people
							.getColumnIndex("title"));
					String heat = c_people.getString(c_people
							.getColumnIndex("heat"));

					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ItemTitle", text);
					map.put("ItemText", heat);

					listItem0.add(map);
				}

				PullToRefreshListView mylistview1 = (PullToRefreshListView) mPager
						.findViewById(R.id.hot_people);
				mylistview1.onRefreshComplete();
				break;
			case 1:
				c_place = dbadapter.getplace(0, MaxDataNum);
				Toast.makeText(hot_main.this, "更新了" + list_place.size() + "条",
						Toast.LENGTH_SHORT).show();

				for (int i = 0; c_people.moveToNext(); i++) {
					c_people.moveToPosition(i);
					String text = c_people.getString(c_people
							.getColumnIndex("title"));
					String heat = c_people.getString(c_people
							.getColumnIndex("heat"));

					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ItemTitle", text);
					map.put("ItemText", heat);

					listItem0.add(map);
				}

				PullToRefreshListView mylistview_place = (PullToRefreshListView) mPager
						.findViewById(R.id.hot_place);
				mylistview_place.onRefreshComplete();
				break;
			case 2:
				c_division = dbadapter.getdivision(0, MaxDataNum);
				Toast.makeText(hot_main.this,
						"更新了" + list_division.size() + "条", Toast.LENGTH_SHORT)
						.show();

				for (int i = 0; c_people.moveToNext(); i++) {
					c_people.moveToPosition(i);
					String text = c_people.getString(c_people
							.getColumnIndex("title"));
					String heat = c_people.getString(c_people
							.getColumnIndex("heat"));

					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ItemTitle", text);
					map.put("ItemText", heat);

					listItem0.add(map);
				}

				PullToRefreshListView mylistview_division = (PullToRefreshListView) mPager
						.findViewById(R.id.hot_division);
				mylistview_division.onRefreshComplete();
				break;
			}

			super.onPostExecute(result);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		c_people.close();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

}