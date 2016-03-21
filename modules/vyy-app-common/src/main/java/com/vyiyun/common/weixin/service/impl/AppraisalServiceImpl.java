/**
 * 
 */
package com.vyiyun.common.weixin.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vyiyun.common.weixin.dao.AppraisalMapper;
import com.vyiyun.common.weixin.entity.Appraisal;
import com.vyiyun.common.weixin.entity.ProblemTemplate;
import com.vyiyun.common.weixin.entity.Score;
import com.vyiyun.common.weixin.enums.CommonAppType;
import com.vyiyun.common.weixin.msg.AppraisalMsgExecutor;
import com.vyiyun.common.weixin.service.IAppraisalService;
import com.vyiyun.common.weixin.service.IProblemTemplateService;
import com.vyiyun.common.weixin.service.IScoreService;
import com.vyiyun.weixin.cache.impl.WeixinContactCache;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.entity.EntityAccount;
import com.vyiyun.weixin.exception.VyiyunBusinessException;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.DataResult;
import com.vyiyun.weixin.model.SqlQueryParameter;
import com.vyiyun.weixin.model.WeixinUser;
import com.vyiyun.weixin.service.IEntityAccountService;
import com.vyiyun.weixin.service.impl.AbstractBaseService;
import com.vyiyun.weixin.utils.CommonUtil;
import com.vyiyun.weixin.utils.DateUtil;
import com.vyiyun.weixin.utils.ExcelUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.SpringContextHolder;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;

/**
 * 评价服务实现
 * 
 * @author tf
 * @date 2015年11月16日 上午11:46:57
 */
@Service("appraisalService")
public class AppraisalServiceImpl extends AbstractBaseService implements IAppraisalService {
	private static final Logger LOGGER = Logger.getLogger(AppraisalServiceImpl.class);

	@Autowired
	private AppraisalMapper appraisalDao;
	/**
	 * 实体-账户服务
	 */
	@Autowired
	private IEntityAccountService entityAccountService;
	/**
	 * 问题模板服务
	 */
	@Autowired
	private IProblemTemplateService problemTemplateService;
	/**
	 * 评分服务
	 */
	@Autowired
	private IScoreService scoreService;

	@Override
	public Appraisal getAppraisalById(String id) {
		return appraisalDao.getAppraisalById(id);
	}

	@Override
	public DataResult getAuditAppraisalRecord(Appraisal appraisal, Map<String, Object> params, int pageIndex,
			int pageSize) {
		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPage(true);
		}
		if (pageSize != -1) {
			sqlQueryParameter.setPageSize(pageSize);
		}
		sqlQueryParameter.setParameter(appraisal);
		if (!CollectionUtils.isEmpty(params)) {
			sqlQueryParameter.getKeyValMap().putAll(params);
		}
		List<Appraisal> appraisalList = appraisalDao.getAuditAppraisalRecord(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(appraisalList)) {
			List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
			Map<String, Object> temp = null;
			for (Appraisal a : appraisalList) {
				temp = a.getPersistentState();
				temp.put("screateDate", DateUtil.dateToString(a.getCreateTime(), "yyyy年MM月dd日"));
				temp.put(
						"statusDisplay",
						SystemCacheUtil.getInstance().getSystemStatusCache()
								.getSystemStatusName("Status", StringUtil.getString(temp.get("status"))));
				dataMap.add(temp);
			}
			dataResult.setData(dataMap);
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}

	@Override
	public Object appraisalOperate(JSONObject jsonAppraisalInfo) {
		// 判断对象是否有参数
		if (!CollectionUtils.isEmpty(jsonAppraisalInfo)) {
			// 获取命令信息
			JSONObject jsonCommandInfo = jsonAppraisalInfo.getJSONObject("commandInfo");
			if (CollectionUtils.isEmpty(jsonCommandInfo)) {
				throw new VyiyunException("命令信息不能为空！");
			}
			String commandType = jsonCommandInfo.getString("commandType");
			if (StringUtils.isEmpty(commandType)) {
				throw new VyiyunException("命令类型不能为空！");
			}

			String id = jsonAppraisalInfo.getString("id");
			if (StringUtils.isEmpty(id)) {
				throw new VyiyunException("参数id不能为空！");
			}
			int type = jsonCommandInfo.getIntValue("commandType");
			// 获取当前用户
			WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
			Appraisal currentAppraisal = appraisalDao.getAppraisalById(id);
			if (null == currentAppraisal) {
				throw new VyiyunException(SpringContextHolder.getI18n("1002003", new String[] { "id", id }, null));
			}
			// 实体账户id
			if (CommonAppType.Status.已评价.value().equals(currentAppraisal.getStatus())) {
				throw new VyiyunException("当前评价已处理!");
			}
			// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen start.
			String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
			// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen end.
			switch (type) {
			// 提交评价
			case 1:
				// 获取问题列表
				String eaId = jsonAppraisalInfo.getString("eaId");
				if (StringUtils.isEmpty(eaId)) {
					throw new VyiyunException(SpringContextHolder.getI18n("1002002", new String[] { "eaId", eaId },
							null));
				}
				EntityAccount entityAccount = entityAccountService.getEntityAccountById(eaId);
				if (null == entityAccount) {
					throw new VyiyunException(SpringContextHolder.getI18n("1002003", new String[] { "eaId", eaId },
							null));
				}

				Score score = new Score();
				// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen start.
				score.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen end.
				score.setAppraisalId(id);
				score.setRaterId(weixinUser.getUserid());
				score.setUserId(entityAccount.getAccountId());
				List<Score> scoreList = scoreService.getScore(score);
				if (!CollectionUtils.isEmpty(scoreList)) {
					throw new VyiyunException("您已评价,不能重复评价!");
				}
				scoreList = generateProblemScore(jsonAppraisalInfo, id, weixinUser, entityAccount);
				if (!CollectionUtils.isEmpty(scoreList)) {
					for (Score s : scoreList) {
						s.setCorpId(corpId);
					}
					scoreService.addScore(scoreList.toArray(new Score[] {}));
				}

				// 实体账户id
				if (!CommonAppType.Status.评价中.value().equals(currentAppraisal.getStatus())) {
					Appraisal appraisal = new Appraisal();
					appraisal.setId(id);
					// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen start.
					appraisal.setCorpId(corpId);
					// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen end.
					appraisal.setStatus(CommonAppType.Status.评价中.value());
					appraisalDao.updateAppraisal(appraisal);
				}
				// 这里对每一个人评价进行标记 主要记录当前第几个人操作
				int appraiseTimes = entityAccount.getAppraiseTimes() + 1;
				entityAccount = new EntityAccount();
				entityAccount.setId(eaId);
				// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen start.
				entityAccount.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen end.
				entityAccount.setAppraiseTimes(appraiseTimes);
				entityAccountService.updateEntityAccount(entityAccount);

				entityAccount = new EntityAccount();
				entityAccount.setEntityId(id);
				// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen start.
				entityAccount.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen end.
				entityAccount.setPersonType(CommonAppType.PersonType.PJ.value());
				// 获取总评价人数
				int count = (int) entityAccountService.getEntityAccountCount(entityAccount);
				// 查询所有带评价人 当前是否都已被评价
				entityAccount = new EntityAccount();
				entityAccount.setEntityId(id);
				// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen start.
				entityAccount.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen end.
				entityAccount.setPersonType(CommonAppType.PersonType.BPJ.value());
				List<EntityAccount> entityAccountList = entityAccountService.getEntityAccount(entityAccount);
				if (!CollectionUtils.isEmpty(entityAccountList)) {
					for (EntityAccount ea : entityAccountList) {
						if (ea.getAppraiseTimes() != count) {
							return null;
						}
					}
					Appraisal appraisal = new Appraisal();
					appraisal.setId(id);
					// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen start.
					appraisal.setCorpId(corpId);
					// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen end.
					appraisal.setStatus(CommonAppType.Status.已评价.value());
					appraisalDao.updateAppraisal(appraisal);
				}

				break;
			// 取消评价
			case 2:

				// 如果当前已被评价那么 禁止操作
				if (CommonAppType.Status.未评价.value().equals(currentAppraisal.getStatus())) {
					// 被评价人 评价人 问题
					entityAccountService.deleteByEntityId(id);
					problemTemplateService.deleteByAppraisalId(id);
					appraisalDao.deleteAppraisalById(Lists.newArrayList(id));
				} else {
					throw new VyiyunException("当前评价进行中不能操作!");
				}
				break;
			default:
				throw new VyiyunException("无效操作!");
			}
		} else {
			LOGGER.warn("评价操作信息为空！");
		}
		return null;
	}

	@Override
	public Object launchAppraisal(JSONObject jsonAppraisalObj) {
		// 判断对象是否有参数
		if (!CollectionUtils.isEmpty(jsonAppraisalObj)) {
			// 获取命令信息
			JSONObject jsonCommandInfo = jsonAppraisalObj.getJSONObject("commandInfo");
			if (CollectionUtils.isEmpty(jsonCommandInfo)) {
				throw new VyiyunException("命令信息不能为空！");
			}
			String commandType = jsonCommandInfo.getString("commandType");
			if (StringUtils.isEmpty(commandType)) {
				throw new VyiyunException("命令类型不能为空！");
			}
			if (!Constants.CMD_GENERAL.equals(commandType)) {
				throw new VyiyunException("无效的命令类型：" + commandType);
			}

			// 获取当前用户
			WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
			// 创建评价对象
			Appraisal appraisal = new Appraisal();

			// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen start.
			String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
			appraisal.setCorpId(corpId);
			// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen end.
			appraisal.setUserId(weixinUser.getUserid());
			appraisal.setUserName(weixinUser.getName());
			appraisal.setTheme(jsonAppraisalObj.getString("theme"));
			appraisal.setOverallMerit(jsonAppraisalObj.getBoolean("overallMerit"));

			// 评价id
			String id = jsonAppraisalObj.getString("id");
			// 是否存在id 草稿 重新发起 都会存在id
			boolean hasId = false;
			if (StringUtils.isNotEmpty(id)) {
				appraisal.setId(id);
				hasId = true;
				// 清除与审批相关的 人员
				entityAccountService.deleteByEntityId(appraisal.getId());
			} else {
				appraisal.setId(CommonUtil.GeneGUID());
			}
			// 实体关系
			List<EntityAccount> entityAccountList = null;
			// 草稿状态下只存储
			if (Constants.CMD_DRAFT.equalsIgnoreCase(commandType)) {

			} else /** 发起审批操作 */
			if (Constants.CMD_GENERAL.equalsIgnoreCase(commandType)) {
				appraisal.setStatus(CommonAppType.Status.未评价.value());
				appraisal.setCreateTime(new Date());
				if (hasId) {
					appraisalDao.updateAppraisal(appraisal);
				} else {
					appraisalDao.addAppraisal(Lists.newArrayList(appraisal));
				}
				// 问题记录

				List<ProblemTemplate> problemTemplateList = generateProblemTemplate(jsonAppraisalObj, appraisal.getId());
				if (!CollectionUtils.isEmpty(problemTemplateList)) {
					if (null != appraisal.getOverallMerit() && appraisal.getOverallMerit()) {
						ProblemTemplate problemTemplate = new ProblemTemplate();
						problemTemplate.setId(CommonUtil.GeneGUID());
						// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen start.
						problemTemplate.setCorpId(corpId);
						// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen end.
						problemTemplate.setAppraisalId(appraisal.getId());
						problemTemplate.setQuota("需要填写综合评价");
						problemTemplateList.add(problemTemplate);
					}
					for (ProblemTemplate problemTemplate : problemTemplateList) {
						problemTemplate.setCorpId(corpId);
					}
					problemTemplateService.addProblemTemplate(problemTemplateList.toArray(new ProblemTemplate[] {}));
				}
				// 被评价人 评价人
				// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen start.
				entityAccountList = generateEntityAccount(jsonAppraisalObj, appraisal.getId(), corpId);
				// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen end.
				if (!CollectionUtils.isEmpty(entityAccountList)) {
					StringBuffer shUser = new StringBuffer();
					for (EntityAccount entityAccount : entityAccountList) {
						if (CommonAppType.EntityType.PJ.value().equals(entityAccount.getEntityType())) {
							if (CommonAppType.PersonType.PJ.value().equals(entityAccount.getPersonType())) {
								shUser.append('|').append(entityAccount.getAccountId());
							}
						} else {
							LOGGER.error("人员列表数据内容非法!" + JSON.toJSONString(entityAccountList));
							throw new VyiyunException("人员列表数据内容非法!");
						}
					}
					entityAccountService.addEntityAccount(entityAccountList.toArray(new EntityAccount[] {}));
					if (shUser.length() > 0) {
						shUser.deleteCharAt(0);
					}
					if (StringUtils.isNotEmpty(shUser.toString())) {
						// 给审核人推送消息
						// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen start.
						SystemCacheUtil.getInstance()

						.add(new AppraisalMsgExecutor(appraisal.getId(), corpId, shUser.toString(),
								"template_appraisal_sh"));
						// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen end.
					}
				} else {
					LOGGER.error("被评价或评价人不能为空!");
					throw new VyiyunException("被评价或评价人不能为空!");
				}
			}
			return appraisal.getId();
		} else {
			LOGGER.warn("评价信息为空！");
		}
		return null;
	}

	private List<ProblemTemplate> generateProblemTemplate(JSONObject jsonInfoObj, String entityId) {
		// 获取问题
		ProblemTemplate problemTemplate = null;
		List<ProblemTemplate> problemTemplateList = new ArrayList<ProblemTemplate>();
		JSONObject jsonObj = null;
		// 实体用户信息
		if (jsonInfoObj.containsKey("problemList")) {
			JSONArray jsonArray = jsonInfoObj.getJSONArray("problemList");
			if (!CollectionUtils.isEmpty(jsonArray)) {
				for (Object obj : jsonArray) {
					jsonObj = (JSONObject) obj;
					problemTemplate = new ProblemTemplate();
					problemTemplate.setId(CommonUtil.GeneGUID());
					problemTemplate.setAppraisalId(entityId);
					problemTemplate.setQuota(jsonObj.getString("quota"));
					problemTemplate.setStandard(jsonObj.getString("standard"));
					problemTemplate.setScores(jsonObj.getInteger("score"));
					problemTemplateList.add(problemTemplate);
				}
			}
		}
		return problemTemplateList;
	}

	private List<Score> generateProblemScore(JSONObject jsonInfoObj, String entityId, WeixinUser weixinUser,
			EntityAccount entityAccount) {
		// 获取问题
		Score score = null;
		List<Score> scoreList = new ArrayList<Score>();
		JSONObject jsonObj = null;
		// 实体用户信息
		if (jsonInfoObj.containsKey("problemScore")) {
			JSONArray jsonArray = jsonInfoObj.getJSONArray("problemScore");
			Integer var = null;
			if (!CollectionUtils.isEmpty(jsonArray)) {
				for (Object obj : jsonArray) {
					jsonObj = (JSONObject) obj;
					score = new Score();
					score.setId(CommonUtil.GeneGUID());
					score.setAppraisalId(entityId);
					score.setProblemId(jsonObj.getString("id"));
					var = jsonObj.getInteger("score");
					score.setScore((var == null ? 0 : var));
					score.setOpinion(jsonObj.getString("opinion"));
					score.setRaterId(weixinUser.getUserid());
					score.setRaterName(weixinUser.getName());
					score.setUserId(entityAccount.getAccountId());
					score.setUserName(entityAccount.getAccountName());
					scoreList.add(score);
				}
			}
		}
		return scoreList;
	}

	@Override
	public DataResult getAppraisalDetailRecord(Map<String, Object> params, int pageIndex, int pageSize) {
		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPage(true);
		}
		if (pageSize != -1) {
			sqlQueryParameter.setPageSize(pageSize);
		}
		if (!CollectionUtils.isEmpty(params)) {
			sqlQueryParameter.getKeyValMap().putAll(params);
		}
		// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen start.
		Appraisal appraisal = new Appraisal();
		appraisal.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
		sqlQueryParameter.setParameter(appraisal);
		// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen end.
		List<Map<String, Object>> dataMap = appraisalDao.getAppraisalDetailRecord(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(dataMap)) {
			dataResult.setData(dataMap);
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}

	@Override
	public DataResult getEmployeeRankingRecord(Map<String, Object> params, int pageIndex, int pageSize) {
		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPage(true);
		}
		if (pageSize != -1) {
			sqlQueryParameter.setPageSize(pageSize);
		}
		if (!CollectionUtils.isEmpty(params)) {
			sqlQueryParameter.getKeyValMap().putAll(params);
		}
		// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen start.
		Appraisal appraisal = new Appraisal();
		appraisal.setCorpId(params.get("corpId").toString());
		sqlQueryParameter.setParameter(appraisal);
		// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen end.
		List<Map<String, Object>> dataMap = appraisalDao.getEmployeeRankingRecord(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(dataMap)) {
			dataResult.setData(dataMap);
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}

	@Override
	public DataResult getAppraisalRecord(Appraisal appraisal, Map<String, Object> params, int pageIndex, int pageSize) {
		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPage(true);
		}
		if (pageSize != -1) {
			sqlQueryParameter.setPageSize(pageSize);
		}
		sqlQueryParameter.setParameter(appraisal);
		if (!CollectionUtils.isEmpty(params)) {
			sqlQueryParameter.getKeyValMap().putAll(params);
		}
		List<Appraisal> appraisalList = appraisalDao.getAppraisal(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(appraisalList)) {
			List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
			Map<String, Object> temp = null;
			for (Appraisal a : appraisalList) {
				temp = a.getPersistentState();
				temp.put("screateDate", DateUtil.dateToString(a.getCreateTime(), "yyyy年MM月dd日"));
				temp.put(
						"statusDisplay",
						SystemCacheUtil.getInstance().getSystemStatusCache()
								.getSystemStatusName("Status", StringUtil.getString(temp.get("status"))));
				/*
				 * VyiyunUtils.getSystemStatusName(
				 * SystemCacheUtil.getInstance().getSystemStatusCache(),
				 * appraisal.getCorpId(), Constants.SYSTEM_STATUS,
				 * appraisal.getStatus(), null));
				 */
				dataMap.add(temp);
			}
			dataResult.setData(dataMap);
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}

	@Override
	public DataResult getAppraisalMeRecord(Map<String, Object> params, int pageIndex, int pageSize) {
		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPage(true);
		}
		if (pageSize != -1) {
			sqlQueryParameter.setPageSize(pageSize);
		}
		if (!CollectionUtils.isEmpty(params)) {
			sqlQueryParameter.getKeyValMap().putAll(params);
		}
		// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen start.
		Appraisal appraisal = new Appraisal();
		appraisal.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
		sqlQueryParameter.setParameter(appraisal);
		// 微依云 公共应用 CorpId追加修正2016-01-12 By zb.shen end.
		List<Appraisal> appraisalList = appraisalDao.getAppraisalMeRecord(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(appraisalList)) {
			List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
			Map<String, Object> temp = null;
			for (Appraisal a : appraisalList) {
				temp = a.getPersistentState();
				temp.put("screateDate", DateUtil.dateToString(a.getCreateTime(), "yyyy年MM月dd日"));
				temp.put(
						"statusDisplay",
						SystemCacheUtil.getInstance().getSystemStatusCache()
								.getSystemStatusName("Status", StringUtil.getString(temp.get("status"))));
				/*
				 * VyiyunUtils.getSystemStatusName(
				 * SystemCacheUtil.getInstance().getSystemStatusCache(),
				 * appraisal.getCorpId(), Constants.SYSTEM_STATUS,
				 * appraisal.getStatus(), null));
				 */
				dataMap.add(temp);
			}
			dataResult.setData(dataMap);
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}

	@Override
	public ResponseEntity<byte[]> exportAppraisalListToExcel(JSONObject jsonSearchConditions) throws Exception {
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		String userName = jsonSearchConditions.getString("userName");
		String theme = jsonSearchConditions.getString("theme");
		Date startTime = jsonSearchConditions.getDate("startTime");
		Date endTime = jsonSearchConditions.getDate("endTime");

		sqlQueryParameter.getKeyValMap().put("userNameLike", userName);
		sqlQueryParameter.getKeyValMap().put("themeLike", theme);
		sqlQueryParameter.getKeyValMap().put("startDate", startTime);
		sqlQueryParameter.getKeyValMap().put("endDate", endTime);
		sqlQueryParameter.getKeyValMap().put("orderBy", "CreateTime DESC");
		// 查询
		Appraisal appraisal = new Appraisal();
		appraisal.setStatus(jsonSearchConditions.getString("status"));
		appraisal.setCorpId(jsonSearchConditions.getString("corpId"));
		sqlQueryParameter.setParameter(appraisal);
		List<Appraisal> appraisalList = appraisalDao.getAppraisal(sqlQueryParameter);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		LinkedHashMap<String, String> fieldMap = new LinkedHashMap<String, String>();
		fieldMap.put("userId", "发起人工号");
		fieldMap.put("userName", "发起人姓名");
		fieldMap.put("theme", "评价主题");
		fieldMap.put("overallMerit", "是否有综合评价");
		fieldMap.put("createTime", "发起日期");
		fieldMap.put("statusDisplay", "状态");

		List<Map<String, Object>> excelList = new ArrayList<Map<String, Object>>();
		if (null != appraisalList) {
			for (int index = 0; index < appraisalList.size(); index++) {
				Appraisal tmp = appraisalList.get(index);
				Map<String, Object> excelMap = tmp.getPersistentState();
				excelMap.put(
						"statusDisplay",

						SystemCacheUtil.getInstance().getSystemStatusCache()
								.getSystemStatusName(Constants.SYSTEM_STATUS, tmp.getStatus()));
				excelMap.put("createTime", DateUtil.dateToString(tmp.getCreateTime(), DateUtil.FORMATPATTERN));
				excelMap.put("overallMerit", tmp.getOverallMerit() ? "有" : "否");
				excelList.add(excelMap);
			}
		}
		ExcelUtil.listToExcel(excelList, fieldMap, "员工评价统计", bos);

		HttpHeaders headers = new HttpHeaders();
		String fileName = new String("员工评价统计.xls".getBytes("UTF-8"), "iso-8859-1");// 为了解决中文名称乱码问题
		headers.setContentDispositionFormData("attachment", fileName);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		return new ResponseEntity<byte[]>(bos.toByteArray(), headers, HttpStatus.CREATED);
	}

	@Override
	public Map<String, Object> getAppraisalDetailData(String id, String corpId) {
		Map<String, Object> retData = Maps.newHashMap();
		if (StringUtil.isBlank(id)) {
			return null;
		}
		Appraisal appraisal = appraisalDao.getAppraisalById(id);
		if (null == appraisal) {
			return null;
		}
		retData.put("appraisal", appraisal);
		// 获取评价指标
		ProblemTemplate param = new ProblemTemplate();
		param.setCorpId(corpId);
		param.setAppraisalId(id);
		List<ProblemTemplate> problemTemplateList = problemTemplateService.getProblemTemplate(param);
		if (!CollectionUtils.isEmpty(problemTemplateList)) {
			retData.put("problemTemplate", problemTemplateList);
		}

		// 未评价记录仅显示评价人和被评价人
		if (CommonAppType.Status.未评价.value().equals(appraisal.getStatus())) {
			EntityAccount entityAccount = new EntityAccount();
			entityAccount.setCorpId(corpId);
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

				retData.put("beAppraisalUser", beAppraisalUser);
				retData.put("appraisalUser", appraisalUser);
				retData.put("notAppraisal", "1");
			}
		}
		return retData;
	}

	@Override
	public int addAppraisal(Appraisal... appraisal) {

		return 0;
	}

	@Override
	public Map<String, Object> getUserDetailData(String id, String accountId, String corpId) {
		Map<String, Object> retData = Maps.newHashMap();
		// 评价id
		if (StringUtils.isEmpty(id)) {
			throw new VyiyunException(SpringContextHolder.getI18n("1002002", new String[] { "id" }, null));
		}
		Appraisal appraisal = appraisalDao.getAppraisalById(id);
		if (null == appraisal) {
			throw new VyiyunBusinessException("记录被删除或不存在!");
		}
		if (StringUtils.isEmpty(accountId)) {
			throw new VyiyunException(SpringContextHolder.getI18n("1002002", new String[] { "userId" }, null));
		}
		WeixinContactCache<Object> icache = ((WeixinContactCache<Object>) SystemCacheUtil.getInstance()
				.getWeixinContactCache());
		WeixinUser user = icache.getUserById(accountId);
		if (null == user) {
			user = new WeixinUser();
			appraisal.setAvatar(user.getAvatar());
		}
		retData.put("appraisalInfo", appraisal);
		retData.put("user", user);
		retData.put("department", icache.getUserDept(user.getDepartment()));
		// 获取问题
		ProblemTemplate problemTemplate = new ProblemTemplate();
		problemTemplate.setCorpId(corpId);
		problemTemplate.setAppraisalId(id);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderBy", "px asc");
		List<ProblemTemplate> problemTemplateList = problemTemplateService.getProblemTemplate(problemTemplate, params);

		params.clear();
		params.put("userId", accountId);
		params.put("corpId", corpId);
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
		retData.put("problemTemplateMapList", problemTemplateMapList);
		// 综合评价
		retData.put("opinionMapList", opinionMapList);
		return retData;
	}
}
