package cn.edu.blcu.newsfeed.search;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.blcu.newsfeed.R;
import cn.edu.blcu.newsfeed.activity.jutixinwen;
import cn.edu.blcu.newsfeed.utils.CheckNetwork;
import cn.edu.blcu.newsfeed.utils.ConnectWeb;
import cn.edu.blcu.newsfeed.utils.MakeQuery;


public class search extends Activity {
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;

	public class getData extends
			AsyncTask<Void, Void, ArrayList<HashMap<String, Object>>> {
		private String aboutChina;
		private String dateF;
		private String dateT;
		private String keyword;
		private int max;
		private int start;

		public getData(String keyword, String dateF, String dateT,
				String aboutChina, int start, int max) {
			this.keyword = keyword;
			this.dateF = dateF;
			this.dateT = dateT;
			this.aboutChina = aboutChina;
			this.start = start;
			this.max = max;
		}

		@Override
		protected ArrayList<HashMap<String, Object>> doInBackground(
				Void... params) {
			Map<String, Object> searchmap = ConnectWeb.getsearch(keyword,
					dateF, dateT, aboutChina, start, max);
			List<Map<String, Object>> list = (List<Map<String, Object>>) searchmap
					.get("first");
			Log.d("dateF", dateF);
			Log.d("dateT", dateT);
			numberOfSearchResult = (Integer) searchmap.get("second");
			for (int i = 0; i < list.size(); i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				Map<String, Object> map1 = list.get(i);
				String source = (String) map1.get("source");
				map.put("Title", map1.get("title"));
				map.put("source", source);
				map.put("icon", getIcon(source));
				map.put("ItemTime", map1.get("date"));
				map.put("description", map1.get("description"));
				map.put("url", map1.get("url"));
				listItem.add(map);
			}
			if (list.size() < 30) {
				flagLoadMoreData = 5;
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, Object>> result) {
			String te = "共有" + numberOfSearchResult + "条";
			text.setText(te);
			if (numberOfSearchResult == 0) {
				Toast.makeText(search.this, "没有相关新闻，请调整关键字", Toast.LENGTH_SHORT)
						.show();
			}
			listItemAdapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			Toast.makeText(search.this, "正在加载请稍候", Toast.LENGTH_SHORT).show();
			super.onPreExecute();
		}

	}

	final int DATE_DIALOG_ID = 0;
	private static final String TAG = "EV_DEBUG";
	protected static final int MESSAGE_OK = 0x001;
	protected static final int MESSAGE_FALSE = 0x002;
	boolean aboutChina = false;
	private Button bt;
	String china = "F";
	String dateF;
	String dateT;
	String day;
	int flagLoadMoreData = 0;
	String keyword;
	private int lastVisibleIndex;
	private ArrayList<Map<String, Object>> listItem;
	private SimpleAdapter listItemAdapter;
	private int mDay;
	private int mMonth;
	String month;

	private View moreView;
	private int mYear;
	private ListView myListView;
	private int numberOfSearchResult = 0;
	private ProgressBar pg;
	private int resultOnceQuery = 30;
	TextView text;

	String url;
	protected int from_year, from_month, from_day;
	protected int to_year, to_month, to_day;

	/**
	 * get date for search
	 */
	public void getdate() {
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH) + 1;
		mDay = c.get(Calendar.DAY_OF_MONTH);
		String year = mYear + "";
		int year1 = mYear - 1;
		String yearf = year1 + "";
		if (mMonth <= 9) {
			month = "0" + mMonth;
		} else {
			month = "" + mMonth;

		}
		if (mDay <= 9) {
			day = "0" + mDay;
		} else {
			day = mDay + "";
		}
		dateT = year + month + day;
		dateF = yearf + month + day;
		Log.d("Date", dateF + ":" + dateT + ":" + mMonth);
	}

	/**
	 * 20130505
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public String getFormatdate(int year, int month, int day) {
		String m_month, m_day;
		if (month <= 9) {
			m_month = "0" + month;
		} else {
			m_month = "" + month;
		}
		if (day <= 9) {
			m_day = "0" + day;
		} else {
			m_day = day + "";
		}
		return year + m_month + m_day;
	}

	protected int getIcon(String name) {
		Resources res = getResources();
		int id = res.getIdentifier(name, "drawable", getPackageName());
		if (id == 0) {
			return R.drawable.icon;
		} else {
			return id;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_search_result);
		Log.d(TAG, "search_result_Activity_start");

		ActionBar actionBar = this.getActionBar();
		actionBar.setTitle(getText(R.string.title_search_result));

		sharedPreferences = this.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();

		text = (TextView) findViewById(R.id.textView_newstitle);
		myListView = (ListView) findViewById(R.id.listView_searchresult);
		listItem = new ArrayList<Map<String, Object>>();

		Bundle bundle = this.getIntent().getExtras();
		if (bundle.getString("dateF") == null) {
			getdate();
			keyword = bundle.getString("keyword");
		} else {
			dateF = bundle.getString("dateF");
			dateT = bundle.getString("dateT");
			keyword = bundle.getString("keyword");
		}
		if (bundle.getBoolean("aboutChina") != false) {
			aboutChina = bundle.getBoolean("aboutChina");
			Log.d(TAG, "search aboutChina:" + aboutChina);
		}
		if (aboutChina) {
			china = "T";
		}
		new getData(keyword, dateF, dateT, china, 0, resultOnceQuery).execute();

		listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.zhuanti_item, new String[] { "icon", "source",
						"ItemTime", "Title", "description" }, new int[] {
						R.id.imageView_icon, R.id.textView_source,
						R.id.textView_ItemTime, R.id.Title, R.id.ItemDes });
		// load more data
		if (flagLoadMoreData != 5) {

		}
		moreView = getLayoutInflater().inflate(R.layout.moredata, null);
		bt = (Button) moreView.findViewById(R.id.bt_load);
		pg = (ProgressBar) moreView.findViewById(R.id.pg);
		if (numberOfSearchResult < 4)// ����ȡ���ļ�������̫��ʱ���ü��ظ��İ�ť��ʧ��
		{

			bt.setVisibility(View.GONE);
		}
		myListView.addFooterView(moreView);
		myListView.setAdapter(listItemAdapter);
		/**
		 * ��ӦButton�¼�
		 */
		bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pg.setVisibility(View.VISIBLE);
				bt.setVisibility(View.GONE);
				new getData(keyword, dateF, dateT, china, lastVisibleIndex,
						resultOnceQuery).execute();
			}
		});

		/**
		 * ��ӦList��ÿһ��Item�¼�
		 */
		myListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				CheckNetwork net = new CheckNetwork(search.this);
				boolean net_conn = net.check();
				if (net_conn) {
					String url = listItem.get(arg2).get("url").toString();
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
				lastVisibleIndex = firstVisibleItem + visibleItemCount;
				Log.d(TAG, "totalItemCount:" + totalItemCount
						+ " lastVisibleIndex:" + lastVisibleIndex
						+ "listItemApdater:" + listItemAdapter.getCount()
						+ "numberOfSearchResult" + numberOfSearchResult);
				// if (totalItemCount >= numberOfSearchResult) {
				// myListView.removeFooterView(moreView);
				// }
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						&& lastVisibleIndex == listItemAdapter.getCount() + 1
						&& lastVisibleIndex < numberOfSearchResult) {
					pg.setVisibility(View.VISIBLE);
					bt.setVisibility(View.GONE);
					new getData(keyword, dateF, dateT, china, lastVisibleIndex,
							resultOnceQuery).execute();
				}
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						&& lastVisibleIndex >= numberOfSearchResult) {
					Toast.makeText(search.this, "�Ѿ������һ����",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		Button dingzhiButton = (Button) findViewById(R.id.button_dingzhi);
		dingzhiButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String store_name = sharedPreferences.getString("name",
						"t");

				if (store_name == "t") {
					Toast.makeText(search.this, "请先登录", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				View advanced_setting = getLayoutInflater().inflate(
						R.layout.dingzhi_setting, null);
				final AlertDialog dialog = new AlertDialog.Builder(search.this)
						.create();
				dialog.setView(advanced_setting);
				dialog.show();
				final EditText tv_key = (EditText) advanced_setting
						.findViewById(R.id.searchkeyword);
				final CheckBox cb_aboutChina = (CheckBox) advanced_setting
						.findViewById(R.id.checkBox_aboutChina);
				DatePicker from = (DatePicker) advanced_setting
						.findViewById(R.id.datePicker_from);
				DatePicker to = (DatePicker) advanced_setting
						.findViewById(R.id.datePicker_to);
				if (keyword != null) {
					tv_key.setText(keyword);
				}

				cb_aboutChina.setChecked(aboutChina);
				Calendar c = Calendar.getInstance();
				from_year = c.get(Calendar.YEAR);
				from_month = c.get(Calendar.MONTH);
				from_day = c.get(Calendar.DAY_OF_MONTH);
				to_year = c.get(Calendar.YEAR);
				to_month = c.get(Calendar.MONTH);
				to_day = c.get(Calendar.DAY_OF_MONTH);

				from.init(from_year, from_month, from_day,
						new DatePicker.OnDateChangedListener() {

							@Override
							public void onDateChanged(DatePicker view,
									int year, int monthOfYear, int dayOfMonth) {
								from_year = year;
								from_month = monthOfYear;
								from_day = dayOfMonth;
							}
						});
				to.init(to_year, to_month, to_day,
						new DatePicker.OnDateChangedListener() {

							@Override
							public void onDateChanged(DatePicker view,
									int year, int monthOfYear, int dayOfMonth) {
								to_year = year;
								to_month = monthOfYear;
								to_day = dayOfMonth;
							}
						});
				Button bu = (Button) advanced_setting
						.findViewById(R.id.button_advanced_search);
				bu.setOnClickListener(new OnClickListener() {

					private String query;
					private String key;
					private boolean b;

					@Override
					public void onClick(View v) {
						Log.d(TAG, "from:" + from_year + from_month + from_day
								+ "to:" + to_year + to_month + to_day);
						String dateF = getFormatdate(from_year, from_month,
								from_day);
						String dateT = getFormatdate(to_year, to_month, to_day);
						key = tv_key.getText().toString();
						query = MakeQuery.query(key, dateF, dateT);
						china = "false";
						if (cb_aboutChina.isChecked()) {
							china = "true";
						}
						b = false;
						new Thread() {
							@Override
							public void run() {
								try {
									b = ConnectWeb.submitJob(store_name, query,
											china, key);
								} catch (Exception e) {
									Log.d("TAG", e.toString());
								}
								if (b) {
									Message msg_listData = new Message();
									msg_listData.what = MESSAGE_OK;
									handler.sendMessage(msg_listData);
								} else {
									Message msg_listData = new Message();
									msg_listData.what = MESSAGE_FALSE;
									handler.sendMessage(msg_listData);
								}
							}
						}.start();
						dialog.cancel();
					}
				});
			}
		});
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			switch (message.what) {
			case MESSAGE_OK:
				Toast.makeText(search.this, "定制成功", Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_FALSE:
				Toast.makeText(search.this, "定制失败", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return true;
	}

	@Override
	protected void onRestart() {
		Log.d(TAG, "search onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "search onResume");
		super.onResume();
	}

	@Override
	protected void onStart() {
		Log.d(TAG, "search onstart");
		super.onStart();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "search onStop");
		super.onStop();
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