/**
 * 
 */
package com.vyiyun.weixin.web.api.wxmsg;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.utils.HttpsUtil;
import com.vyiyun.weixin.web.api.wxmsg.model.WxMsg;

/**
 * @author tf
 * 
 *         2015年6月25日
 */
public class WxSendMessageApI {

	private static final Logger LOGGER = Logger.getLogger(WxSendMessageApI.class);
	/**
	 * 发送消息url
	 */
	private static final String SEND_MESSAGE_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=#0";

	private static WxSendMessageApI instance;

	public static WxSendMessageApI getInstance() {
		if (instance == null) {
			synchronized (WxSendMessageApI.class) {
				if (instance == null) {
					instance = new WxSendMessageApI();
				}
			}
		}
		return instance;
	}

	/**
	 * 发送图片消息
	 * 
	 * @param message
	 * @throws Exception
	 */
	public JSONObject sendMsg(WxMsg wxMsg, String accessToken) throws Exception {
		LOGGER.debug("发送消息开始...，消息类型【" + wxMsg.getMsgType() + "】");
		JSONObject responseBody = HttpsUtil.post(SEND_MESSAGE_URL.replace("#0", accessToken), wxMsg.toJSON()
				.toJSONString());
		LOGGER.debug("发送消息结束...，消息响应:" + responseBody);
		return responseBody;
	}
}
