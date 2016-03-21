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
import com.vyiyun.common.weixin.dao.ApprovalMapper;
import com.vyiyun.common.weixin.entity.Approval;
import com.vyiyun.common.weixin.entity.Approvers;
import com.vyiyun.common.weixin.enums.CommonAppType;
import com.vyiyun.common.weixin.service.IApprovalService;
import com.vyiyun.common.weixin.service.IApproversService;
import com.vyiyun.weixin.cache.impl.SystemConfigCache;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.dao.EntityAccountMapper;
import com.vyiyun.weixin.entity.Accessory;
import com.vyiyun.weixin.entity.EntityAccessory;
import com.vyiyun.weixin.entity.EntityAccount;
import com.vyiyun.weixin.entity.SystemConfig;
import com.vyiyun.weixin.enums.WeixinMsgType;
import com.vyiyun.weixin.exception.VyiyunBusinessException;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.DataResult;
import com.vyiyun.weixin.model.SqlQueryParameter;
import com.vyiyun.weixin.model.WeixinUser;
import com.vyiyun.weixin.model.wxmessage.WeixinMessageBase;
import com.vyiyun.weixin.msg.impl.AbstMsgExecutor;
import com.vyiyun.weixin.service.IAccessoryService;
import com.vyiyun.weixin.service.IEntityAccessoryService;
import com.vyiyun.weixin.service.IEntityAccountService;
import com.vyiyun.weixin.service.impl.AbstractBaseService;
import com.vyiyun.weixin.utils.CommonUtil;
import com.vyiyun.weixin.utils.ConfigUtil;
import com.vyiyun.weixin.utils.DateUtil;
import com.vyiyun.weixin.utils.ExcelUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.SpringContextHolder;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;
import com.vyiyun.weixin.utils.WeixinMessageUtil;

/**
 * @author tf
 * 
 *         2015年6月24日
 */
@Service("approvalService")
public class ApprovalServiceImpl extends AbstractBaseService implements IApprovalService {
	private static final Logger LOGGER = Logger.getLogger(ApprovalServiceImpl.class);

	/**
	 * 审核dao
	 */
	@Autowired
	private ApprovalMapper approvalDao;
	/**
	 * 实体-账户服务
	 */
	@Autowired
	private IEntityAccountService entityAccountService;
	/**
	 * 文件服务
	 */
	@Autowired
	private IAccessoryService accessoryService;

	/**
	 * 文件实体关系服务
	 */
	@Autowired
	private IEntityAccessoryService entityAccessoryService;

	/**
	 * 审核人服务
	 */
	@Autowired
	private IApproversService approversService;

	@Autowired
	private EntityAccountMapper entityAccountDao;

	@Override
	public int addApproval(Approval... approval) {
		return approvalDao.addApproval(Arrays.asList(approval));
	}

	@Override
	public DataResult queryApprovalRecord(Approval approval, Map<String, Object> params, int pageIndex, int pageSize) {
		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPage(true);
		}
		if (pageSize != -1) {
			sqlQueryParameter.setPageSize(pageSize);
		}
		sqlQueryParameter.setParameter(approval);
		if (!CollectionUtils.isEmpty(params)) {
			sqlQueryParameter.getKeyValMap().putAll(params);
		}
		List<Approval> approvalList = approvalDao.queryApprovalByPage(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(approvalList)) {
			List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
			Map<String, Object> temp = null;
			for (Approval a : approvalList) {
				temp = a.getPersistentState();
				temp.put(
						"statusDisplay",
						SystemCacheUtil.getInstance().getSystemStatusCache()
								.getSystemStatusName("Status", a.getStatus()));
				temp.put("screateTime", DateUtil.dateToString(a.getCreateTime(), "MM月dd日 HH:mm"));
				dataMap.add(temp);
			}
			dataResult.setData(dataMap);
		}
		if (sqlQueryParameter.isPage()) {
			// dataResult.setTotal((int)
			// approvalDao.queryApprovalCount(sqlQueryParameter));
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}

	@Override
	public void doAudit(JSONObject jsonApprovalInfo) {
		if (null != jsonApprovalInfo) {
			// 获取审批id
			String approvalId = jsonApprovalInfo.getString("approvalId");
			String entityAccountId = jsonApprovalInfo.getString("entityAccountId");
			if (StringUtils.isEmpty(approvalId)) {
				throw new VyiyunException("审批id不能为空!");
			}

			Approval currentApproval = approvalDao.getApprovalById(approvalId);
			if (null == currentApproval) {
				throw new VyiyunException("当前审批已被撤销！");
			}

			JSONObject jsonCommandInfo = jsonApprovalInfo.getJSONObject("commandInfo");
			if (null == jsonCommandInfo) {
				throw new VyiyunException("命令信息不能为空!");
			}
			String commandType = jsonCommandInfo.getString("commandType");
			if (StringUtils.isEmpty(commandType)) {
				throw new VyiyunException("命令类型不能为空！");
			}

			if (!Constants.CMD_UNDO.equalsIgnoreCase(commandType) && !Constants.CMD_GENERAL.equals(commandType)
					&& !Constants.CMD_REMIND.equals(commandType) && !Constants.CMD_ROLLBACK.equals(commandType)
					&& !"5".equals(commandType)) {
				throw new VyiyunException("无效的命令类型：" + commandType);
			}
			if ("5".equals(commandType)) {
				if ("1".equals(currentApproval.getCstatus())) {
					throw new VyiyunException("当前审批已处理!");
				}
			} else {
				if (CommonAppType.Status.已审核.value().equals(currentApproval.getStatus())
						|| CommonAppType.Status.审核退回.value().equals(currentApproval.getStatus())) {
					throw new VyiyunException("当前审批已处理!");
				}
			}
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
			String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
			// 获取审核人 抄送者
			EntityAccount entityAccount = null;
			if (Constants.CMD_GENERAL.equals(commandType)) {
				if (StringUtils.isEmpty(entityAccountId)) {
					throw new VyiyunException("实体账户id不能为空!");
				}
				// 如果没有下一级审核人及审核结束
				entityAccount = new EntityAccount();
				entityAccount.setId(entityAccountId);
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
				entityAccount.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
				entityAccount.setEntityId(approvalId);
				entityAccount.setAccountId(HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid());
				// 备注
				entityAccount.setRemark(jsonApprovalInfo.getString("remark"));
				// 同意
				entityAccount.setDealResult(CommonAppType.CommandType.同意.value());
				// 更新当前操作人状态
				entityAccountService.updateEntityAccount(entityAccount);
				Approval approval = new Approval();
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
				approval.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
				approval.setId(approvalId);
				// 获取当前审核记录 获取下一级审核人
				Approvers approvers = approversService.getApproversByParentId(entityAccountId);
				// 判断是否存在下一级审核人
				if (null != approvers) {
					if (!CommonAppType.Status.审核中.value().equals(approval.getStatus())) {
						approval.setStatus(CommonAppType.Status.审核中.value());
						approvalDao.updateApproval(approval);
					}
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
					msgParams.put("uId", approvers.getAccountId());
					msgParams.put("approvalId", approvalId);
					// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
					msgParams.put("corpId", corpId);
					// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
					SystemCacheUtil.getInstance().add(new AbstMsgExecutor(msgParams) {
						@SuppressWarnings("unchecked")
						@Override
						public void execute() throws Exception {
							Map<String, Object> msgParams = (Map<String, Object>) getObj();
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
							String _corpId = msgParams.get("corpId").toString();
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
							ApprovalMapper approvalDao = (ApprovalMapper) SpringContextHolder
									.getBean(ApprovalMapper.class);
							Approval approval = approvalDao.getApprovalById(StringUtil.getString(msgParams
									.get("approvalId")));
							Map<String, Object> dataMap = new HashMap<String, Object>();
							dataMap.put("userName", approval.getUserName());
							dataMap.put("flowName", approval.getFlowName());
							// 催办提醒：
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
							SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance()
									.getSystemConfigCache().getSystemConfig("system","weburl");
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
							String signAppId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH,
									"approval_agentid"));
							String uId = StringUtil.getString(msgParams.get("uId"));
							String approvalId = StringUtil.getString(msgParams.get("approvalId"));
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
							String content = new WeixinMessageBase(uId, WeixinMsgType.text, signAppId,
									WeixinMessageUtil.generateLinkUrlMsg("template_approval_sh", dataMap,
											systemConfig.getValue(), new Object[] { this.corpId, approvalId }))
									.toJson();
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
							sendMessage(content);
						}

						@Override
						public String getName() {
							return "提醒下一级审核人审核...";
						}
					});
				} else {
					approval.setStatus(CommonAppType.Status.已审核.value());
					approval.setEndTime(new Date());
					approvalDao.updateApproval(approval);
					approversService.deleteApprovers(null, approval.getId());
					// 给申请人发送消息
					Map<String, Object> msgParams = new HashMap<String, Object>();
					msgParams.put("uId", currentApproval.getUserId());
					msgParams.put("approvalId", approvalId);
					// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
					msgParams.put("corpId", corpId);
					// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
					SystemCacheUtil.getInstance().add(new AbstMsgExecutor(msgParams) {
						/**
						 * 
						 */
						private static final long serialVersionUID = 31730967041476340L;

						@SuppressWarnings("unchecked")
						@Override
						public void execute() throws Exception {
							Map<String, Object> msgParams = (Map<String, Object>) getObj();
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
							String _corpId = msgParams.get("corpId").toString();
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
							ApprovalMapper approvalDao = (ApprovalMapper) SpringContextHolder
									.getBean(ApprovalMapper.class);
							Approval approval = approvalDao.getApprovalById(StringUtil.getString(msgParams
									.get("approvalId")));
							Map<String, Object> dataMap = new HashMap<String, Object>();
							dataMap.put("userName", approval.getUserName());
							dataMap.put("flowName", approval.getFlowName());
							// 催办提醒：
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
							SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance()
									.getSystemConfigCache().getSystemConfig("system","weburl");
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
							String signAppId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH,
									"approval_agentid"));
							String uId = StringUtil.getString(msgParams.get("uId"));
							String approvalId = StringUtil.getString(msgParams.get("approvalId"));
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
							String content = new WeixinMessageBase(uId, WeixinMsgType.text, signAppId,
									WeixinMessageUtil.generateLinkUrlMsg("template_approval_pass", dataMap,
											systemConfig.getValue(), new Object[] { _corpId, approvalId })).toJson();
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
							sendMessage(content);
						}

						@Override
						public String getName() {
							return "审批通过消息...";
						}
					});
				}
			} else if (Constants.CMD_REMIND.equals(commandType)) {
				Map<String, Object> msgParams = new HashMap<String, Object>();
				// 参数校验
				String uId = jsonApprovalInfo.getString("uId");
				if (StringUtils.isEmpty(uId)) {
					throw new VyiyunException("待审核人不能为空!");
				}

				msgParams.put("uId", uId);
				msgParams.put("approvalId", approvalId);
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
				msgParams.put("corpId", corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
				SystemCacheUtil.getInstance().add(new AbstMsgExecutor(msgParams) {
					@SuppressWarnings("unchecked")
					@Override
					public void execute() throws Exception {
						Map<String, Object> msgParams = (Map<String, Object>) getObj();
						// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
						String _corpId = msgParams.get("corpId").toString();
						// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
						ApprovalMapper approvalDao = (ApprovalMapper) SpringContextHolder.getBean(ApprovalMapper.class);
						Approval approval = approvalDao.getApprovalById(StringUtil.getString(msgParams
								.get("approvalId")));
						Map<String, Object> dataMap = new HashMap<String, Object>();
						dataMap.put("userName", approval.getUserName());
						dataMap.put("flowName", approval.getFlowName());
						// 催办提醒：
						// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
						SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance().getSystemConfigCache()
								.getSystemConfig("system","weburl");
						// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
						String approvalAppId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH,
								"approval_agentid"));
						String uId = StringUtil.getString(msgParams.get("uId"));
						String approvalId = StringUtil.getString(msgParams.get("approvalId"));
						// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
						String content = new WeixinMessageBase(uId, WeixinMsgType.text, approvalAppId,
								WeixinMessageUtil.generateLinkUrlMsg("template_approvalRemind", dataMap,
										systemConfig.getValue(), new Object[] { _corpId, approvalId })).toJson();
						// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
						sendMessage(content);
					}

					@Override
					public String getName() {
						return "发布催办信息...";
					}
				});
			} else if (Constants.CMD_ROLLBACK.equals(commandType)) {
				if (StringUtils.isEmpty(entityAccountId)) {
					throw new VyiyunException("实体账户id不能为空!");
				}
				// 所有参入者记录 修改为作废
				entityAccount = new EntityAccount();
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
				entityAccount.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
				entityAccount.setId(entityAccountId);
				entityAccount.setRemark(jsonApprovalInfo.getString("remark"));
				entityAccount.setDealResult(CommonAppType.CommandType.退回.value());
				// 更新审核状态
				entityAccountService.updateEntityAccount(entityAccount);
				Approval approval = new Approval();
				approval.setId(approvalId);
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
				approval.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
				approval.setStatus(CommonAppType.Status.审核退回.value());
				// 更新审批状态
				approvalDao.updateApproval(approval);
				approversService.deleteApprovers(null, approval.getId());
				// 发送消息提示退回
				// 给申请人发送消息
				Map<String, Object> msgParams = new HashMap<String, Object>();
				msgParams.put("uId", currentApproval.getUserId());
				msgParams.put("approvalId", approvalId);
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
				msgParams.put("corpId", corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
				SystemCacheUtil.getInstance().add(new AbstMsgExecutor(msgParams) {
					@SuppressWarnings("unchecked")
					@Override
					public void execute() throws Exception {
						Map<String, Object> msgParams = (Map<String, Object>) getObj();
						// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
						String _corpId = msgParams.get("corpId").toString();
						// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
						ApprovalMapper approvalDao = (ApprovalMapper) SpringContextHolder.getBean(ApprovalMapper.class);
						Approval approval = approvalDao.getApprovalById(StringUtil.getString(msgParams
								.get("approvalId")));
						Map<String, Object> dataMap = new HashMap<String, Object>();
						dataMap.put("userName", approval.getUserName());
						dataMap.put("flowName", approval.getFlowName());
						// 催办提醒：
						// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
						SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance().getSystemConfigCache()
								.getSystemConfig("system","weburl");
						// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
						String signAppId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH, "approval_agentid"));
						String uId = StringUtil.getString(msgParams.get("uId"));
						String approvalId = StringUtil.getString(msgParams.get("approvalId"));
						// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
						String content = new WeixinMessageBase(uId, WeixinMsgType.text, signAppId, WeixinMessageUtil
								.generateLinkUrlMsg("template_approval_refuse", dataMap, systemConfig.getValue(),
										new Object[] { _corpId, approvalId })).toJson();
						// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
						sendMessage(content);
					}

					@Override
					public String getName() {
						return "审批拒绝消息...";
					}
				});
			} else /** 已办 */
			if ("5".equals(commandType)) {
				// 参数校验
				Approval approval = new Approval();
				approval.setId(approvalId);
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
				approval.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
				approval.setCstatus("1");
				approvalDao.updateApproval(approval);
			}
			/* 审批申请撤销 */
			else if (Constants.CMD_UNDO.equalsIgnoreCase(commandType)) {
				Approval approval = approvalDao.getApprovalById(approvalId);
				if (approval == null) {
					throw new VyiyunException("该条审批不存在!");
				}
				if (CommonAppType.Status.待审核.value().equalsIgnoreCase(approval.getStatus())) {
					// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
					entityAccount = new EntityAccount(approvalId, corpId);
					// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
					List<EntityAccount> entityAccountList = entityAccountService.getEntityAccount(entityAccount);
					StringBuilder uIds = new StringBuilder();
					if (entityAccountList != null) {
						for (EntityAccount ea : entityAccountList) {
							uIds.append('|').append(ea.getAccountId());
						}
						uIds.deleteCharAt(0);
					}

					// 给审核人抄送人发送消息
					Map<String, Object> msgParams = new HashMap<String, Object>();
					msgParams.put("uId", uIds.toString());
					msgParams.put("approvalId", approvalId);
					// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
					msgParams.put("corpId", corpId);
					// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
					Map<String, Object> dataMap = new HashMap<String, Object>();
					dataMap.put("userName", approval.getUserName());
					dataMap.put("flowName", approval.getFlowName());
					List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
					dataList.add(msgParams);
					dataList.add(dataMap);
					SystemCacheUtil.getInstance().add(new AbstMsgExecutor(dataList) {
						/**
						 * 
						 */
						private static final long serialVersionUID = 8503060005719466768L;

						@SuppressWarnings("unchecked")
						@Override
						public void execute() throws Exception {
							List<Map<String, Object>> dataList = (List<Map<String, Object>>) getObj();

							Map<String, Object> msgParams = dataList.get(0);
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
							String _corpId = msgParams.get("corpId").toString();
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
							Map<String, Object> dataMap = dataList.get(1);
							// 催办提醒：
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
							SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance()
									.getSystemConfigCache().getSystemConfig("system", "weburl");
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
							String signAppId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH,
									"approval_agentid"));
							String uId = StringUtil.getString(msgParams.get("uId"));
							String approvalId = StringUtil.getString(msgParams.get("approvalId"));
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
							String content = new WeixinMessageBase(uId, WeixinMsgType.text, signAppId,
									WeixinMessageUtil.generateLinkUrlMsg("template_approval_undo", dataMap,
											systemConfig.getValue(), new Object[] { _corpId, approvalId })).toJson();
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
							sendMessage(content);
						}

						@Override
						public String getName() {
							return "审批拒绝消息...";
						}
					});
					// 做删除操作
					entityAccountService.deleteByEntityId(approvalId);
					List<EntityAccessory> entityFileList = entityAccessoryService.getEntityAccessoryById(approvalId,
							null);
					if (entityFileList != null) {
						for (EntityAccessory entityFile : entityFileList) {
							accessoryService.deleteAccessoryById(entityFile.getFileId());
						}
					}
					approversService.deleteApprovers(null, approvalId);
					approvalDao.deleteApprovalById(approvalId);
				} else {
					throw new VyiyunException("已经进入审核期，不能撤回审批申请了");
				}
			}
		}
	}

	@Override
	public String launchApproval(JSONObject jsonLaunchApprovalInfo) {

		// 判断对象是否有参数
		if (!CollectionUtils.isEmpty(jsonLaunchApprovalInfo)) {
			// 获取审批类型
			String approvalType = jsonLaunchApprovalInfo.getString("approvalType");
			if (StringUtils.isEmpty(approvalType)) {
				throw new VyiyunException("审批类型不能为空！");
			}
			if (!"0".equals(approvalType) && !"1".equals(approvalType)) {
				throw new VyiyunException("无效的审批类型：" + approvalType);
			}

			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
			String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.

			// 获取命令信息
			JSONObject jsonCommandInfo = jsonLaunchApprovalInfo.getJSONObject("commandInfo");
			if (CollectionUtils.isEmpty(jsonCommandInfo)) {
				throw new VyiyunException("命令信息不能为空！");
			}
			String commandType = jsonCommandInfo.getString("commandType");
			if (StringUtils.isEmpty(commandType)) {
				throw new VyiyunException("命令类型不能为空！");
			}
			if (!Constants.CMD_GENERAL.equals(commandType) && !Constants.CMD_DRAFT.equals(commandType)) {
				throw new VyiyunException("无效的命令类型：" + commandType);
			}

			// 获取当前用户
			WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
			// 创建审批实体对象
			Approval approval = new Approval();
			approval.setUserId(weixinUser.getUserid());
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
			approval.setCorpId(corpId);
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
			approval.setUserName(weixinUser.getName());
			approval.setFlowName(jsonLaunchApprovalInfo.getString("flowName"));
			approval.setFlowType(jsonLaunchApprovalInfo.getString("flowType"));
			approval.setDepartment(jsonLaunchApprovalInfo.getString("department"));
			approval.setContent(jsonLaunchApprovalInfo.getString("content"));
			approval.setRemark(jsonLaunchApprovalInfo.getString("remark"));
			approval.setFlowType(approvalType);
			// 合同审批
			if ("1".equals(approvalType)) {
				approval.setContractNumber(jsonLaunchApprovalInfo.getString("contractNumber"));
				approval.setPartner(jsonLaunchApprovalInfo.getString("partner"));
			}
			// 获取审批id
			String approvalId = jsonLaunchApprovalInfo.getString("approvalId");
			// 是否存在id 草稿 重新发起 都会存在id
			boolean hasId = false;
			if (StringUtils.isNotEmpty(approvalId)) {
				approval.setId(approvalId);
				hasId = true;
			} else {
				approval.setId(CommonUtil.GeneGUID());
			}

			// 实体关系
			List<EntityAccount> entityAccountList = new ArrayList<EntityAccount>();

			// 首先情况数据
			// 清除与审批相关的 人员
			entityAccountService.deleteByEntityId(approval.getId());
			// 删除与附件关系
			entityAccessoryService.deleteEntityAccessory(approval.getId());
			// 草稿状态下只存储
			if (Constants.CMD_DRAFT.equalsIgnoreCase(commandType)) {
				if (hasId) {
					approvalDao.updateApproval(approval);
				} else {
					approval.setCreateTime(new Date());
					approval.setStatus(CommonAppType.Status.草稿.value());
					List<Approval> addList = new ArrayList<Approval>();
					addList.add(approval);
					approvalDao.addApproval(addList);
				}
				// 审核人 抄送人
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
				entityAccountList = generateEntityAccount(jsonLaunchApprovalInfo, approval.getId(), corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
				if (!CollectionUtils.isEmpty(entityAccountList)) {
					for (int i = 0, size = entityAccountList.size(); i < size; i++) {
						entityAccountList.get(i).setRemark(String.valueOf((i + 1)));
					}
					entityAccountService.addEntityAccount(entityAccountList.toArray(new EntityAccount[] {}));
				}
			} else /** 发起审批操作 */
			if (Constants.CMD_GENERAL.equalsIgnoreCase(commandType)) {
				if (hasId) {
					approval.setStatus(CommonAppType.Status.待审核.value());
					approvalDao.updateApproval(approval);
				} else {
					approval.setCreateTime(new Date());
					approval.setStatus(CommonAppType.Status.待审核.value());
					List<Approval> addList = new ArrayList<Approval>();
					addList.add(approval);
					addApproval(approval);
				}
				// 审核人 抄送人
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
				entityAccountList = generateEntityAccount(jsonLaunchApprovalInfo, approval.getId(), corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
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
					dataMap.put("userName", approval.getUserName());
					dataMap.put("flowName", approval.getFlowName());
					Map<String, Object> msgParams = new HashMap<String, Object>();
					msgParams.put("entityAccountList", cwList);
					// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
					msgParams.put("corpId", corpId);
					// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
					List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
					dataList.add(msgParams);
					dataList.add(dataMap);
					SystemCacheUtil.getInstance().add(new AbstMsgExecutor(dataList) {

						@Override
						public String getName() {
							return "发送审批申请信息...";
						}

						@Override
						@SuppressWarnings("unchecked")
						public void execute() throws Exception {
							List<Map<String, Object>> dataList = (List<Map<String, Object>>) getObj();
							Map<String, Object> msgParams = dataList.get(0);
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
							String _corpId = msgParams.get("corpId").toString();
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
							Map<String, Object> dataMap = dataList.get(1);
							List<EntityAccount> entityAccountList = (List<EntityAccount>) msgParams
									.get("entityAccountList");
							// 需要考虑 用户数过大 微信一次最多1000 个用户
							String shUser = new String();
							StringBuffer ccUsers = new StringBuffer();
							for (EntityAccount entityAccount : entityAccountList) {
								if (CommonAppType.PersonType.SH.value().equals(entityAccount.getPersonType())) {
									if (StringUtils.isEmpty(shUser)) {
										shUser = entityAccount.getAccountId();
									}
								} else if (CommonAppType.PersonType.CS.value().equals(entityAccount.getPersonType())) {
									ccUsers.append('|').append(entityAccount.getAccountId());
								}
							}

							if (ccUsers.length() > 1) {
								ccUsers.deleteCharAt(0);
							}

							SystemConfigCache<Object> sysConfigCache = SystemCacheUtil.getInstance()
									.getSystemConfigCache();
							SystemConfig systemConfig = sysConfigCache.getSystemConfig("system", "weburl");
							String approvalAppId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH,
									"approval_agentid"));
							String entityId = entityAccountList.get(0).getEntityId();
							if (StringUtils.isNotEmpty(shUser)) {
								// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen
								// start.
								String content = new WeixinMessageBase(shUser, WeixinMsgType.text, approvalAppId,
										WeixinMessageUtil.generateLinkUrlMsg("template_approval_sh", dataMap,
												systemConfig.getValue(), new Object[] { _corpId, entityId })).toJson();
								// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen
								// end.
								sendMessage(content);
							}
							if (StringUtils.isNotEmpty(ccUsers.toString())) {
								// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen
								// start.
								String content = new WeixinMessageBase(ccUsers.toString(), WeixinMsgType.text,
										approvalAppId, WeixinMessageUtil.generateLinkUrlMsg("template_approval_cc",
												dataMap, systemConfig.getValue(), new Object[] { _corpId, entityId }))
										.toJson();
								// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen
								// end.
								sendMessage(content);
							}
						}
					});
					// 发送结束
				}
			}
			// 附件信息
			if (jsonLaunchApprovalInfo.containsKey("accessoryInfo")) {
				entityAccessoryService.addEntityAccessory(approval.getId(),
						jsonLaunchApprovalInfo.getJSONObject("accessoryInfo"));
			}
			return approval.getId();
		} else {
			LOGGER.warn("请假信息为空！");
		}
		return null;
	}

	@Override
	public DataResult getAuditRecord(Approval approval, Map<String, Object> params, int pageIndex, int pageSize) {
		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPage(true);
		}
		if (pageSize != -1) {
			sqlQueryParameter.setPageSize(pageSize);
		}
		sqlQueryParameter.setParameter(approval);
		if (!CollectionUtils.isEmpty(params)) {
			sqlQueryParameter.getKeyValMap().putAll(params);
		}
		List<Approval> approvalList = approvalDao.getApprovalAuditRecord(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(approvalList)) {
			List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
			Map<String, Object> temp = null;
			for (Approval a : approvalList) {
				temp = a.getPersistentState();
				temp.put(
						"statusDisplay",
						SystemCacheUtil.getInstance().getSystemStatusCache()
								.getSystemStatusName("Status", a.getStatus()));

				temp.put("screateTime", DateUtil.dateToString(a.getCreateTime(), "MM月dd日 HH:mm"));
				dataMap.add(temp);
			}
			dataResult.setData(dataMap);
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}

	@Override
	public DataResult getBeAuditRecord(Approval approval, Map<String, Object> params, int pageIndex, int pageSize) {
		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1 && pageSize != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPageSize(pageSize);
			sqlQueryParameter.setPage(true);
		}
		sqlQueryParameter.setParameter(approval);
		if (!CollectionUtils.isEmpty(params)) {
			sqlQueryParameter.getKeyValMap().putAll(params);
		}
		List<Map<String, Object>> dataMap = approvalDao.getApprovalBeAuditRecord(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(dataMap)) {
			String status = null;
			for (Map<String, Object> temp : dataMap) {
				status = StringUtil.getString(temp.get("status"));
				if ("1".equals(status)) {
					temp.put("statusDisplay", "同意");
				} else if ("2".equals(status)) {
					temp.put("statusDisplay", "退回");
				}
				temp.put("screateTime", DateUtil.dateToString((Date) temp.get("createTime"), "MM月dd日 HH:mm"));
			}
			dataResult.setData(dataMap);
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}

	@Override
	public Approval getApprovalById(String id) {
		return approvalDao.getApprovalById(id);

	}

	@Override
	public DataResult getApprovalCcRecord(Approval approval, Map<String, Object> params, int pageIndex, int pageSize) {
		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPage(true);
		}
		if (pageSize != -1) {
			sqlQueryParameter.setPageSize(pageSize);
		}
		sqlQueryParameter.setParameter(approval);
		if (!CollectionUtils.isEmpty(params)) {
			sqlQueryParameter.getKeyValMap().putAll(params);
		}
		List<Approval> approvalList = approvalDao.getApprovalCcRecord(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(approvalList)) {
			List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
			Map<String, Object> temp = null;
			for (Approval a : approvalList) {
				temp = a.getPersistentState();
				temp.put(
						"statusDisplay",
						SystemCacheUtil.getInstance().getSystemStatusCache()
								.getSystemStatusName("Status", a.getStatus()));
				temp.put("screateTime", DateUtil.dateToString(a.getCreateTime(), "MM月dd日 HH:mm"));
				dataMap.add(temp);
			}
			dataResult.setData(dataMap);
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}

	@Override
	public Map<String, Object> getApprovalDataById(String id) {
		Map<String, Object> dataMap = new HashMap<String, Object>(); // 返回数据K-V映射
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
		if (StringUtils.isEmpty(id)) {
			throw new VyiyunException("参数id不能为空 ！");
		}
		Approval approval = approvalDao.getApprovalById(id);
		if (null == approval) {
			throw new VyiyunBusinessException("记录已被撤销或不存在!");
		}
		dataMap.put("approvalInfo", approval);
		dataMap.put("stautsDisplay",
				SystemCacheUtil.getInstance().getSystemStatusCache()
						.getSystemStatusName("Status", approval.getStatus()));

		// 获取当前审核人
		EntityAccount entityAccount = new EntityAccount();
		entityAccount.setEntityId(id);
		entityAccount.setCorpId(corpId);
		entityAccount.setEntityType(CommonAppType.EntityType.SP.value());
		List<EntityAccount> entityAccountList = entityAccountDao.getEntityAccount(entityAccount);
		// 审核
		List<EntityAccount> auditorList = new ArrayList<EntityAccount>();
		// 抄送者
		StringBuffer ccUserBuffer = new StringBuffer();
		if (!CollectionUtils.isEmpty(entityAccountList)) {
			for (EntityAccount ea : entityAccountList) {
				// 获取有效数据
				if (!ea.getInvalid()) {
					if (CommonAppType.PersonType.SH.value().equals(ea.getPersonType())) {
						auditorList.add(ea);
					} else if (CommonAppType.PersonType.CS.value().equals(ea.getPersonType())) {
						if (ccUserBuffer.length() == 0) {
							ccUserBuffer.append(ea.getAccountName());
						} else {
							ccUserBuffer.append(',').append(ea.getAccountName());
						}
					}
				}
			}
			dataMap.put("auditorList", auditorList);
			dataMap.put("ccUser", ccUserBuffer);
			dataMap.put("shList", entityAccountList);
		}

		// 获取报销附件
		// 处理附件信息
		List<Accessory> accessoryList = accessoryService.getAccessoryByEntityId(id);
		if (!CollectionUtils.isEmpty(accessoryList)) {
			dataMap.put("accessoryInfor", accessoryList);
		}
		// 批复结果
		// 已审核
		if (CommonAppType.Status.已审核.value().equals(approval.getStatus())) {
			// 这里根据updateTime 区分最后一个审核人
			dataMap.put("remark", CollectionUtils.isEmpty(auditorList) ? "" : auditorList.get(0).getRemark());
			dataMap.put("approvalStatus", CommonAppType.Status.已审核.value());

		} else if (CommonAppType.Status.审核退回.value().equals(approval.getStatus())) {
			// 审核退回
			dataMap.put("remark", CollectionUtils.isEmpty(auditorList) ? "" : auditorList.get(0).getRemark());
			dataMap.put("approvalStatus", CommonAppType.Status.审核退回.value());
		}
		return dataMap;
	}

	@Override
	public ResponseEntity<byte[]> exportApprovalListToExcel(JSONObject jsonSearchConditions) throws Exception {

		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();

		String userName = jsonSearchConditions.getString("userName");
		String flowName = jsonSearchConditions.getString("flowName");
		Date startTime = jsonSearchConditions.getDate("startTime");
		Date endTime = jsonSearchConditions.getDate("endTime");

		sqlQueryParameter.getKeyValMap().put("userNameLike", userName);
		sqlQueryParameter.getKeyValMap().put("flowNameLike", flowName);
		sqlQueryParameter.getKeyValMap().put("startDate", startTime);
		sqlQueryParameter.getKeyValMap().put("endDate", endTime);
		sqlQueryParameter.getKeyValMap().put("excludedStatus", CommonAppType.Status.草稿.value());
		sqlQueryParameter.getKeyValMap().put("orderBy", "CreateTime DESC");
		// 查询
		Approval approval = new Approval();
		approval.setStatus(jsonSearchConditions.getString("status"));
		approval.setCorpId(jsonSearchConditions.getString("corpId"));
		sqlQueryParameter.setParameter(approval);
		List<Approval> approvalList = approvalDao.queryApprovalByPage(sqlQueryParameter);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		LinkedHashMap<String, String> fieldMap = new LinkedHashMap<String, String>();
		fieldMap.put("flowName", "流程名称");
		fieldMap.put("userId", "发起人工号");
		fieldMap.put("userName", "发起人姓名");
		fieldMap.put("department", "归属部门");
		fieldMap.put("content", "审批内容");
		fieldMap.put("contractNumber", "合同编号");
		fieldMap.put("partner", "合作方");
		fieldMap.put("remark", "备注");
		fieldMap.put("createTime", "申请时间");
		fieldMap.put("statusDisplay", "审核状态");
		List<Map<String, Object>> excelList = new ArrayList<Map<String, Object>>();
		if (null != approvalList) {
			for (int index = 0; index < approvalList.size(); index++) {
				Approval tmp = approvalList.get(index);
				Map<String, Object> excelMap = tmp.getPersistentState();

				excelMap.put(
						"statusDisplay",
						SystemCacheUtil.getInstance().getSystemStatusCache()
								.getSystemStatusName(Constants.SYSTEM_STATUS, tmp.getStatus()));
				excelMap.put("createTime", DateUtil.dateToString(tmp.getCreateTime(), DateUtil.FORMATPATTERN));
				excelList.add(excelMap);
			}
		}
		ExcelUtil.listToExcel(excelList, fieldMap, "审批统计", bos);

		HttpHeaders headers = new HttpHeaders();
		String fileName = new String("审批统计.xls".getBytes("UTF-8"), "iso-8859-1");// 为了解决中文名称乱码问题
		headers.setContentDispositionFormData("attachment", fileName);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		return new ResponseEntity<byte[]>(bos.toByteArray(), headers, HttpStatus.CREATED);
	}
}
