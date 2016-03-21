/**
 * 
 */
package com.vyiyun.weixin.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.vyiyun.weixin.framework.spring.MultipleResourceSource;
import com.vyiyun.weixin.msg.MsgQueueThread;

/**
 * @author tf
 * 
 * @date 2015年7月7日 下午1:51:01
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class SpringContextHolder implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

	/**
	 * spring 上下文
	 */
	private static ApplicationContext applicationContext;

	/**
	 * 系统是否初始化完成
	 */
	private static boolean isInit;

	private static DataSource dataSource;

	/**
	 * 系统配置文件路径
	 */
	private static Map<String, String> sysCfgFilePath = new HashMap<String, String>();

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContextHolder.applicationContext = applicationContext;
	}

	/**
	 * 检查
	 */
	public static void checkApplicationContext() {
		if (applicationContext == null) {
			throw new IllegalStateException("applicaitonContext未注入,请在applicationContext.xml中定义SpringContextHolder");
		}
	}

	/**
	 * 获取对象
	 * 
	 * @param name
	 * @return
	 */

	public static <T> T getBean(String name) {
		checkApplicationContext();
		return (T) applicationContext.getBean(name);
	}

	/**
	 * 获取对象
	 * 
	 * @param <T>
	 * 
	 * @param name
	 * @return
	 */
	public static <T> Object getBean(Class<T> className) {
		checkApplicationContext();
		return applicationContext.getBean(className);
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() == null) {
			// root application context 没有parent，他就是老大.
			// 需要执行的逻辑代码，当spring容器初始化完成后就会执行该方法。
			// 初始化系统配置
			init();
			isInit = true;
		}
	}

	public static ApplicationContext getApplicationContext() {
		checkApplicationContext();
		return applicationContext;
	}

	/**
	 * 初始化系统配置 系统启动后执行
	 */
	private static void init() {
		// 初始化系统消息队列线程
		Thread thread = new Thread(new MsgQueueThread());
		// 设置后台线程
		thread.setDaemon(true);
		thread.start();
		SystemCacheUtil.getInstance();
	}

	public static boolean isInit() {
		return isInit;
	}

	public void setSysCfgFilePath(Map<String, String> systemConfgFile) {
		if (null != systemConfgFile) {
			SpringContextHolder.sysCfgFilePath.putAll(systemConfgFile);
		}
	}

	public static DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		SpringContextHolder.dataSource = dataSource;
	}

	public static Map<String, String> getSysCfgFilePath() {
		return sysCfgFilePath;
	}

	/**
	 * 获取配置文件路径
	 * 
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		return sysCfgFilePath.get(key);
	}

	public static String getKeyVal(){
		MultipleResourceSource a = null;
		return null;
	}
	
	/**
	 * 获取国际化信息
	 * 
	 * @param key
	 * @param args
	 * @param defaultMessage
	 * @return
	 */
	public static String getI18n(String key, String[] args, String defaultMessage) {
		String message = null;
		if (null != applicationContext) {
			message = applicationContext.getMessage(key, args, defaultMessage, Locale.getDefault());
		}
		if (StringUtils.isNotEmpty(message)) {
			return message;
		}
		return key;
	}

	/**
	 * 获取国际化信息
	 * 
	 * @param key
	 * @param defaultMessage
	 * @return
	 */
	public static String getI18n(String key, String defaultMessage) {
		String message = null;
		if (null != applicationContext) {
			message = applicationContext.getMessage(key, null, defaultMessage, Locale.getDefault());
		}
		if (StringUtils.isNotEmpty(message)) {
			return message;
		}
		return key;
	}

	/**
	 * 获取国际化信息
	 * 
	 * @param key
	 * @return
	 */
	public static String getI18n(String key) {
		String message = null;
		if (null != applicationContext) {
			message = applicationContext.getMessage(key, null, null, Locale.getDefault());
		}
		if (StringUtils.isNotEmpty(message)) {
			return message;
		}
		return key;
	}
}
