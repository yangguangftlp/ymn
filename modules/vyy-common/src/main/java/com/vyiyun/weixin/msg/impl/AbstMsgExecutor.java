package com.vyiyun.weixin.msg.impl;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.msg.MsgExecutor;
import com.vyiyun.weixin.utils.SystemCacheUtil;
import com.vyiyun.weixin.utils.WeixinUtil;

/**
 * 抽象的消息执行器
 * 
 * @author tf
 * 
 * @date 2015年7月23日 下午4:14:13
 * @version 1.0
 */
public abstract class AbstMsgExecutor implements MsgExecutor, Serializable {

	private static Logger LOG = Logger.getLogger(AbstMsgExecutor.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 5530825770310909365L;
	private Object obj;
	protected String name;
	protected String corpId;
	protected String suiteId;
	protected String suiteSecret;

	public AbstMsgExecutor(Object obj) {
		super();
		this.obj = obj;
	}

	@Override
	public String getName() {
		return name;
	}

	public AbstMsgExecutor() {
		super();
	}

	protected String getAccessToken() throws Exception {
		return WeixinUtil.getAccessToken(corpId, suiteId, suiteSecret);
	}

	protected int sendMessage(String msgBody) throws Exception {
		LOG.debug("开始执行发送消息，msgBody：" + msgBody);
		JSONObject jsonObject = WeixinUtil.sendMessage(msgBody, getAccessToken());
		LOG.debug("执行发送消息结果，result：" + jsonObject);
		return 0;
	}

	/**
	 * 将套件中应用id换成当前企业应用id 使用场景：
	 * 
	 * @param id
	 */
	public String convertAppId(String appId) {
		return SystemCacheUtil.getInstance().getDefaultCache().convertAppId(this.corpId, this.suiteId, appId);
	}

	/*
	 * @Override public void execute() throws Exception { try { executeMsg(); }
	 * catch (Exception e) { e.printStackTrace(); } }
	 * 
	 * public abstract void executeMsg() throws Exception;
	 */

	public Object getObj() {
		return obj;
	}

	@Override
	public void set(Object obj) {
		this.obj = obj;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public void setSuiteId(String suiteId) {
		this.suiteId = suiteId;
	}

	public void setSuiteSecret(String suiteSecret) {
		this.suiteSecret = suiteSecret;
	}
}
