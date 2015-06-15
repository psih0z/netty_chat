package by.kam32ar.server.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Utilites {

	public static int currentTime() {
		
		return (int)TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
	}
	
	public static String intTimeToString(int time) {
		Date date = new Date(TimeUnit.SECONDS.toMillis(time));
		return new SimpleDateFormat("hh:mm:ss").format(date);
	}
	
	/**
	 * Возвращает вариант описания количества для указанного числа
	 * @param count количество
	 * @param var1 вариант для числа 1 (1 вол)
	 * @param var2 вариант для числа 2 (2 вола)
	 * @param var5 вариант для числа 5 (5 волов)
	 * @return
	 */
	public static String parseCount(int count, String var1, String var2, String var5) {
		String s = String.valueOf(count);
		if (s.length() > 1 && s.charAt(s.length() - 2) == '1') {
			return var5;
		}
		
		switch (s.charAt(s.length() - 1)) {
			case '1':
				return var1;
			case '2':
			case '3':
			case '4':
				return var2;
			default:
				break;
		}
		
		return var5;
	}
	
}
