/**
 * 
 */
package com.vyiyun.common.weixin.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.vyiyun.common.weixin.dao.FeedbackMapper;
import com.vyiyun.common.weixin.entity.EntityProgress;
import com.vyiyun.common.weixin.entity.Feedback;
import com.vyiyun.common.weixin.service.IEntityProgressService;
import com.vyiyun.common.weixin.service.IFeedbackService;
import com.vyiyun.weixin.cache.ICache;
import com.vyiyun.weixin.cache.impl.SystemConfigCache;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.entity.SystemConfig;
import com.vyiyun.weixin.enums.WeixinMsgType;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.DataResult;
import com.vyiyun.weixin.model.SqlQueryParameter;
import com.vyiyun.weixin.model.WeixinUser;
import com.vyiyun.weixin.model.wxmessage.WeixinMessageBase;
import com.vyiyun.weixin.msg.impl.AbstMsgExecutor;
import com.vyiyun.weixin.service.IEntityAccessoryService;
import com.vyiyun.weixin.service.impl.AbstractBaseService;
import com.vyiyun.weixin.utils.CommonUtil;
import com.vyiyun.weixin.utils.ConfigUtil;
import com.vyiyun.weixin.utils.DateUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;
import com.vyiyun.weixin.utils.WeixinMessageUtil;

/**
 * 系统反馈服务
 * 
 * @author tf
 * @date 2015年10月8日 下午6:03:16
 */
@Service("feedbackService")
public class FeedbackServiceImpl extends AbstractBaseService implements IFeedbackService {

	// 日志
	private static final Logger LOGGER = Logger.getLogger(FeedbackServiceImpl.class);
	@Autowired
	private IEntityProgressService entityProgressService;

	@Autowired
	private FeedbackMapper feedbackDao;
	/**
	 * 实体附件处理
	 */
	@Autowired
	private IEntityAccessoryService entityAccessoryService;

	@Override
	public int addFeedback(Feedback feedback) {
		return feedbackDao.addFeedback(Arrays.asList(feedback));
	}

	@Override
	public DataResult getFeedback(Feedback Feedback, Map<String, Object> params, int pageIndex, int pageSize) {

		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPage(true);
		}
		if (pageSize != -1) {
			sqlQueryParameter.setPageSize(pageSize);
		}
		sqlQueryParameter.setParameter(Feedback);
		if (!CollectionUtils.isEmpty(params)) {
			sqlQueryParameter.getKeyValMap().putAll(params);
		}
		List<Feedback> FeedbackList = feedbackDao.getFeedback(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(FeedbackList)) {
			List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
			Map<String, Object> temp = null;
			for (Feedback a : FeedbackList) {
				temp = a.getPersistentState();
				temp.put("screateTime", DateUtil.dateToString(a.getCreateTime(), "MM月dd日 HH:mm"));
				temp.put("sendTime", DateUtil.dateToString(a.getEndTime(), "MM月dd日 HH:mm"));
				if ("0".equals(a.getStatus())) {
					temp.put("statusDisplay", "待解决");
				} else if ("1".equals(a.getStatus())) {
					temp.put("statusDisplay", "已解决");
				}
				dataMap.add(temp);
			}
			dataResult.setData(dataMap);
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}

	@Override
	public int updateFeedback(Feedback Feedback) {
		return feedbackDao.updateFeedback(Feedback);
	}

	@Override
	public DataResult getFeedbackTop(String sort, int pageIndex, int pageSize) {

		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPage(true);
		}
		if (pageSize != -1) {
			sqlQueryParameter.setPageSize(pageSize);
		}
		if (StringUtils.isEmpty(sort)) {
			sqlQueryParameter.getKeyValMap().put("sort", "desc");
		} else {
			sqlQueryParameter.getKeyValMap().put("sort", sort);
		}
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
		sqlQueryParameter.getKeyValMap().put("corpId", HttpRequestUtil.getInst().getCurrentCorpId());
		// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
		List<Map<String, Object>> dataMapList = feedbackDao.getFeedbackTop(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(dataMapList)) {
			dataResult.setData(dataMapList);
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}

	@Override
	public Feedback getFeedbackById(String id) {
		return feedbackDao.getFeedbackById(id);
	}

	@Override
	public Object submitFeedback(JSONObject jsonFeedback) {

		if (!CollectionUtils.isEmpty(jsonFeedback)) {
			Feedback feedback = new Feedback();
			String problem = jsonFeedback.getString("problem");
			if (StringUtils.isEmpty(problem)) {
				throw new VyiyunException("提交问题内容不能为空!");
			}
			WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
			feedback.setId(CommonUtil.GeneGUID());
			// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
			feedback.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
			feedback.setProblem(problem);
			feedback.setSuggest(jsonFeedback.getString("suggest"));
			feedback.setCreateTime(new Date());
			feedback.setStatus("0");
			feedback.setUserId(weixinUser.getUserid());
			feedback.setUserName(weixinUser.getName());
			List<Feedback> feedbacks = new ArrayList<Feedback>();
			feedbacks.add(feedback);
			feedbackDao.addFeedback(feedbacks);
			// 附件信息
			if (jsonFeedback.containsKey("accessoryInfo")) {
				entityAccessoryService.addEntityAccessory(feedback.getId(), jsonFeedback.getJSONObject("accessoryInfo"));
			}
			return feedback.getId();
		} else {
			LOGGER.warn("反馈信息为空！");
		}
		return null;
	}

	@Override
	public Object doFeedbackOperate(JSONObject jsonFeedback) {
		if (!CollectionUtils.isEmpty(jsonFeedback)) {
			if (jsonFeedback.containsKey("commandInfo")) {
				JSONObject jsonCommandInfo = jsonFeedback.getJSONObject("commandInfo");
				if (CollectionUtils.isEmpty(jsonCommandInfo)) {
					throw new VyiyunException("命令信息不能为空！");
				}
				String commandType = jsonCommandInfo.getString("commandType");
				if (StringUtils.isEmpty(commandType)) {
					throw new VyiyunException("命令类型不能为空！");
				}
				String feedbackId = jsonFeedback.getString("feedbackId");
				if (StringUtils.isEmpty(feedbackId)) {
					throw new VyiyunException("参数feedbackId不能为空！");
				}
				int type = jsonCommandInfo.getIntValue("commandType");
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
				String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
				switch (type) {
				case 0:
					// 相关负责人(运营经理)
					EntityProgress entityProgress = new EntityProgress();
					// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
					entityProgress.setCorpId(corpId);
					// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
					entityProgress.setEntityId(feedbackId);
					entityProgress.setContent(jsonFeedback.getString("content"));
					entityProgress.setId(CommonUtil.GeneGUID());
					Date currentDate = new Date();
					entityProgress.setCreateTime(currentDate);
					entityProgressService.addEntityProgress(entityProgress);
					Map<String, Object> resultData = new HashMap<String, Object>();
					resultData.put("content", entityProgress.getContent());
					resultData.put("scurrentTime", DateUtil.dateToString(currentDate, "yyyy-MM-dd HH:mm"));
					return resultData;
				case 2:
					// 已解决
					Feedback feedback = new Feedback();
					feedback.setId(feedbackId);
					// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
					feedback.setCorpId(corpId);
					// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
					feedback.setStatus("1");
					feedback.setEndTime(new Date());
					feedbackDao.updateFeedback(feedback);
					break;
				case 3:
					// 系统管理操作
					String userId = jsonFeedback.getString("userId");
					Map<String,String> dataMap = new HashMap<String, String>();
					dataMap.put("userId", userId);
					dataMap.put("feedbackId", feedbackId);
					// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
					dataMap.put("corpId", corpId);
					// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
					if (StringUtils.isNotEmpty(userId)) {
						// 提醒
						SystemCacheUtil.getInstance().add(new AbstMsgExecutor(dataMap) {
							
							private static final long serialVersionUID = 1L;

							@Override
							public String getName() {
								return "问题反馈提醒";
							}

							@SuppressWarnings("unchecked")
							@Override
							public void execute() throws Exception {
								
								Map<String,String> dataMap = (Map<String, String>) getObj();
								// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
								String _corpId = dataMap.get("corpId");
								// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
								SystemConfigCache<Object> sysConfigCache = SystemCacheUtil.getInstance().getSystemConfigCache();
								SystemConfig systemConfig = sysConfigCache.getSystemConfig("system","weburl");
								// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
								// String corpId = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "corpId");
								String appId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH, "feedback_agentid"));
								String userId = dataMap.get("userId");
								String feedbackId = dataMap.get("feedbackId");
								if (StringUtils.isNotEmpty(userId)) {
									// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
									String content = new WeixinMessageBase(userId, WeixinMsgType.text, appId,
											WeixinMessageUtil.generateLinkUrlMsg("template_feedback_remind", null,systemConfig.getValue(), new Object[] { _corpId,
											 feedbackId })).toJson();
									// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
									sendMessage(content);
								}
							}
						});
					} else {
						LOGGER.warn("问题反馈提醒用户id为空！");
					}
					break;
				}
			} else {
				LOGGER.warn("请假信息为空！");
			}
		}
		return null;
	}

	@Override
	public Object feedbackRemind(JSONObject jsonReminder) {
		if (!CollectionUtils.isEmpty(jsonReminder)) {
			SystemCacheUtil.getInstance().add(new AbstMsgExecutor(jsonReminder) {
				private static final long serialVersionUID = 4900799953891845460L;

				@Override
				public String getName() {
					return "反馈解决提醒";
				}

				@Override
				public void execute() throws Exception {
					JSONObject param = (JSONObject)getObj();
					String feedbackId = param.getString("feedbackId");
					String userId = param.getString("userId");
					String _corpId = param.getString("corpId");
					String problem = param.getString("problem");
					SystemConfigCache<Object> sysConfigCache = SystemCacheUtil.getInstance().getSystemConfigCache();
					SystemConfig systemConfig = sysConfigCache.getSystemConfig("system","weburl");
					String appId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH, "feedback_agentid"));
					if (StringUtils.isNotEmpty(userId)) {
						Map<String, Object> themeMap = Maps.newHashMap();
						themeMap.put("problem", problem);
						String content = new WeixinMessageBase(
								userId,
								WeixinMsgType.text,
								appId,
								WeixinMessageUtil.generateLinkUrlMsg(
										"template_feedback_remind2user",
										themeMap,
										systemConfig.getValue(),
										new Object[] { _corpId, feedbackId })
								).toJson();
						sendMessage(content);
					}
				}
			});
		}
		return true;
	}
}
