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
	String url;// ��ȡ�����URL
	private static final String TAG = "EV_DEBUG";
	private ArrayList<Map<String, Object>> listItem;// ������ʾlistview
	private List<Map<String, Object>> list;// ȡ����
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
		// ���ɶ�̬���飬��������
		listItem = new ArrayList<Map<String, Object>>();
		// ȡ����
		getData();
		if (listItem.size() != 0) {
			// ������������Item�Ͷ�̬�����Ӧ��Ԫ��
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
			// ��Ӳ�����ʾ
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

		// ��ӵ��
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
				// �������ɼ���Ŀ������
				// Log.d(TAG,
				// "firstVisi"+firstVisibleItem+" visibleItemCount:"+visibleItemCount+" total:"+totalItemCount);
				lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
				// ���е���Ŀ�Ѿ������������ȣ����Ƴ��ײ���View
				Log.d(TAG, "totalItemCount:" + totalItemCount + " MaxDataNum"
						+ MaxDataNum);
				if (totalItemCount >= MaxDataNum) {
					myListView.removeFooterView(moreView);
					Toast.makeText(search.this, "����ȫ��������ɣ�û�и������ݣ�",
							Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// �����ײ����Զ����أ��ж�listview�Ѿ�ֹͣ�������������ӵ���Ŀ����adapter����Ŀ
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						&& lastVisibleIndex == listItemAdapter.getCount()) {
					// �������ײ�ʱ�Զ�����
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

		// ��ת���ƽ���
		Button btnButton = (Button) findViewById(R.id.button_dingzhi);
		btnButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// �����Ƶă��݂��͵����ƽ���,���g߀Ҫ���^�߼��O�ý���

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
	 * ȡ����
	 */
	public void getdate() {
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR); // ��ȡ��ǰ���
		mMonth = c.get(Calendar.MONTH) + 1;// ��ȡ��ǰ�·�
		mDay = c.get(Calendar.DAY_OF_MONTH);// ��ȡ��ǰ�·ݵ����ں���
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
	 * ȡ�������
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
		String te = "����" + MaxDataNum + "��";
		text.setText(te);
		// text = "����"+MaxDataNum+"������";
	}

	/**
	 * ��һ��ȡ�������
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
	 * ���ظ���
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
			// �����Ѿ�����5��
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