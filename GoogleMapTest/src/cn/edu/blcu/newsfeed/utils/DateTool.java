package cn.edu.blcu.newsfeed.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTool {
	static public String getTodayDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(currentTime);
	}

	static public String getDateTodayMinusDay(int days) {
		Date currentTime = new Date(System.currentTimeMillis() - days * 24 * 60
				* 60 * 1000);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(currentTime);
	}
}
