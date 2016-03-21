package com.vyiyun.weixin.utils;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

/**
 * @ClassName: CommonUtil
 * @Description: 工具类
 * @author CCLIU
 * @date 2015-6-18 下午3:35:32 v1.0
 */
public class CommonUtil {

	/**
	 * 获取32位GUID
	 * 
	 * @return
	 */
	public static String GeneGUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * 生成单据号
	 * 
	 * @return
	 */
	public static String geneSerialNum(String prefix) {
		if (StringUtils.isNotEmpty(prefix)) {
			return prefix + DateUtil.dateToString(new Date(), "yyMMddHHmmss");
		} else {
			return DateUtil.dateToString(new Date(), "yyMMddHHmmss");
		}
	}

	/**
	 * 生成单据号
	 * 
	 * @return
	 */
	public static String geneExpenseNum() {
		return DateUtil.dateToString(new Date(), "yyMMddHHmmssSSS");
	}
}
