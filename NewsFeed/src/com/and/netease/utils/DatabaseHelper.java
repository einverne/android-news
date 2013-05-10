package com.and.netease.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 数据库管理工具这个类：数据库管理工具 ：扩展SQLiteOpenHelper类，它是一个Android辅助类，主要用于数据库创建和版本管理。
 * 
 * @author lzt
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	// 数据库名
	private static final String DATABASE_NAME = "NewsFeed";
	// 版本名
	private static final int DATABASE_VERSION = 3;
	// 表名

	// 这个常量包括创建所需表的SQL语句
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
		// 如果已经指定 database已经存在，那么在重新运行程序的时候，就不会执行这个方法了。
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
		// onUpgrade()方法用于升级数据库:只不过是简单地删除表，然后在创建表而已
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
