package com.and.netease;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.and.netease.utils.ConnectWeb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TabLoginActivity extends Activity {
	private static final String TAG = "Demo";

	
	String str_name;
	public Person person;

	List info_list = new ArrayList();
	ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_login);

		Log.d(TAG, "Login start");
		
		Button loginButton = (Button) findViewById(R.id.button_denglu);
		loginButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if ( Get_login_info()) {
					 str_name =user_info();
					 person.setFlag(1);
						Log.d(TAG,str_name);
					
						Intent intent = new Intent();
						intent.setClass(TabLoginActivity.this, dingzhi.class);						
						Bundle data = new Bundle();
						data.putString("name",str_name);
						intent.putExtras(data);
						String str = (String)intent.getStringExtra("name");
						startActivity(intent);
				
				} else {
					// 提示用户名或密码错误
					Toast.makeText(TabLoginActivity.this, "用户名或密码错误",
							Toast.LENGTH_SHORT).show();
					setNull();
				}

			}
		});

		Button quxiaoButton = (Button) findViewById(R.id.button_quxiao);
		quxiaoButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				setNull();

			}
		});

	}
	
	//清空填入的数据
	public void setNull(){
		EditText edit_name,edit_psw;
		edit_name = (EditText)findViewById(R.id.editText_username);
		edit_name.setText("");
		edit_psw = (EditText)findViewById(R.id.editText_psw);
		edit_psw.setText("");
		
	}

	// 获得用户输入的用户名和密码,判断是否存在该用户名和密码
	public Boolean Get_login_info() {
		EditText UserName = (EditText) findViewById(R.id.editText_username);
		EditText psw = (EditText) findViewById(R.id.editText_psw);
		String str_username = UserName.getText().toString();
		String str_password = psw.getText().toString();
		person = (Person) getApplication();
		person.setContent(str_username, str_password);
		Boolean boo = ConnectWeb.getlogin(str_username,str_password);
		return boo;
	
	}

	public String user_info() {
		return person.getUsername().toString();

	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
}
