package com.and.netease;

import android.app.Application;

public class Person extends Application{
	
	public String userName = null;
	public String passWord = null;
	public int flag_user;//验证用户是否已经登录
	
	public String getUsername(){
		return this.userName;
	}
	
	public String getPassworr(){
		return this.passWord;
	}
	
	public int getFlag(){
		return this.flag_user;
	}
	
	public void setFlag (int arg){
		this.flag_user = arg;
	}
	
	public void setContent(String username,String psw){
		this.userName = username;
		this.passWord = psw;
		
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		this.setContent("s", "a");
	}
	
	
	
	
	
	

}
