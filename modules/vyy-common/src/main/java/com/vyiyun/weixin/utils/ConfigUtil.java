/**
 * 
 */
package com.vyiyun.weixin.utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author tf
 * 
 *         2015年6月25日
 */
public class ConfigUtil {

	private static final Logger LOGGER = Logger.getLogger(ConfigUtil.class);

	private static ConfigUtil instance = new ConfigUtil();

	private static Map<String, PropertiesConfiguration> configMap = new HashMap<String, PropertiesConfiguration>();

	private boolean isAutoSave = true;

	private ConfigUtil() {
	}

	/**
	 * 获取内容
	 * 
	 * @param configName
	 * @param property
	 * @return
	 */
	public static String get(String configName, String property) {
		if (!configMap.containsKey(configName)) {
			instance.initConfig(configName);
		}
		PropertiesConfiguration config = configMap.get(configName);
		if (null != config) {
			return config.getString(property);
		} else {
			return null;
		}
	}

	/**
	 * 获取内容
	 * 
	 * @param configName
	 * @param property
	 * @return
	 */
	public static String get(String configName, String property, String defaultValue) {
		if (!configMap.containsKey(configName)) {
			instance.initConfig(configName);
		}
		PropertiesConfiguration config = configMap.get(configName);
		if (null != config) {
			return configMap.get(configName).getString(property);
		} else {
			return defaultValue;
		}
	}

	/**
	 * 载入配置文件，初始化后加入map
	 * 
	 * @param configName
	 */
	private synchronized void initConfig(String configName) {
		try {

			String path = SpringContextHolder.get(configName);
			if (StringUtils.isNotEmpty(path)) {
				URL url = org.apache.commons.configuration.ConfigurationUtils.locate(path);
				LOGGER.info("Loading configuration [" + url + "]...");
				PropertiesConfiguration config = new PropertiesConfiguration(url);
				configMap.put(configName, config);
				config.setAutoSave(isAutoSave);
			} else {
				LOGGER.error("Loading configuration [" + configName + "] path[" + path + "] fail!");
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static List<String> getStringArray(String configName, String property, String... defaultValue) {
		if (!configMap.containsKey(configName)) {
			instance.initConfig(configName);
		}
		List<String> dataArray = new ArrayList<String>();
		PropertiesConfiguration config = configMap.get(configName);
		if (null != config) {
			dataArray.addAll(Arrays.asList(config.getStringArray(property)));
		} else if (null != defaultValue && defaultValue.length > 0) {
			dataArray.addAll(Arrays.asList(defaultValue));
		}
		return dataArray;
	}
}
