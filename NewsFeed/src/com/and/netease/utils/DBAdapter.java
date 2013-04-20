//在Android中，数据库存放在 /data/data/PACKAGE_NAME/databases 目录下

package com.and.netease.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 数据库操作类 适配器
 */
public class DBAdapter

{
	private static final String TAG = "EV_DebugDBAdapter";
	// 上下文
	private final Context context;
	private static final String DATABASE_TABLE_news = "news";
	private static final String DATABASE_TABLE_peoples = "peoples";
	private static final String DATABASE_TABLE_places = "places";
	private static final String DATABASE_TABLE_divisions = "divisions";
	private static final String DATABASE_TABLE_zuijinxinwen = "zuijinxinwen";
	private static final String DATABASE_TABLE_user = "user";
	private static final String DATABASE_TABLE_usernews = "usernews";

	// 数据库数据格式
	/*
	 * title source description date url
	 */
	public static final String newsKEY_ROWID = "_id";// 专题带下来的新闻们
	public static final String newsKEY_Title = "title";
	public static final String newsKEY_Source = "source";
	public static final String newsKEY_Date = "date";
	public static final String newsKEY_Description = "description";
	public static final String newsKEY_Url = "url";
	public static final String newsKEY_ZhuantiId = "zhuantiId";

	public static final String zuijinxinwenKEY_ROWID = "_id";// 是最近新闻的专题们
	public static final String zuijinxinwenKEY_Words = "words";		//专题关键字
	public static final String zuijinxinwenKEY_Count = "count";		//
	public static final String zuijinxinwenKEY_Title = "title";		//
	public static final String zuijinxinwenKEY_Date = "date";		//专题日期
	
	public static final String userKEY_ROWID = "_id";// 是最近新闻的专题们
	public static final String userKEY_To = "to1";		//专题关键字
	public static final String userKEY_Count = "count";		//
	public static final String userKEY_From = "from1";		//
	public static final String userKEY_Days = "days";		//专题日期
	public static final String userKEY_user= "user";	
	public static final String userKEY_jobname = "jobname";	


	
	public static final String usernewsKEY_ROWID = "_id";// 专题带下来的新闻们
	public static final String usernewsKEY_Title = "title";
	public static final String usernewsKEY_Source = "source";
	public static final String usernewsKEY_Date = "date";
	public static final String usernewsKEY_Words = "words";
	public static final String usernewsKEY_Description = "description";
	public static final String usernewsKEY_Url = "url";
	public static final String usernewsKEY_ZhuantiId = "zhuantiId";
	
	
	public static final String peoplesKEY_ROWID = "_id";
	public static final String peoplesKEY_Title = "title";
	public static final String peoplesKEY_Heat = "heat";

	public static final String placesKEY_ROWID = "_id";
	public static final String placesKEY_Title = "title";
	public static final String placesKEY_Heat = "heat";

	public static final String divisionsKEY_ROWID = "_id";
	public static final String divisionsKEY_Title = "title";
	public static final String divisionsKEY_Heat = "heat";

	//数据库管理工具 ：扩展SQLiteOpenHelper类，它是一个Android辅助类，主要用于数据库创建和版本管理。
	private DatabaseHelper DBHelper;
	// SQLiteDatabase代表一个数据库对象，提供了操作数据库的一些方法
	private SQLiteDatabase db;

	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}	

	
	/**
	 * ---向数据库中插入一个标题--- 最近新闻
	 * @param words keywords
	 * @param title
	 * @param date
	 * @param count
	 * @return	*/
	public long userinsert(String user, String jobname ,int days,String from,String to,int count) {
		db = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(userKEY_user, user);
		initialValues.put(userKEY_Count, count);
		initialValues.put(userKEY_jobname, jobname);
		initialValues.put(userKEY_To, to);
		initialValues.put(userKEY_From, from);
		initialValues.put(userKEY_Days, days);
		Cursor mCursor = db.query(DATABASE_TABLE_user,
				new String[] { userKEY_ROWID}, userKEY_user
						+ " = '"+user+"'  and "+userKEY_jobname+" = '"+jobname+"'",null, null, null, null, null);
		long result=-1;
		if (mCursor .moveToFirst()!=false) {
				result=-1;
			
		}
		else
		{
			result=db.insert(DATABASE_TABLE_user, null, initialValues);
		}
	
			
		
		db.close();
		DBHelper.close();
		mCursor.close();
		Log.d("test", "after simpleinsert "+String.valueOf(result));
	return result;
	}
	
	public long insert(String words, String title ,String date,int count) {
		db = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(zuijinxinwenKEY_Title, title);
		initialValues.put(zuijinxinwenKEY_Date, date);
		initialValues.put(zuijinxinwenKEY_Words,words);
		initialValues.put(zuijinxinwenKEY_Count, count);
		Cursor mCursor =db.query(true, DATABASE_TABLE_zuijinxinwen,
				new String[] { zuijinxinwenKEY_ROWID,
						zuijinxinwenKEY_Count}, zuijinxinwenKEY_Words+ "=\""
						+ words+"\"", null, null, null, null, null);
		long result=-1;
		if (mCursor .moveToFirst()!=false) {
			int dbcount=Integer.parseInt((String)mCursor.getString(1));
			if (count !=dbcount)
			{
				db.update(DATABASE_TABLE_zuijinxinwen, initialValues,zuijinxinwenKEY_ROWID+"="+(String)mCursor.getString(0) , null);
				db.delete(DATABASE_TABLE_news, newsKEY_ZhuantiId+"="+(String)mCursor.getString(0), null);
				result=Integer.parseInt((String)mCursor.getString(0));
			}else
			{
				result=-1;
			}
			
		}
		else
		{
			result=db.insert(DATABASE_TABLE_zuijinxinwen, null, initialValues);
		}
	
			
		mCursor.close();
		db.close();
		DBHelper.close();
		Log.d("test", "after simpleinsert "+String.valueOf(result));
	return result;
	}

	/**
	 * 
	 * @param title
	 * @param source
	 * @param description
	 * @param date
	 * @param url
	 * @param zhuantiId
	 * @return
	 */
	public long insert(String title, String source, String description,
			String date, String url, long zhuantiId) {
		db = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(newsKEY_Title, title);
		initialValues.put(newsKEY_Source, source);
		initialValues.put(newsKEY_Date, date);
		initialValues.put(newsKEY_Description, description);
		initialValues.put(newsKEY_Url, url);
		initialValues.put(newsKEY_ZhuantiId, zhuantiId);
		
		long result= db.insert(DATABASE_TABLE_news, null, initialValues);
		db.close();
		DBHelper.close();
		return result;
	}
	/**
	 * 
	 * @param title
	 * @param source
	 * @param description
	 * @param date
	 * @param url
	 * @param zhuantiId
	 * @return
	 * 
	 */
	public long userinsertnews(String title, String source, String description,
			String date, String url, String words,long zhuantiId) {
		db = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(usernewsKEY_Title, title);
		initialValues.put(usernewsKEY_Source, source);
		initialValues.put(usernewsKEY_Date, date);
		initialValues.put(usernewsKEY_Description, description);
		initialValues.put(usernewsKEY_Url, url);
		initialValues.put(usernewsKEY_Words, words);
		initialValues.put(usernewsKEY_ZhuantiId, zhuantiId);
		
		long result= db.insert(DATABASE_TABLE_usernews , null, initialValues);
		db.close();
		DBHelper.close();
		return result;
	}




	public long insertpeople(String title, int heat) // 如果有update就是0，如果有insert，就返回位置，如果没有
														// 就返回-1
	{
		db = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(peoplesKEY_Title, title);
		initialValues.put(peoplesKEY_Heat, String.valueOf(heat));
		Cursor mCursor = db.query(DATABASE_TABLE_peoples,
				new String[] { peoplesKEY_ROWID,peoplesKEY_Heat }, peoplesKEY_Title + "=?",
				new String[] { title }, null, null, null, null);
		long result=-1;
		if (mCursor.moveToFirst()!=false) {
			int dbheat=Integer.parseInt((String)mCursor.getString(1));
			if (heat !=dbheat)
			{
				db.update(DATABASE_TABLE_peoples, initialValues,peoplesKEY_ROWID+"="+(String)mCursor.getString(0) , null);
				result=Integer.parseInt((String)mCursor.getString(0));
			}else
			{
				result=-1;
			}
			
		}
		else
		{
			result=db.insert(DATABASE_TABLE_peoples, null, initialValues);
		}
		mCursor.close();
		db.close();
		DBHelper.close();
	return result;
	}

	public long insertplace(String title, int heat) {
		db = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(placesKEY_Title, title);
		initialValues.put(placesKEY_Heat, heat);
	

		Cursor mCursor = db.query(DATABASE_TABLE_places,
				new String[] { placesKEY_ROWID,placesKEY_Heat }, placesKEY_Title + "=?",
				new String[] { title }, null, null, null, null);
		long result=-1;
		if (mCursor.moveToFirst()!=false) {
			mCursor.moveToFirst();
			int dbheat=Integer.parseInt((String)mCursor.getString(1));
			if (heat !=dbheat)
			{
				db.update(DATABASE_TABLE_places, initialValues,placesKEY_ROWID+"="+(String)mCursor.getString(0) , null);
				result=Integer.parseInt((String)mCursor.getString(0));
			}else
			{
				result=-1;
			}
			
		}
		else
		{
			result=db.insert(DATABASE_TABLE_places, null, initialValues);
		}
		mCursor.close();
		db.close();
		DBHelper.close();
	return result;
	}

	public long insertdivision(String title, int heat) {
		db = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(divisionsKEY_Title, title);
		initialValues.put(divisionsKEY_Heat, heat);
		Cursor mCursor = db.query(DATABASE_TABLE_divisions,
				new String[] { divisionsKEY_ROWID,divisionsKEY_Heat }, divisionsKEY_Title + "=?",
				new String[] { title }, null, null, null, null);
		long result=-1;
		if (mCursor.moveToFirst()!=false) {
			mCursor.moveToFirst();
			int dbheat=Integer.parseInt((String)mCursor.getString(1));
			if (heat !=dbheat)
			{
				db.update(DATABASE_TABLE_divisions, initialValues,divisionsKEY_ROWID+"="+(String)mCursor.getString(0) , null);
				result=Integer.parseInt((String)mCursor.getString(0));
			}else
			{
				result=-1;
			}
			
		}
		else
		{
			result=db.insert(DATABASE_TABLE_divisions, null, initialValues);
		}
		mCursor.close();
		db.close();
		DBHelper.close();
	return result;
	}



	// -----为最近新闻所调用 desc
	public Cursor getzuijinxinwen(int num1, int num2) {
		db = DBHelper.getWritableDatabase();
		Cursor mCursor = db.query(DATABASE_TABLE_zuijinxinwen, null, null, null, null, null,
				zuijinxinwenKEY_Date+ " desc"+","+zuijinxinwenKEY_Count+ " desc", String.valueOf(num1) + ","
						+ String.valueOf(num2));
		if (mCursor != null) {
			mCursor.moveToFirst();

		}
		
		db.close();
		DBHelper.close();
		return mCursor;
	}
	


	public Cursor getzuijinxinwenFromDate(String date) {
		db = DBHelper.getWritableDatabase();
		Cursor mCursor = db.query(DATABASE_TABLE_zuijinxinwen, null, zuijinxinwenKEY_Date+"='"+date+"'", null, null, null,
				zuijinxinwenKEY_Count+ " desc", null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		db.close();
		DBHelper.close();
		return mCursor;
	}


	public Cursor getusernews(long userid) {
		db = DBHelper.getWritableDatabase();
		Cursor mCursor = db.query(DATABASE_TABLE_usernews,
				null, usernewsKEY_ZhuantiId+"="+String.valueOf(userid),null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();

		}

		db.close();
		DBHelper.close();
		return mCursor;
	}
	
	public Cursor dingzhizhuanti(String user,String jobname)
	{
		db = DBHelper.getWritableDatabase();
		Cursor mCursor = getuser(user,jobname);
		long userid=Integer.parseInt((String)mCursor.getString(mCursor.getColumnIndex("_id")));
		mCursor.close();
		Cursor mCursor1 = getusernews(userid);
		if (mCursor1 != null) {
			mCursor1.moveToFirst();

		}

		db.close();
		DBHelper.close();
		return mCursor1;

	}
	
	public Cursor getuser(String user,String jobname) {
		db = DBHelper.getWritableDatabase();
		Cursor mCursor = db.query(DATABASE_TABLE_user,
				null, userKEY_user
						+ " = '"+user+"'  and "+userKEY_jobname+" = '"+jobname+"'",null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();

		}

		db.close();
		DBHelper.close();
		return mCursor;
	}
	// ---检索一个指定行---
	public Cursor getselectedrow(int rowId) throws SQLException {
		db = DBHelper.getWritableDatabase();
		Cursor mCursor = db.query(true, DATABASE_TABLE_zuijinxinwen,
				new String[] { zuijinxinwenKEY_ROWID, zuijinxinwenKEY_Words,
						zuijinxinwenKEY_Count }, zuijinxinwenKEY_ROWID + "="
						+ rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();

		}

		db.close();
		DBHelper.close();
		return mCursor;
	}
	public Cursor getnews(int id)
	{
		db = DBHelper.getWritableDatabase();
		Cursor mCursor = db.query(true, DATABASE_TABLE_news,null, newsKEY_ZhuantiId + "="
						+ id, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();

		}

		db.close();
		DBHelper.close();
		return mCursor;
	}
	
	public Cursor getnews(String words) throws SQLException {
		db = DBHelper.getWritableDatabase();
		Cursor mCursor = null;
		if (words.equals("others"))
			mCursor = db.query(true, DATABASE_TABLE_news, new String[] {
					newsKEY_ROWID, newsKEY_Title, newsKEY_Source,
					newsKEY_Description, newsKEY_Date, newsKEY_Url,
					newsKEY_ZhuantiId }, newsKEY_ZhuantiId + "=" + 0, null,
					null, null, null, null);
		else {
			mCursor = db.query(true, DATABASE_TABLE_zuijinxinwen, null, zuijinxinwenKEY_Title + "=\""
					+ words + "\"", null, null, null, null, null);

			if (mCursor != null) {
				mCursor.moveToFirst();
			}
			String id = (String) mCursor.getString(mCursor.getColumnIndex("_id"));
			Log.d(TAG, "getString函数:"+id);
			String[] news_columns = new String[] {
					newsKEY_ROWID, newsKEY_Title, newsKEY_Source,
					newsKEY_Description, newsKEY_Date, newsKEY_Url,
					newsKEY_ZhuantiId };
			mCursor = db.query(true, DATABASE_TABLE_news, news_columns, newsKEY_ZhuantiId + "=" + id, null,
					null, null, null, null);
		}
		if (mCursor != null)
			mCursor.moveToFirst();

		db.close();
		DBHelper.close();
		return mCursor;
	}

	// -----为热点activity所调用
	public Cursor getpeople(int num1, int num2) {
		db = DBHelper.getWritableDatabase();
		Cursor mCursor = db.query(DATABASE_TABLE_peoples, new String[] {
				peoplesKEY_ROWID, peoplesKEY_Title, peoplesKEY_Heat }, null,
				null, null, null, peoplesKEY_Heat + " desc",
				String.valueOf(num1) + "," + String.valueOf(num2));
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		db.close();
		DBHelper.close();
		return mCursor;
	}

	public Cursor getplace(int num1, int num2) {
		db = DBHelper.getWritableDatabase();
		Cursor mCursor = db.query(DATABASE_TABLE_places, new String[] {
				placesKEY_ROWID, placesKEY_Title, placesKEY_Heat }, null, null,
				null, null, placesKEY_Heat + " desc", String.valueOf(num1)
						+ "," + String.valueOf(num2));
		if (mCursor != null) {
			mCursor.moveToFirst();

		}

		db.close();
		DBHelper.close();
		return mCursor;
	}

	public Cursor getdivision(int num1, int num2) {
		db = DBHelper.getWritableDatabase();
		Cursor mCursor = db.query(DATABASE_TABLE_divisions, new String[] {
				divisionsKEY_ROWID, divisionsKEY_Title, divisionsKEY_Heat },
				null, null, null, null, divisionsKEY_Heat + " desc",
				String.valueOf(num1) + "," + String.valueOf(num2));
		if (mCursor != null) {
			mCursor.moveToFirst();

		}
		db.close();
		DBHelper.close();
		return mCursor;
	}

	// -----为专题activity所调用

	public long getselectednews(String words) {
		db = DBHelper.getWritableDatabase();
		Cursor mCursor = db.query(true, DATABASE_TABLE_zuijinxinwen,
				new String[] { zuijinxinwenKEY_ROWID, zuijinxinwenKEY_Words,
						zuijinxinwenKEY_Count }, zuijinxinwenKEY_Words + "='"
						+ words + "'", null, null, null, null, null);
		if (mCursor != null)
			mCursor.moveToFirst();

		String id = mCursor.getString(0);
		int result=Integer.parseInt(id);
		db.close();
		DBHelper.close();
		return result;

	}

	// ---删除一个指定标题---


	// ---删除---

		//--按时间排序，删除到只剩前30条,如果本身不足30条，比如只有10条，就只剩10条
		private void delete_news()
		{
			Cursor mCursor = db.query(DATABASE_TABLE_news, null, newsKEY_ZhuantiId+"<30", null,
					null, null, null,null);
			if (mCursor != null) 
				mCursor.moveToFirst();
			do{
				ContentValues initialValues = new ContentValues();
				initialValues.put(newsKEY_Source, mCursor.getString(1));
				initialValues.put(newsKEY_Date, mCursor.getString(2));
				initialValues.put(newsKEY_Description, mCursor.getString(3));
				initialValues.put(newsKEY_Url, mCursor.getString(4));
				initialValues.put(newsKEY_ZhuantiId, mCursor.getString(5));
				

				db.insert(DATABASE_TABLE_news, null, initialValues);			
			}while(mCursor.moveToNext());
		}
		private void delete_peoples()
		{
			Cursor mCursor = db.query(DATABASE_TABLE_peoples, new String[] {
					peoplesKEY_ROWID, peoplesKEY_Title, peoplesKEY_Heat}, null, null,
					null, null, peoplesKEY_Heat+ " desc","30");
			if (mCursor != null) 
				mCursor.moveToFirst();
			do{
				ContentValues initialValues = new ContentValues();
				initialValues.put(peoplesKEY_Title, mCursor.getString(1));
				initialValues.put(peoplesKEY_Heat, mCursor.getString(2));
				

				db.insert(DATABASE_TABLE_peoples, null, initialValues);			
			}while(mCursor.moveToNext());
		}
		private void delete_divisions()
		{
			Cursor mCursor = db.query(DATABASE_TABLE_divisions, new String[] {
					divisionsKEY_ROWID, divisionsKEY_Title, divisionsKEY_Heat}, null, null,
					null, null, divisionsKEY_Heat+ " desc","30");
			if (mCursor != null) 
				mCursor.moveToFirst();
			do{
				ContentValues initialValues = new ContentValues();
				initialValues.put(divisionsKEY_Title, mCursor.getString(1));
				initialValues.put(divisionsKEY_Heat, mCursor.getString(2));
				

				db.insert(DATABASE_TABLE_divisions, null, initialValues);			
			}while(mCursor.moveToNext());
		}
		private void delete_places()
		{
			Cursor mCursor = db.query(DATABASE_TABLE_places, new String[] {
					placesKEY_ROWID, placesKEY_Title, placesKEY_Heat}, null, null,
					null, null, placesKEY_Heat+ " desc","30");
			if (mCursor != null) 
				mCursor.moveToFirst();
			do{
				ContentValues initialValues = new ContentValues();
				initialValues.put(placesKEY_Title, mCursor.getString(1));
				initialValues.put(placesKEY_Heat, mCursor.getString(2));
				

				db.insert(DATABASE_TABLE_places, null, initialValues);			
			}while(mCursor.moveToNext());
		}
		private void delete_zuijinxinwen()
		{
			
			Cursor mCursor = db.query(DATABASE_TABLE_zuijinxinwen, new String[] {
					zuijinxinwenKEY_ROWID, zuijinxinwenKEY_Words, zuijinxinwenKEY_Count}, null, null,
					null, null, zuijinxinwenKEY_Count+ " desc","30");
			if (mCursor != null) 
				mCursor.moveToFirst();
			do{
				ContentValues initialValues = new ContentValues();
				initialValues.put(zuijinxinwenKEY_Words, mCursor.getString(1));
				initialValues.put(zuijinxinwenKEY_Count, mCursor.getString(2));
				

				db.insert(DATABASE_TABLE_zuijinxinwen, null, initialValues);			
			}while(mCursor.moveToNext());
		}
		public void delete() {
			db = DBHelper.getWritableDatabase();
			this.delete_divisions();
			this.delete_news();
			this.delete_places();
			this.delete_zuijinxinwen();
			this.delete_peoples();

			db.close();
			DBHelper.close();
		}
}
