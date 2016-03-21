package com.vyiyun.weixin.web.api.wxmsg.model;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author tf
 * 
 * @date 下午3:39:25
 */
public class WxTextMessage extends WxBaseMessage {

	public WxTextMessage() {
		super("text");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -760949736534044473L;
	/** 消息内容 */
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.putAll(super.toJSON());
		JSONObject jsonTextObject = new JSONObject();
		jsonTextObject.put("content", text);
		jsonObject.put("text", jsonTextObject);
		return jsonObject;
	}

}
