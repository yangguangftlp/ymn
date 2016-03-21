package com.vyiyun.weixin.model.wxmessage;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;

import com.vyiyun.weixin.enums.WeixinMsgType;

/**
 * @ClassName: WeixinResponseMessage
 * @Description: 微信被动响应消息
 * @author CCLIU
 * @date 2015-6-29 上午10:30:36 v1.0
 */
public class WeixinResponseMessage implements Serializable {

	private static final long serialVersionUID = 4352208869985298140L;

	/*
	 * 成员UserID
	 */
	public String toUserName;

	/*
	 * 企业号CorpID
	 */
	public String fromUserName;

	/*
	 * 消息创建时间（整型）
	 */
	public long createTime;

	/*
	 * 消息类型
	 */
	public WeixinMsgType msgType;

	/*
	 * 文本消息内容
	 */
	public String content;

	/*
	 * 文件id，可以调用上传媒体文件接口获取
	 */
	public String mediaId;

	/*
	 * 消息视频
	 */
	public WeixinVideo video;

	/*
	 * 图文消息
	 */
	public WeixinArticle[] articles;

	public WeixinResponseMessage() {
		super();
	}

	/**
	 * text消息的构造函数
	 * 
	 * @param toUserName
	 * @param fromUserName
	 * @param createTime
	 * @param msgType
	 * @param content
	 */
	public WeixinResponseMessage(String toUserName, String fromUserName,
			WeixinMsgType msgType, String content) {
		super();
		this.toUserName = toUserName;
		this.fromUserName = fromUserName;
		this.createTime = new Date().getTime();
		this.msgType = msgType;
		this.content = content;
	}

	/**
	 * image、voice消息的构造函数
	 * 
	 * @param toUserName
	 * @param fromUserName
	 * @param createTime
	 * @param mediaId
	 * @param msgType
	 */
	public WeixinResponseMessage(String toUserName, String fromUserName,
			String mediaId, WeixinMsgType msgType) {
		super();
		this.toUserName = toUserName;
		this.fromUserName = fromUserName;
		this.createTime = new Date().getTime();
		this.msgType = msgType;
		this.mediaId = mediaId;
	}

	/**
	 * video消息的构造函数
	 * 
	 * @param toUserName
	 * @param fromUserName
	 * @param createTime
	 * @param msgType
	 * @param video
	 */
	public WeixinResponseMessage(String toUserName, String fromUserName,
			WeixinMsgType msgType, WeixinVideo video) {
		super();
		this.toUserName = toUserName;
		this.fromUserName = fromUserName;
		this.createTime = new Date().getTime();
		this.msgType = msgType;
		this.video = video;
	}

	/**
	 * news 图文消息的构造函数
	 * 
	 * @param toUserName
	 * @param fromUserName
	 * @param createTime
	 * @param msgType
	 * @param articles
	 */
	public WeixinResponseMessage(String toUserName, String fromUserName,
			WeixinMsgType msgType, WeixinArticle... articles) {
		super();
		this.toUserName = toUserName;
		this.fromUserName = fromUserName;
		this.createTime = new Date().getTime();
		this.msgType = msgType;
		this.articles = articles;
	}

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public WeixinMsgType getMsgType() {
		return msgType;
	}

	public void setMsgType(WeixinMsgType msgType) {
		this.msgType = msgType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public WeixinVideo getVideo() {
		return video;
	}

	public void setVideo(WeixinVideo video) {
		this.video = video;
	}

	public WeixinArticle[] getArticles() {
		return articles;
	}

	public void setArticles(WeixinArticle... articles) {
		this.articles = articles;
	}

	public String toXml() {
		StringBuffer result = new StringBuffer(MessageFormat.format("<xml><ToUserName><![CDATA[{0}]]></ToUserName>"
				+ "<FromUserName><![CDATA[{1}]]></FromUserName><CreateTime>{2}</CreateTime>"
				+ "<MsgType><![CDATA[{3}]]></MsgType>", toUserName, fromUserName,
				createTime+"", msgType.name()));
		switch (msgType) {
		case text:
			result.append("<Content><![CDATA[" + content + "]]></Content>");
			break;
		case image:
			result.append("<Image><MediaId><![CDATA[" + mediaId + "]]></MediaId></Image>");
			break;
		case voice:
			result.append("<Voice><MediaId><![CDATA[" + mediaId + "]]></MediaId></Voice>");
			break;
		case video:
			result.append(MessageFormat.format("<Video><MediaId><![CDATA[{0}]]></MediaId>"
					+ "<Title><![CDATA[{1}]]></Title>"
					+ "<Description><![CDATA[{2}]]></Description><Video>", video.getMediaId(),
					video.getTitle(), video.getDescription()));
			break;
		case news:
			String arti = "<item>" + "<Title><![CDATA[{0}]]></Title>"
					+ "<Description><![CDATA[{1}]]></Description>"
					+ "<PicUrl><![CDATA[{2}]]></PicUrl>" + "<Url><![CDATA[{3}]]></Url>" + "</item>";
			if (null != articles && articles.length > 0) {
				result.append("<ArticleCount>"+articles.length+"</ArticleCount>");
				result.append("<Articles>");
				for (WeixinArticle article : articles) {
					result.append(MessageFormat.format(arti, article.getTitle(), article.getDescription(),
							article.getPicurl(), article.getUrl()));
				}
				result.append("</Articles>");
			}
			break;
		default:
			break;
		};
		result.append("</xml>");
		return result.toString();
	}
	
	public static void main(String[] args) {
		// text 消息
		WeixinResponseMessage s = new WeixinResponseMessage("1","2",WeixinMsgType.text,"3");
		System.out.println(s.toXml());
		// image 消息
		WeixinResponseMessage s1 = new WeixinResponseMessage("1","2","3",WeixinMsgType.image);
		System.out.println(s1.toXml());
		// voice 消息
		WeixinResponseMessage s2 = new WeixinResponseMessage("1","2","3",WeixinMsgType.voice);
		System.out.println(s2.toXml());
		// video 消息
		WeixinResponseMessage s3 = new WeixinResponseMessage("1","2",WeixinMsgType.video, new WeixinVideo("333","fff","ddd"));
		System.out.println(s3.toXml());
		// text 消息
		WeixinResponseMessage s4 = new WeixinResponseMessage("1","2",WeixinMsgType.news);
		System.out.println(s4.toXml());
	}
}
