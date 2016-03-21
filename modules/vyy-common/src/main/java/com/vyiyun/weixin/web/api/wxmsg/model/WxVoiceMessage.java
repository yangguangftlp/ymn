package com.vyiyun.weixin.web.api.wxmsg.model;

import com.alibaba.fastjson.JSONObject;

/**
 * voice消息
 * 
 * @author tf
 * 
 * @date 下午4:08:41
 */
public class WxVoiceMessage extends WxMediaMessage {

	public WxVoiceMessage() {
		super("voice");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4994746381047547122L;

	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.putAll(super.toJSON());
		jsonObject.put("msgtype", "voice");
		JSONObject jsonTextObject = new JSONObject();
		jsonTextObject.put("media_id", getMedia_id());
		jsonObject.put("voice", jsonTextObject);
		return jsonObject;
	}
}
