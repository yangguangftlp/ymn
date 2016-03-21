package com.vyiyun.weixin.model.wxmessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.enums.WeixinMsgType;


/** 
 * @ClassName: WeixinNewsMessage 
 * @Description: 微信新闻消息
 * @author CCLIU 
 * @date 2015-6-29 上午10:30:36 
 * v1.0
 */
public class WeixinNewsMessage extends WeixinMessageBase{

	private static final long serialVersionUID = -6520986640415839662L;
	
	/*
	 * 消息内容
	 */
    public List<WeixinArticle> articles;

	public WeixinNewsMessage(String touser, String toparty, String totag, WeixinMsgType msgtype, String agentid,
			List<WeixinArticle> articles) {
		super(touser, toparty, totag, msgtype, agentid);
		this.articles = articles;
	}

	public WeixinNewsMessage() {
		super();
	}

	public List<WeixinArticle> getArticles() {
		return articles;
	}

	public void setArticles(List<WeixinArticle> articles) {
		this.articles = articles;
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
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("articles", articles);
		jo.put(msgtype.name(), param);
		return jo.toJSONString();
	}

}
