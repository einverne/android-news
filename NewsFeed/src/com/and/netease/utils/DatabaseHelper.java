package com.and.netease.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * ���ݿ����������ࣺ���ݿ������ ����չSQLiteOpenHelper�࣬����һ��Android�����࣬��Ҫ�������ݿⴴ���Ͱ汾����
 * 
 * @author lzt
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	// ���ݿ���
	private static final String DATABASE_NAME = "NewsFeed";
	// �汾��
	private static final int DATABASE_VERSION = 3;
	// ����

	// ���������������������SQL���
	private static final String DATABASE_CREATE_news = "create table if not exists news (_id integer primary key autoincrement, "
			+ "title text not null, source text,description text,date text,url text,zhuantiId integer,read text);";
	private static final String DATABASE_CREATE_zuijinxinwen = "create table if not exists zuijinxinwen(_id integer primary key autoincrement, "
			+ "words text not null,date text,title text,count integer,read text,UNIQUE (words));";
	private static final String DATABASE_CREATE_peoples = "create table if not exists peoples (_id integer primary key autoincrement, "
			+ "title text not null, heat integer,UNIQUE (title));";
	private static final String DATABASE_CREATE_places = "create table if not exists places (_id integer primary key autoincrement, "
			+ "title text not null, heat integer,UNIQUE (title));";
	private static final String DATABASE_CREATE_divisions = "create table if not exists divisions (_id integer primary key autoincrement, "
			+ "title text not null, heat integer,UNIQUE (title));";
	private static final String DATABASE_CREATE_user = "create table if not exists user(_id integer primary key autoincrement, "
			+ "user text not null,jobname text not null,from1 text,to1 text,count integer,UNIQUE (user,jobname));";
	private static final String DATABASE_CREATE_usernews = "create table if not exists usernews (_id integer primary key autoincrement, "
			+ "title text not null, source text,words text,description text,date text,url text,zhuantiId integer,read text);";
	private static final String DATABASE_CREATE_user_zhuanti = "create table if not exists userzhuanti (_id integer primary key autoincrement, "
			+ "jobname text not null, id text,count text,words text);";

	
	private static final String TAG = "EV_Debug";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// ����Ѿ�ָ�� database�Ѿ����ڣ���ô���������г����ʱ�򣬾Ͳ���ִ����������ˡ�
		db.execSQL(DATABASE_CREATE_news);
		db.execSQL(DATABASE_CREATE_peoples);
		db.execSQL(DATABASE_CREATE_places);
		db.execSQL(DATABASE_CREATE_divisions);
		db.execSQL(DATABASE_CREATE_zuijinxinwen);
		db.execSQL(DATABASE_CREATE_user);
		db.execSQL(DATABASE_CREATE_usernews);
		db.execSQL(DATABASE_CREATE_user_zhuanti);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// onUpgrade()���������������ݿ�:ֻ�����Ǽ򵥵�ɾ����Ȼ���ڴ��������
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS news");
		db.execSQL("DROP TABLE IF EXISTS peopless");
		db.execSQL("DROP TABLE IF EXISTS places");
		db.execSQL("DROP TABLE IF EXISTS divisions");
		db.execSQL("DROP TABLE IF EXISTS zuijinxinwen");
		db.execSQL("DROP TABLE IF EXISTS userzhuanti");
		db.execSQL("DROP TABLE IF EXISTS usernews");
		db.execSQL("DROP TABLE IF EXISTS userzhuanti");
		onCreate(db);
	}

}
