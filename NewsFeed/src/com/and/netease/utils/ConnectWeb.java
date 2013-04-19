package com.and.netease.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

public class ConnectWeb {

	static public List<Map<String, Object>> getsearch(String keyword,
			String dateF, String dateT, String relateToChina, int start, int max) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;

		try {
			String theurl = "http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=GetDocs&s="+keyword+"&dateF=2012051300&dateT=20121106x&relate=T&start=0&max=20";
			String str = HttpConn.getJsonFromUrlGet(theurl);
			JSONArray jay = new JSONArray(str);

			for (int i = 0; i < jay.length(); i += 1) {
				JSONObject object = (JSONObject) jay.get(i);
				JSONObject inforMap = new JSONObject(
						(String) object.getString("inforMap"));
				String title = (String) inforMap.getString("title");
				String text = (String) inforMap.getString("text");
				String source = (String) inforMap.getString("source");
				String description = (String) inforMap.getString("description");
				String date = (String) inforMap.getString("date");
				String url = (String) inforMap.getString("url");
				map = new HashMap<String, Object>();
				map.put("title", title);
				map.put("text", text);
				map.put("source", source);
				map.put("description", description);
				map.put("url", url);
				// 2012-06-05 08:14:54
				date = jiexidate(date);
				map.put("date", date);
				list.add(map);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	static public int getzuijinxinwen(DBAdapter dbadapter) {
		/*
		 * 数据格式： result clusters others cluster words count source-distribution
		 * doc title words source date url
		 */
		int count = 0;
		String todaydate = todaydate();
		try {

			String theurl = "http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=GetXML&t=fresh";
			String str = HttpConn.getJsonFromUrlGet(theurl);
			// 通过json 来解析收到的字符串
			JSONObject jay = new JSONObject(str);
			Log.d("test", "get result");
			JSONObject result = new JSONObject((String) jay.getString("result"));
			// 解析key result所对应的值
			// count是总共取到的专题条数
			count = Integer.parseInt((String) result.getString("count"));
			Log.d("test count", String.valueOf(count));
			// clusters包括两部分,分别是 others 和 专题数组
			JSONObject clusters = new JSONObject(
					(String) result.getString("clusters"));
			// 取出专题数组
			JSONArray cluster = clusters.getJSONArray("cluster");
			// 循环从专题数取出每个专题进行解析
			Log.d("test cluster.length()", String.valueOf(cluster.length()));// 0
			for (int i = 0; i < cluster.length(); i += 1) {
				// 挨个取出专题
				JSONObject onecluster = (JSONObject) cluster.get(i);
				// 取出专题的关键字word和 count和doc
				String words = (String) onecluster.getString("words");
				words = jiexiwords(words);
				int oneclustercount = Integer.parseInt((String) onecluster
						.getString("count"));
				// 取出doc，doc是具体新闻名的数组
				JSONArray doc = onecluster.getJSONArray("doc");
				// 取出第一条新闻名作为专题的标题
				String title = getzhuantititle(doc);
				// 此时将专题插入专题表
				// 目标是：如果有重名 而 新闻个数不同 就将之前的覆盖掉；然后返回 id
				long oneclusterid = insert(dbadapter, title, todaydate, words,
						oneclustercount);
				// 将新闻插入新闻表
				if (oneclusterid != -1) {// 当值为-1时，说明此时的专题数据库中已存在，不需要更新
					newsinsert(dbadapter, doc, oneclusterid);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.d("test", String.valueOf(count));
		return count;
	}

	/**
	 * 
	 * @param dbadapter
	 * @param doc
	 * @param oneclusterid
	 * @throws JSONException
	 */
	static private void newsinsert(DBAdapter dbadapter, JSONArray doc,
			long oneclusterid) throws JSONException {
		for (int j = 0; j < doc.length(); j += 1) {
			JSONObject onexinwen = (JSONObject) doc.get(j);
			String title = (String) onexinwen.getString("title");
			String source = (String) onexinwen.getString("source");
			String description = (String) onexinwen.getString("description");
			String date = (String) onexinwen.getString("date");
			// 2012-06-05 08:14:54

			String url = (String) onexinwen.getString("url");
			Log.d("test news title", title);
			dbadapter.insert(title, source, description, date, url,
					oneclusterid);
		}
	}

	/**
	 * 目标是：将专题插入专题表 如果有重名 而 新闻个数不同 就将之前的覆盖掉，而且将新闻表里对应的新闻删掉
	 * 
	 * @param dbadapter
	 * @param title
	 * @param todaydate
	 * @param words
	 * @param oneclustercount
	 * @return
	 */
	static private long insert(DBAdapter dbadapter, String title,
			String todaydate, String words, int oneclustercount) {

		return dbadapter.insert(words, title, todaydate, oneclustercount);
	}

	/**
	 * 
	 * @param doc
	 * @return
	 * @throws JSONException
	 */
	static private String getzhuantititle(JSONArray doc) throws JSONException {
		JSONObject onexinwen = (JSONObject) doc.get(0);
		return (String) onexinwen.getString("title");

	}

	static private String jiexiwords(String words) {
		int index1 = -1;
		int index2 = -1;
		String keyword = "";
		int tmp = 0;
		while ((index1 = words.indexOf("(", index1)) > -1
				&& (index2 = words.indexOf("&", index2)) > -1 && (tmp < 5)) {
			keyword = keyword + words.substring(index1 + 1, index2) + " ";
			index1 += 1;
			index2 += 1;
			tmp++;

		}
		return keyword;
	}

	static private String todaydate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(currentTime);

	}

	static public List<Map<String, Object>> getzuijinxinwenmore(
			DBAdapter dbadapter) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		String todaydate = todaydate();
		try {
			String theurl = "http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=GetXML&t=fresh";
			String str = HttpConn.getJsonFromUrlGet(theurl);
			JSONObject jay = new JSONObject(str);
			JSONObject result = new JSONObject((String) jay.getString("result"));
			JSONObject clusters = new JSONObject(
					(String) result.getString("clusters"));
			JSONArray cluster = clusters.getJSONArray("cluster");
			for (int i = 0; i < cluster.length(); i += 1) {
				JSONObject onecluster = (JSONObject) cluster.get(i);
				String words = (String) onecluster.getString("words");
				words = jiexiwords(words);
				int oneclustercount = Integer.parseInt((String) onecluster
						.getString("count"));
				JSONArray doc = onecluster.getJSONArray("doc");
				String title = getzhuantititle(doc);
				long oneclusterid = insert(dbadapter, title, todaydate, words,
						oneclustercount);
				if (oneclusterid >= 0) {
					map = new HashMap<String, Object>();
					map.put("words", words);
					map.put("count", oneclustercount);
					list.add(map);
					newsinsert(dbadapter, doc, oneclusterid);
				}
			}
			// 根据文本排序
			if (!list.isEmpty()) {
				Collections.sort(list, new Comparator<Map<String, Object>>() {
					public int compare(Map<String, Object> object1,
							Map<String, Object> object2) {
						return ((String) object2.get("count"))
								.compareTo((String) object1.get("count"));
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 留下list 的前5个
		for (int i = 5; i < list.size(); i++)
			list.remove(i);
		return list;
	}

	static public void getpeoples(DBAdapter dbadapter) {
		try {
			String theurl = "http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=GetHot&tf=2&type=0";
			String string = HttpConn.getJsonFromUrlGet(theurl);
			JSONArray jay = new JSONArray(string);
			for (int i = 0; i < jay.length(); i += 1) {
				JSONObject temp = (JSONObject) jay.get(i);
				String title = (String) temp.getString("first");
				String heat = (String) temp.getString("second");
				Log.d("test people title", title);
				dbadapter.insertpeople(title, Integer.parseInt(heat));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static public List<Map<String, Object>> getpeoplesmore(DBAdapter dbadapter) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		try {
			String theurl = "http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=GetHot&tf=2&type=0";

			String string = HttpConn.getJsonFromUrlGet(theurl);
			JSONArray jay = new JSONArray(string);
			for (int i = 0; i < jay.length(); i += 1) {
				JSONObject temp = (JSONObject) jay.get(i);
				String title = (String) temp.getString("first");
				String heat = (String) temp.getString("second");
				long tmp = dbadapter
						.insertpeople(title, Integer.parseInt(heat));
				if (tmp >= 0)// 如果不是零，就insert，说明没有
				{
					map = new HashMap<String, Object>();
					map.put("title", title);
					map.put("heat", heat);
					list.add(map);
				}
			}
			Log.d("test list is empty?", String.valueOf(list.isEmpty()));
			if (!list.isEmpty()) {
				Collections.sort(list, new Comparator<Map<String, Object>>() {
					public int compare(Map<String, Object> object1,
							Map<String, Object> object2) {
						// 根据文本排序
						return ((String) object2.get("heat"))
								.compareTo((String) object1.get("heat"));
					}
				});
			}

			for (int i = 4; i < list.size(); i++)
				list.remove(i);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	static public List<Map<String, Object>> getplacesmore(DBAdapter dbadapter) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		try {
			String theurl = "http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=GetHot&tf=2&type=1";

			String string = HttpConn.getJsonFromUrlGet(theurl);
			JSONArray jay = new JSONArray(string);
			for (int i = 0; i < jay.length(); i += 1) {
				JSONObject temp = (JSONObject) jay.get(i);
				String title = (String) temp.getString("first");
				String heat = (String) temp.getString("second");
				long tmp = dbadapter.insertplace(title, Integer.parseInt(heat));
				if (tmp >= 0)// 如果不是零，就insert，说明没有
				{
					map = new HashMap<String, Object>();
					map.put("title", title);
					map.put("heat", heat);
					list.add(map);
				}
			}
			if (!list.isEmpty()) {
				Collections.sort(list, new Comparator<Map<String, Object>>() {
					public int compare(Map<String, Object> object1,
							Map<String, Object> object2) {
						// 根据文本排序
						return ((String) object2.get("heat"))
								.compareTo((String) object1.get("heat"));
					}
				});
			}

			for (int i = 5; i < list.size(); i++)
				list.remove(i);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	static public void getplaces(DBAdapter dbadapter) {
		try {
			String theurl = "http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=GetHot&tf=2&type=1";

			String string = HttpConn.getJsonFromUrlGet(theurl);
			JSONArray jay = new JSONArray(string);
			for (int i = 0; i < jay.length(); i += 1) {
				JSONObject temp = (JSONObject) jay.get(i);
				String title = (String) temp.getString("first");
				String heat = (String) temp.getString("second");
				dbadapter.insertplace(title, Integer.parseInt(heat));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static public void getdivisions(DBAdapter dbadapter) {
		try {
			String theurl = "http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=GetHot&tf=2&type=2";

			String string = HttpConn.getJsonFromUrlGet(theurl);

			JSONArray jay = new JSONArray(string);
			for (int i = 0; i < jay.length(); i += 1) {
				JSONObject temp = (JSONObject) jay.get(i);
				String title = (String) temp.getString("first");
				Log.d("json", title);
				String heat = (String) temp.getString("second");
				Log.d("json", heat);
				dbadapter.insertdivision(title, Integer.parseInt(heat));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static public List<Map<String, Object>> getdivisionsmore(DBAdapter dbadapter) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		try {
			String theurl = "http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=GetHot&tf=2&type=2";

			String string = HttpConn.getJsonFromUrlGet(theurl);

			JSONArray jay = new JSONArray(string);
			for (int i = 0; i < jay.length(); i += 1) {
				JSONObject temp = (JSONObject) jay.get(i);
				String title = (String) temp.getString("first");
				Log.d("json", title);
				String heat = (String) temp.getString("second");
				long tmp = dbadapter.insertdivision(title,
						Integer.parseInt(heat));
				if (tmp >= 0)// 如果不是零，就insert，说明没有
				{
					map = new HashMap<String, Object>();
					map.put("title", title);
					map.put("heat", heat);
					list.add(map);
				}
			}
			if (!list.isEmpty()) {
				Collections.sort(list, new Comparator<Map<String, Object>>() {
					public int compare(Map<String, Object> object1,
							Map<String, Object> object2) {
						// 根据文本排序
						return ((String) object2.get("heat"))
								.compareTo((String) object1.get("heat"));
					}
				});
			}

			for (int i = 4; i < list.size(); i++)
				list.remove(i);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	static public Map<String, String> getjutixinwen(String theurl) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			String str = "http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=GetDoc&url=";
			theurl = str + theurl;
			String string = HttpConn.getJsonFromUrlGet(theurl);
			JSONObject jay = new JSONObject(string);
			JSONObject inforMap = new JSONObject(
					(String) jay.getString("inforMap"));
			String title = (String) inforMap.getString("title");
			Log.d("test string", title);
			String text = (String) inforMap.getString("text");
			Log.d("test string", text);
			String source = (String) inforMap.getString("source");
			String date = (String) inforMap.getString("date");
			map.put("title", title);
			map.put("text", text);
			map.put("source", source);
			date = jiexidate(date);
			map.put("date", date);

		} catch (Exception e) {
			Log.d("test error", "error");
			e.printStackTrace();
		}
		return map;
	}

	static private String jiexidate(String date) {
		String d1 = date.substring(0, 4);
		String d2 = date.substring(4, 6);
		String d3 = date.substring(6, 8);
		String d4 = date.substring(8, 10);
		String d5 = date.substring(10, 12);
		String d6 = date.substring(12, 14);
		// 2012-06-05 08:14:54
		String result = d1 + "-" + d2 + "-" + d3 + " " + d4 + ":" + d5 + ":"
				+ d6;
		return result;
	}

}
