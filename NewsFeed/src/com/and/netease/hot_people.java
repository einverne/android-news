package com.and.netease;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.database.Cursor;

//import com.and.netease.TabzuijinxinwenActivity.GetDataTask;

import com.and.netease.utils.ConnectWeb;
import com.and.netease.utils.DBAdapter;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class hot_people extends ListActivity implements OnScrollListener {
	private ArrayList<HashMap<String, Object>> listItem;
	private int MaxDataNum;
	private View moreView;
	private Handler handler;
	private int lastVisibleIndex;
	private Button bt;
	private ProgressBar pg;
	SimpleAdapter listItemAdapter;
	int flag = 0;
	private DBAdapter dbadapter;
	private Cursor c;
	ConnectWeb conn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.hot_people);
		// Button divisionButton = (Button) findViewById(R.id.division3);
		// Button placeButton = (Button) findViewById(R.id.place3);
		MaxDataNum = 30;
		moreView = getLayoutInflater().inflate(R.layout.moredata, null);
		bt = (Button) moreView.findViewById(R.id.bt_load);
		pg = (ProgressBar) moreView.findViewById(R.id.pg);
		handler = new Handler();
		listItem = new ArrayList<HashMap<String, Object>>();

		dbadapter = new DBAdapter(this);
		dbadapter.open();
		conn = new ConnectWeb(dbadapter);
		conn.getpeoples();

		c = dbadapter.getpeople(0, MaxDataNum);
		Log.d("db", String.valueOf(c.getPosition()));
		listItem = new ArrayList<HashMap<String, Object>>();

		for (int i = 0; i < 10 && c.moveToNext(); i++) {
			c.moveToPosition(i);
			String text = c.getString(1);
			String time = c.getString(2);

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemTitle", text);
			map.put("ItemText", time);
			listItem.add(map);
			Log.d("db", String.valueOf(c.getPosition()));

		}
		Log.d("db", String.valueOf(c.getPosition()));

		// ������������Item�Ͷ�̬�����Ӧ��Ԫ��
		listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.zuijinxinwen_item, new String[] { "ItemTitle",
						"ItemText" },
				new int[] { R.id.ItemTitle, R.id.ItemText });
		// ��Ӳ�����ʾ
		setListAdapter(listItemAdapter);

		((PullToRefreshListView) getListView()).addFooterView(moreView);

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
						// TODO Auto-generated method stub
						loadMoreData();
						bt.setVisibility(View.VISIBLE);
						pg.setVisibility(View.GONE);
						listItemAdapter.notifyDataSetChanged();
					}

				}, 2000);
			}
		});

		// ����ˢ��
		((PullToRefreshListView) getListView())
				.setOnRefreshListener(new OnRefreshListener() {
					@Override
					public void onRefresh() {
						// Do work to refresh the list here.
						new GetDataTask().execute();
					}
				});

		// ��ӵ��

		((PullToRefreshListView) getListView())
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						Intent intent = new Intent();
						intent.setClass(hot_people.this, zhuanti.class);
						startActivity(intent);
						hot_people.this.finish();
					}
				});

	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

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

			List<Map<String, Object>> list = conn.getpeoplesmore();
			for (int j = 0; j < list.size(); j++) {
				Map<String, Object> map1 = list.get(j);
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ItemTitle", (String) map1.get("title"));
				map.put("ItemText", (String) map1.get("heat"));
				listItem.add(j, map);
			}

			((PullToRefreshListView) getListView()).onRefreshComplete();

			super.onPostExecute(result);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// ����Ƿ��ؼ�,ֱ�ӷ��ص�����
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return true;

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		c.close();
		dbadapter.close();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void loadMoreData() {
		// TODO Auto-generated method stub
		int count = listItemAdapter.getCount();
		if (count + 5 < MaxDataNum) {
			{
				for (int i = count; i < count + 5 && c.moveToNext(); i++) {
					c.moveToPosition(i);
					String text = c.getString(1);
					String time = c.getString(2);

					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ItemTitle", text);
					map.put("ItemText", time);
					listItem.add(map);

				}
			}
		}

		else {
			// �����Ѿ�����5��

			for (int i = count; i < MaxDataNum && c.moveToNext(); i++) {
				c.moveToPosition(i);
				String text = c.getString(1);
				String time = c.getString(2);

				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ItemTitle", text);
				map.put("ItemText", time);
				listItem.add(map);

			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// �������ɼ���Ŀ������
		lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
		// ���е���Ŀ�Ѿ������������ȣ����Ƴ��ײ���View
		Log.d("db3", String.valueOf(c.getPosition()));

		if ((totalItemCount >= MaxDataNum || (c.isAfterLast() == true))
				&& flag == 0) {
			((PullToRefreshListView) getListView()).removeFooterView(moreView);
			Toast.makeText(this, "����ȫ��������ɣ�û�и������ݣ�", Toast.LENGTH_LONG).show();
			flag = 1;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// �����ײ����Զ����أ��ж�listview�Ѿ�ֹͣ�������������ӵ���Ŀ����adapter����Ŀ
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& lastVisibleIndex == listItemAdapter.getCount()) {
			// �������ײ�ʱ�Զ�����
			// pg.setVisibility(View.VISIBLE);
			// bt.setVisibility(View.GONE);
			// handler.postDelayed(new Runnable() {
			//
			// @Override
			// public void run() {
			// loadMoreDate();
			// bt.setVisibility(View.VISIBLE);
			// pg.setVisibility(View.GONE);
			// mSimpleAdapter.notifyDataSetChanged();
			// }
			//
			// }, 2000);
		}
	}
}
