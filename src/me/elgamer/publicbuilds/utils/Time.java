package me.elgamer.publicbuilds.utils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Time {
	
	public static long currentTime() {	
		
		return System.currentTimeMillis();
	}
	
	public static String getDate(long time) {
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd:HH:mm", Locale.UK);
		Date date = new Date(time);
		return formatter.format(date);
	}

}
