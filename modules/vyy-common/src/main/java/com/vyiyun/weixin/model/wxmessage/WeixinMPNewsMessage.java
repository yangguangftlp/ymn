package com.vyiyun.weixin.model.wxmessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.enums.WeixinMsgType;


/** 
 * @ClassName: WeixinMPNewsMessage 
 * @Description: 微信图文新闻消息
 * articles消息与news消息类似，不同的是图文消息内容存储在微信后台，
 * 并且支持保密选项。每个应用每天最多可以发送100次
 * @author CCLIU 
 * @date 2015-6-29 上午10:30:36 
 * v1.0
 */
public class WeixinMPNewsMessage extends WeixinMessageBase{

	private static final long serialVersionUID = 1866049550575155612L;
	
	/*
	 * 图文消息内容
	 */
    public List<WeixinMPArticle> articles;

	public WeixinMPNewsMessage(String touser, String toparty, String totag, WeixinMsgType msgtype, String agentid,
			List<WeixinMPArticle> articles) {
		super(touser, toparty, totag, msgtype, agentid);
		this.articles = articles;
	}

	public WeixinMPNewsMessage() {
		super();
	}

	public List<WeixinMPArticle> getArticles() {
		return articles;
	}

	public void setArticles(List<WeixinMPArticle> articles) {
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
		if(null!=articles&&articles.size()>0){
			List<Object> obj = new ArrayList<Object>();
			for (WeixinMPArticle article : articles) {
				Map<String, Object> ar = new HashMap<String, Object>();
				ar.put("title", article.getTitle());
				ar.put("thumb_media_id", article.getThumbMediaId());
				ar.put("author", article.getAuthor());
				ar.put("content_source_url", article.getContentSourceUrl());
				ar.put("content", article.getContent());
				ar.put("digest", article.getDigest());
				ar.put("show_cover_pic", article.getShowCoverPic());
				obj.add(ar);
			}
			param.put("articles", obj);
		}
		jo.put(msgtype.name(), param);
		return jo.toJSONString().replaceAll(",\\{\"\\$ref\":\"\\$.mpnews.articles\\[0\\]\"\\}", "");
	}

}
