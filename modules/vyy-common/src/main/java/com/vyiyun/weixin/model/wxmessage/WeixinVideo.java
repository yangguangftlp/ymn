package com.vyiyun.weixin.model.wxmessage;

import java.io.Serializable;

/** 
 * @ClassName: WeixinVideo 
 * @Description: 微信视频
 * @author CCLIU 
 * @date 2015-6-29 上午10:30:36 
 * v1.0
 */
public class WeixinVideo implements Serializable{

	private static final long serialVersionUID = -9218026119247014832L;
	
	/*
	 * 媒体ID
	 */
    public String mediaId;
	
	/*
	 * 视频标题
	 */
    public String title;
    
    /*
	 * 视频描述
	 */
    public String description;

	public WeixinVideo(String mediaId, String title, String description) {
		super();
		this.mediaId = mediaId;
		this.title = title;
		this.description = description;
	}

	public WeixinVideo() {
		super();
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
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
    
}
