package com.and.netease.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;
import android.widget.SlidingDrawer;

public class HttpConn {

	public static String getJsonFromUrlGet(String url) {
		Log.d("EV_DEBUG", "getJsonFromUrlGet");
		String str = null;
		//将url格式化
		Log.d("EV_DEBUG", url);
		url = url.replace(" ", "%20");
		url = url.replace("\"", "%22");
		Log.d("EV_DEBUG", url);
		//创建HTTPGET连接
		HttpGet httpRequest = new HttpGet(url);
		try {
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
			//若状态码为200，OK
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String string = EntityUtils.toString(httpResponse.getEntity());
				str = string;
			} else {
				str ="Error Response: "+httpResponse.getStatusLine().toString();
			}
		} catch (ClientProtocolException e) {
			// TODO: handle exception
			str = e.getMessage().toString();
			e.printStackTrace();
		} catch (IOException e) {
			str = e.getMessage().toString();
			e.printStackTrace();
		} catch (Exception e) {
			str =e.getMessage().toString();
			e.printStackTrace();
		}
		return str;
	}

	
	public static String getJsonFromUrlPost(String url) {
		String str = null;
		// 创建HTTPGET连接
		HttpPost httpRequest = new HttpPost(url);
		try {
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			// 若状态码为200，OK
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String string = EntityUtils.toString(httpResponse.getEntity());

				// string = eregi_replace("(\\r\\n\\|\\r|\\n|\\n\\r)", "",
				// string);
				str = string;
			} else {
				str = "Error Response: "
						+ httpResponse.getStatusLine().toString();
			}
		} catch (ClientProtocolException e) {
			// TODO: handle exception
			str = e.getMessage().toString();
			e.printStackTrace();
		} catch (IOException e) {
			str = e.getMessage().toString();
			e.printStackTrace();
		} catch (Exception e) {
			str = e.getMessage().toString();
			e.printStackTrace();
		}
		return str;
	}

	public static String eregi_replace(String strFrom, String strTo,
			String strTarget) {
		String strPatternString = "(?i)" + strFrom;
		Pattern pattern = Pattern.compile(strPatternString);
		Matcher matcher = pattern.matcher(strTarget);
		if (matcher.find()) {
			return strTarget.replaceAll(strFrom, strTo);
		} else {
			return strTarget;
		}
	}
}
