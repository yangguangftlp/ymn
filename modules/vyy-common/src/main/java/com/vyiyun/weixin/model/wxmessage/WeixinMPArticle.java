package com.vyiyun.weixin.model.wxmessage;

import java.io.Serializable;

/** 
 * @ClassName: WeixinVideo 
 * @Description: 微信视频
 * @author CCLIU 
 * @date 2015-6-29 上午10:30:36 
 * v1.0
 */
public class WeixinMPArticle implements Serializable{

	private static final long serialVersionUID = -7679303594928144247L;
 
	/*
	 * 标题
	 */
    public String title;
    
    /*
	 * 图文消息缩略图的media_id, 可以在上传多媒体文件接口中获得。
	 * 此处thumb_media_id即上传接口返回的media_id
	 */
    public String thumbMediaId;
    
    /*
	 * 图文消息的作者
	 */
    public String author;
    
    /*
	 * 图文消息点击“阅读原文”之后的页面链接
	 */
    public String contentSourceUrl;
    
	/*
   	 * 图文消息的内容，支持html标签
	 */
	public String content;
       
	/*
  	 * 图文消息的摘要
  	 */
	public String digest;
	
	/*
  	 * 是否显示封面，1为显示，0为不显示
  	 */
	public int showCoverPic;
	
	public WeixinMPArticle(String title, String thumbMediaId, String author,
			String contentSourceUrl, String content, String digest, int showCoverPic) {
		super();
		this.title = title;
		this.thumbMediaId = thumbMediaId;
		this.author = author;
		this.contentSourceUrl = contentSourceUrl;
		this.content = content;
		this.digest = digest;
		this.showCoverPic = showCoverPic;
	}

	public WeixinMPArticle() {
		super();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getThumbMediaId() {
		return thumbMediaId;
	}

	public void setThumbMediaId(String thumbMediaId) {
		this.thumbMediaId = thumbMediaId;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getContentSourceUrl() {
		return contentSourceUrl;
	}

	public void setContentSourceUrl(String contentSourceUrl) {
		this.contentSourceUrl = contentSourceUrl;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public int getShowCoverPic() {
		return showCoverPic;
	}

	public void setShowCoverPic(int showCoverPic) {
		this.showCoverPic = showCoverPic;
	}

}
