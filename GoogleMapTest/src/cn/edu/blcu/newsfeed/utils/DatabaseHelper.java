package cn.edu.blcu.newsfeed.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * ��ݿ���?������ࣺ��ݿ���?��
 * ����չSQLiteOpenHelper�࣬����һ��Android�����࣬��Ҫ������ݿⴴ���Ͱ汾���?
 * 
 * @author lzt
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	// ��ݿ���
	public static final String DATABASE_NAME = "NewsFeed";
	// �汾��
	public static final int DATABASE_VERSION = 3;
	// ����
	public static final String TABLE_NEWS = "news";
	public static final String TABLE_ZUIJINXINWEN = "zuijinxinwen";
	public static final String TABLE_PEOPLE = "peoples";
	public static final String TABLE_PLACE = "places";
	public static final String TABLE_DIVISION = "divisions";
	public static final String TABLE_USER = "user";
	public static final String TABLE_USERNEWS = "usernews";

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
			+ "user text not null,jobname text not null,count integer,words text,UNIQUE (user,jobname,words));";
	private static final String DATABASE_CREATE_usernews = "create table if not exists usernews (_id integer primary key autoincrement, "
			+ "title text not null, source text,words text,description text,date text,url text,zhuantiId integer,read text);";

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
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// onUpgrade()������������ݿ�:ֻ�����Ǽ򵥵�ɾ��?Ȼ���ڴ��������
		db.execSQL("DROP TABLE IF EXISTS news");
		db.execSQL("DROP TABLE IF EXISTS peopless");
		db.execSQL("DROP TABLE IF EXISTS places");
		db.execSQL("DROP TABLE IF EXISTS divisions");
		db.execSQL("DROP TABLE IF EXISTS zuijinxinwen");
		db.execSQL("DROP TABLE IF EXISTS user");
		db.execSQL("DROP TABLE IF EXISTS usernews");
		onCreate(db);
	}

}
