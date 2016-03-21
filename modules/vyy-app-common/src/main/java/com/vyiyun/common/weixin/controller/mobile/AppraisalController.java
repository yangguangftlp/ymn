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
import com.vyiyun.common.weixin.entity.Appraisal;
import com.vyiyun.common.weixin.entity.ProblemTemplate;
import com.vyiyun.common.weixin.entity.Score;
import com.vyiyun.common.weixin.enums.CommonAppType;
import com.vyiyun.common.weixin.service.IAppraisalService;
import com.vyiyun.common.weixin.service.IProblemTemplateService;
import com.vyiyun.common.weixin.service.IScoreService;
import com.vyiyun.weixin.controller.AbstWebController;
import com.vyiyun.weixin.entity.EntityAccount;
import com.vyiyun.weixin.exception.VyiyunBusinessException;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.WeixinUser;
import com.vyiyun.weixin.service.IEntityAccountService;
import com.vyiyun.weixin.utils.DateUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.SpringContextHolder;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyiyun.weixin.web.model.ResponseResult;
import com.vyy.weixin.annotation.App;
import com.vyy.weixin.annotation.OAuth;
import com.vyy.weixin.annotation.Suite;

/**
 * 评价应用
 * 
 * @author tf
 * @date 2015年11月16日 下午2:02:58
 */
@Controller
@RequestMapping(value = "/mobile/appraisal")
@Suite("4")
@OAuth
@App(id = "employeeAppraisal")
public class AppraisalController extends AbstWebController {
	private static final Logger LOGGER = Logger.getLogger(LoanController.class);
	/**
	 * 评价服务
	 */
	@Autowired
	private IAppraisalService appraisalService;
	/**
	 * 问题模板服务
	 */
	@Autowired
	private IProblemTemplateService problemTemplateService;
	/**
	 * 人员获取
	 */
	@Autowired
	private IEntityAccountService entityAccountService;
	/**
	 * 评分
	 */
	@Autowired
	private IScoreService scoreService;

	// 发起评价
	@RequestMapping(value = "launchAppraisalView", method = { RequestMethod.GET })
	public ModelAndView launchAppraisalView(HttpServletRequest request) {
		ModelAndView modelAndView = createModelAndView("launchAppraisal");
		return modelAndView;
	}

	// 我的发起
	@RequestMapping(value = "myLaunchView", method = { RequestMethod.GET })
	public ModelAndView myLaunchView(HttpServletRequest request) {
		ModelAndView modelAndView = createModelAndView("myLaunch");
		return modelAndView;
	}

	// 发起结果
	@RequestMapping(value = "launchResultView", method = { RequestMethod.GET })
	public ModelAndView launchResultView(HttpServletRequest request) {
		ModelAndView modelAndView = createModelAndView("launchResult");
		String id = HttpRequestUtil.getInst().getString("id");
		if (StringUtils.isEmpty(id)) {
			throw new VyiyunException(SpringContextHolder.getI18n("1002002", new String[] { "id" }, null));
		}
		Appraisal appraisal = appraisalService.getAppraisalById(id);
		if (null == appraisal) {
			throw new VyiyunException(SpringContextHolder.getI18n("1002003", new String[] { "id", id }, null));
		}

		// 列表来源 1 列表 这里控制 未评价操作成功后跳转
		String referer = HttpRequestUtil.getInst().getString("referer");
		if (StringUtils.isNotEmpty(referer)) {
			modelAndView.addObject("referer", referer);
		}
		modelAndView.addObject("appraisalInfo", appraisal);
		// 获取问题列表
		ProblemTemplate problemTemplate = new ProblemTemplate();
		// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen start.
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
		problemTemplate.setCorpId(corpId);
		// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen end.
		problemTemplate.setAppraisalId(id);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderBy", "px asc");
		List<ProblemTemplate> problemTemplateList = problemTemplateService.getProblemTemplate(problemTemplate, params);
		modelAndView.addObject("problemTemplateList", problemTemplateList);
		// 被评价人
		// 评价人
		EntityAccount entityAccount = new EntityAccount();
		// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen start.
		entityAccount.setCorpId(corpId);
		// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen end.
		entityAccount.setEntityId(id);
		List<EntityAccount> entityAccountList = entityAccountService.getEntityAccount(entityAccount);
		if (!CollectionUtils.isEmpty(entityAccountList)) {
			List<EntityAccount> beAppraisalUser = new ArrayList<EntityAccount>();
			List<EntityAccount> appraisalUser = new ArrayList<EntityAccount>();
			for (EntityAccount ea : entityAccountList) {
				if (CommonAppType.PersonType.BPJ.value().equals(ea.getPersonType())) {
					beAppraisalUser.add(ea);
				} else if (CommonAppType.PersonType.PJ.value().equals(ea.getPersonType())) {
					appraisalUser.add(ea);
				}
			}

			modelAndView.addObject("beAppraisalUser", beAppraisalUser);
			modelAndView.addObject("appraisalUser", appraisalUser);
		}
		WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
		// 判断是否发起人
		if (appraisal.getUserId().equals(weixinUser.getUserid())) {
			modelAndView.addObject("isSponsor", "1");
		}
		return modelAndView;
	}

	// 评价查询
	@RequestMapping(value = "queryAppraisalView", method = { RequestMethod.GET })
	public ModelAndView queryAppraisalView(HttpServletRequest request) {
		ModelAndView modelAndView = createModelAndView("queryAppraisal");
		return modelAndView;
	}

	// 员工排行
	@RequestMapping(value = "employeeRankingView", method = { RequestMethod.GET })
	public ModelAndView employeeRankingView(HttpServletRequest request) {
		ModelAndView modelAndView = createModelAndView("employeeRanking");
		// 评价id
		String id = HttpRequestUtil.getInst().getString("id");
		if (StringUtils.isEmpty(id)) {
			throw new VyiyunException(SpringContextHolder.getI18n("1002002", new String[] { "id" }, null));
		}
		String operateType = HttpRequestUtil.getInst().getString("operateType");
		if ("0".equals(operateType)) {
			WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
			modelAndView.addObject("uId", weixinUser.getUserid());
		}
		modelAndView.addObject("appraisalId", id);
		return modelAndView;
	}

	// 评价结果
	@RequestMapping(value = "appraisalResultView", method = { RequestMethod.GET })
	public ModelAndView appraisalResultView(HttpServletRequest request) {
		ModelAndView modelAndView = createModelAndView("appraisalResult");
		WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
		// 评价id
		String id = HttpRequestUtil.getInst().getString("id");
		String userId = HttpRequestUtil.getInst().getString("userId");
		if (StringUtils.isEmpty(id)) {
			throw new VyiyunException(SpringContextHolder.getI18n("1002002", new String[] { "id" }, null));
		}
		Appraisal appraisal = appraisalService.getAppraisalById(id);
		if (null == appraisal) {
			throw new VyiyunBusinessException("记录被删除或不存在!");
		}
		modelAndView.addObject("appraisalInfo", appraisal);
		if (StringUtils.isEmpty(userId)) {
			throw new VyiyunException(SpringContextHolder.getI18n("1002002", new String[] { "userId" }, null));
		}
		// 获取问题
		ProblemTemplate problemTemplate = new ProblemTemplate();
		// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen start.
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
		problemTemplate.setCorpId(corpId);
		// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen end.
		problemTemplate.setAppraisalId(id);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderBy", "px asc");
		List<ProblemTemplate> problemTemplateList = problemTemplateService.getProblemTemplate(problemTemplate, params);

		params.clear();
		params.put("userId", userId);
		// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen start.
		params.put("corpId", corpId);
		// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen end.
		params.put("appraisalId", id);
		// 获取问题评分
		List<Map<String, Object>> problemScoreMapList = scoreService.getProblemScoreStatistic(params);
		Map<String, List<Map<String, Object>>> scroeDataList = new HashMap<String, List<Map<String, Object>>>();
		if (!CollectionUtils.isEmpty(problemScoreMapList)) {
			String problemId = null;
			for (Map<String, Object> map : problemScoreMapList) {
				problemId = StringUtil.getString(map.get("problemId"));
				if (!scroeDataList.containsKey(problemId)) {
					scroeDataList.put(problemId, new ArrayList<Map<String, Object>>());
				}
				scroeDataList.get(problemId).add(map);
			}
		}
		// params.put("problemId", problemTemplateList.get(0).getId());
		List<Map<String, Object>> opinionMapList = scoreService.getProblemOpinion(params);

		List<Map<String, Object>> problemTemplateMapList = new ArrayList<Map<String, Object>>();
		if (!CollectionUtils.isEmpty(problemTemplateList)) {
			Map<String, Object> temp = null;
			for (ProblemTemplate pt : problemTemplateList) {
				temp = pt.getPersistentState();
				if (scroeDataList.containsKey(pt.getId())) {
					temp.put("scoreData", scroeDataList.get(pt.getId()));
				}
				problemTemplateMapList.add(temp);
			}
		}
		modelAndView.addObject("problemTemplateMapList", problemTemplateMapList);
		// 综合评价
		modelAndView.addObject("opinionMapList", opinionMapList);
		// 这里判断当前是否为被评价人
		EntityAccount entityAccount = new EntityAccount();
		// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen start.
		entityAccount.setCorpId(corpId);
		// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen end.
		entityAccount.setEntityId(id);
		entityAccount.setPersonType(CommonAppType.PersonType.BPJ.value());
		entityAccount.setEntityType(CommonAppType.EntityType.PJ.value());
		entityAccount.setAccountId(weixinUser.getUserid());
		int count = (int) entityAccountService.getEntityAccountCount(entityAccount);
		if (count > 0) {
			// 这里屏蔽被评价查看,综合评价
			modelAndView.addObject("bpj", "0");
		}else {
			modelAndView.addObject("bpj", "1");
		}
		return modelAndView;
	}

	// 待我评价
	@RequestMapping(value = "myAuditView", method = { RequestMethod.GET })
	public ModelAndView auditView(HttpServletRequest request) {
		ModelAndView modelAndView = createModelAndView("myAudit");

		return modelAndView;
	}

	// 评价详情
	@RequestMapping(value = "auditDetailView", method = { RequestMethod.GET })
	public ModelAndView auditDetailView(HttpServletRequest request) {
		ModelAndView modelAndView = createModelAndView("auditDetail");
		String id = HttpRequestUtil.getInst().getString("id");
		if (StringUtils.isEmpty(id)) {
			throw new VyiyunException(SpringContextHolder.getI18n("1002002", new String[] { "id" }, null));
		}
		modelAndView.addObject("appraisalId", id);
		return modelAndView;
	}

	// 进行评价
	@RequestMapping(value = "appraisalView", method = { RequestMethod.GET })
	public ModelAndView appraisalView(HttpServletRequest request) {
		ModelAndView modelAndView = createModelAndView("appraisal");
		String eaId = HttpRequestUtil.getInst().getString("eaId");
		EntityAccount entityAccount = entityAccountService.getEntityAccountById(eaId);
		if (null == entityAccount) {
			throw new VyiyunException(SpringContextHolder.getI18n("1002003", new String[] { "id", eaId }, null));
		}
		String appraisalId = entityAccount.getEntityId();
		Appraisal appraisal = appraisalService.getAppraisalById(appraisalId);
		if (null == appraisal) {
			throw new VyiyunException(SpringContextHolder.getI18n("1002003", new String[] { "appraisalId", appraisalId },
					null));
		}
		modelAndView.addObject("appraisalInfo", appraisal);
		String flag = HttpRequestUtil.getInst().getString("flag");
		if (StringUtils.isNotEmpty(flag)) {
			modelAndView.addObject("flag", flag);
		}
		WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
		Score score = new Score();
		score.setUserId(entityAccount.getAccountId());
		score.setRaterId(weixinUser.getUserid());
		score.setAppraisalId(appraisalId);
		ProblemTemplate problemTemplate = new ProblemTemplate();
		problemTemplate.setAppraisalId(appraisalId);
		Map<String, Object> params = new HashMap<String, Object>();
		// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen start.
		params.put("corpId", HttpRequestUtil.getInst().getCurrentCorpId());
		// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen end.
		params.put("appraisalId", appraisalId);
		params.put("raterId", weixinUser.getUserid());
		params.put("userId", entityAccount.getAccountId());
		List<Map<String, Object>> scoreMapList = scoreService.getProblemScore(params);
		modelAndView.addObject("scoreMapList", scoreMapList);
		modelAndView.addObject("appraisalId", appraisalId);
		modelAndView.addObject("eaId", eaId);
		return modelAndView;
	}

	// 评价详情
	@RequestMapping(value = "appraisalDetailView", method = { RequestMethod.GET })
	public ModelAndView appraisalDetailView(HttpServletRequest request) {
		ModelAndView modelAndView = createModelAndView("appraisalDetail");
		return modelAndView;
	}

	// 评价我的
	@RequestMapping(value = "myAppraisalView", method = { RequestMethod.GET })
	public ModelAndView myAppraisalView(HttpServletRequest request) {
		ModelAndView modelAndView = createModelAndView("myAppraisal");
		return modelAndView;
	}

	/******************************** 数据区 ********************************************/
	// 发起数据提交
	@RequestMapping(value = "launchAppraisal", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object launchAppraisal() {
		// 响应数据
		ResponseResult responseResult = new ResponseResult();
		// 评价信息
		String jsonAppraisalInfo = HttpRequestUtil.getInst().getString("jsonAppraisalInfo");
		try {
			JSONObject jsonAppraisalObj = JSON.parseObject(jsonAppraisalInfo);
			responseResult.setValue(appraisalService.launchAppraisal(jsonAppraisalObj));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("发起评价失败!", e);
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

	// 评价查询数据获取
	@RequestMapping(value = "getAppraisalRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getAppraisalData() {
		// 响应数据
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
			String theme = HttpRequestUtil.getInst().getString("theme");
			String startDate = HttpRequestUtil.getInst().getString("startDate");
			String endDate = HttpRequestUtil.getInst().getString("endDate");
			String operateType = HttpRequestUtil.getInst().getString("operateType");
			Appraisal appraisal = new Appraisal();
			// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen start.
			appraisal.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen end.
			if ("0".equals(operateType)) {
				WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
				appraisal.setUserId(weixinUser.getUserid());
			}
			Map<String, Object> params = new HashMap<String, Object>();
			if (StringUtils.isNotEmpty(theme)) {
				params.put("themeLike", theme);
			}
			if (StringUtils.isNotEmpty(startDate)) {
				params.put("startDate", DateUtil.stringToDate(startDate, "yyyy-MM-dd"));
			}
			if (StringUtils.isNotEmpty(endDate)) {
				params.put("endDate", DateUtil.stringToDate(endDate, "yyyy-MM-dd"));
			}
			params.put("orderBy", "createTime desc");
			responseResult.setValue(appraisalService.getAppraisalRecord(appraisal, params, pageIndex, pageSize));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("发起评价失败!", e);
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

	// 评价详情数据
	@RequestMapping(value = "getAppraisalDetailRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getAppraisalDetailRecord() {
		// 响应数据
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
			String id = HttpRequestUtil.getInst().getString("id");
			if (StringUtils.isEmpty(id)) {
				throw new VyiyunException(SpringContextHolder.getI18n("1002002", new String[] { "id" }, null));
			}
			WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("appraisalId", id);
			params.put("raterId", weixinUser.getUserid());
			responseResult.setValue(appraisalService.getAppraisalDetailRecord(params, pageIndex, pageSize).getData());
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("获取评价人员数据失败!", e);
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

	// 评价查询数据获取
	@RequestMapping(value = "getAuditAppraisalRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getAuditAppraisalRecord() {
		// 响应数据
		ResponseResult responseResult = new ResponseResult();
		try {
			String operationType = HttpRequestUtil.getInst().getString("operationType");
			if (StringUtils.isEmpty(operationType)) {
				throw new VyiyunException(SpringContextHolder.getI18n("1002002", new String[] { "operationType" }, null));
			}
			// 判断操作类型 0 未处理 1 已处理
			if (!"1".equals(operationType) && !"2".equals(operationType)) {
				throw new VyiyunException(SpringContextHolder.getI18n("1002003", new String[] { "operationType",
						operationType }, null));
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
			Appraisal appraisal = new Appraisal();
			// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen start.
			appraisal.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen end.
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("operationType", operationType);
			params.put("userId", weixinUser.getUserid());
			responseResult.setValue(appraisalService.getAuditAppraisalRecord(appraisal, params, pageIndex, pageSize));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("获取待我评价我的数据失败!", e);
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

	// 评价查询数据获取
	@RequestMapping(value = "getAppraisalMeRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getAppraisalMeRecord() {
		// 响应数据
		ResponseResult responseResult = new ResponseResult();
		try {
			String operationType = HttpRequestUtil.getInst().getString("operationType");
			if (StringUtils.isEmpty(operationType)) {
				throw new VyiyunException(SpringContextHolder.getI18n("1002002", new String[] { "operationType" }, null));
			}
			// 判断操作类型 0 未处理 1 已处理
			if (!"1".equals(operationType) && !"2".equals(operationType)) {
				throw new VyiyunException(SpringContextHolder.getI18n("1002003", new String[] { "operationType",
						operationType }, null));
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
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("operationType", operationType);
			params.put("userId", weixinUser.getUserid());
			responseResult.setValue(appraisalService.getAppraisalMeRecord(params, pageIndex, pageSize));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("获取评价我的数据失败!", e);
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

	// 员工排行
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "getEmployeeRankingRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getEmployeeRankingRecord() {
		// 响应数据
		ResponseResult responseResult = new ResponseResult();
		int pageIndex = -1;
		int pageSize = -1;
		String sPageIndex = HttpRequestUtil.getInst().getString("pageIndex");
		String sPageSize = HttpRequestUtil.getInst().getString("pageSize");
		String id = HttpRequestUtil.getInst().getString("id");
		if (StringUtils.isEmpty(id)) {
			throw new VyiyunException(SpringContextHolder.getI18n("1002002", new String[] { "id" }, null));
		}
		if (StringUtils.isNotEmpty(sPageIndex) && sPageIndex.matches("\\d+")) {
			pageIndex = Integer.parseInt(sPageIndex);
		}
		if (StringUtils.isNotEmpty(sPageSize) && sPageSize.matches("\\d+")) {
			pageSize = Integer.parseInt(sPageSize);
		}
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("appraisalId", id);
			params.put("corpId", HttpRequestUtil.getInst().getCurrentCorpId());
			List<Map<String, Object>> datList = (List<Map<String, Object>>) appraisalService.getEmployeeRankingRecord(
					params, pageIndex, pageSize).getData();
			if (!CollectionUtils.isEmpty(datList)) {
				int rank = 0;
				for (Map<String, Object> map : datList) {
					if (map.containsKey("rank") && null != map.get("rank")) {
						rank = (int) Float.parseFloat(StringUtil.getString(map.get("rank")));
					} else {
						map.put("rank", (rank + 1));
					}
				}
			}
			responseResult.setValue(datList);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("获取员工排行数据失败!", e);
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

	// 评价操作
	@RequestMapping(value = "appraisalOperate", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object appraisalOperate() {
		// 响应数据
		ResponseResult responseResult = new ResponseResult();
		// 评价信息
		String jsonAppraisalInfo = HttpRequestUtil.getInst().getString("jsonAppraisalInfo");
		try {
			JSONObject jsonAppraisalObj = JSON.parseObject(jsonAppraisalInfo);
			responseResult.setValue(appraisalService.appraisalOperate(jsonAppraisalObj));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("评价操作失败!", e);
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

	@Override
	protected String getPrefix() {
		return super.getPrefix() + "mobile/appraisal/";
	}
}
