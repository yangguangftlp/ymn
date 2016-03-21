package com.vyiyun.common.weixin.controller.mobile;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.entity.AttendanceRule;
import com.vyiyun.common.weixin.entity.Sign;
import com.vyiyun.common.weixin.entity.SignUser;
import com.vyiyun.common.weixin.enums.CommonAppType;
import com.vyiyun.common.weixin.service.IAttendanceRuleService;
import com.vyiyun.common.weixin.service.ISignService;
import com.vyiyun.common.weixin.service.ISignUserService;
import com.vyiyun.weixin.controller.AbstWebController;
import com.vyiyun.weixin.entity.Accessory;
import com.vyiyun.weixin.entity.EntityAccount;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.DataResult;
import com.vyiyun.weixin.model.WeixinUser;
import com.vyiyun.weixin.service.IAccessoryService;
import com.vyiyun.weixin.service.IEntityAccountService;
import com.vyiyun.weixin.utils.CommonUtil;
import com.vyiyun.weixin.utils.DateUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.SpringContextHolder;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyiyun.weixin.web.model.ResponseResult;
import com.vyy.weixin.annotation.App;
import com.vyy.weixin.annotation.OAuth;
import com.vyy.weixin.annotation.Suite;

/**
 * @ClassName: SignController
 * @Description: 签到相关请求处理器
 * @author CCLIU
 * @date 2015-6-11 上午11:05:22 v1.0
 */
@Controller
@RequestMapping(value = "/mobile/sign")
@SuppressWarnings("unchecked")
@Suite("1")
@OAuth
@App(id = "sign")
public class SignController extends AbstWebController {

	/**
	 * 考勤规则服务
	 */
	@Autowired
	private IAttendanceRuleService attendanceRuleService;

	@Autowired
	private ISignService signService;

	@Autowired
	private ISignUserService signUserService;

	@Autowired
	private IEntityAccountService entityAccountService;
	/**
	 * 附件处理
	 */
	@Autowired
	private IAccessoryService accessoryService;

	/**
	 * 发起签到页面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "launchSignView", method = RequestMethod.GET)
	// @OAuth
	public ModelAndView launchSignView(ModelMap model) {
		// a.deleteById("12345");
		ModelAndView modelAndView = super.createModelAndView("launch_sign");
		return modelAndView;
	}

	/**
	 * 发起签到
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "launchSign", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object launchSign(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		try {
			// 获取签到发布信息
			JSONObject jsonLaunchSignInfo = JSON.parseObject(URLDecoder.decode(
					HttpRequestUtil.getInst().getString("launchSignInfo"), "UTF-8"));
			String id = CommonUtil.GeneGUID();
			jsonLaunchSignInfo.put("id", id);
			responseResult.setValue(id);
			signService.launchSign(jsonLaunchSignInfo);
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 签到处，显示今天所有的签到信息
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "signView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView signView(HttpServletRequest request) {
		ModelAndView modelAndView = super.createModelAndView("sign");
		Sign sign = new Sign();
		sign.setSignType(CommonAppType.SignType.KQ.value());
		// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen start.
		sign.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
		// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen end.
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid());
		List<Map<String, Object>> signListMap = (List<Map<String, Object>>) signService.querySignRecord(sign, params,
				-1, -1).getData();
		modelAndView.addObject("signListMap", signListMap);
		return modelAndView;
	}

	/**
	 * 获取签到数据
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "getWaitSignRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getWaitSignRecord(HttpServletRequest request) {
		Sign sign = new Sign();
		// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen start.
		sign.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
		// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen end.
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid());
		params.put("isKq", true);
		return signService.querySignRecord(sign, params, -1, -1).getData();
	}

	/**
	 * 签到详情
	 * 
	 * @return
	 */
	@RequestMapping(value = "signDetailView", method = RequestMethod.GET)
	public ModelAndView signDetailView(HttpServletRequest request) {
		ModelAndView modelAndView = super.createModelAndViewWithSign("sign_detail", request);
		String id = HttpRequestUtil.getInst().getString("id");
		if (StringUtil.isEmpty(id)) {
			throw new VyiyunException(SpringContextHolder.getI18n("1002002", new String[] { "id" }, null));
		}
		// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen start.
		WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
		// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen end.

		// 需要获取签到信息
		Sign sign = new Sign();
		// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen start.
		sign.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
		// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen end.
		sign.setId(id);
		List<Map<String, Object>> signListMap = signService.getSign(sign);
		if (!CollectionUtils.isEmpty(signListMap)) {
			modelAndView.addObject("signInfo", signListMap.get(0));
		} else {
			if (CollectionUtils.isEmpty(signListMap)) {
				throw new VyiyunException(SpringContextHolder.getI18n("1002003", new String[] { "id", id }, null));
			}
		}
		
		Date currentDate = new Date();
		String signType = (String) signListMap.get(0).get("signType");
		Date beginTime = (Date) signListMap.get(0).get("beginTime");
		
		
		SignUser signUser = new SignUser();
		// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen start.
		sign.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
		// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen end.
		signUser.setSignId(id);
		// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen start.
		signUser.setUserId(weixinUser.getUserid());
		// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen end.
		List<Map<String, Object>> signUserList = signUserService.getSignUser(signUser);
		if (!CollectionUtils.isEmpty(signUserList)) {
			Map<String, Object> temp = null;
			for (int i = 0, size = signUserList.size(); i < size; i++) {
				temp = signUserList.get(i);
				// 签到信息
				if ("0".equals(temp.get("attendType"))) {
					modelAndView.addObject("sign_in", temp);
					// TODO 逻辑确认要
					List<Accessory> accessoryList = accessoryService.getAccessoryByEntityId(StringUtil.getString(temp
							.get("id")));
					if (!CollectionUtils.isEmpty(accessoryList)) {
						modelAndView.addObject("signInAccessoryInfor", accessoryList);
					}
				} else /** 签退信息 */
				{
					modelAndView.addObject("sign_out", temp);
					// TODO 逻辑确认要
					List<Accessory> accessoryList = accessoryService.getAccessoryByEntityId(StringUtil.getString(temp
							.get("id")));
					if (!CollectionUtils.isEmpty(accessoryList)) {
						modelAndView.addObject("signOutAccessoryInfor", accessoryList);
					}
				}
			}
		}
		
		if ("0".equals(signType) && signUserList.size() < 2) {
			// 判断如果不是当天的考勤不能正常进行
			if (!DateUtil.dateToString(currentDate, "yyyy-MM-dd")
					.equals(DateUtil.dateToString(beginTime, "yyyy-MM-dd"))) {
				throw new VyiyunException(SpringContextHolder.getI18n("1002009"));
			}
		}
		
		Date curentDate = new Date();
		modelAndView.addObject("date", DateUtil.dateToString(curentDate, "MM月dd日") + DateUtil.getDayOfWeek(curentDate));
		modelAndView.addObject("time", DateUtil.dateToString(curentDate, "HH:mm"));
		AttendanceRule attendanceRule = attendanceRuleService.getAttendanceRule(HttpRequestUtil.getInst().getCurrentCorpId());

		if (null != attendanceRule) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			int startHour = Integer.parseInt(attendanceRule.getStartHour());
			int endHour = Integer.parseInt(attendanceRule.getEndHour());
			int startMinute = Integer.parseInt(attendanceRule.getStartMinute());
			int endMinute = Integer.parseInt(attendanceRule.getEndMinute());
			int delayMinutes = Integer.parseInt(attendanceRule.getDelayMinutes());
			long startHours = 0;
			long endHours = 0;
			// 开始时间
			cal.set(Calendar.HOUR_OF_DAY, startHour);
			cal.set(Calendar.MINUTE, startMinute);
			cal.set(Calendar.SECOND, 0);
			startHours = cal.getTime().getTime();
			cal.set(Calendar.MINUTE, startMinute + delayMinutes);
			modelAndView.addObject("startHour", DateUtil.dateToString(cal.getTime(), "HH:mm"));

			// 结束时间
			cal.set(Calendar.HOUR_OF_DAY, endHour);
			cal.set(Calendar.MINUTE, endMinute);
			cal.set(Calendar.SECOND, 0);
			endHours = cal.getTime().getTime();
			cal.set(Calendar.MINUTE, endMinute - delayMinutes);
			modelAndView.addObject("endHour", DateUtil.dateToString(cal.getTime(), "HH:mm"));

			modelAndView.addObject("workHours", DateUtil.getTimeHour((endHours - startHours)));
		} else {
			throw new VyiyunException("当前考勤规则没有配置！");
		}
		return modelAndView;
	}

	/**
	 * 签到处理
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "doSign", method = { RequestMethod.POST })
	public @ResponseBody
	Object doSign() {
		ResponseResult responseResult = new ResponseResult();
		try {
			// 获取请假申请信息
			JSONObject jsonSignInfoObj = JSON.parseObject(URLDecoder.decode(
					HttpRequestUtil.getInst().getString("signInfo"), "UTF-8"));
			Map<String, Object> resultData = new HashMap<String, Object>();
			responseResult.setValue(resultData);
			if (null != jsonSignInfoObj) {
				String sSignTime = DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm");
				Date signTime = DateUtil.stringToDate(sSignTime + ":00");
				String id = CommonUtil.GeneGUID();
				jsonSignInfoObj.put("signTime", signTime);
				jsonSignInfoObj.put("id", id);
				resultData.put("id", id);
				resultData.put("signTime", DateUtil.dateToString(signTime, "HH:mm"));
			}
			signService.doSign(jsonSignInfoObj);
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 签到处理
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "doRemark", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object doRemark() {
		ResponseResult responseResult = new ResponseResult();
		// 获取请假申请信息
		try {
			JSONObject jsonRemarkObj = JSON.parseObject(URLDecoder.decode(
					HttpRequestUtil.getInst().getString("remarkInfo"), "UTF-8"));
			signService.doRemark(jsonRemarkObj);
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 根据月份查询当月统计
	 * 
	 * @return
	 */
	@RequestMapping(value = "signStatisticView", method = RequestMethod.GET)
	public ModelAndView signStatisticView() {
		ModelAndView modelAndView = super.createModelAndView("sign-statistic");
		modelAndView.addObject("currentMonth", DateUtil.dateToString(new Date(), "yyyy-MM"));
		AttendanceRule attendanceRule = attendanceRuleService.getAttendanceRule(HttpRequestUtil.getInst()
				.getCurrentCorpId());

		if (null != attendanceRule) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			int startHour = Integer.parseInt(attendanceRule.getStartHour());
			int endHour = Integer.parseInt(attendanceRule.getEndHour());
			int startMinute = Integer.parseInt(attendanceRule.getStartMinute());
			int endMinute = Integer.parseInt(attendanceRule.getEndMinute());
			int delayMinutes = Integer.parseInt(attendanceRule.getDelayMinutes());
			long startHours = 0;
			long endHours = 0;
			// 开始时间
			cal.set(Calendar.HOUR_OF_DAY, startHour);
			cal.set(Calendar.MINUTE, startMinute);
			cal.set(Calendar.SECOND, 0);
			startHours = cal.getTime().getTime();
			cal.set(Calendar.MINUTE, startMinute + delayMinutes);
			modelAndView.addObject("startHour", DateUtil.dateToString(cal.getTime(), "HH:mm"));

			// 结束时间
			cal.set(Calendar.HOUR_OF_DAY, endHour);
			cal.set(Calendar.MINUTE, endMinute);
			cal.set(Calendar.SECOND, 0);
			endHours = cal.getTime().getTime();
			cal.set(Calendar.MINUTE, endMinute - delayMinutes);
			modelAndView.addObject("endHour", DateUtil.dateToString(cal.getTime(), "HH:mm"));

			modelAndView.addObject("workHours", DateUtil.getTimeHour((endHours - startHours)));
		} else {
			throw new VyiyunException("当前考勤规则没有配置！");
		}
		// TODO 取出当月统计数据
		return modelAndView;
	}

	/**
	 * 查询考勤统计到信息 根据月份
	 * 
	 * @return
	 */
	@RequestMapping(value = "getAttendanceStatisticByMonth", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getAttendanceStatisticByMonth(HttpServletRequest request) {
		// 获取请假申请信息
		DataResult dataResult = new DataResult();
		Map<String, Object> resultData = new HashMap<String, Object>();
		Map<String, Object> singCountInfo = new HashMap<String, Object>();
		singCountInfo.put("userId", HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid());
		singCountInfo.put("monthDate",
				DateUtil.stringToDate(HttpRequestUtil.getInst().getString("monthDate"), "yyyy-MM"));
		// 考勤统计
		singCountInfo.put("signType", "0");
		resultData.put("kqSignCountInfo", signUserService.getSignCountByMonth(singCountInfo));
		// 迟到早退
		singCountInfo.put("isSignException", "0");
		resultData.put("kqBeLateLeaveEarlyInfo", signUserService.getSignCountByMonth(singCountInfo));
		// 工作时长
		// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen start.
		singCountInfo.put("corpId", HttpRequestUtil.getInst().getCurrentWeixinUser().getCorpId());
		// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen end.
		resultData.put("kqworkHour", signUserService.getTotalHourByMonth(singCountInfo));

		dataResult.setData(resultData);
		return dataResult;
	}

	/**
	 * 查询签到统计到信息 根据月份
	 * 
	 * @return
	 */
	@RequestMapping(value = "getSignStatisticByMonth", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getSignStatisticByMonth(HttpServletRequest request) {
		// 获取请假申请信息
		DataResult dataResult = new DataResult();
		Map<String, Object> resultData = new HashMap<String, Object>();
		Map<String, Object> singCountInfo = new HashMap<String, Object>();
		singCountInfo.put("userId", HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid());
		singCountInfo.put("monthDate",
				DateUtil.stringToDate(HttpRequestUtil.getInst().getString("monthDate"), "yyyy-MM"));
		// 签到统计
		singCountInfo.put("signType", "1");
		resultData.put("qtSignCountInfo", signUserService.getSignCountByMonth(singCountInfo));
		// 迟到早退
		singCountInfo.put("isSignException", "0");
		resultData.put("qtBeLateLeaveEarlyInfo", signUserService.getSignCountByMonth(singCountInfo));
		// 出勤率
		// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen start.
		singCountInfo.put("corpId", HttpRequestUtil.getInst().getCurrentWeixinUser().getCorpId());
		// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen end.
		resultData.put("qtAttendance", signUserService.getAttendanceRateByMonth(singCountInfo));
		dataResult.setData(resultData);
		return dataResult;
	}

	/**
	 * 查询签到异常信息 根据月份 并统计
	 * 
	 * @return
	 */
	@RequestMapping(value = "getExceptionKQRecordByMonth", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getExceptionKQRecordByMonth(HttpServletRequest request) {
		// 获取请假申请信息
		DataResult dataResult = new DataResult();
		Map<String, Object> singCountInfo = new HashMap<String, Object>();
		singCountInfo.put("userId", HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid());
		singCountInfo.put("monthDate",
				DateUtil.stringToDate(HttpRequestUtil.getInst().getString("monthDate"), "yyyy-MM"));
		// 异常考勤记录
		dataResult.setData(signUserService.getExceptionKQSignByMonth(singCountInfo));
		return dataResult;
	}

	/**
	 * 我发起的签到信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "myLaunchSignView", method = RequestMethod.GET)
	public ModelAndView myLaunchSignView() {
		ModelAndView modelAndView = super.createModelAndView("myLaunch_sign");
		// TODO 取出当月统计数据
		return modelAndView;
	}

	/**
	 * 我发起的签到信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "myLaunchSignDetailView", method = RequestMethod.GET)
	public ModelAndView myLaunchSignDetailView() {
		ModelAndView modelAndView = super.createModelAndView("myLaunch_signDetail");
		String id = HttpRequestUtil.getInst().getString("id");
		// 需要获取签到信息
		Map<String, Object> dataMap = signService.getSignById(id);
		modelAndView.addObject("signInfo", dataMap);
		EntityAccount entityAccount = new EntityAccount();
		entityAccount.setEntityId(id);
		entityAccount.setEntityType(CommonAppType.EntityType.QD.value());
		// 获取应签到用户
		List<EntityAccount> entityAccountList = entityAccountService.getEntityAccount(entityAccount);
		// 获取所有应签到用户
		SignUser signUser = new SignUser();
		signUser.setSignId(id);
		// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen start.
		signUser.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
		// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen end.
		List<Map<String, Object>> signUserList = signUserService.getSignUser(signUser);
		// 查询已经签到用户
		List<String> userList = new ArrayList<String>();
		String userId = null;
		for (Map<String, Object> map : signUserList) {
			userId = StringUtil.getString(map.get("userId"));
			if (!userList.contains(userId)) {
				userList.add(userId);
			}
		}
		// 应该签到者
		StringBuffer shouldSignUserInfo = new StringBuffer();
		// 已签到者
		StringBuffer signUserInfo = new StringBuffer();
		// 未签到者
		StringBuffer noSignUserInfo = new StringBuffer();

		// 遍历应签到人
		for (int i = 0, size = (entityAccountList == null) ? 0 : entityAccountList.size(); i < size; i++) {
			entityAccount = entityAccountList.get(i);
			userId = entityAccount.getAccountId();
			if (userList.contains(userId)) {
				signUserInfo.append(',').append(entityAccount.getAccountName());
			} else {
				noSignUserInfo.append(',').append(entityAccount.getAccountName());
			}
			shouldSignUserInfo.append(',').append(entityAccount.getAccountName());
		}

		if (signUserInfo.length() > 0) {
			signUserInfo.deleteCharAt(0);
		}
		if (noSignUserInfo.length() > 0) {
			noSignUserInfo.deleteCharAt(0);
		}
		if (shouldSignUserInfo.length() > 0) {
			shouldSignUserInfo.deleteCharAt(0);
		}
		if (StringUtils.isEmpty(signUserInfo.toString())) {
			signUserInfo.append("无");
		}
		if (StringUtils.isEmpty(noSignUserInfo.toString())) {
			noSignUserInfo.append("无");
		}
		if (StringUtils.isEmpty(shouldSignUserInfo.toString())) {
			shouldSignUserInfo.append("无");
		}
		modelAndView.addObject("signUserInfo", signUserInfo.toString());
		modelAndView.addObject("noSignUserInfo", noSignUserInfo.toString());
		modelAndView.addObject("shouldSignUserInfo", shouldSignUserInfo.toString());
		// 当前时间信息
		Date curentDate = new Date();
		modelAndView.addObject("date", DateUtil.dateToString(curentDate, "MM月dd日") + DateUtil.getDayOfWeek(curentDate));
		modelAndView.addObject("time", DateUtil.dateToString(curentDate, "HH:mm"));

		// TODO 取出当月统计数据
		return modelAndView;
	}

	/**
	 * 我发起的签到记录
	 * 
	 * @return
	 */
	@RequestMapping(value = "getMyLaunchSignRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	DataResult getMyLaunchSignRecord() {
		String spageIndex = HttpRequestUtil.getInst().getString("pageIndex");
		String spageSize = HttpRequestUtil.getInst().getString("pageSize");
		String type = HttpRequestUtil.getInst().getString("type");
		int pageIndex = -1;
		int pageSize = -1;
		if (StringUtils.isNotEmpty(spageIndex)) {
			pageIndex = Integer.valueOf(spageIndex);
		}
		if (StringUtils.isNotEmpty(spageSize)) {
			pageSize = Integer.valueOf(spageSize);
		}
		Sign sign = new Sign();
		Map<String, Object> params = new HashMap<String, Object>();
		// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen start.
		WeixinUser currentUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
		sign.setUserId(currentUser.getUserid());
		sign.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
		// 微依云 公共应用 CorpId追加修正 2015-12-28 By zb.shen end.
		if ("0".equals(type)) {
			params.put("isNoEnd", true);
		} else if ("1".equals(type)) {
			params.put("isEnd", true);
		}
		return signService.querySignRecord(sign, params, pageIndex, pageSize);
	}

	@Override
	protected String getPrefix() {
		return super.getPrefix() + "mobile/sign/";
	}
}
