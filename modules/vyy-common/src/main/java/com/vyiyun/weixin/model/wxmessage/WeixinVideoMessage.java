package com.vyiyun.weixin.model.wxmessage;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.enums.WeixinMsgType;

/** 
 * @ClassName: VideoMessage 
 * @Description: 微信视频消息
 * @author CCLIU 
 * @date 2015-6-29 上午10:30:36 
 * v1.0
 */
public class WeixinVideoMessage extends WeixinMessageBase{

	private static final long serialVersionUID = 7441156817882660128L;
	
	/*
	 * 消息视频
	 */
    public WeixinVideo video;

	public WeixinVideoMessage(String touser, String toparty, String totag, WeixinMsgType msgtype, String agentid,
			String safe, WeixinVideo video) {
		super(touser, toparty, totag, msgtype, agentid, safe);
		this.video = video;
	}

	public WeixinVideoMessage() {
		super();
	}

	public WeixinVideo getVideo() {
		return video;
	}

	public void setVideo(WeixinVideo video) {
		this.video = video;
	}
	
	@Override
	public String toJson() {
		JSONObject jo = new JSONObject();
		if (null != touser)
			jo.put("touser", touser);
		if (null != toparty)
			jo.put("toparty", toparty);
		if (null != totag)
			jo.put("totag", totag);
		if (null != safe)
			jo.put("safe", safe);
		jo.put("msgtype", msgtype);
		jo.put("agentid", agentid);
		Map<String, String> param = new HashMap<String, String>();
		param.put("media_id", video.mediaId);
		param.put("title", video.title);
		param.put("description", video.description);
		jo.put(msgtype.name(), param);
		return jo.toJSONString();
	}

}
