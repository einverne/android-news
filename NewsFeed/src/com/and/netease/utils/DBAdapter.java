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

	// ---�����ݿ�---
	public DBAdapter open() throws SQLException {
		Log.d("DBA", "DBA open");
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---�ر����ݿ�---
	public void close() {
		db.close();
		DBHelper.close();
	}
	
	public long simpleinsert(String title,String date,String words,  int count) {
		db = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(zuijinxinwenKEY_Title, title);
		initialValues.put(zuijinxinwenKEY_Date, date);
		initialValues.put(zuijinxinwenKEY_Words,words);
		initialValues.put(zuijinxinwenKEY_Count, count);
		Log.d("test", "before simpleinsert");
		long result=db.insert(DATABASE_TABLE_zuijinxinwen, null, initialValues);//insert()�����ķ���ֵ�������ݲ����λ�ã���IDֵ��
		Log.d("test", "after simpleinsert "+String.valueOf(result));
		if (result<0)
		{
			Cursor mCursor =db.query(true, DATABASE_TABLE_zuijinxinwen,
					new String[] { zuijinxinwenKEY_ROWID, zuijinxinwenKEY_Title,
							zuijinxinwenKEY_Count}, zuijinxinwenKEY_Title + "='"
							+ title+"'", null, null, null, null, null);
			
			if (mCursor != null) {
				mCursor.moveToFirst();
				result=Integer.parseInt((String)mCursor.getString(0));
			}
			
			
		}
		db.close();
		DBHelper.close();
		Log.d("test", "after simpleinsert "+String.valueOf(result));
		return result;
	}
	
	/**
	 * ---�����ݿ��в���һ������--- �������
	 * @param words keywords
	 * @param title
	 * @param date
	 * @param count
	 * @return
	 */
	public long insert(String words, String title ,String date,int count) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(zuijinxinwenKEY_Words, words);
		initialValues.put(zuijinxinwenKEY_Count, count);
		initialValues.put(zuijinxinwenKEY_Title, title);
		initialValues.put(zuijinxinwenKEY_Date, date);
		int temp = 0;
		Cursor c = db.query(DATABASE_TABLE_zuijinxinwen,
				new String[] { zuijinxinwenKEY_Count }, zuijinxinwenKEY_Words
						+ "=?", new String[] { words }, null, null, null, null);
		c.moveToFirst();
		for (int i = 0; i < c.getCount(); i++) {
			String incount = c.getString(0);
			if (Integer.parseInt(incount) != count)
				temp = db.update(DATABASE_TABLE_zuijinxinwen, initialValues,
						zuijinxinwenKEY_Words + " = '" + words + "'", null);
			else
				temp = -1;
			c.moveToNext();
		}
		c.close();
		if (temp == 0)// ���temp��=0 update�� �Ͳ�insert
			return db.insert(DATABASE_TABLE_zuijinxinwen, null, initialValues);
		else if (temp < 0)
			return temp;
		else
			return 0;
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
		ContentValues initialValues = new ContentValues();
		initialValues.put(newsKEY_Title, title);
		initialValues.put(newsKEY_Source, source);
		initialValues.put(newsKEY_Date, date);
		initialValues.put(newsKEY_Description, description);
		initialValues.put(newsKEY_Url, url);
		initialValues.put(newsKEY_ZhuantiId, zhuantiId);

		return db.insert(DATABASE_TABLE_news, null, initialValues);
	}

	// �ȵ�����ݿ����
	public Cursor rawquery(String str) {
		Cursor c = db.rawQuery(str, null);
		c.moveToFirst();
		return c;
	}

	public long insertpeople(String title, int heat) // �����update����0�������insert���ͷ���λ�ã����û��
														// �ͷ���-1
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(peoplesKEY_Title, title);
		initialValues.put(peoplesKEY_Heat, String.valueOf(heat));
		int temp = 0;
		Cursor c = db.query(DATABASE_TABLE_peoples,
				new String[] { peoplesKEY_Heat }, peoplesKEY_Title + "=?",
				new String[] { title }, null, null, null, null);
		c.moveToFirst();
		Log.d("test c count in insertpeople", String.valueOf(c.getCount()));
		for (int i = 0; i < c.getCount(); i++) {
			String dbheat = c.getString(0);
			Log.d("test dbheat insertpeople", dbheat);
			Log.d("test heat insertpeople", String.valueOf(heat));
			if (Integer.parseInt(dbheat) != heat)
				temp = db.update(DATABASE_TABLE_peoples, initialValues,
						peoplesKEY_Title + " = '" + title + "'", null);
			else
				temp = -1;
			c.moveToNext();
		}
		c.close();
		if (temp == 0)// ���temp��=0 update�� �Ͳ�insert
			return db.insert(DATABASE_TABLE_peoples, null, initialValues);
		else if (temp < 0)
			return temp;
		else
			return 0;
	}

	public long insertplace(String title, int heat) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(placesKEY_Title, title);
		initialValues.put(placesKEY_Heat, heat);
		int temp = 0;
		// query(table, columns, selection, selectionArgs, groupBy, having,
		// orderBy, limit);

		Cursor c = db.query(DATABASE_TABLE_places,
				new String[] { placesKEY_Heat }, placesKEY_Title + "=?",
				new String[] { title }, null, null, null, null);
		// Cursor c=db.query(
		// "SELECT "+placesKEY_Heat+" FROM "+DATABASE_TABLE_places+" WHERE "+placesKEY_Title+"  = '"+
		// title+"'", null);
		c.moveToFirst();
		for (int i = 0; i < c.getCount(); i++) {
			String dbheat = c.getString(0);
			if (Integer.parseInt(dbheat) != heat)
				temp = db.update(DATABASE_TABLE_places, initialValues,
						placesKEY_Title + " = '" + title + "'", null);
			else
				temp = -1;
			c.moveToNext();
		}
		c.close();
		if (temp == 0)// ���temp��=0 update�� �Ͳ�insert
			return db.insert(DATABASE_TABLE_places, null, initialValues);
		else if (temp < 0)
			return temp;
		else
			return 0;
	}

	public long insertdivision(String title, int heat) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(divisionsKEY_Title, title);
		initialValues.put(divisionsKEY_Heat, heat);
		int temp = 0;
		Cursor c = db.query(DATABASE_TABLE_divisions,
				new String[] { divisionsKEY_Heat }, divisionsKEY_Title + "=?",
				new String[] { title }, null, null, null, null);
		c.moveToFirst();
		for (int i = 0; i < c.getCount(); i++) {
			String dbheat = c.getString(0);
			if (Integer.parseInt(dbheat) != heat)
				temp = db.update(DATABASE_TABLE_divisions, initialValues,
						divisionsKEY_Title + " = '" + title + "'", null);
			else
				temp = -1;
			c.moveToNext();
		}
		c.close();
		if (temp == 0)// ���temp��=0 update�� �Ͳ�insert
			return db.insert(DATABASE_TABLE_divisions, null, initialValues);
		else if (temp < 0)
			return temp;
		else
			return 0;
	}

	// ---ɾ��---
	public void deleteAll() {
		db.delete(DATABASE_TABLE_news, newsKEY_ROWID + ">0", null);
		db.delete(DATABASE_TABLE_peoples, peoplesKEY_ROWID + ">0", null);
		db.delete(DATABASE_TABLE_places, placesKEY_ROWID + ">0", null);
		db.delete(DATABASE_TABLE_divisions, divisionsKEY_ROWID + ">0", null);
		db.delete(DATABASE_TABLE_zuijinxinwen, newsKEY_ROWID + ">0", null);
	}

	public int deletenews(long Id) {
		return db.delete(DATABASE_TABLE_news,
				newsKEY_ZhuantiId + "=" + String.valueOf(Id), null);

	}

	// -----Ϊ������������� desc
	public Cursor getzuijinxinwen(int num1, int num2) {
		String[] columns = new String[] {
				zuijinxinwenKEY_ROWID, zuijinxinwenKEY_Words,
				zuijinxinwenKEY_Count,zuijinxinwenKEY_Date };
		Cursor mCursor = db.query(DATABASE_TABLE_zuijinxinwen, null, null, null, null, null,
				zuijinxinwenKEY_Count + " desc", String.valueOf(num1) + ","
						+ String.valueOf(num2));
		if (mCursor != null) {
			mCursor.moveToFirst();

		}

		return mCursor;
	}

	// ---����һ��ָ����---
	public Cursor getselectedrow(int rowId) throws SQLException {
		Cursor mCursor = db.query(true, DATABASE_TABLE_zuijinxinwen,
				new String[] { zuijinxinwenKEY_ROWID, zuijinxinwenKEY_Words,
						zuijinxinwenKEY_Count }, zuijinxinwenKEY_ROWID + "="
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
					newsKEY_ROWID, newsKEY_Title, newsKEY_Source,
					newsKEY_Description, newsKEY_Date, newsKEY_Url,
					newsKEY_ZhuantiId }, newsKEY_ZhuantiId + "=" + 0, null,
					null, null, null, null);
		else {
			String[] columns = new String[] {
					zuijinxinwenKEY_ROWID, zuijinxinwenKEY_Words,
					zuijinxinwenKEY_Count };
			mCursor = db.query(true, DATABASE_TABLE_zuijinxinwen, null, zuijinxinwenKEY_Title + "='"
					+ words + "'", null, null, null, null, null);

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

		return mCursor;
	}

	// -----Ϊ�ȵ�activity������
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

	// -----Ϊר��activity������

	public long getselectednews(String words) {
		Cursor mCursor = db.query(true, DATABASE_TABLE_zuijinxinwen,
				new String[] { zuijinxinwenKEY_ROWID, zuijinxinwenKEY_Words,
						zuijinxinwenKEY_Count }, zuijinxinwenKEY_Words + "='"
						+ words + "'", null, null, null, null, null);
		if (mCursor != null)
			mCursor.moveToFirst();

		String id = mCursor.getString(0);
		return Integer.parseInt(id);

	}

	// ---ɾ��һ��ָ������---
	public boolean deleteothers() {
		return db
				.delete(DATABASE_TABLE_news, newsKEY_ZhuantiId + "=" + 0, null) > 0;
	}

	// һ���α꣨Cursor�����󣬵�ע��Cursors�ǲ�ͬ����

	/*
	 * public Cursor getAllHotspots() { return db.query(DATABASE_TABLE_hotspots,
	 * new String[]
	 * {hotspotsKEY_ROWID,hotspotsKEY_Name,hotspotsKEY_Heat},null,null
	 * ,null,null,hotspotsKEY_Heat); }
	 * 
	 * /*
	 * 
	 * 
	 * //---ɾ��һ��ָ������--- public boolean deleteTitle(long rowId) { return
	 * db.delete(DATABASE_TABLE, KEY_ROWID +"=" + rowId, null) > 0; }
	 * //---����һ��ָ������--- public Cursor getTitle(long rowId) throws SQLException {
	 * Cursor mCursor =db.query(true, DATABASE_TABLE, new String[]
	 * {KEY_ROWID,KEY_ItemText,KEY_ItemText},KEY_ROWID + "=" +
	 * rowId,null,null,null,null,null); if (mCursor != null) {
	 * mCursor.moveToFirst();
	 * 
	 * }
	 * 
	 * return mCursor; } //---����һ������-- public boolean updateTitle(long rowId,
	 * String name,String summary) { ContentValues args = new ContentValues();
	 * args.put(KEY_ItemText, name); args.put(KEY_ItemText, summary); //
	 * args.put(KEY_PUBLISHER, publisher); return db.update(DATABASE_TABLE,
	 * args,KEY_ROWID + "=" + rowId, null) > 0; }
	 */
}
