package com.vyiyun.common.weixin.controller.manage;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import com.google.common.collect.Maps;
import com.vyiyun.common.weixin.entity.Appraisal;
import com.vyiyun.common.weixin.service.IAppraisalService;
import com.vyiyun.weixin.controller.AbstWebController;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.DataResult;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyy.weixin.annotation.Suite;

/**
 * @ClassName: AppraisalMController
 * @Description:员工评价管理请求处理器
 * @author zb.shen
 * @date 2016年1月12日
 */
@Controller
@RequestMapping(value = "/manage/appraisal")
@Suite("OA")
public class AppraisalMController extends AbstWebController {
	private static final Logger LOGGER = Logger.getLogger(AppraisalMController.class);

	/**
	 * 评价服务
	 */
	@Autowired
	private IAppraisalService appraisalService;

	/*
	 * 视图控制
	 */
	/**
	 * 员工评价列表页面
	 * @return 页面视图
	 */
	@RequestMapping(value = "appraisalList", method = RequestMethod.GET)
	public ModelAndView appraisalListView() {
		ModelAndView modelAndView = super.createModelAndView("appraisalList");
		return modelAndView;
	}

	/**
	 * 评价详情页面
	 * @return 页面视图
	 */
	@RequestMapping(value = "appraisalDetail", method = RequestMethod.GET)
	public ModelAndView appraisalDetailView() {
		ModelAndView modelAndView = super.createModelAndView("appraisalDetail");
		String id = HttpRequestUtil.getInst().getString("id"); // 评价ID
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
		modelAndView.addObject("data", appraisalService.getAppraisalDetailData(id, corpId));
		return modelAndView;
	}

	/**
	 * 人员评价详情页面
	 * @return 页面视图
	 */
	@RequestMapping(value = "userDetail", method = RequestMethod.GET)
	public ModelAndView userDetailView() {
		ModelAndView modelAndView = super.createModelAndView("userDetail");
		String id = HttpRequestUtil.getInst().getString("id"); // 评价ID
		String accountId = HttpRequestUtil.getInst().getString("accountId"); // 人员ID
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
		Map<String, Object> userDetailData = appraisalService.getUserDetailData(id, accountId, corpId);
		userDetailData.put("total", HttpRequestUtil.getInst().getString("total"));
		modelAndView.addObject("data", userDetailData);
		return modelAndView;
	}

	/*
	 * 数据控制
	 */
	/**
	 * 查询评价列表数据
	 * @return 评价列表数据
	 */
	@RequestMapping(value = "queryAppraisalList", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object queryAppraisalListData() {

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
			params.put("themeLike", jsonSearchConditions.getString("theme"));
			params.put("startDate", jsonSearchConditions.getDate("startTime"));
			// BUG#179修改  by zb.shen 2016-01-25 start.
			Date endDate = jsonSearchConditions.getDate("endTime");
			if (null != endDate) {
				endDate.setTime(endDate.getTime() + 24 * 3600 * 1000 - 1);
			}
			params.put("endDate", endDate);
			// BUG#179修改  by zb.shen 2016-01-25 end.
			// BUG #200 修改 by zb.shen 2016-01-22 start.
			params.put("orderBy", "CreateTime DESC");
			// BUG #200 修改 by zb.shen 2016-01-22 end.
			Appraisal appraisal = new Appraisal();
			appraisal.setStatus(jsonSearchConditions.getString("status"));
//			appraisal.setTheme(jsonSearchConditions.getString("theme"));
			appraisal.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			// 查询列表数据 
			DataResult dataResult = appraisalService.getAppraisalRecord(appraisal, params, pageIndex, pageSize);
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
	 * 查询人员评价排名数据
	 * @return 人员评价排名数据
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "queryUserRankingList", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object queryUserRankingListData() {

		Map<String, Object> dataMap = new HashMap<String, Object>();
		try {
			String id = HttpRequestUtil.getInst().getString("id"); // 评价ID
			if (StringUtil.isBlank(id)) {
				throw new VyiyunException("评价ID不能为空！");
			}
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

			Map<String, Object> params = Maps.newHashMap();
			params.put("appraisalId", id);
			params.put("corpId", HttpRequestUtil.getInst().getCurrentCorpId());
			// 查询列表数据 
			DataResult dataResult = appraisalService.getEmployeeRankingRecord(params, pageIndex, pageSize);
			List<Map<String, Object>> dataList = (List<Map<String, Object>>) dataResult.getData();
			if (!CollectionUtils.isEmpty(dataList)) {
				Iterator<Map<String, Object>> iter = dataList.iterator();
				int rank = 0;
				while (iter.hasNext()) {
					Map<String, Object> map = iter.next();
					if (map.containsKey("rank") && null != map.get("rank")) {
						rank = (int) Float.parseFloat(StringUtil.getString(map.get("rank")));
					} else {
						map.put("rank", (rank + 1));
					}
				} 
			}
			dataMap.put("sEcho", HttpRequestUtil.getInst().getString("sEcho"));
			dataMap.put("iTotalRecords", dataResult.getTotal());
			dataMap.put("iTotalDisplayRecords", dataResult.getTotal());
			dataMap.put("data", dataResult.getData());
			return dataMap;
		} catch (Exception e) {
			dataMap.put("status", -1);
			if (e instanceof VyiyunException) {
				dataMap.put("errorMsg", e.getMessage());
			} else {
				dataMap.put("errorMsg", "数据获取异常!");
			}
			LOGGER.error(e.getMessage(), e);
		}
		return dataMap;
	}

	/**
	 * 导出员工评价数据
	 * @return excel文件字节流
	 */
	@RequestMapping(value = "exportAppraisalListToExcel", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<byte[]> exportAppraisalListToExcel() {
		// 获取查询条件
		String searchConditions = HttpRequestUtil.getInst().getString("searchConditions");
		try {
			JSONObject jsonSearchConditions = JSONObject.parseObject(searchConditions);
			// 导出员工评价数据 
			jsonSearchConditions.put("corpId", HttpRequestUtil.getInst().getCurrentCorpId());
			return appraisalService.exportAppraisalListToExcel(jsonSearchConditions);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("导出数据异常!", e);
		}
		return null;
		
	}

	@Override
	protected String getPrefix() {
		return super.getPrefix() + "manage/appraisal/";
	}
}
