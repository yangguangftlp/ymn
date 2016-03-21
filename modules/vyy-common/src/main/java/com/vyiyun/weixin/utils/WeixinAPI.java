package com.vyiyun.weixin.utils;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.cache.ICache;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.entity.SystemConfig;
import com.vyiyun.weixin.enums.WeixinDepartStatus;
import com.vyiyun.weixin.enums.WeixinMsgType;
import com.vyiyun.weixin.model.WeixinUser;
import com.vyiyun.weixin.model.wxmessage.WeixinResponseMessage;
import com.vyiyun.weixin.utils.wxutil.Sign;

/**
 * @ClassName: WeixinUtil
 * @Description: 微信相关辅助类
 * @author CCLIU
 * @date 2015-6-26 下午3:04:45 v1.0
 */
public class WeixinAPI {

	private static final Logger LOGGER = Logger.getLogger(WeixinAPI.class);

	/**
	 * 获取AccessToken
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getAccessToken() throws Exception {
		Object current = SystemCacheUtil.getInstance().getDefaultCache().get("access_token");// 取出缓存
		// 若未缓存或缓存中的token还有5分钟过期，则重新获取token
		if (null == current
				|| ((((JSONObject) current).getDate("time").getTime() + 7200000) - new Date().getTime()) <= 300000) {
			// 取得配置项
			String corpId = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "corpId");
			String secret = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "secret");
			String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "access_token_url");
			// 获取token
			JSONObject jo = HttpsUtil.post(url, "corpid=" + corpId + "&corpsecret=" + secret);
			/*
			 * jo数据结构示例： { "access_token": "accesstoken000001", "expires_in":
			 * 7200 }
			 */
			// 加入缓存
			jo.put("time", new Date());// 设置缓存时间
			SystemCacheUtil.getInstance().getDefaultCache().add("access_token", jo);
			return null == jo ? null : jo.get("access_token").toString();
		} else { // 否则，直接从缓存中取出返回
			return ((JSONObject) current).get("access_token").toString();
		}
	}

	/**
	 * 根据企业corpid获取AccessToken
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getAccessToken(String corpId) throws Exception {
		Object current = SystemCacheUtil.getInstance().getDefaultCache().get(corpId + "_access_token");// 取出缓存
		// 若未缓存或缓存中的token还有5分钟过期，则重新获取token
		if (null == current
				|| ((((JSONObject) current).getDate("time").getTime() + 7200000) - new Date().getTime()) <= 300000) {
			// 取得配置项
			// https://qyapi.weixin.qq.com/cgi-bin/service/get_corp_token?suite_access_token=SUITE_ACCESS_TOKEN
			// String corpId = ConfigUtil.get(Constants.WEIXIN_APP_PATH,
			// "corpId");
			String secret = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "secret");
			String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "access_token_url");
			// 获取token
			JSONObject jo = HttpsUtil.post(url, "corpid=" + corpId + "&corpsecret=" + secret);
			/*
			 * jo数据结构示例： { "access_token": "accesstoken000001", "expires_in":
			 * 7200 }
			 */
			// 加入缓存
			jo.put("time", new Date());// 设置缓存时间
			SystemCacheUtil.getInstance().getDefaultCache().add("access_token", jo);
			return null == jo ? null : jo.get("access_token").toString();
		} else { // 否则，直接从缓存中取出返回
			return ((JSONObject) current).get("access_token").toString();
		}
	}

	/**
	 * 获取jsapi_ticket，用于JS请求
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getJsapiTicket() throws Exception {
		Object current = SystemCacheUtil.getInstance().getDefaultCache().get("jsapi_ticket");// 取出缓存
		// 若未缓存或缓存中的ticket还有5分钟过期，则重新获取ticket
		if (null == current
				|| ((((JSONObject) current).getDate("time").getTime() + 7200000) - new Date().getTime()) <= 300000) {
			// 取得配置项
			String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "jsapi_ticket_url");
			url += "?access_token=" + getAccessToken();

			LOGGER.debug("getJsapiTicket url is:" + url);
			// 获取token
			JSONObject jo = HttpsUtil.get(url, "");
			LOGGER.debug("getJsapiTicket data is:" + jo);
			/*
			 * jo数据结构示例：{ "errcode":0, "errmsg":"ok", "ticket":
			 * "bxLdikRXVbTPdHSM05e5u5sUoXNKd8-41ZO3MhKoyN5OfkWITDGgnr2fwJ0m9E8NYzWKVZvdVtaUgWvsdshFKA"
			 * , "expires_in":7200 }
			 */
			// 加入缓存
			jo.put("time", new Date());// 设置缓存时间
			SystemCacheUtil.getInstance().getDefaultCache().add("jsapi_ticket", jo);
			return null == jo ? null : jo.get("ticket").toString();
		} else { // 否则，直接从缓存中取出返回
			return ((JSONObject) current).get("ticket").toString();
		}
	}

	/**
	 * 获取签名，用于JS请求（签名要给每个页面生成，不可以缓存）
	 * 
	 * @param request
	 *            ，用于生产签名的请求页面
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getSignature(String url, String params) throws Exception {
		// 取得配置项
		String corpId = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "corpId");
		// 生成ticket
		String jsapiTicket = getJsapiTicket();
		// 获取当前URL
		url = (null == params) ? url : url.concat("?").concat(params);
		url = url.split("#")[0];// 截取掉#后面的内容
		// 生成签名
		JSONObject jo = Sign.sign(jsapiTicket, url);
		// 增加内容
		jo.put("appId", corpId);
		jo.put("time", new Date());
		return jo;
	}

	/**
	 * 获取微信服务器的ip段
	 * 
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getWeixinIpList() throws Exception {
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "weixin_iplist_url");
		String accessToken = getAccessToken();
		return HttpsUtil.post(url, "access_token=" + accessToken);
	}

	/**
	 * 获取当前用户信息
	 * 
	 * @param code
	 *            ，从微信企业号跳转过来时的code参数
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getCurrentUser(String code) throws Exception {
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "user_info_url");
		String accessToken = getAccessToken();
		return HttpsUtil.post(url, "access_token=" + accessToken + "&code=" + code);
	}

	/**
	 * 获取用户详细信息
	 * 
	 * @param 用户ID
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getUserDetail(String userId) throws Exception {
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "user_detail_url");
		String accessToken = getAccessToken();
		return HttpsUtil.post(url, "access_token=" + accessToken + "&userid=" + userId);
	}

	/**
	 * 创建微信用户(针对公司外部人员，内部人员不在此创建)
	 * 
	 * @param name
	 *            人员姓名
	 * @param mobile
	 *            人员联系方式
	 * @param email
	 *            人员邮箱
	 * @return
	 * @throws Exception
	 */
	public static JSONObject createUser(final String userId, String name, String mobile, String email) throws Exception {
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "user_create_url");
		// 设置默认用户信息
		// final String userId = CommonUtil.GeneGUID();
		String avatarId = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "default_avatar_id");
		String departId = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "default_department_id");

		// 构造请求对象
		JSONObject jo = new JSONObject();
		jo.put("userid", userId);
		jo.put("name", name);
		jo.put("department", departId);
		if (!StringUtils.isEmpty(mobile))
			jo.put("mobile", mobile);
		if (!StringUtils.isEmpty(email))
			jo.put("email", email);
		jo.put("avatar_mediaid", avatarId);

		// 发送请求
		String accessToken = getAccessToken();
		url += "?access_token=" + accessToken;
		JSONObject result = HttpsUtil.post(url, jo);

		// 若用户添加成功，则发送关注邀请
		if (result != null && result.getString("errcode").equals("0")) {
			LOGGER.info(new Date().toString().concat("向用户" + name + "发送关注邀请"));
			// 创建一个延时任务
			new java.util.Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					try {
						System.out.println(inviteUser(userId));
					} catch (Exception e) {
						LOGGER.error("消息推送失败", e);
					}
					this.cancel();
				}
			}, 100);
		}

		return result;
	}

	/**
	 * 邀请成员关注企业号
	 * 
	 * @param 用户ID
	 * @return
	 * @throws Exception
	 */
	public static JSONObject inviteUser(String userId) throws Exception {
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "user_invite_url");
		String accessToken = getAccessToken();
		// 发送请求
		url += "?access_token=" + accessToken;
		JSONObject jo = new JSONObject();
		jo.put("userid", userId);
		return HttpsUtil.post(url, jo);
	}

	/**
	 * 删除用户
	 * 
	 * @param 用户ID
	 * @return
	 * @throws Exception
	 */
	public static JSONObject deleteUser(String userId) throws Exception {
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "user_delete_url");
		String accessToken = getAccessToken();
		// 发送请求
		return HttpsUtil.post(url, "?access_token=" + accessToken + "&userid=" + userId);
	}

	/**
	 * 新增用户
	 * 
	 * @param 用户ID
	 * @return
	 * @throws Exception
	 */
	public static JSONObject addUser(WeixinUser wxUser) throws Exception {
		StringBuffer url = new StringBuffer();
		url.append(ConfigUtil.get(Constants.WEIXIN_APP_PATH, "user_create_url"));
		url.append("?access_token=").append(getAccessToken());
		// 发送请求
		return HttpsUtil.post(url.toString(), JSON.toJSONString(wxUser));
	}

	/**
	 * 获取部门列表
	 * 
	 * @param depId
	 *            ，部门id，可以为null
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getDepartments(String depId) throws Exception {
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "department_list_url");
		String accessToken = getAccessToken();
		String params = "access_token=" + accessToken + "&id=" + depId;
		return HttpsUtil.post(url, params);
	}

	/**
	 * 获取部门用户简略信息
	 * 
	 * @param depId
	 *            ，部门id
	 * @param fetchChild
	 *            ，1/0，是否递归获取子部门下面的成员
	 * @param statuses
	 *            ，0获取全部成员，1获取已关注成员列表，2获取禁用成员列表，4获取未关注成员列表。statuses可填写多个
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getDepartUsers(String depId, int fetchChild, WeixinDepartStatus... statuses)
			throws Exception {
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "department_users_url");
		String accessToken = getAccessToken();
		int status = 0;
		for (WeixinDepartStatus s : statuses) {
			status += s.index();
		}
		String params = "access_token=" + accessToken + "&department_id=" + depId + "&fetch_child=" + fetchChild
				+ "&status=" + status;
		return HttpsUtil.post(url, params);
	}

	/**
	 * 获取部门用户详细信息
	 * 
	 * @param depId
	 *            ，部门id
	 * @param fetchChild
	 *            ，1/0，是否递归获取子部门下面的成员
	 * @param statuses
	 *            ，0获取全部成员，1获取已关注成员列表，2获取禁用成员列表，4获取未关注成员列表。statuses可填写多个
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getDepartUsersDetail(String depId, int fetchChild, WeixinDepartStatus... statuses)
			throws Exception {
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "department_users_detail_url");
		String accessToken = getAccessToken();
		int status = 0;
		for (WeixinDepartStatus s : statuses) {
			status += s.index();
		}
		String params = "access_token=" + accessToken + "&department_id=" + depId + "&fetch_child=" + fetchChild
				+ "&status=" + status;
		return HttpsUtil.post(url, params);
	}

	/**
	 * 获取标签列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getWTag() throws Exception {
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "tag_list_url");
		String accessToken = getAccessToken();
		return HttpsUtil.get(url, "access_token=" + accessToken);
	}

	/**
	 * 获取标签列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getWTagChild(String tagId) throws Exception {
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "tag_get_url");
		String accessToken = getAccessToken();
		return HttpsUtil.get(url, "access_token=" + accessToken + "&tagid=" + tagId);
	}

	/**
	 * TODO 获取永久素材(存在问题)
	 * 
	 * @param agentId
	 *            ，应用ID
	 * @param mediaId
	 *            ，媒体ID
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getMaterial(String agentId, String mediaId) throws Exception {
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "material_get_url");
		String accessToken = getAccessToken();
		String params = "access_token=" + accessToken + "&media_id=" + mediaId + "&agentid=" + agentId;
		return HttpsUtil.get(url, params);
	}

	/**
	 * TODO 获取永久素材(存在问题)
	 * 
	 * @param agentId
	 *            ，应用ID
	 * @param mediaId
	 *            ，媒体ID
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getMedia(String mediaId) throws Exception {
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "media_get_url");
		String accessToken = getAccessToken();
		String params = "access_token=" + accessToken + "&media_id=" + mediaId;
		return HttpsUtil.downMediaResources(url, params);
	}

	/**
	 * 获取应用详细内容
	 * 
	 * @param agentId
	 *            ，应用ID
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getAppDetail(String agentId) throws Exception {
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "app_detail_url");
		String accessToken = getAccessToken();
		String params = "access_token=" + accessToken + "&agentid=" + agentId;
		return HttpsUtil.post(url, params);
	}

	/**
	 * 设置应用详细内容
	 * 
	 * @param agentId
	 *            ，应用ID
	 * @return
	 * @throws Exception
	 */
	public static JSONObject setAppDetail(String accessToken, int agentId, int reportLocationFlag, String logoMediaId,
			String name, String description, String redirectDomain, int isreportuser, int isreportenter)
			throws Exception {
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "app_set_url");
		url += "?access_token=" + accessToken;
		// 构造应用信息
		JSONObject agent = new JSONObject();
		agent.put("agentid", agentId);
		agent.put("report_location_flag", reportLocationFlag);
		agent.put("logo_mediaid", logoMediaId);
		agent.put("name", name);
		agent.put("description", description);
		agent.put("redirect_domain", redirectDomain);
		agent.put("isreportuser", isreportuser);
		agent.put("isreportenter", isreportenter);
		return HttpsUtil.post(url, agent);
	}

	/**
	 * 获取管理组内的所有应用信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getAppList() throws Exception {
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "app_list_url");
		String accessToken = getAccessToken();
		String params = "access_token=" + accessToken;
		return HttpsUtil.post(url, params);
	}

	/**
	 * userid转换成openid接口
	 * 
	 * 该接口使用场景为微信支付、微信红包和企业转账，企业号用户在使用微信支付的功能时， 需要自行将企业号的userid转成openid。
	 * 在使用微信红包功能时，需要将应用id和userid转成appid和openid才能使用。
	 * 
	 * @param userId
	 * @param agentId
	 *            ，需要发送红包的应用ID，若只是使用微信支付和企业转账，则无需该参数
	 * @return { "errcode": 0, "errmsg": "ok", "openid":
	 *         "oDOGms-6yCnGrRovBj2yHij5JL6E", "appid":"wxf874e15f78cc84a7" }
	 * @throws Exception
	 */
	public static JSONObject convertToOpenid(String userId, String agentId) throws Exception {
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "convert_to_openid_url");
		String accessToken = getAccessToken();
		url += "?access_token=" + accessToken;
		JSONObject jo = new JSONObject();
		jo.put("userid", userId);
		jo.put("agentid", agentId);
		return HttpsUtil.post(url, jo);
	}

	/**
	 * openid转换成userid接口
	 * 
	 * 该接口主要应用于使用微信支付、微信红包和企业转账之后的结果查询， 开发者需要知道某个结果事件的openid对应企业号内成员的信息时，
	 * 可以通过调用该接口进行转换查询。
	 * 
	 * @param openId
	 *            ，在使用微信支付、微信红包和企业转账之后，返回结果的openid
	 * @return { "errcode": 0, "errmsg": "ok", "userid": "zhangsan" }
	 * @throws Exception
	 */
	public static JSONObject convertToUserid(String openId) throws Exception {
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "convert_to_userid_url");
		String accessToken = getAccessToken();
		url += "?access_token=" + accessToken;
		JSONObject jo = new JSONObject();
		jo.put("openid", openId);
		return HttpsUtil.post(url, jo);
	}

	/**
	 * 发送消息
	 * 
	 * @param msgBody
	 * @return
	 * @throws Exception
	 */
	public static JSONObject sendMessage(String msgBody) throws Exception {
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "send_message_url");
		String accessToken = getAccessToken();
		url += "?access_token=" + accessToken;
		LOGGER.debug("send message url is " + url + ",msgBody=" + msgBody);
		JSONObject responseData = HttpsUtil.post(url, msgBody);
		LOGGER.debug("get message response is " + responseData.toJSONString());
		return responseData;
	}

	/**
	 * 发送消息
	 * 
	 * @param accessToken
	 *            ，企业号访问令牌
	 * @param msgBody
	 *            ，消息体
	 * @return
	 * @throws Exception
	 */
	public static JSONObject sendMessageWithToken(String accessToken, String msgBody) throws Exception {
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "send_message_url");
		url += "?access_token=" + accessToken;
		LOGGER.debug("send message url is " + url + ",msgBody=" + msgBody);
		return HttpsUtil.post(url, msgBody);
	}

	/**
	 * TODO 微信消息处理
	 * 
	 * @param msg
	 * @return
	 */
	public static String messageHandle(String msg) {
		String respMessage = null;
		try {
			// xml请求解析
			Map<String, String> requestMap = WeixinMessageUtil.parseXml(msg);
			String fromUserName = requestMap.get("FromUserName");// 发送方帐号
			String toUserName = requestMap.get("ToUserName");// 接收方帐号，即企业号CorpID
			String agentId = requestMap.get("AgentID");// 应用ID
			String msgType = requestMap.get("MsgType");// 消息类型

			// 文本消息
			if (msgType.equals(WeixinMessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
			}
			// 图片消息
			else if (msgType.equals(WeixinMessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
			}
			// 地理位置消息
			else if (msgType.equals(WeixinMessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
			}
			// 链接消息
			else if (msgType.equals(WeixinMessageUtil.REQ_MESSAGE_TYPE_LINK)) {
			}
			// 音频消息
			else if (msgType.equals(WeixinMessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
			}
			// 事件推送
			else if (msgType.equals(WeixinMessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
				// 事件类型
				String eventType = requestMap.get("Event");
				// 若为进入应用事件，则获取用户信息，并放至session
				if (eventType.equalsIgnoreCase(WeixinMessageUtil.EVENT_AGENT)) {
					// TODO 存放用户
				}
				// 自定义菜单点击事件
				if (eventType.equalsIgnoreCase(WeixinMessageUtil.EVENT_TYPE_CLICK)) {
					// 事件KEY值，与创建自定义菜单时指定的KEY值对应
					String eventKey = requestMap.get("EventKey");
					System.out.println(eventKey);
				}
			}
			respMessage = new WeixinResponseMessage(fromUserName, toUserName, WeixinMsgType.text, "事件处理").toXml();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
			respMessage = "有异常了。。。";
		}
		LOGGER.debug("respMessage=" + respMessage);
		return respMessage;
	}

	/**
	 * 创建菜单
	 * 
	 * @param token
	 *            ，企业号access_token
	 * @param agentId
	 *            ，企业号内应用ID
	 * @param params
	 *            ，菜单参数
	 * @return
	 * @throws Exception
	 */
	public static boolean createMenu(String token, int agentId, JSONObject params) throws Exception {
		// 取得配置项
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "menu_create_url");
		url += "?access_token=" + token + "&agentid=" + agentId;
		// 执行菜单创建
		JSONObject jo = HttpsUtil.post(url, params);
		return (null != jo & jo.getString("errcode").equals("0"));
	}

	/**
	 * 删除菜单
	 * 
	 * @param token
	 *            ，企业号access_token
	 * @param agentId
	 *            ，企业号内应用ID
	 * @return
	 * @throws Exception
	 */
	public static boolean deleteMenu(String token, int agentId) throws Exception {
		// 取得配置项
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "menu_delete_url");
		url += "?access_token=" + token + "&agentid=" + agentId;
		// 执行菜单创建
		JSONObject jo = HttpsUtil.post(url, "");
		return (null != jo & jo.getString("errcode").equals("0"));
	}

	/**
	 * 获取菜单列表
	 * 
	 * @param token
	 *            ，企业号access_token
	 * @param agentId
	 *            ，企业号内应用ID
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getMenu(String token, int agentId) throws Exception {
		// 取得配置项
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "menu_get_url");
		url += "?access_token=" + token + "&agentid=" + agentId;
		// 执行菜单创建
		JSONObject jo = HttpsUtil.post(url, "");
		return jo;
	}

	/**
	 * 根据经纬度获取地理位置，使用腾讯地图API
	 * 
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getLocation(String lat, String lng) throws Exception {
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "location_url");
		String ak = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "location_api_ak");
		StringBuffer urlStr = new StringBuffer();
		urlStr.append(url)
				.append("?key=")
				.append(ak)
				.append("&location=")
				.append(lat)
				.append(',')
				.append(lng)
				.append("&coord_type=1&get_poi=1&parameter={%22scene_type%22:%22tohome%22,%22poi_num%22:10}&&output=json");
		LOGGER.debug("获取地理位置url :" + urlStr.toString());
		return JSON.parseObject(HttpsUtil.sendGet(urlStr.toString(), ""));
	}

	/**
	 * 将坐标转换成google 坐标
	 * 
	 * @param lat
	 * @param lng
	 * @return
	 */
	public static JSONObject gpsToGoogle(String lat, String lng) {
		JSONObject returnJson = new JSONObject();
		String gLat = null;
		String gLng = null;
		LOGGER.debug("将微信获取的地理位置google 坐标...lat【" + lat + "】lng=【" + lng + "】");
		try {
			String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "conversion_gps");
			url += "?lat=" + lat + "&lng=" + lng;
			LOGGER.debug("url is  =" + url);
			JSONObject jsonObject = JSON.parseObject(HttpsUtil.sendGet(url, ""));
			if (!CollectionUtils.isEmpty(jsonObject)) {
				JSONObject jsonGoogle = jsonObject.getJSONObject("google");
				gLat = jsonGoogle.getString("lat");
				gLng = jsonGoogle.getString("lng");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("微信gps位置转换出现异常!", e);
		}
		// 如果后续gps转换 api出现问题 保证原来逻辑正常
		if (StringUtils.isNotEmpty(gLat) && StringUtils.isNotEmpty(gLng)) {
			returnJson.put("lat", gLat);
			returnJson.put("lng", gLng);
			LOGGER.debug("将微信获取的地理位置google 坐标成功...gLat【" + gLat + "】gLat=【" + gLng + "】");
		} else {
			returnJson.put("lat", lat);
			returnJson.put("lng", lng);
			LOGGER.debug("将微信获取的地理位置google 坐标失败...");
		}
		return returnJson;
	}

	/******* region 微信服务商相关接口 begin *********/

	/**
	 * 获取服务商 AccessToken
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getProviderAccessToken() throws Exception {
		Object current = SystemCacheUtil.getInstance().getDefaultCache().get("provider_access_token");// 取出缓存
		// 若未缓存或缓存中的token还有5分钟过期，则重新获取token
		if (null == current
				|| ((((JSONObject) current).getDate("time").getTime() + 7200000) - new Date().getTime()) <= 300000) {
			// 取得配置项
			String corpId = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "corpId");
			String secret = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "provider_secret");
			String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "suite_provider_token_url");

			JSONObject params = new JSONObject();
			params.put("corpid", corpId);
			params.put("provider_secret", secret);

			// 获取token
			JSONObject jo = HttpsUtil.post(url, params);
			// 加入缓存
			jo.put("time", new Date());// 设置缓存时间
			SystemCacheUtil.getInstance().getDefaultCache().add("provider_access_token", jo);
			return null == jo ? null : jo.get("provider_access_token").toString();
		} else { // 否则，直接从缓存中取出返回
			return ((JSONObject) current).get("provider_access_token").toString();
		}
	}

	/**
	 * 获取授权的管理员的信息
	 * 
	 * @param authCode
	 *            ，oauth2.0授权企业号管理员登录产生的code
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getAuthManagerInfo(String authCode) throws Exception {
		// 服务商TOKEN
		String providerAccessToken = getProviderAccessToken();
		// 取得配置项
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "suite_login_info_url");
		url += providerAccessToken;
		JSONObject params = new JSONObject();
		params.put("auth_code", authCode);
		// 获取token
		JSONObject jo = HttpsUtil.post(url, params);
		return jo;
	}

	/**
	 * 获取应用套件令牌
	 * 
	 * @param suiteId
	 *            ，套件ID
	 * @return
	 * @throws Exception
	 */
	public static String getSuiteToken(String suiteId) throws Exception {
		String tokenName = suiteId + "_suite_token";
		Object current = SystemCacheUtil.getInstance().get(tokenName);// 取出缓存
		// 若未缓存或缓存中的token还有5分钟过期，则重新获取token
		if (null == current
				|| ((((JSONObject) current).getDate("time").getTime() + 7200000) - new Date().getTime()) <= 300000) {
			// 取得配置项
			String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "suite_token_url");
			JSONObject params = geneSuiteParams(suiteId);
			// 获取token
			JSONObject jo = HttpsUtil.post(url, params);
			// 加入缓存
			jo.put("time", new Date());// 设置缓存时间
			SystemCacheUtil.getInstance().getDefaultCache().add(tokenName, jo);
			LOGGER.debug(tokenName + "=====" + jo);
			return null == jo ? null : jo.get("suite_access_token").toString();
		} else { // 否则，直接从缓存中取出返回
			return ((JSONObject) current).get("suite_access_token").toString();
		}
	}

	/**
	 * 获取预授权码
	 * 
	 * @param suiteId
	 *            ，套件ID
	 * @param appId
	 *            ，应用ID数组
	 * @return
	 * @throws Exception
	 */
	public static String getPreAuthCode(String suiteId, int... appId) throws Exception {
		String tokenName = suiteId + "_pre_auth_code";
		Object current = SystemCacheUtil.getInstance().get(tokenName);// 取出缓存
		// 若未缓存或缓存中的token还有1分钟过期，则重新获取token
		if (null == current
				|| ((((JSONObject) current).getDate("time").getTime() + 1200000) - new Date().getTime()) <= 60000) {
			// 取得配置项
			String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "suite_preauthcode_url");
			url += getSuiteToken(suiteId);
			JSONObject params = new JSONObject();
			params.put("suite_id", suiteId);
			if (null != appId)
				params.put("appid", appId);
			System.out.println("getPreAuthCode params: " + params);
			// 获取token
			JSONObject jo = HttpsUtil.post(url, params);
			// 加入缓存
			jo.put("time", new Date());// 设置缓存时间
			System.out.println("getPreAuthCode jo: " + jo);
			SystemCacheUtil.getInstance().getDefaultCache().add(tokenName, jo);
			return null == jo ? null : jo.get("pre_auth_code").toString();
		} else { // 否则，直接从缓存中取出返回
			return ((JSONObject) current).get("pre_auth_code").toString();
		}
	}

	/**
	 * 设置授权配置
	 * 
	 * @param suiteId
	 *            ，套件ID
	 * @param appId
	 *            ，应用ID数组
	 * @return
	 * @throws Exception
	 */
	public static JSONObject setSessionInfo(String suiteId, int... appId) throws Exception {
		// 取套件预授权码
		String preAuthCode = getPreAuthCode(suiteId, appId);
		// 取得配置项
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "suite_session_url");
		url += getSuiteToken(suiteId);
		JSONObject params = new JSONObject();
		params.put("pre_auth_code", preAuthCode);
		if (null != appId) {
			JSONObject jo = new JSONObject();
			jo.put("appid", appId);
			params.put("session_info", jo);
		}
		// 获取token
		JSONObject jo = HttpsUtil.post(url, params);
		return jo;
	}

	/**
	 * 获取企业号的永久授权码
	 * 
	 * @param suiteId
	 *            ，套件ID
	 * @param preAuthCode
	 *            ，临时授权码，会在套件授权成功后附加在redirect_uri中跳转回应用提供商网站
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getPermanentCode(String suiteId, String preAuthCode) throws Exception {
		// 取得配置项
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "suite_permanentcode_url");
		url += getSuiteToken(suiteId);
		JSONObject params = new JSONObject();
		params.put("suite_id", suiteId);
		params.put("auth_code", preAuthCode);
		// 获取token
		JSONObject jo = HttpsUtil.post(url, params);
		return jo;
	}

	/**
	 * 获取企业号的授权信息
	 * 
	 * @param suiteId
	 *            ，套件ID
	 * @param authCorpid
	 *            ，授权企业号ID
	 * @param permanentCode
	 *            ，授权企业号在套件下的永久授权码
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getAuthInfo(String suiteId, String authCorpid, String permanentCode) throws Exception {
		// 取得配置项
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "suite_authinfo_url");
		url += getSuiteToken(suiteId);
		JSONObject params = new JSONObject();
		params.put("suite_id", suiteId);
		params.put("auth_corpid", authCorpid);
		params.put("permanent_code", permanentCode);
		// 获取token
		JSONObject jo = HttpsUtil.post(url, params);
		return jo;
	}

	/**
	 * 获取企业号应用信息
	 * 
	 * @param suiteId
	 *            ，套件ID
	 * @param authCorpid
	 *            ，授权企业号ID
	 * @param permanentCode
	 *            ，授权企业号在套件下的永久授权码
	 * @param agentId
	 *            ，应用ID
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getAgentInfo(String suiteId, String authCorpid, String permanentCode, int agentId)
			throws Exception {
		// 取得配置项
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "suite_agent_get_url");
		url += getSuiteToken(suiteId);
		JSONObject params = new JSONObject();
		params.put("suite_id", suiteId);
		params.put("auth_corpid", authCorpid);
		params.put("permanent_code", permanentCode);
		params.put("agentid ", agentId);
		// 获取token
		JSONObject jo = HttpsUtil.post(url, params);
		return jo;
	}

	/**
	 * 设置企业号应用信息
	 * 
	 * @param suiteId
	 *            ，套件ID
	 * @param authCorpid
	 *            ，授权企业号ID
	 * @param permanentCode
	 *            ，授权企业号在套件下的永久授权码
	 * @param agentId
	 *            ，授权企业应用的ID
	 * @param reportLocationFlag
	 *            ，企业应用是否打开地理位置上报 0：不上报；1：进入会话上报；2：持续上报
	 * @param logoMediaId
	 *            ，企业应用头像的mediaid，通过多媒体接口上传图片获得mediaid，上传后会自动裁剪成方形和圆形两个头像
	 * @param name
	 *            ，企业应用名称
	 * @param description
	 *            ，企业应用详情
	 * @param redirectDomain
	 *            ，企业应用可信域名
	 * @param isreportuser
	 *            ，是否接收用户变更通知。0：不接收；1：接收
	 * @param isreportenter
	 *            ，是否上报用户进入应用事件。0：不接收；1：接收
	 * @return
	 * @throws Exception
	 */
	public static JSONObject setAgentInfo(String suiteId, String authCorpid, String permanentCode, int agentId,
			int reportLocationFlag, String logoMediaId, String name, String description, String redirectDomain,
			int isreportuser, int isreportenter) throws Exception {
		// 取得配置项
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "suite_agent_set_url");
		url += getSuiteToken(suiteId);
		// 构造参数
		JSONObject params = new JSONObject();
		params.put("suite_id", suiteId);
		params.put("auth_corpid", authCorpid);
		params.put("permanent_code", permanentCode);
		// 构造应用信息
		JSONObject agent = new JSONObject();
		agent.put("agentid", agentId);
		agent.put("report_location_flag", reportLocationFlag);
		agent.put("logo_mediaid", logoMediaId);
		agent.put("name", name);
		agent.put("description", description);
		agent.put("redirect_domain", redirectDomain);
		agent.put("isreportuser", isreportuser);
		agent.put("isreportenter", isreportenter);
		params.put("agent ", agent);
		System.out.println("setAgentInfo params ===" + params);
		// 获取token
		JSONObject jo = HttpsUtil.post(url, params);
		return jo;
	}

	/**
	 * 获取企业号access_token
	 * 
	 * @param suiteId
	 *            ，套件ID
	 * @param authCorpid
	 *            ，授权企业号ID
	 * @param permanentCode
	 *            ，授权企业号在套件下的永久授权码
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getCorpToken(String suiteId, String authCorpid, String permanentCode) throws Exception {
		// 取得配置项
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "suite_corp_token_url");
		url += getSuiteToken(suiteId);
		JSONObject params = new JSONObject();
		params.put("suite_id", suiteId);
		params.put("auth_corpid", authCorpid);
		params.put("permanent_code", permanentCode);
		// 获取token
		JSONObject jo = HttpsUtil.post(url, params);
		return jo;
	}

	/**
	 * 组织参数
	 * 
	 * @param suiteType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static JSONObject geneSuiteParams(String suiteId) {
		JSONObject params = new JSONObject();
		ICache<Object> cache = SystemCacheUtil.getInstance().getDefaultCache();
		// 取出配置项
		String life = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "suite_life_id");
		String entm = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "suite_entertainment_id");
		String info = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "suite_information_id");
		String oa = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "suite_oa_id");

		// 构造参数
		params.put("suite_id", suiteId);
		if (life.equals(suiteId)) {
			params.put("suite_secret", ConfigUtil.get(Constants.WEIXIN_APP_PATH, "suite_life_secret"));
			if (null != cache && null != cache.get("suite_life_ticket"))
				params.put("suite_ticket", ((Map<String, String>) (cache.get("suite_life_ticket"))).get("SuiteTicket"));
		}
		if (entm.equals(suiteId)) {
			params.put("suite_secret", ConfigUtil.get(Constants.WEIXIN_APP_PATH, "suite_entertainment_secret"));
			if (null != cache && null != cache.get("suite_entertainment_ticket"))
				params.put("suite_ticket",
						((Map<String, String>) (cache.get("suite_entertainment_ticket"))).get("SuiteTicket"));
		}
		if (info.equals(suiteId)) {
			params.put("suite_secret", ConfigUtil.get(Constants.WEIXIN_APP_PATH, "suite_information_secret"));
			if (null != cache && null != cache.get("suite_information_ticket"))
				params.put("suite_ticket",
						((Map<String, String>) (cache.get("suite_information_ticket"))).get("SuiteTicket"));
		}
		if (oa.equals(suiteId)) {
			params.put("suite_secret", ConfigUtil.get(Constants.WEIXIN_APP_PATH, "suite_oa_secret"));
			if (null != cache && null != cache.get("suite_oa_ticket"))
				params.put("suite_ticket", ((Map<String, String>) (cache.get("suite_oa_ticket"))).get("SuiteTicket"));
		}
		return params;
	}

	/**
	 * 获取微信部门
	 * 
	 * @param object
	 * @return
	 */
	public static JSONObject getWeixinDepts(String depId) throws Exception {
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "department_list_url");
		String accessToken = getAccessToken();
		String params = "access_token=" + accessToken + "&id=" + depId;
		return HttpsUtil.post(url, params);
	}

	/**
	 * 通过code获取当前微信登录用户信息
	 * 
	 * @param code
	 * @return
	 */
	public static JSONObject getWCurrentUser(String code) throws Exception {
		String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "user_info_url");
		String corpId = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "corpId");
		String accessToken = getAccessToken(corpId);
		return HttpsUtil.post(url, "access_token=" + accessToken + "&code=" + code);
	}

	/**
	 * 获取企业访问token
	 * 
	 * @param corpId
	 *            企业号
	 * @param suiteId
	 *            套件id
	 * @param permanentCode
	 *            永久授权码
	 * @throws Exception
	 */
	public static String getAccessToken(String corpId, String suiteId, String permanentCode) throws Exception {
		String key = corpId + "_access_token";
		ICache<Object> iCache = SystemCacheUtil.getInstance().getWeixinContactCache();

		Object current = iCache.get(key);// 取出缓存
		// 若未缓存或缓存中的token还有5分钟过期，则重新获取token
		if (null == current
				|| ((((JSONObject) current).getDate("time").getTime() + 7200000) - new Date().getTime()) <= 300000) {
			SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance().getSystemConfigCache()
					.get("system_getAccessToken");
			JSONObject responseResult = HttpsUtil.get(systemConfig.getValue(), "suiteId=" + suiteId);
			String url = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "suite_permanentcode_url")
					+ responseResult.getString("accessToken");
			String params = MessageFormat.format("auth_corpid=${0}&suite_id=${1}&permanent_code{2}", new Object[] {
					corpId, suiteId, permanentCode });
			// 获取token
			JSONObject jo = HttpsUtil.post(url, params);
			/*
			 * jo数据结构示例： { "access_token": "accesstoken000001", "expires_in":
			 * 7200 }
			 */
			if (null != jo) {
				// 加入缓存
				jo.put("time", new Date());// 设置缓存时间
				iCache.add(key, jo);
				if (jo.containsKey("access_token")) {
					return jo.get("access_token").toString();
				}
			}
			return null;
		} else { // 否则，直接从缓存中取出返回
			return ((JSONObject) current).get("access_token").toString();
		}
	}
	/******* region 微信服务商相关接口 end *********/

}
