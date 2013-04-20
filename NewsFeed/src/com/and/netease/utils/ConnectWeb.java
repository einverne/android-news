package com.and.netease.utils;

import java.io.IOException;
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
	
	/**
	 * 获取用户定制内容的某一项,需呀参数user和jobname
	 * http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=getJobOfUser&user=gaojinping&jobname=20111226095814434
	 * @param  aboutchina 传true 或 false
	 * @param 
	 * @return
	 */
	static public void getJobOfUser(DBAdapter dbadapter,String username,String jobname)
	{
		String theurl="http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=getJobOfUser&user="+username+"&jobname="+jobname;
		try {
			String str = HttpConn.getJsonFromUrlGet(theurl);
			// 通过json 来解析收到的字符串
			JSONObject jay = new JSONObject(str);
			JSONObject result = new JSONObject((String) jay.getString("result"));
			// 解析key result所对应的值
			// count是总共取到的专题条数
			int count = Integer.parseInt((String) result.getString("count"));
			String to= (String) result.getString("to");
			String from=(String) result.getString("from");
			int days= Integer.parseInt((String) result.getString("days"));
			long jobid=dbadapter.userinsert(username, jobname, days, from, to, count);
			if(jobid<0)
				return;
			Log.d("test getJobOfUser count", String.valueOf(count));
			// clusters包括两部分,分别是 others 和 专题数组
			JSONObject clusters = new JSONObject(
					(String) result.getString("clusters"));
			// 取出专题数组
			JSONArray cluster = clusters.getJSONArray("cluster");
			// 循环从专题数取出每个专题进行解析
			Log.d("test getJobOfUser cluster.length()", String.valueOf(cluster.length()));// 0
			for (int i = 0; i < cluster.length(); i += 1) {
				// 挨个取出专题
				JSONObject onecluster = (JSONObject) cluster.get(i);
				
				// 取出doc，doc是具体新闻名的数组
				JSONArray doc = onecluster.getJSONArray("doc");
				// 取出第一条新闻名作为专题的标题
				// 此时将专题插入专题表
				// 目标是：如果有重名 而 新闻个数不同 就将之前的覆盖掉；然后返回 id
				// 将新闻插入新闻表
				for (int j= 0; j < doc.length(); j += 1) {
					JSONObject onenews = (JSONObject) doc.get(i);
					String title = (String) onenews.getString("title");
					String words = (String) onenews.getString("words");
					words = jiexiwords(words);
					String source = (String) onenews.getString("source");
					String description = (String) onenews.getString("description");
					String date = (String) onenews.getString("date");
					String url = (String) onenews.getString("url");
					dbadapter.userinsertnews(title, source, description, date, url, words, jobid);
					}
				}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	/**
	 * 提交一个任务
	 *  http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=submitJob&query=date:[20110101x TO x] AND (title:China Customs"20OR description:"China Customs" OR text:"China Customs")&aboutChina=true&user=test&desc=chania
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	static public boolean submitJob(String username,String query,String aboutchina,String description)
	{
		String theurl="http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=submitJob&query="+query+"&aboutChina="+aboutchina+"&user="+username+"&desc="+description;
		String str = HttpConn.getJsonFromUrlGet(theurl);
		boolean result= Boolean.valueOf(str).booleanValue();
		return result;
		
	}
	/**
	 * http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=deleteJob&username=gaojinping&jobname=20111222194809870
	 * 20120523191938669 删除指定用户的指定定制任务
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	static public boolean deleteJob(String name,String jobname)
	{
		String theurl="http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=deleteJob&username="+name+"&jobname="+jobname;
		String str = HttpConn.getJsonFromUrlGet(theurl);
		boolean result= Boolean.valueOf(str).booleanValue();
		return result;
	}
	
	//http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=getAllJobsOfUser&user=test
	static public List<Map<String, Object>>getAllJobsOfUser(String name){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		String theurl="http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=getAllJobsOfUser&user="+name;
		try {
			String str = HttpConn.getJsonFromUrlGet(theurl);
			JSONArray jay = new JSONArray(str);

			for (int i = 0; i < jay.length(); i += 1) {
				JSONObject inforMap =(JSONObject) jay.get(i);
				String createtime = (String) inforMap.getString("createtime");
				String aboutChina = (String) inforMap.getString("aboutChina");
				String endtime = (String) inforMap.getString("endtime");
				String description = (String) inforMap.getString("description");
				String query = (String) inforMap.getString("query");
				String name1 = (String) inforMap.getString("name");
				String status = (String) inforMap.getString("status");
				String result = (String) inforMap.getString("result");
				String end = (String) inforMap.getString("end");
				String type = (String) inforMap.getString("type");
				
				if(type=="hotmining")
				{

				map = new HashMap<String, Object>();
				map.put("createtime", createtime);
				map.put("aboutChina", aboutChina);
				map.put("endtime", endtime);
				map.put("description", description);
				map.put("query", query);
				map.put("name", name1);
				map.put("status", status);
				map.put("result", result);
				map.put("end", end);
				map.put("type", type);
				// 2012-06-05 08:14:54
				list.add(map);}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	static public boolean getlogin(String name,String psw)
	{
		boolean result=false;
		String theurl="http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=VerifyUser&name="+name+"&pwd="+psw;
		String str = HttpConn.getJsonFromUrlGet(theurl);
		result= Boolean.valueOf(str).booleanValue();
		return result;
	}
	static public boolean CheckUsed(String name)
	{
		boolean result=false;
		String theurl="http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=CheckUsed&name="+name;
		String str = HttpConn.getJsonFromUrlGet(theurl);
		result= Boolean.valueOf(str).booleanValue();
		return result;
		
	}
	/**
	 * 
	 * @param keyword
	 * @param dateF 2020121106
	 * @param dateT 2020121106x x表示其余不计 2020121106 20201211
	 * @return 符合搜索语句的条数
	 */
	static public int  getsearchcount(String keyword,String dateF, String dateT) {
	String theurl="http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=GetNewsHit&s=title:\""+keyword+"\"ANDdate:["+dateF+" TO "+dateT+"]";
	String str = HttpConn.getJsonFromUrlGet(theurl);
	return  Integer.parseInt(str);

	}
	
	
	static public List<Map<String, Object>> getsearch(String keyword,
			String dateF, String dateT, String relateToChina, int start, int max) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;

		try {
			String theurl = "http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=GetDocs&s="+keyword+"&dateF="+dateF+"&dateT="+dateT+"&relate="+relateToChina+"&start="+Integer.toString(start)+"&max="+Integer.toString(max);
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

	static public int getzuijinxinwen(DBAdapter dbadapter,String date) {
		/*
		 * 数据格式： result clusters others cluster words count source-distribution
		 * doc title words source date url
		 */
		int count = 0;
		try {

			String theurl = "http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=GetXML&t="+date;
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
				long oneclusterid = insert(dbadapter, title, date, words,
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
	 * 插入News表
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
