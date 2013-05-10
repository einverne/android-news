package com.and.netease;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.and.netease.utils.ConnectWeb;

public class TabLoginActivity extends Activity {
	private static final String TAG = "Demo";
	private EditText username;
	private EditText psw;
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	Boolean boo;
	String str_name;

	ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_login);
		Log.d(TAG, "Login start");
		username = (EditText) findViewById(R.id.editText_username);
		psw = (EditText) findViewById(R.id.editText_psw);

		this.sharedPreferences = this.getSharedPreferences("userinfo",
				MODE_PRIVATE);
		editor = sharedPreferences.edit();

		String store_name = sharedPreferences.getString("name", "t");
		String store_psw = sharedPreferences.getString("psw", "t");
		if (store_name != "t") {
			username.setText(store_name);
			psw.setText(store_psw);
		}
		// Intent intent = new Intent();
		// intent.setClass(TabLoginActivity.this, dingzhi.class);
		// Bundle data = new Bundle();
		// data.putString("name",store_name);
		// intent.putExtras(data);
		// String str = (String)intent.getStringExtra("name");
		// startActivity(intent);
		// }

		Button loginButton = (Button) findViewById(R.id.button_denglu);
		loginButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String str_name = username.getText().toString();
				final String str_psw = psw.getText().toString();
				if (str_name != "" && !str_psw.equals(null)) {
					boo = ConnectWeb.getlogin(str_name, str_psw);
					if (boo) {
						Log.d(TAG, "str_name" + str_name + "str_psw" + str_psw);
						editor.putString("name", str_name);
						editor.putString("psw", str_psw);
						editor.commit();
						Intent intent = new Intent(TabLoginActivity.this,
								dingzhi.class);
						Bundle data = new Bundle();
						data.putString("name", str_name);
						intent.putExtras(data);
						startActivity(intent);
					} else {
						// ��ʾ�û������������
						Toast.makeText(TabLoginActivity.this, "�û������������",
								Toast.LENGTH_SHORT).show();
						setNull();
					}
				} else {
					Toast.makeText(TabLoginActivity.this, "�û��������벻��Ϊ��",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		Button quxiaoButton = (Button) findViewById(R.id.button_quxiao);
		quxiaoButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				setNull();
			}
		});

	}

	// ������������
	public void setNull() {
		username.setText("");
		psw.setText("");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
}
