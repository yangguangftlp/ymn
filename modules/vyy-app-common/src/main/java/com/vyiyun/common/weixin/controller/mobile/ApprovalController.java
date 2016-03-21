package com.vyiyun.common.weixin.controller.mobile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.vyiyun.common.weixin.entity.Approval;
import com.vyiyun.common.weixin.enums.CommonAppType;
import com.vyiyun.common.weixin.service.IApprovalService;
import com.vyiyun.common.weixin.service.IApproversService;
import com.vyiyun.weixin.cache.impl.SystemStatusCache;
import com.vyiyun.weixin.cache.impl.WeixinContactCache;
import com.vyiyun.weixin.controller.AbstWebController;
import com.vyiyun.weixin.entity.Accessory;
import com.vyiyun.weixin.entity.EntityAccount;
import com.vyiyun.weixin.exception.VyiyunBusinessException;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.WeixinUser;
import com.vyiyun.weixin.service.IAccessoryService;
import com.vyiyun.weixin.service.IEntityAccessoryService;
import com.vyiyun.weixin.service.IEntityAccountService;
import com.vyiyun.weixin.utils.DateUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.SpringContextHolder;
import com.vyiyun.weixin.utils.SystemCacheUtil;
import com.vyiyun.weixin.web.model.ResponseResult;
import com.vyy.weixin.annotation.App;
import com.vyy.weixin.annotation.OAuth;
import com.vyy.weixin.annotation.Suite;

/**
 * @ClassName: ApprovalController
 * @Description: 审批相关请求处理器
 * @author CCLIU
 * @date 2015-6-11 上午11:05:22 v1.0
 */
@Controller
@RequestMapping(value = "/mobile/approval")
@Suite("3")
@OAuth
@App(id = "approval")
public class ApprovalController extends AbstWebController {

	/**
	 * 审批服务
	 */
	@Autowired
	private IApprovalService approvalService;
	@Autowired
	private IEntityAccountService entityAccountService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IApproversService approversService;
	@Autowired
	private IEntityAccessoryService entityAccessoryService;

	/**
	 * 普通审批 --发起审批
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "relaunchApprovalView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView relaunchApprovalView(HttpServletRequest request) {
		ModelAndView modelView = createModelAndViewWithSign("approval", request);
		String approvalId = HttpRequestUtil.getInst().getString("id");
		String approvalType = HttpRequestUtil.getInst().getString("approvalType");
		modelView.addObject("approvalType", approvalType);
		if (StringUtils.isEmpty(approvalType)) {
			throw new VyiyunException("参数approvalType不能为空 ！");
		}
		if (!"0".equals(approvalType) && !"1".equals(approvalType)) {
			throw new VyiyunException("无效的approvalType类型:" + approvalType);
		}
		if (!"0".equals(approvalType) && !"1".equals(approvalType)) {
			throw new VyiyunException("无效的id参数:" + approvalId);
		}
		// 删除审核人
		entityAccountService.deleteByEntityId(approvalId);
		approversService.deleteApprovers(null, approvalId);
		entityAccessoryService.deleteEntityAccessory(approvalId);

		Approval approval = approvalService.getApprovalById(approvalId);
		if (null == approval) {
			throw new VyiyunBusinessException("记录已被撤销或不存在!");
		}
		modelView.addObject("approvalId", approvalId);
		modelView.addObject("approvalInfo", approval);
		return modelView;
	}

	/**
	 * 普通审批 --发起审批
	 * 
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "approvalView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView approvalView(HttpServletRequest request) {
		ModelAndView modelView = createModelAndViewWithSign("approval", request);
		String approvalType = HttpRequestUtil.getInst().getString("approvalType");
		if (StringUtils.isEmpty(approvalType)) {
			throw new VyiyunException("参数approvalType不能为空 ！");
		}
		if (!"0".equals(approvalType) && !"1".equals(approvalType)) {
			throw new VyiyunException("无效的approvalType类型:" + approvalType);
		}
		// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
		// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
		modelView.addObject("approvalType", approvalType);
		String approvalId = HttpRequestUtil.getInst().getString("id");
		if (!StringUtils.isEmpty(approvalId)) {
			Approval approval = approvalService.getApprovalById(approvalId);
			if (null == approval) {
				throw new VyiyunBusinessException("记录已被撤销或不存在!");
			}
			modelView.addObject("approvalId", approvalId);
			modelView.addObject("approvalInfo", approval);
			// 获取审核人以及 抄送者
			// 获取当前审核人
			EntityAccount entityAccount = new EntityAccount();
			entityAccount.setEntityId(approvalId);
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
			entityAccount.setCorpId(corpId);
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
			entityAccount.setEntityType(CommonAppType.EntityType.SP.value());
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("orderBy", "UpdateTime desc");
			// 判断当前报销审批是否完成
			// 这里会有两条数据
			List<EntityAccount> entityAccountList = (List<EntityAccount>) entityAccountService
					.queryEntityAccountRecord(entityAccount, null, -1, -1).getData();
			if (!CollectionUtils.isEmpty(entityAccountList)) {
				List<EntityAccount> auditorList = new ArrayList<EntityAccount>();
				List<EntityAccount> principalList = new ArrayList<EntityAccount>();

				for (EntityAccount ea : entityAccountList) {
					if (CommonAppType.PersonType.SH.value().equals(ea.getPersonType())) {
						auditorList.add(ea);
					} else if (CommonAppType.PersonType.CS.value().equals(ea.getPersonType())) {
						principalList.add(ea);
					}
				}
				// 对审核人进行排序
				Collections.sort(auditorList, new Comparator<EntityAccount>() {
					@Override
					public int compare(EntityAccount o1, EntityAccount o2) {
						return o1.getRemark().compareTo(o2.getRemark());
					}

				});
				modelView.addObject("auditorList", auditorList);
				modelView.addObject("principalList", principalList);
			}
			// 获取附件信息
			List<Accessory> accessoryList = accessoryService.getAccessoryByEntityId(approvalId);
			if (!CollectionUtils.isEmpty(accessoryList)) {
				modelView.addObject("accessoryInfor", accessoryList);
			}
		} else {
			Approval approval = new Approval();
			WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
			WeixinContactCache<Object> weixinContactCache = (WeixinContactCache<Object>) SystemCacheUtil.getInstance()
					.getWeixinContactCache();
			approval.setDepartment(weixinContactCache.getUserDept(weixinUser.getDepartment()));
			modelView.addObject("approvalInfo", approval);
		}
		// BUG #62 修改 zb.shen 2015年12月25日 start.
		modelView.addObject("flag", HttpRequestUtil.getInst().getString("flag"));
		// BUG #62 修改 zb.shen 2015年12月25日 end.
		return modelView;
	}

	/**
	 * 发起审批
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "launchApproval", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object launchApproval(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		// 获取签到发布信息
		String launchApprovalInfo = HttpRequestUtil.getInst().getString("launchApprovalInfo");
		try {
			JSONObject jsonLaunchApprovalInfo = JSON.parseObject(launchApprovalInfo);
			responseResult.setValue(approvalService.launchApproval(jsonLaunchApprovalInfo));
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 审批详情页面
	 * 
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "approvalDetailView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView approvalDetailView(HttpServletRequest request) {
		ModelAndView modelView = createModelAndViewWithSign("approval_detail", request);
		String id = HttpRequestUtil.getInst().getString("id");
		String flag = HttpRequestUtil.getInst().getString("flag");
		// 这里给详情页面标示 页面来源路径 主要解决审批后退回问题
		if (StringUtils.isNotEmpty(flag)) {
			modelView.addObject("flag", flag);
		}
		if (StringUtils.isEmpty(id)) {
			throw new VyiyunException("参数id不能为空 ！");
		}
		// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
		// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
		Approval approval = approvalService.getApprovalById(id);
		if (null == approval) {
			throw new VyiyunBusinessException("记录已被撤销或不存在!");
		}
		modelView.addObject("approvalInfo", approval);
		SystemStatusCache<Object> systemStatusCache = (SystemStatusCache<Object>) SystemCacheUtil.getInstance()
				.getSystemStatusCache();
		// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
		modelView.addObject("stautsDisplay", systemStatusCache.getSystemStatusName("Status", approval.getStatus()));
		// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
		// 针对抄送中财务处理
		String uType = HttpRequestUtil.getInst().getString("uType");
		modelView.addObject("uType", uType);
		// 获取当前用户
		WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
		// 获取当前审核人
		EntityAccount entityAccount = new EntityAccount();
		entityAccount.setEntityId(id);
		// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
		entityAccount.setCorpId(corpId);
		// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
		entityAccount.setEntityType(CommonAppType.EntityType.SP.value());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderBy", "UpdateTime desc");
		// 判断当前报销审批是否完成
		// 这里会有两条数据
		List<EntityAccount> entityAccountList = (List<EntityAccount>) entityAccountService.queryEntityAccountRecord(
				entityAccount, null, -1, -1).getData();
		// 审核
		List<EntityAccount> auditorList = new ArrayList<EntityAccount>();
		// 抄送者
		StringBuffer ccUserBuffer = new StringBuffer();
		if (!CollectionUtils.isEmpty(entityAccountList)) {
			for (EntityAccount ea : entityAccountList) {
				// 获取有效数据
				if (!ea.getInvalid()) {
					if (CommonAppType.PersonType.SH.value().equals(ea.getPersonType())) {
						auditorList.add(ea);
					} else if (CommonAppType.PersonType.CS.value().equals(ea.getPersonType())) {
						ccUserBuffer.append(',').append(ea.getAccountName());
					}
				}
			}

			if (ccUserBuffer.length() > 0) {
				ccUserBuffer.deleteCharAt(0);
			}
			modelView.addObject("auditorList", auditorList);
			modelView.addObject("ccUser", ccUserBuffer.toString());
			modelView.addObject("shList", entityAccountList);
		}
		// 3、获取报销附件
		// 处理附件信息
		List<Accessory> accessoryList = accessoryService.getAccessoryByEntityId(id);
		if (!CollectionUtils.isEmpty(accessoryList)) {
			modelView.addObject("accessoryInfor", accessoryList);
		}

		if (weixinUser.getUserid().equals(approval.getUserId())) {
			// 4、判断当前人是否为审批提交者 且 当前审批未完成下 涉及到重新发起 催办
			if (!CommonAppType.Status.已审核.value().equals(approval.getStatus())) {
				modelView.addObject("userType", "0");
			}
		}

		// 批复结果
		// 已审核
		if (CommonAppType.Status.已审核.value().equals(approval.getStatus())) {
			// 这里根据updateTime 区分最后一个审核人
			modelView.addObject("remark", auditorList.get(0).getRemark());
			modelView.addObject("approvalStatus", CommonAppType.Status.已审核.value());

		} else // 审核退回
		if (CommonAppType.Status.审核退回.value().equals(approval.getStatus())) {
			modelView.addObject("remark", auditorList.get(0).getRemark());
			modelView.addObject("approvalStatus", CommonAppType.Status.审核退回.value());
		}
		// BUG #62 修改 zb.shen 2015年12月24日 start.
		modelView.addObject("flag", HttpRequestUtil.getInst().getString("flag"));
		// BUG #62 修改 zb.shen 2015年12月24日 end.
		return modelView;
	}

	/**
	 * 我的记录视图
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "approvalRecordView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView approvalRecordView(ModelMap model) {
		ModelAndView modelView = createModelAndView("approval_record");
		// BUG #62 修改 zb.shen 2015年12月25日 start.
		modelView.addObject("flag", HttpRequestUtil.getInst().getString("flag"));
		// BUG #62 修改 zb.shen 2015年12月25日 end.
		return modelView;
	}

	/**
	 * 我的记录
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "getApprovalRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getApprovalRecord(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		String operationType = HttpRequestUtil.getInst().getString("operationType");
		if (StringUtils.isEmpty(operationType)) {
			responseResult.setStatus(-1);
			responseResult.setErrorMsg("参数operationType不能为空！");
			return responseResult;
		}
		// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
		// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
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
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
			approval.setCorpId(corpId);
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
			approval.setUserId(HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid());
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("orderBy", "CreateTime desc");
			params.put("operationType", operationType);
			responseResult.setValue(approvalService.queryApprovalRecord(approval, params, pageIndex, pageSize)
					.getData());
		} catch (Exception e) {
			responseResult.setStatus(-1);
			responseResult.setErrorMsg("获取审批记录失败!");
		}
		return responseResult;
	}

	/**
	 * 审核记录视图
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "auditRecordView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView auditRecordView(ModelMap model) {
		ModelAndView modelView = createModelAndView("audit_record");
		// BUG #62 修改 zb.shen 2015年12月25日 start.
		modelView.addObject("flag", HttpRequestUtil.getInst().getString("flag"));
		// BUG #62 修改 zb.shen 2015年12月25日 end.
		return modelView;
	}

	/**
	 * 待审核记录
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "getAuditRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getAuditRecord(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
		// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
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
			WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", weixinUser.getUserid());
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
			Approval approval = new Approval();
			approval.setCorpId(corpId);
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
			responseResult.setValue(approvalService.getAuditRecord(approval, params, pageIndex, pageSize).getData());
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setStatus(-1);
			responseResult.setErrorMsg("系统出现错误，请联系管理!");
		}
		return responseResult;
	}

	/**
	 * 已审核记录
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "getBeAuditRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getBeAuditRecord(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
		// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
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
			WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("personType", "0");
			params.put("userId", weixinUser.getUserid());
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
			Approval approval = new Approval();
			approval.setCorpId(corpId);
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
			responseResult.setValue(approvalService.getBeAuditRecord(approval, params, pageIndex, pageSize).getData());
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setStatus(-1);
			responseResult.setErrorMsg("系统出现错误，请联系管理!");
		}
		return responseResult;
	}

	/**
	 * 审核处理页面
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "auditDetailView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView auditDetailView(HttpServletRequest request) {
		ModelAndView modelView = createModelAndViewWithSign("audit_detail", request);
		String id = HttpRequestUtil.getInst().getString("id");
		if (StringUtils.isEmpty(id)) {
			throw new VyiyunException("参数id不能为空 ！");
		}
		Approval approval = approvalService.getApprovalById(id);
		if (null == approval) {
			throw new VyiyunBusinessException("记录已被撤销或不存在!");
		}

		// 如果已审核
		if (CommonAppType.Status.已审核.value().equals(approval.getStatus())
				|| CommonAppType.Status.审核退回.value().equals(approval.getStatus())) {
			return approvalDetailView(request);
		}
		modelView.addObject("approvalInfo", approval);
		// 获取当前审核人
		WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
		// 2、已审核人 报销人 待审核人 待带报销人
		// 获取当前审核人
		EntityAccount entityAccount = new EntityAccount();
		// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
		entityAccount.setCorpId(corpId);
		// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
		entityAccount.setEntityId(id);
		entityAccount.setEntityType(CommonAppType.EntityType.SP.value());
		entityAccount.setInvalid(false);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderBy", "UpdateTime desc");
		// 判断当前报销审批是否完成
		List<EntityAccount> entityAccountList = (List<EntityAccount>) entityAccountService.queryEntityAccountRecord(
				entityAccount, null, -1, -1).getData();
		// 待审核
		StringBuffer alreadyUser = new StringBuffer();
		// 抄送者
		StringBuffer ccUserBuffer = new StringBuffer();
		List<EntityAccount> entityAccounts = new ArrayList<EntityAccount>();
		if (!CollectionUtils.isEmpty(entityAccountList)) {
			for (EntityAccount ea : entityAccountList) {
				// 获取有效数据
				if (!ea.getInvalid()) {
					if (CommonAppType.PersonType.SH.value().equals(ea.getPersonType())) {
						if (ea.getDealResult().equals("0")) {
							entityAccounts.add(ea);
						} else {
							alreadyUser.append(",").append(ea.getAccountName());
						}
					} else if (CommonAppType.PersonType.CS.value().equals(ea.getPersonType())) {
						ccUserBuffer.append(",").append(ea.getAccountName());
					}
				}
			}
			// 当前审核人必须是一位
			if (entityAccounts.size() == 1) {
				modelView.addObject("entityAccount", entityAccounts.get(0));
			} else if (entityAccounts.size() > 1) {
				throw new VyiyunException("数据错误，当前审核人必须是一位!");
			} else if (entityAccounts.isEmpty()) {
				return approvalDetailView(request);
			}
			// 当前非审核人
			if (!entityAccounts.get(0).getAccountId().equals(weixinUser.getUserid())) {
				return approvalDetailView(request);
			}
			if (alreadyUser.length() > 0) {
				alreadyUser.deleteCharAt(0);
			}
			if (ccUserBuffer.length() > 0) {
				ccUserBuffer.deleteCharAt(0);
			}
			modelView.addObject("alreadyUser", alreadyUser);
			modelView.addObject("ccUser", ccUserBuffer);
			modelView.addObject("shList", entityAccountList);
		} else {
			return approvalDetailView(request);
		}
		// 3、获取报销附件
		// 处理附件信息
		List<Accessory> accessoryList = accessoryService.getAccessoryByEntityId(id);
		if (!CollectionUtils.isEmpty(accessoryList)) {
			modelView.addObject("accessoryInfor", accessoryList);
		}
		// BUG #62 修改 zb.shen 2015年12月24日 start.
		modelView.addObject("flag", HttpRequestUtil.getInst().getString("flag"));
		// BUG #62 修改 zb.shen 2015年12月24日 end.
		return modelView;
	}

	/**
	 * 审核处理 有审核处理针对审核记录
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "approvalAudit", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object approvalAudit(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		String approvalInfo = HttpRequestUtil.getInst().getString("approvalAuditInfo");
		try {
			JSONObject jsonApprovalInfo = JSON.parseObject(approvalInfo);
			approvalService.doAudit(jsonApprovalInfo);
		} catch (Exception e) {
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
	@RequestMapping(value = "getccApprovalRecord", method = { RequestMethod.GET, RequestMethod.POST })
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
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
			approval.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
			Map<String, Object> params = new HashMap<String, Object>();
			String flowName = HttpRequestUtil.getInst().getString("flowName");
			String userName = HttpRequestUtil.getInst().getString("userName");
			String createDate = HttpRequestUtil.getInst().getString("createDate");
			String status = HttpRequestUtil.getInst().getString("status");
			if (StringUtils.isNotEmpty(flowName)) {
				params.put("flowNameLike", flowName);
			}
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
			responseResult.setValue(approvalService.getApprovalCcRecord(approval, params, pageIndex, pageSize)
					.getData());
		} catch (Exception e) {
			responseResult.setStatus(-1);
			responseResult.setErrorMsg("获取审批记录失败!");
		}
		return responseResult;
	}

	@Override
	protected String getPrefix() {
		return super.getPrefix() + "mobile/approval/";
	}
}
