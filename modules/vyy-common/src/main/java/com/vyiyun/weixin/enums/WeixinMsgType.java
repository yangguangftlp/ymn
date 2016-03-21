package com.vyiyun.weixin.enums;

public enum WeixinMsgType {
	// 0-文本消息, 1-图片消息, 2-声音消息, 3-视频消息, 4-文件消息,5-新闻消息,6-图文消息,7-其他;
	text(0), image(1), voice(2), video(3), file(4), news(5), mpnews(6), other(7);

	private int index = 0;

	private WeixinMsgType(int index) {
		this.index = index;
	}

	public static WeixinMsgType valueOf(int index) {
		switch (index) {
		case 0:
			return text;
		case 1:
			return image;
		case 2:
			return voice;
		case 3:
			return video;
		case 4:
			return file;
		case 5:
			return news;
		case 6:
			return mpnews;
		default:
			return other;
		}
	}

	public int index() {
		return this.index;
	}
}
