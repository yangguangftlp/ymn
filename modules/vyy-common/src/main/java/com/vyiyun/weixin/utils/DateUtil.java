/**
 * 
 */
package com.vyiyun.weixin.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * @author tf
 * 
 *         2015年6月25日
 */
public class DateUtil {

	public static final String FORMATPATTERN = "yyyy-MM-dd HH:mm";

	/**
	 * 获取时间差 返回 xx 天 xx 小时
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws ParseException
	 */
	public static String getTimeDiff(Date startTime, Date endTime) {
		return getTimeDiff(endTime.getTime() - startTime.getTime());
	}

	/**
	 * 字符串转换日期
	 * 
	 * @param str
	 * @return
	 */
	public static Date stringToDate(String str, String parsePattern) {
		// str = " 2008-07-10 19:20:00 " 格式
		SimpleDateFormat format = new SimpleDateFormat(parsePattern);
		if (StringUtils.isNotEmpty(str)) {
			try {
				return format.parse(str);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String getDayOfWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		switch (dayOfWeek) {
		case 1:
			return "星期日";
		case 2:
			return "星期一";
		case 3:
			return "星期二";
		case 4:
			return "星期三";
		case 5:
			return "星期四";
		case 6:
			return "星期五";
		case 7:
			return "星期六";
		}
		return StringUtils.EMPTY;
	}

	/**
	 * 字符串转换日期
	 * 
	 * @param str
	 * @return
	 */
	public static String dateToString(Date date, String parsePattern) {
		// str = " 2008-07-10 19:20:00 " 格式
		if (null != date) {
			SimpleDateFormat format = new SimpleDateFormat(parsePattern);
			if (null != date) {
				return format.format(date);
			}
		}
		return null;
	}

	/**
	 * 字符串转换日期
	 * 
	 * @param str
	 * @return
	 */
	public static Date stringToDate(String str) {
		return stringToDate(str, FORMATPATTERN);
	}

	public static String getTimeDiff(long diff) {
		long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
		long nh = 1000 * 60 * 60;// 一小时的毫秒数
		long nm = 1000 * 60;// 一分钟的毫秒数
		long day = 0;
		long hour = 0;
		long min = 0;
		day = diff / nd;// 计算差多少天
		hour = diff % nd / nh;// 计算差多少小时
		min = diff % nd % nh / nm;// 计算差多少分钟
		StringBuffer diifTime = new StringBuffer();
		if (day > 0) {
			diifTime.append(day).append("天");

		}
		if (hour > 0) {
			diifTime.append(hour).append("小时").toString();

		}
		if (min > 0) {
			diifTime.append(min).append("分钟").toString();
		}
		return diifTime.toString();
	}

	public static String getTimeHour(long diff) {
		long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
		long nh = 1000 * 60 * 60;// 一小时的毫秒数
		long nm = 1000 * 60;// 一分钟的毫秒数
		long hour = 0;
		double min = 0;
		hour = diff % nd / nh;// 计算差多少小时
		min = ((float) (diff % nd % nh / nm)) / 60;// 计算差多少分钟
		StringBuffer diifTime = new StringBuffer();
		if (hour > 0) {
			diifTime.append(hour);
		}
		if (min > 0) {
			if (hour > 0) {
				diifTime.delete(0, diifTime.length());
				diifTime.append((hour + min));

			} else {
				diifTime.append(min);
			}
		}
		return diifTime.toString();
	}
}
