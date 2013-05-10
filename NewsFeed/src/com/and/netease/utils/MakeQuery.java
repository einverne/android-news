package com.and.netease.utils;

import android.util.Log;

public class MakeQuery {
	final static String title = "title";
	final String description = "description";
	final String text = "text";

	public static String query(String keyword, String dateF, String dateT) {
		// title:"Boston" OR description:"Boston" OR text:"Boston" AND
		// date:[2012051300 TO 20121106x]
		String query = title + ":\"" + keyword + "\" OR description:\""
				+ keyword + "\" OR text:\"" + keyword + "\" AND date:[" + dateF
				+ " TO " + dateT + "]";
		Log.d("EV_DEBUG", query);
		return query;
	}
}
