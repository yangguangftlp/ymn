/**
 * 
 */
package com.vyiyun.weixin.web.api.wxmsg.model;

import com.alibaba.fastjson.JSONObject;

/**
 * 视频消息
 * 
 * @author tf
 * 
 * @date 下午4:17:21
 */
public class WxVideoMessage extends WxMediaMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3397099970903678586L;

	public WxVideoMessage() {
		super("video");
	}

	/** 视频消息的标题 */
	private String title;
	/** 视频消息的描述 */
	private String description;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.putAll(super.toJSON());
		jsonObject.put("msgtype", "voice");
		JSONObject jsonVideoObject = new JSONObject();
		jsonVideoObject.put("media_id", getMedia_id());
		jsonVideoObject.put("title", title);
		jsonVideoObject.put("description", description);
		jsonObject.put("video", jsonVideoObject);
		return jsonObject;
	}
}
