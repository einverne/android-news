package cn.edu.blcu.newsfeed.tabactivity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import cn.edu.blcu.newsfeed.R;
import cn.edu.blcu.newsfeed.utils.CheckNetwork;
import cn.edu.blcu.newsfeed.utils.ConnectWeb;
import cn.edu.blcu.newsfeed.utils.DBAdapter;
import cn.edu.blcu.newsfeed.utils.DateTool;
import cn.edu.blcu.newsfeed.zuijinxinwen.zhuanti;

import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class TabzuijinxinwenActivity extends ListActivity implements
		OnScrollListener {

	private View moreView;
	private SimpleAdapter listItemAdapter;
	private int lastVisibleIndex;
	private Button bt;
	private ProgressBar pg;
	private DBAdapter dbadapter;
	private Cursor c;

	ArrayList<HashMap<String, String>> listItem;
	private String queryDate;
	private int days = 3;

	private static final String TAG = "EV_DEBUG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_zuijinxinwen);
		Log.d(TAG, "Tabzuijinxinwen onCreate");

		moreView = getLayoutInflater().inflate(R.layout.moredata, null);
		bt = (Button) moreView.findViewById(R.id.bt_load);
		pg = (ProgressBar) moreView.findViewById(R.id.pg);
		queryDate = DateTool.getDateTodayMinusDay(days);

		dbadapter = new DBAdapter(this);
		c = dbadapter.getzuijinxinwen(0, 50);
		listItem = new ArrayList<HashMap<String, String>>();
		for (int i = 0; c.moveToNext(); i++) {
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
		Log.d(TAG, "listItem oncreate" + listItem.size());

		listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.zuijinxinwen_item, new String[] { "date", "counts",
						"ItemTitle", "ItemText" }, new int[] {
						R.id.textView_datedistribution, R.id.textView_count,
						R.id.count, R.id.ItemText });
		((PullToRefreshListView) getListView()).addFooterView(moreView);
		setListAdapter(listItemAdapter);

		((PullToRefreshListView) getListView()).setOnScrollListener(this);

		bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// pg.setVisibility(View.VISIBLE);
				// bt.setVisibility(View.GONE);
				// handler.postDelayed(new Runnable() {
				//
				// @Override
				// public void run() {
				// loadMoreData();
				// bt.setVisibility(View.VISIBLE);
				// pg.setVisibility(View.GONE);
				// listItemAdapter.notifyDataSetChanged();
				// }
				// }, 200);
			}
		});

		((PullToRefreshListView) getListView())
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						String title = listItem.get(position - 1).get(
								"ItemTitle");
						Bundle bundle = new Bundle();
						Intent intent = new Intent(
								TabzuijinxinwenActivity.this, zhuanti.class);
						title = title.replace("\'", "\'\'");
						bundle.putString("title", title);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});

		((PullToRefreshListView) getListView())
				.setOnRefreshListener(new OnRefreshListener() {
					@Override
					public void onRefresh() {
						// Do work to refresh the list here.
						CheckNetwork checknet = new CheckNetwork(
								TabzuijinxinwenActivity.this);
						if (checknet.check()) {
							new GetDataTask("fresh").execute();
						} else {
							Toast.makeText(TabzuijinxinwenActivity.this,
									"检查联网状态̬", Toast.LENGTH_SHORT).show();
							((PullToRefreshListView) TabzuijinxinwenActivity.this
									.getListView()).onRefreshComplete();
						}
					}
				});

	}

	@Override
	protected void onStart() {
		Log.d(TAG, "TabzuijinxinwenActivity onStart");
		super.onStart();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "Tabzuijinxinwen Activity onKeydown");
		return false;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
		// ���е���Ŀ�Ѿ������������ȣ����Ƴ�ײ���View
		// Log.d(TAG, "totalItemCount:" + totalItemCount + " lastVisibleIndex"
		// + lastVisibleIndex);
		// if ((totalItemCount >= 50 || (c.isAfterLast() == true))
		// && flag == 0) {
		// ((PullToRefreshListView) getListView()).removeFooterView(moreView);
		// Toast.makeText(this, "���ȫ��������ɣ�û�и����ݣ�",
		// Toast.LENGTH_LONG).show();
		// flag = 1;
		// }
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		Log.d(TAG, "lastVisibleIndex:" + lastVisibleIndex
				+ "ItemAdapter getCount:" + listItemAdapter.getCount());
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& lastVisibleIndex >= listItemAdapter.getCount()) {
			pg.setVisibility(View.VISIBLE);
			bt.setVisibility(View.GONE);
			new GetDataTask(queryDate).execute();
			queryDate = DateTool.getDateTodayMinusDay(++days);
		}
	}

	private class GetDataTask extends AsyncTask<Void, Void, Integer> {
		String date;

		public GetDataTask(String date) {
			this.date = date;
		}

		@Override
		protected void onPreExecute() {
			Log.d(TAG, "GetDataTask pre");
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			int count;
			if (date != "fresh") {
				count = ConnectWeb.getZhuantiFromDate(dbadapter, date);
			} else {
				count = ConnectWeb.getzuijinxinwen(dbadapter);
			}
			return count;
		}

		@Override
		protected void onPostExecute(Integer result) {
			Log.d(TAG, "TabzuijinxinwenActivity GetDataTask PostExecute");
			c = dbadapter.getzuijinxinwenFromDate(date);
			for (int i = 0; c.moveToNext(); i++) {
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
			Log.d(TAG, "listItem size" + listItem.size());
			if (date == "fresh") {
				((PullToRefreshListView) getListView()).onRefreshComplete();
			}
			Toast.makeText(TabzuijinxinwenActivity.this,
					"共更新了" + result.toString() + "条", Toast.LENGTH_SHORT)
					.show();
			super.onPostExecute(result);
		}

	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "Tabzuijinxinwen Destroy");
		super.onDestroy();
		c.close();
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "TabzuijinxinwenActivity onPause");
		super.onPause();
	}

	@Override
	protected void onRestart() {
		Log.d(TAG, "TabzuijinxinwenActivity onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "TabzuijinxinwenActivity onResume");
		super.onResume();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "TabzuijinxinwenActivity onStop");
		super.onStop();
	}
}
