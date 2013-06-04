package cn.edu.blcu.newsfeed.dingzhi;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import cn.edu.blcu.newsfeed.R;
import cn.edu.blcu.newsfeed.activity.jutixinwen;
import cn.edu.blcu.newsfeed.utils.CheckNetwork;
import cn.edu.blcu.newsfeed.utils.DBAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;


/**
 * @author einverne
 * 
 */
public class dingzhi_xinwen extends SherlockListActivity {

	private static final String TAG = "EV_DEBUG";
	private static final String SOURCE = "source";
	private static final String TITLE = "title";
	private static final String WORDS = "words";
	private static final String DESCRIPTION = "description";
	private static final String DATE = "date";
	private static final String URL = "url";
	private ArrayList<HashMap<String, Object>> list;
	DBAdapter dbAdapter;
	SimpleAdapter listAdapter;
	Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_zhuanti);

		ActionBar actionBar = this.getSupportActionBar();
		actionBar.setTitle(R.string.actionbar_dingzhi);

		list = new ArrayList<HashMap<String, Object>>();
		dbAdapter = new DBAdapter(this);
		Intent intent = this.getIntent();
		String id = intent.getStringExtra("ID");
		Log.d(TAG, "id:" + id);
		cursor = dbAdapter.getUserZhuanti(Long.parseLong(id));
		if (cursor != null) {
			for (int i = 0; cursor.moveToNext(); i++) {
				cursor.moveToPosition(i);
				String title = cursor.getString(cursor.getColumnIndex(TITLE));
				String source = cursor.getString(cursor.getColumnIndex(SOURCE));
				String words = cursor.getString(cursor.getColumnIndex(WORDS));
				String description = cursor.getString(cursor
						.getColumnIndex(DESCRIPTION));
				String date = cursor.getString(cursor.getColumnIndex(DATE));
				String url = cursor.getString(cursor.getColumnIndex(URL));

				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("icon", getIcon(source));
				map.put("source", source);
				map.put("ItemTime", date);
				map.put("Title", title);
				map.put("description", description);
				list.add(map);
			}
		}

		listAdapter = new SimpleAdapter(this, list, R.layout.zhuanti_item,
				new String[] { "icon", "source", "ItemTime", "Title",
						"description", "ItemMedio" }, new int[] {
						R.id.imageView_icon, R.id.textView_source,
						R.id.textView_ItemTime, R.id.Title, R.id.ItemDes });
		setListAdapter(listAdapter);
		getListView()
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						CheckNetwork net = new CheckNetwork(dingzhi_xinwen.this);
						boolean net_conn = net.check();
						if (net_conn) {
							cursor.moveToPosition(arg2);
							String url = cursor.getString(cursor
									.getColumnIndex(URL));
							Bundle bundle = new Bundle();
							Intent intent = new Intent(dingzhi_xinwen.this,
									jutixinwen.class);
							bundle.putString(URL, url);
							intent.putExtras(bundle);
							startActivity(intent);
						} else {
							Toast.makeText(dingzhi_xinwen.this, "请检查联网状态",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
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
	protected void onDestroy() {
		super.onDestroy();
	}

}
