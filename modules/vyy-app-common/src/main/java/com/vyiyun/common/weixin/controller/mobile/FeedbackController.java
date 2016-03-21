/**
 * 
 */
package com.vyiyun.common.weixin.controller.mobile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.vyiyun.common.weixin.entity.EntityProgress;
import com.vyiyun.common.weixin.entity.Feedback;
import com.vyiyun.common.weixin.service.IEntityProgressService;
import com.vyiyun.common.weixin.service.IFeedbackService;
import com.vyiyun.weixin.controller.AbstWebController;
import com.vyiyun.weixin.entity.Accessory;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.service.IAccessoryService;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.SpringContextHolder;
import com.vyiyun.weixin.web.model.ResponseResult;
import com.vyy.weixin.annotation.App;
import com.vyy.weixin.annotation.OAuth;
import com.vyy.weixin.annotation.Suite;

/**
 * 系统反馈
 * 
 * @author tf
 * @date 2015年10月8日 上午9:45:00
 */
@Controller
@RequestMapping(value = "/mobile/feedback")
@Suite("2")
@OAuth
@App(id = "feedback")
public class FeedbackController extends AbstWebController {

	@Autowired
	private IFeedbackService feedbackService;
	/**
	 * 附件处理
	 */
	@Autowired
	private IAccessoryService accessoryService;

	@Autowired
	private IEntityProgressService entityProgressService;

	/**
	 * 系统反馈页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "feedbackView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView indexView(HttpServletRequest request) {
		ModelAndView modelView = createModelAndViewWithSign("feedbackView", request);
		/*暂时去除标签，还要改回来*/
		return modelView;
	}

	@RequestMapping(value = "submitFeedback", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object submitFeedback() {
		ResponseResult responseResult = new ResponseResult();
		String feedbackInfo = HttpRequestUtil.getInst().getString("feedbackInfo");
		try {
			responseResult.setValue(feedbackService.submitFeedback(JSON.parseObject(feedbackInfo)));
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
	 * 我的反馈页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "myFeedbackView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView myFeedbackView() {
		ModelAndView modelView = createModelAndView("myFeedbackView");
		return modelView;
	}

	/**
	 * 反馈排行页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "feedbackTopView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView feedbackTopView() {
		ModelAndView modelView = createModelAndView("feedbackTopView");
		modelView.addObject("feedbackTopList", feedbackService.getFeedbackTop("desc", 1, 20).getData());
		return modelView;
	}

	/**
	 * 我的反馈页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "feedbackDetail", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView feedbackDetail(HttpServletRequest request) {
		ModelAndView modelView = createModelAndViewWithSign("feedbackDetail", request);
		/*暂时去除标签，还要改回来*/
		String id = HttpRequestUtil.getInst().getString("id");
		if (StringUtils.isEmpty(id)) {
			throw new VyiyunException("参数id不能为空!");
		}
		Feedback feedback = feedbackService.getFeedbackById(id);
		if (null == feedback) {
			throw new VyiyunException("无效的参数id!");
		}

		modelView.addObject("feedbackInfo", feedback);
		modelView.addObject("flag", (feedback.getEndTime() == null ? "0" : "1"));

		// 3、获取报销附件
		List<Accessory> accessoryList = accessoryService.getAccessoryByEntityId(id);
		if (!CollectionUtils.isEmpty(accessoryList)) {
			modelView.addObject("accessoryInfor", accessoryList);
		}
		// 获取反馈记录
		EntityProgress entityProgress = new EntityProgress();
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
		entityProgress.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
		entityProgress.setEntityId(id);
		List<EntityProgress> entityProgressList = entityProgressService.getEntityProgress(entityProgress);
		if (!CollectionUtils.isEmpty(entityProgressList)) {
			modelView.addObject("entityProgressList", entityProgressList);
		}
		return modelView;
	}

	@RequestMapping(value = "doFeedbackOperate", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object doFeedbackOperate() {
		ResponseResult responseResult = new ResponseResult();
		String feedbackData = HttpRequestUtil.getInst().getString("feedbackInfo");
		try {
			responseResult.setValue(feedbackService.doFeedbackOperate(JSON.parseObject(feedbackData)));
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
	 * 我的反馈记录
	 * 
	 * @return
	 */
	@RequestMapping(value = "getFeedbackRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getFeedRecord() {
		// 响应数据
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

			Feedback systemFeed = new Feedback();
			// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
			systemFeed.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
			systemFeed.setUserId(HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid());
			// systemFeed.setStatus(operationType);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("operationType", operationType);
			params.put("orderBy", "CreateTime desc");
			responseResult.setValue(feedbackService.getFeedback(systemFeed, params, pageIndex, pageSize));
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 我的反馈记录
	 * 
	 * @return
	 */
	@RequestMapping(value = "getSysteFeedTop", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getQuestionTop() {
		// 响应数据
		ResponseResult responseResult = new ResponseResult();
		try {
			responseResult.setValue(feedbackService.getFeedbackTop("desc", 1, 20));
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

	@Override
	protected String getPrefix() {
		return "jsp/mobile/feedback/";
	}
}
