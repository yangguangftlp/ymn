/**
 * 
 */
package com.vyiyun.common.weixin.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.dao.AbsentMapper;
import com.vyiyun.common.weixin.entity.Absent;
import com.vyiyun.common.weixin.entity.Approval;
import com.vyiyun.common.weixin.entity.Approvers;
import com.vyiyun.common.weixin.enums.CommonAppType;
import com.vyiyun.common.weixin.service.IAbsentService;
import com.vyiyun.common.weixin.service.IApproversService;
import com.vyiyun.weixin.cache.impl.SystemStatusCache;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.entity.EntityAccount;
import com.vyiyun.weixin.entity.SystemConfig;
import com.vyiyun.weixin.enums.WeixinMsgType;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.DataResult;
import com.vyiyun.weixin.model.SqlQueryParameter;
import com.vyiyun.weixin.model.WeixinUser;
import com.vyiyun.weixin.model.wxmessage.WeixinMessageBase;
import com.vyiyun.weixin.msg.impl.AbstMsgExecutor;
import com.vyiyun.weixin.service.IEntityAccountService;
import com.vyiyun.weixin.service.impl.AbstractBaseService;
import com.vyiyun.weixin.utils.ConfigUtil;
import com.vyiyun.weixin.utils.DateUtil;
import com.vyiyun.weixin.utils.ExcelUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.SpringContextHolder;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;
import com.vyiyun.weixin.utils.WeixinMessageUtil;

/**
 * 请假服务
 * 
 * @author tf
 * 
 *         2015年6月24日
 */
@Service("absentService")
public class AbsentServiceImpl extends AbstractBaseService implements IAbsentService {

	private static final Logger LOGGER = Logger.getLogger(AbsentServiceImpl.class);

	/**
	 * 审核人服务
	 */
	@Autowired
	private IApproversService approversService;

	/**
	 * 请假
	 */
	@Autowired
	private AbsentMapper absentDao;
	/**
	 * 实体-账户
	 */
	@Autowired
	private IEntityAccountService entityAccountService;

	@Override
	public int addAbsent(Absent... absent) {
		if (absent.length < 0) {
			return 0;
		}
		return absentDao.addAbsent(Arrays.asList(absent));
	}

	@Override
	public List<Absent> getAbsent(Absent absent) {
		return absentDao.getAbsent(absent);
	}

	@Override
	public void deleteById(String id) {
		absentDao.deleteAbsentById(id);
	}

	@Override
	public void updateAbsent(Absent absent) {
		absentDao.updateAbsent(absent);
	}

	@Override
	public void doApply(JSONObject jsonAbsentApplyInfo) {

		// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
		// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
		// 获取请假信息
		Absent absent = new Absent();
		WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
		absent.setId(jsonAbsentApplyInfo.getString("id"));
		// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
		absent.setCorpId(corpId);
		// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
		absent.setUserId(weixinUser.getUserid());
		absent.setUserName(weixinUser.getName());
		absent.setAbsentType(jsonAbsentApplyInfo.getString("absentType"));
		absent.setReason(jsonAbsentApplyInfo.getString("reason"));
		absent.setPosition(jsonAbsentApplyInfo.getString("position"));
		absent.setDepartment(jsonAbsentApplyInfo.getString("department"));
		absent.setBeginTime(DateUtil.stringToDate(jsonAbsentApplyInfo.getString("beginTime")));
		absent.setEndTime(DateUtil.stringToDate(jsonAbsentApplyInfo.getString("endTime")));
		// 获取天数
		absent.setCreateTime(new Date());
		absent.setStatus(CommonAppType.Status.待审核.value());
		// 添加请假信息
		List<Absent> absentList = new ArrayList<Absent>();
		absentList.add(absent);
		absentDao.addAbsent(absentList);
		SystemStatusCache<Object> systemStatusCache = SystemCacheUtil.getInstance().getSystemStatusCache();
		// 获取审核人 抄送者
		// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
		List<EntityAccount> entityAccountList = generateEntityAccount(jsonAbsentApplyInfo, absent.getId(), corpId);
		// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
		// 审核人 抄送人
		if (!CollectionUtils.isEmpty(entityAccountList)) {

			List<EntityAccount> shList = new ArrayList<EntityAccount>();
			List<EntityAccount> cwList = new ArrayList<EntityAccount>();
			List<Approvers> approversList = new ArrayList<Approvers>();
			Approvers approvers = null;
			Approvers rootAapprovers = null;
			for (EntityAccount ea : entityAccountList) {
				if (CommonAppType.PersonType.SH.value().equals(ea.getPersonType())) {
					shList.add(ea);
					approvers = new Approvers();
					approvers.setId(ea.getId());
					// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
					approvers.setCorpId(corpId);
					// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
					approvers.setPersonType(ea.getPersonType());
					approvers.setEntityId(ea.getEntityId());
					approvers.setAccountId(ea.getAccountId());
					approvers.setAccountName(ea.getAccountName());
					approvers.setAccountType(ea.getAccountType());
					approvers.setAvatar(ea.getAvatar());
					approvers.setPersonType(ea.getPersonType());
					approvers.setEntityType(ea.getEntityType());
					if (null != rootAapprovers) {
						approvers.setParentId(rootAapprovers.getId());
						rootAapprovers.setNext(approvers.getId());
					}
					approversList.add(approvers);
					rootAapprovers = approvers;
				} else if (CommonAppType.PersonType.CS.value().equals(ea.getPersonType())) {
					cwList.add(ea);
				}
			}
			if (shList.size() < 1) {
				throw new VyiyunException("审核人必须存在!");
			}
			cwList.add(shList.get(0));
			approversList.remove(0);
			approversService.addApprovers(approversList.toArray(new Approvers[] {}));

			entityAccountService.addEntityAccount(cwList.toArray(new EntityAccount[] {}));
			// 发送消息通知

			Map<String, Object> dataMap = new HashMap<String, Object>();
			// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
			dataMap.put("absentType",
					systemStatusCache.getSystemStatusValue(corpId, "AbsentType", absent.getAbsentType()));

			// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
			dataMap.put("userName", absent.getUserName());

			Map<String, Object> msgParams = new HashMap<String, Object>();
			msgParams.put("entityAccountList", cwList);
			// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
			msgParams.put("corpId", corpId);
			// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			dataList.add(msgParams);
			dataList.add(dataMap);

			SystemCacheUtil.getInstance().add(new AbstMsgExecutor(dataList) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 9134886544418117797L;

				@Override
				public void set(Object obj) {

				}

				@Override
				public String getName() {
					return "发送请假申请信息...";
				}

				@Override
				@SuppressWarnings("unchecked")
				public void execute() throws Exception {
					List<Map<String, Object>> dataList = (List<Map<String, Object>>) getObj();

					Map<String, Object> msgParams = dataList.get(0);
					Map<String, Object> dataMap = dataList.get(1);
					List<EntityAccount> entityAccountList = (List<EntityAccount>) msgParams.get("entityAccountList");
					// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
					String _corpId = msgParams.get("corpId").toString();
					// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
					int size = entityAccountList.size();
					// 需要考虑 用户数过大 微信一次最多1000 个用户
					StringBuffer shUsers = new StringBuffer();
					StringBuffer ccUsers = new StringBuffer();
					for (int i = 0; i < size; i++) {
						if (CommonAppType.PersonType.SH.value().equals(entityAccountList.get(i).getPersonType())) {
							shUsers.append('|');
							shUsers.append(entityAccountList.get(i).getAccountId());
						} else if (CommonAppType.PersonType.CS.value().equals(entityAccountList.get(i).getPersonType())) {
							ccUsers.append('|');
							ccUsers.append(entityAccountList.get(i).getAccountId());
						}
					}
					if (shUsers.length() > 1) {
						shUsers.deleteCharAt(0);
					}
					if (ccUsers.length() > 1) {
						ccUsers.deleteCharAt(0);
					}
					// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
					SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance().getSystemConfigCache()
							.getSystemConfig("system", "weburl");
					// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
					String absentAppId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH, "absent_agentid"));
					String entityId = entityAccountList.get(0).getEntityId();
					if (StringUtils.isNotEmpty(shUsers.toString())) {
						// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
						String content = new WeixinMessageBase(shUsers.toString(), WeixinMsgType.text, absentAppId,
								WeixinMessageUtil.generateLinkUrlMsg("template_absent_sh", dataMap,
										systemConfig.getValue(), new Object[] { _corpId, entityId })).toJson();
						// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
						// sendMessage(content);
						sendMessage(content);
					}
					if (StringUtils.isNotEmpty(ccUsers.toString())) {
						// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
						String content = new WeixinMessageBase(ccUsers.toString(), WeixinMsgType.text, absentAppId,
								WeixinMessageUtil.generateLinkUrlMsg("template_absent_cc", dataMap,
										systemConfig.getValue(), new Object[] { _corpId, entityId })).toJson();
						// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
						// sendMessage(content);
						sendMessage(content);
					}
				}
			});
		}
	}

	@Override
	public DataResult queryAbsentRecord(Absent absent, int pageIndex, int pageSize) {
		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1 && pageSize != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPageSize(pageSize);
		}
		sqlQueryParameter.setParameter(absent);
		dataResult.setData(absentDao.queryAbsentByPage(sqlQueryParameter));
		dataResult.setTotal((int) absentDao.queryAbsentCount(sqlQueryParameter));
		return dataResult;
	}

	@Override
	public void doAbsent(JSONObject jsonAbsentInfoObj) {
		if (!CollectionUtils.isEmpty(jsonAbsentInfoObj)) {
			SystemStatusCache<Object> systemStatusCache = SystemCacheUtil.getInstance().getSystemStatusCache();
			JSONObject jsonCommandInfoObj = jsonAbsentInfoObj.getJSONObject("commandInfo");
			String commandType = jsonCommandInfoObj.getString("commandType");
			String absentId = jsonAbsentInfoObj.getString("absentId");
			Absent currentAbsent = absentDao.getAbsentById(absentId);
			if (null == currentAbsent) {
				throw new VyiyunException("当前请假已被撤销！");
			}
			if (CommonAppType.Status.已审核.value().equals(currentAbsent.getStatus())
					|| CommonAppType.Status.审核退回.value().equals(currentAbsent.getStatus())) {
				throw new VyiyunException("当前请假已处理!");
			}
			// 同意
			if (Constants.CMD_GENERAL.equalsIgnoreCase(commandType)) {
				// 如果没有下一级选择人那么就结束请假
				EntityAccount entityAccount = new EntityAccount();
				// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
				String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
				entityAccount.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
				entityAccount.setId(jsonAbsentInfoObj.getString("entityId"));
				entityAccount.setEntityId(absentId);
				entityAccount.setAccountId(HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid());
				entityAccount.setRemark(jsonAbsentInfoObj.getString("remark"));
				entityAccount.setDealResult(CommonAppType.CommandType.同意.value());
				entityAccountService.updateEntityAccount(entityAccount);
				// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
				// List<EntityAccount> entityAccountList =
				// generateEntityAccount(jsonAbsentInfoObj, absentId, corpId);
				// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
				Absent absent = new Absent();
				absent.setId(absentId);
				// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
				absent.setCorpId(corpId);
				Approvers approvers = approversService.getApproversByParentId(entityAccount.getId());
				// 判断是否存在下一级审核人
				if (null != approvers) {
					absent.setStatus(CommonAppType.Status.审核中.value());
					absentDao.updateAbsent(absent);
					// 获取下一级审核人处理
					entityAccount = new EntityAccount();
					entityAccount.setId(approvers.getId());
					// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
					entityAccount.setCorpId(corpId);
					// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
					entityAccount.setPersonType(approvers.getPersonType());
					entityAccount.setEntityId(approvers.getEntityId());
					entityAccount.setAccountId(approvers.getAccountId());
					entityAccount.setAccountName(approvers.getAccountName());
					entityAccount.setAccountType(approvers.getAccountType());
					entityAccount.setAvatar(approvers.getAvatar());
					entityAccount.setEntityType(approvers.getEntityType());
					entityAccount.setDealResult("0");
					// 添加下一级审核人
					entityAccountService.addEntityAccount(entityAccount);
					// 删除当前审核人记录
					approversService.deleteApprovers(approvers.getId(), null);
					// 给下一级审核人发消息
					Map<String, Object> msgParams = new HashMap<String, Object>();
					msgParams.put("uId", entityAccount.getAccountId());
					msgParams.put("absentId", absentId);
					// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
					msgParams.put("corpId", corpId);
					// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
					List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
					dataList.add(msgParams);
					SystemCacheUtil.getInstance().add(new AbstMsgExecutor(dataList) {
						/**
						 * 
						 */
						private static final long serialVersionUID = 7771053679681430314L;

						@SuppressWarnings("unchecked")
						@Override
						public void execute() throws Exception {
							List<Map<String, Object>> dataList = (List<Map<String, Object>>) getObj();

							Map<String, Object> msgParams = dataList.get(0);

							// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
							String _corpId = msgParams.get("corpId").toString();
							// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
							AbsentMapper iAbsentDao = (AbsentMapper) SpringContextHolder.getBean(AbsentMapper.class);
							Absent absent = iAbsentDao.getAbsentById(StringUtil.getString(msgParams.get("absentId")));
							Map<String, Object> dataMap = new HashMap<String, Object>();
							dataMap.put("userName", absent.getUserName());
							// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
							SystemStatusCache<Object> systemStatusCache = SystemCacheUtil.getInstance()
									.getSystemStatusCache();
							dataMap.put(
									"absentType",
									systemStatusCache.getSystemStatusValue(_corpId, "AbsentType",
											absent.getAbsentType()));
							// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
							// 催办提醒：
							// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
							SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance()
									.getSystemConfigCache().getSystemConfig("system", "weburl");
							// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
							String signAppId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH, "absent_agentid"));
							String uId = StringUtil.getString(msgParams.get("uId"));
							String entityId = StringUtil.getString(msgParams.get("absentId"));
							// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
							String content = new WeixinMessageBase(uId, WeixinMsgType.text, signAppId,
									WeixinMessageUtil.generateLinkUrlMsg("template_absent_sh", dataMap,
											systemConfig.getValue(), new Object[] { _corpId, entityId })).toJson();
							// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
							sendMessage(content);
						}

						@Override
						public String getName() {
							return "提醒下一级审核人审核...";
						}
					});
				} else {
					// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
					absent.setStatus(CommonAppType.Status.已审核.value());
					absentDao.updateAbsent(absent);
					// 审批已通过
					// 给申请人发送消息
					Map<String, Object> msgParams = new HashMap<String, Object>();
					msgParams.put("uId", currentAbsent.getUserId());
					msgParams.put("absentId", absentId);
					// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
					msgParams.put("corpId", corpId);
					// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
					List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
					dataList.add(msgParams);
					SystemCacheUtil.getInstance().add(new AbstMsgExecutor(dataList) {
						/**
						 * 
						 */
						private static final long serialVersionUID = -1266734649259010017L;

						@SuppressWarnings("unchecked")
						@Override
						public void execute() throws Exception {
							List<Map<String, Object>> dataList = (List<Map<String, Object>>) getObj();

							Map<String, Object> msgParams = dataList.get(0);

							// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
							String _corpId = msgParams.get("corpId").toString();
							// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
							AbsentMapper iAbsentDao = (AbsentMapper) SpringContextHolder.getBean(AbsentMapper.class);
							Absent absent = iAbsentDao.getAbsentById(StringUtil.getString(msgParams.get("absentId")));
							Map<String, Object> dataMap = new HashMap<String, Object>();
							dataMap.put("userName", absent.getUserName());
							// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
							SystemStatusCache<Object> systemStatusCache = SystemCacheUtil.getInstance()
									.getSystemStatusCache();
							dataMap.put(
									"absentType",
									systemStatusCache.getSystemStatusValue(_corpId, "AbsentType",
											absent.getAbsentType()));
							// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
							// 催办提醒：
							SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance()
									.getSystemConfigCache().getSystemConfig("system", "weburl");
							String signAppId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH, "absent_agentid"));
							String uId = StringUtil.getString(msgParams.get("uId"));
							String entityId = StringUtil.getString(msgParams.get("absentId"));
							// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
							String content = new WeixinMessageBase(uId, WeixinMsgType.text, signAppId,
									WeixinMessageUtil.generateLinkUrlMsg("template_absent_pass", dataMap,
											systemConfig.getValue(), new Object[] { _corpId, entityId })).toJson();
							// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
							sendMessage(content);
						}

						@Override
						public String getName() {
							return "请假通过消息...";
						}
					});

				}

			} else /** 催办 */
			if (Constants.CMD_REMIND.equalsIgnoreCase(commandType)) {
				Map<String, Object> msgParams = new HashMap<String, Object>();
				msgParams.put("uId", jsonAbsentInfoObj.getString("uId"));
				msgParams.put("absentId", jsonAbsentInfoObj.getString("absentId"));
				// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
				msgParams.put("corpId", HttpRequestUtil.getInst().getCurrentCorpId());
				// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
				Map<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("userName", HttpRequestUtil.getInst().getCurrentWeixinUser().getName());
				dataMap.put("absentType", jsonAbsentInfoObj.getString("absentTypeName"));
				List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
				dataList.add(msgParams);
				dataList.add(dataMap);
				SystemCacheUtil.getInstance().add(new AbstMsgExecutor(dataList) {
					/**
					 * 
					 */
					private static final long serialVersionUID = 540924886197239302L;

					@SuppressWarnings("unchecked")
					@Override
					public void execute() throws Exception {
						List<Map<String, Object>> dataList = (List<Map<String, Object>>) getObj();

						Map<String, Object> msgParams = dataList.get(0);
						// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
						String _corpId = msgParams.get("corpId").toString();
						// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
						Map<String, Object> dataMap = dataList.get(1);
						// 催办提醒：
						// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
						SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance().getSystemConfigCache()
								.getSystemConfig("system", "weburl");
						// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
						String signAppId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH, "absent_agentid"));
						String uId = StringUtil.getString(msgParams.get("uId"));
						String entityId = StringUtil.getString(msgParams.get("absentId"));
						// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
						String content = new WeixinMessageBase(uId, WeixinMsgType.text, signAppId, WeixinMessageUtil
								.generateLinkUrlMsg("template_absentRemind", dataMap, systemConfig.getValue(),
										new Object[] { _corpId, entityId })).toJson();
						// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
						sendMessage(content);
					}

					@Override
					public String getName() {
						return "发布催办信息...";
					}
				});
			}
			/* 在待审核状态下撤销请假申请 */
			else if (Constants.CMD_UNDO.equalsIgnoreCase(commandType)) {
				if (CommonAppType.Status.待审核.value().equals(currentAbsent.getStatus())) {
					EntityAccount entityAccount = new EntityAccount();
					// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
					String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
					entityAccount.setCorpId(corpId);
					// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
					entityAccount.setEntityId(absentId);
					List<EntityAccount> entityAccountList = entityAccountService.getEntityAccount(entityAccount);

					// 给下一级审核人发消息
					StringBuffer uIds = new StringBuffer();
					if (entityAccountList != null) {
						for (EntityAccount ea : entityAccountList) {
							uIds.append('|').append(ea.getAccountId());
						}
						uIds.deleteCharAt(0);
					}

					Absent absent = absentDao.getAbsentById(StringUtil.getString(absentId));
					Map<String, Object> dataMap = new HashMap<String, Object>();
					dataMap.put("userName", absent.getUserName());
					dataMap.put("absentType",
							systemStatusCache.getSystemStatusValue(corpId, "AbsentType", absent.getAbsentType()));
					Map<String, Object> msgParams = new HashMap<String, Object>();
					msgParams.put("uId", uIds.toString());
					msgParams.put("absentId", absentId);
					// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
					msgParams.put("corpId", corpId);
					// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
					List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
					dataList.add(msgParams);
					dataList.add(dataMap);
					SystemCacheUtil.getInstance().add(new AbstMsgExecutor(dataList) {
						/**
						 * 
						 */
						private static final long serialVersionUID = 5277416288371875185L;

						@SuppressWarnings("unchecked")
						@Override
						public void execute() throws Exception {
							List<Map<String, Object>> dataList = (List<Map<String, Object>>) getObj();

							Map<String, Object> msgParams = dataList.get(0);
							// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
							String _corpId = msgParams.get("corpId").toString();
							// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
							Map<String, Object> dataMap = dataList.get(1);
							// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
							SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance()
									.getSystemConfigCache().getSystemConfig("system", "weburl");
							// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
							String signAppId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH, "absent_agentid"));
							String uId = StringUtil.getString(msgParams.get("uId"));
							String entityId = StringUtil.getString(msgParams.get("absentId"));
							// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
							String content = new WeixinMessageBase(uId, WeixinMsgType.text, signAppId,
									WeixinMessageUtil.generateLinkUrlMsg("template_absent_undo", dataMap,
											systemConfig.getValue(), new Object[] { _corpId, entityId })).toJson();
							// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
							sendMessage(content);
						}

						@Override
						public String getName() {
							return "请假撤销操作";
						}
					});

					entityAccountService.deleteByEntityId(absentId);
					this.deleteById(absentId);

				} else {
					throw new VyiyunException("请假申请已进入审核期，不能撤回了");
				}

			} else if (Constants.CMD_ROLLBACK.equalsIgnoreCase(commandType)) {
				// 所有参入者记录 修改为作废
				EntityAccount entityAccount = new EntityAccount();
				entityAccount.setId(jsonAbsentInfoObj.getString("entityId"));
				// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
				String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
				entityAccount.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
				entityAccount.setEntityId(absentId);
				entityAccount.setAccountId(HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid());
				entityAccount.setRemark(jsonAbsentInfoObj.getString("remark"));
				entityAccount.setDealResult(CommonAppType.CommandType.退回.value());
				// entityAccount.setInvalid(true);
				entityAccountService.updateEntityAccount(entityAccount);
				Absent absent = new Absent();
				absent.setId(absentId);
				// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
				absent.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
				absent.setStatus(CommonAppType.Status.审核退回.value());
				absentDao.updateAbsent(absent);
				// 审批已处理
				// 给申请人发送消息
				Map<String, Object> msgParams = new HashMap<String, Object>();
				msgParams.put("uId", currentAbsent.getUserId());
				msgParams.put("absentId", absentId);
				// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
				msgParams.put("corpId", corpId);
				// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
				List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
				dataList.add(msgParams);
				SystemCacheUtil.getInstance().add(new AbstMsgExecutor(dataList) {
					/**
					 * 
					 */
					private static final long serialVersionUID = -2619193894905504054L;

					@SuppressWarnings("unchecked")
					@Override
					public void execute() throws Exception {
						List<Map<String, Object>> dataList = (List<Map<String, Object>>) getObj();

						Map<String, Object> msgParams = dataList.get(0);
						// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
						String _corpId = msgParams.get("corpId").toString();
						// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.

						AbsentMapper iAbsentDao = (AbsentMapper) SpringContextHolder.getBean(AbsentMapper.class);
						Absent absent = iAbsentDao.getAbsentById(StringUtil.getString(msgParams.get("absentId")));
						Map<String, Object> dataMap = new HashMap<String, Object>();
						dataMap.put("userName", absent.getUserName());
						SystemStatusCache<Object> systemStatusCache = SystemCacheUtil.getInstance()
								.getSystemStatusCache();
						// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
						dataMap.put("absentType",
								systemStatusCache.getSystemStatusValue(_corpId, "AbsentType", absent.getAbsentType()));
						/*
						 * VyiyunUtils.getSystemStatusName(SystemCacheUtil.
						 * getInstance().getSystemStatusCache(),
						 * HttpRequestUtil.getInst().getCurrentCorpId(),
						 * Constants.SYSTEM_ABSENT_TYPE, absent.getAbsentType(),
						 * null));
						 */
						// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
						// 催办提醒：
						SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance().getSystemConfigCache()
								.getSystemConfig("system", "weburl");
						String signAppId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH, "absent_agentid"));
						String uId = StringUtil.getString(msgParams.get("uId"));
						String entityId = StringUtil.getString(msgParams.get("absentId"));
						// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
						String content = new WeixinMessageBase(uId, WeixinMsgType.text, signAppId, WeixinMessageUtil
								.generateLinkUrlMsg("template_absent_refuse", dataMap, systemConfig.getValue(),
										new Object[] { _corpId, entityId })).toJson();
						// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
						sendMessage(content);
					}

					@Override
					public String getName() {
						return "请假拒绝消息...";
					}
				});
			}
		} else {
			LOGGER.warn("请假信息为空！");
		}
	}

	@Override
	public Absent getAbsentById(String id) {
		return absentDao.getAbsentById(id);
	}

	@Override
	public DataResult queryAbsentRecord(Absent absent, Map<String, Object> params, int pageIndex, int pageSize) {
		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPage(true);
		}
		if (pageSize != -1) {
			sqlQueryParameter.setPageSize(pageSize);
		}
		sqlQueryParameter.setParameter(absent);
		if (!CollectionUtils.isEmpty(params)) {
			sqlQueryParameter.getKeyValMap().putAll(params);
		}
		SystemStatusCache<Object> systemStatusCache = SystemCacheUtil.getInstance().getSystemStatusCache();
		List<Absent> absentList = absentDao.queryAbsentByPage(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(absentList)) {
			List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
			Map<String, Object> temp = null;
			for (Absent entity : absentList) {
				temp = entity.getPersistentState();
				// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
				String corpId = absent.getCorpId();
				temp.put("absentTypeName",
						systemStatusCache.getSystemStatusValue(corpId, "AbsentType", entity.getAbsentType()));
				temp.put("statusDisplay", systemStatusCache.getSystemStatusName("Status", entity.getStatus()));

				// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
				temp.put("sbeginTime", DateUtil.dateToString(entity.getBeginTime(), "MM月dd日 HH:mm"));
				temp.put("sEndTime", DateUtil.dateToString(entity.getEndTime(), "MM月dd日 HH:mm"));
				temp.put("screateDate", DateUtil.dateToString(entity.getCreateTime(), "MM月dd日"));
				dataMap.add(temp);
			}
			dataResult.setData(dataMap);
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}

	@Override
	public DataResult queryAuditRecord(Map<String, Object> params, int pageIndex, int pageSize) {
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
		List<Absent> absentList = absentDao.queryAuditRecord(sqlQueryParameter);
		SystemStatusCache<Object> systemStatusCache = SystemCacheUtil.getInstance().getSystemStatusCache();
		if (!CollectionUtils.isEmpty(absentList)) {
			List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
			Map<String, Object> temp = null;
			for (Absent entity : absentList) {
				temp = entity.getPersistentState();
				// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
				temp.put("absentTypeName", systemStatusCache.getSystemStatusValue(HttpRequestUtil.getInst()
						.getCurrentCorpId(), "AbsentType", entity.getAbsentType()));
				// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
				temp.put("sbeginTime", DateUtil.dateToString(entity.getBeginTime(), "MM月dd日 HH:mm"));
				temp.put("sEndTime", DateUtil.dateToString(entity.getEndTime(), "MM月dd日 HH:mm"));
				dataMap.add(temp);
			}
			dataResult.setData(dataMap);
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}

	@Override
	public DataResult getAbsentCcRecord(Approval approval, Map<String, Object> params, int pageIndex, int pageSize) {

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
		SystemStatusCache<Object> systemStatusCache = SystemCacheUtil.getInstance().getSystemStatusCache();
		List<Absent> absentList = absentDao.getAbsentCcRecord(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(absentList)) {
			List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
			Map<String, Object> temp = null;
			for (Absent entity : absentList) {
				temp = entity.getPersistentState();
				// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
				String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
				temp.put("absentTypeName",
						systemStatusCache.getSystemStatusValue(corpId, "AbsentType", entity.getAbsentType()));

				// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
				temp.put("sbeginTime", DateUtil.dateToString(entity.getBeginTime(), "MM月dd日 HH:mm"));
				temp.put("sEndTime", DateUtil.dateToString(entity.getEndTime(), "MM月dd日 HH:mm"));
				// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
				temp.put("statusDisplay", systemStatusCache.getSystemStatusName("Status", entity.getStatus()));
				// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
				dataMap.add(temp);
			}
			dataResult.setData(dataMap);
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}

	@Override
	public ResponseEntity<byte[]> exportAbsentListToExcel(JSONObject jsonSearchConditions) throws Exception {

		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();

		String userName = jsonSearchConditions.getString("userName");
		Date startTime = jsonSearchConditions.getDate("startTime");
		Date endTime = jsonSearchConditions.getDate("endTime");

		sqlQueryParameter.getKeyValMap().put("userNameLike", userName);
		sqlQueryParameter.getKeyValMap().put("startDate", startTime);
		sqlQueryParameter.getKeyValMap().put("endDate", endTime);
		sqlQueryParameter.getKeyValMap().put("orderBy", "CreateTime DESC");
		// 查询
		Absent absent = new Absent();
		absent.setStatus(jsonSearchConditions.getString("status"));
		absent.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
		sqlQueryParameter.setParameter(absent);
		List<Absent> absentList = absentDao.queryAbsentByPage(sqlQueryParameter);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		LinkedHashMap<String, String> fieldMap = new LinkedHashMap<String, String>();
		fieldMap.put("userId", "请假人工号");
		fieldMap.put("userName", "请假人姓名");
		fieldMap.put("position", "请假人职位");
		fieldMap.put("department", "所在部门");
		fieldMap.put("reason", "请假事由");
		fieldMap.put("beginTime", "开始时间");
		fieldMap.put("endTime", "结束时间");
		fieldMap.put("createTime", "申请时间");
		fieldMap.put("statusDisplay", "审核状态");
		List<Map<String, Object>> excelList = new ArrayList<Map<String, Object>>();
		SystemStatusCache<Object> systemStatusCache = SystemCacheUtil.getInstance().getSystemStatusCache();
		if (null != absentList) {
			for (int index = 0; index < absentList.size(); index++) {
				Absent tmp = absentList.get(index);
				Map<String, Object> excelMap = tmp.getPersistentState();

				excelMap.put("statusDisplay",
						systemStatusCache.getSystemStatusName(Constants.SYSTEM_STATUS, tmp.getStatus()));
				excelMap.put("beginTime", DateUtil.dateToString(tmp.getBeginTime(), DateUtil.FORMATPATTERN));
				excelMap.put("endTime", DateUtil.dateToString(tmp.getEndTime(), DateUtil.FORMATPATTERN));
				excelMap.put("createTime", DateUtil.dateToString(tmp.getCreateTime(), DateUtil.FORMATPATTERN));
				excelList.add(excelMap);
			}
		}
		ExcelUtil.listToExcel(excelList, fieldMap, "请假统计", bos);

		HttpHeaders headers = new HttpHeaders();
		String fileName = new String("请假统计.xls".getBytes("UTF-8"), "iso-8859-1");// 为了解决中文名称乱码问题
		headers.setContentDispositionFormData("attachment", fileName);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		return new ResponseEntity<byte[]>(bos.toByteArray(), headers, HttpStatus.CREATED);
	}

	@Override
	public void deleteRelatedRecord(String corpId, String absentType) {
		Absent absent = new Absent();
		absent.setCorpId(corpId);
		absent.setAbsentType(absentType);
		// TODO 审核中记录向申请人发消息
		absentDao.deleteRelatedRecord(absent);
	}
}
