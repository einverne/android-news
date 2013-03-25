package com.and.netease.utils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpConn {
	public static String JsonFromUrl(String url) {
		String strURL = url;
		String str = null;
		//创建HTTPGET连接
		HttpGet httpRequest = new HttpGet(strURL);
		try {
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
			//若状态码为200，OK
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String string = EntityUtils.toString(httpResponse.getEntity());
				
				string = eregi_replace("(\\r\\n\\|\\r|\\n|\\n\\r)", "", string);
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

	
	
	public static String eregi_replace(String strFrom,String strTo,String strTarget) {
		String strPatternString = "(?i)"+strFrom;
		Pattern pattern = Pattern.compile(strPatternString);
		Matcher matcher = pattern.matcher(strTarget);
		if (matcher.find()) {
			return strTarget.replaceAll(strFrom, strTo);
		} else {
			return strTarget;
		}
	}
}
