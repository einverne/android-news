//��Android�У���ݿ����� /data/data/PACKAGE_NAME/databases Ŀ¼��

package com.and.netease.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * ��ݿ������
 */
public class DBAdapter

{
	private static final String TAG = "EV_DebugDBAdapter";
	// ������
	private final Context context;

	/**
	 * ר��������������� title source description date url
	 */
	public static final String newsKEY_ROWID = "_id";
	public static final String newsKEY_Title = "title";
	public static final String newsKEY_Source = "source";
	public static final String newsKEY_Date = "date";
	public static final String newsKEY_Description = "description";
	public static final String newsKEY_Url = "url";
	public static final String newsKEY_ZhuantiId = "zhuantiId";
	public static final String newsKEY_Read = "read";

	public static final String zuijinxinwenKEY_ROWID = "_id";// ��������ŵ�ר����
	public static final String zuijinxinwenKEY_Words = "words"; // ר��ؼ���
	public static final String zuijinxinwenKEY_Count = "count"; //
	public static final String zuijinxinwenKEY_Title = "title"; //
	public static final String zuijinxinwenKEY_Date = "date"; // ר������
	public static final String zuijinxinwenKEY_Read = "read";

	public static final String userKEY_ROWID = "_id";// ��������ŵ�ר����
	public static final String userKEY_Count = "count"; //
	public static final String userKEY_words = "words";
	public static final String userKEY_user = "user";
	public static final String userKEY_jobname = "jobname";

	public static final String usernewsKEY_ROWID = "_id";// ר���������������
	public static final String usernewsKEY_Title = "title";
	public static final String usernewsKEY_Source = "source";
	public static final String usernewsKEY_Date = "date";
	public static final String usernewsKEY_Words = "words";
	public static final String usernewsKEY_Description = "description";
	public static final String usernewsKEY_Url = "url";
	public static final String usernewsKEY_ZhuantiId = "zhuantiId";
	public static final String usernewsKEY_Read = "read";

	public static final String peoplesKEY_ROWID = "_id";
	public static final String peoplesKEY_Title = "title";
	public static final String peoplesKEY_Heat = "heat";

	public static final String placesKEY_ROWID = "_id";
	public static final String placesKEY_Title = "title";
	public static final String placesKEY_Heat = "heat";

	public static final String divisionsKEY_ROWID = "_id";
	public static final String divisionsKEY_Title = "title";
	public static final String divisionsKEY_Heat = "heat";

	// ��ݿ���?�� ����չSQLiteOpenHelper�࣬����һ��Android�����࣬��Ҫ������ݿⴴ���Ͱ汾���?
	private DatabaseHelper DBHelper;
	// SQLiteDatabase���һ����ݿ�����ṩ�˲�����ݿ��һЩ����
	private SQLiteDatabase db;

	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	/**
	 * ---����ݿ��в���һ������--- �������
	 * 
	 * @param words
	 *            keywords
	 * @param title
	 * @param date
	 * @param count
	 * @return
	 */
	public long userInsert(String user, String jobname, String words, int count) {
		db = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(userKEY_user, user);
		initialValues.put(userKEY_Count, count);
		initialValues.put(userKEY_jobname, jobname);
		initialValues.put(userKEY_words, words);
		Cursor mCursor = db.query(DatabaseHelper.TABLE_USER,
				new String[] { userKEY_ROWID }, userKEY_user + " = '" + user
						+ "'  and " + userKEY_jobname + " = '" + jobname + "'"
						+ " and " + userKEY_words + "='" + words + "'", null,
				null, null, null, null);
		long result = -1;
		if (mCursor.moveToFirst() != false) {
			result = -1;
		} else {
			result = db.insert(DatabaseHelper.TABLE_USER, null, initialValues);
		}
		db.close();
		DBHelper.close();
		mCursor.close();
		Log.d(TAG, "����user���������" + String.valueOf(result));
		return result;
	}

	public long insert(String words, String title, String date, int count,
			boolean read) {
		db = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(zuijinxinwenKEY_Title, title);
		initialValues.put(zuijinxinwenKEY_Date, date);
		initialValues.put(zuijinxinwenKEY_Words, words);
		initialValues.put(zuijinxinwenKEY_Count, count);
		initialValues.put(zuijinxinwenKEY_Read, String.valueOf(read));
		Cursor mCursor = db.query(true, DatabaseHelper.TABLE_ZUIJINXINWEN,
				new String[] { zuijinxinwenKEY_ROWID, zuijinxinwenKEY_Count },
				zuijinxinwenKEY_Words + "=\"" + words + "\"", null, null, null,
				null, null);
		long result = -1;
		if (mCursor.moveToFirst() != false) {
			int dbcount = Integer.parseInt(mCursor.getString(1));
			if (count != dbcount) {
				db.update(DatabaseHelper.TABLE_ZUIJINXINWEN, initialValues,
						zuijinxinwenKEY_ROWID + "=" + mCursor.getString(0),
						null);
				db.delete(DatabaseHelper.TABLE_NEWS, newsKEY_ZhuantiId + "="
						+ mCursor.getString(0), null);
				result = Integer.parseInt(mCursor.getString(0));
			} else {
				result = -1;
			}

		} else {
			result = db.insert(DatabaseHelper.TABLE_ZUIJINXINWEN, null,
					initialValues);
		}

		mCursor.close();
		db.close();
		DBHelper.close();
		Log.d("test", "after simpleinsert " + String.valueOf(result));
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
			String date, String url, long zhuantiId, boolean read) {
		db = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(newsKEY_Title, title);
		initialValues.put(newsKEY_Source, source);
		initialValues.put(newsKEY_Date, date);
		initialValues.put(newsKEY_Description, description);
		initialValues.put(newsKEY_Url, url);
		initialValues.put(newsKEY_ZhuantiId, zhuantiId);
		initialValues.put(newsKEY_Read, String.valueOf(read));

		long result = db.insert(DatabaseHelper.TABLE_NEWS, null, initialValues);
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
	public long userInsertNews(String title, String source, String description,
			String date, String url, String words, long zhuantiId, boolean read) {
		Log.d(TAG, "����һ�����ţ�title:" + title);
		db = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(usernewsKEY_Title, title);
		initialValues.put(usernewsKEY_Source, source);
		initialValues.put(usernewsKEY_Date, date);
		initialValues.put(usernewsKEY_Description, description);
		initialValues.put(usernewsKEY_Url, url);
		initialValues.put(usernewsKEY_Words, words);
		initialValues.put(usernewsKEY_ZhuantiId, zhuantiId);
		initialValues.put(usernewsKEY_Read, String.valueOf(read));
		long result = db.insert(DatabaseHelper.TABLE_USERNEWS, null,
				initialValues);
		db.close();
		DBHelper.close();
		return result;
	}

	public boolean setread_usernews(String _id, boolean read) {
		db = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(usernewsKEY_Read, String.valueOf(read));
		int updateresult = db.update(DatabaseHelper.TABLE_USERNEWS,
				initialValues, usernewsKEY_ROWID + "=" + _id, null);
		boolean result = false;
		if (updateresult > 0)
			result = true;
		db.close();
		DBHelper.close();
		return result;
	}

	public boolean setread_zuijinxinwen(String _id, boolean read) {
		db = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(zuijinxinwenKEY_Read, String.valueOf(read));
		int updateresult = db.update(DatabaseHelper.TABLE_ZUIJINXINWEN,
				initialValues, zuijinxinwenKEY_ROWID + "=" + _id, null);
		boolean result = false;
		if (updateresult > 0)
			result = true;
		db.close();
		DBHelper.close();
		return result;
	}

	public boolean setread_news(String _id, boolean read) {
		db = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(newsKEY_Read, String.valueOf(read));
		int updateresult = db.update(DatabaseHelper.TABLE_NEWS, initialValues,
				newsKEY_ROWID + "=" + _id, null);
		boolean result = false;
		if (updateresult > 0)
			result = true;
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
		Cursor mCursor = db.query(DatabaseHelper.TABLE_PEOPLE, new String[] {
				peoplesKEY_ROWID, peoplesKEY_Heat }, peoplesKEY_Title + "=?",
				new String[] { title }, null, null, null, null);
		long result = -1;
		if (mCursor.moveToFirst() != false) {
			int dbheat = Integer.parseInt(mCursor.getString(1));
			if (heat != dbheat) {
				db.update(DatabaseHelper.TABLE_PEOPLE, initialValues,
						peoplesKEY_ROWID + "=" + mCursor.getString(0), null);
				result = Integer.parseInt(mCursor.getString(0));
			} else {
				result = -1;
			}

		} else {
			result = db
					.insert(DatabaseHelper.TABLE_PEOPLE, null, initialValues);
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

		Cursor mCursor = db.query(DatabaseHelper.TABLE_PLACE, new String[] {
				placesKEY_ROWID, placesKEY_Heat }, placesKEY_Title + "=?",
				new String[] { title }, null, null, null, null);
		long result = -1;
		if (mCursor.moveToFirst() != false) {
			mCursor.moveToFirst();
			int dbheat = Integer.parseInt(mCursor.getString(1));
			if (heat != dbheat) {
				db.update(DatabaseHelper.TABLE_PLACE, initialValues,
						placesKEY_ROWID + "=" + mCursor.getString(0), null);
				result = Integer.parseInt(mCursor.getString(0));
			} else {
				result = -1;
			}

		} else {
			result = db.insert(DatabaseHelper.TABLE_PLACE, null, initialValues);
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
		Cursor mCursor = db.query(DatabaseHelper.TABLE_DIVISION, new String[] {
				divisionsKEY_ROWID, divisionsKEY_Heat }, divisionsKEY_Title
				+ "=?", new String[] { title }, null, null, null, null);
		long result = -1;
		if (mCursor.moveToFirst() != false) {
			mCursor.moveToFirst();
			int dbheat = Integer.parseInt(mCursor.getString(1));
			if (heat != dbheat) {
				db.update(DatabaseHelper.TABLE_DIVISION, initialValues,
						divisionsKEY_ROWID + "=" + mCursor.getString(0), null);
				result = Integer.parseInt(mCursor.getString(0));
			} else {
				result = -1;
			}

		} else {
			result = db.insert(DatabaseHelper.TABLE_DIVISION, null,
					initialValues);
		}
		mCursor.close();
		db.close();
		DBHelper.close();
		return result;
	}

	// -----Ϊ������������ desc
	public Cursor getzuijinxinwen(int num1, int num2) {
		db = DBHelper.getWritableDatabase();
		Cursor mCursor = db.query(DatabaseHelper.TABLE_ZUIJINXINWEN, null,
				null, null, null, null, zuijinxinwenKEY_Date + " desc" + ","
						+ zuijinxinwenKEY_Count + " desc", String.valueOf(num1)
						+ "," + String.valueOf(num2));
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		db.close();
		DBHelper.close();
		return mCursor;
	}

	public Cursor getzuijinxinwenFromDate(String date) {
		db = DBHelper.getWritableDatabase();
		Cursor mCursor = db.query(DatabaseHelper.TABLE_ZUIJINXINWEN, null,
				zuijinxinwenKEY_Date + "='" + date + "'", null, null, null,
				zuijinxinwenKEY_Count + " desc", null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		db.close();
		DBHelper.close();
		return mCursor;
	}

	// db.query(String distinct, String table, String[] columns, String
	// selection, String[] selectionArgs, String groupBy, String having, String
	// orderBy, String limit);

	public Cursor getloginuser() {
		db = DBHelper.getWritableDatabase();
		boolean distinct = true;
		Cursor mCursor = db.query(distinct, DatabaseHelper.TABLE_USERNEWS,
				null, null, null, null, null, null, null);

		db.close();
		DBHelper.close();
		return mCursor;

	}

	/**
	 * ͨ����ר��Id,��ȡר����Ϣ
	 * 
	 * @param zhuantiid
	 * @return
	 */
	public Cursor getUserZhuanti(long zhuantiid) {
		db = DBHelper.getWritableDatabase();
		Cursor mCursor = db
				.query(DatabaseHelper.TABLE_USERNEWS,
						null,
						usernewsKEY_ZhuantiId + "= '"
								+ String.valueOf(zhuantiid) + "'", null, null,
						null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		db.close();
		DBHelper.close();
		return mCursor;
	}

	/**
	 * ͨ��user��jobname���Jobname�б�
	 * 
	 * @param user
	 * @param jobname
	 * @return
	 */
	public Cursor dingzhizhuanti(String user, String jobname) {
		Cursor mCursor1 = null;
		db = DBHelper.getWritableDatabase();
		Cursor mCursor = getuser(user, jobname);
		if (mCursor.moveToFirst()) {
			long userid = Integer.parseInt(mCursor.getString(mCursor
					.getColumnIndex("_id")));

			mCursor1 = getUserZhuanti(userid);
		}
		mCursor.close();
		db.close();
		DBHelper.close();
		return mCursor1;

	}

	/**
	 * User and jobname get the job list of user
	 * 
	 * @param user
	 * @param jobname
	 * @return
	 */
	public Cursor getuser(String user, String jobname) {
		db = DBHelper.getWritableDatabase();
		Cursor mCursor = db.query(DatabaseHelper.TABLE_USER, null, userKEY_user
				+ " = '" + user + "'  and " + userKEY_jobname + " = '"
				+ jobname + "'", null, null, null, null, null);
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
		Cursor mCursor = db.query(true, DatabaseHelper.TABLE_ZUIJINXINWEN,
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

	public Cursor getnews(int id) {
		db = DBHelper.getWritableDatabase();
		Cursor mCursor = db.query(true, DatabaseHelper.TABLE_NEWS, null,
				newsKEY_ZhuantiId + "=" + id, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();

		}

		db.close();
		DBHelper.close();
		return mCursor;
	}

	public Cursor getZhuantiNews(String words) throws SQLException {
		db = DBHelper.getWritableDatabase();
		Cursor mCursor = null;
		mCursor = db.query(true, DatabaseHelper.TABLE_ZUIJINXINWEN, null,
				zuijinxinwenKEY_Title + "=\'" + words + "\'", null, null, null,
				null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		String id = mCursor.getString(mCursor.getColumnIndex("_id"));
		Log.d(TAG, "getString����:" + id);
		mCursor = db.query(true, DatabaseHelper.TABLE_NEWS, null,
				newsKEY_ZhuantiId + "=" + id, null, null, null, null, null);

		if (mCursor != null)
			mCursor.moveToFirst();

		db.close();
		DBHelper.close();
		return mCursor;
	}

	// -----Ϊ�ȵ�activity�����
	public Cursor getHotWords() {
		db = DBHelper.getWritableDatabase();
		Cursor mCursor = db.query(DatabaseHelper.TABLE_ZUIJINXINWEN,
				new String[] { zuijinxinwenKEY_Words }, null, null, null, null,
				zuijinxinwenKEY_Count + " desc", null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		db.close();
		DBHelper.close();
		return mCursor;
	}

	public Cursor getpeople(int num1, int num2) {
		db = DBHelper.getWritableDatabase();
		Cursor mCursor = db.query(DatabaseHelper.TABLE_PEOPLE, new String[] {
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
		Cursor mCursor = db.query(DatabaseHelper.TABLE_PLACE, new String[] {
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
		Cursor mCursor = db.query(DatabaseHelper.TABLE_DIVISION, new String[] {
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

	// -----Ϊר��activity�����

	public long getselectednews(String words) {
		db = DBHelper.getWritableDatabase();
		Cursor mCursor = db.query(true, DatabaseHelper.TABLE_ZUIJINXINWEN,
				new String[] { zuijinxinwenKEY_ROWID, zuijinxinwenKEY_Words,
						zuijinxinwenKEY_Count }, zuijinxinwenKEY_Words + "='"
						+ words + "'", null, null, null, null, null);
		if (mCursor != null)
			mCursor.moveToFirst();

		String id = mCursor.getString(0);
		int result = Integer.parseInt(id);
		db.close();
		DBHelper.close();
		return result;

	}

	// ---ɾ��һ��ָ������---
	public boolean deleteuser(String user) {
		db = DBHelper.getWritableDatabase();
		boolean result = false;
		int i = db.delete(DatabaseHelper.TABLE_USER, userKEY_user + " = '"
				+ user + "'", null);
		if (i > 0)
			result = true;

		db.close();
		DBHelper.close();
		return result;
	}
	// ---ɾ��---
	/*
	 * //--��ʱ������ɾ��ֻʣǰ30��,����?��30��������ֻ��10������ֻʣ10�� private void
	 * delete_news() { db.delete(DatabaseHelper.TABLE_NEWS, "_id >=0", null); }
	 * private void delete_peoples() {
	 * 
	 * Cursor mCursor = db.query(DatabaseHelper.TABLE_PEOPLE, null, null, null,
	 * null, null, peoplesKEY_Heat+ " desc","30");
	 * db.delete(DatabaseHelper.TABLE_PEOPLE, "_id >=0", null); if (mCursor !=
	 * null) mCursor.moveToFirst(); do{ ContentValues initialValues = new
	 * ContentValues(); initialValues.put(peoplesKEY_Title,
	 * mCursor.getString(1)); initialValues.put(peoplesKEY_Heat,
	 * mCursor.getString(2));
	 * 
	 * 
	 * db.insert(DatabaseHelper.TABLE_PEOPLE, null, initialValues);
	 * }while(mCursor.moveToNext()); } private void delete_divisions() { Cursor
	 * mCursor = db.query(DatabaseHelper.TABLE_DIVISION,null, null, null, null,
	 * null, divisionsKEY_Heat+ " desc","30");
	 * db.delete(DatabaseHelper.TABLE_DIVISION, "_id >=0", null); if (mCursor !=
	 * null) mCursor.moveToFirst(); do{ ContentValues initialValues = new
	 * ContentValues(); initialValues.put(divisionsKEY_Title,
	 * mCursor.getString(1)); initialValues.put(divisionsKEY_Heat,
	 * mCursor.getString(2));
	 * 
	 * 
	 * db.insert(DatabaseHelper.TABLE_DIVISION, null, initialValues);
	 * }while(mCursor.moveToNext()); } private void delete_places() { Cursor
	 * mCursor = db.query(DatabaseHelper.TABLE_PLACE, null, null, null, null,
	 * null, placesKEY_Heat+ " desc","30");
	 * db.delete(DatabaseHelper.TABLE_PLACE, "_id >=0", null); if (mCursor !=
	 * null) mCursor.moveToFirst(); do{ ContentValues initialValues = new
	 * ContentValues(); initialValues.put(placesKEY_Title,
	 * mCursor.getString(1)); initialValues.put(placesKEY_Heat,
	 * mCursor.getString(2));
	 * 
	 * 
	 * db.insert(DatabaseHelper.TABLE_PLACE, null, initialValues);
	 * }while(mCursor.moveToNext()); } private void delete_zuijinxinwen() {
	 * 
	 * Cursor mCursor = db.query(DatabaseHelper.TABLE_ZUIJINXINWEN,null, null,
	 * null, null, null, zuijinxinwenKEY_Count+ " desc","30");
	 * db.delete(DatabaseHelper.TABLE_ZUIJINXINWEN, "_id >=0", null); if
	 * (mCursor != null) mCursor.moveToFirst(); do{ ContentValues initialValues
	 * = new ContentValues(); initialValues.put(zuijinxinwenKEY_Words,
	 * mCursor.getString(1)); initialValues.put(zuijinxinwenKEY_Count,
	 * mCursor.getString(2));
	 * 
	 * 
	 * db.insert(DatabaseHelper.TABLE_ZUIJINXINWEN, null, initialValues);
	 * }while(mCursor.moveToNext()); } public void delete() { db =
	 * DBHelper.getWritableDatabase(); this.delete_divisions();
	 * this.delete_news(); this.delete_places(); this.delete_zuijinxinwen();
	 * this.delete_peoples();
	 * 
	 * db.close(); DBHelper.close(); }
	 */
}
