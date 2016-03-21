package com.vyiyun.weixin.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.vyiyun.weixin.cache.ICache;
import com.vyiyun.weixin.cache.impl.SystemConfigCache;
import com.vyiyun.weixin.entity.SystemConfig;

/**
 * 消息工具类
 * 
 * @author sunlight
 * 
 */
public class WeixinMessageUtil {

	private static final Logger LOGGER = Logger.getLogger(WeixinMessageUtil.class);
	/**
	 * 返回消息类型：文本
	 */
	public static final String RESP_MESSAGE_TYPE_TEXT = "text";

	/**
	 * 返回消息类型：音乐
	 */
	public static final String RESP_MESSAGE_TYPE_MUSIC = "music";

	/**
	 * 返回消息类型：图文
	 */
	public static final String RESP_MESSAGE_TYPE_NEWS = "news";

	/**
	 * 请求消息类型：文本
	 */
	public static final String REQ_MESSAGE_TYPE_TEXT = "text";

	/**
	 * 请求消息类型：图片
	 */
	public static final String REQ_MESSAGE_TYPE_IMAGE = "image";

	/**
	 * 请求消息类型：链接
	 */
	public static final String REQ_MESSAGE_TYPE_LINK = "link";

	/**
	 * 请求消息类型：地理位置
	 */
	public static final String REQ_MESSAGE_TYPE_LOCATION = "location";

	/**
	 * 请求消息类型：音频
	 */
	public static final String REQ_MESSAGE_TYPE_VOICE = "voice";

	/**
	 * 请求消息类型：推送
	 */
	public static final String REQ_MESSAGE_TYPE_EVENT = "event";

	/**
	 * 事件类型：subscribe(订阅)
	 */
	public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";

	/**
	 * 事件类型：unsubscribe(取消订阅)
	 */
	public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";

	/**
	 * 事件类型：CLICK(自定义菜单点击事件)
	 */
	public static final String EVENT_TYPE_CLICK = "CLICK";

	/**
	 * 事件类型：点击进入应用事件
	 */
	public static final String EVENT_AGENT = "enter_agent";

	/**
	 * 解析微信发来的请求（XML）
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> parseXml(String msg) throws Exception {
		// 将解析结果存储在HashMap中
		Map<String, String> map = new HashMap<String, String>();

		// 从request中取得输入流
		InputStream inputStream = new ByteArrayInputStream(msg.getBytes("UTF-8"));

		// 读取输入流
		SAXReader reader = new SAXReader();
		Document document = reader.read(inputStream);
		// 得到xml根元素
		Element root = document.getRootElement();
		// 得到根元素的所有子节点
		List<Element> elementList = root.elements();

		// 遍历所有子节点
		for (Element e : elementList)
			map.put(e.getName(), e.getText());

		// 释放资源
		inputStream.close();
		inputStream = null;

		return map;
	}

	/**
	 * object转xml
	 * 
	 * @param object
	 * @return
	 */
	public static String objectToXml(Object object) {
		return xstream.toXML(object);
	}

	/**
	 * 扩展xstream，使其支持CDATA块
	 * 
	 * @date 2013-05-19
	 */
	private static XStream xstream = new XStream(new XppDriver() {
		@Override
		public HierarchicalStreamWriter createWriter(Writer out) {
			return new PrettyPrintWriter(out) {
				// 对所有xml节点的转换都增加CDATA标记
				boolean cdata = true;

				@Override
				@SuppressWarnings("rawtypes")
				public void startNode(String name, Class clazz) {
					super.startNode(name, clazz);
				}

				@Override
				protected void writeText(QuickWriter writer, String text) {
					if (cdata) {
						writer.write("<![CDATA[");
						writer.write(text);
						writer.write("]]>");
					} else {
						writer.write(text);
					}
				}
			};
		}
	});

	/**
	 * 组装ID
	 * 
	 * @param name
	 * @param meetingId
	 * @return
	 */
	public static String geneMeetingTextMessage(String meetingId, String theme) {
		/*
		 * String corpId = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "corpId");
		 * String energyUrl = ConfigUtil.get(Constants.ENERGY_CONFIG_PATH,
		 * "energy_url"); ICache<Object> systemConfigCache =
		 * SystemCacheUtil.getInstance().getSystemConfigCache(); String template
		 * = ((SystemConfig)
		 * systemConfigCache.get("template_meeting_message")).getValue();
		 */
		// String url = MessageFormat.format(template, corpId, energyUrl,
		// meetingId);
		return "";// MessageFormat.format("<a href=\"{0}\">{1}</a>", url,
					// theme);
	}

	/**
	 * 请假审批
	 * 
	 * @param name
	 * @param meetingId
	 * @return
	 */
	/*
	 * public static String geneAbsentAuditTextMessage(String id) { String
	 * corpId = "";// ConfigUtil.get(Constants.WEIXIN_APP_PATH, // "corpId");
	 * SystemConfigCache<Object> systemConfigCache =
	 * SystemCacheUtil.getInstance().getSystemConfigCache(); String template =
	 * StringUtil
	 * .getString(systemConfigCache.getSystemConfig("url_absent_audit")); return
	 * MessageFormat.format(template, corpId, id); }
	 */

	/**
	 * 生成消息内容
	 * 
	 * @param templateId
	 *            对应模板templateId
	 * @param theme
	 *            主题
	 * @param args
	 *            参数
	 * @return
	 */
	public static String generateLinkUrlMsg(String templateId, Map<String, Object> themeMap, String webUrl,
			Object[] args) {
		String val = templateId.replace("template_", "");
		SystemConfigCache<Object> systemConfigCache = SystemCacheUtil.getInstance().getSystemConfigCache();
		SystemConfig systemConfig = systemConfigCache.getSystemConfig("template", val);
		String template = systemConfig.getValue();
		String textMsg = TemplateParser.getObj().parser(themeMap, systemConfig.getContent());
		if (!StringUtil.isEmpty(template) && !"#".equals(template)) {
			String url = MessageFormat.format(webUrl + template, args);
			textMsg = MessageFormat.format("<a href=\"{0}\">{1}</a>", url, textMsg);
		}
		LOGGER.debug("th generateLinkUrlMsg is:" + textMsg);
		return textMsg;
	}

	/**
	 * 生成消息内容
	 * 
	 * @param templateId
	 *            对应模板templateId
	 * @param theme
	 *            主题
	 * @param args
	 *            参数
	 * @return
	 */
	public static String generateTextMsg(String templateId, Map<String, Object> themeMap, Object[] args) {
		String val = templateId.replace("template_", "");
		SystemConfigCache<Object> systemConfigCache = SystemCacheUtil.getInstance().getSystemConfigCache();
		SystemConfig systemConfig = (SystemConfig) systemConfigCache.getSystemConfig("template",val);
		String textMsg = TemplateParser.getObj().parser(themeMap, systemConfig.getContent());
		LOGGER.debug("th egenerateTextMsg is:" + textMsg);
		return textMsg;
	}

	/**
	 * 生成消息内容
	 * 
	 * @param templateId
	 *            对应模板templateId
	 * @param theme
	 *            主题
	 * @param args
	 *            参数
	 * @return
	 */
	/*
	 * public static String generateUrlTextMsg(String templateId, Object...
	 * args) { ICache<Object> systemConfigCache =
	 * SystemCacheUtil.getInstance().getSystemConfigCache(); // SystemConfig
	 * systemConfig = (SystemConfig) // systemConfigCache.get(templateId);
	 * String template = ""; String theme = ""; String url =
	 * MessageFormat.format(template, args); String textMsg =
	 * MessageFormat.format("<a href=\"{0}\">{1}</a>", url, theme);
	 * LOGGER.debug("the text msg is:" + textMsg); return textMsg; }
	 */
}
