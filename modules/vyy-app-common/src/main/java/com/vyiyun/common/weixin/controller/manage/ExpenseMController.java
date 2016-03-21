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
import com.vyiyun.common.weixin.entity.Expense;
import com.vyiyun.common.weixin.service.IExpenseService;
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
 * @ClassName: ExpenseMController
 * @Description: 报销管理请求处理器
 * @author zb.shen
 * @date 2016年1月5日
 */
@Controller
@RequestMapping(value = "/manage/expense")
@Suite("OA")
public class ExpenseMController extends AbstWebController {
	private static final Logger LOGGER = Logger.getLogger(ExpenseMController.class);

	/**
	 * 系统状态服务
	 */
	@Autowired
	private ISystemStatusService systemStatusService;

	/**
	 * 报销服务
	 */
	@Autowired
	private IExpenseService expenseService;

	/*
	 * 视图控制
	 */
	/**
	 * 设置报销类型页面
	 * @param model
	 * @return 页面视图
	 */
	@RequestMapping(value = "setupCostCategory", method = RequestMethod.GET)
	public ModelAndView setupCostCategoryView() {
		ModelAndView modelAndView = super.createModelAndView("setupCostCategory");
		modelAndView.addObject("costCategoryList", systemStatusService.getSystemStatus(HttpRequestUtil.getInst().getCurrentCorpId(), Constants.COST_CATEGORY));
		return modelAndView;
	}

	/**
	 * 报销列表页面
	 * @return 页面视图
	 */
	@RequestMapping(value = "expenseList", method = RequestMethod.GET)
	public ModelAndView expenseListView() {
		ModelAndView modelAndView = super.createModelAndView("expenseList");
		return modelAndView;
	}

	/**
	 * 报销详情页面
	 * @return 页面视图
	 */
	@RequestMapping(value = "expenseDetail", method = RequestMethod.GET)
	public ModelAndView expenseDetailView() {
		ModelAndView modelAndView = super.createModelAndView("expenseDetail");
		// 报销详情取得
		Map<String, Object> expenseData = expenseService.getExpenseDataById(HttpRequestUtil.getInst().getString("id"));
		Expense expense = (Expense)expenseData.get("expenseInfo");
		WeixinUser user = ((WeixinContactCache<Object>)SystemCacheUtil.getInstance().getWeixinContactCache()).getUserById(expense.getUserId());
		expenseData.put("avatar", user.getAvatar());
		modelAndView.addObject("expenseData", expenseData);
		return modelAndView;
	}

	/*
	 * 数据控制
	 */
	/**
	 * 修改报销费用类型
	 * @return 操作结果
	 */
	@RequestMapping(value = "modifyCostCategory", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object modifyCostCategory() {
		ResponseResult responseResult = new ResponseResult();
		try {
			// 报销费用类型JSON字符串
			String costCategoryStr = HttpRequestUtil.getInst().getString("costCategory");
			SystemStatus systemStatus = JSONObject.parseObject(costCategoryStr, SystemStatus.class);
			String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
			if (StringUtil.isBlank(systemStatus.getId())) {
				// 新增报销费用类型
				systemStatus.setId(CommonUtil.GeneGUID());
				systemStatus.setType(Constants.COST_CATEGORY);
				systemStatus.setCorpId(corpId);
				systemStatusService.addSystemStatus(systemStatus);
			} else {
				// 修改报销费用类型
				systemStatus.setCorpId(corpId);
				systemStatusService.updateSystemStatus(systemStatus);
			}
			// 删除缓存
			SystemCacheUtil.getInstance().getSystemStatusCache().remove(corpId + "_" + Constants.COST_CATEGORY);
			responseResult.setValue(systemStatus.getId());
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 删除报销费用类型
	 * @return 操作结果
	 */
	@RequestMapping(value = "deleteCostCategory", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object deleteCostCategory() {
		ResponseResult responseResult = new ResponseResult();
		try {
			// 报销费用类型ID
			String id = HttpRequestUtil.getInst().getString("id");
			if (StringUtil.isBlank(id)) {
				responseResult.setStatus(-1);
				responseResult.setErrorMsg("报销费用类型ID不能为空");
			}
			// 删除报销费用类型
			String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
			SystemStatus param = new SystemStatus();
			param.setId(id);
			param.setCorpId(corpId);
			List<SystemStatus> systemStatusList = systemStatusService.getSystemStatus(param);
			if (!CollectionUtils.isEmpty(systemStatusList)) {
				SystemStatus systemStatus = systemStatusList.get(0);
				String costCategory = systemStatus.getValue();
				int optCnt = expenseService.deleteRelatedRecord(corpId, costCategory);
				optCnt += systemStatusService.deleteBySystemStatusId(id);
				SystemCacheUtil.getInstance().getSystemStatusCache().remove(corpId + "_" + Constants.COST_CATEGORY);;
				// 删除该报销费用类型相关数据
				responseResult.setValue(optCnt);
			} else {
				throw new VyiyunException("该请假类型不存在或已删除");
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof VyiyunException) {
				responseResult.setErrorMsg(e.getMessage());
			}
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 查询报销数据
	 * @return 报销数据
	 */
	@RequestMapping(value = "queryExpenseList", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object queryExpenseListData() {
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
			// BUG#200修改  by zb.shen 2016-01-22 start.
			params.put("orderBy", "CreateTime DESC");
			// BUG#200修改  by zb.shen 2016-01-22 end.
			Expense expense = new Expense();
			expense.setStatus(jsonSearchConditions.getString("status"));
			expense.setExpenseNum(jsonSearchConditions.getString("expenseNum"));
			expense.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			// 查询列表数据
			DataResult dataResult = expenseService.queryExpenseRecord(expense, params, pageIndex, pageSize);
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
	 * 导出报销数据
	 * @return excel文件字节流
	 */
	@RequestMapping(value = "exportExpenseListToExcel", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<byte[]> exportExpenseListToExcel() {
		// 获取查询条件
		String searchConditions = HttpRequestUtil.getInst().getString("searchConditions");
		try {
			JSONObject jsonSearchConditions = JSONObject.parseObject(searchConditions);
			// 导出报销数据
			jsonSearchConditions.put("corpId", HttpRequestUtil.getInst().getCurrentCorpId());
			return expenseService.exportExpenseListToExcel(jsonSearchConditions);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("导出数据异常!", e);
		}
		return null;
		
	}

	@Override
	protected String getPrefix() {
		return super.getPrefix() + "manage/expense/";
	}
}
