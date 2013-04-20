package com.and.netease;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	ConnectWeb cnn;
	String str_name;

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
				
				if (Check_login()) {
					 str_name =user_info();
						Log.d(TAG,str_name);

					
					if (Check_login() == true) {
						Intent intent = new Intent();
						intent.setClass(TabLoginActivity.this, dingzhi.class);						
						Bundle data = new Bundle();
						data.putString("name",str_name);
						intent.putExtras(data);
						String str = (String)intent.getStringExtra("name");
						startActivity(intent);
					}
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

	// 获得用户输入的用户名和密码
	public Person Get_login_info() {
		EditText UserName = (EditText) findViewById(R.id.editText_username);
		EditText psw = (EditText) findViewById(R.id.editText_psw);
		Person person = new Person(UserName.getText().toString(), psw.getText()
				.toString());
		return person;
	}

	// 判断是否存在该用户名
	public Boolean Check_login() {
		Person p = this.Get_login_info();
		Boolean boo = cnn.getlogin(p.userName, p.passWord);
		return boo;
	}

	public String user_info() {

		Person p1 = this.Get_login_info();
		String usrname = p1.userName;
		return usrname;

	}

	/*
	 * 传递该用户名对应的定制内容 public List dingzhi_list(){ List dingzhi_list = new
	 * ArrayList(); if(Check_login()){ HashMap<String, String> map_title=new
	 * HashMap<String, String>(); HashMap<String, String> map_time=new
	 * HashMap<String, String>(); HashMap<String, String> map_about=new
	 * HashMap<String, String>();
	 * 
	 * 
	 * 
	 * } }
	 */

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
}
