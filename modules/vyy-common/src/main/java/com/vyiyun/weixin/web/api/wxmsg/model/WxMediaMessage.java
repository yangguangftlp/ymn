/**
 * 
 */
package com.vyiyun.weixin.web.api.wxmsg.model;

import com.alibaba.fastjson.JSONObject;

/**
 * 媒体
 * 
 * @author tf
 * 
 * @date 下午4:22:40
 */
public class WxMediaMessage extends WxBaseMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7945464390477761615L;
	/** 视频媒体文件id，可以调用上传临时素材或者永久素材接口获取 */
	private String media_id;

	public String getMedia_id() {
		return media_id;
	}

	public void setMedia_id(String media_id) {
		this.media_id = media_id;
	}

	public WxMediaMessage(String msgtype) {
		super(msgtype);
	}

	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.putAll(super.toJSON());
		return jsonObject;
	}
}
