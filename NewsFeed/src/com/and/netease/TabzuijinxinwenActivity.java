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

import com.and.netease.utils.ConnectWeb;
import com.and.netease.utils.DBAdapter;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class TabzuijinxinwenActivity extends ListActivity implements
		OnScrollListener {

	private ArrayList<HashMap<String, String>> listItem;// ������ʾlistview
	private int MaxDataNum;
	private View moreView;
	private Handler handler;
	private SimpleAdapter listItemAdapter;
	private int lastVisibleIndex;
	private Button bt;
	private ProgressBar pg;
	int flag = 0;

	private DBAdapter dbadapter;
	private Cursor c;
	ConnectWeb conn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_zuijinxinwen);

		MaxDataNum = 50;

		moreView = getLayoutInflater().inflate(R.layout.moredata, null);
		bt = (Button) moreView.findViewById(R.id.bt_load);
		pg = (ProgressBar) moreView.findViewById(R.id.pg);
		handler = new Handler();

		dbadapter = new DBAdapter(this);
		dbadapter.open();
		conn = new ConnectWeb(dbadapter);
		conn.getzuijinxinwen();
		/*
		 * for (int i = 0; i < 20; i++) { dbadapter.insert("����ר�����" +
		 * i,"������רý��","2012-11-"+i); }
		 */
		c = dbadapter.getzuijinxinwen(0, MaxDataNum);
		listItem = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < 10 && c.moveToNext(); i++) {
			c.moveToPosition(i);
			String keyWords = c.getString(1);
			String number = c.getString(2);

			HashMap<String, String> map = new HashMap<String, String>();
			map.put("ItemTitle", keyWords);
			map.put("ItemText", number);
			listItem.add(map);
		}

		// ������������Item�Ͷ�̬�����Ӧ��Ԫ��
		listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.zuijinxinwen_item, 
				new String[] { "ItemTitle","ItemText"},
				new int[] { R.id.ItemTitle, R.id.ItemText });
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
						// TODO Auto-generated method stub
						loadMoreData();
						bt.setVisibility(View.VISIBLE);
						pg.setVisibility(View.GONE);
						listItemAdapter.notifyDataSetChanged();
					}
				}, 1000);
			}
		});

		// ��ӵ��
		((PullToRefreshListView) getListView())
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						//EV_BUG  /�����BUG������ˢ��֮�� ���ݿ�û��ˢ�£����Ե�������ɵ�ר���б�ͻ�FC
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

		// ����ˢ��
		((PullToRefreshListView) getListView())
				.setOnRefreshListener(new OnRefreshListener() {
					@Override
					public void onRefresh() {
						// Do work to refresh the list here.
						new GetDataTask().execute();
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
			//�����Ѿ�����5��
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
		// �������ɼ���Ŀ������
		lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
		// ���е���Ŀ�Ѿ������������ȣ����Ƴ��ײ���View
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
//			   �������ײ�ʱ�Զ�����
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
				map.put("ItemTitle", "���ű��� (ˢ�º�)");
				map.put("ItemText", "����ר��ժҪ");
				listItem.add(j, map);
			}
			// listItem.Insert(2, "����");

			// Call onRefreshComplete when the list has been refreshed.
			((PullToRefreshListView) getListView()).onRefreshComplete();

			super.onPostExecute(result);
		}
	}

}
