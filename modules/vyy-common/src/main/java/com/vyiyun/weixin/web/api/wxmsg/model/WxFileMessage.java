/**
 * 
 */
package com.vyiyun.weixin.web.api.wxmsg.model;

import com.alibaba.fastjson.JSONObject;

/**
 * file消息
 * 
 * @author tf
 * 
 * @date 下午4:21:08
 */
public class WxFileMessage extends WxMediaMessage {

	public WxFileMessage() {
		super("file");
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -5798468632751119956L;

	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.putAll(super.toJSON());
		JSONObject jsonFileObject = new JSONObject();
		jsonFileObject.put("media_id", getMedia_id());
		jsonObject.put("file", jsonFileObject);
		return jsonObject;
	}
}
