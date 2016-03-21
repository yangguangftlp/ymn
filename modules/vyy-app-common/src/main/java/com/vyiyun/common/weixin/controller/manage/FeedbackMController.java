package com.vyiyun.common.weixin.controller.manage;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.vyiyun.common.weixin.entity.EntityProgress;
import com.vyiyun.common.weixin.entity.Feedback;
import com.vyiyun.common.weixin.enums.CommonAppType;
import com.vyiyun.common.weixin.service.IEntityProgressService;
import com.vyiyun.common.weixin.service.IFeedbackService;
import com.vyiyun.weixin.controller.AbstWebController;
import com.vyiyun.weixin.entity.Accessory;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.DataResult;
import com.vyiyun.weixin.service.IAccessoryService;
import com.vyiyun.weixin.utils.CommonUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.SpringContextHolder;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyiyun.weixin.web.model.ResponseResult;
import com.vyy.weixin.annotation.Suite;

/**
 * @ClassName: FeedbackMController
 * @Description: 系统反馈管理请求处理器
 * @author zb.shen
 * @date 2016年1月8日
 */
@Controller
@RequestMapping(value = "/manage/feedback")
@Suite("OA")
public class FeedbackMController extends AbstWebController {
	private static final Logger LOGGER = Logger.getLogger(FeedbackMController.class);

	/**
	 * 系统反馈服务
	 */
	@Autowired
	private IFeedbackService feedbackService;

	/**
	 * 实体-附件服务
	 */
	@Autowired
	private IAccessoryService accessoryService;

	/**
	 * 实体-进展服务
	 */
	@Autowired
	private IEntityProgressService entityProgressService;

	/*
	 * 视图控制
	 */
	/**
	 * 反馈列表页面
	 * 
	 * @return 页面视图
	 */
	@RequestMapping(value = "feedbackList", method = RequestMethod.GET)
	public ModelAndView feedbackListView() {
		ModelAndView modelAndView = super.createModelAndView("feedbackList");
		return modelAndView;
	}

	/**
	 * 反馈详情页面
	 * 
	 * @return 页面视图
	 */
	@RequestMapping(value = "feedbackDetail", method = RequestMethod.GET)
	public ModelAndView feedbackDetailView() {
		ModelAndView modelAndView = super.createModelAndView("feedbackDetail");
		modelAndView.addObject("id", HttpRequestUtil.getInst().getString("id"));
		return modelAndView;
	}

	/**
	 * 反馈详情页面Ajax获取数据
	 * 
	 * @return Object
	 */
	@RequestMapping(value = "getFeedbackDetailData", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object getFeedbackDetailData() {
		ResponseResult responseResult = new ResponseResult();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		// 反馈详情取得
		String feedBackId = HttpRequestUtil.getInst().getString("id");
		if (StringUtils.isEmpty(feedBackId)) {
			responseResult.setStatus(-2);
			responseResult.setErrorMsg("id号为空");
			return responseResult;
		}
		Feedback feedback = feedbackService.getFeedbackById(feedBackId);
		if (null != feedback) {
			Map<String, Object> feedbackData = feedback.getPersistentState();
			if (feedback.getStatus().equals(CommonAppType.FeedbackStatus.已解决.value())) {
				feedbackData.put("statusDisplay", "已解决");
			} else {
				feedbackData.put("statusDisplay", "待解决");
			}
			dataMap.put("feedbackInfo", feedbackData);

			// 获取附件记录
			List<Accessory> accessoryList = accessoryService.getAccessoryByEntityId(feedBackId);
			if (!CollectionUtils.isEmpty(accessoryList)) {
				dataMap.put("accessoryInfor", accessoryList);
			}

			// 获取反馈记录
			EntityProgress entityProgress = new EntityProgress();
			entityProgress.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			entityProgress.setEntityId(feedback.getId());
			List<EntityProgress> entityProgressList = entityProgressService.getEntityProgress(entityProgress);
			if (!CollectionUtils.isEmpty(entityProgressList)) {
				dataMap.put("entityProgressList", entityProgressList);
			}

			// 存入responseResult
			responseResult.setValue(dataMap);
		} else {
			responseResult.setStatus(-1);
			responseResult.setErrorMsg("没有数据");
		}
		return responseResult;
	}

	/*
	 * 数据控制
	 */
	/**
	 * 增加修改建议
	 * 
	 * @return 操作结果
	 */
	@RequestMapping(value = "addSuggestion4Revision", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object addSuggestion4Revision() {
		ResponseResult responseResult = new ResponseResult();
		String feedBackId = HttpRequestUtil.getInst().getString("id");
		if (StringUtils.isEmpty(feedBackId)) {
			responseResult.setStatus(-2);
			responseResult.setErrorMsg("id号为空");
			return responseResult;
		}
		String suggestion4Revision = HttpRequestUtil.getInst().getString("suggestion4Revision");
		if (StringUtil.isEmpty(suggestion4Revision)) {
			responseResult.setStatus(-1);
			responseResult.setErrorMsg("建议不能为空");
			return responseResult;
		}
		try {
			EntityProgress entityProgress = new EntityProgress();
			entityProgress.setId(CommonUtil.GeneGUID());
			entityProgress.setEntityId(feedBackId);
			entityProgress.setContent(suggestion4Revision);
			entityProgress.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			entityProgress.setCreateTime(new Date());
			entityProgressService.addEntityProgress(entityProgress);
			responseResult.setValue(entityProgress);
		} catch (Exception e) {
			responseResult.setStatus(-3);
			responseResult.setErrorMsg("添加失败");
			return responseResult;
		}
		responseResult.setErrorMsg("添加成功");
		return responseResult;
	}

	/**
	 * 反馈提醒
	 * 
	 * @return 操作结果
	 */
	@RequestMapping(value = "feedbackReminder", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object feedbackReminder() {
		ResponseResult responseResult = new ResponseResult();
		String reminderInfo = HttpRequestUtil.getInst().getString("reminderInfo");
		try {
			// BUG #193修改 by zb.shen 2016-01-21 start.
			JSONObject jsonReminder = JSON.parseObject(reminderInfo);
			jsonReminder.put("corpId", HttpRequestUtil.getInst().getCurrentCorpId());
			responseResult.setValue(feedbackService.feedbackRemind(jsonReminder));
			// BUG #193修改 by zb.shen 2016-01-21 end.
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
	 * 查询反馈数据
	 * 
	 * @return 反馈数据
	 */
	@RequestMapping(value = "queryFeedbackList", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object queryFeedbackListData() {
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
			params.put("startTime", jsonSearchConditions.getDate("startTime"));
			// BUG#179修改  by zb.shen 2016-01-25 start.
			Date endDate = jsonSearchConditions.getDate("endTime");
			if (null != endDate) {
				endDate.setTime(endDate.getTime() + 24 * 3600 * 1000 - 1);
			}
			params.put("endTime", endDate);
			// BUG#179修改  by zb.shen 2016-01-25 end.
			// BUG#200修改  by zb.shen 2016-01-22 start.
			params.put("orderBy", "CreateTime DESC");
			// BUG#200修改  by zb.shen 2016-01-22 end.
			Feedback feedback = new Feedback();
			feedback.setStatus(jsonSearchConditions.getString("status"));
			feedback.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			// 查询列表数据
			DataResult dataResult = feedbackService.getFeedback(feedback, params, pageIndex, pageSize);
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

	@Override
	protected String getPrefix() {
		return super.getPrefix() + "manage/feedback/";
	}
}
