package com.and.netease;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.R.string;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class dingzhi extends ListActivity {
	private static String TAG = "Demo";
	
    private ArrayList<String> strList = new ArrayList<String>();
    private ArrayList<Integer> idList = new ArrayList<Integer>();
    private ListVIewAdapter ladapter;
    boolean visflag = false;
    ListView lv;
    static String str[] = { "��������1", "��������2", "��������3", "��������4", "��������5", "��������6", "��������7", "��������8",
            "��������9", "��������10", "��������11", "��������12", "��������13" };
    CheckBox cb;
    {
        for (int i = 0; i < str.length; i++) {
            strList.add(i, str[i]);
        }
    }
    
   
        
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dingzhi);
 
        ladapter = new ListVIewAdapter(this);
 
        List list = new ArrayList();
        Map map = new HashMap();
        for (int i=0;i<str.length;i++){
        	map.put("title", "����");
        	map.put("description", "�й�������һЩ����");
        	map.put("s_time", "2012-12-12");
        	map.put("e_time", "2012-12-12");
        	list.add(map);
        }
        
        lv = this.getListView();
        
        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.dingzhi_zhuanti, new String[]{"title","description","s_time","e_time"}, new int[]{R.id.textView_title,R.id.textView_description,R.id.textView_startTime,R.id.textView_endTime});
        lv.setAdapter(adapter);
        
        
        
    //    lv.setAdapter(ladapter);
        lv.setScrollBarStyle(1);
        
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
 
            	
            	Intent intent = new Intent();
            	intent.setClass(dingzhi.this, Dingzhi_zhuanti.class);
            	startActivity(intent);
            	//dingzhi.this.finish();
            	
                //Toast.makeText(dingzhi.this, "λ�ã�" + position,Toast.LENGTH_SHORT).show();
            }
        });
 
        lv.setOnLongClickListener(new OnLongClickListener() {
			
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				
				Toast.makeText(dingzhi.this, "λ�ã�" ,Toast.LENGTH_SHORT).show();
				return false;
			}
		});
    }
 
    class ListVIewAdapter extends BaseAdapter {
 
        Context c;
        LayoutInflater mInflater;
 
        ListVIewAdapter(Context context) {
            c = context;
            mInflater = getLayoutInflater();
        }
 
        @Override
        public int getCount() {
            return strList.size();
        }
 
        @Override
        public Object getItem(int position) {
            return strList.get(position);
        }
        
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            final int pos = position;
            if (convertView == null) {
                System.out.println("convertView==null");
                convertView = mInflater.inflate(R.layout.list_item, null);
            }
            holder.tv = (TextView) convertView.findViewById(R.id.textView);
            holder.cb = (CheckBox) convertView.findViewById(R.id.checkBox);
            holder.tv.setText(strList.get(position));
            holder.cb.setChecked(false);
            System.out.println("setText��" + position);
 
            holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
 
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                        boolean isChecked) {
                    if (isChecked && (!idList.contains(pos))) {
                        idList.add(pos);
                    } else {
                        if (idList.contains(pos)) {
                            idList.remove(Integer.valueOf(pos));
                        }
                    }
 
                    System.out.println(idList.toString());
                    Toast.makeText(dingzhi.this, "id��" + pos,
                            Toast.LENGTH_SHORT).show();
                }
            });
 
            if (visflag) {
                holder.cb.setVisibility(View.VISIBLE);
            } else {
                holder.cb.setVisibility(View.INVISIBLE);
            }
 
            return convertView;
        }
 
        class ViewHolder {
            TextView tv;
            CheckBox cb;
        }
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "��������");
        menu.add(0, 1, 0, "ȷ��ɾ��");
        return super.onCreateOptionsMenu(menu);
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 0: // ��������
        {
            if (visflag == true) {
                visflag = false;
                idList.clear();
            } else {
                visflag = true;
            }
            this.ladapter.notifyDataSetInvalidated();
            break;
        }
        case 1: // ȷ��ɾ��
        {
            if (idList.size() > 0) {
                for (Integer in : idList) {
                    System.out.println("����ȷ��ɾ�� in����������" + in);
                    strList.set(in, "-1");
                }
                Iterator it = strList.iterator();
                while (it.hasNext()) {
                    String ss = (String) it.next();
                    if (ss.trim().equals("-1")) {
                        it.remove();
                    }
                }
 
                idList.clear();
                this.ladapter.notifyDataSetChanged();
            }
            break;
        }
        }
        return super.onOptionsItemSelected(item);
    }

	//@Override
	//public boolean dispatchKeyEvent(KeyEvent event) {
		
	//	Log.d(TAG, "back");
    	
    	//Intent intent = new Intent();
    	//intent.setClass(dingzhi.this, MainActivity.class);
    	//startActivity(intent);
    	//dingzhi.this.finish();
		
		//return super.dispatchKeyEvent(event);
	//}
    
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// ����Ƿ��ؼ�,ֱ�ӷ��ص�����
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			Intent intent = new Intent();
//			intent.setClass(dingzhi.this, MainActivity.class);
//			startActivity(intent);
//			dingzhi.this.finish();
//		}
//		return true;
//	}
}
