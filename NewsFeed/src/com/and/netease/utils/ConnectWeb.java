package com.and.netease.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

//将请求地址写入构造函数URL中，然后将取得的数据加入数据库adapter的不同的表中

/**
 * 
 * 
 * @author
 * 
 */
public class ConnectWeb {
	DBAdapter dbadapter; // 数据库对象

	private static final String TAG = "EV_Debug";

	public ConnectWeb(DBAdapter adapter) {
		dbadapter = adapter;
	}

	public int getzuijinxinwen() {
		int count = 0;
		try {

			String theurl = "http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=GetXML&t=fresh";
			String str = HttpConn.getJsonFromUrlGet(theurl);
			JSONObject jay = new JSONObject(str);
			JSONObject result = new JSONObject((String) jay.getString("result"));
			count = Integer.parseInt((String) result.getString("count"));// count是总共取到的专题条数
			JSONObject clusters = new JSONObject(
					(String) result.getString("clusters"));
			JSONArray cluster = clusters.getJSONArray("cluster");// zhuaiti
			Date currentTime = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String todaydate = formatter.format(currentTime);
			for (int i = 0; i < cluster.length(); i += 1) {
				JSONObject onecluster = (JSONObject) cluster.get(i);
				String words = (String) onecluster.getString("words");
				int index1 = -1;
				int index2 = -1;
				String keyword = "";
				int tmp = 0;
				while ((index1 = words.indexOf("(", index1)) > -1
						&& (index2 = words.indexOf("&", index2)) > -1
						&& (tmp < 5)) {
					keyword = keyword + words.substring(index1 + 1, index2)
							+ " ";
					index1 += 1;
					index2 += 1;
					tmp++;
				}
				int oneclustercount = Integer.parseInt((String) onecluster
						.getString("count"));
				JSONArray doc = onecluster.getJSONArray("doc");// xinwen
				JSONObject onexinwen = (JSONObject) doc.get(0);
				String title = (String) onexinwen.getString("title");
				dbadapter.simpleinsert(title, todaydate, keyword,
						oneclustercount);// ！！！这里只要insert就行！！！！！
				long oneclusterid = dbadapter.getselectednews(keyword);
				for (int j = 0; j < doc.length(); j += 1) {
					onexinwen = (JSONObject) doc.get(j);
					title = (String) onexinwen.getString("title");
					String source = (String) onexinwen.getString("source");
					String description = (String) onexinwen
							.getString("description");
					String date = (String) onexinwen.getString("date");
					// 2012-12-12 00:00:00
					date = date.substring(11, 19);
					// 2012-06-05 08:14:54

					String url = (String) onexinwen.getString("url");
					dbadapter.insert(title, source, description, date, url,
							oneclusterid);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.d("test", String.valueOf(count));
		return count;
	}

	public List<Map<String, Object>> getzuijinxinwenmore() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		try {

			String theurl = "http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=GetXML&t=fresh";
			String str = HttpConn.getJsonFromUrlGet(theurl);
			JSONObject jay = new JSONObject(str);
			JSONObject result = new JSONObject((String) jay.getString("result"));
			JSONObject clusters = new JSONObject(
					(String) result.getString("clusters"));
			JSONArray cluster = clusters.getJSONArray("cluster");// zhuaiti
			Date currentTime = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String todaydate = formatter.format(currentTime);
			for (int i = 0; i < cluster.length(); i += 1) {
				JSONObject onecluster = (JSONObject) cluster.get(i);
				String words = (String) onecluster.getString("words");
				int index1 = -1;
				int index2 = -1;
				String tmp1 = "";
				int tmp2 = 0;
				while ((index1 = words.indexOf("(", index1)) > -1
						&& (index2 = words.indexOf("&", index2)) > -1
						&& (tmp2 < 5)) {
					tmp1 = tmp1 + words.substring(index1 + 1, index2) + " ";
					index1 += 1;
					index2 += 1;
					tmp2++;
				}
				int oneclustercount = Integer.parseInt((String) onecluster
						.getString("count"));
				JSONArray doc = onecluster.getJSONArray("doc");// xinwen
				JSONObject onexinwen = (JSONObject) doc.get(0);
				String title = (String) onexinwen.getString("title");
				long oneclusterid = dbadapter.insert(title, todaydate, tmp1,
						oneclustercount);
				if (oneclusterid >= 0)// 如果大于零，就insert；如果为-1，说明没有；如果是update 会总是0
				{
					map = new HashMap<String, Object>();
					map.put("words", tmp1);
					map.put("count", oneclustercount);
					list.add(map);
					if (oneclusterid == 0)
						oneclusterid = dbadapter.getselectednews(tmp1);
					dbadapter.deletenews(oneclusterid);
					for (int j = 0; j < doc.length(); j += 1) {// 先删除 再添加
						onexinwen = (JSONObject) doc.get(j);
						title = (String) onexinwen.getString("title");
						String source = (String) onexinwen.getString("source");
						String description = (String) onexinwen
								.getString("description");
						String date = (String) onexinwen.getString("date");
						date = date.substring(11, 19);
						String url = (String) onexinwen.getString("url");
						dbadapter.insert(title, source, description, date, url,
								oneclusterid);

					}
				}
			}

			if (!list.isEmpty()) {
				Collections.sort(list, new Comparator<Map<String, Object>>() {
					public int compare(Map<String, Object> object1,
							Map<String, Object> object2) {
						// 根据文本排序
						return ((String) object2.get("count"))
								.compareTo((String) object1.get("count"));
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 4; i < list.size(); i++)
			// 一共更新了list.size()的专题个数
			list.remove(i);
		return list;
	}

	public void getpeoples() {
		try {
			String theurl = "http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=GetHot&tf=2&type=0";

			String string = HttpConn.getJsonFromUrlGet(theurl);
			// String string
			// ="[{\"first\":\"Wang Zhe\",\"second\":12},{\"first\":\"Olajide Omotayo\",\"second\":9},{\"first\":\"Raymond Ip\",\"second\":6},{\"first\":\"Samuel Robert\",\"second\":6},{\"first\":\"Kuala Lumpur\",\"second\":6},{\"first\":\"Bunda\",\"second\":6},{\"first\":\"Mojeed Olayiwola\",\"second\":6},{\"first\":\"Groom Terance Fung\",\"second\":6},{\"first\":\"Joergen Svendsen Killi\",\"second\":6},{\"first\":\"God\",\"second\":6},{\"first\":\"Novak Djokovic\",\"second\":5},{\"first\":\"Wahid Oshodi\",\"second\":3},{\"first\":\"Stacy Rasgon\",\"second\":3},{\"first\":\"Maria Sharapova\",\"second\":3},{\"first\":\"Kevin Anderson\",\"second\":2},{\"first\":\"Venus Williams\",\"second\":2},{\"first\":\"Radek Stepanek\",\"second\":2},{\"first\":\"Omer Esiner\",\"second\":2},{\"first\":\"Jerzy Janowicz\",\"second\":2},{\"first\":\"Ana Ivanovic\",\"second\":2},{\"first\":\"Caroline Valetkevitch\",\"second\":2},{\"first\":\"Nick Zieminski\",\"second\":2},{\"first\":\"JoAnne Feeney\",\"second\":2},{\"first\":\"Noel Randewich\",\"second\":2},{\"first\":\"Bernadette Baum\",\"second\":2},{\"first\":\"Rod Laver Arena\",\"second\":2},{\"first\":\"Agnieszka Radwanska\",\"second\":2},{\"first\":\"Nicolas Almagro\",\"second\":1},{\"first\":\"Jelena Jankovic\",\"second\":1},{\"first\":\"Sanford Bernstein\",\"second\":1},{\"first\":\"Janko Tipsarevic\",\"second\":1},{\"first\":\"Marcos Baghdatis\",\"second\":1},{\"first\":\"Richard Chang\",\"second\":1},{\"first\":\"Kenneth Barry\",\"second\":1},{\"first\":\"Stacy Smith\",\"second\":1},{\"first\":\"Alistair Barr\",\"second\":1},{\"first\":\"Lance Armstrong\",\"second\":1},{\"first\":\"Madison Keys\",\"second\":1},{\"first\":\"Herbert Lash\",\"second\":1},{\"first\":\"Angelique Kerber\",\"second\":1},{\"first\":\"David Ferrer\",\"second\":1},{\"first\":\"Tomas Berdych\",\"second\":1},{\"first\":\"Shinzo Abe\",\"second\":1},{\"first\":\"Sorana Cirstea\",\"second\":1},{\"first\":\"Sam Querrey\",\"second\":1},{\"first\":\"Fernando Verdasco\",\"second\":1},{\"first\":\"Rafa Nadal\",\"second\":1},{\"first\":\"Kei Nishikori\",\"second\":1},{\"first\":\"Kim Clijsters\",\"second\":1},{\"first\":\"Li Na\",\"second\":1},{\"first\":\"Jurgen Melzer\",\"second\":1},{\"first\":\"Bryant Evans\",\"second\":1},{\"first\":\"Jens Nordvig\",\"second\":1},{\"first\":\"Julien Benneteau\",\"second\":1},{\"first\":\"Heather Watson\",\"second\":1},{\"first\":\"Gary Crosse\",\"second\":1},{\"first\":\"Angela Moon\",\"second\":1},{\"first\":\"Phil Flynn\",\"second\":1},{\"first\":\"Stanislas Wawrinka\",\"second\":1}]";
			// Log.d("string",string);
			JSONArray jay = new JSONArray(string);
			for (int i = 0; i < jay.length(); i += 1) {
				JSONObject temp = (JSONObject) jay.get(i);
				String title = (String) temp.getString("first");
				String heat = (String) temp.getString("second");
				dbadapter.insertpeople(title, Integer.parseInt(heat));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List<Map<String, Object>> getpeoplesmore() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		try {
			String theurl = "http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=GetHot&tf=2&type=0";

			String string = HttpConn.getJsonFromUrlGet(theurl);
			// String string
			// ="[{\"first\":\"lzt\",\"second\":20},{\"first\":\"Olajide Omotayo\",\"second\":10},{\"first\":\"Raymond Ip\",\"second\":6},{\"first\":\"Samuel Robert\",\"second\":6},{\"first\":\"Kuala Lumpur\",\"second\":6},{\"first\":\"Bunda\",\"second\":6},{\"first\":\"Mojeed Olayiwola\",\"second\":6},{\"first\":\"Groom Terance Fung\",\"second\":6},{\"first\":\"Joergen Svendsen Killi\",\"second\":6},{\"first\":\"God\",\"second\":6},{\"first\":\"Novak Djokovic\",\"second\":5},{\"first\":\"Wahid Oshodi\",\"second\":3},{\"first\":\"Stacy Rasgon\",\"second\":3},{\"first\":\"Maria Sharapova\",\"second\":3},{\"first\":\"Kevin Anderson\",\"second\":2},{\"first\":\"Venus Williams\",\"second\":2},{\"first\":\"Radek Stepanek\",\"second\":2},{\"first\":\"Omer Esiner\",\"second\":2},{\"first\":\"Jerzy Janowicz\",\"second\":2},{\"first\":\"Ana Ivanovic\",\"second\":2},{\"first\":\"Caroline Valetkevitch\",\"second\":2},{\"first\":\"Nick Zieminski\",\"second\":2},{\"first\":\"JoAnne Feeney\",\"second\":2},{\"first\":\"Noel Randewich\",\"second\":2},{\"first\":\"Bernadette Baum\",\"second\":2},{\"first\":\"Rod Laver Arena\",\"second\":2},{\"first\":\"Agnieszka Radwanska\",\"second\":2},{\"first\":\"Nicolas Almagro\",\"second\":1},{\"first\":\"Jelena Jankovic\",\"second\":1},{\"first\":\"Sanford Bernstein\",\"second\":1},{\"first\":\"Janko Tipsarevic\",\"second\":1},{\"first\":\"Marcos Baghdatis\",\"second\":1},{\"first\":\"Richard Chang\",\"second\":1},{\"first\":\"Kenneth Barry\",\"second\":1},{\"first\":\"Stacy Smith\",\"second\":1},{\"first\":\"Alistair Barr\",\"second\":1},{\"first\":\"Lance Armstrong\",\"second\":1},{\"first\":\"Madison Keys\",\"second\":1},{\"first\":\"Herbert Lash\",\"second\":1},{\"first\":\"Angelique Kerber\",\"second\":1},{\"first\":\"David Ferrer\",\"second\":1},{\"first\":\"Tomas Berdych\",\"second\":1},{\"first\":\"Shinzo Abe\",\"second\":1},{\"first\":\"Sorana Cirstea\",\"second\":1},{\"first\":\"Sam Querrey\",\"second\":1},{\"first\":\"Fernando Verdasco\",\"second\":1},{\"first\":\"Rafa Nadal\",\"second\":1},{\"first\":\"Kei Nishikori\",\"second\":1},{\"first\":\"Kim Clijsters\",\"second\":1},{\"first\":\"Li Na\",\"second\":1},{\"first\":\"Jurgen Melzer\",\"second\":1},{\"first\":\"Bryant Evans\",\"second\":1},{\"first\":\"Jens Nordvig\",\"second\":1},{\"first\":\"Julien Benneteau\",\"second\":1},{\"first\":\"Heather Watson\",\"second\":1},{\"first\":\"Gary Crosse\",\"second\":1},{\"first\":\"Angela Moon\",\"second\":1},{\"first\":\"Phil Flynn\",\"second\":1},{\"first\":\"Stanislas Wawrinka\",\"second\":1}]";
			// Log.d("string",string);
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

	public List<Map<String, Object>> getplacesmore() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		try {
			String theurl = "http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=GetHot&tf=2&type=1";

			String string = HttpConn.getJsonFromUrlGet(theurl);
			// String string
			// ="[{\"first\":\"China\",\"second\":180},{\"first\":\"U.S.\",\"second\":86},{\"first\":\"Iran\",\"second\":59},{\"first\":\"Hong Kong\",\"second\":56},{\"first\":\"Japan\",\"second\":42},{\"first\":\"Beijing\",\"second\":38},{\"first\":\"United States\",\"second\":36},{\"first\":\"North Korea\",\"second\":31},{\"first\":\"France\",\"second\":27},{\"first\":\"London\",\"second\":27},{\"first\":\"India\",\"second\":26},{\"first\":\"Davos\",\"second\":26},{\"first\":\"Shanghai\",\"second\":23},{\"first\":\"Asia\",\"second\":23},{\"first\":\"Pyongyang\",\"second\":22},{\"first\":\"Chinese\",\"second\":21},{\"first\":\"Germany\",\"second\":20},{\"first\":\"Europe\",\"second\":19},{\"first\":\"HONG KONG\",\"second\":18},{\"first\":\"Indonesia\",\"second\":18},{\"first\":\"Spain\",\"second\":17},{\"first\":\"Russia\",\"second\":13},{\"first\":\"Italy\",\"second\":13},{\"first\":\"European\",\"second\":13},{\"first\":\"Belgium\",\"second\":12},{\"first\":\"Philippines\",\"second\":11},{\"first\":\"Asian\",\"second\":11},{\"first\":\"Switzerland\",\"second\":11},{\"first\":\"Britain\",\"second\":11},{\"first\":\"America\",\"second\":11},{\"first\":\"Washington\",\"second\":10},{\"first\":\"Egypt\",\"second\":10},{\"first\":\"Lombardy\",\"second\":10},{\"first\":\"South Korea\",\"second\":10},{\"first\":\"Shenzhen\",\"second\":9},{\"first\":\"Frankfurt\",\"second\":9},{\"first\":\"Syria\",\"second\":9},{\"first\":\"LONDON\",\"second\":8},{\"first\":\"Middle East\",\"second\":8},{\"first\":\"Hangzhou\",\"second\":8},{\"first\":\"South Africa\",\"second\":8},{\"first\":\"TOKYO\",\"second\":8},{\"first\":\"South China Sea\",\"second\":7},{\"first\":\"Heathrow\",\"second\":7},{\"first\":\"Pakistan\",\"second\":7},{\"first\":\"Malaysia\",\"second\":7},{\"first\":\"US\",\"second\":6},{\"first\":\"Tokyo\",\"second\":6},{\"first\":\"Holland\",\"second\":6},{\"first\":\"Ireland\",\"second\":6},{\"first\":\"Poland\",\"second\":6},{\"first\":\"Africa\",\"second\":6},{\"first\":\"Australia\",\"second\":6},{\"first\":\"Singapore\",\"second\":6},{\"first\":\"French\",\"second\":5},{\"first\":\"Taiwan\",\"second\":5},{\"first\":\"Iraq\",\"second\":5},{\"first\":\"Paris\",\"second\":5},{\"first\":\"Madrid\",\"second\":5},{\"first\":\"Sydney\",\"second\":5},{\"first\":\"German\",\"second\":5},{\"first\":\"NEW YORK\",\"second\":5},{\"first\":\"Brazil\",\"second\":5},{\"first\":\"Gatwick\",\"second\":5},{\"first\":\"academia\",\"second\":4},{\"first\":\"Hong Hao\",\"second\":4},{\"first\":\"Adelaide\",\"second\":4},{\"first\":\"BEIJING\",\"second\":4},{\"first\":\"MELBOURNE\",\"second\":4},{\"first\":\"Scotland\",\"second\":4},{\"first\":\"Melbourne Park\",\"second\":4},{\"first\":\"Canadian\",\"second\":4},{\"first\":\"Southeast Asian\",\"second\":4},{\"first\":\"WASHINGTON\",\"second\":4},{\"first\":\"JAKARTA\",\"second\":4},{\"first\":\"Turkey\",\"second\":4},{\"first\":\"North Korean\",\"second\":4},{\"first\":\"JOHANNESBURG\",\"second\":4},{\"first\":\"Russian\",\"second\":4},{\"first\":\"Andorra\",\"second\":4},{\"first\":\"Naples\",\"second\":4},{\"first\":\"Vietnam\",\"second\":4},{\"first\":\"North Africa\",\"second\":3},{\"first\":\"Asia-Pacific\",\"second\":3},{\"first\":\"American\",\"second\":3},{\"first\":\"Rome\",\"second\":3},{\"first\":\"Macau\",\"second\":3},{\"first\":\"Surrey\",\"second\":3},{\"first\":\"SPAIN\",\"second\":3},{\"first\":\"Manila\",\"second\":3},{\"first\":\"Mali\",\"second\":3},{\"first\":\"PARIS\",\"second\":3},{\"first\":\"ZURICH\",\"second\":3},{\"first\":\"Southeast Asia\",\"second\":3},{\"first\":\"East China Sea\",\"second\":3},{\"first\":\"England\",\"second\":3},{\"first\":\"East Coast\",\"second\":3},{\"first\":\"Italian\",\"second\":3},{\"first\":\"Zhanjiang\",\"second\":3},{\"first\":\"Gulf\",\"second\":3},{\"first\":\"New York\",\"second\":3},{\"first\":\"Hollywood\",\"second\":3},{\"first\":\"Modbury\",\"second\":2},{\"first\":\"Afghanistan\",\"second\":2},{\"first\":\"DAVOS\",\"second\":2},{\"first\":\"Brittany\",\"second\":2},{\"first\":\"Northumberland\",\"second\":2},{\"first\":\"Bangladesh\",\"second\":2},{\"first\":\"Sportwereld\",\"second\":2},{\"first\":\"Islamabad\",\"second\":2},{\"first\":\"UK\",\"second\":2},{\"first\":\"Canada\",\"second\":2},{\"first\":\"East Midlands\",\"second\":2},{\"first\":\"Treviso\",\"second\":2},{\"first\":\"Ajaccio\",\"second\":2},{\"first\":\"Ivrea\",\"second\":2},{\"first\":\"South West Trains\",\"second\":2},{\"first\":\"SHANGHAI\",\"second\":2},{\"first\":\"Bardonecchia\",\"second\":2},{\"first\":\"Armonk\",\"second\":2},{\"first\":\"Korea\",\"second\":2},{\"first\":\"Israeli\",\"second\":2},{\"first\":\"Seoul\",\"second\":2},{\"first\":\"Critérium\",\"second\":2},{\"first\":\"North\",\"second\":2},{\"first\":\"New York-based\",\"second\":2},{\"first\":\"Virginia\",\"second\":2},{\"first\":\"MANILA\",\"second\":2},{\"first\":\"Pontevedra\",\"second\":2},{\"first\":\"Redhill\",\"second\":2},{\"first\":\"Col du Galibier\",\"second\":2},{\"first\":\"East England\",\"second\":2},{\"first\":\"Vaucluse\",\"second\":2},{\"first\":\"Chongqing\",\"second\":2},{\"first\":\"Middlesbrough\",\"second\":2},{\"first\":\"Estepona\",\"second\":2},{\"first\":\"Adriatic\",\"second\":2},{\"first\":\"Vuelta\",\"second\":2},{\"first\":\"Tarragona\",\"second\":2},{\"first\":\"Tunisia\",\"second\":2},{\"first\":\"Tehran\",\"second\":2},{\"first\":\"Jerez de la Frontera\",\"second\":2},{\"first\":\"Lyon\",\"second\":2},{\"first\":\"Tanunda\",\"second\":2},{\"first\":\"MUMBAI\",\"second\":2},{\"first\":\"Vaison la Romaine-Gap\",\"second\":2},{\"first\":\"Tyrrhenian Sea\",\"second\":2},{\"first\":\"Montreal\",\"second\":2},{\"first\":\"Newtown\",\"second\":2},{\"first\":\"Sansepolcro\",\"second\":2},{\"first\":\"Longarone\",\"second\":2},{\"first\":\"Provence\",\"second\":2},{\"first\":\"Luxembourg\",\"second\":2},{\"first\":\"Melbourne\",\"second\":2},{\"first\":\"Guijuelo\",\"second\":2},{\"first\":\"Aix\",\"second\":2},{\"first\":\"Walloon Arrow\",\"second\":2},{\"first\":\"Annecy\",\"second\":2},{\"first\":\"Algeria\",\"second\":2},{\"first\":\"South Wales\",\"second\":2},{\"first\":\"Ischia\",\"second\":2},{\"first\":\"Almendralejo\",\"second\":2},{\"first\":\"Liège\",\"second\":2},{\"first\":\"Quebec\",\"second\":2},{\"first\":\"Fisterra\",\"second\":2},{\"first\":\"Mainland Chinese\",\"second\":2},{\"first\":\"Toronto\",\"second\":2},{\"first\":\"Tre Cime di Lavaredo\",\"second\":2},{\"first\":\"Firenze\",\"second\":2},{\"first\":\"DUBLIN\",\"second\":2},{\"first\":\"Spanish\",\"second\":2},{\"first\":\"Swiss\",\"second\":2},{\"first\":\"Montasio\",\"second\":2},{\"first\":\"San Sebastián\",\"second\":2},{\"first\":\"HK\",\"second\":2},{\"first\":\"Tarazona\",\"second\":2},{\"first\":\"Serra San Bruno\",\"second\":2},{\"first\":\"Vicenza\",\"second\":2},{\"first\":\"Capitol Hill\",\"second\":2},{\"first\":\"Basta\",\"second\":2},{\"first\":\"Benelux\",\"second\":2},{\"first\":\"San Salvo\",\"second\":2},{\"first\":\"CHINA\",\"second\":2},{\"first\":\"Espa?a\",\"second\":2},{\"first\":\"Valloire\",\"second\":2},{\"first\":\"Wales\",\"second\":2},{\"first\":\"Mirador de Lobeira\",\"second\":2},{\"first\":\"Venezuela\",\"second\":2},{\"first\":\"Pennsylvania\",\"second\":2},{\"first\":\"Denmark Hill\",\"second\":2},{\"first\":\"Compiègne\",\"second\":2},{\"first\":\"Reigate\",\"second\":2},{\"first\":\"Hamburg\",\"second\":2},{\"first\":\"Plouay\",\"second\":2},{\"first\":\"Philippine\",\"second\":2},{\"first\":\"San Vicente de la Barquera\",\"second\":2},{\"first\":\"Bastogne\",\"second\":2},{\"first\":\"Munich\",\"second\":2},{\"first\":\"Calahorra\",\"second\":2},{\"first\":\"Southeastern\",\"second\":2},{\"first\":\"United Kingdom\",\"second\":2},{\"first\":\"Iranian\",\"second\":2},{\"first\":\"Lombardia\",\"second\":2},{\"first\":\"Korean peninsula\",\"second\":2},{\"first\":\"London City Airport\",\"second\":1},{\"first\":\"Morocco\",\"second\":1},{\"first\":\"Far East\",\"second\":1},{\"first\":\"West\",\"second\":1},{\"first\":\"Pennines\",\"second\":1},{\"first\":\"Otterburn\",\"second\":1},{\"first\":\"Springfield\",\"second\":1},{\"first\":\"Flatts Lane Country Park\",\"second\":1},{\"first\":\"New Hampshire\",\"second\":1},{\"first\":\"Asia Pacific\",\"second\":1},{\"first\":\"Midlands\",\"second\":1},{\"first\":\"Palestinian Authority\",\"second\":1},{\"first\":\"New York City\",\"second\":1},{\"first\":\"Potomac\",\"second\":1},{\"first\":\"Piccadilly\",\"second\":1},{\"first\":\"District\",\"second\":1},{\"first\":\"Henley-On-Thames\",\"second\":1},{\"first\":\"Trafalgar Square\",\"second\":1},{\"first\":\"Paris Orleans\",\"second\":1},{\"first\":\"Alexandra Palace\",\"second\":1},{\"first\":\"Northern Ireland\",\"second\":1},{\"first\":\"Arabian Gulf\",\"second\":1},{\"first\":\"Cumbria\",\"second\":1},{\"first\":\"Las Vegas\",\"second\":1},{\"first\":\"Qianhai\",\"second\":1},{\"first\":\"Appalachia\",\"second\":1},{\"first\":\"Mongolia\",\"second\":1},{\"first\":\"Persian Gulf\",\"second\":1},{\"first\":\"Servi Optica\",\"second\":1},{\"first\":\"Gulf of Aden\",\"second\":1},{\"first\":\"Pacific Ocean\",\"second\":1},{\"first\":\"London Victoria\",\"second\":1},{\"first\":\"south\",\"second\":1},{\"first\":\"Syrian\",\"second\":1},{\"first\":\"Doncaster\",\"second\":1},{\"first\":\"Auckland\",\"second\":1},{\"first\":\"Guangzhou\",\"second\":1},{\"first\":\"Thailand\",\"second\":1},{\"first\":\"Northumbria\",\"second\":1},{\"first\":\"Baghdad\",\"second\":1},{\"first\":\"Florida\",\"second\":1},{\"first\":\"Brunei\",\"second\":1},{\"first\":\"Church Meadow\",\"second\":1},{\"first\":\"Al Arabiya Urdu\",\"second\":1},{\"first\":\"Berlin Wall\",\"second\":1},{\"first\":\"Israeli-Palestinian\",\"second\":1},{\"first\":\"Navajo\",\"second\":1},{\"first\":\"Kuwait\",\"second\":1},{\"first\":\"Chavez Venezuela\",\"second\":1},{\"first\":\"Ghana\",\"second\":1},{\"first\":\"Hanau\",\"second\":1},{\"first\":\"Lahore\",\"second\":1},{\"first\":\"Twyford\",\"second\":1},{\"first\":\"London City\",\"second\":1},{\"first\":\"Muritz\",\"second\":1},{\"first\":\"Honolulu\",\"second\":1},{\"first\":\"Dalian\",\"second\":1},{\"first\":\"Weybridge\",\"second\":1},{\"first\":\"Armenia\",\"second\":1},{\"first\":\"South East\",\"second\":1},{\"first\":\"Croatia\",\"second\":1},{\"first\":\"New Jersey\",\"second\":1},{\"first\":\"Michigan\",\"second\":1},{\"first\":\"Nanjing\",\"second\":1},{\"first\":\"Oxford\",\"second\":1},{\"first\":\"County Durham\",\"second\":1},{\"first\":\"Detroit\",\"second\":1},{\"first\":\"Kenya\",\"second\":1},{\"first\":\"KUALA LUMPUR\",\"second\":1},{\"first\":\"Earth\",\"second\":1},{\"first\":\"South East of England\",\"second\":1},{\"first\":\"Baldock Shuttle\",\"second\":1},{\"first\":\"Togo\",\"second\":1},{\"first\":\"Gulf States\",\"second\":1},{\"first\":\"Al-Eskandariya City\",\"second\":1},{\"first\":\"Rod Laver Arena\",\"second\":1},{\"first\":\"Portugal\",\"second\":1},{\"first\":\"Portsmouth\",\"second\":1},{\"first\":\"Brighton\",\"second\":1},{\"first\":\"South West England\",\"second\":1},{\"first\":\"M3\",\"second\":1},{\"first\":\"Hong Kong-listed\",\"second\":1},{\"first\":\"San Francisco\",\"second\":1},{\"first\":\"Haywards Heath\",\"second\":1},{\"first\":\"Church Meadows\",\"second\":1},{\"first\":\"Rio Tinto\",\"second\":1},{\"first\":\"Wolfsburg\",\"second\":1},{\"first\":\"Czech Republic\",\"second\":1},{\"first\":\"Kansas\",\"second\":1},{\"first\":\"Benin\",\"second\":1},{\"first\":\"North West England\",\"second\":1},{\"first\":\"South West\",\"second\":1},{\"first\":\"Moscow\",\"second\":1},{\"first\":\"Kharg\",\"second\":1},{\"first\":\"Caribbean\",\"second\":1},{\"first\":\"NEW DELHI\",\"second\":1},{\"first\":\"Strait of Hormuz\",\"second\":1},{\"first\":\"West Midlands\",\"second\":1},{\"first\":\"Bristol\",\"second\":1},{\"first\":\"Jakarta\",\"second\":1},{\"first\":\"Buckinghamshire\",\"second\":1},{\"first\":\"Latin America\",\"second\":1},{\"first\":\"Trent Bridge\",\"second\":1},{\"first\":\"Bordeaux\",\"second\":1},{\"first\":\"Valemaxes\",\"second\":1},{\"first\":\"Connecticut\",\"second\":1},{\"first\":\"Hungary\",\"second\":1},{\"first\":\"Arizona\",\"second\":1},{\"first\":\"Georgia\",\"second\":1},{\"first\":\"Norway\",\"second\":1},{\"first\":\"Somerset\",\"second\":1},{\"first\":\"Llanfach\",\"second\":1},{\"first\":\"Thames Valley\",\"second\":1},{\"first\":\"Yorkshire\",\"second\":1},{\"first\":\"Southern China\",\"second\":1},{\"first\":\"Indian\",\"second\":1},{\"first\":\"Seattle\",\"second\":1},{\"first\":\"Jordan\",\"second\":1},{\"first\":\"Indonesian\",\"second\":1},{\"first\":\"Maryland\",\"second\":1},{\"first\":\"Aqua\",\"second\":1},{\"first\":\"Myanmar\",\"second\":1},{\"first\":\"Gobi\",\"second\":1},{\"first\":\"London Bridge\",\"second\":1},{\"first\":\"New Komeito\",\"second\":1},{\"first\":\"Libya\",\"second\":1},{\"first\":\"South Croydon\",\"second\":1},{\"first\":\"Gansu\",\"second\":1},{\"first\":\"Rothschild\",\"second\":1},{\"first\":\"Hampshire\",\"second\":1},{\"first\":\"Edinburgh\",\"second\":1},{\"first\":\"Qatar\",\"second\":1},{\"first\":\"Guangxi\",\"second\":1},{\"first\":\"Caerphilly\",\"second\":1},{\"first\":\"Richmond\",\"second\":1},{\"first\":\"Newcastle\",\"second\":1},{\"first\":\"Israel\",\"second\":1},{\"first\":\"Guangdong\",\"second\":1},{\"first\":\"Azerbaijan\",\"second\":1},{\"first\":\"Ipswich\",\"second\":1},{\"first\":\"MURITZ LAMB\",\"second\":1},{\"first\":\"Colchester\",\"second\":1},{\"first\":\"Finland\",\"second\":1},{\"first\":\"Salisbury\",\"second\":1},{\"first\":\"KS\",\"second\":1},{\"first\":\"North East England\",\"second\":1},{\"first\":\"Sherwood Pines Forest Park\",\"second\":1},{\"first\":\"Zurich\",\"second\":1},{\"first\":\"Notts\",\"second\":1},{\"first\":\"Benapenem\",\"second\":1},{\"first\":\"Lewes\",\"second\":1}]";
			// Log.d("string",string);
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

			for (int i = 4; i < list.size(); i++)
				list.remove(i);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public void getplaces() {
		try {
			String theurl = "http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=GetHot&tf=2&type=1";

			String string = HttpConn.getJsonFromUrlGet(theurl);
			// String string
			// ="[{\"first\":\"China\",\"second\":180},{\"first\":\"U.S.\",\"second\":86},{\"first\":\"Iran\",\"second\":59},{\"first\":\"Hong Kong\",\"second\":56},{\"first\":\"Japan\",\"second\":42},{\"first\":\"Beijing\",\"second\":38},{\"first\":\"United States\",\"second\":36},{\"first\":\"North Korea\",\"second\":31},{\"first\":\"France\",\"second\":27},{\"first\":\"London\",\"second\":27},{\"first\":\"India\",\"second\":26},{\"first\":\"Davos\",\"second\":26},{\"first\":\"Shanghai\",\"second\":23},{\"first\":\"Asia\",\"second\":23},{\"first\":\"Pyongyang\",\"second\":22},{\"first\":\"Chinese\",\"second\":21},{\"first\":\"Germany\",\"second\":20},{\"first\":\"Europe\",\"second\":19},{\"first\":\"HONG KONG\",\"second\":18},{\"first\":\"Indonesia\",\"second\":18},{\"first\":\"Spain\",\"second\":17},{\"first\":\"Russia\",\"second\":13},{\"first\":\"Italy\",\"second\":13},{\"first\":\"European\",\"second\":13},{\"first\":\"Belgium\",\"second\":12},{\"first\":\"Philippines\",\"second\":11},{\"first\":\"Asian\",\"second\":11},{\"first\":\"Switzerland\",\"second\":11},{\"first\":\"Britain\",\"second\":11},{\"first\":\"America\",\"second\":11},{\"first\":\"Washington\",\"second\":10},{\"first\":\"Egypt\",\"second\":10},{\"first\":\"Lombardy\",\"second\":10},{\"first\":\"South Korea\",\"second\":10},{\"first\":\"Shenzhen\",\"second\":9},{\"first\":\"Frankfurt\",\"second\":9},{\"first\":\"Syria\",\"second\":9},{\"first\":\"LONDON\",\"second\":8},{\"first\":\"Middle East\",\"second\":8},{\"first\":\"Hangzhou\",\"second\":8},{\"first\":\"South Africa\",\"second\":8},{\"first\":\"TOKYO\",\"second\":8},{\"first\":\"South China Sea\",\"second\":7},{\"first\":\"Heathrow\",\"second\":7},{\"first\":\"Pakistan\",\"second\":7},{\"first\":\"Malaysia\",\"second\":7},{\"first\":\"US\",\"second\":6},{\"first\":\"Tokyo\",\"second\":6},{\"first\":\"Holland\",\"second\":6},{\"first\":\"Ireland\",\"second\":6},{\"first\":\"Poland\",\"second\":6},{\"first\":\"Africa\",\"second\":6},{\"first\":\"Australia\",\"second\":6},{\"first\":\"Singapore\",\"second\":6},{\"first\":\"French\",\"second\":5},{\"first\":\"Taiwan\",\"second\":5},{\"first\":\"Iraq\",\"second\":5},{\"first\":\"Paris\",\"second\":5},{\"first\":\"Madrid\",\"second\":5},{\"first\":\"Sydney\",\"second\":5},{\"first\":\"German\",\"second\":5},{\"first\":\"NEW YORK\",\"second\":5},{\"first\":\"Brazil\",\"second\":5},{\"first\":\"Gatwick\",\"second\":5},{\"first\":\"academia\",\"second\":4},{\"first\":\"Hong Hao\",\"second\":4},{\"first\":\"Adelaide\",\"second\":4},{\"first\":\"BEIJING\",\"second\":4},{\"first\":\"MELBOURNE\",\"second\":4},{\"first\":\"Scotland\",\"second\":4},{\"first\":\"Melbourne Park\",\"second\":4},{\"first\":\"Canadian\",\"second\":4},{\"first\":\"Southeast Asian\",\"second\":4},{\"first\":\"WASHINGTON\",\"second\":4},{\"first\":\"JAKARTA\",\"second\":4},{\"first\":\"Turkey\",\"second\":4},{\"first\":\"North Korean\",\"second\":4},{\"first\":\"JOHANNESBURG\",\"second\":4},{\"first\":\"Russian\",\"second\":4},{\"first\":\"Andorra\",\"second\":4},{\"first\":\"Naples\",\"second\":4},{\"first\":\"Vietnam\",\"second\":4},{\"first\":\"North Africa\",\"second\":3},{\"first\":\"Asia-Pacific\",\"second\":3},{\"first\":\"American\",\"second\":3},{\"first\":\"Rome\",\"second\":3},{\"first\":\"Macau\",\"second\":3},{\"first\":\"Surrey\",\"second\":3},{\"first\":\"SPAIN\",\"second\":3},{\"first\":\"Manila\",\"second\":3},{\"first\":\"Mali\",\"second\":3},{\"first\":\"PARIS\",\"second\":3},{\"first\":\"ZURICH\",\"second\":3},{\"first\":\"Southeast Asia\",\"second\":3},{\"first\":\"East China Sea\",\"second\":3},{\"first\":\"England\",\"second\":3},{\"first\":\"East Coast\",\"second\":3},{\"first\":\"Italian\",\"second\":3},{\"first\":\"Zhanjiang\",\"second\":3},{\"first\":\"Gulf\",\"second\":3},{\"first\":\"New York\",\"second\":3},{\"first\":\"Hollywood\",\"second\":3},{\"first\":\"Modbury\",\"second\":2},{\"first\":\"Afghanistan\",\"second\":2},{\"first\":\"DAVOS\",\"second\":2},{\"first\":\"Brittany\",\"second\":2},{\"first\":\"Northumberland\",\"second\":2},{\"first\":\"Bangladesh\",\"second\":2},{\"first\":\"Sportwereld\",\"second\":2},{\"first\":\"Islamabad\",\"second\":2},{\"first\":\"UK\",\"second\":2},{\"first\":\"Canada\",\"second\":2},{\"first\":\"East Midlands\",\"second\":2},{\"first\":\"Treviso\",\"second\":2},{\"first\":\"Ajaccio\",\"second\":2},{\"first\":\"Ivrea\",\"second\":2},{\"first\":\"South West Trains\",\"second\":2},{\"first\":\"SHANGHAI\",\"second\":2},{\"first\":\"Bardonecchia\",\"second\":2},{\"first\":\"Armonk\",\"second\":2},{\"first\":\"Korea\",\"second\":2},{\"first\":\"Israeli\",\"second\":2},{\"first\":\"Seoul\",\"second\":2},{\"first\":\"Critérium\",\"second\":2},{\"first\":\"North\",\"second\":2},{\"first\":\"New York-based\",\"second\":2},{\"first\":\"Virginia\",\"second\":2},{\"first\":\"MANILA\",\"second\":2},{\"first\":\"Pontevedra\",\"second\":2},{\"first\":\"Redhill\",\"second\":2},{\"first\":\"Col du Galibier\",\"second\":2},{\"first\":\"East England\",\"second\":2},{\"first\":\"Vaucluse\",\"second\":2},{\"first\":\"Chongqing\",\"second\":2},{\"first\":\"Middlesbrough\",\"second\":2},{\"first\":\"Estepona\",\"second\":2},{\"first\":\"Adriatic\",\"second\":2},{\"first\":\"Vuelta\",\"second\":2},{\"first\":\"Tarragona\",\"second\":2},{\"first\":\"Tunisia\",\"second\":2},{\"first\":\"Tehran\",\"second\":2},{\"first\":\"Jerez de la Frontera\",\"second\":2},{\"first\":\"Lyon\",\"second\":2},{\"first\":\"Tanunda\",\"second\":2},{\"first\":\"MUMBAI\",\"second\":2},{\"first\":\"Vaison la Romaine-Gap\",\"second\":2},{\"first\":\"Tyrrhenian Sea\",\"second\":2},{\"first\":\"Montreal\",\"second\":2},{\"first\":\"Newtown\",\"second\":2},{\"first\":\"Sansepolcro\",\"second\":2},{\"first\":\"Longarone\",\"second\":2},{\"first\":\"Provence\",\"second\":2},{\"first\":\"Luxembourg\",\"second\":2},{\"first\":\"Melbourne\",\"second\":2},{\"first\":\"Guijuelo\",\"second\":2},{\"first\":\"Aix\",\"second\":2},{\"first\":\"Walloon Arrow\",\"second\":2},{\"first\":\"Annecy\",\"second\":2},{\"first\":\"Algeria\",\"second\":2},{\"first\":\"South Wales\",\"second\":2},{\"first\":\"Ischia\",\"second\":2},{\"first\":\"Almendralejo\",\"second\":2},{\"first\":\"Liège\",\"second\":2},{\"first\":\"Quebec\",\"second\":2},{\"first\":\"Fisterra\",\"second\":2},{\"first\":\"Mainland Chinese\",\"second\":2},{\"first\":\"Toronto\",\"second\":2},{\"first\":\"Tre Cime di Lavaredo\",\"second\":2},{\"first\":\"Firenze\",\"second\":2},{\"first\":\"DUBLIN\",\"second\":2},{\"first\":\"Spanish\",\"second\":2},{\"first\":\"Swiss\",\"second\":2},{\"first\":\"Montasio\",\"second\":2},{\"first\":\"San Sebastián\",\"second\":2},{\"first\":\"HK\",\"second\":2},{\"first\":\"Tarazona\",\"second\":2},{\"first\":\"Serra San Bruno\",\"second\":2},{\"first\":\"Vicenza\",\"second\":2},{\"first\":\"Capitol Hill\",\"second\":2},{\"first\":\"Basta\",\"second\":2},{\"first\":\"Benelux\",\"second\":2},{\"first\":\"San Salvo\",\"second\":2},{\"first\":\"CHINA\",\"second\":2},{\"first\":\"Espa?a\",\"second\":2},{\"first\":\"Valloire\",\"second\":2},{\"first\":\"Wales\",\"second\":2},{\"first\":\"Mirador de Lobeira\",\"second\":2},{\"first\":\"Venezuela\",\"second\":2},{\"first\":\"Pennsylvania\",\"second\":2},{\"first\":\"Denmark Hill\",\"second\":2},{\"first\":\"Compiègne\",\"second\":2},{\"first\":\"Reigate\",\"second\":2},{\"first\":\"Hamburg\",\"second\":2},{\"first\":\"Plouay\",\"second\":2},{\"first\":\"Philippine\",\"second\":2},{\"first\":\"San Vicente de la Barquera\",\"second\":2},{\"first\":\"Bastogne\",\"second\":2},{\"first\":\"Munich\",\"second\":2},{\"first\":\"Calahorra\",\"second\":2},{\"first\":\"Southeastern\",\"second\":2},{\"first\":\"United Kingdom\",\"second\":2},{\"first\":\"Iranian\",\"second\":2},{\"first\":\"Lombardia\",\"second\":2},{\"first\":\"Korean peninsula\",\"second\":2},{\"first\":\"London City Airport\",\"second\":1},{\"first\":\"Morocco\",\"second\":1},{\"first\":\"Far East\",\"second\":1},{\"first\":\"West\",\"second\":1},{\"first\":\"Pennines\",\"second\":1},{\"first\":\"Otterburn\",\"second\":1},{\"first\":\"Springfield\",\"second\":1},{\"first\":\"Flatts Lane Country Park\",\"second\":1},{\"first\":\"New Hampshire\",\"second\":1},{\"first\":\"Asia Pacific\",\"second\":1},{\"first\":\"Midlands\",\"second\":1},{\"first\":\"Palestinian Authority\",\"second\":1},{\"first\":\"New York City\",\"second\":1},{\"first\":\"Potomac\",\"second\":1},{\"first\":\"Piccadilly\",\"second\":1},{\"first\":\"District\",\"second\":1},{\"first\":\"Henley-On-Thames\",\"second\":1},{\"first\":\"Trafalgar Square\",\"second\":1},{\"first\":\"Paris Orleans\",\"second\":1},{\"first\":\"Alexandra Palace\",\"second\":1},{\"first\":\"Northern Ireland\",\"second\":1},{\"first\":\"Arabian Gulf\",\"second\":1},{\"first\":\"Cumbria\",\"second\":1},{\"first\":\"Las Vegas\",\"second\":1},{\"first\":\"Qianhai\",\"second\":1},{\"first\":\"Appalachia\",\"second\":1},{\"first\":\"Mongolia\",\"second\":1},{\"first\":\"Persian Gulf\",\"second\":1},{\"first\":\"Servi Optica\",\"second\":1},{\"first\":\"Gulf of Aden\",\"second\":1},{\"first\":\"Pacific Ocean\",\"second\":1},{\"first\":\"London Victoria\",\"second\":1},{\"first\":\"south\",\"second\":1},{\"first\":\"Syrian\",\"second\":1},{\"first\":\"Doncaster\",\"second\":1},{\"first\":\"Auckland\",\"second\":1},{\"first\":\"Guangzhou\",\"second\":1},{\"first\":\"Thailand\",\"second\":1},{\"first\":\"Northumbria\",\"second\":1},{\"first\":\"Baghdad\",\"second\":1},{\"first\":\"Florida\",\"second\":1},{\"first\":\"Brunei\",\"second\":1},{\"first\":\"Church Meadow\",\"second\":1},{\"first\":\"Al Arabiya Urdu\",\"second\":1},{\"first\":\"Berlin Wall\",\"second\":1},{\"first\":\"Israeli-Palestinian\",\"second\":1},{\"first\":\"Navajo\",\"second\":1},{\"first\":\"Kuwait\",\"second\":1},{\"first\":\"Chavez Venezuela\",\"second\":1},{\"first\":\"Ghana\",\"second\":1},{\"first\":\"Hanau\",\"second\":1},{\"first\":\"Lahore\",\"second\":1},{\"first\":\"Twyford\",\"second\":1},{\"first\":\"London City\",\"second\":1},{\"first\":\"Muritz\",\"second\":1},{\"first\":\"Honolulu\",\"second\":1},{\"first\":\"Dalian\",\"second\":1},{\"first\":\"Weybridge\",\"second\":1},{\"first\":\"Armenia\",\"second\":1},{\"first\":\"South East\",\"second\":1},{\"first\":\"Croatia\",\"second\":1},{\"first\":\"New Jersey\",\"second\":1},{\"first\":\"Michigan\",\"second\":1},{\"first\":\"Nanjing\",\"second\":1},{\"first\":\"Oxford\",\"second\":1},{\"first\":\"County Durham\",\"second\":1},{\"first\":\"Detroit\",\"second\":1},{\"first\":\"Kenya\",\"second\":1},{\"first\":\"KUALA LUMPUR\",\"second\":1},{\"first\":\"Earth\",\"second\":1},{\"first\":\"South East of England\",\"second\":1},{\"first\":\"Baldock Shuttle\",\"second\":1},{\"first\":\"Togo\",\"second\":1},{\"first\":\"Gulf States\",\"second\":1},{\"first\":\"Al-Eskandariya City\",\"second\":1},{\"first\":\"Rod Laver Arena\",\"second\":1},{\"first\":\"Portugal\",\"second\":1},{\"first\":\"Portsmouth\",\"second\":1},{\"first\":\"Brighton\",\"second\":1},{\"first\":\"South West England\",\"second\":1},{\"first\":\"M3\",\"second\":1},{\"first\":\"Hong Kong-listed\",\"second\":1},{\"first\":\"San Francisco\",\"second\":1},{\"first\":\"Haywards Heath\",\"second\":1},{\"first\":\"Church Meadows\",\"second\":1},{\"first\":\"Rio Tinto\",\"second\":1},{\"first\":\"Wolfsburg\",\"second\":1},{\"first\":\"Czech Republic\",\"second\":1},{\"first\":\"Kansas\",\"second\":1},{\"first\":\"Benin\",\"second\":1},{\"first\":\"North West England\",\"second\":1},{\"first\":\"South West\",\"second\":1},{\"first\":\"Moscow\",\"second\":1},{\"first\":\"Kharg\",\"second\":1},{\"first\":\"Caribbean\",\"second\":1},{\"first\":\"NEW DELHI\",\"second\":1},{\"first\":\"Strait of Hormuz\",\"second\":1},{\"first\":\"West Midlands\",\"second\":1},{\"first\":\"Bristol\",\"second\":1},{\"first\":\"Jakarta\",\"second\":1},{\"first\":\"Buckinghamshire\",\"second\":1},{\"first\":\"Latin America\",\"second\":1},{\"first\":\"Trent Bridge\",\"second\":1},{\"first\":\"Bordeaux\",\"second\":1},{\"first\":\"Valemaxes\",\"second\":1},{\"first\":\"Connecticut\",\"second\":1},{\"first\":\"Hungary\",\"second\":1},{\"first\":\"Arizona\",\"second\":1},{\"first\":\"Georgia\",\"second\":1},{\"first\":\"Norway\",\"second\":1},{\"first\":\"Somerset\",\"second\":1},{\"first\":\"Llanfach\",\"second\":1},{\"first\":\"Thames Valley\",\"second\":1},{\"first\":\"Yorkshire\",\"second\":1},{\"first\":\"Southern China\",\"second\":1},{\"first\":\"Indian\",\"second\":1},{\"first\":\"Seattle\",\"second\":1},{\"first\":\"Jordan\",\"second\":1},{\"first\":\"Indonesian\",\"second\":1},{\"first\":\"Maryland\",\"second\":1},{\"first\":\"Aqua\",\"second\":1},{\"first\":\"Myanmar\",\"second\":1},{\"first\":\"Gobi\",\"second\":1},{\"first\":\"London Bridge\",\"second\":1},{\"first\":\"New Komeito\",\"second\":1},{\"first\":\"Libya\",\"second\":1},{\"first\":\"South Croydon\",\"second\":1},{\"first\":\"Gansu\",\"second\":1},{\"first\":\"Rothschild\",\"second\":1},{\"first\":\"Hampshire\",\"second\":1},{\"first\":\"Edinburgh\",\"second\":1},{\"first\":\"Qatar\",\"second\":1},{\"first\":\"Guangxi\",\"second\":1},{\"first\":\"Caerphilly\",\"second\":1},{\"first\":\"Richmond\",\"second\":1},{\"first\":\"Newcastle\",\"second\":1},{\"first\":\"Israel\",\"second\":1},{\"first\":\"Guangdong\",\"second\":1},{\"first\":\"Azerbaijan\",\"second\":1},{\"first\":\"Ipswich\",\"second\":1},{\"first\":\"MURITZ LAMB\",\"second\":1},{\"first\":\"Colchester\",\"second\":1},{\"first\":\"Finland\",\"second\":1},{\"first\":\"Salisbury\",\"second\":1},{\"first\":\"KS\",\"second\":1},{\"first\":\"North East England\",\"second\":1},{\"first\":\"Sherwood Pines Forest Park\",\"second\":1},{\"first\":\"Zurich\",\"second\":1},{\"first\":\"Notts\",\"second\":1},{\"first\":\"Benapenem\",\"second\":1},{\"first\":\"Lewes\",\"second\":1}]";
			JSONArray jay = new JSONArray(string);
			for (int i = 0; i < jay.length(); i += 1) {
				JSONObject temp = (JSONObject) jay.get(i);
				String title = (String) temp.getString("first");
				Log.d("json", title);
				String heat = (String) temp.getString("second");
				Log.d("json", heat);
				dbadapter.insertplace(title, Integer.parseInt(heat));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void getdivisions() {
		try {
			String theurl = "http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=GetHot&tf=2&type=2";

			String string = HttpConn.getJsonFromUrlGet(theurl);
			// String string
			// ="[{\"first\":\"Reuters Breakingviews\",\"second\":44},{\"first\":\"BOJ\",\"second\":43},{\"first\":\"Thomson Reuters StarMine\",\"second\":33},{\"first\":\"U.N. Security Council\",\"second\":28},{\"first\":\"IBM\",\"second\":23},{\"first\":\"Bank of Japan\",\"second\":18},{\"first\":\"S\u0026P\",\"second\":15},{\"first\":\"Asharq Al-Awsat\",\"second\":14},{\"first\":\"Toyota Motor Corp\",\"second\":12},{\"first\":\"AIA Group\",\"second\":12},{\"first\":\"TBC\",\"second\":12},{\"first\":\"ZEW\",\"second\":12},{\"first\":\"MSCI\",\"second\":11},{\"first\":\"Glencore\",\"second\":10},{\"first\":\"ROTHSCHILDS Rothschild\",\"second\":10},{\"first\":\"U.S. House of Representatives\",\"second\":10},{\"first\":\"China Vanke Co.\",\"second\":10},{\"first\":\"Dow Jones Newswires\",\"second\":9},{\"first\":\"\\* Great Wall Motor Co Ltd\",\"second\":9},{\"first\":\"Standard \u0026 Poor \u0027s\",\"second\":8},{\"first\":\"Richmond Fed\",\"second\":8},{\"first\":\"FDI\",\"second\":8},{\"first\":\"Google Inc\",\"second\":8},{\"first\":\"International Centre for Development Studies Iran\",\"second\":7},{\"first\":\"ILFC\",\"second\":7},{\"first\":\"UBSN\",\"second\":7},{\"first\":\"Communist Party Congress\",\"second\":7},{\"first\":\"Kingfisher Airlines\",\"second\":6},{\"first\":\"Amstel Gold\",\"second\":6},{\"first\":\"DAX\",\"second\":6},{\"first\":\"IEA\",\"second\":6},{\"first\":\"WEF\",\"second\":6},{\"first\":\"UCI\",\"second\":6},{\"first\":\"Vale SA\",\"second\":6},{\"first\":\"Vanke Properties Overseas Ltd\",\"second\":6},{\"first\":\"International Business Machines Corp\",\"second\":6},{\"first\":\"White House\",\"second\":6},{\"first\":\"Apple Inc\",\"second\":6},{\"first\":\"Credit Suisse\",\"second\":6},{\"first\":\"United Nations Susan Rice\",\"second\":6},{\"first\":\"Nice\",\"second\":6},{\"first\":\"Citic Securities\",\"second\":5},{\"first\":\"Xinhua\",\"second\":5},{\"first\":\"KPMG China\",\"second\":5},{\"first\":\"European Central Bank\",\"second\":5},{\"first\":\"United Nations Convention\",\"second\":5},{\"first\":\"Baosteel Group\",\"second\":5},{\"first\":\"BHP Billiton\",\"second\":5},{\"first\":\"Citigroup\",\"second\":4},{\"first\":\"L\u0027Oreal\",\"second\":4},{\"first\":\"Treasury\",\"second\":4},{\"first\":\"Essilor International\",\"second\":4},{\"first\":\"Vattenfall Cyclassics\",\"second\":4},{\"first\":\"Bank of Communication International\",\"second\":4},{\"first\":\"Eneco Tour\",\"second\":4},{\"first\":\"First Capital Connect\",\"second\":4},{\"first\":\"International Cycling Union\",\"second\":4},{\"first\":\"Espa?a\",\"second\":4},{\"first\":\"ECB\",\"second\":4},{\"first\":\"Asia-Pacific\",\"second\":4},{\"first\":\"Montpellier\",\"second\":4},{\"first\":\"Supreme Court\",\"second\":4},{\"first\":\"AAPL\",\"second\":4},{\"first\":\"Nikkei Stock Average\",\"second\":4},{\"first\":\"Airbus\",\"second\":4},{\"first\":\"Team Sky\",\"second\":4},{\"first\":\"Ghent\",\"second\":4},{\"first\":\"CNPC\",\"second\":4},{\"first\":\"CNN\",\"second\":4},{\"first\":\"HSI\",\"second\":4},{\"first\":\"AIG\",\"second\":4},{\"first\":\"LME Alum\",\"second\":4},{\"first\":\"Philadelphia Federal Reserve Bank\",\"second\":4},{\"first\":\"Deutsche Bank\",\"second\":3},{\"first\":\"KWG Property\",\"second\":3},{\"first\":\"Magic Mountain\",\"second\":3},{\"first\":\"Asean\",\"second\":3},{\"first\":\"Barclays Capital\",\"second\":3},{\"first\":\"SOEs\",\"second\":3},{\"first\":\"DuPont\",\"second\":3},{\"first\":\"Clement Tan\",\"second\":3},{\"first\":\"Verizon Communications\",\"second\":3},{\"first\":\"Shenzhen International Holdings\",\"second\":3},{\"first\":\"Intel Corp\",\"second\":3},{\"first\":\"South West Trains\",\"second\":3},{\"first\":\"National Front\",\"second\":3},{\"first\":\"COMEX Cu\",\"second\":3},{\"first\":\"Royal Mail\",\"second\":3},{\"first\":\"SIEMENS Newspaper Handelsblatt\",\"second\":3},{\"first\":\"Parliament\",\"second\":3},{\"first\":\"PPP\",\"second\":3},{\"first\":\"Atari Interactive\",\"second\":3},{\"first\":\"Chinese Foreign Ministry Spokesman\",\"second\":3},{\"first\":\"World Bank\",\"second\":3},{\"first\":\"Ministry of Land and Resources\",\"second\":2},{\"first\":\"Korea Kumryong Trading Corp.\",\"second\":2},{\"first\":\"DPRK\",\"second\":2},{\"first\":\"Bastia\",\"second\":2},{\"first\":\"Lalín\",\"second\":2},{\"first\":\"Bank of East Land\",\"second\":2},{\"first\":\"ASO\",\"second\":2},{\"first\":\"Cheung Kong Holdings\",\"second\":2},{\"first\":\"IBERDROLA\",\"second\":2},{\"first\":\"Porto Vecchio\",\"second\":2},{\"first\":\"OCBC\",\"second\":2},{\"first\":\"Cortal Consors\",\"second\":2},{\"first\":\"Goldman Sachs Group Inc.\",\"second\":2},{\"first\":\"HEW Cyclassics\",\"second\":2},{\"first\":\"Microsoft\",\"second\":2},{\"first\":\"Davos Congress Centre\",\"second\":2},{\"first\":\"Keidanren\",\"second\":2},{\"first\":\"Eskom\",\"second\":2},{\"first\":\"Forrester Research\",\"second\":2},{\"first\":\"VOLVO Norway\",\"second\":2},{\"first\":\"Iraqi Ministry of Trade\",\"second\":2},{\"first\":\"KCNA\",\"second\":2},{\"first\":\"Northumbria Police\",\"second\":2},{\"first\":\"Essex\",\"second\":2},{\"first\":\"Bank of England\",\"second\":2},{\"first\":\"Democratic Party of Japan\",\"second\":2},{\"first\":\"ISI Group\",\"second\":2},{\"first\":\"Sinopec\",\"second\":2},{\"first\":\"Givors\",\"second\":2},{\"first\":\"Transport for London\",\"second\":2},{\"first\":\"La Gazzetta\",\"second\":2},{\"first\":\"St Nazaire\",\"second\":2},{\"first\":\"Torredelcampo\",\"second\":2},{\"first\":\"Saint-Nicolas\",\"second\":2},{\"first\":\"FTSE Bursa Malaysia KLCI\",\"second\":2},{\"first\":\"Central Bank of Iran\",\"second\":2},{\"first\":\"Brescia\",\"second\":2},{\"first\":\"Boeing\",\"second\":2},{\"first\":\"Castres\",\"second\":2},{\"first\":\"Bagà\",\"second\":2},{\"first\":\"Christian Dior\",\"second\":2},{\"first\":\"Fougeres\",\"second\":2},{\"first\":\"Cesana Torinese\",\"second\":2},{\"first\":\"BANCA MONTE DEI PASCHI DI SIENA\",\"second\":2},{\"first\":\"Pescara\",\"second\":2},{\"first\":\"Travelers\",\"second\":2},{\"first\":\"Maella\",\"second\":2},{\"first\":\"Paris-Roubaix\",\"second\":2},{\"first\":\"CNOOC\",\"second\":2},{\"first\":\"St Girons-Bagneres de Bigorre\",\"second\":2},{\"first\":\"Finance Ministry\",\"second\":2},{\"first\":\"Standard and Poor\",\"second\":2},{\"first\":\"Virgin Trains\",\"second\":2},{\"first\":\"Ponte di Legno\",\"second\":2},{\"first\":\"ENAGAS\",\"second\":2},{\"first\":\"Republicans\",\"second\":2},{\"first\":\"Cauberg\",\"second\":2},{\"first\":\"Michelin\",\"second\":2},{\"first\":\"Leganés\",\"second\":2},{\"first\":\"Winsor Properties\",\"second\":2},{\"first\":\"Texas Instruments\",\"second\":2},{\"first\":\"Cordenons\",\"second\":2},{\"first\":\"Ajaccio\",\"second\":2},{\"first\":\"Nissan Motor Co\",\"second\":2},{\"first\":\"La Grande Boucle\",\"second\":2},{\"first\":\"HSBC Holdings\",\"second\":2},{\"first\":\"LVMH Moet Hennessy Louis Vuitton\",\"second\":2},{\"first\":\"CDC\",\"second\":2},{\"first\":\"St Amand Montrond\",\"second\":2},{\"first\":\"Tosong Technology Trading Corp.\",\"second\":2},{\"first\":\"Xstrata\",\"second\":2},{\"first\":\"Bari\",\"second\":2},{\"first\":\"Kent Police\",\"second\":2},{\"first\":\"National Energy Administration\",\"second\":2},{\"first\":\"MLR\",\"second\":2},{\"first\":\"Korean Committee for Space Technology\",\"second\":2},{\"first\":\"Army\",\"second\":2},{\"first\":\"Ministry of Oil\",\"second\":2},{\"first\":\"Amaury Sport Organisation\",\"second\":2},{\"first\":\"Fifa\",\"second\":2},{\"first\":\"Ryonha Machinery Joint Venture Corp.\",\"second\":2},{\"first\":\"McKinsey \u0026 Co.\",\"second\":2},{\"first\":\"McLaren Vale\",\"second\":2},{\"first\":\"Naranco\",\"second\":2},{\"first\":\"Vuelta\",\"second\":2},{\"first\":\"Przeglad Sportowy\",\"second\":2},{\"first\":\"Ernst \u0026 Young ITEM\",\"second\":2},{\"first\":\"De Panne\",\"second\":2},{\"first\":\"La Redoute\",\"second\":2},{\"first\":\"Senate Republican\",\"second\":2},{\"first\":\"Caravaggio\",\"second\":2},{\"first\":\"Carmel Crimmins\",\"second\":2},{\"first\":\"Unley\",\"second\":2},{\"first\":\"Brookings Institution\",\"second\":2},{\"first\":\"St Pourcain sur Sioule\",\"second\":2},{\"first\":\"World Economic Forum\",\"second\":2},{\"first\":\"DVB Bank\",\"second\":2},{\"first\":\"Nasdaq Composite\",\"second\":2},{\"first\":\"Riese Pio X\",\"second\":2},{\"first\":\"Renmin University\",\"second\":2},{\"first\":\"New York Stock Exchange\",\"second\":2},{\"first\":\"PT Pertamina\",\"second\":2},{\"first\":\"Southern\",\"second\":2},{\"first\":\"Ouest-France\",\"second\":2},{\"first\":\"Democratic Action Party\",\"second\":2},{\"first\":\"Standard Chartered Bank\",\"second\":2},{\"first\":\"Rabobank\",\"second\":2},{\"first\":\"Freeport McMoran Copper \u0026 Gold Inc\",\"second\":2},{\"first\":\"Jakarta Post\",\"second\":2},{\"first\":\"Shanghai Composite Index\",\"second\":2},{\"first\":\"ABN Amro\",\"second\":2},{\"first\":\"Cagnes\",\"second\":2},{\"first\":\"Caterpillar\",\"second\":2},{\"first\":\"St Gildas des Bois\",\"second\":2},{\"first\":\"Vigo\",\"second\":2},{\"first\":\"Marseille\",\"second\":2},{\"first\":\"Competition Tribunal\",\"second\":2},{\"first\":\"Halliburton\",\"second\":1},{\"first\":\"Outokumpu\",\"second\":1},{\"first\":\"James Cook University Hospital\",\"second\":1},{\"first\":\"Volkswagen AG\",\"second\":1},{\"first\":\"Obamas\",\"second\":1},{\"first\":\"Association of School and College Leaders\",\"second\":1},{\"first\":\"Sarasin\",\"second\":1},{\"first\":\"Saudi Royal Family\",\"second\":1},{\"first\":\"Cosco Pacific\",\"second\":1},{\"first\":\"Aurel BGC Head of Global Research Christian Parisot\",\"second\":1},{\"first\":\"Getty Images\",\"second\":1},{\"first\":\"LME Cu\",\"second\":1},{\"first\":\"New York Times\",\"second\":1},{\"first\":\"International Labor Organization\",\"second\":1},{\"first\":\"Dongfeng Motor Group Co\",\"second\":1},{\"first\":\"Ascot\",\"second\":1},{\"first\":\"Berkshire\",\"second\":1},{\"first\":\"University of Zurich\",\"second\":1},{\"first\":\"Costco\",\"second\":1},{\"first\":\"Bedford\",\"second\":1},{\"first\":\"SuperSonics\",\"second\":1},{\"first\":\"Zhengzhou Siwei Mechanical \u0026 Electrical Manufacturing\",\"second\":1},{\"first\":\"HLL\",\"second\":1},{\"first\":\"Synovus Financial\",\"second\":1},{\"first\":\"CSGN\",\"second\":1},{\"first\":\"IST REUTERS\",\"second\":1},{\"first\":\"EPFR\",\"second\":1},{\"first\":\"Eastern Germany\",\"second\":1},{\"first\":\"Elverfeld\",\"second\":1},{\"first\":\"FCHI\",\"second\":1},{\"first\":\"JKSE\",\"second\":1},{\"first\":\"Commerzbank\",\"second\":1},{\"first\":\"Shenzhen Investment Ltd\",\"second\":1},{\"first\":\"Dagens Industri\",\"second\":1},{\"first\":\"ASCL\",\"second\":1},{\"first\":\"Marine International Oil Company\",\"second\":1},{\"first\":\"Nomura\",\"second\":1},{\"first\":\"Hyundai Motor Co\",\"second\":1},{\"first\":\"LME Tin\",\"second\":1},{\"first\":\"BRICS\",\"second\":1},{\"first\":\"Middlesbrough\",\"second\":1},{\"first\":\"Water and Power\",\"second\":1},{\"first\":\"Guangzhou Shipyard International Co Ltd\",\"second\":1},{\"first\":\"Nortel\",\"second\":1},{\"first\":\"Northamptonshire\",\"second\":1},{\"first\":\"Public Health Computational and Operations Research Group\",\"second\":1},{\"first\":\"Sharon Stone\",\"second\":1},{\"first\":\"Khodro\",\"second\":1},{\"first\":\"Horsham\",\"second\":1},{\"first\":\"UNCLOS\",\"second\":1},{\"first\":\"Pakistan Peoples Party\",\"second\":1},{\"first\":\"Korea Development Bank\",\"second\":1},{\"first\":\"Hammersmith \u0026 City\",\"second\":1},{\"first\":\"TCC\",\"second\":1},{\"first\":\"EADS\",\"second\":1},{\"first\":\"General Electric Co.\",\"second\":1},{\"first\":\"Great North East Air Ambulance\",\"second\":1},{\"first\":\"J\u0026J\",\"second\":1},{\"first\":\"NOVARTIS\",\"second\":1},{\"first\":\"Sussex\",\"second\":1},{\"first\":\"RIA Novosti\",\"second\":1},{\"first\":\"PETA\",\"second\":1},{\"first\":\"Freeport-McMoRan Copper \u0026 Gold\",\"second\":1},{\"first\":\"State Council\",\"second\":1},{\"first\":\"Surbiton\",\"second\":1},{\"first\":\"National Development Fund\",\"second\":1},{\"first\":\"General Motors\",\"second\":1},{\"first\":\"United Stock Exchange\",\"second\":1},{\"first\":\"Lufthansa\",\"second\":1},{\"first\":\"Martini\",\"second\":1},{\"first\":\"National Stock Exchange\",\"second\":1},{\"first\":\"London Metal Exchange\",\"second\":1},{\"first\":\"Reuters Davos\",\"second\":1},{\"first\":\"LME Zinc\",\"second\":1},{\"first\":\"Royal Dutch Shell\",\"second\":1},{\"first\":\"Magenta Advisors\",\"second\":1},{\"first\":\"University of Pittsburgh Medical Center\",\"second\":1},{\"first\":\"Schlumberger\",\"second\":1},{\"first\":\"Airline Economics\",\"second\":1},{\"first\":\"Toshiba Corp\",\"second\":1},{\"first\":\"Scientific-Atlanta\",\"second\":1},{\"first\":\"Longfor Properties Co Ltd\",\"second\":1},{\"first\":\"Hong Kong Broadband\",\"second\":1},{\"first\":\"Yangzi Evening News\",\"second\":1},{\"first\":\"Hengdeli Holdings Ltd\",\"second\":1},{\"first\":\"RBS\",\"second\":1},{\"first\":\"Punahou School\",\"second\":1},{\"first\":\"Singapore Airlines\",\"second\":1},{\"first\":\"NBA\",\"second\":1},{\"first\":\"Fitch Ratings\",\"second\":1},{\"first\":\"U.S. Centers for Disease Control and Prevention\",\"second\":1},{\"first\":\"National Population and Family Planning Commission\",\"second\":1},{\"first\":\"Elizabeth Joseph\",\"second\":1},{\"first\":\"East of England Ambulance Service\",\"second\":1},{\"first\":\"Pinebridge Investments\",\"second\":1},{\"first\":\"Cie. Financiere Richemont\",\"second\":1},{\"first\":\"People \u0027s Justice Party\",\"second\":1},{\"first\":\"Brookings-Tsinghua Center for Public Policy\",\"second\":1},{\"first\":\"Edelman\",\"second\":1},{\"first\":\"Hindustan Unilever\",\"second\":1},{\"first\":\"Department of Health \u0026 Human Services\",\"second\":1},{\"first\":\"Southeastern\",\"second\":1},{\"first\":\"Diesel\",\"second\":1},{\"first\":\"Network Rail\",\"second\":1},{\"first\":\"AXJO\",\"second\":1},{\"first\":\"Cleveland Police\",\"second\":1},{\"first\":\"St Petersburg\",\"second\":1},{\"first\":\"International Lease Finance Corp\",\"second\":1},{\"first\":\"Metropolitan\",\"second\":1},{\"first\":\"State Oceanic Administration\",\"second\":1},{\"first\":\"World Health Organization\",\"second\":1},{\"first\":\"BBC\",\"second\":1},{\"first\":\"Leeds Bradford\",\"second\":1},{\"first\":\"Secret Service\",\"second\":1},{\"first\":\"LGA\",\"second\":1},{\"first\":\"Ash Vale\",\"second\":1},{\"first\":\"JP Morgan Asian Credit Index\",\"second\":1},{\"first\":\"China Daily\",\"second\":1},{\"first\":\"Children and Young People department\",\"second\":1},{\"first\":\"Live Television\",\"second\":1},{\"first\":\"Heidrick \u0026 Struggles\",\"second\":1},{\"first\":\"Young Global Leaders and Global Shapers\",\"second\":1},{\"first\":\"OM Group\",\"second\":1},{\"first\":\"Durham Police\",\"second\":1},{\"first\":\"Assets Supervision and Administration Commission\",\"second\":1},{\"first\":\"Mitsubishi UFJ Asset Management\",\"second\":1},{\"first\":\"China National Petroleum Company\",\"second\":1},{\"first\":\"National Council on Social Security Reforms\",\"second\":1},{\"first\":\"United Malays National Organization\",\"second\":1},{\"first\":\"Research in Motion\",\"second\":1},{\"first\":\"PML-N\",\"second\":1},{\"first\":\"Gatwick Express\",\"second\":1},{\"first\":\"Japan Sports Association\",\"second\":1},{\"first\":\"National Energy Commission\",\"second\":1},{\"first\":\"Met Office\",\"second\":1},{\"first\":\"Cambridge \u0026 Stevenage\",\"second\":1},{\"first\":\"Maybank Investment Bank Bhd.\",\"second\":1},{\"first\":\"NYSE\",\"second\":1},{\"first\":\"London Fire Brigade\",\"second\":1},{\"first\":\"Au Jardin Les Amis\",\"second\":1},{\"first\":\"Cartier\",\"second\":1},{\"first\":\"Fraser \u0026 Neave\",\"second\":1},{\"first\":\"A\u0026E\",\"second\":1},{\"first\":\"International Container Terminal Services\",\"second\":1},{\"first\":\"Navy\",\"second\":1},{\"first\":\"ALSTOM\",\"second\":1},{\"first\":\"Scorpio\",\"second\":1},{\"first\":\"Regions Financial\",\"second\":1},{\"first\":\"Lippo Karawaci\",\"second\":1},{\"first\":\"Gawick Express\",\"second\":1},{\"first\":\"Sihuan Pharmaceutical Holdings Group\",\"second\":1},{\"first\":\"China Shenhua Energy Co Ltd\",\"second\":1},{\"first\":\"Helsingin Sanomat\",\"second\":1},{\"first\":\"Target Corp\",\"second\":1},{\"first\":\"USSR\",\"second\":1},{\"first\":\"Philex Petroleum Corp\",\"second\":1},{\"first\":\"Jilin Qifeng Chemical Finer Co Ltd\",\"second\":1},{\"first\":\"\\* Air China\",\"second\":1},{\"first\":\"People \u0027s Bank of China\",\"second\":1},{\"first\":\"Bundesbank\",\"second\":1},{\"first\":\"Air India\",\"second\":1},{\"first\":\"Chinese Business News\",\"second\":1},{\"first\":\"Hopson Development Holdings Ltd.\",\"second\":1},{\"first\":\"Elaine Lies\",\"second\":1},{\"first\":\"Bumi Plc\",\"second\":1},{\"first\":\"Reliance Industries\",\"second\":1},{\"first\":\"Sany Heavy Equipment International Holdings\",\"second\":1},{\"first\":\"Merdeka Center\",\"second\":1},{\"first\":\"Iranian Riyal\",\"second\":1},{\"first\":\"U.S. Agency for International Development\",\"second\":1},{\"first\":\"Sanofi-Aventis\",\"second\":1},{\"first\":\"Burberry Group\",\"second\":1},{\"first\":\"Micex\",\"second\":1},{\"first\":\"Miami Herald and World Politics Review\",\"second\":1},{\"first\":\"State Food and Drug Administration\",\"second\":1},{\"first\":\"Urenco\",\"second\":1},{\"first\":\"National Development and Reform Commission\",\"second\":1},{\"first\":\"Agile Property Holdings Ltd.\",\"second\":1},{\"first\":\"International Energy Agency\",\"second\":1},{\"first\":\"Harbor Advisory Corp\",\"second\":1},{\"first\":\"Scotch Corner\",\"second\":1},{\"first\":\"Uchiyamada\",\"second\":1},{\"first\":\"Bain \u0026 Co.\",\"second\":1},{\"first\":\"European Union\",\"second\":1},{\"first\":\"Muslim Brotherhood\",\"second\":1},{\"first\":\"Orange Sky Entertainment\",\"second\":1},{\"first\":\"PPR\",\"second\":1},{\"first\":\"BSAN\",\"second\":1},{\"first\":\"China International Marine Containers Group\",\"second\":1},{\"first\":\"LME Nickel\",\"second\":1},{\"first\":\"Nagoya University\",\"second\":1},{\"first\":\"Japan-China Economic Association\",\"second\":1},{\"first\":\"Diaoyus\",\"second\":1},{\"first\":\"LPG\",\"second\":1},{\"first\":\"Air Force\",\"second\":1},{\"first\":\"Local Government Association\",\"second\":1},{\"first\":\"TSX\",\"second\":1},{\"first\":\"China Securities Regulatory Commission\",\"second\":1},{\"first\":\"CRU\",\"second\":1},{\"first\":\"Accenture\",\"second\":1},{\"first\":\"Nano\",\"second\":1},{\"first\":\"China Merchants Holdings\",\"second\":1},{\"first\":\"Telegraph\",\"second\":1},{\"first\":\"Sunac China Holdings Ltd\",\"second\":1},{\"first\":\"Hampton Court\",\"second\":1},{\"first\":\"Clapham Junction\",\"second\":1},{\"first\":\"British House of Commons committee\",\"second\":1},{\"first\":\"AREVA\",\"second\":1},{\"first\":\"OREP\",\"second\":1},{\"first\":\"Nokia\",\"second\":1},{\"first\":\"Association of Southeast Asian Nations\",\"second\":1},{\"first\":\"Sacramento Kings\",\"second\":1},{\"first\":\"Tony Blair Sports Foundation\",\"second\":1},{\"first\":\"Mongolian Mining Corporation\",\"second\":1},{\"first\":\"Ore Fabrica\",\"second\":1},{\"first\":\"London Kings Cross\",\"second\":1},{\"first\":\"JP Morgan Chase \u0026 Co\",\"second\":1},{\"first\":\"Capgemini Consulting\",\"second\":1},{\"first\":\"Walter E. Washington Convention Center\",\"second\":1},{\"first\":\"New York Mercantile Exchange\",\"second\":1},{\"first\":\"Bossini International Holdings Ltd\",\"second\":1},{\"first\":\"Nottinghamshire\",\"second\":1},{\"first\":\"Osama bin Laden\",\"second\":1},{\"first\":\"Kyodo News\",\"second\":1},{\"first\":\"Aberdeen Islamic Asset Management Sdn Bhd.\",\"second\":1},{\"first\":\"North American airlines\",\"second\":1},{\"first\":\"Johnson \u0026 Johnson\",\"second\":1},{\"first\":\"Central Commission for Discipline Inspection\",\"second\":1},{\"first\":\"U.S. Bureau of Labor Statistics\",\"second\":1},{\"first\":\"Nazi Party\",\"second\":1},{\"first\":\"Shenfield\",\"second\":1},{\"first\":\"China Luxury\",\"second\":1},{\"first\":\"Bayern LB\",\"second\":1},{\"first\":\"Schroders\",\"second\":1}]";

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

	public List<Map<String, Object>> getdivisionsmore() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		try {
			String theurl = "http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=GetHot&tf=2&type=2";

			String string = HttpConn.getJsonFromUrlGet(theurl);
			// String string
			// ="[{\"first\":\"Reuters Breakingviews\",\"second\":44},{\"first\":\"BOJ\",\"second\":43},{\"first\":\"Thomson Reuters StarMine\",\"second\":33},{\"first\":\"U.N. Security Council\",\"second\":58},{\"first\":\"IBM\",\"second\":23},{\"first\":\"Bank of Japan\",\"second\":18},{\"first\":\"S\u0026P\",\"second\":15},{\"first\":\"Asharq Al-Awsat\",\"second\":14},{\"first\":\"Toyota Motor Corp\",\"second\":12},{\"first\":\"AIA Group\",\"second\":12},{\"first\":\"TBC\",\"second\":12},{\"first\":\"ZEW\",\"second\":12},{\"first\":\"MSCI\",\"second\":11},{\"first\":\"Glencore\",\"second\":10},{\"first\":\"ROTHSCHILDS Rothschild\",\"second\":10},{\"first\":\"U.S. House of Representatives\",\"second\":10},{\"first\":\"China Vanke Co.\",\"second\":10},{\"first\":\"Dow Jones Newswires\",\"second\":9},{\"first\":\"\\* Great Wall Motor Co Ltd\",\"second\":9},{\"first\":\"Standard \u0026 Poor \u0027s\",\"second\":8},{\"first\":\"Richmond Fed\",\"second\":8},{\"first\":\"FDI\",\"second\":8},{\"first\":\"Google Inc\",\"second\":8},{\"first\":\"International Centre for Development Studies Iran\",\"second\":7},{\"first\":\"ILFC\",\"second\":7},{\"first\":\"UBSN\",\"second\":7},{\"first\":\"Communist Party Congress\",\"second\":7},{\"first\":\"Kingfisher Airlines\",\"second\":6},{\"first\":\"Amstel Gold\",\"second\":6},{\"first\":\"DAX\",\"second\":6},{\"first\":\"IEA\",\"second\":6},{\"first\":\"WEF\",\"second\":6},{\"first\":\"UCI\",\"second\":6},{\"first\":\"Vale SA\",\"second\":6},{\"first\":\"Vanke Properties Overseas Ltd\",\"second\":6},{\"first\":\"International Business Machines Corp\",\"second\":6},{\"first\":\"White House\",\"second\":6},{\"first\":\"Apple Inc\",\"second\":6},{\"first\":\"Credit Suisse\",\"second\":6},{\"first\":\"United Nations Susan Rice\",\"second\":6},{\"first\":\"Nice\",\"second\":6},{\"first\":\"Citic Securities\",\"second\":5},{\"first\":\"Xinhua\",\"second\":5},{\"first\":\"KPMG China\",\"second\":5},{\"first\":\"European Central Bank\",\"second\":5},{\"first\":\"United Nations Convention\",\"second\":5},{\"first\":\"Baosteel Group\",\"second\":5},{\"first\":\"BHP Billiton\",\"second\":5},{\"first\":\"Citigroup\",\"second\":4},{\"first\":\"L\u0027Oreal\",\"second\":4},{\"first\":\"Treasury\",\"second\":4},{\"first\":\"Essilor International\",\"second\":4},{\"first\":\"Vattenfall Cyclassics\",\"second\":4},{\"first\":\"Bank of Communication International\",\"second\":4},{\"first\":\"Eneco Tour\",\"second\":4},{\"first\":\"First Capital Connect\",\"second\":4},{\"first\":\"International Cycling Union\",\"second\":4},{\"first\":\"Espa?a\",\"second\":4},{\"first\":\"ECB\",\"second\":4},{\"first\":\"Asia-Pacific\",\"second\":4},{\"first\":\"Montpellier\",\"second\":4},{\"first\":\"Supreme Court\",\"second\":4},{\"first\":\"AAPL\",\"second\":4},{\"first\":\"Nikkei Stock Average\",\"second\":4},{\"first\":\"Airbus\",\"second\":4},{\"first\":\"Team Sky\",\"second\":4},{\"first\":\"Ghent\",\"second\":4},{\"first\":\"CNPC\",\"second\":4},{\"first\":\"CNN\",\"second\":4},{\"first\":\"HSI\",\"second\":4},{\"first\":\"AIG\",\"second\":4},{\"first\":\"LME Alum\",\"second\":4},{\"first\":\"Philadelphia Federal Reserve Bank\",\"second\":4},{\"first\":\"Deutsche Bank\",\"second\":3},{\"first\":\"KWG Property\",\"second\":3},{\"first\":\"Magic Mountain\",\"second\":3},{\"first\":\"Asean\",\"second\":3},{\"first\":\"Barclays Capital\",\"second\":3},{\"first\":\"SOEs\",\"second\":3},{\"first\":\"DuPont\",\"second\":3},{\"first\":\"Clement Tan\",\"second\":3},{\"first\":\"Verizon Communications\",\"second\":3},{\"first\":\"Shenzhen International Holdings\",\"second\":3},{\"first\":\"Intel Corp\",\"second\":3},{\"first\":\"South West Trains\",\"second\":3},{\"first\":\"National Front\",\"second\":3},{\"first\":\"COMEX Cu\",\"second\":3},{\"first\":\"Royal Mail\",\"second\":3},{\"first\":\"SIEMENS Newspaper Handelsblatt\",\"second\":3},{\"first\":\"Parliament\",\"second\":3},{\"first\":\"PPP\",\"second\":3},{\"first\":\"Atari Interactive\",\"second\":3},{\"first\":\"Chinese Foreign Ministry Spokesman\",\"second\":3},{\"first\":\"World Bank\",\"second\":3},{\"first\":\"Ministry of Land and Resources\",\"second\":2},{\"first\":\"Korea Kumryong Trading Corp.\",\"second\":2},{\"first\":\"DPRK\",\"second\":2},{\"first\":\"Bastia\",\"second\":2},{\"first\":\"Lalín\",\"second\":2},{\"first\":\"Bank of East Land\",\"second\":2},{\"first\":\"ASO\",\"second\":2},{\"first\":\"Cheung Kong Holdings\",\"second\":2},{\"first\":\"IBERDROLA\",\"second\":2},{\"first\":\"Porto Vecchio\",\"second\":2},{\"first\":\"OCBC\",\"second\":2},{\"first\":\"Cortal Consors\",\"second\":2},{\"first\":\"Goldman Sachs Group Inc.\",\"second\":2},{\"first\":\"HEW Cyclassics\",\"second\":2},{\"first\":\"Microsoft\",\"second\":2},{\"first\":\"Davos Congress Centre\",\"second\":2},{\"first\":\"Keidanren\",\"second\":2},{\"first\":\"Eskom\",\"second\":2},{\"first\":\"Forrester Research\",\"second\":2},{\"first\":\"VOLVO Norway\",\"second\":2},{\"first\":\"Iraqi Ministry of Trade\",\"second\":2},{\"first\":\"KCNA\",\"second\":2},{\"first\":\"Northumbria Police\",\"second\":2},{\"first\":\"Essex\",\"second\":2},{\"first\":\"Bank of England\",\"second\":2},{\"first\":\"Democratic Party of Japan\",\"second\":2},{\"first\":\"ISI Group\",\"second\":2},{\"first\":\"Sinopec\",\"second\":2},{\"first\":\"Givors\",\"second\":2},{\"first\":\"Transport for London\",\"second\":2},{\"first\":\"La Gazzetta\",\"second\":2},{\"first\":\"St Nazaire\",\"second\":2},{\"first\":\"Torredelcampo\",\"second\":2},{\"first\":\"Saint-Nicolas\",\"second\":2},{\"first\":\"FTSE Bursa Malaysia KLCI\",\"second\":2},{\"first\":\"Central Bank of Iran\",\"second\":2},{\"first\":\"Brescia\",\"second\":2},{\"first\":\"Boeing\",\"second\":2},{\"first\":\"Castres\",\"second\":2},{\"first\":\"Bagà\",\"second\":2},{\"first\":\"Christian Dior\",\"second\":2},{\"first\":\"Fougeres\",\"second\":2},{\"first\":\"Cesana Torinese\",\"second\":2},{\"first\":\"BANCA MONTE DEI PASCHI DI SIENA\",\"second\":2},{\"first\":\"Pescara\",\"second\":2},{\"first\":\"Travelers\",\"second\":2},{\"first\":\"Maella\",\"second\":2},{\"first\":\"Paris-Roubaix\",\"second\":2},{\"first\":\"CNOOC\",\"second\":2},{\"first\":\"St Girons-Bagneres de Bigorre\",\"second\":2},{\"first\":\"Finance Ministry\",\"second\":2},{\"first\":\"Standard and Poor\",\"second\":2},{\"first\":\"Virgin Trains\",\"second\":2},{\"first\":\"Ponte di Legno\",\"second\":2},{\"first\":\"ENAGAS\",\"second\":2},{\"first\":\"Republicans\",\"second\":2},{\"first\":\"Cauberg\",\"second\":2},{\"first\":\"Michelin\",\"second\":2},{\"first\":\"Leganés\",\"second\":2},{\"first\":\"Winsor Properties\",\"second\":2},{\"first\":\"Texas Instruments\",\"second\":2},{\"first\":\"Cordenons\",\"second\":2},{\"first\":\"Ajaccio\",\"second\":2},{\"first\":\"Nissan Motor Co\",\"second\":2},{\"first\":\"La Grande Boucle\",\"second\":2},{\"first\":\"HSBC Holdings\",\"second\":2},{\"first\":\"LVMH Moet Hennessy Louis Vuitton\",\"second\":2},{\"first\":\"CDC\",\"second\":2},{\"first\":\"St Amand Montrond\",\"second\":2},{\"first\":\"Tosong Technology Trading Corp.\",\"second\":2},{\"first\":\"Xstrata\",\"second\":2},{\"first\":\"Bari\",\"second\":2},{\"first\":\"Kent Police\",\"second\":2},{\"first\":\"National Energy Administration\",\"second\":2},{\"first\":\"MLR\",\"second\":2},{\"first\":\"Korean Committee for Space Technology\",\"second\":2},{\"first\":\"Army\",\"second\":2},{\"first\":\"Ministry of Oil\",\"second\":2},{\"first\":\"Amaury Sport Organisation\",\"second\":2},{\"first\":\"Fifa\",\"second\":2},{\"first\":\"Ryonha Machinery Joint Venture Corp.\",\"second\":2},{\"first\":\"McKinsey \u0026 Co.\",\"second\":2},{\"first\":\"McLaren Vale\",\"second\":2},{\"first\":\"Naranco\",\"second\":2},{\"first\":\"Vuelta\",\"second\":2},{\"first\":\"Przeglad Sportowy\",\"second\":2},{\"first\":\"Ernst \u0026 Young ITEM\",\"second\":2},{\"first\":\"De Panne\",\"second\":2},{\"first\":\"La Redoute\",\"second\":2},{\"first\":\"Senate Republican\",\"second\":2},{\"first\":\"Caravaggio\",\"second\":2},{\"first\":\"Carmel Crimmins\",\"second\":2},{\"first\":\"Unley\",\"second\":2},{\"first\":\"Brookings Institution\",\"second\":2},{\"first\":\"St Pourcain sur Sioule\",\"second\":2},{\"first\":\"World Economic Forum\",\"second\":2},{\"first\":\"DVB Bank\",\"second\":2},{\"first\":\"Nasdaq Composite\",\"second\":2},{\"first\":\"Riese Pio X\",\"second\":2},{\"first\":\"Renmin University\",\"second\":2},{\"first\":\"New York Stock Exchange\",\"second\":2},{\"first\":\"PT Pertamina\",\"second\":2},{\"first\":\"Southern\",\"second\":2},{\"first\":\"Ouest-France\",\"second\":2},{\"first\":\"Democratic Action Party\",\"second\":2},{\"first\":\"Standard Chartered Bank\",\"second\":2},{\"first\":\"Rabobank\",\"second\":2},{\"first\":\"Freeport McMoran Copper \u0026 Gold Inc\",\"second\":2},{\"first\":\"Jakarta Post\",\"second\":2},{\"first\":\"Shanghai Composite Index\",\"second\":2},{\"first\":\"ABN Amro\",\"second\":2},{\"first\":\"Cagnes\",\"second\":2},{\"first\":\"Caterpillar\",\"second\":2},{\"first\":\"St Gildas des Bois\",\"second\":2},{\"first\":\"Vigo\",\"second\":2},{\"first\":\"Marseille\",\"second\":2},{\"first\":\"Competition Tribunal\",\"second\":2},{\"first\":\"Halliburton\",\"second\":1},{\"first\":\"Outokumpu\",\"second\":1},{\"first\":\"James Cook University Hospital\",\"second\":1},{\"first\":\"Volkswagen AG\",\"second\":1},{\"first\":\"Obamas\",\"second\":1},{\"first\":\"Association of School and College Leaders\",\"second\":1},{\"first\":\"Sarasin\",\"second\":1},{\"first\":\"Saudi Royal Family\",\"second\":1},{\"first\":\"Cosco Pacific\",\"second\":1},{\"first\":\"Aurel BGC Head of Global Research Christian Parisot\",\"second\":1},{\"first\":\"Getty Images\",\"second\":1},{\"first\":\"LME Cu\",\"second\":1},{\"first\":\"New York Times\",\"second\":1},{\"first\":\"International Labor Organization\",\"second\":1},{\"first\":\"Dongfeng Motor Group Co\",\"second\":1},{\"first\":\"Ascot\",\"second\":1},{\"first\":\"Berkshire\",\"second\":1},{\"first\":\"University of Zurich\",\"second\":1},{\"first\":\"Costco\",\"second\":1},{\"first\":\"Bedford\",\"second\":1},{\"first\":\"SuperSonics\",\"second\":1},{\"first\":\"Zhengzhou Siwei Mechanical \u0026 Electrical Manufacturing\",\"second\":1},{\"first\":\"HLL\",\"second\":1},{\"first\":\"Synovus Financial\",\"second\":1},{\"first\":\"CSGN\",\"second\":1},{\"first\":\"IST REUTERS\",\"second\":1},{\"first\":\"EPFR\",\"second\":1},{\"first\":\"Eastern Germany\",\"second\":1},{\"first\":\"Elverfeld\",\"second\":1},{\"first\":\"FCHI\",\"second\":1},{\"first\":\"JKSE\",\"second\":1},{\"first\":\"Commerzbank\",\"second\":1},{\"first\":\"Shenzhen Investment Ltd\",\"second\":1},{\"first\":\"Dagens Industri\",\"second\":1},{\"first\":\"ASCL\",\"second\":1},{\"first\":\"Marine International Oil Company\",\"second\":1},{\"first\":\"Nomura\",\"second\":1},{\"first\":\"Hyundai Motor Co\",\"second\":1},{\"first\":\"LME Tin\",\"second\":1},{\"first\":\"BRICS\",\"second\":1},{\"first\":\"Middlesbrough\",\"second\":1},{\"first\":\"Water and Power\",\"second\":1},{\"first\":\"Guangzhou Shipyard International Co Ltd\",\"second\":1},{\"first\":\"Nortel\",\"second\":1},{\"first\":\"Northamptonshire\",\"second\":1},{\"first\":\"Public Health Computational and Operations Research Group\",\"second\":1},{\"first\":\"Sharon Stone\",\"second\":1},{\"first\":\"Khodro\",\"second\":1},{\"first\":\"Horsham\",\"second\":1},{\"first\":\"UNCLOS\",\"second\":1},{\"first\":\"Pakistan Peoples Party\",\"second\":1},{\"first\":\"Korea Development Bank\",\"second\":1},{\"first\":\"Hammersmith \u0026 City\",\"second\":1},{\"first\":\"TCC\",\"second\":1},{\"first\":\"EADS\",\"second\":1},{\"first\":\"General Electric Co.\",\"second\":1},{\"first\":\"Great North East Air Ambulance\",\"second\":1},{\"first\":\"J\u0026J\",\"second\":1},{\"first\":\"NOVARTIS\",\"second\":1},{\"first\":\"Sussex\",\"second\":1},{\"first\":\"RIA Novosti\",\"second\":1},{\"first\":\"PETA\",\"second\":1},{\"first\":\"Freeport-McMoRan Copper \u0026 Gold\",\"second\":1},{\"first\":\"State Council\",\"second\":1},{\"first\":\"Surbiton\",\"second\":1},{\"first\":\"National Development Fund\",\"second\":1},{\"first\":\"General Motors\",\"second\":1},{\"first\":\"United Stock Exchange\",\"second\":1},{\"first\":\"Lufthansa\",\"second\":1},{\"first\":\"Martini\",\"second\":1},{\"first\":\"National Stock Exchange\",\"second\":1},{\"first\":\"London Metal Exchange\",\"second\":1},{\"first\":\"Reuters Davos\",\"second\":1},{\"first\":\"LME Zinc\",\"second\":1},{\"first\":\"Royal Dutch Shell\",\"second\":1},{\"first\":\"Magenta Advisors\",\"second\":1},{\"first\":\"University of Pittsburgh Medical Center\",\"second\":1},{\"first\":\"Schlumberger\",\"second\":1},{\"first\":\"Airline Economics\",\"second\":1},{\"first\":\"Toshiba Corp\",\"second\":1},{\"first\":\"Scientific-Atlanta\",\"second\":1},{\"first\":\"Longfor Properties Co Ltd\",\"second\":1},{\"first\":\"Hong Kong Broadband\",\"second\":1},{\"first\":\"Yangzi Evening News\",\"second\":1},{\"first\":\"Hengdeli Holdings Ltd\",\"second\":1},{\"first\":\"RBS\",\"second\":1},{\"first\":\"Punahou School\",\"second\":1},{\"first\":\"Singapore Airlines\",\"second\":1},{\"first\":\"NBA\",\"second\":1},{\"first\":\"Fitch Ratings\",\"second\":1},{\"first\":\"U.S. Centers for Disease Control and Prevention\",\"second\":1},{\"first\":\"National Population and Family Planning Commission\",\"second\":1},{\"first\":\"Elizabeth Joseph\",\"second\":1},{\"first\":\"East of England Ambulance Service\",\"second\":1},{\"first\":\"Pinebridge Investments\",\"second\":1},{\"first\":\"Cie. Financiere Richemont\",\"second\":1},{\"first\":\"People \u0027s Justice Party\",\"second\":1},{\"first\":\"Brookings-Tsinghua Center for Public Policy\",\"second\":1},{\"first\":\"Edelman\",\"second\":1},{\"first\":\"Hindustan Unilever\",\"second\":1},{\"first\":\"Department of Health \u0026 Human Services\",\"second\":1},{\"first\":\"Southeastern\",\"second\":1},{\"first\":\"Diesel\",\"second\":1},{\"first\":\"Network Rail\",\"second\":1},{\"first\":\"AXJO\",\"second\":1},{\"first\":\"Cleveland Police\",\"second\":1},{\"first\":\"St Petersburg\",\"second\":1},{\"first\":\"International Lease Finance Corp\",\"second\":1},{\"first\":\"Metropolitan\",\"second\":1},{\"first\":\"State Oceanic Administration\",\"second\":1},{\"first\":\"World Health Organization\",\"second\":1},{\"first\":\"BBC\",\"second\":1},{\"first\":\"Leeds Bradford\",\"second\":1},{\"first\":\"Secret Service\",\"second\":1},{\"first\":\"LGA\",\"second\":1},{\"first\":\"Ash Vale\",\"second\":1},{\"first\":\"JP Morgan Asian Credit Index\",\"second\":1},{\"first\":\"China Daily\",\"second\":1},{\"first\":\"Children and Young People department\",\"second\":1},{\"first\":\"Live Television\",\"second\":1},{\"first\":\"Heidrick \u0026 Struggles\",\"second\":1},{\"first\":\"Young Global Leaders and Global Shapers\",\"second\":1},{\"first\":\"OM Group\",\"second\":1},{\"first\":\"Durham Police\",\"second\":1},{\"first\":\"Assets Supervision and Administration Commission\",\"second\":1},{\"first\":\"Mitsubishi UFJ Asset Management\",\"second\":1},{\"first\":\"China National Petroleum Company\",\"second\":1},{\"first\":\"National Council on Social Security Reforms\",\"second\":1},{\"first\":\"United Malays National Organization\",\"second\":1},{\"first\":\"Research in Motion\",\"second\":1},{\"first\":\"PML-N\",\"second\":1},{\"first\":\"Gatwick Express\",\"second\":1},{\"first\":\"Japan Sports Association\",\"second\":1},{\"first\":\"National Energy Commission\",\"second\":1},{\"first\":\"Met Office\",\"second\":1},{\"first\":\"Cambridge \u0026 Stevenage\",\"second\":1},{\"first\":\"Maybank Investment Bank Bhd.\",\"second\":1},{\"first\":\"NYSE\",\"second\":1},{\"first\":\"London Fire Brigade\",\"second\":1},{\"first\":\"Au Jardin Les Amis\",\"second\":1},{\"first\":\"Cartier\",\"second\":1},{\"first\":\"Fraser \u0026 Neave\",\"second\":1},{\"first\":\"A\u0026E\",\"second\":1},{\"first\":\"International Container Terminal Services\",\"second\":1},{\"first\":\"Navy\",\"second\":1},{\"first\":\"ALSTOM\",\"second\":1},{\"first\":\"Scorpio\",\"second\":1},{\"first\":\"Regions Financial\",\"second\":1},{\"first\":\"Lippo Karawaci\",\"second\":1},{\"first\":\"Gawick Express\",\"second\":1},{\"first\":\"Sihuan Pharmaceutical Holdings Group\",\"second\":1},{\"first\":\"China Shenhua Energy Co Ltd\",\"second\":1},{\"first\":\"Helsingin Sanomat\",\"second\":1},{\"first\":\"Target Corp\",\"second\":1},{\"first\":\"USSR\",\"second\":1},{\"first\":\"Philex Petroleum Corp\",\"second\":1},{\"first\":\"Jilin Qifeng Chemical Finer Co Ltd\",\"second\":1},{\"first\":\"\\* Air China\",\"second\":1},{\"first\":\"People \u0027s Bank of China\",\"second\":1},{\"first\":\"Bundesbank\",\"second\":1},{\"first\":\"Air India\",\"second\":1},{\"first\":\"Chinese Business News\",\"second\":1},{\"first\":\"Hopson Development Holdings Ltd.\",\"second\":1},{\"first\":\"Elaine Lies\",\"second\":1},{\"first\":\"Bumi Plc\",\"second\":1},{\"first\":\"Reliance Industries\",\"second\":1},{\"first\":\"Sany Heavy Equipment International Holdings\",\"second\":1},{\"first\":\"Merdeka Center\",\"second\":1},{\"first\":\"Iranian Riyal\",\"second\":1},{\"first\":\"U.S. Agency for International Development\",\"second\":1},{\"first\":\"Sanofi-Aventis\",\"second\":1},{\"first\":\"Burberry Group\",\"second\":1},{\"first\":\"Micex\",\"second\":1},{\"first\":\"Miami Herald and World Politics Review\",\"second\":1},{\"first\":\"State Food and Drug Administration\",\"second\":1},{\"first\":\"Urenco\",\"second\":1},{\"first\":\"National Development and Reform Commission\",\"second\":1},{\"first\":\"Agile Property Holdings Ltd.\",\"second\":1},{\"first\":\"International Energy Agency\",\"second\":1},{\"first\":\"Harbor Advisory Corp\",\"second\":1},{\"first\":\"Scotch Corner\",\"second\":1},{\"first\":\"Uchiyamada\",\"second\":1},{\"first\":\"Bain \u0026 Co.\",\"second\":1},{\"first\":\"European Union\",\"second\":1},{\"first\":\"Muslim Brotherhood\",\"second\":1},{\"first\":\"Orange Sky Entertainment\",\"second\":1},{\"first\":\"PPR\",\"second\":1},{\"first\":\"BSAN\",\"second\":1},{\"first\":\"China International Marine Containers Group\",\"second\":1},{\"first\":\"LME Nickel\",\"second\":1},{\"first\":\"BLCU\",\"second\":30},{\"first\":\"Japan-China Economic Association\",\"second\":1},{\"first\":\"Diaoyus\",\"second\":1},{\"first\":\"LPG\",\"second\":1},{\"first\":\"Air Force\",\"second\":1},{\"first\":\"Local Government Association\",\"second\":1},{\"first\":\"TSX\",\"second\":1},{\"first\":\"China Securities Regulatory Commission\",\"second\":1},{\"first\":\"CRU\",\"second\":1},{\"first\":\"Accenture\",\"second\":1},{\"first\":\"Nano\",\"second\":1},{\"first\":\"China Merchants Holdings\",\"second\":1},{\"first\":\"Telegraph\",\"second\":1},{\"first\":\"Sunac China Holdings Ltd\",\"second\":1},{\"first\":\"Hampton Court\",\"second\":1},{\"first\":\"Clapham Junction\",\"second\":1},{\"first\":\"British House of Commons committee\",\"second\":1},{\"first\":\"AREVA\",\"second\":1},{\"first\":\"OREP\",\"second\":1},{\"first\":\"Nokia\",\"second\":1},{\"first\":\"Association of Southeast Asian Nations\",\"second\":1},{\"first\":\"Sacramento Kings\",\"second\":1},{\"first\":\"Tony Blair Sports Foundation\",\"second\":1},{\"first\":\"Mongolian Mining Corporation\",\"second\":1},{\"first\":\"Ore Fabrica\",\"second\":1},{\"first\":\"London Kings Cross\",\"second\":1},{\"first\":\"JP Morgan Chase \u0026 Co\",\"second\":1},{\"first\":\"Capgemini Consulting\",\"second\":1},{\"first\":\"Walter E. Washington Convention Center\",\"second\":1},{\"first\":\"New York Mercantile Exchange\",\"second\":1},{\"first\":\"Bossini International Holdings Ltd\",\"second\":1},{\"first\":\"Nottinghamshire\",\"second\":1},{\"first\":\"Osama bin Laden\",\"second\":1},{\"first\":\"Kyodo News\",\"second\":1},{\"first\":\"Aberdeen Islamic Asset Management Sdn Bhd.\",\"second\":1},{\"first\":\"North American airlines\",\"second\":1},{\"first\":\"Johnson \u0026 Johnson\",\"second\":1},{\"first\":\"Central Commission for Discipline Inspection\",\"second\":1},{\"first\":\"U.S. Bureau of Labor Statistics\",\"second\":1},{\"first\":\"Nazi Party\",\"second\":1},{\"first\":\"Shenfield\",\"second\":1},{\"first\":\"China Luxury\",\"second\":1},{\"first\":\"Bayern LB\",\"second\":1},{\"first\":\"Schroders\",\"second\":1}]";

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

	public Map<String, String> getjutixinwen(String theurl) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			String str = "http://democlip.blcu.edu.cn:8081/RMI_WEB/rmi?r=GetDoc&url=";
			theurl = str + theurl;
			// String
			// string="{\"inforMap\":{\"title\":\"NM Scientist Accused of Giving Research to China\",\"text\":\"The federal government says a former scientist stole research at Sandia National Labs in New Mexico to share it with China.\r\nKRQE-TV reports ( http://bit.ly/M8grzK ) that Jianyu Huang will be arraigned Tuesday on five counts of federal program fraud and one count of making false statements. He was arrested over the weekend.\r\nAuthorities say Huang passed off nanotechnology research that belongs to the U.S. as his own. They say he went online to share the data with state-run schools in China.\r\nHuang is also accused of lying about taking a lab-owned laptop computer there.\r\nSandia Lab says Huang was fired in April for violating procedures and that he never had access to classified national security information.\r\nA message left for Huang\u0027s public defender, Brian Pori, was not immediately returned.\r\n———\r\nInformation from: KRQE-TV, http://www.krqe.com\",\"keywords\":\"Science, China, United States, Greater China, East Asia, Asia, North America, us news, news articles, online news, us news, abnews, abc news, abcnews online, abc news online, online news\",\"source\":\"abc\",\"description\":\"NM Scientist Accused of Giving Research to China\",\"date\":\"20120605081454\",\"url\":\"http://abcnews.go.com/US/wireStory/nm-scientist-accused-giving-research-china-16498874\"}}";

			String string = HttpConn.getJsonFromUrlGet(theurl);
			Log.d("test string", string);
			Log.d("test string", string);
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
			// 20120605081454
			String d1 = date.substring(0, 4);
			String d2 = date.substring(4, 6);
			String d3 = date.substring(6, 8);
			String d4 = date.substring(8, 10);
			String d5 = date.substring(10, 12);
			String d6 = date.substring(12, 14);
			// 2012-06-05 08:14:54
			date = d1 + "-" + d2 + "-" + d3 + " " + d4 + ":" + d5 + ":" + d6;
			map.put("date", date);

		} catch (Exception e) {
			Log.d("test error", "error");
			e.printStackTrace();
		}
		return map;
	}

	private static String stringToJson(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {

			char c = s.charAt(i);
			switch (c) {
			case '\"':
				sb.append("\\\"");
				break;
			case '\\': // 如果不处理单引号，可以释放此段代码，若结合下面的方法处理单引号就必须注释掉该段代码
				sb.append("\\\\");
				break;
			case '/':
				sb.append("\\/");
				break;
			case '\b': // 退格
				sb.append("\\b");
				break;
			case '\f': // 走纸换页
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");// 换行
				break;
			case '\r': // 回车
				sb.append("\\r");
				break;
			case '\t': // 横向跳格
				sb.append("\\t");
				break;
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}

	// 注意，下面的方法是在把你的数据文本接入json数据字符串之前使用，预先对文本做处理后在接入JSON字符串
	// 处理文本中的单引号 和 斜杠
	private static String StringDanYinToJSON(String ors) {
		ors = ors == null ? "" : ors;
		StringBuffer buffer = new StringBuffer(ors);
		int i = 0;
		while (i < buffer.length()) {
			if (buffer.charAt(i) == '\'' || buffer.charAt(i) == '\\') {
				buffer.insert(i, '\\');
				i += 2;
			} else {
				i++;
			}
		}
		return buffer.toString();
	}

}
