package com.vyiyun.weixin.web.api.wxmsg.model;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

/**
 * 微信实体对象接口a
 * 
 * @author tf
 * 
 * @date 下午3:41:23
 */
public interface WxMsg extends Serializable {
	/**
	 * 生成json对象
	 * 
	 * @return
	 */
	JSONObject toJSON();

	/**
	 * 获取消息类型
	 * 
	 * @return
	 */
	String getMsgType();
}
