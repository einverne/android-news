package cn.edu.blcu.newsfeed.tabactivity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import cn.edu.blcu.newsfeed.R;
import cn.edu.blcu.newsfeed.hot.hot_main;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
	public static TabHost tabHost;
	TabHost.TabSpec tabSpec;
	RadioGroup radioGroup;
	RelativeLayout bottom_layout;
	int startLeft;

	ImageButton imgButton;

	static int screenwidth;
	private static final String TAG = "EV_Debug";

	ActionBar actionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Log.d(TAG, "MainAct create");

		actionBar = this.getActionBar();

		bottom_layout = (RelativeLayout) findViewById(R.id.layout_bottom);
		tabHost = getTabHost();
		tabHost.addTab(tabHost.newTabSpec("zuijinxinwen")
				.setIndicator("zuijinxinwen")
				.setContent(new Intent(this, TabzuijinxinwenActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("search").setIndicator("search")
				.setContent(new Intent(this, TabSearchActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("hot").setIndicator("hot")
				.setContent(new Intent(this, hot_main.class)));
		tabHost.addTab(tabHost.newTabSpec("login").setIndicator("login")
				.setContent(new Intent(this, TabLoginActivity.class)));

		radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
		radioGroup.setOnCheckedChangeListener(checkedChangeListener);

		CharSequence zuijinxinwenCharSequence = getText(R.string.biaoti_zuijinxinwen);
		actionBar.setTitle(zuijinxinwenCharSequence);

		screenwidth = getWindowManager().getDefaultDisplay().getWidth();

	}

	static public void ChangeTab(int i) {
		switch (i) {
		case 0:
			tabHost.setCurrentTab(0);
			break;
		case 1:
			tabHost.setCurrentTab(1);
			break;
		case 2:
			tabHost.setCurrentTab(2);
			break;
		case 3:
			tabHost.setCurrentTab(3);
			break;
		default:
			break;
		}
	}

	public OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.radio_zuijinxinwen:
				tabHost.setCurrentTabByTag("zuijinxinwen");
				CharSequence zuijinxinwenCharSequence = getText(R.string.biaoti_zuijinxinwen);
				actionBar.setTitle(zuijinxinwenCharSequence);
				break;
			case R.id.radio_search:
				tabHost.setCurrentTabByTag("search");

				CharSequence quanwenCharSequence = getText(R.string.biaoti_quanwen);
				actionBar.setTitle(quanwenCharSequence);
				break;
			case R.id.radio_hot:
				tabHost.setCurrentTabByTag("hot");
				CharSequence redianCharSequence = getText(R.string.biaoti_redian);
				actionBar.setTitle(redianCharSequence);
				break;
			case R.id.radio_login:
				tabHost.setCurrentTabByTag("login");
				CharSequence dengluCharSequence = getText(R.string.biaoti_denglu);
				actionBar.setTitle(dengluCharSequence);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			dialog();
			return false;
		}
		return false;
	}

	protected void dialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("注意");
		builder.setIcon(R.drawable.alert);
		builder.setMessage("你确定要退出吗?");
		builder.setPositiveButton("确定",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				});
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "MainActivity OnDestroy");
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "MainActivity onResume");
		super.onResume();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "MainActivity onStop");
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.search:
			tabHost.setCurrentTabByTag("search");
			RadioButton RB = (RadioButton) findViewById(R.id.radio_search);
			RB.setChecked(true);
			CharSequence quanwenCharSequence = getText(R.string.biaoti_quanwen);
			actionBar.setTitle(quanwenCharSequence);
			break;
		case R.id.login:
			tabHost.setCurrentTab(3);
			RadioButton RB1 = (RadioButton) findViewById(R.id.radio_login);
			RB1.setChecked(true);
			CharSequence quanwenCharSequence1 = getText(R.string.biaoti_denglu);
			actionBar.setTitle(quanwenCharSequence1);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}