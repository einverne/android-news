package com.and.netease;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
	public static TabHost tabHost;
	TabHost.TabSpec tabSpec;
	RadioGroup radioGroup;
	RelativeLayout bottom_layout;
	int startLeft;

	TextView biaoti;
	ImageButton imgButton;

	static int screenwidth;
	private static final String TAG = "EV_Debug";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Log.d(TAG, "MainAct create");

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

		biaoti = (TextView) findViewById(R.id.biaoti);
		CharSequence zuijinxinwenCharSequence = getText(R.string.biaoti_zuijinxinwen);
		biaoti.setText(zuijinxinwenCharSequence);

		screenwidth = getWindowManager().getDefaultDisplay().getWidth();

		imgButton = (ImageButton) findViewById(R.id.imageButton_search);
		imgButton.setOnClickListener(new OnClickListener() {
			// 设置标题栏搜索
			@Override
			public void onClick(View v) {
				tabHost.setCurrentTabByTag("search");
				RadioButton RB = (RadioButton) findViewById(R.id.radio_search);
				RB.setChecked(true);
				Log.d("wwwwwwwinnnn", "innnn");
				CharSequence quanwenCharSequence = getText(R.string.biaoti_quanwen);
				biaoti.setText(quanwenCharSequence);
			}
		});

		// 响应标题栏登陆ImageButton
		ImageButton imagebutton_login = (ImageButton) findViewById(R.id.imageButton_login);
		imagebutton_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tabHost.setCurrentTab(3);

				RadioButton RB = (RadioButton) findViewById(R.id.radio_login);
				RB.setChecked(true);

				CharSequence quanwenCharSequence = getText(R.string.biaoti_denglu);
				biaoti.setText(quanwenCharSequence);
			}
		});

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
				biaoti.setText(zuijinxinwenCharSequence);
				break;
			case R.id.radio_search:
				tabHost.setCurrentTabByTag("search");

				CharSequence quanwenCharSequence = getText(R.string.biaoti_quanwen);
				biaoti.setText(quanwenCharSequence);
				break;
			case R.id.radio_hot:
				tabHost.setCurrentTabByTag("hot");
				CharSequence redianCharSequence = getText(R.string.biaoti_redian);
				biaoti.setText(redianCharSequence);
				break;
			case R.id.radio_login:
				tabHost.setCurrentTabByTag("login");
				CharSequence dengluCharSequence = getText(R.string.biaoti_denglu);
				biaoti.setText(dengluCharSequence);
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
		builder.setTitle("提示");
		builder.setIcon(R.drawable.alert);
		builder.setMessage("确定要退出吗?");
		builder.setPositiveButton("确认",
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

}