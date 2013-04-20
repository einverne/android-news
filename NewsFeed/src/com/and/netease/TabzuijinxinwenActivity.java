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

	private DBAdapter dbadapter;
	private Cursor c;

	ArrayList<HashMap<String, String>> listItem;
	private String yesterday_date; // yyyy-MM-dd

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

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date yesterday = new Date(System.currentTimeMillis() - 2*24 * 60 * 60
				* 1000);
		yesterday_date = formatter.format(yesterday);
		Log.d(TAG, "yesterday:" + yesterday_date);

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
		// ��Ӳ�����ʾ
		((PullToRefreshListView) getListView()).addFooterView(moreView);
		setListAdapter(listItemAdapter);

		// ���ظ���
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

		// ��ӵ��
		((PullToRefreshListView) getListView())
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						// EV_BUG /�����BUG������ˢ��֮�� ���ݿ�û��ˢ�£����Ե�������ɵ�ר���б�ͻ�FC
						Log.d(TAG, "zuijinxinwen�е��Item���:"+position);
						String title = (String) listItem.get(position - 1).get(
								"ItemTitle");
						Bundle bundle = new Bundle();
						Intent intent = new Intent(
								TabzuijinxinwenActivity.this, zhuanti.class);
						Log.d(TAG, "���ݵ�ר������" + title);
						bundle.putString("title", title);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});

		// ����ˢ��
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
									"���粻����,��������״̬", Toast.LENGTH_SHORT).show();
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
			// �����Ѿ�����5��
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

	// view ���滬��״̬����ͼ
	// firstVisibleItem ���ӵĵ�һ���б��������
	// visibleItemCount ���ӵ��б������
	// totalItemCount �ܹ����б������
	// �������ɼ���Ŀ������
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		lastVisibleIndex = firstVisibleItem + visibleItemCount;
		// ���е���Ŀ�Ѿ������������ȣ����Ƴ��ײ���View
		if ((totalItemCount >= MaxDataNum || (c.isAfterLast() == true))) {
			new getOldData().execute(yesterday_date);
			Toast.makeText(this, "���ר���Լ����꣬������ǰר��", Toast.LENGTH_SHORT).show();
		}
	}

	// view ���滬��״̬����ͼ
	// scrollState ����״̬
	// ����״̬����
	// SCROLL_STATE_IDLE : 0 ��ͼû�л���
	// SCROLL_STATE_TOUCH_SCROLL : 1 �û����ڴ�����������ָ������Ļ��
	// SCROLL_STATE_FLING : 2 �û�֮ǰ�����������������ڻ��У�ֱ��ֹͣ
	// �����ײ����Զ����أ��ж�listview�Ѿ�ֹͣ�������������ӵ���Ŀ����adapter����Ŀ
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// OnScrollListener.SCROLL_STATE_IDLE ��ʾListView����
		Log.d(TAG, "lastVisibleIndex:" + lastVisibleIndex + "getCount:"
				+ listItemAdapter.getCount());
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& lastVisibleIndex >= listItemAdapter.getCount()) {
			// �������ײ�ʱ�Զ�����
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

	private class getOldData extends AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(String... params) {
			return new Integer(ConnectWeb.getZhuantiFromDate(dbadapter,
					params[0]));
		}

		@Override
		protected void onPostExecute(Integer result) {
			Log.d(TAG, "TabzuijinxinwenActivity GetDataTask PostExecute");
			Log.d(TAG, "yesterday:"+yesterday_date);
			c = dbadapter.getzuijinxinwenFromDate(yesterday_date);
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
		}

	}

	private class GetDataTask extends AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(String... params) {
			return new Integer(ConnectWeb.getzuijinxinwen(dbadapter)); // ��̨�����������
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
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		c.close();
	}
}
