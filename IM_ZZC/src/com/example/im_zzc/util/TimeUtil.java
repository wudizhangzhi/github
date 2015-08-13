package com.example.im_zzc.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

	public static String getHourAndMin(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.format(new Date(time));
	}

	/**
	 * 获取聊天时间：因为sdk的时间默认到秒故应该乘1000
	 * 
	 * @param timesamp
	 * @return
	 */
	public static String getChatTime(long timesamp) {
		String result = "";
		long clearTime = timesamp * 1000;
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		Date today = new Date(System.currentTimeMillis());
		Date other = new Date(clearTime);
		int temp = Integer.parseInt(sdf.format(today))
				- Integer.parseInt(sdf.format(other));
		switch (temp) {
		case 0:
			 result="今天 "+getHourAndMin(clearTime);
			break;

		case 1:
			result="昨天 "+getHourAndMin(clearTime);
			break;
		case 2:
			result="前天 "+getHourAndMin(clearTime);
			break;
		default:
			result=getHourAndMin(clearTime);
			break;
		}

		return result;
	}
}
