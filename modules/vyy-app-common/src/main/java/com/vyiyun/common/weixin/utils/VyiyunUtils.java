package com.vyiyun.common.weixin.utils;

import java.util.Iterator;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.google.common.base.Strings;
import com.vyiyun.common.weixin.entity.AttendanceRule;
import com.vyiyun.weixin.cache.ICache;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.entity.SystemStatus;

public class VyiyunUtils implements Constants{

	/**
	 * 根据签到规则确认当天是否需要签到
	 * @param rule 签到规则
	 * @param dayOfWeek Calendar.DAY_OF_WEEK
	 * @return 是否需要签到
	 */
	public static boolean checkSign4Today(AttendanceRule rule, int dayOfWeek) {
		switch (dayOfWeek) {
		case 1:
			if (STRING_0.equals(rule.getSun())) {
				return false;
			}
			break;
		case 2:
			if (STRING_0.equals(rule.getMon())) {
				return false;
			}
			break;
		case 3:
			if (STRING_0.equals(rule.getTues())) {
				return false;
			}
			break;
		case 4:
			if (STRING_0.equals(rule.getWed())) {
				return false;
			}
			break;
		case 5:
			if (STRING_0.equals(rule.getThur())) {
				return false;
			}
			break;
		case 6:
			if (STRING_0.equals(rule.getFri())) {
				return false;
			}
			break;
		case 7:
			if (STRING_0.equals(rule.getSun())) {
				return false;
			}
			break;

		default:
			break;
		}
		return true;
	}

	/**
	 * 已separator为分隔符，拼接字符串数组为字符串
	 * <br> 字符串数组为null或者空数组时，返回空字符串
	 * @param separator 分隔符
	 * @param strings 字符串数组
	 * @return 字符串
	 */
	public static String strArray2Str (char separator, String... strings) {
		if (strings == null || strings.length == 0) {
			return EMPTY;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strings.length; i++) {
			String str = strings[i];
			sb.append(str);
			if (i != (strings.length - 1)) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}

	/**
	 * 获取系统状态名称
	 * @param iCache 系统状态缓存
	 * @param corpId 企业ID （必须）
	 * @param type 类型 （必须）
	 * @param value 值 （必须）
	 * @param status 是否有效
	 * @return 系统状态名称
	 */
	@SuppressWarnings("unchecked")
	public static String getSystemStatusName1(ICache<Object> iCache, String corpId, String type, String value, String status) {
		String id = type.equals(SYSTEM_STATUS) || corpId == null ? type : corpId + "_" + type;
		List<SystemStatus> systemStatusList = (List<SystemStatus>) iCache.get(id);
		if (CollectionUtils.isEmpty(systemStatusList)) {
			iCache.init();
			systemStatusList = (List<SystemStatus>) iCache.get(id);
		}
		if (!CollectionUtils.isEmpty(systemStatusList)) {
			Iterator<SystemStatus> iter = systemStatusList.iterator();
			while(iter.hasNext()) {
				SystemStatus systemStatus = iter.next();
				if ((value == null || Strings.nullToEmpty(systemStatus.getValue()).equals(value))
						&& (status == null || Strings.nullToEmpty(systemStatus.getStatus()).equals(status))) {
					return Strings.nullToEmpty(systemStatus.getName());
				}
			}
		}
		return EMPTY;
	}

	public static void main(String[] args) {
		String[] strings = new String[] {"a", "b" ,"c"};
		String[] nulls = null;
		String[] empty = new String[]{};
		System.out.println(strArray2Str(',', strings));
		System.out.println(strArray2Str(',', nulls));
		System.out.println(strArray2Str(',', empty));
	}

}
