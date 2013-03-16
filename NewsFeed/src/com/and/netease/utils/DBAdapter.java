//在Android中，数据库存放在 /data/data/PACKAGE_NAME/databases 目录下

package com.and.netease.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
 * 适配器
 */
public class DBAdapter

{
	private static final String TAG = "DBAdapter";
	// 上下文
	private final Context context;
	// 数据库名
	private static final String DATABASE_NAME = "NewsFeed21"  ;
	// 版本名
	private static final int DATABASE_VERSION = 1;
	// 表名
	private static final String DATABASE_TABLE_news = "news";
	private static final String DATABASE_TABLE_peoples = "peoples";
	private static final String DATABASE_TABLE_places = "places";
	private static final String DATABASE_TABLE_divisions = "divisions";
	private static final String DATABASE_TABLE_zuijinxinwen = "zuijinxinwen";

	// 数据库数据格式
	/*
	title
	source
	description
	date
	url*/
	public static final String newsKEY_ROWID = "_id";// 专题带下来的新闻们
	public static final String newsKEY_Title = "title";
	public static final String newsKEY_Source = "source";
	public static final String newsKEY_Date = "date";
	public static final String newsKEY_Description = "description";
	public static final String newsKEY_Url = "url";
	public static final String newsKEY_ZhuantiId= "zhuantiId";

	public static final String zuijinxinwenKEY_ROWID = "_id";// 是最近新闻的专题们
	public static final String zuijinxinwenKEY_Words = "words";
	public static final String zuijinxinwenKEY_Count = "count";

	public static final String peoplesKEY_ROWID = "_id";
	public static final String peoplesKEY_Title = "title";
	public static final String peoplesKEY_Heat = "heat";

	public static final String placesKEY_ROWID = "_id";
	public static final String placesKEY_Title = "title";
	public static final String placesKEY_Heat = "heat";

	public static final String divisionsKEY_ROWID = "_id";
	public static final String divisionsKEY_Title = "title";
	public static final String divisionsKEY_Heat = "heat";

	// 这个常量包括创建所需表的SQL语句
	private static final String DATABASE_CREATE_news = "create table if not exists news (_id integer primary key autoincrement, "
			+ "title text not null, source text,description text,date text,url text,zhuantiId integer);";
	private static final String DATABASE_CREATE_zuijinxinwen = "create table if not exists zuijinxinwen(_id integer primary key autoincrement, "
			+ "words text not null,count integer,UNIQUE (words));";
	private static final String DATABASE_CREATE_peoples = "create table if not exists peoples (_id integer primary key autoincrement, "
			+ "title text not null, heat integer,UNIQUE (title));";
	private static final String DATABASE_CREATE_places = "create table if not exists places (_id integer primary key autoincrement, "
			+ "title text not null, heat integer,UNIQUE (title));";
	private static final String DATABASE_CREATE_divisions = "create table if not exists divisions (_id integer primary key autoincrement, "
			+ "title text not null, heat integer,UNIQUE (title));";

	// 数据库管理工具 ：扩展SQLiteOpenHelper类，它是一个Android辅助类，主要用于数据库创建和版本管理。
	private DatabaseHelper DBHelper;
	// SQLiteDatabase代表一个数据库对象，提供了操作数据库的一些方法
	private SQLiteDatabase db;

	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	// 数据库管理工具这个类：数据库管理工具 ：扩展SQLiteOpenHelper类，它是一个Android辅助类，主要用于数据库创建和版本管理。
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		// 扩展SQLiteOpenHelper类覆盖onCreate() 和 onUpgrade()方法

		@Override
		// 如果已经指定 database已经存在，那么在重新运行程序的时候，就不会执行这个方法了。
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE_news);
			db.execSQL(DATABASE_CREATE_peoples);
			db.execSQL(DATABASE_CREATE_places);
			db.execSQL(DATABASE_CREATE_divisions);
			db.execSQL(DATABASE_CREATE_zuijinxinwen);
			
		}

		@Override
		// onUpgrade()方法用于升级数据库:只不过是简单地删除表，然后在创建表而已
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS news");
			db.execSQL("DROP TABLE IF EXISTS peopless");
			db.execSQL("DROP TABLE IF EXISTS places");
			db.execSQL("DROP TABLE IF EXISTS divisions");
			db.execSQL("DROP TABLE IF EXISTS zuijinxinwen");
			onCreate(db);
		}
	}

	// ---打开数据库---
	public DBAdapter open() throws SQLException {
		Log.d("DBA", "DBA open");
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---关闭数据库---
	public void close() {
		db.close();
		DBHelper.close();
	}

	// ---向数据库中插入一个标题---
	public long insert(String words, int count) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(zuijinxinwenKEY_Words,words);
		initialValues.put(zuijinxinwenKEY_Count, count);
		int temp=0;
		Cursor c=db.query(DATABASE_TABLE_zuijinxinwen,new  String[] {zuijinxinwenKEY_Count},zuijinxinwenKEY_Words+"=?",new  String[] {words},null,null,null,null);
			c.moveToFirst();  
			for  ( int  i =  0 ; i < c.getCount(); i++) {   
			 String  incount= c.getString(0); 
			 if( Integer.parseInt(incount)!=count)
				 temp=db.update(DATABASE_TABLE_zuijinxinwen,initialValues,zuijinxinwenKEY_Words+ " = '" +words+"'", null);
			 else
				 temp=-1;
			 c.moveToNext();  
			}  
			c.close(); 
			if(temp ==0)//如果temp！=0 update了 就不insert
				return db.insert(DATABASE_TABLE_zuijinxinwen, null, initialValues);
			else if(temp<0)
				return temp;
			else
				return 0;
	}

	public long insert(String title, String source, String description, String date,String url, long zhuantiId) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(newsKEY_Title, title);
		initialValues.put(newsKEY_Source, source);
		initialValues.put(newsKEY_Date, date);
		initialValues.put(newsKEY_Description, description);
		initialValues.put(newsKEY_Url, url);
		initialValues.put(newsKEY_ZhuantiId, zhuantiId);
		

		return db.insert(DATABASE_TABLE_news, null, initialValues);
	}
	

	// 热点的数据库插入
	public Cursor rawquery(String str)
	{
	 Cursor c=db.rawQuery(str, null);
		c.moveToFirst();  
		return c;
	}
	public long insertpeople(String title, int heat) //如果有update就是0，如果有insert，就返回位置，如果没有 就返回-1
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(peoplesKEY_Title, title);
		initialValues.put(peoplesKEY_Heat, String.valueOf(heat));
		int temp=0;
	Cursor c=db.query(DATABASE_TABLE_peoples,new  String[] {peoplesKEY_Heat},peoplesKEY_Title+"=?",new  String[] {title},null,null,null,null);
		c.moveToFirst();  
		Log.d("test c count in insertpeople",String.valueOf(c.getCount()));
		for  ( int  i =  0 ; i < c.getCount(); i++) {   
		 String  dbheat= c.getString(0); 
		 Log.d("test dbheat insertpeople",dbheat);
		 Log.d("test heat insertpeople",String.valueOf(heat));
		 if( Integer.parseInt(dbheat)!=heat)
			 temp=db.update(DATABASE_TABLE_peoples,initialValues,peoplesKEY_Title + " = '" +title+"'", null);
		 else
			 temp=-1;
		 c.moveToNext();  
		}  
		c.close(); 
		if(temp ==0)//如果temp！=0 update了 就不insert
			return db.insert(DATABASE_TABLE_peoples, null, initialValues);
		else if(temp<0)
			return temp;
		else
			return 0;
	}

	public long insertplace(String title, int heat) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(placesKEY_Title, title);
		initialValues.put(placesKEY_Heat, heat);
		int temp=0;
		//query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit); 


		Cursor c=db.query(DATABASE_TABLE_places,new  String[] { placesKEY_Heat},placesKEY_Title+"=?",new  String[] {title},null,null,null,null);
		//Cursor c=db.query( "SELECT "+placesKEY_Heat+" FROM "+DATABASE_TABLE_places+" WHERE "+placesKEY_Title+"  = '"+	title+"'", null);
		c.moveToFirst();  
		for  ( int  i =  0 ; i < c.getCount(); i++) {   
		 String  dbheat= c.getString(0); 
		 if( Integer.parseInt(dbheat)!=heat)
			 temp=db.update(DATABASE_TABLE_places,initialValues,placesKEY_Title + " = '" +title+"'", null);
		 else
			 temp=-1;
		 c.moveToNext();  
		}  
		c.close(); 
		if(temp ==0)//如果temp！=0 update了 就不insert
			return db.insert(DATABASE_TABLE_places, null, initialValues);
		else if(temp<0)
			return temp;
		else
			return 0;
	}

	public long insertdivision(String title, int heat) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(divisionsKEY_Title, title);
		initialValues.put(divisionsKEY_Heat, heat);
		int temp=0;
		Cursor c=db.query(DATABASE_TABLE_divisions,new  String[] { divisionsKEY_Heat},divisionsKEY_Title+"=?",new  String[] {title},null,null,null,null);
		c.moveToFirst();  
		for  ( int  i =  0 ; i < c.getCount(); i++) {   
		 String  dbheat= c.getString(0); 
		 if( Integer.parseInt(dbheat)!=heat)
			 temp=db.update(DATABASE_TABLE_divisions,initialValues,divisionsKEY_Title + " = '" +title+"'", null);
		 else
			 temp=-1;
		 c.moveToNext();  
		}  
		c.close(); 
		if(temp ==0)//如果temp！=0 update了 就不insert
			return db.insert(DATABASE_TABLE_divisions, null, initialValues);
		else if(temp<0)
			return temp;
		else
			return 0;
	}

	// ---删除---
	public void deleteAll() {
		db.delete(DATABASE_TABLE_news, newsKEY_ROWID + ">0", null);
		db.delete(DATABASE_TABLE_peoples, peoplesKEY_ROWID + ">0", null);
		db.delete(DATABASE_TABLE_places, placesKEY_ROWID + ">0", null);
		db.delete(DATABASE_TABLE_divisions, divisionsKEY_ROWID + ">0", null);
		db.delete(DATABASE_TABLE_zuijinxinwen, newsKEY_ROWID + ">0", null);
	}
	
	public int deletenews(long Id) {
		return db.delete(DATABASE_TABLE_news, newsKEY_ZhuantiId+ "="+String.valueOf(Id), null);
		
	}

	// -----为最近新闻所调用 desc
	public Cursor getzuijinxinwen(int num1, int num2) {
		Cursor mCursor = db.query(DATABASE_TABLE_zuijinxinwen, new String[] {
				zuijinxinwenKEY_ROWID, zuijinxinwenKEY_Words,
				zuijinxinwenKEY_Count }, null, null,
				null, null, zuijinxinwenKEY_Count + " desc",
				String.valueOf(num1) + "," + String.valueOf(num2));
		if (mCursor != null) {
			mCursor.moveToFirst();

		}

		return mCursor;
	}

	// ---检索一个指定行---
	public Cursor getselectedrow(int rowId) throws SQLException {
		Cursor mCursor = db.query(true, DATABASE_TABLE_zuijinxinwen,
				new String[] { zuijinxinwenKEY_ROWID, zuijinxinwenKEY_Words,
						zuijinxinwenKEY_Count}, zuijinxinwenKEY_ROWID + "="
						+ rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();

		}

		return mCursor;
	}

	public Cursor getnews(String words) throws SQLException {
		Cursor mCursor = null;
		if (words.equals("others"))
			mCursor = db.query(true, DATABASE_TABLE_news, new String[] {
					newsKEY_ROWID, newsKEY_Title, newsKEY_Source, newsKEY_Description, newsKEY_Date, newsKEY_Url, 
					newsKEY_ZhuantiId}, newsKEY_ZhuantiId + "=" + 0, null, null, null, null, null);
		else{
			mCursor =db.query(true, DATABASE_TABLE_zuijinxinwen,
				new String[] { zuijinxinwenKEY_ROWID, zuijinxinwenKEY_Words,
						zuijinxinwenKEY_Count}, zuijinxinwenKEY_Words + "='"
						+ words+"'", null, null, null, null, null);
		
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		String id=(String)mCursor.getString(0);
		mCursor = db.query(true, DATABASE_TABLE_news, new String[] {
				newsKEY_ROWID, newsKEY_Title, newsKEY_Source, newsKEY_Description, newsKEY_Date, newsKEY_Url, 
				newsKEY_ZhuantiId}, newsKEY_ZhuantiId + "=" + id, null, null, null, null, null);}
		if (mCursor != null) 
			mCursor.moveToFirst();
		

		return mCursor;
	}
	// -----为热点activity所调用
	public Cursor getpeople(int num1, int num2) {
		Cursor mCursor = db.query(DATABASE_TABLE_peoples, new String[] {
				peoplesKEY_ROWID, peoplesKEY_Title, peoplesKEY_Heat }, null,
				null, null, null, peoplesKEY_Heat + " desc",
				String.valueOf(num1) + "," + String.valueOf(num2));
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor getplace(int num1, int num2) {
		Cursor mCursor = db.query(DATABASE_TABLE_places, new String[] {
				placesKEY_ROWID, placesKEY_Title, placesKEY_Heat }, null, null,
				null, null, placesKEY_Heat + " desc", String.valueOf(num1)
						+ "," + String.valueOf(num2));
		if (mCursor != null) {
			mCursor.moveToFirst();

		}

		return mCursor;
	}

	public Cursor getdivision(int num1, int num2) {
		Cursor mCursor = db.query(DATABASE_TABLE_divisions, new String[] {
				divisionsKEY_ROWID, divisionsKEY_Title, divisionsKEY_Heat },
				null, null, null, null, divisionsKEY_Heat + " desc",
				String.valueOf(num1) + "," + String.valueOf(num2));
		if (mCursor != null) {
			mCursor.moveToFirst();

		}

		return mCursor;
	}

	// -----为专题activity所调用
	
	public long getselectednews(String words) {
		Cursor mCursor = db.query(true, DATABASE_TABLE_zuijinxinwen,
				new String[] { zuijinxinwenKEY_ROWID, zuijinxinwenKEY_Words,
						zuijinxinwenKEY_Count}, zuijinxinwenKEY_Words + "='"
						+ words+"'", null, null, null, null, null);
		if (mCursor != null) 
			mCursor.moveToFirst();
		
			 String  id= mCursor.getString(0); 
			 return Integer.parseInt(id);
			
		
	}

	 //---删除一个指定标题--- 
	public boolean deleteothers() { 
		return db.delete(DATABASE_TABLE_news,newsKEY_ZhuantiId +"=" +0, null) > 0; 
		}
	
	
	
	
	
	
	
	
	// 一个游标（Cursor）对象，但注意Cursors是不同步的


	
	/*
	 * public Cursor getAllHotspots() { return db.query(DATABASE_TABLE_hotspots,
	 * new String[]
	 * {hotspotsKEY_ROWID,hotspotsKEY_Name,hotspotsKEY_Heat},null,null
	 * ,null,null,hotspotsKEY_Heat); }
	 * 
	 * /*
	 * 
	 * 
	 * //---删除一个指定标题--- public boolean deleteTitle(long rowId) { return
	 * db.delete(DATABASE_TABLE, KEY_ROWID +"=" + rowId, null) > 0; }
	 * //---检索一个指定标题--- public Cursor getTitle(long rowId) throws SQLException {
	 * Cursor mCursor =db.query(true, DATABASE_TABLE, new String[]
	 * {KEY_ROWID,KEY_ItemText,KEY_ItemText},KEY_ROWID + "=" +
	 * rowId,null,null,null,null,null); if (mCursor != null) {
	 * mCursor.moveToFirst();
	 * 
	 * }
	 * 
	 * return mCursor; } //---更新一个标题-- public boolean updateTitle(long rowId,
	 * String name,String summary) { ContentValues args = new ContentValues();
	 * args.put(KEY_ItemText, name); args.put(KEY_ItemText, summary); //
	 * args.put(KEY_PUBLISHER, publisher); return db.update(DATABASE_TABLE,
	 * args,KEY_ROWID + "=" + rowId, null) > 0; }
	 */
}
