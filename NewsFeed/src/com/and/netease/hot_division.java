package com.and.netease;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

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

import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;



public class hot_division extends ListActivity implements OnScrollListener {

	
	private ArrayList<HashMap<String, Object>> listItem;
	private int MaxDataNum;
	private View moreView;
	private Handler handler;
	private int lastVisibleIndex;
	private Button bt;
	private ProgressBar pg;
	SimpleAdapter listItemAdapter;
	int flag=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.hot_division);
		
		//Button peopleButton = (Button) findViewById(R.id.people2);
		//Button placeButton = (Button) findViewById(R.id.place2);
		
		MaxDataNum = 30;
        moreView = getLayoutInflater().inflate(R.layout.moredata, null);
		bt = (Button) moreView.findViewById(R.id.bt_load);
		pg = (ProgressBar) moreView.findViewById(R.id.pg);
		handler = new Handler();
		
		listItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 10; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemTitle", "����" + i);
			map.put("ItemText", "����ר��ժҪ");
			listItem.add(map);
		}

		// ������������Item�Ͷ�̬�����Ӧ��Ԫ��
		 listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.zuijinxinwen_item, new String[] { "ItemTitle",
						"ItemText" },
				new int[] { R.id.ItemTitle, R.id.ItemText });
		// ��Ӳ�����ʾ
		((PullToRefreshListView) getListView()).addFooterView(moreView);
		setListAdapter(listItemAdapter);
		
		//���ظ���
        
		
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

		
		//����ˢ��
		((PullToRefreshListView) getListView()).setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                new GetDataTask().execute();
            }
        });
	
		// ��ӵ��
        
        ((PullToRefreshListView) getListView()).setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();
				intent.setClass(hot_division.this, zhuanti.class);
				startActivity(intent);
				hot_division.this.finish();
			}
		});
        /*
        peopleButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(hot_division.this,hot_people.class);
				startActivity(intent);
				hot_division.this.finish();
			}
		});
        placeButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(hot_division.this, hot_place.class);
				startActivity(intent);
				hot_division.this.finish();
			}
		});
        
        */
        

        
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
        	for(int j = 0; j < 3; j++)
        	{
        	HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemTitle", "ˢ������" );
			map.put("ItemText", "����ר��ժҪ");
			listItem.add(j,map);
        	}
            ((PullToRefreshListView) getListView()).onRefreshComplete();

            super.onPostExecute(result);
        }
    }



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// ����Ƿ��ؼ�,ֱ�ӷ��ص�����
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.setClass(hot_division.this, MainActivity.class);
			startActivity(intent);
			hot_division.this.finish();

		}
		return true;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	private void loadMoreData() {
		// TODO Auto-generated method stub
		int count = listItemAdapter.getCount();
		if (count+5<MaxDataNum) {
			for(int i = count; i < count+5 ; i++){
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ItemTitle", "���ű���    " + i);
				map.put("ItemText", "����ר��ժҪ");
				listItem.add(map);
			}
		} else {
			// �����Ѿ�����5��
            for (int i = count; i < MaxDataNum; i++) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemTitle", "���ű���    " + i);
				map.put("ItemText", "����ר��ժҪ");
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
        if (totalItemCount >= MaxDataNum && flag==0) {
        	((PullToRefreshListView) getListView()).removeFooterView(moreView);
            Toast.makeText(this, "����ȫ��������ɣ�û�и������ݣ�", Toast.LENGTH_LONG).show();
            flag=1;
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
