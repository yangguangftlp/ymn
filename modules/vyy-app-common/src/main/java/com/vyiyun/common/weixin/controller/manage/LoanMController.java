package com.vyiyun.common.weixin.controller.manage;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.entity.Loan;
import com.vyiyun.common.weixin.service.ILoanService;
import com.vyiyun.weixin.cache.impl.WeixinContactCache;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.controller.AbstWebController;
import com.vyiyun.weixin.entity.SystemStatus;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.DataResult;
import com.vyiyun.weixin.model.WeixinUser;
import com.vyiyun.weixin.service.ISystemStatusService;
import com.vyiyun.weixin.utils.CommonUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;
import com.vyiyun.weixin.web.model.ResponseResult;
import com.vyy.weixin.annotation.Suite;

/**
 * @ClassName: LoanMController
 * @Description:借款管理请求处理器
 * @author zb.shen
 * @date 2016年1月6日
 */
@Controller
@RequestMapping(value = "/manage/loan")
@Suite("OA")
public class LoanMController extends AbstWebController {
	private static final Logger LOGGER = Logger.getLogger(LoanMController.class);

	/**
	 * 系统状态服务
	 */
	@Autowired
	private ISystemStatusService systemStatusService;

	/**
	 * 借款服务
	 */
	@Autowired
	private ILoanService loanService;

	/*
	 * 视图控制
	 */
	/**
	 * 对私借款列表页面
	 * @return 页面视图
	 */
	@RequestMapping(value = "privateLoanList", method = RequestMethod.GET)
	public ModelAndView privateLoanListView() {
		ModelAndView modelAndView = super.createModelAndView("privateLoanList");
		return modelAndView;
	}

	/**
	 * 对私借款详情页面
	 * @return 页面视图
	 */
	@RequestMapping(value = "privateLoanDetail", method = RequestMethod.GET)
	public ModelAndView privateLoanDetailView() {
		ModelAndView modelAndView = super.createModelAndView("privateLoanDetail");
		// 详情数据取得
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
		String id = HttpRequestUtil.getInst().getString("id"); // 借款ID
		modelAndView.addObject("loanData", loanService.getLoanDetail(id, corpId));

		return modelAndView;
	}

	/**
	 * 对公借款列表页面
	 * @return 页面视图
	 */
	@RequestMapping(value = "publicLoanList", method = RequestMethod.GET)
	public ModelAndView publicLoanListView() {
		ModelAndView modelAndView = super.createModelAndView("publicLoanList");
		return modelAndView;
	}

	/**
	 * 对公借款详情页面
	 * @return 页面视图
	 */
	@RequestMapping(value = "publicLoanDetail", method = RequestMethod.GET)
	public ModelAndView publicLoanDetailView() {
		ModelAndView modelAndView = super.createModelAndView("publicLoanDetail");
		// 详情数据取得
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
		String id = HttpRequestUtil.getInst().getString("id"); // 借款ID
		modelAndView.addObject("loanData", loanService.getLoanDetail(id, corpId));

		return modelAndView;
	}

	/**
	 * 设置对公，对私借款财务负责人页面
	 * @return 页面视图
	 */
	@RequestMapping(value = "setupLoanRule", method = RequestMethod.GET)
	public ModelAndView setupLoanRuleView() {
		ModelAndView modelAndView = super.createModelAndView("setupLoanRule");

		// 企业ID
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId();

		List<SystemStatus> privateChargerList = systemStatusService.getSystemStatus(corpId, "Loan1Financial");
//				(List<SystemStatus>) systemStatusCache.get(corpId + "_Loan1Financial");
		List<SystemStatus> publicChargerList = systemStatusService.getSystemStatus(corpId, "Loan2Financial");
//				(List<SystemStatus>) systemStatusCache.get(corpId + "_Loan2Financial");

		WeixinContactCache<Object> weixinContactCache = (WeixinContactCache<Object>) SystemCacheUtil.getInstance().getWeixinContactCache();
		if (!CollectionUtils.isEmpty(privateChargerList)) {
			WeixinUser privateCharger = weixinContactCache.getUserById(privateChargerList.get(0).getValue());
			modelAndView.addObject("privateCharger", privateCharger);
			modelAndView.addObject("privateId", privateChargerList.get(0).getId());
		}
		if (!CollectionUtils.isEmpty(publicChargerList)) {
			WeixinUser publicCharger = weixinContactCache.getUserById(publicChargerList.get(0).getValue());
			modelAndView.addObject("publicCharger", publicCharger);
			modelAndView.addObject("publicId", publicChargerList.get(0).getId());
		}
		return modelAndView;
	}

	/**
	 * 设置请假类型页面
	 * 
	 * @return 页面视图
	 */
	@RequestMapping(value = "setupLoanUse", method = RequestMethod.GET)
	public ModelAndView setupLoanUseView() {
		ModelAndView modelAndView = super.createModelAndView("setupLoanUse");
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
		List<SystemStatus> loanUseList = systemStatusService.getSystemStatus(corpId, Constants.SYSTEM_LOAN_USE);
		if (!CollectionUtils.isEmpty(loanUseList)) {
			modelAndView.addObject("loanUseList", loanUseList);
		}
		return modelAndView;
	}
	/*
	 * 数据控制
	 */
	/**
	 * 修改对公，对私借款财务负责人处理
	 * @return 操作结果
	 */
	@RequestMapping(value = "modifyLoanRule", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object modifyLoanRule() {
		ResponseResult responseResult = new ResponseResult();
		try {
			String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
			// 获取对公，对私借款财务负责人 
			JSONObject jsonLoan1Financial = JSONObject.parseObject(HttpRequestUtil.getInst().getString("Loan1Financial")); // 对私
			JSONObject jsonLoan2Financial = JSONObject.parseObject(HttpRequestUtil.getInst().getString("Loan2Financial")); // 对公

			// 修改对公，对私借款财务负责人
			SystemStatus systemStatus1 = JSONObject.toJavaObject(jsonLoan1Financial, SystemStatus.class);
			systemStatus1.setType("Loan1Financial");
			if (StringUtil.isBlank(systemStatus1.getId())) {
				systemStatus1.setId(CommonUtil.GeneGUID());
				systemStatus1.setCorpId(corpId);
				systemStatusService.addSystemStatus(systemStatus1);
			} else {
				systemStatusService.updateSystemStatus(systemStatus1);
			}
			SystemCacheUtil.getInstance().getSystemStatusCache().remove(corpId + "_Loan1Financial");

			SystemStatus systemStatus2 = JSONObject.toJavaObject(jsonLoan2Financial, SystemStatus.class);
			systemStatus2.setType("Loan2Financial");
			if (StringUtil.isBlank(systemStatus2.getId())) {
				systemStatus2.setId(CommonUtil.GeneGUID());
				systemStatus2.setCorpId(corpId);
				systemStatusService.addSystemStatus(systemStatus2);
			} else {
				systemStatusService.updateSystemStatus(systemStatus2);
			}
			SystemCacheUtil.getInstance().getSystemStatusCache().remove(corpId + "_Loan2Financial");

			responseResult.setValue(true);
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 查询借款列表数据
	 * @return 考勤列表数据
	 */
	@RequestMapping(value = "queryLoanList", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object queryLoanListData() {

		Map<String, Object> dataMap = new HashMap<String, Object>();
		// 获取查询条件
		String searchConditions = HttpRequestUtil.getInst().getString("searchConditions");
		try {
			JSONObject jsonSearchConditions = JSONObject.parseObject(searchConditions);
			String sPageIndex = HttpRequestUtil.getInst().getString("start"); // 页码
			String sPageSize = HttpRequestUtil.getInst().getString("length"); // 页面大小
			int pageIndex = -1;
			int pageSize = -1;
			if (StringUtils.isNotEmpty(sPageSize) && sPageSize.matches("\\d+")) {
				pageSize = Integer.parseInt(sPageSize);
			}
			if (StringUtils.isNotEmpty(sPageIndex) && sPageIndex.matches("\\d+")) {
				pageIndex = Integer.parseInt(sPageIndex);
				if (pageIndex == 0) {
					pageIndex = 1;
				} else {
					pageIndex = pageIndex / pageSize + 1;
				}
			}
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userNameLike", jsonSearchConditions.getString("userName"));
			params.put("startDate", jsonSearchConditions.getDate("startTime"));
			// BUG#179修改  by zb.shen 2016-01-25 start.
			Date endDate = jsonSearchConditions.getDate("endTime");
			if (null != endDate) {
				endDate.setTime(endDate.getTime() + 24 * 3600 * 1000 - 1);
			}
			params.put("endDate", endDate);
			// BUG#179修改  by zb.shen 2016-01-25 end.
			Loan loan = new Loan();
			loan.setStatus(jsonSearchConditions.getString("status"));
			loan.setLoanNum(jsonSearchConditions.getString("loanNum"));
			loan.setLoanType(jsonSearchConditions.getString("loanType"));
			loan.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			// 查询列表数据 
			DataResult dataResult = loanService.getLoanRecord(loan, params, pageIndex, pageSize);
			dataMap.put("sEcho", HttpRequestUtil.getInst().getString("sEcho"));
			dataMap.put("iTotalRecords", dataResult.getTotal());
			dataMap.put("iTotalDisplayRecords", dataResult.getTotal());
			dataMap.put("data", dataResult.getData());
			return dataMap;
		} catch (Exception e) {
			dataMap.put("status", -1);
			dataMap.put("errorMsg", "数据获取异常!");
			LOGGER.error("数据获取异常!", e);
		}
		return dataMap;
	}

	/**
	 * 导出借款数据
	 * @return excel文件字节流
	 */
	@RequestMapping(value = "exportLoanListToExcel", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<byte[]> exportLoanListToExcel() {
		// 获取查询条件
		String searchConditions = HttpRequestUtil.getInst().getString("searchConditions");
		try {
			JSONObject jsonSearchConditions = JSONObject.parseObject(searchConditions);
			// 导出借款数据 
			jsonSearchConditions.put("corpId", HttpRequestUtil.getInst().getCurrentCorpId());
			return loanService.exportLoanListToExcel(jsonSearchConditions);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("导出数据异常!", e);
		}
		return null;
		
	}

	/**
	 * 修改借款用途
	 * 
	 * @return 操作结果
	 */
	@RequestMapping(value = "modifyLoanUse", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object modifyLoanUse() {
		ResponseResult responseResult = new ResponseResult();
		try {
			// 请假类型JSON字符串
			String loanUseStr = HttpRequestUtil.getInst().getString("loanUse");
			SystemStatus systemStatus = JSONObject.parseObject(loanUseStr, SystemStatus.class);
			String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
			if (StringUtil.isBlank(systemStatus.getId())) {
				// 新增请假类型
				systemStatus.setId(CommonUtil.GeneGUID());
				systemStatus.setCorpId(corpId);
				systemStatus.setType(Constants.SYSTEM_LOAN_USE);
				systemStatusService.addSystemStatus(systemStatus);
				responseResult.setValue(systemStatus.getId());
			} else {				
				// 修改请假类型
				systemStatusService.updateSystemStatus(systemStatus);
				responseResult.setValue(systemStatus.getId());
			}
			// 清除缓存
			SystemCacheUtil.getInstance().getSystemStatusCache().remove(HttpRequestUtil.getInst().getCurrentCorpId() + "_" + Constants.SYSTEM_LOAN_USE);
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 删除请假类型
	 * 
	 * @return 操作结果
	 */
	@RequestMapping(value = "deleteLoanUse", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object deleteLoanUse() {
		ResponseResult responseResult = new ResponseResult();
		try {
			// 请假类型ID
			String id = HttpRequestUtil.getInst().getString("id");
			if (StringUtil.isBlank(id)) {
				responseResult.setStatus(-1);
				responseResult.setErrorMsg("请假类型ID不能为空");
			}
			// 删除请假类型
			String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
			SystemStatus param = new SystemStatus();
			param.setId(id);
			param.setCorpId(corpId);
			List<SystemStatus> systemStatusList = systemStatusService.getSystemStatus(param);
			if (!CollectionUtils.isEmpty(systemStatusList)) {
				SystemStatus systemStatus = systemStatusList.get(0);
				String loanUse = systemStatus.getValue();
				// 删除该请假类型相关数据
				int optCnt = loanService.deleteRelatedRecord(corpId, loanUse);
				optCnt += systemStatusService.deleteBySystemStatusId(id);
				SystemCacheUtil.getInstance().getSystemStatusCache().remove(corpId + "_" + Constants.SYSTEM_LOAN_USE);;
				responseResult.setValue(optCnt);
			} else {
				throw new VyiyunException("该借款用途不存在或已删除");
			}

		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setErrorMsg(e.getMessage());
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	@Override
	protected String getPrefix() {
		return super.getPrefix() + "manage/loan/";
	}
}
