package com.vyiyun.weixin.model.wxmessage;

import java.io.Serializable;

/** 
 * @ClassName: WeixinVideo 
 * @Description: 微信视频
 * @author CCLIU 
 * @date 2015-6-29 上午10:30:36 
 * v1.0
 */
public class WeixinArticle implements Serializable{

	private static final long serialVersionUID = -7679303594928144247L;

	/*
	 * 标题
	 */
    public String title;
    
    /*
	 * 描述
	 */
    public String description;
    
    /*
	 * 点击后跳转的链接
	 */
    public String url;
    
    /*
	 * 图文消息的图片链接，支持JPG、PNG格式，
	 * 较好的效果为大图640*320，小图80*80。如不填，在客户端不显示图片
	 */
    public String picurl;

	public WeixinArticle(String title, String description, String url, String picurl) {
		super();
		this.title = title;
		this.description = description;
		this.url = url;
		this.picurl = picurl;
	}

	public WeixinArticle() {
		super();
	}

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPicurl() {
		return picurl;
	}

	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}
	
}
