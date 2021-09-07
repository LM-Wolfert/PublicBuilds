package me.elgamer.publicbuilds.utils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class Time {
	
	public static long currentTime() {	
		
		return System.currentTimeMillis();
	}
	
	public static String getDate(long time) {
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm a");
		formatter.setTimeZone(TimeZone.getTimeZone("Europe/London"));
		Date date = new Date(time);
		return formatter.format(date);
	}

}
