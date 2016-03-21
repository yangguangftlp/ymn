package com.vyiyun.weixin.controller;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.enums.WeixinMsgType;
import com.vyiyun.weixin.model.wxmessage.WeixinMessageBase;
import com.vyiyun.weixin.service.ISystemStatusService;
import com.vyiyun.weixin.utils.ConfigUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.IOUtil;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;
import com.vyiyun.weixin.utils.WeixinMessageUtil;
import com.vyiyun.weixin.utils.WeixinUtil;
import com.vyiyun.weixin.utils.wxutil.WXBizMsgCrypt;

/**
 * @ClassName: WeixinController
 * @Description: 微信相关请求处理器
 * @author CCLIU
 * @date 2015-6-11 上午11:05:22 v1.0
 */
//@Controller
@RequestMapping(value = "/wx")
public class WeixinController extends AbstWebController {

	private static final Logger LOGGER = Logger.getLogger(WeixinController.class);

	@Autowired
	private ISystemStatusService systemStatusService;

	/**
	 * 企业号应用验证
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "weixin", method = RequestMethod.GET)
	public void weixinGet(HttpServletRequest request, HttpServletResponse response) {
		// 请求参数
		// 读取配置
		String sCorpID = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "corpId");
		String sToken = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "token");
		String sEncodingAESKey = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "encodingAESKey");
		String sEchoStr = "init"; // 需要返回的明文
		try {
			WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);
			String sVerifyMsgSig = HttpRequestUtil.getInst().getString("msg_signature").toString();
			String sVerifyTimeStamp = HttpRequestUtil.getInst().getString("timestamp").toString();
			String sVerifyNonce = HttpRequestUtil.getInst().getString("nonce").toString();
			String sVerifyEchoStr = HttpRequestUtil.getInst().getString("echostr").toString();

			sEchoStr = wxcpt.VerifyURL(sVerifyMsgSig, sVerifyTimeStamp, sVerifyNonce, sVerifyEchoStr);
			System.out.println("verifyurl echostr: " + sEchoStr);
			response.getOutputStream().write(sEchoStr.getBytes());
			response.getOutputStream().flush();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 企业号消息接收
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "weixin", method = RequestMethod.POST)
	public void weixinPost(HttpServletRequest request, HttpServletResponse response) {
		// 请求参数
		// 读取配置
		String sCorpID = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "corpId");
		String sToken = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "token");
		String sEncodingAESKey = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "encodingAESKey");
		try {
			WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);
			String sVerifyMsgSig = HttpRequestUtil.getInst().getString("msg_signature").toString();
			String sVerifyTimeStamp = HttpRequestUtil.getInst().getString("timestamp").toString();
			String sVerifyNonce = HttpRequestUtil.getInst().getString("nonce").toString();

			// 从请求中读取整个post数据
			InputStream inputStream = request.getInputStream();
			String postData = IOUtil.getString(inputStream, "UTF-8");

			// 解密消息，取得数据
			String msg = wxcpt.DecryptMsg(sVerifyMsgSig, sVerifyTimeStamp, sVerifyNonce, postData);
			Map<String, String> requestMap = WeixinMessageUtil.parseXml(msg);
			String fromUserName = requestMap.get("FromUserName");// 发送方帐号
			String agentId = requestMap.get("AgentID");// 应用ID
			String msgType = requestMap.get("MsgType");// 消息类型

			// 处理进入应用事件
			if (msgType.equals(WeixinMessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
				/*
				 * String eventType = requestMap.get("Event"); if
				 * (eventType.equalsIgnoreCase(WeixinMessageUtil.EVENT_AGENT)) {
				 * SystemStatus status =
				 * systemStatusService.getSystemStatus("AppId", agentId); String
				 * appName = status == null ? "" : status.getName(); WeixinUser
				 * weixinUser = CommonUtil.getUserById(fromUserName); String
				 * userName = weixinUser == null ? "" : weixinUser.getName();
				 * AppAccess appAccess = new AppAccess(CommonUtil.GeneGUID(),
				 * agentId, appName, fromUserName, userName, new Date());
				 * appAccessService.updateAppAccessByDay(appAccess); }
				 */
			}

			// 其他事件暂不处理

			/*
			 * // 调用核心业务类接收消息、处理消息 String respMessage =
			 * WeixinUtil.messageHandle(request, msg); // 加密回复消息 String
			 * encryptMsg = wxcpt.EncryptMsg(respMessage, sVerifyTimeStamp,
			 * sVerifyNonce); PrintWriter out = response.getWriter();
			 * out.print(encryptMsg); out.close();
			 */
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 微信服务商登陆授权页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "wxService", method = { RequestMethod.GET })
	public ModelAndView wxServicePage(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelView = new ModelAndView("wxService");
		String authCode = StringUtil.getString(HttpRequestUtil.getInst().getString("auth_code"));
		String corpId = "wx7d7e4794f9070be4"; // 授权企业号的ID
		String suiteId = "tjf320fd06332151fa"; // 套件ID
		modelView.addObject("suiteId", suiteId);
		int appId = 1; // 套件中某应用的ID
		try {
			// 获取企业号授权管理员信息
			JSONObject jo = WeixinUtil.getAuthManagerInfo(authCode);
			modelView.addObject("managerInfo", jo);

			// 获取套件token
			String suiteToken = WeixinUtil.getSuiteToken(suiteId);
			System.out.println("suiteToken=====" + suiteToken);

			// 获取套件预授权码
			String preAuthCode = WeixinUtil.getPreAuthCode(suiteId, appId);
			System.out.println("preAuthCode=====" + preAuthCode);
			modelView.addObject("preAuthCode", preAuthCode);

			// 获取套件永久授权码，另外包括授权信息、企业access_token
			JSONObject permanentCode = WeixinUtil.getPermanentCode(suiteId, authCode);
			System.out.println("permanentCode=====" + permanentCode);
			if (null != permanentCode) {
				String pCode = permanentCode.getString("permanent_code");
				SystemCacheUtil.getInstance().getDefaultCache().add(suiteId + "_" + corpId + "_permanent_code", pCode);
				int agentId = permanentCode.getJSONObject("auth_info").getJSONArray("agent").getJSONObject(0)
						.getIntValue("agentid");

				// 获取授权企业号信息及授权应用信息
				JSONObject authInfo = WeixinUtil.getAuthInfo(suiteId, corpId, pCode);
				System.out.println("authInfo=====" + authInfo);

				// 获取某个授权应用信息
				JSONObject agentInfo = WeixinUtil.getAgentInfo(suiteId, corpId, pCode, agentId);
				System.out.println("agentInfo=====" + agentInfo);

				// 设置某个授权应用信息
				JSONObject setResult = WeixinUtil.setAgentInfo(suiteId, corpId, pCode, agentId, 2,
						"jXFwFjZjVyvMv_tnSbKBbBPBJ6HAKz786aHlg2tpF9DM3iSAOf2PD05PvsBsUA1V", "授权应用测试", "授权应用侧四哈啊哈",
						"weixin.energyexplorer.cn", 1, 1);
				System.out.println("setResult=====" + setResult);

				// 获取授权企业号token
				JSONObject corpToken = WeixinUtil.getCorpToken(suiteId, corpId, pCode);
				System.out.println("corpToken=====" + corpToken);

				// 设置某个授权应用信息
				JSONObject setApp = WeixinUtil.setAppDetail(corpToken.getString("access_token"), agentId, 2,
						"jXFwFjZjVyvMv_tnSbKBbBPBJ6HAKz786aHlg2tpF9DM3iSAOf2PD05PvsBsUA1V", "授权应用测试", "授权应用侧四哈啊哈",
						"weixin.energyexplorer.cn", 1, 1);
				System.out.println("setApp=====" + setApp);

				// 修改应用菜单
				JSONObject menuParams = new JSONObject();
				JSONArray menuArray = new JSONArray();
				JSONObject menuItem = new JSONObject();
				menuItem.put("name", "闯闯的测试");
				menuItem.put("type", "view");
				menuItem.put("url", "https://www.baidu.com");
				menuArray.add(menuItem);
				menuParams.put("button", menuArray);
				boolean result = WeixinUtil.createMenu(corpToken.getString("access_token"), agentId, menuParams);
				System.out.println("createMenu=====" + result);

				// 发送消息
				String content = new WeixinMessageBase("NT00015001", WeixinMsgType.text, agentId + "", "这是服务商测试消息")
						.toJson();
				WeixinUtil.sendMessageWithToken(corpToken.getString("access_token"), content);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return modelView;
	}

	/**
	 * “日常生活”套件服务事件接收
	 * 
	 * @return
	 */
	@RequestMapping(value = "suiteLife", method = { RequestMethod.GET, RequestMethod.POST })
	public void suiteLife(HttpServletRequest request, HttpServletResponse response) {

		// 读取配置
		String sSuiteId = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "suite_life_id");
		String sToken = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "token");
		String sEncodingAESKey = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "encodingAESKey");
		try {
			WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sSuiteId);
			String sVerifyMsgSig = HttpRequestUtil.getInst().getString("msg_signature").toString();
			String sVerifyTimeStamp = HttpRequestUtil.getInst().getString("timestamp").toString();
			String sVerifyNonce = HttpRequestUtil.getInst().getString("nonce").toString();
			// 从请求中读取整个post数据
			InputStream inputStream = request.getInputStream();
			String postData = IOUtil.getString(inputStream, "UTF-8");
			// 解密消息
			String msg = wxcpt.DecryptMsg(sVerifyMsgSig, sVerifyTimeStamp, sVerifyNonce, postData);
			// xml请求解析
			Map<String, String> requestMap = WeixinMessageUtil.parseXml(msg);
			String infoType = requestMap.get("InfoType");
			if ("suite_ticket".equals(infoType)) {// 若为套件ticket信息
				// 将信息放入缓存中
				LOGGER.debug("日常生活套件ticket：" + requestMap);
				SystemCacheUtil.getInstance().getDefaultCache().add("suite_life_ticket", requestMap);
			} else if ("change_auth".equals(infoType)) { // 变更授权
				LOGGER.debug("变更授权：corpid=" + requestMap.get("AuthCorpId") + "-------SuiteId="
						+ requestMap.get("SuiteId"));
				// 修改数据库数据，增加或修改应用关联
			} else if ("cancel_auth".equals(infoType)) { // 取消授权
				LOGGER.debug("取消授权：corpid=" + requestMap.get("AuthCorpId") + "-------SuiteId="
						+ requestMap.get("SuiteId"));
				// 修改数据库数据，取消用户应用关联
			}
			// 响应消息
			PrintWriter out = response.getWriter();
			out.print("success");
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * “工作小娱乐”套件服务事件接收
	 * 
	 * @return
	 */
	@RequestMapping(value = "suiteEntm", method = { RequestMethod.GET, RequestMethod.POST })
	public void suiteEntm(HttpServletRequest request, HttpServletResponse response) {
		// 读取配置
		String sSuiteId = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "suite_entertainment_id");
		String sToken = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "token");
		String sEncodingAESKey = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "encodingAESKey");
		try {
			WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sSuiteId);
			String sVerifyMsgSig = HttpRequestUtil.getInst().getString("msg_signature").toString();
			String sVerifyTimeStamp = HttpRequestUtil.getInst().getString("timestamp").toString();
			String sVerifyNonce = HttpRequestUtil.getInst().getString("nonce").toString();
			// 从请求中读取整个post数据
			InputStream inputStream = request.getInputStream();
			String postData = IOUtil.getString(inputStream, "UTF-8");
			// 解密消息
			String msg = wxcpt.DecryptMsg(sVerifyMsgSig, sVerifyTimeStamp, sVerifyNonce, postData);
			// xml请求解析
			Map<String, String> requestMap = WeixinMessageUtil.parseXml(msg);
			String infoType = requestMap.get("InfoType");
			if ("suite_ticket".equals(infoType)) {// 若为套件ticket信息
				// 将信息放入缓存中
				LOGGER.debug("工作小娱乐套件ticket：" + requestMap);
				SystemCacheUtil.getInstance().getDefaultCache().add("suite_entertainment_ticket", requestMap);
			} else if ("change_auth".equals(infoType)) { // 变更授权
				LOGGER.debug("变更授权：corpid=" + requestMap.get("AuthCorpId") + "-------SuiteId="
						+ requestMap.get("SuiteId"));
				// 修改数据库数据，增加或修改应用关联
			} else if ("cancel_auth".equals(infoType)) { // 取消授权
				LOGGER.debug("取消授权：corpid=" + requestMap.get("AuthCorpId") + "-------SuiteId="
						+ requestMap.get("SuiteId"));
				// 修改数据库数据，取消用户应用关联
			}
			// 响应消息
			PrintWriter out = response.getWriter();
			out.print("success");
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * “信息发布”套件服务事件接收
	 * 
	 * @return
	 */
	@RequestMapping(value = "suiteInfo", method = { RequestMethod.GET, RequestMethod.POST })
	public void suiteInfo(HttpServletRequest request, HttpServletResponse response) {
		// 读取配置
		String sSuiteId = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "suite_information_id");
		String sToken = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "token");
		String sEncodingAESKey = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "encodingAESKey");
		try {
			WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sSuiteId);
			String sVerifyMsgSig = HttpRequestUtil.getInst().getString("msg_signature").toString();
			String sVerifyTimeStamp = HttpRequestUtil.getInst().getString("timestamp").toString();
			String sVerifyNonce = HttpRequestUtil.getInst().getString("nonce").toString();
			// 从请求中读取整个post数据
			InputStream inputStream = request.getInputStream();
			String postData = IOUtil.getString(inputStream, "UTF-8");
			// 解密消息
			String msg = wxcpt.DecryptMsg(sVerifyMsgSig, sVerifyTimeStamp, sVerifyNonce, postData);
			// xml请求解析
			Map<String, String> requestMap = WeixinMessageUtil.parseXml(msg);
			String infoType = requestMap.get("InfoType");
			if ("suite_ticket".equals(infoType)) {// 若为套件ticket信息
				// 将信息放入缓存中
				LOGGER.debug("信息发布套件ticket：" + requestMap);
				SystemCacheUtil.getInstance().getDefaultCache().add("suite_information_ticket", requestMap);
			} else if ("change_auth".equals(infoType)) { // 变更授权
				LOGGER.debug("变更授权：corpid=" + requestMap.get("AuthCorpId") + "-------SuiteId="
						+ requestMap.get("SuiteId"));
				// 修改数据库数据，增加或修改应用关联
			} else if ("cancel_auth".equals(infoType)) { // 取消授权
				LOGGER.debug("取消授权：corpid=" + requestMap.get("AuthCorpId") + "-------SuiteId="
						+ requestMap.get("SuiteId"));
				// 修改数据库数据，取消用户应用关联
			}
			// 响应消息
			PrintWriter out = response.getWriter();
			out.print("success");
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * “移动办公”套件服务事件接收
	 * 
	 * @return
	 */
	@RequestMapping(value = "suiteMoa", method = { RequestMethod.GET, RequestMethod.POST })
	public void suiteMoa(HttpServletRequest request, HttpServletResponse response) {
		// 读取配置
		String sSuiteId = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "suite_oa_id");
		String sToken = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "token");
		String sEncodingAESKey = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "encodingAESKey");
		try {
			WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sSuiteId);
			String sVerifyMsgSig = HttpRequestUtil.getInst().getString("msg_signature").toString();
			String sVerifyTimeStamp = HttpRequestUtil.getInst().getString("timestamp").toString();
			String sVerifyNonce = HttpRequestUtil.getInst().getString("nonce").toString();
			// 从请求中读取整个post数据
			InputStream inputStream = request.getInputStream();
			String postData = IOUtil.getString(inputStream, "UTF-8");
			// 解密消息
			String msg = wxcpt.DecryptMsg(sVerifyMsgSig, sVerifyTimeStamp, sVerifyNonce, postData);
			// xml请求解析
			Map<String, String> requestMap = WeixinMessageUtil.parseXml(msg);
			String infoType = requestMap.get("InfoType");
			if ("suite_ticket".equals(infoType)) {// 若为套件ticket信息
				// 将信息放入缓存中
				LOGGER.debug("移动办公套件ticket：" + requestMap);
				SystemCacheUtil.getInstance().getDefaultCache().add("suite_oa_ticket", requestMap);
			} else if ("change_auth".equals(infoType)) { // 变更授权
				LOGGER.debug("变更授权：corpid=" + requestMap.get("AuthCorpId") + "-------SuiteId="
						+ requestMap.get("SuiteId"));
				// 修改数据库数据，增加或修改应用关联
			} else if ("cancel_auth".equals(infoType)) { // 取消授权
				LOGGER.debug("取消授权：corpid=" + requestMap.get("AuthCorpId") + "-------SuiteId="
						+ requestMap.get("SuiteId"));
				// 修改数据库数据，取消用户应用关联
			}
			// 响应消息
			PrintWriter out = response.getWriter();
			out.print("success");
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
