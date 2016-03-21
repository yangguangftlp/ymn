/**
 * 
 */
package com.vyiyun.common.weixin.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import com.vyiyun.common.weixin.dao.LoanMapper;
import com.vyiyun.common.weixin.entity.Approvers;
import com.vyiyun.common.weixin.entity.Loan;
import com.vyiyun.common.weixin.enums.CommonAppType;
import com.vyiyun.common.weixin.msg.LoanMsgExecutor;
import com.vyiyun.common.weixin.service.IApproversService;
import com.vyiyun.common.weixin.service.ILoanService;
import com.vyiyun.weixin.cache.impl.SystemStatusCache;
import com.vyiyun.weixin.cache.impl.WeixinContactCache;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.dao.AccessoryMapper;
import com.vyiyun.weixin.dao.EntityAccessoryMapper;
import com.vyiyun.weixin.dao.EntityAccountMapper;
import com.vyiyun.weixin.entity.Accessory;
import com.vyiyun.weixin.entity.EntityAccount;
import com.vyiyun.weixin.entity.SystemStatus;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.DataResult;
import com.vyiyun.weixin.model.SqlQueryParameter;
import com.vyiyun.weixin.model.WeixinUser;
import com.vyiyun.weixin.service.IAccessoryService;
import com.vyiyun.weixin.service.IEntityAccessoryService;
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
 * 借款服务实现
 * 
 * @author tf
 * @date 2015年11月3日 下午1:47:58
 */
@Service("loanService")
public class LoanServiceImpl extends AbstractBaseService implements ILoanService {
	private static final Logger LOGGER = Logger.getLogger(LoanServiceImpl.class);

	@Autowired
	private LoanMapper loanDao;

	/**
	 * 实体-账户服务
	 */
	@Autowired
	private IEntityAccountService entityAccountService;

	@Autowired
	private IApproversService approversService;

	/**
	 * 实体附件处理
	 */
	@Autowired
	private IEntityAccessoryService entityAccessoryService;

	/**
	 * 附件处理
	 */
	@Autowired
	private IAccessoryService accessoryService;

	/**
	 * 实体-用户DAO
	 */
	@Autowired
	private EntityAccountMapper entityAccountDao;

	/**
	 * 附件DAO
	 */
	@Autowired
	private AccessoryMapper accessoryDao;

	/**
	 * 实体-附件DAO
	 */
	@Autowired
	private EntityAccessoryMapper entityAccessoryDao;

	@Override
	public Object launchLoan(JSONObject jsonLaunchLoanInfo) {
		// 判断对象是否有参数
		if (!CollectionUtils.isEmpty(jsonLaunchLoanInfo)) {
			// 获取命令信息
			JSONObject jsonCommandInfo = jsonLaunchLoanInfo.getJSONObject("commandInfo");
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
			// 私有还是公有
			String loanType = jsonLaunchLoanInfo.getString("loanType");
			if (StringUtils.isEmpty(loanType) || (!"0".equals(loanType) && !"1".equals(loanType))) {
				throw new VyiyunException("无效的loanType类型：" + loanType);
			}
			// 获取当前用户
			WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
			// 创建审批实体对象
			Loan loan = new Loan();
			if (jsonLaunchLoanInfo.containsKey("amount")) {
				loan.setAmount(jsonLaunchLoanInfo.getFloat("amount"));
			}
			if (jsonLaunchLoanInfo.containsKey("bank")) {
				loan.setBank(jsonLaunchLoanInfo.getString("bank"));
			}
			if (jsonLaunchLoanInfo.containsKey("capitalAmount")) {
				loan.setCapitalAmount(jsonLaunchLoanInfo.getString("capitalAmount"));
			}
			if (jsonLaunchLoanInfo.containsKey("clientName")) {
				loan.setClientName(jsonLaunchLoanInfo.getString("clientName"));
			}
			if (jsonLaunchLoanInfo.containsKey("company")) {
				loan.setCompany(jsonLaunchLoanInfo.getString("company"));
			}
			if (jsonLaunchLoanInfo.containsKey("contractAmount")) {
				loan.setContractAmount(jsonLaunchLoanInfo.getFloat("contractAmount"));
			}

			if (jsonLaunchLoanInfo.containsKey("department")) {
				loan.setDepartment(jsonLaunchLoanInfo.getString("department"));
			}
			if (jsonLaunchLoanInfo.containsKey("details")) {
				loan.setDetails(jsonLaunchLoanInfo.getString("details"));
			}
			if (jsonLaunchLoanInfo.containsKey("loanUse")) {
				loan.setLoanUse(jsonLaunchLoanInfo.getString("loanUse"));
			}
			if (jsonLaunchLoanInfo.containsKey("projectName")) {
				loan.setProjectName(jsonLaunchLoanInfo.getString("projectName"));
			}
			if (jsonLaunchLoanInfo.containsKey("receiveAccount")) {
				loan.setReceiveAccount(jsonLaunchLoanInfo.getString("receiveAccount"));
			}
			if (jsonLaunchLoanInfo.containsKey("remainingAmount")) {
				loan.setRemainingAmount(jsonLaunchLoanInfo.getFloat("remainingAmount"));
			}
			if (jsonLaunchLoanInfo.containsKey("remark")) {
				loan.setRemark(jsonLaunchLoanInfo.getString("remark"));
			}
			if (jsonLaunchLoanInfo.containsKey("subject")) {
				loan.setSubject(jsonLaunchLoanInfo.getString("subject"));
			}
			// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
			String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
			loan.setCorpId(corpId);
			// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
			loan.setCreateTime(new Date());
			loan.setUserId(weixinUser.getUserid());
			loan.setUserName(weixinUser.getName());
			loan.setLoanNum(CommonUtil.geneSerialNum("JK"));
			loan.setLoanType(jsonLaunchLoanInfo.getString("loanType"));
			// 获取借款id
			String id = jsonLaunchLoanInfo.getString("id");
			// 是否存在id 草稿 重新发起 都会存在id
			boolean hasId = false;
			if (StringUtils.isNotEmpty(id)) {
				loan.setId(id);
				hasId = true;
				// 清除与审批相关的 人员
				entityAccountService.deleteByEntityId(loan.getId());
				// 删除与附件关系
				entityAccessoryService.deleteEntityAccessory(loan.getId());
			} else {
				loan.setId(CommonUtil.GeneGUID());
			}

			// 实体关系
			List<EntityAccount> entityAccountList = null;
			// 草稿状态下只存储
			if (Constants.CMD_DRAFT.equalsIgnoreCase(commandType)) {

			} else /** 发起审批操作 */
			if (Constants.CMD_GENERAL.equalsIgnoreCase(commandType)) {

				if (hasId) {
					loan.setStatus(CommonAppType.Status.待审核.value());
					loan.setApplyDate(new Date());
					loanDao.updateLoan(loan);
				} else {
					loan.setCreateTime(new Date());
					loan.setApplyDate(new Date());
					loan.setStatus(CommonAppType.Status.待审核.value());
					addLoan(loan);
				}
				// 审核人 抄送人
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
				entityAccountList = generateEntityAccount(jsonLaunchLoanInfo, loan.getId(), corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
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
					// 给审核人推送消息
					// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
					SystemCacheUtil.getInstance()
							.add(new LoanMsgExecutor(corpId, loan.getId(), shList.get(0).getAccountId(),
									"template_loan_sh"));
					// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
				} else {
					LOGGER.error("审核人不能为空!");
					throw new VyiyunException("审核人不能为空!");
				}
			}
			// 附件信息
			if (jsonLaunchLoanInfo.containsKey("accessoryInfo")) {
				entityAccessoryService.addEntityAccessory(loan.getId(),
						jsonLaunchLoanInfo.getJSONObject("accessoryInfo"));
			}
			return loan.getId();
		} else {
			LOGGER.warn("借款信息为空！");
		}
		return null;
	}

	@Override
	public DataResult getBeAuditRecord(Map<String, Object> params, int pageIndex, int pageSize) {
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
		List<Map<String, Object>> loanList = loanDao.getLoanBeAuditRecord(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(loanList)) {
			List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> temp : loanList) {
				temp.put(
						"statusDisplay",
						SystemCacheUtil.getInstance().getSystemStatusCache()
								.getSystemStatusName("Status", StringUtil.getString(temp.get("status"))));

				temp.put(
						"loanUseDisplay",
						SystemCacheUtil.getInstance().getSystemStatusCache()
								.getSystemStatusValue(params.get("corpId").toString(), "LoanUse", StringUtil.getString(temp.get("loanUse"))));

				temp.put("sapplyDate", DateUtil.dateToString((Date) temp.get("applyDate"), "MM月dd日"));
				temp.put(
						"loanTypeDisplay",
						SystemCacheUtil.getInstance().getSystemStatusCache()
								.getSystemStatusName("LoanType", StringUtil.getString(temp.get("loanType"))));

				dataMap.add(temp);
			}
			dataResult.setData(dataMap);
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}

	@Override
	public DataResult getAuditRecord(Map<String, Object> params, int pageIndex, int pageSize) {
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
		List<Map<String, Object>> loanList = loanDao.getLoanAuditRecord(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(loanList)) {
			List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> temp : loanList) {
				temp.put(
						"statusDisplay",
						SystemCacheUtil.getInstance().getSystemStatusCache()
								.getSystemStatus("Status", StringUtil.getString(temp.get("status"))));
				temp.put(
						"loanUseDisplay",
						SystemCacheUtil.getInstance().getSystemStatusCache()
								.getSystemStatusValue(StringUtil.getString(params.get("corpId")), "LoanUse", StringUtil.getString(temp.get("loanUse"))));
				temp.put("sapplyDate", DateUtil.dateToString((Date) temp.get("applyDate"), "MM月dd日"));
				temp.put(
						"loanTypeDisplay",
						SystemCacheUtil.getInstance().getSystemStatusCache()
								.getSystemStatusName("LoanType", StringUtil.getString(temp.get("loanType"))));
				dataMap.add(temp);
			}
			dataResult.setData(dataMap);
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}

	@Override
	public Object loanOperate(JSONObject jsonLoanInfo) {
		// 判断对象是否有参数
		if (!CollectionUtils.isEmpty(jsonLoanInfo)) {
			// 获取命令信息
			JSONObject jsonCommandInfo = jsonLoanInfo.getJSONObject("commandInfo");
			if (CollectionUtils.isEmpty(jsonCommandInfo)) {
				throw new VyiyunException("命令信息不能为空！");
			}
			String commandType = jsonCommandInfo.getString("commandType");
			if (StringUtils.isEmpty(commandType)) {
				throw new VyiyunException("命令类型不能为空！");
			}

			String id = jsonLoanInfo.getString("id");
			if (StringUtils.isEmpty(id)) {
				throw new VyiyunException("参数id不能为空！");
			}
			int type = jsonCommandInfo.getIntValue("commandType");
			// 获取当前用户
			WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
			Loan currentLoan = loanDao.getLoanById(id);
			if (null == currentLoan) {
				throw new VyiyunException("当前借款已被撤销！");
			}
			// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
			String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
			// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
			// 这里以数据类型为准 借款类型
			String loanType = currentLoan.getLoanType();
			// 实体账户id
			String eaId = null;
			// 实体账户
			EntityAccount entityAccount = null;
			// 排除催办 加签
			if (type != 9 && type != 8 && type != 7) {
				eaId = jsonLoanInfo.getString("eaId");
				if (StringUtils.isEmpty(id)) {
					throw new VyiyunException("参数eaId不能为空！");
				}
				entityAccount = entityAccountService.getEntityAccountById(eaId);
				if (null == entityAccount) {
					throw new VyiyunException(SpringContextHolder.getI18n("1002003", new String[] { "eaId", "" }, null));
				}
				if (!weixinUser.getUserid().equals(entityAccount.getAccountId())) {
					throw new VyiyunException(SpringContextHolder.getI18n("1002004",
							new String[] { weixinUser.getName() }, null));
				}
			}

			List<SystemStatus> systemStatusList = null;
			WeixinUser wUser = null;
			if (CommonAppType.Status.已通过.value().equals(currentLoan.getStatus())
					|| CommonAppType.Status.审核退回.value().equals(currentLoan.getStatus())
					|| CommonAppType.Status.借款退回.value().equals(currentLoan.getStatus())) {
				throw new VyiyunException("当前借款已处理!");
			}
			Loan loan = null;
			switch (type) {
			// 审批操作 通过
			case 1:
				if (CommonAppType.CommandType.同意.value().equals(entityAccount.getDealResult())) {
					throw new VyiyunException(SpringContextHolder.getI18n("1002005"));
				}
				entityAccount = new EntityAccount();
				entityAccount.setId(eaId);
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
				entityAccount.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
				entityAccount.setDealResult(CommonAppType.CommandType.同意.value());
				entityAccount.setRemark(jsonLoanInfo.getString("remark"));
				entityAccountService.updateEntityAccount(entityAccount);
				loan = new Loan();
				loan.setId(id);
				// 这里获取下一级审核人
				// 获取当前审核记录 获取下一级审核人
				Approvers approvers = approversService.getApproversByParentId(eaId);
				// 判断是否存在下一级审核人
				if (null != approvers) {
					// 如果当前是陈总，那么当前需要自动提交给张总 并消息提示
					loan.setStatus(CommonAppType.Status.审核中.value());
					loanDao.updateLoan(loan);
					// 获取下一级审核人处理
					entityAccount = new EntityAccount();
					entityAccount.setCorpId(corpId);
					entityAccount.setId(approvers.getId());
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
					// 给审核人推送消息
					// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
					SystemCacheUtil.getInstance().add(
							new LoanMsgExecutor(corpId, currentLoan.getId(), entityAccount.getAccountId(),
									"template_loan_sh"));
				} else {
					loan.setStatus(CommonAppType.Status.待报销.value());
					loanDao.updateLoan(loan);
					// 获取财务人员
					// 根据借款类型 分别添加财务人员
					SystemStatusCache<Object> systemStatusCache = SystemCacheUtil.getInstance().getSystemStatusCache();
					if ("0".equals(loanType)) {
						systemStatusList = systemStatusCache.getSystemStatus(corpId, "Loan1Financial");
					} else if ("1".equals(loanType)) {
						systemStatusList = systemStatusCache.getSystemStatus(corpId, "Loan2Financial");
					}
					if (CollectionUtils.isEmpty(systemStatusList)) {
						throw new VyiyunException("1000000");
					}

					entityAccount = new EntityAccount();
					entityAccount.setId(CommonUtil.GeneGUID());
					// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
					entityAccount.setCorpId(corpId);
					// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
					entityAccount.setEntityId(id);
					entityAccount.setAccountId(systemStatusList.get(0).getValue());
					entityAccount.setAccountName(systemStatusList.get(0).getName());
					entityAccount.setAccountType(CommonAppType.AccountType.U.value());
					WeixinContactCache<Object> weixinContactCache = (WeixinContactCache<Object>) SystemCacheUtil
							.getInstance().getWeixinContactCache();
					wUser = weixinContactCache.getUserById(entityAccount.getAccountId());
					if (wUser == null) {
						throw new VyiyunException("CorpId【" + corpId + "】借款财务人员配置错误!");
					}
					entityAccount.setAvatar(wUser.getAvatar());
					entityAccount.setPersonType(CommonAppType.PersonType.CW.value());
					entityAccount.setEntityType(CommonAppType.EntityType.JK.value());
					entityAccount.setDealResult("0");
					entityAccountService.addEntityAccount(entityAccount);
					// 给审核人推送消息
					// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
					SystemCacheUtil.getInstance().add(
							new LoanMsgExecutor(corpId, currentLoan.getId(), entityAccount.getAccountId(),
									"template_loan_cw"));
				}
				break;
			// 审批操作 拒绝
			case 2:
				if (CommonAppType.CommandType.拒绝.value().equals(entityAccount.getDealResult())) {
					throw new VyiyunException(SpringContextHolder.getI18n("1002005"));
				}
				entityAccount = new EntityAccount();
				entityAccount.setId(eaId);
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
				entityAccount.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
				entityAccount.setDealResult(CommonAppType.CommandType.拒绝.value());
				entityAccount.setRemark(jsonLoanInfo.getString("remark"));
				entityAccountService.updateEntityAccount(entityAccount);
				loan = new Loan();
				loan.setId(id);
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
				loan.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
				loan.setStatus(CommonAppType.Status.审核退回.value());
				loanDao.updateLoan(loan);
				// 给申请人发送消息
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
				SystemCacheUtil.getInstance().add(
						new LoanMsgExecutor(corpId, currentLoan.getId(), currentLoan.getUserId(),
								"template_loan_refuse"));
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
				break;
			// 财务操作 通过
			case 3:
				if (CommonAppType.CommandType.同意.value().equals(entityAccount.getDealResult())) {
					throw new VyiyunException(SpringContextHolder.getI18n("1002005"));
				}
				entityAccount = new EntityAccount();
				entityAccount.setId(eaId);
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
				entityAccount.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
				entityAccount.setRemark(jsonLoanInfo.getString("remark"));
				entityAccount.setDealResult(CommonAppType.CommandType.同意.value());
				entityAccountService.updateEntityAccount(entityAccount);
				loan = new Loan();
				loan.setId(id);
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
				loan.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
				loan.setStatus(CommonAppType.Status.已通过.value());
				loanDao.updateLoan(loan);
				// 给申请人发送通过消息
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
				SystemCacheUtil.getInstance()
						.add(new LoanMsgExecutor(corpId, currentLoan.getId(), currentLoan.getUserId(),
								"template_loan_pass"));
				break;
			// 财务操作 拒绝
			case 4:
				if (CommonAppType.CommandType.拒绝.value().equals(entityAccount.getDealResult())) {
					throw new VyiyunException(SpringContextHolder.getI18n("1002005"));
				}
				String personType = entityAccount.getPersonType();
				entityAccount = new EntityAccount();
				entityAccount.setId(eaId);
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
				entityAccount.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
				entityAccount.setDealResult(CommonAppType.CommandType.拒绝.value());
				entityAccount.setRemark(jsonLoanInfo.getString("remark"));
				entityAccountService.updateEntityAccount(entityAccount);
				loan = new Loan();
				loan.setId(id);
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
				loan.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
				if (CommonAppType.PersonType.SH.value().equals(personType)) {
					loan.setStatus(CommonAppType.Status.审核退回.value());
				} else if (CommonAppType.PersonType.CW.value().equals(personType)) {
					loan.setStatus(CommonAppType.Status.借款退回.value());
				}
				loanDao.updateLoan(loan);
				// 发送消息
				// 给申请人发送消息
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
				SystemCacheUtil.getInstance().add(
						new LoanMsgExecutor(corpId, currentLoan.getId(), currentLoan.getUserId(),
								"template_loan_refuse"));
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
				break;
			// 财务操作 执行
			case 5:
				entityAccount = new EntityAccount();
				entityAccount.setId(eaId);
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
				entityAccount.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
				entityAccount.setDealResult(CommonAppType.CommandType.同意.value());
				entityAccountService.updateEntityAccount(entityAccount);
				loan = new Loan();
				loan.setId(id);
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
				loan.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
				loan.setStatus(CommonAppType.Status.已通过.value());
				loanDao.updateLoan(loan);
				// 给申请人发送通过消息
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
				SystemCacheUtil.getInstance()
						.add(new LoanMsgExecutor(corpId, currentLoan.getId(), currentLoan.getUserId(),
								"template_loan_pass"));
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
				break;
			// 财务操作 拒绝
			case 6:
				loan = new Loan();
				loan.setId(id);
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
				loan.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
				loan.setStatus(CommonAppType.Status.借款退回.value());
				loanDao.updateLoan(loan);
				// 给申请人发送消息
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
				SystemCacheUtil.getInstance()

				.add(new LoanMsgExecutor(corpId, currentLoan.getId(), currentLoan.getUserId(), "template_loan_refuse"));
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
				break;
			// 加签
			case 7:
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
				List<EntityAccount> entityAccountList = generateEntityAccount(jsonLoanInfo, id, corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
				if (!CollectionUtils.isEmpty(entityAccountList)) {
					entityAccountService.addEntityAccount(entityAccountList.get(0));
					// 给审核人发送消息
					// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
					SystemCacheUtil.getInstance()

					.add(new LoanMsgExecutor(corpId, currentLoan.getId(), entityAccountList.get(0).getAccountId(),
							"template_loan_sh"));
					// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
				}
				break;
			// 催办
			case 8:
				// 参数校验
				String uId = jsonLoanInfo.getString("uId");
				eaId = jsonLoanInfo.getString("eaId");
				if (StringUtils.isEmpty(uId)) {
					throw new VyiyunException("待审核人不能为空!");
				}
				if (StringUtils.isEmpty(eaId)) {
					throw new VyiyunException("eaId参数不能为空!");
				}
				entityAccount = entityAccountService.getEntityAccountById(eaId);
				if (null == entityAccount) {
					if (null == entityAccount) {
						throw new VyiyunException(SpringContextHolder.getI18n("1002003", new String[] { "eaId", eaId },
								null));
					}
				}
				if (CommonAppType.PersonType.CW.value().equals(entityAccount.getPersonType())) {
					// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
					SystemCacheUtil.getInstance().add(new LoanMsgExecutor(corpId, id, uId, "template_loan_cw"));
					// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
				} else {
					// 给申请人发送消息
					// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
					SystemCacheUtil.getInstance().add(new LoanMsgExecutor(corpId, id, uId, "template_loan_sh"));
					// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
				}
				break;
			/* 添加借款申请撤销功能 */
			case 9:
				if (CommonAppType.Status.待审核.value().equals(currentLoan.getStatus())) {
					// 给审核人和抄送人发送消息
					// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
					entityAccount = new EntityAccount(id, corpId);
					// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
					List<EntityAccount> list = entityAccountService.getEntityAccount(entityAccount);
					StringBuffer uIds = new StringBuffer();
					if (list != null) {
						for (EntityAccount ea : list) {
							uIds.append("|").append(ea.getAccountId());
						}
						uIds.deleteCharAt(0);
					}
					HashMap<String, Object> themeMap = new HashMap<String, Object>();
					themeMap.put("userName", currentLoan.getUserName());
					themeMap.put("loanTypeDisplay", SystemCacheUtil.getInstance().getSystemStatusCache()
							.getSystemStatusName("LoanType", currentLoan.getLoanType()));
					// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen start.
					SystemCacheUtil.getInstance()

					.add(new LoanMsgExecutor(corpId, currentLoan.getId(), uIds.toString(), "template_loan_undo", null,
							themeMap));
					// 微依云 公共应用 CorpId追加修正 2016-01-08 By zb.shen end.
					// 删除相关信息
					entityAccountService.deleteByEntityId(id);
					ArrayList<Accessory> accessoryList = (ArrayList<Accessory>) accessoryService
							.getAccessoryByEntityId(id);
					if (accessoryList != null) {
						for (Accessory accessory : accessoryList) {
							accessoryService.deleteAccessoryById(accessory.getId());
						}
					}
					this.loanDao.deleteLoan(currentLoan);
				} else {
					throw new VyiyunException("借款已进入审核期，不能撤回了");
				}
				break;
			default:
				throw new VyiyunException("无效操作!");
			}
		} else {
			LOGGER.warn("借款操作信息为空！");
		}
		return null;
	}

	@Override
	public Loan getLoanById(String id) {
		return loanDao.getLoanById(id);
	}

	@Override
	public DataResult getLoanRecord(Loan loan, Map<String, Object> params, int pageIndex, int pageSize) {
		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPage(true);
		}
		if (pageSize != -1) {
			sqlQueryParameter.setPageSize(pageSize);
		}
		sqlQueryParameter.setParameter(loan);
		if (!CollectionUtils.isEmpty(params)) {
			sqlQueryParameter.getKeyValMap().putAll(params);
		}
		List<Loan> loanList = loanDao.getLoan(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(loanList)) {
			List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
			SystemStatusCache<Object> systemStatusCache = (SystemStatusCache<Object>) SystemCacheUtil.getInstance()
					.getSystemStatusCache();
			Map<String, Object> temp = null;
			for (Loan lo : loanList) {
				temp = lo.getPersistentState();
				temp.put("statusDisplay", systemStatusCache.getSystemStatusName("Status", lo.getStatus()));
				temp.put("loanUseDisplay", systemStatusCache.getSystemStatusValue(loan.getCorpId(), "LoanUse", lo.getLoanUse()));
				temp.put("loanTypeDisplay", systemStatusCache.getSystemStatusName("LoanType", lo.getLoanType()));
				temp.put("sapplyDate", DateUtil.dateToString(lo.getCreateTime(), "MM月dd日"));
				dataMap.add(temp);
			}
			dataResult.setData(dataMap);
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}

	@Override
	public ResponseEntity<byte[]> exportLoanListToExcel(JSONObject jsonSearchConditions) throws Exception {

		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();

		String userName = jsonSearchConditions.getString("userName");
		Date startTime = jsonSearchConditions.getDate("startTime");
		Date endTime = jsonSearchConditions.getDate("endTime");

		sqlQueryParameter.getKeyValMap().put("userNameLike", userName);
		sqlQueryParameter.getKeyValMap().put("startDate", startTime);
		sqlQueryParameter.getKeyValMap().put("endDate", endTime);
		// 查询
		Loan loan = new Loan();
		loan.setStatus(jsonSearchConditions.getString("status"));
		loan.setLoanNum(jsonSearchConditions.getString("loanNum"));
		loan.setLoanType(jsonSearchConditions.getString("loanType"));
		loan.setCorpId(jsonSearchConditions.getString("corpId"));
		sqlQueryParameter.setParameter(loan);
		List<Loan> loanList = loanDao.getLoan(sqlQueryParameter);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		LinkedHashMap<String, String> fieldMap = new LinkedHashMap<String, String>();
		fieldMap.put("loanNum", "借款单号");
		fieldMap.put("userId", "借款人工号");
		fieldMap.put("userName", "借款人姓名");
		fieldMap.put("applyDate", "申请日期");
		fieldMap.put("amount", "借款金额（元）");
		fieldMap.put("capitalAmount", "大写金额");
		String sheetName = "借款统计";
		if (loan.getLoanType().equals(CommonAppType.LoanType.对私.value())) {
			fieldMap.put("department", "借款部门");
			fieldMap.put("subject", "借款事由");
			sheetName = sheetName + "（对私）";
		}
		if (loan.getLoanType().equals(CommonAppType.LoanType.对公.value())) {
			fieldMap.put("company", "公司名称");
			fieldMap.put("contractAmount", "合同金额");
			fieldMap.put("remainingAmount", "剩余金额");
			sheetName = sheetName + "（对公）";
		}
		fieldMap.put("receiveAccount", "收款账号");
		fieldMap.put("statusDisplay", "审核状态");
		List<Map<String, Object>> excelList = new ArrayList<Map<String, Object>>();
		if (null != loanList) {
			for (int index = 0; index < loanList.size(); index++) {
				Loan tmp = loanList.get(index);
				Map<String, Object> excelMap = tmp.getPersistentState();
				excelMap.put(
						"statusDisplay",
						SystemCacheUtil.getInstance().getSystemStatusCache()
								.getSystemStatusName(Constants.SYSTEM_STATUS, tmp.getStatus()));
				excelMap.put(
						"loanUseDisplay",
						SystemCacheUtil.getInstance().getSystemStatusCache()
						.getSystemStatusValue(jsonSearchConditions.getString("corpId"), Constants.SYSTEM_LOAN_USE, tmp.getLoanUse()));
				excelMap.put("applyDate", DateUtil.dateToString(tmp.getApplyDate(), "yyyy-MM-dd"));
				excelList.add(excelMap);
			}
		}
		ExcelUtil.listToExcel(excelList, fieldMap, sheetName, bos);

		HttpHeaders headers = new HttpHeaders();
		String fileName = new String((sheetName + ".xls").getBytes("UTF-8"), "iso-8859-1");// 为了解决中文名称乱码问题
		headers.setContentDispositionFormData("attachment", fileName);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		return new ResponseEntity<byte[]>(bos.toByteArray(), headers, HttpStatus.CREATED);
	}

	@Override
	public Map<String, Object> getLoanDetail(String id, String corpId) {
		Map<String, Object> retData = new HashMap<String, Object>();
		Loan loan = loanDao.getLoanById(id);
		if (null != loan) {
			Map<String, Object> data = loan.getPersistentState();
			WeixinUser user = ((WeixinContactCache<Object>) SystemCacheUtil.getInstance().getWeixinContactCache())
					.getUserById(loan.getUserId());
			if (null == user) {
				user = new WeixinUser();
			}
			data.put("avatar", user.getAvatar());
			data.put("position", user.getPosition());
			data.put(
					"statusDisplay",
					SystemCacheUtil.getInstance().getSystemStatusCache()
							.getSystemStatusName(Constants.SYSTEM_STATUS, loan.getStatus()));

			if (loan.getLoanType().equals(CommonAppType.LoanType.对公.value())) {
				data.put(
						"loanUseDisplay",
						SystemCacheUtil.getInstance().getSystemStatusCache()
								.getSystemStatusValue(corpId, "LoanUse", loan.getLoanUse()));
				/*
				 * VyiyunUtils.getSystemStatusName(SystemCacheUtil.getInstance().
				 * getSystemStatusCache(), loan.getCorpId(),
				 * Constants.SYSTEM_LOAN_USE, loan.getLoanUse(), null));
				 */data.put("loanTypeDisplay", SystemCacheUtil.getInstance().getSystemStatusCache()
						.getSystemStatusName("LoanType", loan.getLoanType()));
				/*
				 * VyiyunUtils.getSystemStatusName(SystemCacheUtil.getInstance().
				 * getSystemStatusCache(), loan.getCorpId(),
				 * Constants.SYSTEM_LOAN_TYPE, loan.getLoanType(), null));
				 */}
			retData.put("loan", data);
			// 附件
			List<Accessory> accessoryList = accessoryService.getAccessoryByEntityId(id);
			if (!CollectionUtils.isEmpty(accessoryList)) {
				retData.put("accessoryInfo", accessoryList);
			}

			// 审核人
			EntityAccount entityAccount = new EntityAccount();
			entityAccount.setEntityId(id);
			entityAccount.setCorpId(corpId);
			entityAccount.setEntityType(CommonAppType.EntityType.JK.value());
			List<EntityAccount> entityAccountList = entityAccountService.getEntityAccount(entityAccount);
			if (!CollectionUtils.isEmpty(entityAccountList)) {
				retData.put("shList", entityAccountList);
			}
		}
		return retData;
	}

	@Override
	public int deleteRelatedRecord(String corpId, String loanUse) {
		Loan param = new Loan();
		param.setCorpId(corpId);
		param.setLoanUse(loanUse);
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		sqlQueryParameter.setParameter(param);
		List<Loan> loanList = loanDao.getLoan(sqlQueryParameter);
		int optCnt = Constants.ZERO;
		if (!CollectionUtils.isEmpty(loanList)) {
			Iterator<Loan> iter = loanList.iterator();
			List<String> ids = new ArrayList<String>();
			while (iter.hasNext()) {
				Loan tmp = iter.next();
				if (!tmp.getStatus().equals(CommonAppType.Status.待审核.value())
						&& !tmp.getStatus().equals(CommonAppType.Status.草稿.value())) {
					// 已在审核中的记录无法删除
					throw new VyiyunException("该借款用途存在正在借款流程中的记录，无法删除");
				} else {
					ids.add(tmp.getId());
				}
			}
			if (!ids.isEmpty()) {
				optCnt += loanDao.deleteByIds(ids);
				optCnt += accessoryDao.deleteAccessoryByLoanIds(ids);
				optCnt += entityAccessoryDao.deleteEntityAccessoryByLoanIds(ids);
				optCnt += entityAccountDao.deleteEntityAccountByEntityIds(ids);
			}
		}
		return optCnt;
	}

	@Override
	public int addLoan(Loan... loan) {
		if (loan.length < 1) {
			return 0;
		}
		return loanDao.addLoan(Arrays.asList(loan));
	}

}
