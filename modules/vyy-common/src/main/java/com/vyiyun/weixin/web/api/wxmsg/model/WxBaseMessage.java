package com.vyiyun.weixin.web.api.wxmsg.model;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.utils.StringUtil;

/**
 * 微信消息接口
 * 
 * @author tf
 * 
 * @date 下午3:32:59
 */
public class WxBaseMessage implements WxMsg {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4495979819601556196L;
	/** 员ID列表（消息接收者，多个接收者用‘|’分隔，最多支持1000个）。特殊情况：指定为@all，则向关注该企业应用的全部成员发送 */
	private String touser;
	/** 部门ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数 */
	private String toparty;
	/** 标签ID列表，多个接收者用‘|’分隔。当touser为@all时忽略本参数 */
	private String totag;
	/** 消息类型，此时固定为：text */
	private String msgtype;
	/** 企业应用的id，整型。可在应用的设置页面查看 */
	private String agentid;
	/** 表示是否是保密消息，0表示否，1表示是，默认0 */
	private String safe = "0";

	public WxBaseMessage(String msgtype) {
		this.msgtype = msgtype;
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

	public String getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(String msgtype) {
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

	@Override
	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		if (StringUtil.isNotEmpty(touser)) {
			jsonObject.put("touser", touser);
		}
		if (StringUtil.isNotEmpty(toparty)) {
			jsonObject.put("toparty", toparty);
		}
		if (StringUtil.isNotEmpty(totag)) {
			jsonObject.put("totag", totag);
		}
		jsonObject.put("msgtype", msgtype);
		jsonObject.put("agentid", agentid);
		jsonObject.put("safe", safe);
		return jsonObject;
	}

	@Override
	public String getMsgType() {
		// TODO 自动生成的方法存根
		return null;
	}

}
