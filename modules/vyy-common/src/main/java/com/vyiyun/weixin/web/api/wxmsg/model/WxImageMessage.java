package com.vyiyun.weixin.web.api.wxmsg.model;

import com.alibaba.fastjson.JSONObject;

public class WxImageMessage extends WxMediaMessage {

	public WxImageMessage() {
		super("image");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4703130710389278328L;

	/** 图片媒体文件id，可以调用上传临时素材或者永久素材接口获取,永久素材media_id必须由发消息的应用创建 */
	private String image;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.putAll(super.toJSON());
		JSONObject jsonTextObject = new JSONObject();
		jsonTextObject.put("media_id", getMedia_id());
		jsonObject.put("image", jsonTextObject);
		return jsonObject;
	}
}
