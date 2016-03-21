package com.vyiyun.common.weixin.controller.mobile;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.entity.Expense;
import com.vyiyun.common.weixin.entity.ExpenseFee;
import com.vyiyun.common.weixin.enums.CommonAppType;
import com.vyiyun.common.weixin.service.IExpenseFeeService;
import com.vyiyun.common.weixin.service.IExpenseService;
import com.vyiyun.weixin.cache.impl.SystemStatusCache;
import com.vyiyun.weixin.cache.impl.WeixinContactCache;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.controller.AbstWebController;
import com.vyiyun.weixin.entity.Accessory;
import com.vyiyun.weixin.entity.EntityAccount;
import com.vyiyun.weixin.entity.SystemStatus;
import com.vyiyun.weixin.exception.VyiyunBusinessException;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.WeixinUser;
import com.vyiyun.weixin.service.IAccessoryService;
import com.vyiyun.weixin.service.IEntityAccessoryService;
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
 * 报销相关请求处理器
 * 
 * @author tf
 * @date 2015年10月23日 下午4:07:54
 */
@Controller
@RequestMapping(value = "/mobile/expense")
@Suite("3")
@OAuth
@App(id = "expense")
public class ExpenseController extends AbstWebController {

	/**
	 * 日志
	 */
	private static final Logger LOGGER = Logger.getLogger(ExpenseController.class);

	@Autowired
	private IExpenseService expenseService;
	@Autowired
	private IEntityAccountService entityAccountService;
	@Autowired
	private IEntityAccessoryService entityAccessoryService;
	@Autowired
	private ISystemStatusService systemStatusService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IExpenseFeeService expenseFeeService;

	/**
	 * 普通审批 --发起审批
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "relaunchExpenseView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView relaunchApprovalView(HttpServletRequest request) {
		ModelAndView modelView = createModelAndViewWithSign("expense", request);
		String expenseId = HttpRequestUtil.getInst().getString("id");
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
		// 删除审核人
		entityAccountService.deleteByEntityId(expenseId);
		// expenseFeeService.deleteExpenseFeeByExpenseId(expenseId);
		entityAccessoryService.deleteEntityAccessory(expenseId);
		if (!StringUtils.isEmpty(expenseId)) {
			Expense expense = expenseService.getExpenseById(expenseId);
			if (null == expense) {
				throw new VyiyunBusinessException("记录已被撤销或不存在!");
			}
			modelView.addObject("expenseInfo", expense);
			ExpenseFee expenseFee = new ExpenseFee();
			expenseFee.setExpenseId(expenseId);
			expenseFee.setCorpId(corpId);
			List<ExpenseFee> expenseFeeList = expenseFeeService.getExpenseFee(expenseFee);
			if (!CollectionUtils.isEmpty(expenseFeeList)) {
				List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
				Map<String, Object> temp = null;
				SystemStatusCache<Object> systemStatusCache = SystemCacheUtil.getInstance().getSystemStatusCache();
				for (ExpenseFee ex : expenseFeeList) {
					temp = ex.getPersistentState();
					temp.put("categoryDisplay",
							systemStatusCache.getSystemStatusValue(corpId, "CostCategory", ex.getCategory()));
					dataMap.add(temp);
				}
				modelView.addObject("expenseFeeList", dataMap);
			}
		}
		List<SystemStatus> costCategoryList = (List<SystemStatus>) systemStatusService.getSystemStatus(corpId,
				Constants.COST_CATEGORY, true);
		// (List<SystemStatus>)
		// SystemCacheUtil.getInstance().getSystemStatusCache()
		// .getSystemStatus(corpId, "CostCategory");
		if (!CollectionUtils.isEmpty(costCategoryList)) {
			modelView.addObject("costCategoryList", costCategoryList);
		}
		return modelView;
	}

	/**
	 * 报销页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "expenseView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView createExpenseView(HttpServletRequest request) {
		ModelAndView modelView = createModelAndViewWithSign("expense", request);
		// 1、获取报销信息
		String id = HttpRequestUtil.getInst().getString("id");
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
		if (!StringUtils.isEmpty(id)) {
			Expense expense = expenseService.getExpenseById(id);
			if (null == expense) {
				throw new VyiyunBusinessException("记录已被撤销或不存在!");
			}
			modelView.addObject("expenseInfo", expense);
			List<Accessory> accessoryList = accessoryService.getAccessoryByEntityId(id);
			if (!CollectionUtils.isEmpty(accessoryList)) {
				modelView.addObject("accessoryInfor", accessoryList);
			}
			ExpenseFee expenseFee = new ExpenseFee();
			expenseFee.setExpenseId(id);
			expenseFee.setCorpId(corpId);
			List<ExpenseFee> expenseFeeList = expenseFeeService.getExpenseFee(expenseFee);
			if (!CollectionUtils.isEmpty(expenseFeeList)) {
				List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
				Map<String, Object> temp = null;
				SystemStatusCache<Object> systemStatusCache = SystemCacheUtil.getInstance().getSystemStatusCache();
				for (ExpenseFee ex : expenseFeeList) {
					temp = ex.getPersistentState();
					temp.put("categoryDisplay",
							systemStatusCache.getSystemStatusValue(corpId, "CostCategory", ex.getCategory()));
					dataMap.add(temp);
				}
				modelView.addObject("expenseFeeList", dataMap);
			}
		} else {
			// 获取报销类别
			Expense expense = new Expense();
			WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
			WeixinContactCache<Object> weixinContactCache = (WeixinContactCache<Object>) SystemCacheUtil.getInstance()
					.getWeixinContactCache();
			expense.setDepartment(weixinContactCache.getUserDept(weixinUser.getDepartment()));
			modelView.addObject("expenseInfo", expense);
		}
		List<SystemStatus> costCategoryList = (List<SystemStatus>) systemStatusService.getSystemStatus(corpId,
				Constants.COST_CATEGORY, true);
		// (List<SystemStatus>)
		// SystemCacheUtil.getInstance().getSystemStatusCache()
		// .getSystemStatus(corpId, "CostCategory");
		if (!CollectionUtils.isEmpty(costCategoryList)) {
			modelView.addObject("costCategoryList", costCategoryList);
		}
		return modelView;
	}

	/**
	 * 报销申请
	 * 
	 * @return
	 */
	@RequestMapping(value = "expenseApply", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object addExpense(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		// 获取请假申请信息
		String jsonExpenseApplyInfo = HttpRequestUtil.getInst().getString("expenseApplyInfo");
		if (StringUtils.isEmpty(jsonExpenseApplyInfo)) {
			throw new VyiyunException("报销申请信息为空!");
		}
		try {
			JSONObject jsonExpenseApplyInfoObj = JSON.parseObject(URLDecoder.decode(jsonExpenseApplyInfo, "UTF-8"));
			String id = jsonExpenseApplyInfoObj.getString("id");
			// 存在id说明是重新发起
			if (StringUtils.isNotEmpty(id)) {
				jsonExpenseApplyInfoObj.put("isAgain", true);
			} else if (StringUtils.isEmpty(id)) {
				id = CommonUtil.GeneGUID();
				jsonExpenseApplyInfoObj.put("id", id);
			}
			responseResult.setValue(id);
			expenseService.doApply(jsonExpenseApplyInfoObj);
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setErrorMsg("请联系系统管理员");
			responseResult.setStatus(-1);
		}
		// 返回数据
		return responseResult;
	}

	/**
	 * 报销信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "expenseDetailView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView expenseDetailView(HttpServletRequest request) {
		ModelAndView modelView = createModelAndViewWithSign("expense_detail", request);
		// 获取请假申请信息
		String id = HttpRequestUtil.getInst().getString("id");
		String flag = HttpRequestUtil.getInst().getString("flag");
		// 这里给详情页面标示 页面来源路径 主要解决审批后退回问题
		if (StringUtils.isNotEmpty(flag)) {
			modelView.addObject("flag", flag);
		}
		// 1、获取报销信息
		Expense expense = expenseService.getExpenseById(id);
		if (null == expense) {
			throw new VyiyunBusinessException("记录已被撤销或不存在!");
		}
		modelView.addObject("expenseInfo", expense);
		modelView
				.addObject(
						"stautsDisplay",
						SystemCacheUtil.getInstance().getSystemStatusCache()
								.getSystemStatusName("Status", expense.getStatus()));
		WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();

		// 2、已审核人 报销人 待审核人 待带报销人
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
		// 获取当前审核人
		EntityAccount entityAccount = new EntityAccount();
		entityAccount.setEntityId(id);
		entityAccount.setEntityType(CommonAppType.EntityType.BX.value());

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderBy", "UpdateTime desc");
		// 判断当前报销审批是否完成
		// 这里会有两条数据
		List<EntityAccount> entityAccountList = (List<EntityAccount>) entityAccountService
				.getEntityAccount(entityAccount);
		// 审核
		List<EntityAccount> auditorList = new ArrayList<EntityAccount>();
		// 财务
		List<EntityAccount> principalList = new ArrayList<EntityAccount>();
		// 权限用户
		List<String> permissionUsers = new ArrayList<String>();
		// 拒绝
		EntityAccount refuse = null;

		if (!CollectionUtils.isEmpty(entityAccountList)) {
			for (EntityAccount ea : entityAccountList) {
				// 获取有效数据
				if (!ea.getInvalid()) {
					if (CommonAppType.PersonType.CW.value().equals(ea.getPersonType())) {
						principalList.add(ea);
						if (CommonAppType.CommandType.退回.value().equals(ea.getDealResult())) {
							refuse = ea;
						}
					} else if (CommonAppType.PersonType.SH.value().equals(ea.getPersonType())) {
						auditorList.add(ea);
					}
				}
				// 后续考虑
				permissionUsers.add(ea.getAccountId());
			}

			modelView.addObject("auditorList", auditorList);
			modelView.addObject("principalList", principalList);
			modelView.addObject("shList", entityAccountList);
		}
		// 3、获取报销附件
		// 处理附件信息
		List<Accessory> accessoryList = accessoryService.getAccessoryByEntityId(id);
		if (!CollectionUtils.isEmpty(accessoryList)) {
			modelView.addObject("accessoryInfor", accessoryList);
		}

		if (weixinUser.getUserid().equals(expense.getUserId())) {
			// 4、判断当前人是否为报销者且报销状态审核中 即 报销人可以催办
			if (!CommonAppType.Status.已报销.value().equals(expense.getStatus())) {
				modelView.addObject("userType", "0");
			}
		}
		// 获取报销费用
		ExpenseFee expenseFee = new ExpenseFee();
		expenseFee.setExpenseId(id);
		List<ExpenseFee> listExpenseFee = expenseFeeService.getExpenseFee(expenseFee);
		if (!CollectionUtils.isEmpty(listExpenseFee)) {
			List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
			Map<String, Object> temp = null;
			SystemStatusCache<Object> systemStatusCache = SystemCacheUtil.getInstance().getSystemStatusCache();
			for (ExpenseFee ex : listExpenseFee) {
				temp = ex.getPersistentState();
				temp.put("categoryDisplay",
						systemStatusCache.getSystemStatusValue(corpId, "CostCategory", ex.getCategory()));
				dataMap.add(temp);
			}
			modelView.addObject("listExpenseFee", dataMap);
		}
		// 批复结果
		// 报销已完成
		if (CommonAppType.Status.已报销.value().equals(expense.getStatus())) {
			modelView.addObject("expenseStatus", CommonAppType.Status.已报销.value());
			// modelView.addObject("remark", principalList.get(0).getRemark());
		} else // 报销已完成
		if (CommonAppType.Status.报销退回.value().equals(expense.getStatus())) {
			// 获取最后审批结果
			modelView.addObject("expenseStatus", CommonAppType.Status.报销退回.value());
			if (null != refuse) {
				modelView.addObject("remark", refuse.getRemark());
			}
		}
		// BUG #62 修改 zb.shen 2015年12月25日 start.
		modelView.addObject("flag", HttpRequestUtil.getInst().getString("flag"));
		// BUG #62 修改 zb.shen 2015年12月25日 end.
		return modelView;
	}

	/**
	 * 报销记录
	 * 
	 * @return
	 */
	@RequestMapping(value = "expenseRecordView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView expenseRecordView() {
		ModelAndView modelView = createModelAndView("expense_record");
		return modelView;
	}

	/**
	 * 获取报销记录
	 * 
	 * @return
	 */
	@RequestMapping(value = "getExpenseRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getExpenseRecord(HttpServletRequest request) {

		ResponseResult responseResult = new ResponseResult();
		try {
			// 获取请假申请信息
			String operationType = HttpRequestUtil.getInst().getString("operationType");
			if (StringUtils.isEmpty(operationType)) {
				responseResult.setStatus(-1);
				responseResult.setErrorMsg("参数operationType不能为空！");
				throw new VyiyunException("无效的operationType值!");
			}
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
			Expense expense = new Expense();
			expense.setUserId(HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid());
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("orderBy", "CreateTime desc");
			params.put("operationType", operationType);
			responseResult.setValue(expenseService.queryExpenseRecord(expense, params, pageIndex, pageSize).getData());
		} catch (Exception e) {
			LOGGER.debug("获取数据失败", e);
			responseResult.setStatus(-1);
			if (e instanceof VyiyunException) {
				responseResult.setErrorMsg(e.getMessage());
			} else {
				responseResult.setErrorMsg("获取数据记录失败,请联系统管理员!");
			}
		}
		return responseResult;
	}

	/**
	 * 审核记录
	 * 
	 * @return
	 */
	@RequestMapping(value = "auditRecordView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView auditRecordView(HttpServletRequest request) {
		ModelAndView modelView = createModelAndView("audit_record");
		String auditType = HttpRequestUtil.getInst().getString("auditType");
		if (StringUtils.isEmpty(auditType)) {
			throw new VyiyunException("无效的auditType值！");
		}
		// 控制是审核人审核 还是财务处理
		if ("1".equals(auditType)) {
			modelView.addObject("personType", "0");
		} else /** 报销财务审核 */
		if ("2".equals(auditType)) {
			modelView.addObject("personType", "2");
		} else {
			throw new VyiyunException("参数auditType值无效!");
		}
		modelView.addObject("auditType", auditType);
		return modelView;
	}

	/**
	 * 获取报销记录
	 * 
	 * @return
	 */
	@RequestMapping(value = "getAuditRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getAuditRecord(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		try {
			// 获取请假申请信息
			String operationType = HttpRequestUtil.getInst().getString("operationType");
			if (StringUtils.isEmpty(operationType)) {
				responseResult.setStatus(-1);
				responseResult.setErrorMsg("参数operationType不能为空！");
				throw new VyiyunException("无效的operationType值!");
			}

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
			Expense expense = new Expense();
			expense.setUserId(weixinUser.getUserid());
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("orderBy", "CreateTime desc");

			String userName = HttpRequestUtil.getInst().getString("userName");
			String startDate = HttpRequestUtil.getInst().getString("startDate");
			String endDate = HttpRequestUtil.getInst().getString("endDate");
			String expenseNum = HttpRequestUtil.getInst().getString("expenseNum");

			String personType = HttpRequestUtil.getInst().getString("personType");

			if (StringUtils.isNotEmpty(userName)) {
				params.put("uNameLike", userName);
			}
			if (StringUtils.isNotEmpty(expenseNum)) {
				params.put("expenseNumLIke", expenseNum);
			}
			if (StringUtils.isNotEmpty(startDate)) {
				params.put("startDate", DateUtil.stringToDate(startDate, "yyyy-MM-dd"));
			}
			if (StringUtils.isNotEmpty(endDate)) {
				params.put("endDate", DateUtil.stringToDate(endDate, "yyyy-MM-dd"));
			}
			if (StringUtils.isNotEmpty(endDate)) {
				params.put("endDate", DateUtil.stringToDate(endDate, "yyyy-MM-dd"));
			}

			if ("0".equals(personType)) {
				if ("1".equals(operationType)) {
					params.put("operationType", "1");
					responseResult.setValue(expenseService.queryAuditRecord(expense, params, pageIndex, pageSize)
							.getData());
				} else if ("2".equals(operationType)) {
					params.put("personType", "0");
					params.put("userId", weixinUser.getUserid());
					responseResult.setValue(entityAccountService.queryEntityAccountExpense(params, pageIndex, pageSize)
							.getData());
				}
			} else if ("2".equals(personType)) {
				if ("1".equals(operationType)) {
					params.put("operationType", "3");
					responseResult.setValue(expenseService.queryAuditRecord(expense, params, pageIndex, pageSize)
							.getData());
				} else if ("2".equals(operationType)) {
					params.put("personType", "2");
					params.put("userId", weixinUser.getUserid());
					responseResult.setValue(entityAccountService.queryEntityAccountExpense(params, pageIndex, pageSize)
							.getData());
				}
			}

		} catch (Exception e) {
			LOGGER.debug("获取数据失败", e);
			responseResult.setStatus(-1);
			if (e instanceof VyiyunException) {
				responseResult.setErrorMsg(e.getMessage());
			} else {
				responseResult.setErrorMsg("获取数据记录失败,请联系统管理员!");
			}
		}
		return responseResult;

	}

	/**
	 * 获取报销记录
	 * 
	 * @return
	 */
	/*
	 * @RequestMapping(value = "getAuditRecord", method = { RequestMethod.GET,
	 * RequestMethod.POST }) public @ResponseBody Object
	 * getAuditRecord(HttpServletRequest request) { ResponseResult
	 * responseResult = new ResponseResult(); try { // 获取请假申请信息 String
	 * operationType = HttpRequestUtil.getInst().getString("operationType"); if
	 * (StringUtils.isEmpty(operationType)) { responseResult.setStatus(-1);
	 * responseResult.setErrorMsg("参数operationType不能为空！"); throw new
	 * VyiyunException("无效的operationType值!"); }
	 * 
	 * int pageIndex = -1; int pageSize = -1; String sPageIndex =
	 * HttpRequestUtil.getInst().getString("pageIndex"); String sPageSize =
	 * HttpRequestUtil.getInst().getString("pageSize"); if
	 * (StringUtils.isNotEmpty(sPageIndex) && sPageIndex.matches("\\d+")) {
	 * pageIndex = Integer.parseInt(sPageIndex); } if
	 * (StringUtils.isNotEmpty(sPageSize) && sPageSize.matches("\\d+")) {
	 * pageSize = Integer.parseInt(sPageSize); } WeixinUser weixinUser =
	 * HttpRequestUtil.getInst().getCurrentWeixinUser(); Expense expense = new
	 * Expense(); expense.setUserId(weixinUser.getUserid()); Map<String, Object>
	 * params = new HashMap<String, Object>(); params.put("orderBy",
	 * "CreateTime desc");
	 * 
	 * if ("1".equals(operationType) || "3".equals(operationType)) {
	 * params.put("operationType", operationType); responseResult
	 * .setValue(expenseService.queryAuditRecord(expense, params, pageIndex,
	 * pageSize).getData()); } else if ("2".equals(operationType)) { Map<String,
	 * Object> dataMap = new HashMap<String, Object>();
	 * dataMap.put("personType", "0"); dataMap.put("userId",
	 * weixinUser.getUserid());
	 * responseResult.setValue(entityAccountService.queryEntityAccountExpense
	 * (dataMap, pageIndex, pageSize) .getData()); } else if
	 * ("4".equals(operationType)) { Map<String, Object> dataMap = new
	 * HashMap<String, Object>(); dataMap.put("personType", "2");
	 * dataMap.put("userId", weixinUser.getUserid());
	 * responseResult.setValue(entityAccountService
	 * .queryEntityAccountExpense(dataMap, pageIndex, pageSize) .getData()); } }
	 * catch (Exception e) { LOGGER.debug("获取数据失败", e);
	 * responseResult.setStatus(-1); if (e instanceof VyiyunException) {
	 * responseResult.setErrorMsg(e.getMessage()); } else {
	 * responseResult.setErrorMsg("获取数据记录失败,请联系统管理员!"); } } return
	 * responseResult;
	 * 
	 * }
	 */

	/**
	 * 审核处理
	 * 
	 * @return
	 */
	@RequestMapping(value = "auditDetailView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView auditDetailView(HttpServletRequest request) {
		ModelAndView modelView = createModelAndViewWithSign("audit_detail", request);
		// 获取请假申请信息
		String id = HttpRequestUtil.getInst().getString("id");
		// 1、获取报销信息
		Expense expense = expenseService.getExpenseById(id);
		if (null == expense) {
			throw new VyiyunBusinessException("记录已被撤销或不存在!");
		}
		// 如果已审核
		if (CommonAppType.Status.已报销.value().equals(expense.getStatus())) {
			return expenseDetailView(request);
		}
		// 获取报销费用
		ExpenseFee expenseFee = new ExpenseFee();
		expenseFee.setExpenseId(id);
		List<ExpenseFee> listExpenseFee = expenseFeeService.getExpenseFee(expenseFee);
		if (!CollectionUtils.isEmpty(listExpenseFee)) {
			List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
			Map<String, Object> temp = null;
			SystemStatusCache<Object> systemStatusCache = SystemCacheUtil.getInstance().getSystemStatusCache();
			for (ExpenseFee ex : listExpenseFee) {
				temp = ex.getPersistentState();
				temp.put("categoryDisplay", systemStatusCache.getSystemStatusValue(HttpRequestUtil.getInst()
						.getCurrentCorpId(), "CostCategory", ex.getCategory()));
				dataMap.add(temp);
			}
			modelView.addObject("listExpenseFee", dataMap);
		}
		// 获取当前审核人
		modelView.addObject("expenseInfo", expense);
		WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
		// 2、已审核人 报销人 待审核人 待带报销人
		// 获取当前审核人
		EntityAccount entityAccount = new EntityAccount();
		entityAccount.setEntityId(id);
		entityAccount.setEntityType(CommonAppType.EntityType.BX.value());
		entityAccount.setInvalid(false);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderBy", "UpdateTime desc");
		// 判断当前报销审批是否完成
		List<EntityAccount> entityAccountList = (List<EntityAccount>) entityAccountService
				.getEntityAccount(entityAccount);
		// 待审核
		StringBuffer alreadyUser = new StringBuffer();
		// 权限用户
		List<EntityAccount> entityAccounts = new ArrayList<EntityAccount>();

		if (!CollectionUtils.isEmpty(entityAccountList)) {
			for (EntityAccount ea : entityAccountList) {
				// 获取有效数据
				if (!ea.getInvalid()) {
					if (CommonAppType.PersonType.SH.value().equals(ea.getPersonType())) {
						if (!ea.getDealResult().equals("0")) {
							alreadyUser.append(",").append(ea.getAccountName());
						} else if (ea.getDealResult().equals("0")) {
							entityAccounts.add(ea);
						}
					} else if (CommonAppType.PersonType.CW.value().equals(ea.getPersonType())) {
						if (!ea.getDealResult().equals("0")) {
							alreadyUser.append(",").append(ea.getAccountName());
						} else if (ea.getDealResult().equals("0")
								&& CommonAppType.Status.待报销.value().equals(expense.getStatus())) {
							entityAccounts.add(ea);
						}
					}
				}
			}

			// 当前审核人必须是一位
			if (entityAccounts.size() == 1) {
				modelView.addObject("entityAccount", entityAccounts.get(0));
				modelView.addObject("auditor", entityAccounts.get(0).getAccountName());
			} else if (entityAccounts.size() > 1) {
				throw new VyiyunException("数据错误，当前审核人必须是一位!");
			} else if (entityAccounts.isEmpty()) {
				return expenseDetailView(request);
			}
			// 当前非审核人
			if (!entityAccounts.get(0).getAccountId().equals(weixinUser.getUserid())) {
				return expenseDetailView(request);
			}
			if (alreadyUser.length() > 0) {
				alreadyUser.deleteCharAt(0);
			}
			modelView.addObject("alreadyUser", alreadyUser);
			modelView.addObject("shList", entityAccountList);
		} else {
			return expenseDetailView(request);
		}
		// 3、获取报销附件
		// 处理附件信息
		List<Accessory> accessoryList = accessoryService.getAccessoryByEntityId(id);
		if (!CollectionUtils.isEmpty(accessoryList)) {
			modelView.addObject("accessoryInfor", accessoryList);
		}
		// BUG #62 修改 zb.shen 2015年12月25日 start.
		modelView.addObject("flag", HttpRequestUtil.getInst().getString("flag"));
		// BUG #62 修改 zb.shen 2015年12月25日 end.
		return modelView;
	}

	/**
	 * 报销审核
	 * 
	 * @return
	 */
	@RequestMapping(value = "expenseAudit", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object expenseAudit(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		// 获取请假申请信息
		String expenseAuditInfo = HttpRequestUtil.getInst().getString("expenseAuditInfo");
		if (StringUtils.isEmpty(expenseAuditInfo)) {
			throw new VyiyunException("报销审核信息为空!");
		}
		try {
			JSONObject jsonExpenseAuditInfo = JSON.parseObject(URLDecoder.decode(expenseAuditInfo, "UTF-8"));
			expenseService.doAudit(jsonExpenseAuditInfo);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("报销审核数据格式错误，data:" + expenseAuditInfo, e);
			responseResult.setStatus(-1);
			if (e instanceof VyiyunException) {
				responseResult.setErrorMsg(e.getMessage());
			} else {
				responseResult.setErrorMsg(SpringContextHolder.getI18n("1000000"));
			}
		}
		// 返回数据
		return responseResult;
	}

	/**
	 * 删除报销费用
	 * 
	 * @return
	 */
	@RequestMapping(value = "deleteExpenseFee", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object deleteExpenseFee(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		// 获取请假申请信息
		String id = HttpRequestUtil.getInst().getString("id");
		if (StringUtils.isEmpty(id)) {
			responseResult.setStatus(-1);
			responseResult.setErrorMsg(SpringContextHolder.getI18n("1002002", new String[] { "id" }, null));
		}
		try {
			expenseFeeService.deleteExpenseFeeById(id);
		} catch (Exception e) {
			LOGGER.error("删除报销费用失败!", e);
			responseResult.setStatus(-1);
			responseResult.setErrorMsg(SpringContextHolder.getI18n("1000000"));
		}
		// 返回数据
		return responseResult;
	}

	@Override
	protected String getPrefix() {
		return super.getPrefix() + "mobile/expense/";
	}
}