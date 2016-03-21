package com.vyiyun.common.weixin.controller.mobile;

import java.net.URLDecoder;
import java.util.ArrayList;
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
import com.vyiyun.common.weixin.entity.Absent;
import com.vyiyun.common.weixin.entity.Approval;
import com.vyiyun.common.weixin.enums.CommonAppType;
import com.vyiyun.common.weixin.service.IAbsentService;
import com.vyiyun.weixin.cache.impl.WeixinContactCache;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.controller.AbstWebController;
import com.vyiyun.weixin.entity.EntityAccount;
import com.vyiyun.weixin.exception.VyiyunBusinessException;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.WeixinUser;
import com.vyiyun.weixin.service.IEntityAccountService;
import com.vyiyun.weixin.service.ISystemStatusService;
import com.vyiyun.weixin.utils.CommonUtil;
import com.vyiyun.weixin.utils.DateUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.SpringContextHolder;
import com.vyiyun.weixin.utils.SystemCacheUtil;
import com.vyiyun.weixin.web.model.ResponseResult;
import com.vyy.weixin.annotation.App;
import com.vyy.weixin.annotation.OAuth;
import com.vyy.weixin.annotation.Suite;

/**
 * @ClassName: AbsentController
 * @Description: 请假相关请求处理器
 * @author CCLIU
 * @date 2015-6-11 上午11:05:22 v1.0
 */
@Controller
@RequestMapping(value = "/mobile/absent")
@Suite("1")
@OAuth
@App(id = "absent")
public class AbsentController extends AbstWebController {

	@Autowired
	private IAbsentService absentService;
	@Autowired
	private IEntityAccountService entityAccountService;
	@Autowired
	private ISystemStatusService systemStatusService;

	/**
	 * 请假
	 * 
	 * @return
	 */
	@RequestMapping(value = "absentView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView absentView() {
		ModelAndView modelView = createModelAndView("absent");
		// 获取请假类型数据
		// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
		modelView.addObject("absentTypeList", systemStatusService.getSystemStatus(HttpRequestUtil.getInst().getCurrentCorpId(), Constants.SYSTEM_ABSENT_TYPE, true));
		// 微依云 公共应用 CorpId追加修正 2016-01-04 By zb.shen end.
		WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
		WeixinContactCache<Object> weixinContactCache = (WeixinContactCache<Object>) SystemCacheUtil.getInstance()
				.getWeixinContactCache();
		modelView.addObject("department", weixinContactCache.getUserDept(weixinUser.getDepartment()));
		modelView.addObject("weixinUser", weixinUser);
		// BUG #62 修改 zb.shen 2015年12月25日 start.
		modelView.addObject("flag", HttpRequestUtil.getInst().getString("flag"));
		// BUG #62 修改 zb.shen 2015年12月25日 end.
		return modelView;
	}

	/**
	 * 请假申请
	 * 
	 * @return
	 */
	@RequestMapping(value = "absentApply", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object absentApply(HttpServletRequest request) {
		// 响应数据
		ResponseResult responseResult = new ResponseResult();
		// 获取请假申请信息
		String jsonAbsentApplyInfo = HttpRequestUtil.getInst().getString("absentApplyInfo");
		try {
			JSONObject jsonAbsentApplyInfoObj = JSON.parseObject(URLDecoder.decode(jsonAbsentApplyInfo, "UTF-8"));
			String id = CommonUtil.GeneGUID();
			jsonAbsentApplyInfoObj.put("id", id);
			responseResult.setValue(id);
			absentService.doApply(jsonAbsentApplyInfoObj);
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setErrorMsg("请联系系统管理员");
			responseResult.setStatus(-1);
		}
		// 返回数据
		return responseResult;
	}

	/**
	 * 请假记录
	 * 
	 * @return
	 */
	@RequestMapping(value = "absentRecordView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView absentRecordView() {
		ModelAndView modelView = createModelAndView("absent_record");
		// BUG #62 修改 zb.shen 2015年12月25日 start.
		modelView.addObject("flag", HttpRequestUtil.getInst().getString("flag"));
		// BUG #62 修改 zb.shen 2015年12月25日 end.
		return modelView;
	}

	/**
	 * 获取请假记录
	 * 
	 * @return
	 */
	@RequestMapping(value = "getAbsentRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getAbsentRecord(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		String operationType = HttpRequestUtil.getInst().getString("operationType");
		if (StringUtils.isEmpty(operationType)) {
			responseResult.setErrorMsg("参数为空!");
			responseResult.setStatus(-1);
			return responseResult;
		}
		try {

			int pageIndex = -1;
			int pageSize = -1;
			String sPageIndex = HttpRequestUtil.getInst().getString("pageIndex");
			String sPageSize = HttpRequestUtil.getInst().getString("pageSize");
			if (StringUtils.isNotEmpty(sPageIndex) && sPageIndex.matches("\\d+")) {
				pageIndex = Integer.parseInt(sPageIndex);
			}
			if (StringUtils.isNotEmpty(sPageSize) && sPageSize.matches("\\d+")) {
				pageSize = Integer.parseInt(sPageSize);
			}

			Absent absent = new Absent();
			absent.setUserId(HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid());
			// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
			absent.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
			absent.setStatus(operationType);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("orderBy", "CreateTime desc");
			responseResult.setValue(absentService.queryAbsentRecord(absent, params, pageIndex, pageSize).getData());
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 审核记录
	 * 
	 * @return
	 */
	@RequestMapping(value = "auditRecordView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView auditRecordView() {
		ModelAndView modelView = createModelAndView("audit_record");
		// BUG #62 修改 zb.shen 2015年12月25日 start.
		modelView.addObject("flag", HttpRequestUtil.getInst().getString("flag"));
		// BUG #62 修改 zb.shen 2015年12月25日 end.
		return modelView;
	}

	/**
	 * 获取审核记录
	 * 
	 * @return
	 */
	@RequestMapping(value = "getAuditRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getAuditRecord(HttpServletRequest request) {

		ResponseResult responseResult = new ResponseResult();
		String operationType = HttpRequestUtil.getInst().getString("operationType");
		if (StringUtils.isEmpty(operationType)) {
			responseResult.setErrorMsg("参数为空!");
			responseResult.setStatus(-1);
			return responseResult;
		}
		try {
			int pageIndex = -1;
			int pageSize = -1;
			String sPageIndex = HttpRequestUtil.getInst().getString("pageIndex");
			String sPageSize = HttpRequestUtil.getInst().getString("pageSize");
			if (StringUtils.isNotEmpty(sPageIndex) && sPageIndex.matches("\\d+")) {
				pageIndex = Integer.parseInt(sPageIndex);
			}
			if (StringUtils.isNotEmpty(sPageSize) && sPageSize.matches("\\d+")) {
				pageSize = Integer.parseInt(sPageSize);
			}
			// 获取请假申请信息
			EntityAccount entityAccount = new EntityAccount();
			entityAccount.setAccountId(HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid());
			Map<String, Object> params = new HashMap<String, Object>();
			// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
			params.put("corpId", HttpRequestUtil.getInst().getCurrentCorpId());
			// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
			params.put("operationType", operationType);
			params.put("userId", HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid());
			params.put("orderBy", "CreateTime desc");
			responseResult.setValue(absentService.queryAuditRecord(params, pageIndex, pageSize).getData());
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 请假记录详情
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "absentDetailView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView absentDetailView(HttpServletRequest request) {
		ModelAndView modelView = createModelAndViewWithSign("absent_detail", request);
		String id = HttpRequestUtil.getInst().getString("id");
		String flag = HttpRequestUtil.getInst().getString("flag");
		// 这里给详情页面标示 页面来源路径 主要解决审批后退回问题
		if (StringUtils.isNotEmpty(flag)) {
			modelView.addObject("flag", flag);
		}
		if (StringUtils.isEmpty(id)) {
			throw new VyiyunException("参数id为空!");
		}
		// 获取当前用户
		WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
		Absent absent = absentService.getAbsentById(id);
		if (null == absent) {
			throw new VyiyunBusinessException("记录已被撤销或不存在!");
		}
		// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
		modelView.addObject("stautsDisplay",
				SystemCacheUtil.getInstance().getSystemStatusCache().getSystemStatusName("Status", absent.getStatus()));
		// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.

		// 获取当前审核人
		EntityAccount entityAccount = new EntityAccount();
		// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
		entityAccount.setCorpId(weixinUser.getCorpId());
		// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
		entityAccount.setEntityId(id);
		entityAccount.setEntityType(CommonAppType.EntityType.QJ.value());
		entityAccount.setPersonType(CommonAppType.PersonType.SH.value());
		entityAccount.setDealResult("0");
		List<EntityAccount> entityAccountList = (List<EntityAccount>) entityAccountService.queryEntityAccountRecord(
				entityAccount, null, -1, -1).getData();
		if (!CollectionUtils.isEmpty(entityAccountList)) {
			// 待审核
			modelView.addObject("awaitUser", entityAccountList.get(0));
		} else {
			// 如果没有待审核者 及需要统计审批结果
			// 3、退回
			// 2、不同意
			entityAccount = new EntityAccount();
			entityAccount.setEntityId(id);
			// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
			entityAccount.setCorpId(weixinUser.getCorpId());
			// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
			entityAccount.setEntityType(CommonAppType.EntityType.QJ.value());
			entityAccount.setPersonType(CommonAppType.PersonType.SH.value());
			entityAccount.setDealResult(CommonAppType.CommandType.退回.value());
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("orderBy", "UpdateTime desc");
			entityAccountList = (List<EntityAccount>) entityAccountService.queryEntityAccountRecord(entityAccount,
					params, -1, -1).getData();
			if (!CollectionUtils.isEmpty(entityAccountList)) {
				modelView.addObject("applyResult", entityAccountList.get(0).getDealResult());
				modelView.addObject("applyRemark", entityAccountList.get(0).getRemark());
			} else {
				// 1、同意
				entityAccount.setDealResult(CommonAppType.CommandType.同意.value());
				entityAccountList = (List<EntityAccount>) entityAccountService.queryEntityAccountRecord(entityAccount,
						params, -1, -1).getData();
				if (!CollectionUtils.isEmpty(entityAccountList)) {
					modelView.addObject("applyResult", entityAccountList.get(0).getDealResult());
					modelView.addObject("applyRemark", entityAccountList.get(0).getRemark());
				}
			}
		}

		// 获取已审核人
		// 获取已审核人 抄送人
		StringBuffer alreadyUserBuffer = new StringBuffer();
		StringBuffer ccUserBuffer = new StringBuffer();
		entityAccount = new EntityAccount();
		// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
		entityAccount.setCorpId(weixinUser.getCorpId());
		// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
		entityAccount.setEntityId(id);
		entityAccount.setEntityType(CommonAppType.EntityType.QJ.value());
		entityAccountList = entityAccountService.getEntityAccount(entityAccount);
		// 权限用户
		List<String> permissionUsers = new ArrayList<String>();

		if (!CollectionUtils.isEmpty(entityAccountList)) {
			for (EntityAccount ea : entityAccountList) {
				// 处理过
				if (ea.getPersonType().equals(CommonAppType.PersonType.SH.value())) {
					if (!ea.getDealResult().equals("0")) {
						alreadyUserBuffer.append(",").append(ea.getAccountName());
					}
				} else if (ea.getPersonType().equals(CommonAppType.PersonType.CS.value())) {
					ccUserBuffer.append(",").append(ea.getAccountName());
				}
				permissionUsers.add(ea.getAccountId());
			}
			if (alreadyUserBuffer.length() > 0) {
				alreadyUserBuffer.deleteCharAt(0);
			}
			if (ccUserBuffer.length() > 0) {
				ccUserBuffer.deleteCharAt(0);
			}
			modelView.addObject("audited", alreadyUserBuffer.toString());
			modelView.addObject("ccUser", ccUserBuffer.toString());
			modelView.addObject("shList", entityAccountList);
		}

		if (weixinUser.getUserid().equals(absent.getUserId())) {
			// 请假发起人
			if (!CommonAppType.Status.已审核.value().equals(absent.getStatus())) {
				modelView.addObject("userType", "0");
			}
		} else /** 判断当前用户是否 是抄送 以及审核过 其他用户一律无权限范围 */
		if (!permissionUsers.contains(weixinUser.getUserid())) {
			// throw new EnergyException("当前【" + weixinUser.getName() +
			// "】非法用户操作!");
		}
		modelView.addObject("absentInfo", absent);
		// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
		modelView.addObject("absentTypeName", SystemCacheUtil.getInstance().getSystemStatusCache()
				.getSystemStatusValue(corpId, "AbsentType", absent.getAbsentType()));

		// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
		// 计算请假时间
		modelView.addObject("absentTime", DateUtil.getTimeDiff(absent.getBeginTime(), absent.getEndTime()));
		// BUG #62 修改 zb.shen 2015年12月25日 start.
		modelView.addObject("flag", HttpRequestUtil.getInst().getString("flag"));
		// BUG #62 修改 zb.shen 2015年12月25日 end.
		return modelView;
	}

	/**
	 * 审核操作界面
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "auditDetailView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView auditView(HttpServletRequest request) {
		ModelAndView modelView = createModelAndView("audit_detail");

		String id = HttpRequestUtil.getInst().getString("id");
		if (StringUtils.isEmpty(id)) {
			throw new VyiyunException("参数id为空!");
		}
		// 获取当前用户
		WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
		Absent absent = absentService.getAbsentById(id);
		if (null == absent) {
			throw new VyiyunBusinessException("记录已被撤销或不存在!");
		}
		// 如果已审核
		if (CommonAppType.Status.已审核.value().equals(absent.getStatus())) {
			return absentDetailView(request);
		}
		// 根据当前人 以及清假id 判断是否存在审核
		EntityAccount entityAccount = new EntityAccount();
		entityAccount.setEntityId(id);
		// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
		entityAccount.setCorpId(weixinUser.getCorpId());
		// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
		entityAccount.setEntityType(CommonAppType.EntityType.QJ.value());
		entityAccount.setPersonType(CommonAppType.PersonType.SH.value());
		entityAccount.setDealResult("0");
		entityAccount.setAccountId(weixinUser.getUserid());
		List<EntityAccount> entityAccountList = (List<EntityAccount>) entityAccountService.queryEntityAccountRecord(
				entityAccount, null, -1, -1).getData();
		// 待审核
		if (!CollectionUtils.isEmpty(entityAccountList)) {
			modelView.addObject("entityId", entityAccountList.get(0).getId());
			// 获取已审核人 抄送人
			StringBuffer alreadyUserBuffer = new StringBuffer();
			StringBuffer ccUserBuffer = new StringBuffer();
			entityAccount = new EntityAccount();
			// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
			entityAccount.setCorpId(weixinUser.getCorpId());
			// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
			entityAccount.setEntityId(id);
			entityAccount.setInvalid(null);
			entityAccount.setEntityType(CommonAppType.EntityType.QJ.value());
			entityAccountList = (List<EntityAccount>) entityAccountService.queryEntityAccountRecord(entityAccount,
					null, -1, -1).getData();
			// 权限用户
			List<String> permissionUsers = new ArrayList<String>();

			if (!CollectionUtils.isEmpty(entityAccountList)) {
				for (EntityAccount ea : entityAccountList) {
					// 处理过
					if (ea.getPersonType().equals(CommonAppType.PersonType.SH.value())) {
						if (!ea.getDealResult().equals("0")) {
							alreadyUserBuffer.append(",").append(ea.getAccountName());
						}
					} else if (ea.getPersonType().equals(CommonAppType.PersonType.CS.value())) {
						ccUserBuffer.append(",").append(ea.getAccountName());
					}
					permissionUsers.add(ea.getAccountId());
				}
				if (alreadyUserBuffer.length() > 0) {
					alreadyUserBuffer.deleteCharAt(0);
				}
				if (ccUserBuffer.length() > 0) {
					ccUserBuffer.deleteCharAt(0);
				}
				modelView.addObject("audited", alreadyUserBuffer.toString());
				modelView.addObject("ccUser", ccUserBuffer.toString());
				modelView.addObject("shList", entityAccountList);
			}
			// 存储请假信息
			modelView.addObject("absentInfo", absent);
			// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
			String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
			modelView.addObject("absentTypeName", SystemCacheUtil.getInstance().getSystemStatusCache()
					.getSystemStatusValue(corpId, "AbsentType", absent.getAbsentType()));

			// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
			// 计算请假时间
			modelView.addObject("absentTime", DateUtil.getTimeDiff(absent.getBeginTime(), absent.getEndTime()));
		} else {
			return absentDetailView(request);
		}
		// BUG #62 修改 zb.shen 2015年12月25日 start.
		modelView.addObject("flag", HttpRequestUtil.getInst().getString("flag"));
		// BUG #62 修改 zb.shen 2015年12月25日 end.
		return modelView;
	}

	/**
	 * 获取请假类型
	 * 
	 * @return
	 */
	@RequestMapping(value = "getAbsentType", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getAbsentType(HttpServletRequest request) {
		return systemStatusService.getSystemStatus(HttpRequestUtil.getInst().getCurrentCorpId(), Constants.SYSTEM_ABSENT_TYPE, true);
	}

	/**
	 * 审核记录详情
	 * 
	 * @return
	 */
	@RequestMapping(value = "auditRecordDetailView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView auditRecordDetailView() {
		ModelAndView modelView = createModelAndView("audit_record_detail");
		String id = HttpRequestUtil.getInst().getString("id");
		Absent absent = absentService.getAbsentById(id);
		if (null != absent) {
			modelView.addObject("absentInfo", absent);
		}
		// 获取待审核请假信息
		return modelView;
	}

	/**
	 * 审核处理
	 * 
	 * @return
	 */
	@RequestMapping(value = "doAbsent", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object doAudit() {
		ResponseResult responseResult = new ResponseResult();
		try {
			absentService.doAbsent(JSON.parseObject(HttpRequestUtil.getInst().getString("absentInfo")));
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setStatus(-1);
			if (e instanceof VyiyunException) {
				responseResult.setErrorMsg(e.getMessage());
			} else {
				responseResult.setErrorMsg(SpringContextHolder.getI18n("1000000"));
			}
		}
		return responseResult;
	}

	/**
	 * add by tf 抄送记录视图
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "ccRecordView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView ccRecordView(ModelMap model) {
		ModelAndView modelView = createModelAndView("cc_record");
		return modelView;
	}

	/**
	 * 抄送给我的记录
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "getccAbsentRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getccApprovalRecord(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		try {
			int pageIndex = -1;
			int pageSize = -1;
			String sPageIndex = HttpRequestUtil.getInst().getString("pageIndex");
			String sPageSize = HttpRequestUtil.getInst().getString("pageSize");
			if (StringUtils.isNotEmpty(sPageIndex) && sPageIndex.matches("\\d+")) {
				pageIndex = Integer.parseInt(sPageIndex);
			}
			if (StringUtils.isNotEmpty(sPageSize) && sPageSize.matches("\\d+")) {
				pageSize = Integer.parseInt(sPageSize);
			}
			Approval approval = new Approval();
			Map<String, Object> params = new HashMap<String, Object>();
			String userName = HttpRequestUtil.getInst().getString("userName");
			String createDate = HttpRequestUtil.getInst().getString("createDate");
			String status = HttpRequestUtil.getInst().getString("status");

			if (StringUtils.isNotEmpty(userName)) {
				params.put("userNameLike", userName);
			}
			if (StringUtils.isNotEmpty(status)) {
				params.put("status", status);
			}
			if (StringUtils.isNotEmpty(createDate)) {
				params.put("createDate", DateUtil.stringToDate(createDate, "yyyy-MM-dd"));
			}
			params.put("userId", HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid());
			// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
			String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
			params.put("corpId", corpId);
			// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
			responseResult.setValue(absentService.getAbsentCcRecord(approval, params, pageIndex, pageSize).getData());
		} catch (Exception e) {
			responseResult.setStatus(-1);
			responseResult.setErrorMsg("获取审批记录失败!");
		}
		return responseResult;
	}

	/**
	 * 请假查询所有
	 * 
	 * @return
	 */
	@RequestMapping(value = "queryAbsentView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView queryAbsentView() {
		ModelAndView modelView = createModelAndView("queryAbsent");
		return modelView;
	}

	/**
	 * 查询所有请假
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "getQueryAbsentRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getQueryAbsentRecord(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		String operationType = HttpRequestUtil.getInst().getString("operationType");
		if (StringUtils.isEmpty(operationType)) {
			responseResult.setErrorMsg("参数为空!");
			responseResult.setStatus(-1);
			return responseResult;
		}
		try {
			int pageIndex = -1;
			int pageSize = -1;
			String sPageIndex = HttpRequestUtil.getInst().getString("pageIndex");
			String sPageSize = HttpRequestUtil.getInst().getString("pageSize");
			if (StringUtils.isNotEmpty(sPageIndex) && sPageIndex.matches("\\d+")) {
				pageIndex = Integer.parseInt(sPageIndex);
			}
			if (StringUtils.isNotEmpty(sPageSize) && sPageSize.matches("\\d+")) {
				pageSize = Integer.parseInt(sPageSize);
			}
			Absent absent = new Absent();
			Map<String, Object> params = new HashMap<String, Object>();
			String userName = HttpRequestUtil.getInst().getString("userName");
			String startDate = HttpRequestUtil.getInst().getString("startDate");
			String endDate = HttpRequestUtil.getInst().getString("endDate");
			if (StringUtils.isNotEmpty(userName)) {
				params.put("userNameLike", userName);
			}
			absent.setStatus(operationType);
			// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
			absent.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			;
			// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
			if (StringUtils.isNotEmpty(startDate)) {
				params.put("startDate", DateUtil.stringToDate(startDate, "yyyy-MM-dd"));
			}
			if (StringUtils.isNotEmpty(endDate)) {
				params.put("endDate", DateUtil.stringToDate(endDate, "yyyy-MM-dd"));
			}
			params.put("orderBy", "CreateTime desc");
			responseResult.setValue(absentService.queryAbsentRecord(absent, params, pageIndex, pageSize).getData());
		} catch (Exception e) {
			responseResult.setStatus(-1);
			responseResult.setErrorMsg("获取审批记录失败!");
		}
		return responseResult;
	}

	@Override
	protected String getPrefix() {
		return super.getPrefix() + "mobile/absent/";
	}
}
