//��Android�У����ݿ����� /data/data/PACKAGE_NAME/databases Ŀ¼��

package com.and.netease.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * ���ݿ������ ������
 */
public class DBAdapter

{
	private static final String TAG = "EV_DebugDBAdapter";
	// ������
	private final Context context;
	private static final String DATABASE_TABLE_news = "news";
	private static final String DATABASE_TABLE_peoples = "peoples";
	private static final String DATABASE_TABLE_places = "places";
	private static final String DATABASE_TABLE_divisions = "divisions";
	private static final String DATABASE_TABLE_zuijinxinwen = "zuijinxinwen";
	private static final String DATABASE_TABLE_user = "user";
	private static final String DATABASE_TABLE_usernews = "usernews";

	// ���ݿ����ݸ�ʽ
	/*
	 * title source description date url
	 */
	public static final String newsKEY_ROWID = "_id";// ר���������������
	public static final String newsKEY_Title = "title";
	public static final String newsKEY_Source = "source";
	public static final String newsKEY_Date = "date";
	public static final String newsKEY_Description = "description";
	public static final String newsKEY_Url = "url";
	public static final String newsKEY_ZhuantiId = "zhuantiId";

	public static final String zuijinxinwenKEY_ROWID = "_id";// ��������ŵ�ר����
	public static final String zuijinxinwenKEY_Words = "words";		//ר��ؼ���
	public static final String zuijinxinwenKEY_Count = "count";		//
	public static final String zuijinxinwenKEY_Title = "title";		//
	public static final String zuijinxinwenKEY_Date = "date";		//ר������
	
	public static final String userKEY_ROWID = "_id";// ��������ŵ�ר����
	public static final String userKEY_To = "to1";		//ר��ؼ���
	public static final String userKEY_Count = "count";		//
	public static final String userKEY_From = "from1";		//
	public static final String userKEY_Days = "days";		//ר������
	public static final String userKEY_user= "user";	
	public static final String userKEY_jobname = "jobname";	


	
	public static final String usernewsKEY_ROWID = "_id";// ר���������������
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

	//���ݿ������ ����չSQLiteOpenHelper�࣬����һ��Android�����࣬��Ҫ�������ݿⴴ���Ͱ汾����
	private DatabaseHelper DBHelper;
	// SQLiteDatabase����һ�����ݿ�����ṩ�˲������ݿ��һЩ����
	private SQLiteDatabase db;

	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}	

	
	/**
	 * ---�����ݿ��в���һ������--- �������
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




	public long insertpeople(String title, int heat) // �����update����0�������insert���ͷ���λ�ã����û��
														// �ͷ���-1
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



	// -----Ϊ������������� desc
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
	// ---����һ��ָ����---
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
			Log.d(TAG, "getString����:"+id);
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

	// -----Ϊ�ȵ�activity������
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

	// -----Ϊר��activity������

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

	// ---ɾ��һ��ָ������---


	// ---ɾ��---

		//--��ʱ������ɾ����ֻʣǰ30��,���������30��������ֻ��10������ֻʣ10��
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
