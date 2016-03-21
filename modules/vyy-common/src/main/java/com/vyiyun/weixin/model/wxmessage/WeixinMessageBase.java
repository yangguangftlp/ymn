package com.vyiyun.weixin.model.wxmessage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.enums.WeixinMsgType;

/**
 * @ClassName: WXMessageBase
 * @Description: 微信消息基础类
 * @author CCLIU
 * @date 2015-6-29 上午10:30:36 v1.0
 */
public class WeixinMessageBase implements Serializable {

	private static final long serialVersionUID = 919256029431311664L;

	/*
	 * 选填，UserID列表（消息接收者，多个接收者用‘|’分隔）。特殊情况：指定为@all，则向关注该企业应用的全部成员发送
	 */
	public String touser;

	/*
	 * 选填，PartyID列表，多个接受者用‘|’分隔。当touser为@all时忽略本参数
	 */
	public String toparty;

	/*
	 * 选填，TagID列表，多个接受者用‘|’分隔。当touser为@all时忽略本参数
	 */
	public String totag;

	/*
	 * 必填，消息类型
	 */
	public WeixinMsgType msgtype;

	/*
	 * 必填，企业应用的id，整型。可在应用的设置页面查看
	 */
	public String agentid;

	/*
	 * 选填，表示是否是保密消息，0表示否，1表示是，默认0
	 */
	public String safe;

	/*
	 * 值，用于填写text消息的content，或者image、voice、file消息的media_id
	 */
	public String value;

	public WeixinMessageBase(String touser, String toparty, String totag, WeixinMsgType msgtype,
			String agentid, String safe, String value) {
		super();
		this.touser = touser;
		this.toparty = toparty;
		this.totag = totag;
		this.msgtype = msgtype;
		this.agentid = agentid;
		this.safe = safe;
		this.value = value;
	}

	public WeixinMessageBase(String touser, String toparty, String totag, WeixinMsgType msgtype,
			String agentid, String safe) {
		super();
		this.touser = touser;
		this.toparty = toparty;
		this.totag = totag;
		this.msgtype = msgtype;
		this.agentid = agentid;
		this.safe = safe;
	}

	public WeixinMessageBase(String touser, String toparty, String totag, WeixinMsgType msgtype,
			String agentid) {
		super();
		this.touser = touser;
		this.toparty = toparty;
		this.totag = totag;
		this.msgtype = msgtype;
		this.agentid = agentid;
	}
	
	public WeixinMessageBase(String touser, WeixinMsgType msgtype, String agentid, String value) {
		super();
		this.touser = touser;
		this.msgtype = msgtype;
		this.agentid = agentid;
		this.value = value;
	}

	public WeixinMessageBase() {
		super();
	}

	public String getTouser() {
		return touser;
	}

	public void setTouser(String touser) {
		this.touser = touser;
	}

	public String getToparty() {
		return toparty;
	}

	public void setToparty(String toparty) {
		this.toparty = toparty;
	}

	public String getTotag() {
		return totag;
	}

	public void setTotag(String totag) {
		this.totag = totag;
	}

	public WeixinMsgType getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(WeixinMsgType msgtype) {
		this.msgtype = msgtype;
	}

	public String getAgentid() {
		return agentid;
	}

	public void setAgentid(String agentid) {
		this.agentid = agentid;
	}

	public String getSafe() {
		return safe;
	}

	public void setSafe(String safe) {
		this.safe = safe;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

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
		param.put(msgtype == WeixinMsgType.text ? "content" : "media_id", value);
		jo.put(msgtype.name(), param);
		return jo.toJSONString();
	}

}
