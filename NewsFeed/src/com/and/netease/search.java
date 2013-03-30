package com.and.netease;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import android.widget.Toast;

public class search extends Activity  implements OnScrollListener {
	String url;//获取请求的URL
	private static final String TAG = "Demo";
	private ArrayList<Map<String,Object>> listItem;//用来显示listview
	private List<Map<String,Object>> list;//取数据
	private ListView myListView;
	private int MaxItem;
	private View moreView;
	private Handler handler;
	private SimpleAdapter listItemAdapter;
	private int lastVisibleIndex;
	private Button bt;
	private ProgressBar pg;
	private int MaxDataNum;
	String keyword;
	private DBAdapter dbadapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_search_result);
		Log.d(TAG, "search_result_start");
		
		MaxDataNum=20;
//		// get Intent Bundle
		Bundle bundle = this.getIntent().getExtras();
//		// get data in Bundle
		keyword = bundle.getString("keyword");
		Toast.makeText(this, keyword, Toast.LENGTH_SHORT);
//		// 绑定Layout里面的ListView
		 myListView = (ListView) findViewById(R.id.listView_searchresult);
		 moreView = getLayoutInflater().inflate(R.layout.moredata, null);
			bt = (Button) moreView.findViewById(R.id.bt_load);
			pg = (ProgressBar) moreView.findViewById(R.id.pg);
			handler = new Handler();
			// 生成动态数组，加入数据
		listItem = new ArrayList<Map<String, Object>>();	
//		for (int i = 0; i <= list.size(); i++) {
//			Log.d(TAG,""+i);
//			Map<String, Object>map1=list.get(1);
//			HashMap<String, Object> map = new HashMap<String, Object>();
//			map.put("ItemTitle", (String)map1.get("title") );			
//			map.put("ItemText", (String)map1.get("description"));
//			Log.d("search2",(String)map1.get("description"));
//			map.put("date", (String)map1.get("date") );
//    		listItem.add(map);      
//		}
//		List<Map<String,Object>> jbxx = new ArrayList<Map<String,Object>>();
	
		
		
		// 生成适配器的Item和动态数组对应的元素
		 listItemAdapter = new SimpleAdapter(this,getData(),
				R.layout.zuijinxinwen_item, new String[] {  
						"ItemTitle", "ItemText"}, new int[] {  
						R.id.ItemTitle, R.id.ItemText });
		// 添加并且显示
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
						bt.setVisibility(View.VISIBLE);
						pg.setVisibility(View.GONE);
						listItemAdapter.notifyDataSetChanged();
					}

				}, 2000);
			}
		});

		// 添加点击
		myListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// Intent intent =new Intent();
				// intent.setClass(search.this, zhuanti.class);
				// startActivity(intent);
				// TabzuijinxinwenActivity.this.finish();
			}
		});

		
		Button btnButton = (Button)findViewById(R.id.button_dingzhi);
		btnButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 將定制的內容傳送到定制界面,中間還要經過高級設置界面
				
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

		public List<Map<String, Object>> loadMoreData() {
		// TODO Auto-generated method stub
//		ConnectWeb conn;
//		conn=new ConnectWeb();
//		List<Map<String, Object>> list=conn.getsearch(keyword,"20120202","20131212","T",1,30);
		search conn = new search();
		List<Map<String,Object>> list = conn.testData();
		int count = listItemAdapter.getCount();
		
		if (count+5<MaxDataNum) {
			for(int i = count; i < count+5 ; i++){
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ItemTitle", "新闻标题    " + i);
				map.put("ItemText", "新闻专题摘要");
				listItem.add(map);
			}
		} else {
			// 数据已经不足5条
            for (int i = count; i < MaxDataNum; i++) {
            	HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemTitle", "新闻标题    " + i);
				map.put("ItemText", "新闻专题摘要");
				listItem.add(map);
            }
            
		}
		
		return listItem;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.setClass(search.this, MainActivity.class);
			startActivityForResult(intent, 1);
			search.this.finish();
		}
		return true;
	}
	
	public List<Map<String, Object>> getData()
	{
//		ConnectWeb conn;
//		conn=new ConnectWeb();
//		List<Map<String, Object>> list=conn.getsearch(keyword,"20120202","20131212","T",1,30);
		search conn = new search();
		List<Map<String,Object>> list = conn.testData();
		Log.d(TAG,"isqn2");
		if(list!=null)
		{
		
			for(int i = 0; i < list.size(); i++)
			{
				HashMap<String, Object> map = new HashMap<String, Object>();
				Map<String, Object> map1=list.get(i);
				map.put("ItemTitle", map1.get("title") );		
				map.put("ItemText", (String)map1.get("description")+i);
				listItem.add(map);
			}
			
		}
		else
		{
			Toast.makeText(search.this, "budui",Toast.LENGTH_SHORT).show();
			
		}
		
		return listItem;
		
		
	}
	
	public List<Map<String, Object>> testData()
	{
		
		Map<String,Object> mapt = null;
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for(int i = 0;i<10;i++){	
			mapt = new HashMap<String,Object>();		
			mapt.put("title", i+"");
			mapt.put("description", i+"hh");
			list.add(mapt);
		}
		return list;
	
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// 计算最后可见条目的索引
        lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
        // 所有的条目已经和最大条数相等，则移除底部的View
        if (totalItemCount == MaxDataNum + 1) {
			myListView.removeFooterView(moreView);
            Toast.makeText(this, "数据全部加载完成，没有更多数据！", Toast.LENGTH_LONG).show();
        }
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 滑到底部后自动加载，判断listview已经停止滚动并且最后可视的条目等于adapter的条目
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
                && lastVisibleIndex == listItemAdapter.getCount()) {
            // 当滑到底部时自动加载
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