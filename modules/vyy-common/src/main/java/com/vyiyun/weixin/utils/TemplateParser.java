package com.vyiyun.weixin.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.CollectionUtils;

/**
 * 模板解析器 主要用于模板内容配置
 * 
 * @author tf
 * 
 */
public class TemplateParser {
	private static TemplateParser instance;

	private TemplateParser() {

	}

	public static TemplateParser getObj() {
		if (null == instance) {
			synchronized (TemplateParser.class) {
				if (null == instance) {
					instance = new TemplateParser();
				}
			}
		}
		return instance;
	}

	/**
	 * 解析 主要解析模板中带有 ${}内容
	 * 
	 * @param dataMap
	 * @param template
	 * @return
	 */
	public String parser(Map<String, Object> dataMap, String template) {
		if (!CollectionUtils.isEmpty(dataMap)) {
			Pattern pattern = Pattern.compile("\\$\\{([a-zA-Z0-9_$]{1,})\\}");
			Matcher matcher = pattern.matcher(template);
			StringBuffer content = new StringBuffer();
			String key = null;
			String value = null;
			while (matcher.find()) {
				key = matcher.group(1);
				if (dataMap.containsKey(key)) {
					value = StringUtil.getString(dataMap.get(key));
					if (StringUtil.isEmpty(value)) {
						matcher.appendReplacement(content, key);
					} else {
						matcher.appendReplacement(content, value);
					}
				} else {
					matcher.appendReplacement(content, key);
				}
			}
			matcher.appendTail(content);
			return content.toString();
		}
		return template;
	}

	public static void main(String[] args) {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("themea", "11");
		dataMap.put("startTimea", "111");
		dataMap.put("thendTimeemea", "111");
		String template = "您好，你有一个签到任务主题：${theme} 时间:${startTime}~{endTime}";
		System.out.println(TemplateParser.getObj().parser(dataMap, template));
	}
}
