/**
 * 
 */
package com.vyiyun.common.weixin.controller.mobile;

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
import com.vyiyun.common.weixin.entity.Loan;
import com.vyiyun.common.weixin.enums.CommonAppType;
import com.vyiyun.common.weixin.service.ILoanService;
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
import com.vyiyun.weixin.service.IEntityAccountService;
import com.vyiyun.weixin.service.ISystemStatusService;
import com.vyiyun.weixin.utils.DateUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.SpringContextHolder;
import com.vyiyun.weixin.utils.SystemCacheUtil;
import com.vyiyun.weixin.web.model.ResponseResult;
import com.vyy.weixin.annotation.App;
import com.vyy.weixin.annotation.OAuth;
import com.vyy.weixin.annotation.Suite;

/**
 * 借款
 * 
 * @author tf
 * @date 2015年10月30日 下午2:12:31
 */
@Controller
@RequestMapping(value = "/mobile/loan")
@Suite("3")
@OAuth
@App(id = "loan")
public class LoanController extends AbstWebController {

	private static final Logger LOGGER = Logger.getLogger(LoanController.class);

	@Autowired
	private ILoanService loanService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IEntityAccountService entityAccountService;
	@Autowired
	private ISystemStatusService systemStatusService;

	// 私有
	@RequestMapping(value = "privateLoanView", method = { RequestMethod.GET })
	public ModelAndView privateLoanView(HttpServletRequest request) {
		ModelAndView modelView = createModelAndViewWithSign("privateLoan", request);
		String id = HttpRequestUtil.getInst().getString("id");
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
		modelView.addObject("flag", HttpRequestUtil.getInst().get("flag"));
		Loan loan = new Loan();
		if (StringUtils.isNotEmpty(id)) {
			loan = loanService.getLoanById(id);
			if (null == loan) {
				throw new VyiyunBusinessException("记录已被撤销或不存在!");
			}
			// 附件
			List<Accessory> accessoryList = accessoryService.getAccessoryByEntityId(id);
			if (!CollectionUtils.isEmpty(accessoryList)) {
				modelView.addObject("accessoryInfor", accessoryList);
			}
		} else {
			loan.setLoanType("0");
			WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
			loan.setUserName(weixinUser.getName());
			WeixinContactCache<Object> weixinContactCache = (com.vyiyun.weixin.cache.impl.WeixinContactCache<Object>) SystemCacheUtil
					.getInstance().getWeixinContactCache();
			loan.setDepartment(weixinContactCache.getUserDept(weixinUser.getDepartment()));
		}
		modelView.addObject("loan", loan);
		// BUG #62 修改 zb.shen 2015年12月25日 start.
		modelView.addObject("flag", HttpRequestUtil.getInst().getString("flag"));
		// BUG #62 修改 zb.shen 2015年12月25日 end.
		return modelView;
	}

	// 公共
	@RequestMapping(value = "publicLoanView", method = { RequestMethod.GET })
	public ModelAndView publicLoanView(HttpServletRequest request) {
		ModelAndView modelView = createModelAndViewWithSign("publicLoan", request);
		modelView.addObject("loanType", "1");
		modelView.addObject("flag", HttpRequestUtil.getInst().get("flag"));
		String id = HttpRequestUtil.getInst().getString("id");
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
		Loan loan = new Loan();
		if (StringUtils.isNotEmpty(id)) {
			loan = loanService.getLoanById(id);
			if (null == loan) {
				throw new VyiyunBusinessException("记录已被撤销或不存在!");
			}
			// 附件
			List<Accessory> accessoryList = accessoryService.getAccessoryByEntityId(id);
			if (!CollectionUtils.isEmpty(accessoryList)) {
				modelView.addObject("accessoryInfor", accessoryList);
			}
		} else {
			WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
			loan.setUserName(weixinUser.getName());
			WeixinContactCache<Object> weixinContactCache = (WeixinContactCache<Object>) SystemCacheUtil.getInstance()
					.getWeixinContactCache();
			loan.setDepartment(weixinContactCache.getUserDept(weixinUser.getDepartment()));

			loan.setLoanType("1");
		}
		modelView.addObject("loan", loan);
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
		List<SystemStatus> systemStatusList = (List<SystemStatus>) systemStatusService.getSystemStatus(corpId, Constants.SYSTEM_LOAN_USE, true);
//		(List<SystemStatus>)SystemCacheUtil.getInstance().getSystemStatusCache()
//				.getSystemStatus(corpId, "LoanUse");
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
		if (!CollectionUtils.isEmpty(systemStatusList)) {
			modelView.addObject("systemStatusList", systemStatusList);
		}
		// BUG #62 修改 zb.shen 2015年12月25日 start.
		modelView.addObject("flag", HttpRequestUtil.getInst().getString("flag"));
		// BUG #62 修改 zb.shen 2015年12月25日 end.
		return modelView;
	}

	// 我的借款
	@RequestMapping(value = "myLoanView", method = { RequestMethod.GET })
	public ModelAndView myLoanView() {
		ModelAndView modelView = createModelAndView("myLoan");
		// BUG #62 修改 zb.shen 2015年12月25日 start.
		modelView.addObject("flag", HttpRequestUtil.getInst().getString("flag"));
		// BUG #62 修改 zb.shen 2015年12月25日 end.
		return modelView;
	}

	// 我的借款
	@RequestMapping(value = "loanDetailView", method = { RequestMethod.GET })
	public ModelAndView loanDetailView(HttpServletRequest request) {
		ModelAndView modelView = createModelAndViewWithSign("loanDetail", request);
		String id = HttpRequestUtil.getInst().getString("id");
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.

		if (StringUtils.isEmpty(id)) {
			throw new VyiyunException(SpringContextHolder.getI18n("1002002", new String[] { "id" }, null));
		}
		Loan loan = loanService.getLoanById(id);
		if (null == loan) {
			throw new VyiyunBusinessException("记录已被撤销或不存在!");
		}
		modelView.addObject("flag", HttpRequestUtil.getInst().get("flag"));
		modelView.addObject("loanType", loan.getLoanType());
		modelView.addObject("id", id);
		modelView.addObject("loan", loan);
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
		modelView.addObject("stautsDisplay",
				SystemCacheUtil.getInstance().getSystemStatusCache().getSystemStatusName("Status", loan.getStatus()));

		modelView.addObject("loanUseDisplay", SystemCacheUtil.getInstance().getSystemStatusCache()
				.getSystemStatusValue(corpId, "LoanUse", loan.getLoanUse()));

		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
		// 审核人 待审核
		EntityAccount entityAccount = new EntityAccount();
		entityAccount.setEntityId(id);
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
		entityAccount.setCorpId(corpId);
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
		entityAccount.setEntityType(CommonAppType.EntityType.JK.value());

		List<EntityAccount> entityAccountList = entityAccountService.getEntityAccount(entityAccount);
		// 参入审核的人 已审核 待审核
		List<EntityAccount> shList = new ArrayList<EntityAccount>();
		// 拒绝整个借款审核过程中只会存在一条拒绝
		EntityAccount refuse = null;
		WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
		boolean isSh = false;
		// 审核人
		// 申请人
		// 财务
		if (!CollectionUtils.isEmpty(entityAccountList)) {
			for (EntityAccount ea : entityAccountList) {
				// 待审核
				shList.add(ea);
				if (CommonAppType.PersonType.SH.value().equals(ea.getPersonType())) {
					if (null == ea.getDealResult() || "0".equals(ea.getDealResult())) {
						isSh = true;
					}
				}
				if (CommonAppType.CommandType.拒绝.value().equals(ea.getDealResult())) {
					refuse = ea;
				}
			}
		}
		if (null != refuse) {
			modelView.addObject("refuse", refuse);
		}

		// 处理 申请人 审核人 财务 等显示
		/*
		 * for (EntityAccount ea : shList) { if
		 * (PersonType.CW.value().equals(ea.getPersonType())) { if (!isSh) { if
		 * ("4".equals(ea.getDealResult()) &&
		 * weixinUser.getUserid().equals(ea.getAccountId())) {
		 * ea.setDealResult("0"); } } } }
		 */
		modelView.addObject("shList", shList);
		modelView.addObject("isSh", isSh);
		// 审核人
		// 附件
		List<Accessory> accessoryList = accessoryService.getAccessoryByEntityId(id);
		if (!CollectionUtils.isEmpty(accessoryList)) {
			modelView.addObject("accessoryInfor", accessoryList);
		}
		if (weixinUser.getUserid().equals(loan.getUserId())) {
			// 4、判断当前人是否为报销者且报销状态审核中 即 报销人可以催办
			if (!CommonAppType.Status.已通过.value().equals(loan.getStatus())) {
				modelView.addObject("userType", "0");
			}
		}
		// BUG #62 修改 zb.shen 2015年12月24日 start.
		modelView.addObject("flag", HttpRequestUtil.getInst().getString("flag"));
		// BUG #62 修改 zb.shen 2015年12月24日 end.
		return modelView;
	}

	// 我的待审批
	@RequestMapping(value = "shAuditView", method = { RequestMethod.GET })
	public ModelAndView shAuditView() {
		ModelAndView modelView = createModelAndView("shAudit");
		return modelView;
	}

	// 我的审批
	@RequestMapping(value = "shAuditDetailView", method = { RequestMethod.GET })
	public ModelAndView shAuditDetailView(HttpServletRequest request) {
		ModelAndView modelView = createModelAndViewWithSign("shAudit_detail", request);
		String id = HttpRequestUtil.getInst().getString("id");
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
		if (StringUtils.isEmpty(id)) {
			throw new VyiyunException(SpringContextHolder.getI18n("1002002", new String[] { "id" }, null));
		}
		Loan loan = loanService.getLoanById(id);
		if (null == loan) {
			throw new VyiyunBusinessException("记录已被撤销或不存在!");
		}
		if (CommonAppType.Status.审核退回.value().equals(loan.getStatus())
				|| CommonAppType.Status.借款退回.value().equals(loan.getStatus())
				|| CommonAppType.Status.已通过.value().equals(loan.getStatus())) {
			request.setAttribute("flag", "1");
			return loanDetailView(request);
		}
		modelView.addObject("loan", loan);
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
		modelView.addObject(
				"loanUseDisplay",
				SystemCacheUtil.getInstance().getSystemStatusCache()
						.getSystemStatusValue(corpId, "LoanUse", loan.getLoanUse()));

		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
		WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
		// 审核人 待审核
		EntityAccount entityAccount = new EntityAccount();
		entityAccount.setEntityId(id);
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
		entityAccount.setCorpId(corpId);
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
		entityAccount.setEntityType(CommonAppType.EntityType.JK.value());
		List<EntityAccount> entityAccountList = entityAccountService.getEntityAccount(entityAccount);
		// 参入审核的人 已审核 待审核
		List<EntityAccount> shList = new ArrayList<EntityAccount>();
		// 当前审核人
		EntityAccount currentEA = null;
		if (!CollectionUtils.isEmpty(entityAccountList)) {
			for (EntityAccount ea : entityAccountList) {

				// 待审核
				if (CommonAppType.PersonType.SH.value().equals(ea.getPersonType())) {
					if (null == ea.getDealResult() || "0".equals(ea.getDealResult())) {
						if (weixinUser.getUserid().equals(ea.getAccountId())) {
							currentEA = ea;
						}
					} else {
						shList.add(ea);
					}
				} else {
					shList.add(ea);
				}
			}
		}
		if (null != currentEA) {
			modelView.addObject("currentEA", currentEA);
			shList.add(currentEA);
		}
		// 审核人 包括已审核
		modelView.addObject("shList", shList);
		List<Accessory> accessoryList = accessoryService.getAccessoryByEntityId(id);
		if (!CollectionUtils.isEmpty(accessoryList)) {
			modelView.addObject("accessoryInfor", accessoryList);
		}
		// BUG #62 修改 zb.shen 2015年12月24日 start.
		modelView.addObject("flag", HttpRequestUtil.getInst().getString("flag"));
		// BUG #62 修改 zb.shen 2015年12月24日 end.
		return modelView;
	}

	// 财务处理
	@RequestMapping(value = "cwAuditView", method = { RequestMethod.GET })
	public ModelAndView cwAuditView() {
		ModelAndView modelView = createModelAndView("cwAudit");
		// BUG #62 修改 zb.shen 2015年12月25日 start.
		modelView.addObject("flag", HttpRequestUtil.getInst().getString("flag"));
		// BUG #62 修改 zb.shen 2015年12月25日 end.
		return modelView;
	}

	// 我的审批
	@RequestMapping(value = "cwAuditDetailView", method = { RequestMethod.GET })
	public ModelAndView cwAuditDetailView(HttpServletRequest request) {
		ModelAndView modelView = createModelAndViewWithSign("cwAudit_detail", request);
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
		String id = HttpRequestUtil.getInst().getString("id");
		if (StringUtils.isEmpty(id)) {
			throw new VyiyunException(SpringContextHolder.getI18n("1002002", new String[] { "id" }, null));
		}
		Loan loan = loanService.getLoanById(id);
		if (null == loan) {
			throw new VyiyunBusinessException("记录已被撤销或不存在!");
		}

		if (CommonAppType.Status.审核退回.value().equals(loan.getStatus())
				|| CommonAppType.Status.借款退回.value().equals(loan.getStatus())
				|| CommonAppType.Status.已通过.value().equals(loan.getStatus())) {
			request.setAttribute("flag", "1");
			return loanDetailView(request);
		}

		modelView.addObject("loan", loan);
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
		modelView.addObject(
				"loanUseDisplay",
				SystemCacheUtil.getInstance().getSystemStatusCache()
						.getSystemStatusValue(corpId, "LoanUse", loan.getLoanUse()));

		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
		WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
		// 审核人 待审核
		EntityAccount entityAccount = new EntityAccount();
		entityAccount.setEntityId(id);
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
		entityAccount.setCorpId(corpId);
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
		entityAccount.setEntityType(CommonAppType.EntityType.JK.value());
		List<EntityAccount> entityAccountList = entityAccountService.getEntityAccount(entityAccount);
		// 参入审核的人 已审核 待审核
		List<EntityAccount> shList = new ArrayList<EntityAccount>();
		// 当前审核人
		EntityAccount currentEA = null;
		// 该字段判断当前是否有存在待审核人
		boolean isSh = false;
		if (!CollectionUtils.isEmpty(entityAccountList)) {
			for (EntityAccount ea : entityAccountList) {
				if (CommonAppType.PersonType.SH.value().endsWith(ea.getPersonType())) {
					// 待审核
					if (null == ea.getDealResult() || "0".equals(ea.getDealResult())) {
						isSh = true;
					}
				} else if (CommonAppType.PersonType.CW.value().endsWith(ea.getPersonType())) {
					if (weixinUser.getUserid().equals(ea.getAccountId())) {
						currentEA = ea;
					}
				}
				shList.add(ea);
			}
		}
		// 审核人 包括已审核
		modelView.addObject("shList", shList);
		modelView.addObject("isSh", isSh);
		if (null != currentEA && !isSh) {
			modelView.addObject("currentEA", currentEA);
		}
		List<Accessory> accessoryList = accessoryService.getAccessoryByEntityId(id);
		if (!CollectionUtils.isEmpty(accessoryList)) {
			modelView.addObject("accessoryInfor", accessoryList);
		}
		// BUG #62 修改 zb.shen 2015年12月24日 start.
		modelView.addObject("flag", HttpRequestUtil.getInst().getString("flag"));
		// BUG #62 修改 zb.shen 2015年12月24日 end.
		return modelView;
	}

	// 发起数据提交
	@RequestMapping(value = "lunchLoan", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object lunchLoan() {
		// 响应数据
		ResponseResult responseResult = new ResponseResult();
		// 项目基本信息
		String launchLoanInfo = HttpRequestUtil.getInst().getString("launchLoanInfo");
		try {
			JSONObject jsonLaunchLoanInfo = JSON.parseObject(launchLoanInfo);
			responseResult.setValue(loanService.launchLoan(jsonLaunchLoanInfo));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("发起借款申请失败!", e);
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

	// 我的借款记录
	@RequestMapping(value = "getLoanRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object myLoanRecord() {
		ResponseResult responseResult = new ResponseResult();
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
		try {
			WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
			Loan loan = new Loan();
			// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
			loan.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
			loan.setUserId(weixinUser.getUserid());
			Map<String, Object> params = new HashMap<String, Object>();

			String operationType = HttpRequestUtil.getInst().getString("operationType");
			if (StringUtils.isEmpty(operationType) || (!"0".equals(operationType) && !"1".equals(operationType))) {
				throw new VyiyunException(SpringContextHolder.getI18n("1002003", new String[] { "operationType",
						operationType }, null));
			}
			params.put("operationType", operationType);

			responseResult.setValue(loanService.getLoanRecord(loan, params, pageIndex, pageSize).getData());
		} catch (Exception e) {
			responseResult.setStatus(-1);
			LOGGER.error("获取借款审核记录失败!", e);
			if (e instanceof VyiyunException) {
				responseResult.setErrorMsg(e.getMessage());
			} else {
				responseResult.setErrorMsg(SpringContextHolder.getI18n("1000000"));
			}
		}
		return responseResult;
	}

	// 我的审核
	@RequestMapping(value = "getShAuditRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getShAuditRecord() {
		ResponseResult responseResult = new ResponseResult();
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
		try {
			WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();

			String userName = HttpRequestUtil.getInst().getString("userName");
			String startDate = HttpRequestUtil.getInst().getString("startDate");
			String endDate = HttpRequestUtil.getInst().getString("endDate");
			String applyDate = HttpRequestUtil.getInst().getString("applyDate");
			String loanNum = HttpRequestUtil.getInst().getString("loanNum");

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("personType", CommonAppType.PersonType.SH.value());
			params.put("userId", weixinUser.getUserid());
			// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
			params.put("corpId", HttpRequestUtil.getInst().getCurrentCorpId());
			// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
			if (StringUtils.isNotEmpty(userName)) {
				params.put("userNameLike", userName);
			}
			if (StringUtils.isNotEmpty(loanNum)) {
				params.put("loanNumLike", loanNum);
			}
			if (StringUtils.isNotEmpty(startDate)) {
				params.put("startDate", DateUtil.stringToDate(startDate, "yyyy-MM-dd"));
			}
			if (StringUtils.isNotEmpty(endDate)) {
				params.put("endDate", DateUtil.stringToDate(endDate, "yyyy-MM-dd"));
			}
			if (StringUtils.isNotEmpty(applyDate)) {
				params.put("applyDate", DateUtil.stringToDate(applyDate, "yyyy-MM-dd"));
			}
			responseResult.setValue(loanService.getAuditRecord(params, pageIndex, pageSize).getData());
		} catch (Exception e) {
			responseResult.setStatus(-1);
			LOGGER.error("获取借款审核记录失败!", e);
			if (e instanceof VyiyunException) {
				responseResult.setErrorMsg(e.getMessage());
			} else {
				responseResult.setErrorMsg(SpringContextHolder.getI18n("1000000"));
			}
		}
		return responseResult;
	}

	// 财务
	@RequestMapping(value = "getCwAuditRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getCwAuditRecord() {
		ResponseResult responseResult = new ResponseResult();
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
		try {
			WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
			String userName = HttpRequestUtil.getInst().getString("userName");
			String startDate = HttpRequestUtil.getInst().getString("startDate");
			String endDate = HttpRequestUtil.getInst().getString("endDate");
			String applyDate = HttpRequestUtil.getInst().getString("applyDate");
			String loanNum = HttpRequestUtil.getInst().getString("loanNum");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("personType", CommonAppType.PersonType.CW.value());
			params.put("userId", weixinUser.getUserid());
			// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
			params.put("corpId", HttpRequestUtil.getInst().getCurrentCorpId());
			// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
			if (StringUtils.isNotEmpty(userName)) {
				params.put("userNameLike", userName);
			}
			if (StringUtils.isNotEmpty(loanNum)) {
				params.put("loanNumLike", loanNum);
			}
			if (StringUtils.isNotEmpty(startDate)) {
				params.put("startDate", DateUtil.stringToDate(startDate, "yyyy-MM-dd"));
			}
			if (StringUtils.isNotEmpty(endDate)) {
				params.put("endDate", DateUtil.stringToDate(endDate, "yyyy-MM-dd"));
			}
			if (StringUtils.isNotEmpty(applyDate)) {
				params.put("applyDate", DateUtil.stringToDate(applyDate, "yyyy-MM-dd"));
			}
			responseResult.setValue(loanService.getAuditRecord(params, pageIndex, pageSize).getData());
		} catch (Exception e) {
			responseResult.setStatus(-1);
			LOGGER.error("获取借款审核记录失败!", e);
			if (e instanceof VyiyunException) {
				responseResult.setErrorMsg(e.getMessage());
			} else {
				responseResult.setErrorMsg(SpringContextHolder.getI18n("1000000"));
			}
		}
		return responseResult;
	}

	@RequestMapping(value = "getShBeAuditRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getShBeAuditRecord() {
		ResponseResult responseResult = new ResponseResult();
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
		try {
			WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
			String userName = HttpRequestUtil.getInst().getString("userName");
			String startDate = HttpRequestUtil.getInst().getString("startDate");
			String endDate = HttpRequestUtil.getInst().getString("endDate");
			String applyDate = HttpRequestUtil.getInst().getString("applyDate");
			String loanNum = HttpRequestUtil.getInst().getString("loanNum");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("personType", CommonAppType.PersonType.SH.value());
			params.put("userId", weixinUser.getUserid());
			// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
			params.put("corpId", HttpRequestUtil.getInst().getCurrentCorpId());
			// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
			if (StringUtils.isNotEmpty(userName)) {
				params.put("userNameLike", userName);
			}
			if (StringUtils.isNotEmpty(loanNum)) {
				params.put("loanNumLike", loanNum);
			}
			if (StringUtils.isNotEmpty(startDate)) {
				params.put("startDate", DateUtil.stringToDate(startDate, "yyyy-MM-dd"));
			}
			if (StringUtils.isNotEmpty(endDate)) {
				params.put("endDate", DateUtil.stringToDate(endDate, "yyyy-MM-dd"));
			}
			if (StringUtils.isNotEmpty(applyDate)) {
				params.put("applyDate", DateUtil.stringToDate(applyDate, "yyyy-MM-dd"));
			}
			responseResult.setValue(loanService.getBeAuditRecord(params, pageIndex, pageSize).getData());
		} catch (Exception e) {
			responseResult.setStatus(-1);
			LOGGER.error("获取借款审核记录失败!", e);
			if (e instanceof VyiyunException) {
				responseResult.setErrorMsg(e.getMessage());
			} else {
				responseResult.setErrorMsg(SpringContextHolder.getI18n("1000000"));
			}
		}
		return responseResult;
	}

	@RequestMapping(value = "getCwBeAuditRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getCwBeAuditRecord() {
		ResponseResult responseResult = new ResponseResult();
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
		try {
			WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
			String userName = HttpRequestUtil.getInst().getString("userName");
			String startDate = HttpRequestUtil.getInst().getString("startDate");
			String endDate = HttpRequestUtil.getInst().getString("endDate");
			String applyDate = HttpRequestUtil.getInst().getString("applyDate");
			String loanNum = HttpRequestUtil.getInst().getString("loanNum");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("personType", CommonAppType.PersonType.CW.value());
			params.put("userId", weixinUser.getUserid());
			// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
			params.put("corpId", HttpRequestUtil.getInst().getCurrentCorpId());
			// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
			params.put("flag", "0");
			if (StringUtils.isNotEmpty(userName)) {
				params.put("userNameLike", userName);
			}
			if (StringUtils.isNotEmpty(loanNum)) {
				params.put("loanNumLike", loanNum);
			}
			if (StringUtils.isNotEmpty(startDate)) {
				params.put("startDate", DateUtil.stringToDate(startDate, "yyyy-MM-dd"));
			}
			if (StringUtils.isNotEmpty(endDate)) {
				params.put("endDate", DateUtil.stringToDate(endDate, "yyyy-MM-dd"));
			}
			if (StringUtils.isNotEmpty(applyDate)) {
				params.put("applyDate", DateUtil.stringToDate(applyDate, "yyyy-MM-dd"));
			}
			responseResult.setValue(loanService.getBeAuditRecord(params, pageIndex, pageSize).getData());
		} catch (Exception e) {
			responseResult.setStatus(-1);
			LOGGER.error("获取借款审核记录失败!", e);
			if (e instanceof VyiyunException) {
				responseResult.setErrorMsg(e.getMessage());
			} else {
				responseResult.setErrorMsg(SpringContextHolder.getI18n("1000000"));
			}
		}
		return responseResult;
	}

	@RequestMapping(value = "loanOperate", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object loanOperate() {
		ResponseResult responseResult = new ResponseResult();
		String loanInfo = HttpRequestUtil.getInst().getString("loanInfo");
		try {
			JSONObject jsonLoanInfo = JSON.parseObject(loanInfo);
			loanService.loanOperate(jsonLoanInfo);
		} catch (Exception e) {
			responseResult.setStatus(-1);
			LOGGER.error("借款操作失败!", e);
			if (e instanceof VyiyunException) {
				responseResult.setErrorMsg(e.getMessage());
			} else {
				responseResult.setErrorMsg(SpringContextHolder.getI18n("1000000"));
			}
		}
		return responseResult;
	}

	@Override
	protected String getPrefix() {
		return "jsp/mobile/loan/";
	}
}
