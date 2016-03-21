package com.vyiyun.common.weixin.controller.manage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.entity.Approval;
import com.vyiyun.common.weixin.enums.CommonAppType;
import com.vyiyun.common.weixin.service.IApprovalService;
import com.vyiyun.weixin.cache.impl.WeixinContactCache;
import com.vyiyun.weixin.controller.AbstWebController;
import com.vyiyun.weixin.model.DataResult;
import com.vyiyun.weixin.model.WeixinUser;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;
import com.vyy.weixin.annotation.Suite;

/**
 * @ClassName: ApprovalMController
 * @Description: 审批管理请求处理器
 * @author zb.shen
 * @date 2016年1月5日
 */
@Controller
@RequestMapping(value = "/manage/approval")
@Suite("OA")
public class ApprovalMController extends AbstWebController {

	/**
	 * 审批服务
	 */
	@Autowired
	private IApprovalService approvalService;

	/*
	 * 视图控制
	 */
	/**
	 * 审批列表页面
	 * @return 页面视图
	 */
	@RequestMapping(value = "approvalList", method = RequestMethod.GET)
	public ModelAndView approvalListView() {
		ModelAndView modelAndView = super.createModelAndView("approvalList");
		return modelAndView;
	}

	/**
	 * 审批详情页面
	 * @return 页面视图
	 */
	@RequestMapping(value = "approvalDetail", method = RequestMethod.GET)
	public ModelAndView approvalDetailView() {
		ModelAndView modelAndView = super.createModelAndView("approvalDetail");
		// 审批详情取得
		Map<String, Object> approvalData = approvalService.getApprovalDataById(HttpRequestUtil.getInst().getString("id"));
		Approval approval = (Approval)approvalData.get("approvalInfo");
		WeixinUser user = ((WeixinContactCache<Object>)SystemCacheUtil.getInstance().getWeixinContactCache()).getUserById(approval.getUserId());
		approvalData.put("avatar", user != null ? user.getAvatar() : "");
		modelAndView.addObject("approvalData", approvalData);
		return modelAndView;
	}

	/*
	 * 数据控制
	 */
	/**
	 * 查询审批数据
	 * @return 审批数据
	 */
	@RequestMapping(value = "queryApprovalList", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object queryApprovalListData() {
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
			params.put("flowNameLike", jsonSearchConditions.getString("flowName"));
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
			// BUG#108修改  by zb.shen 2016-01-21 start.
			params.put("excludedStatus", CommonAppType.Status.草稿.value());
			// BUG#108修改  by zb.shen 2016-01-21 end.
			Approval approval = new Approval();
			approval.setStatus(jsonSearchConditions.getString("status"));
//			approval.setFlowName(jsonSearchConditions.getString("flowName"));
			approval.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			// 查询列表数据
			DataResult dataResult = approvalService.queryApprovalRecord(approval, params, pageIndex, pageSize);
			dataMap.put("sEcho", HttpRequestUtil.getInst().getString("sEcho"));
			dataMap.put("iTotalRecords", dataResult.getTotal());
			dataMap.put("iTotalDisplayRecords", dataResult.getTotal());
			dataMap.put("data", dataResult.getData());
			return dataMap;
		} catch (Exception e) {
			dataMap.put("status", -1);
			dataMap.put("errorMsg", "数据获取异常!");
		}
		return dataMap;
		
	}

	/**
	 * 导出审批数据
	 * @return excel文件字节流
	 */
	@RequestMapping(value = "exportApprovalListToExcel", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<byte[]> exportApprovalListToExcel() {
		// 获取查询条件
		String searchConditions = HttpRequestUtil.getInst().getString("searchConditions");
		try {
			JSONObject jsonSearchConditions = JSONObject.parseObject(searchConditions);
			// 导出审批数据
			jsonSearchConditions.put("corpId", HttpRequestUtil.getInst().getCurrentCorpId());
			return approvalService.exportApprovalListToExcel(jsonSearchConditions);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	@Override
	protected String getPrefix() {
		return super.getPrefix() + "manage/approval/";
	}
}
