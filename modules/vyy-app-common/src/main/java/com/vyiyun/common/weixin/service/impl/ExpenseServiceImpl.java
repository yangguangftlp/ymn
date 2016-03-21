/**
 * 
 */
package com.vyiyun.common.weixin.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.dao.EntityProgressMapper;
import com.vyiyun.common.weixin.dao.ExpenseFeeMapper;
import com.vyiyun.common.weixin.dao.ExpenseMapper;
import com.vyiyun.common.weixin.entity.Approvers;
import com.vyiyun.common.weixin.entity.EntityProgress;
import com.vyiyun.common.weixin.entity.Expense;
import com.vyiyun.common.weixin.entity.ExpenseFee;
import com.vyiyun.common.weixin.enums.CommonAppType;
import com.vyiyun.common.weixin.enums.CommonAppType.PersonType;
import com.vyiyun.common.weixin.service.IApproversService;
import com.vyiyun.common.weixin.service.IExpenseFeeService;
import com.vyiyun.common.weixin.service.IExpenseService;
import com.vyiyun.weixin.cache.impl.SystemStatusCache;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.dao.EntityAccountMapper;
import com.vyiyun.weixin.entity.Accessory;
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
@Service("expenseService")
public class ExpenseServiceImpl extends AbstractBaseService implements IExpenseService {

	private static final Logger LOGGER = Logger.getLogger(ExpenseServiceImpl.class);

	/**
	 * 报销dao
	 */
	@Autowired
	private ExpenseMapper expenseDao;

	/**
	 * 费用
	 */
	@Autowired
	private IExpenseFeeService expenseFeeService;

	/**
	 * 审核人服务
	 */
	@Autowired
	private IApproversService approversService;

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

	@Autowired
	private EntityAccountMapper entityAccountDao;

	@Autowired
	private ExpenseFeeMapper expenseFeeDao;

	@Autowired
	private EntityProgressMapper entityProgressDao;

	@Override
	public void addExpense(Expense expense) {
		expenseDao.addExpense(expense);
	}

	@Override
	public List<Expense> getExpense(Expense expense) {
		return expenseDao.getExpense(expense);
	}

	@Override
	public void deleteById(String id) {
		expenseDao.deleteById(id);
	}

	@Override
	public void updateExpense(Expense expense) {
		expenseDao.updateExpense(expense);
	}

	@Override
	public void doApply(JSONObject jsonExpenseApplyInfoObj) {
		if (null != jsonExpenseApplyInfoObj) {
			// 获取报销类型
			String operationType = jsonExpenseApplyInfoObj.getString("operationType");

			if (StringUtils.isEmpty(operationType)) {
				throw new VyiyunException("参数operationType值不能为空!");
			}
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
			String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
			// 获取当前用户
			WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
			// 构造报销信息
			Expense expense = new Expense();
			expense.setId(jsonExpenseApplyInfoObj.getString("id"));
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
			expense.setCorpId(corpId);
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
			expense.setUserId(weixinUser.getUserid());
			expense.setUserName(weixinUser.getName());
			expense.setTheme(jsonExpenseApplyInfoObj.getString("theme"));
			expense.setReason(jsonExpenseApplyInfoObj.getString("reason"));
			expense.setAmount(Float.valueOf(jsonExpenseApplyInfoObj.getString("amount")));
			expense.setAnnexCount(Integer.valueOf(jsonExpenseApplyInfoObj.getString("annexCount")));
			expense.setDepartment(jsonExpenseApplyInfoObj.getString("department"));
			expense.setCreateTime(new Date());
			expense.setStatus(CommonAppType.Status.草稿.value());
			// 获取标示判断是否是再次发起操作
			boolean isAgain = false;
			if (jsonExpenseApplyInfoObj.containsKey("isAgain")) {
				isAgain = jsonExpenseApplyInfoObj.getBooleanValue("isAgain");
			}
			if (isAgain) {
				// 清除信息
				EntityAccount entityAccount = new EntityAccount();
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
				entityAccount.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
				entityAccount.setEntityId(expense.getId());
				entityAccount.setInvalid(true);
				entityAccountService.updateEntityAccount(entityAccount);
				// 清除附件
				entityAccessoryService.deleteEntityAccessory(expense.getId());
			}
			// 获取费用信息
			JSONArray jsonExpenseFee = jsonExpenseApplyInfoObj.getJSONArray("expensefeeInfo");
			if (!CollectionUtils.isEmpty(jsonExpenseFee)) {
				List<ExpenseFee> expenseFeeList = new ArrayList<ExpenseFee>();
				ExpenseFee expenseFee = null;
				JSONObject jsonObject = null;
				for (int i = 0, size = jsonExpenseFee.size(); i < size; i++) {
					jsonObject = jsonExpenseFee.getJSONObject(i);
					// 校验前端数据有效
					if (null != jsonObject && !jsonObject.containsKey("id")) {
						expenseFee = new ExpenseFee();
						expenseFee.setCorpId(corpId);
						expenseFee.setCategory(jsonObject.getString("category"));
						expenseFee.setId(CommonUtil.GeneGUID());
						expenseFee.setExpenseId(expense.getId());
						expenseFee.setMoney(jsonObject.getFloat("money"));
						expenseFeeList.add(expenseFee);
					}
				}
				// 添加费用
				if (!CollectionUtils.isEmpty(expenseFeeList)) {
					expenseFeeService.addExpenseFee(expenseFeeList.toArray(new ExpenseFee[] {}));
				}
			}

			// 审核人 财务
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
			List<EntityAccount> entityAccountList = generateEntityAccount(jsonExpenseApplyInfoObj, expense.getId(),
					corpId);
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
			EntityAccount entityAccount = null;
			if ("0".equals(operationType))/** 正常审核 */
			{
				// 待审核
				expense.setStatus(CommonAppType.Status.待审核.value());
			} else if ("1".equals(operationType))/** 直接财务审核 */
			{
				// 报销中
				expense.setStatus(CommonAppType.Status.待报销.value());
				// 去掉审核人员
				Iterator<EntityAccount> it = entityAccountList.iterator();
				while (it.hasNext()) {
					entityAccount = it.next();
					if (CommonAppType.PersonType.SH.value().equals(entityAccount.getPersonType())) {
						it.remove();
					}
				}
			} else {
				throw new VyiyunException("无效的operationType值!");
			}
			expense.setExpenseType(operationType);
			// 是否重新发起
			if (isAgain) {
				expenseDao.updateExpense(expense);
			} else {
				expense.setExpenseNum(CommonUtil.geneExpenseNum());
				expenseDao.addExpense(expense);
			}
			// 审核人、报销人
			List<EntityAccount> shList = new ArrayList<EntityAccount>();
			List<EntityAccount> cwList = new ArrayList<EntityAccount>();
			if (!CollectionUtils.isEmpty(entityAccountList)) {
				// 如果当前提交人 是审核人 或者报销恩 那么抛异常
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
					} else if (CommonAppType.PersonType.CW.value().equals(ea.getPersonType())) {
						cwList.add(ea);
					}
				}
				if (shList.size() > 0) {
					cwList.add(shList.get(0));
				}
				if (approversList.size() > 0) {
					approversList.remove(0);
					approversService.addApprovers(approversList.toArray(new Approvers[] {}));
				}
				entityAccountService.addEntityAccount(cwList.toArray(new EntityAccount[] {}));
			}
			// 附件信息
			if (jsonExpenseApplyInfoObj.containsKey("accessoryInfo")) {
				accessoryService.addAccessory(expense.getId(), jsonExpenseApplyInfoObj.getJSONObject("accessoryInfo"));
			}
			// 发送消息通知
			String toUser = null;
			// 如果存在审核人
			if (shList.size() > 0) {
				toUser = shList.get(0).getAccountId();
			} else {
				toUser = cwList.get(0).getAccountId();
			}
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("userName", expense.getUserName());
			dataMap.put("theme", expense.getTheme());
			Map<String, Object> msgParams = new HashMap<String, Object>();
			msgParams.put("toUser", toUser);
			msgParams.put("operationType", operationType);
			msgParams.put("expenseId", expense.getId());
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
			msgParams.put("corpId", corpId);
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			dataList.add(msgParams);
			dataList.add(dataMap);
			SystemCacheUtil.getInstance().add(new AbstMsgExecutor(dataList) {
				/**
				 * 
				 */
				private static final long serialVersionUID = -3558067326593616865L;

				@Override
				public void set(Object obj) {
				}

				@Override
				public String getName() {
					return "发送报销申请信息...";
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
					String toUser = StringUtil.getString(msgParams.get("toUser"));
					String expenseId = StringUtil.getString(msgParams.get("expenseId"));
					// 需要考虑 用户数过大 微信一次最多1000 个用户
					// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
					SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance().getSystemConfigCache()
							.getSystemConfig("system", "weburl");
					// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
					String expenseAppId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH, "expense_agentid"));
					if (StringUtils.isNotEmpty(toUser.toString())) {
						// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
						String content = new WeixinMessageBase(toUser.toString(), WeixinMsgType.text, expenseAppId,
								WeixinMessageUtil.generateLinkUrlMsg("template_expense_sh", dataMap,
										systemConfig.getValue(), new Object[] { _corpId, expenseId })).toJson();
						// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
						sendMessage(content);
					}
				}
			});
			// 发送结束
		} else {
			LOGGER.warn("请假信息为空！");
		}
	}

	@Override
	public DataResult queryExpenseRecord(Expense expense, Map<String, Object> params, int pageIndex, int pageSize) {
		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1 && pageSize != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPageSize(pageSize);
			sqlQueryParameter.setPage(true);
		}
		sqlQueryParameter.setParameter(expense);
		if (!CollectionUtils.isEmpty(params)) {
			sqlQueryParameter.getKeyValMap().putAll(params);
		}
		List<Expense> expenseList = expenseDao.queryExpenseByPage(sqlQueryParameter);

		if (!CollectionUtils.isEmpty(expenseList)) {
			List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
			Map<String, Object> temp = null;
			for (Expense e : expenseList) {
				temp = e.getPersistentState();
				temp.put(
						"statusDisplay",
						SystemCacheUtil.getInstance().getSystemStatusCache()
								.getSystemStatusName("Status", e.getStatus()));
				temp.put("screateTime", DateUtil.dateToString(e.getCreateTime(), "MM月dd日 HH:mm"));
				dataMap.add(temp);
			}
			dataResult.setData(dataMap);
		}
		if (sqlQueryParameter.isPage()) {
			// BUG #199 修改 by zb.shen 2016-01-22 start.
			// dataResult.setTotal((int)
			// expenseDao.queryExpenseCount(sqlQueryParameter));
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
			// BUG #199 修改 by zb.shen 2016-01-22 end.
		}
		return dataResult;
	}

	@Override
	public Map<String, Object> getExpenseInfoById(String id) {

		Map<String, Object> resutlDataMap = new HashMap<String, Object>();
		Expense expense = new Expense();
		expense.setId(id);
		expense = expenseDao.getExpenseById(id);
		if (null != expense) {
			resutlDataMap.put("expenseInfo", expense);
		}
		EntityAccount entityAccount = new EntityAccount();
		entityAccount.setEntityId(id);
		entityAccount.setEntityType(CommonAppType.EntityType.BX.value());
		// 实体账户信息
		List<EntityAccount> entityAccountList = entityAccountService.getEntityAccount(entityAccount);
		if (CollectionUtils.isEmpty(entityAccountList)) {
			Map<String, List<EntityAccount>> dataMap = new HashMap<String, List<EntityAccount>>();
			for (int i = 0, size = entityAccountList.size(); i < size; i++) {
				entityAccount = entityAccountList.get(i);
				if (CommonAppType.PersonType.SH.value().equals(entityAccount.getPersonType())) {
					if (!dataMap.containsKey("auditor")) {
						dataMap.put("auditor", new ArrayList<EntityAccount>());
					}
					dataMap.get("auditor").add(entityAccount);
				} else if (CommonAppType.PersonType.CS.value().equals(entityAccount.getPersonType())) {
					if (!dataMap.containsKey("cc")) {
						dataMap.put("cc", new ArrayList<EntityAccount>());
					}
					dataMap.get("cc").add(entityAccount);
				}
			}
			resutlDataMap.putAll(dataMap);
		}
		// 处理附件信息
		Accessory accessory = new Accessory();
		accessory.setId(id);
		List<Accessory> accessoryList = accessoryService.getAccessory(accessory);
		if (CollectionUtils.isEmpty(accessoryList)) {
			resutlDataMap.put("accessoryInfor", accessoryList);
		}
		return resutlDataMap;
	}

	@Override
	public void doAudit(JSONObject jsonExpenseAuditInfo) {
		if (!CollectionUtils.isEmpty(jsonExpenseAuditInfo)) {
			String expenseId = jsonExpenseAuditInfo.getString("expenseId");
			JSONObject jsonCommandInfoObj = jsonExpenseAuditInfo.getJSONObject("commandInfo");
			String commandType = jsonCommandInfoObj.getString("commandType");
			// 获取审核人 抄送者
			List<EntityAccount> entityAccountList = new ArrayList<EntityAccount>();
			EntityAccount entityAccount = null;
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
			String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
			// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
			// 同意
			Expense currentExpense = expenseDao.getExpenseById(expenseId);
			if (null == currentExpense) {
				throw new VyiyunException("当前报销已被撤销！");
			}
			if (CommonAppType.Status.审核退回.value().equals(currentExpense.getStatus())
					|| CommonAppType.Status.报销退回.value().equals(currentExpense.getStatus())
					|| CommonAppType.Status.已报销.value().equals(currentExpense.getStatus())) {
				throw new VyiyunException("当前报销已处理!");
			}
			if (Constants.CMD_GENERAL.equalsIgnoreCase(commandType)) {
				// 如果没有下一级审核人及审核结束
				// 如果没有下一级选择人那么就结束报销
				// 区分 财务和审核人员
				entityAccount = new EntityAccount();
				entityAccount.setId(jsonExpenseAuditInfo.getString("entityAccountId"));
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
				entityAccount.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
				entityAccount.setEntityId(expenseId);
				entityAccount.setAccountId(HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid());
				entityAccount.setRemark(jsonExpenseAuditInfo.getString("remark"));
				entityAccount.setDealResult(CommonAppType.CommandType.同意.value());
				entityAccountService.updateEntityAccount(entityAccount);
				// 同意
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.

				// 这里需要读取配置

				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
				// 判断是否存在下一级审核人
				Expense expense = new Expense();
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
				expense.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
				expense.setId(expenseId);

				Approvers approvers = approversService.getApproversByParentId(entityAccount.getId());
				// 判断是否存在下一级审核人
				if (null != approvers) {
					expense.setStatus(CommonAppType.Status.审核中.value());
					expenseDao.updateExpense(expense);
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
					msgParams.put("expenseId", expenseId);
					// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
					msgParams.put("corpId", corpId);
					// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
					SystemCacheUtil.getInstance().add(new AbstMsgExecutor(msgParams) {
						/**
						 * 
						 */
						private static final long serialVersionUID = -5641888758822366504L;

						@SuppressWarnings("unchecked")
						@Override
						public void execute() throws Exception {
							Map<String, Object> msgParams = (Map<String, Object>) getObj();
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
							String _corpId = msgParams.get("corpId").toString();
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
							ExpenseMapper expenseDao = (ExpenseMapper) SpringContextHolder.getBean(ExpenseMapper.class);
							Expense expense = expenseDao.getExpenseById(StringUtil.getString(msgParams.get("expenseId")));
							Map<String, Object> dataMap = new HashMap<String, Object>();
							dataMap.put("userName", expense.getUserName());
							dataMap.put("theme", expense.getTheme());
							// 催办提醒：
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
							SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance()
									.getSystemConfigCache().getSystemConfig("system", "weburl");
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
							String signAppId = convertAppId(ConfigUtil
									.get(Constants.WEIXIN_APP_PATH, "expense_agentid"));
							String uId = StringUtil.getString(msgParams.get("uId"));
							String expenseId = StringUtil.getString(msgParams.get("expenseId"));
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
							String content = new WeixinMessageBase(uId, WeixinMsgType.text, signAppId,
									WeixinMessageUtil.generateLinkUrlMsg("template_expense_sh", dataMap,
											systemConfig.getValue(), new Object[] { _corpId, expenseId })).toJson();
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
							sendMessage(content);
						}

						@Override
						public String getName() {
							return "提醒下一级审核人审核...";
						}
					});
				} else {
					// 获取当前审核人
					entityAccount = entityAccountService.getEntityAccountById(jsonExpenseAuditInfo
							.getString("entityAccountId"));
					if (CommonAppType.PersonType.CW.value().equals(entityAccount.getPersonType())) {
						// 如果当前人是财务 即报销完成
						expense.setStatus(CommonAppType.Status.已报销.value());
						expense.setActualCost(jsonExpenseAuditInfo.getFloat("actualCost"));
						expense.setEndTime(new Date());

						// 发送消息通知当前报销者
						Map<String, Object> msgParams = new HashMap<String, Object>();
						// 获取系统配置财务人员
						msgParams.put("uId", currentExpense.getUserId());
						msgParams.put("expenseId", expenseId);

						SystemCacheUtil.getInstance().add(new AbstMsgExecutor(msgParams) {
							/**
							 * 
							 */
							private static final long serialVersionUID = 1026534039586977733L;

							@SuppressWarnings("unchecked")
							@Override
							public void execute() throws Exception {
								Map<String, Object> msgParams = (Map<String, Object>) getObj();
								SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance()
										.getSystemConfigCache().getSystemConfig("system", "weburl");
								String expenseAppId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH,
										"expense_agentid"));
								String uId = StringUtil.getString(msgParams.get("uId"));
								String expenseId = StringUtil.getString(msgParams.get("expenseId"));
								Map<String, Object> themeMap = new HashMap<String, Object>();
								themeMap.put("statusDisplay", "通过");
								themeMap.put("shTime", DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm"));
								String content = new WeixinMessageBase(uId, WeixinMsgType.text, expenseAppId,
										WeixinMessageUtil.generateLinkUrlMsg("template_myExpenseStatus", themeMap,
												systemConfig.getValue(), new Object[] { corpId, expenseId })).toJson();
								sendMessage(content);
							}

							@Override
							public String getName() {
								return "报销通过信息...";
							}
						});

					} else {
						expense.setStatus(CommonAppType.Status.待报销.value());
						// 给报销审批者发送消息
						entityAccount = new EntityAccount();
						entityAccount.setEntityId(currentExpense.getId());
						entityAccount.setPersonType(PersonType.CW.value());
						entityAccountList = entityAccountService.getEntityAccount(entityAccount);
						if (!CollectionUtils.isEmpty(entityAccountList)) {
							// 发送消息通知
							Map<String, Object> dataMap = new HashMap<String, Object>();
							dataMap.put("userName", currentExpense.getUserName());
							dataMap.put("theme", currentExpense.getTheme());
							Map<String, Object> msgParams = new HashMap<String, Object>();
							msgParams.put("toUser", entityAccountList.get(0).getAccountId());
							msgParams.put("expenseId", currentExpense.getId());
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
							// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
							List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
							dataList.add(msgParams);
							dataList.add(dataMap);
							SystemCacheUtil.getInstance().add(new AbstMsgExecutor(dataList) {
								/**
							 * 
							 */
								private static final long serialVersionUID = -3558067326593616865L;

								@Override
								public void set(Object obj) {
								}

								@Override
								public String getName() {
									return "发送报销申请信息...";
								}

								@Override
								@SuppressWarnings("unchecked")
								public void execute() throws Exception {
									List<Map<String, Object>> dataList = (List<Map<String, Object>>) getObj();
									Map<String, Object> msgParams = dataList.get(0);
									// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen
									// start.
									// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen
									// end.
									Map<String, Object> dataMap = dataList.get(1);
									String toUser = StringUtil.getString(msgParams.get("toUser"));
									String expenseId = StringUtil.getString(msgParams.get("expenseId"));
									// 需要考虑 用户数过大 微信一次最多1000 个用户
									// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen
									// start.
									SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance()
											.getSystemConfigCache().getSystemConfig("system", "weburl");
									// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen
									// end.
									String expenseAppId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH,
											"expense_agentid"));
									if (StringUtils.isNotEmpty(toUser.toString())) {
										// 微依云 公共应用 CorpId追加修正 2016-01-06 By
										// zb.shen
										// start.
										String content = new WeixinMessageBase(toUser.toString(), WeixinMsgType.text,
												expenseAppId, WeixinMessageUtil.generateLinkUrlMsg(
														"template_expense_sh", dataMap, systemConfig.getValue(),
														new Object[] { this.corpId, expenseId })).toJson();
										// 微依云 公共应用 CorpId追加修正 2016-01-06 By
										// zb.shen
										// end.
										sendMessage(content);
									}
								}
							});
						}
					}
					expenseDao.updateExpense(expense);
				}
			} else /** 催办 */
			if (Constants.CMD_REMIND.equalsIgnoreCase(commandType)) {
				Map<String, Object> msgParams = new HashMap<String, Object>();
				// 参数校验
				String uId = jsonExpenseAuditInfo.getString("uId");
				if (StringUtils.isEmpty(uId)) {
					throw new VyiyunException("待审核人不能为空!");
				}

				msgParams.put("uId", uId);
				msgParams.put("expenseId", expenseId);
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
				msgParams.put("corpId", corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
				SystemCacheUtil.getInstance().add(new AbstMsgExecutor(msgParams) {
					/**
					 * 
					 */
					private static final long serialVersionUID = -6096895351762699804L;

					@SuppressWarnings("unchecked")
					@Override
					public void execute() throws Exception {
						Map<String, Object> msgParams = (Map<String, Object>) getObj();
						// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
						String _corpId = msgParams.get("corpId").toString();
						// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
						Expense expense = expenseDao.getExpenseById(StringUtil.getString(msgParams.get("expenseId")));
						Map<String, Object> dataMap = new HashMap<String, Object>();
						dataMap.put("userName", expense.getUserName());
						dataMap.put("theme", expense.getTheme());
						// 催办提醒：
						// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
						SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance().getSystemConfigCache()
								.getSystemConfig("system", "weburl");
						// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
						String expenseAppId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH, "expense_agentid"));
						String uId = StringUtil.getString(msgParams.get("uId"));
						String expenseId = StringUtil.getString(msgParams.get("expenseId"));
						// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
						String content = new WeixinMessageBase(uId, WeixinMsgType.text, expenseAppId, WeixinMessageUtil
								.generateLinkUrlMsg("template_expenseRemind", dataMap, systemConfig.getValue(),
										new Object[] { _corpId, expenseId })).toJson();
						// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
						sendMessage(content);
					}

					@Override
					public String getName() {
						return "发布催办信息...";
					}
				});
			} else /** 退回 */
			if (Constants.CMD_ROLLBACK.equalsIgnoreCase(commandType)) {
				// 所有参入者记录 修改为作废
				String entityAccountId = jsonExpenseAuditInfo.getString("entityAccountId");
				entityAccount = new EntityAccount();
				entityAccount.setId(entityAccountId);
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
				entityAccount.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
				entityAccount.setRemark(jsonExpenseAuditInfo.getString("remark"));
				entityAccount.setDealResult(CommonAppType.CommandType.退回.value());
				entityAccountService.updateEntityAccount(entityAccount);

				Expense expense = new Expense();
				expense.setId(expenseId);
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen start.
				expense.setCorpId(corpId);
				// 微依云 公共应用 CorpId追加修正 2016-01-06 By zb.shen end.
				// 当前审核人类型
				entityAccount = entityAccountService.getEntityAccountById(entityAccountId);
				String statusDisplay = null;
				if (CommonAppType.PersonType.SH.value().equals(entityAccount.getPersonType())) {
					expense.setStatus(CommonAppType.Status.审核退回.value());
					statusDisplay = "审核退回";
				} else if (CommonAppType.PersonType.CW.value().equals(entityAccount.getPersonType())) {
					expense.setStatus(CommonAppType.Status.报销退回.value());
					statusDisplay = "报销退回";
				} else {
					throw new VyiyunException("无效的人员类型!");
				}
				expenseDao.updateExpense(expense);
				// 发送消息通知当前报销者
				Map<String, Object> msgParams = new HashMap<String, Object>();
				// 获取系统配置财务人员
				msgParams.put("uId", currentExpense.getUserId());
				msgParams.put("expenseId", expenseId);
				msgParams.put("statusDisplay", statusDisplay);
				SystemCacheUtil.getInstance().add(new AbstMsgExecutor(msgParams) {
					/**
					 * 
					 */
					private static final long serialVersionUID = -5124344287114926156L;

					@SuppressWarnings("unchecked")
					@Override
					public void execute() throws Exception {
						Map<String, Object> msgParams = (Map<String, Object>) getObj();
						SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance().getSystemConfigCache()
								.getSystemConfig("system", "weburl");
						String expenseAppId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH, "expense_agentid"));
						String uId = StringUtil.getString(msgParams.get("uId"));
						String expenseId = StringUtil.getString(msgParams.get("expenseId"));
						Map<String, Object> themeMap = new HashMap<String, Object>();
						themeMap.put("statusDisplay", StringUtil.getString(msgParams.get("statusDisplay")));
						String content = new WeixinMessageBase(uId, WeixinMsgType.text, expenseAppId, WeixinMessageUtil
								.generateLinkUrlMsg("template_myExpenseStatus", themeMap, systemConfig.getValue(),
										new Object[] { corpId, expenseId })).toJson();
						sendMessage(content);
					}

					@Override
					public String getName() {
						return "发布报销退回信息...";
					}
				});

			}
			/* 撤销报销申请 */
			else if (Constants.CMD_UNDO.equalsIgnoreCase(commandType)) {
				if (("1".equals(currentExpense.getExpenseType())
						&& CommonAppType.Status.待报销.value().equals(currentExpense.getStatus())) || ("0"
						.equals(currentExpense.getExpenseType()) && CommonAppType.Status.待审核.value().equals(
						currentExpense.getStatus()))) {
					// 发送消息通知审核抄送人
					entityAccount = new EntityAccount();
					entityAccount.setEntityId(expenseId);
					entityAccountList = entityAccountService.getEntityAccount(entityAccount);
					StringBuilder uIds = new StringBuilder();
					if (entityAccountList != null) {
						for (EntityAccount ea : entityAccountList) {
							uIds.append("|").append(ea.getAccountId());
						}
						uIds.deleteCharAt(0);
					}
					// 获取系统配置财务人员
					Map<String, Object> msgParams = new HashMap<String, Object>();
					msgParams.put("uId", uIds.toString());
					msgParams.put("expenseId", expenseId);
					Map<String, Object> themeMap = new HashMap<String, Object>();
					themeMap.put("statusDisplay", "撤销");
					themeMap.put("shUser", currentExpense.getUserName());
					List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
					dataList.add(msgParams);
					dataList.add(themeMap);

					SystemCacheUtil.getInstance().add(new AbstMsgExecutor(dataList) {
						/**
						 * 
						 */
						private static final long serialVersionUID = -5124344287114926156L;

						@SuppressWarnings("unchecked")
						@Override
						public void execute() throws Exception {
							List<Map<String, Object>> dataList = (List<Map<String, Object>>) getObj();
							Map<String, Object> msgParams = dataList.get(0);
							Map<String, Object> themeMap = dataList.get(1);
							SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance()
									.getSystemConfigCache().getSystemConfig("system", "weburl");
							String corpId = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "corpId");
							String expenseAppId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH,
									"en_expense_agentid"));
							String uId = StringUtil.getString(msgParams.get("uId"));
							String expenseId = StringUtil.getString(msgParams.get("expenseId"));

							String content = new WeixinMessageBase(uId, WeixinMsgType.text, expenseAppId,
									WeixinMessageUtil.generateLinkUrlMsg("template_enExpense_undo", themeMap,
											systemConfig.getValue(), new Object[] { corpId, expenseId })).toJson();
							sendMessage(content);
						}

						@Override
						public String getName() {
							return "发布报销退回信息...";
						}
					});
					// 做删除操作
					entityAccountService.deleteByEntityId(expenseId);
					entityAccessoryService.deleteEntityAccessory(expenseId);
					List<Accessory> accessoryList = accessoryService.getAccessoryByEntityId(expenseId);
					if (accessoryList != null) {
						for (Accessory accessory : accessoryList) {
							accessoryService.deleteAccessoryById(accessory.getId());
						}
					}
					expenseFeeService.deleteExpenseFeeByExpenseId(expenseId);
					deleteById(expenseId);
				} else {
					throw new VyiyunException("报销申请已进入审核期，不能撤回了");
				}
			}
		} else {
			LOGGER.warn("报销审核参数为空！");
		}
	}

	@Override
	public Expense getExpenseById(String id) {
		return expenseDao.getExpenseById(id);
	}

	@Override
	public DataResult queryAuditRecord(Expense expense, Map<String, Object> params, int pageIndex, int pageSize) {
		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPage(true);
		}
		if (pageSize != -1) {
			sqlQueryParameter.setPageSize(pageSize);
		}
		sqlQueryParameter.setParameter(expense);
		if (!CollectionUtils.isEmpty(params)) {
			sqlQueryParameter.getKeyValMap().putAll(params);
		}
		List<Expense> expenseList = expenseDao.queryExpenseAuditRecord(sqlQueryParameter);

		if (!CollectionUtils.isEmpty(expenseList)) {
			List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
			Map<String, Object> temp = null;
			for (Expense e : expenseList) {
				temp = e.getPersistentState();
				temp.put(
						"statusDisplay",
						SystemCacheUtil.getInstance().getSystemStatusCache()
								.getSystemStatusName("Status", e.getStatus()));

				temp.put("screateTime", DateUtil.dateToString(e.getCreateTime(), "MM月dd日 HH:mm"));
				dataMap.add(temp);
			}
			dataResult.setData(dataMap);
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}

	@Override
	public Map<String, Object> getExpenseDataById(String id) {
		Map<String, Object> dataMap = new HashMap<String, Object>(); // 返回数据映射
		// 1、获取报销信息
		Expense expense = expenseDao.getExpenseById(id);
		if (null == expense) {
			throw new VyiyunBusinessException("记录已被撤销或不存在!");
		}
		dataMap.put("expenseInfo", expense);
		dataMap.put("stautsDisplay",
				SystemCacheUtil.getInstance().getSystemStatusCache().getSystemStatusName("Status", expense.getStatus()));

		// 获取审核相关人员
		EntityAccount entityAccount = new EntityAccount();
		entityAccount.setEntityId(id);
		entityAccount.setCorpId(expense.getCorpId());
		entityAccount.setEntityType(CommonAppType.EntityType.BX.value());

		List<EntityAccount> entityAccountList = entityAccountDao.getEntityAccount(entityAccount);
		// 权限用户
		List<String> permissionUsers = new ArrayList<String>();
		// 拒绝
		EntityAccount dealResult = null;

		if (!CollectionUtils.isEmpty(entityAccountList)) {
			for (EntityAccount ea : entityAccountList) {
				// 获取有效数据
				if (!ea.getInvalid()) {
					if (CommonAppType.PersonType.CW.value().equals(ea.getPersonType())) {
						if (!"0".equals(ea.getDealResult())) {
							dealResult = ea;
						}
					}
				}
				// 后续考虑
				permissionUsers.add(ea.getAccountId());
			}
			dataMap.put("shList", entityAccountList);
		}

		// 获取报销费用
		ExpenseFee expenseFee = new ExpenseFee();
		expenseFee.setExpenseId(id);
		expenseFee.setCorpId(expense.getCorpId());
		List<ExpenseFee> listExpenseFee = expenseFeeDao.getExpenseFee(expenseFee);
		if (!CollectionUtils.isEmpty(listExpenseFee)) {
			List<Map<String, Object>> tmpMap = new ArrayList<Map<String, Object>>();
			Map<String, Object> temp = null;
			SystemStatusCache<Object> systemStatusCache = SystemCacheUtil.getInstance().getSystemStatusCache();
			for (ExpenseFee ex : listExpenseFee) {
				temp = ex.getPersistentState();
				temp.put("categoryDisplay", systemStatusCache.getSystemStatusValue(HttpRequestUtil.getInst()
						.getCurrentCorpId(), Constants.COST_CATEGORY, ex.getCategory()));

				tmpMap.add(temp);
			}
			dataMap.put("listExpenseFee", tmpMap);
		}

		// 批复结果
		// 报销已完成
		if (CommonAppType.Status.已报销.value().equals(expense.getStatus())) {
			dataMap.put("expenseStatus", CommonAppType.Status.已报销.value());
			if (null != dealResult) {
				dataMap.put("remark", dealResult.getRemark());
			}
		} else // 报销已完成
		if (CommonAppType.Status.报销退回.value().equals(expense.getStatus())) {
			// 获取最后审批结果
			dataMap.put("expenseStatus", CommonAppType.Status.报销退回.value());
			if (null != dealResult) {
				dataMap.put("remark", dealResult.getRemark());
			}
		}

		// 处理附件信息
		List<Accessory> accessoryList = accessoryService.getAccessoryByEntityId(id);
		if (!CollectionUtils.isEmpty(accessoryList)) {
			dataMap.put("accessoryInfor", accessoryList);
		}

		// 报销进度
		EntityProgress entityProgress = new EntityProgress();
		entityProgress.setEntityId(id);
		entityProgress.setCorpId(expense.getCorpId());
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		sqlQueryParameter.setParameter(entityProgress);
		sqlQueryParameter.setPageIndex(-1);
		sqlQueryParameter.setPageSize(-1);
		List<EntityProgress> entityProgressList = entityProgressDao.queryEntityProgressByPage(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(entityProgressList)) {
			dataMap.put("entityProgressList", entityProgressList);
		}
		return dataMap;
	}

	@Override
	public ResponseEntity<byte[]> exportExpenseListToExcel(JSONObject jsonSearchConditions) throws Exception {

		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();

		String userName = jsonSearchConditions.getString("userName");
		Date startTime = jsonSearchConditions.getDate("startTime");
		Date endTime = jsonSearchConditions.getDate("endTime");

		sqlQueryParameter.getKeyValMap().put("userNameLike", userName);
		sqlQueryParameter.getKeyValMap().put("startDate", startTime);
		sqlQueryParameter.getKeyValMap().put("endDate", endTime);
		sqlQueryParameter.getKeyValMap().put("orderBy", "CreateTime DESC");
		// 查询
		Expense expense = new Expense();
		expense.setStatus(jsonSearchConditions.getString("status"));
		expense.setExpenseNum(jsonSearchConditions.getString("expenseNum"));
		expense.setCorpId(jsonSearchConditions.getString("corpId"));
		sqlQueryParameter.setParameter(expense);
		List<Expense> expenseList = expenseDao.queryExpenseByPage(sqlQueryParameter);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		LinkedHashMap<String, String> fieldMap = new LinkedHashMap<String, String>();
		fieldMap.put("expenseNum", "报销单号");
		fieldMap.put("userId", "报销人工号");
		fieldMap.put("userName", "报销人姓名");
		fieldMap.put("reason", "报销事由");
		fieldMap.put("department", "报销部门");
		fieldMap.put("amount", "报销总额");
		fieldMap.put("actualCost", "实际报销");
		fieldMap.put("createTime", "申请时间");
		fieldMap.put("statusDisplay", "审核状态");
		List<Map<String, Object>> excelList = new ArrayList<Map<String, Object>>();
		SystemStatusCache<Object> systemStatusCache = SystemCacheUtil.getInstance().getSystemStatusCache();
		if (null != expenseList) {
			for (int index = 0; index < expenseList.size(); index++) {
				Expense tmp = expenseList.get(index);
				Map<String, Object> excelMap = tmp.getPersistentState();
				excelMap.put("statusDisplay",
						systemStatusCache.getSystemStatusName(Constants.SYSTEM_STATUS, tmp.getStatus()));
				excelMap.put("createTime", DateUtil.dateToString(tmp.getCreateTime(), DateUtil.FORMATPATTERN));
				excelList.add(excelMap);
			}
		}
		ExcelUtil.listToExcel(excelList, fieldMap, "报销统计", bos);

		HttpHeaders headers = new HttpHeaders();
		String fileName = new String("报销统计.xls".getBytes("UTF-8"), "iso-8859-1");// 为了解决中文名称乱码问题
		headers.setContentDispositionFormData("attachment", fileName);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		return new ResponseEntity<byte[]>(bos.toByteArray(), headers, HttpStatus.CREATED);
	}

	@Override
	public int deleteRelatedRecord(String corpId, String costCategory) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("corpId", corpId);
		param.put("costCategory", costCategory);
		List<Expense> expenseList = expenseDao.queryExpenseByCostCategory(param);
		int optCnt = Constants.ZERO;
		if (!CollectionUtils.isEmpty(expenseList)) {
			Iterator<Expense> iter = expenseList.iterator();
			List<String> ids = new ArrayList<String>();
			while (iter.hasNext()) {
				Expense tmp = iter.next();
				if (!tmp.getStatus().equals(CommonAppType.Status.待审核.value())
						&& !tmp.getStatus().equals(CommonAppType.Status.待报销.value())
						&& !tmp.getStatus().equals(CommonAppType.Status.草稿.value())) {
					// 已在审核中的记录无法删除
					throw new VyiyunException("该报销类型存在正在报销流程中的记录，无法删除");
				} else {
					ids.add(tmp.getId());
				}
			}
			if (!ids.isEmpty()) {
				optCnt += expenseDao.deleteByIds(ids);
				optCnt += expenseFeeDao.deleteExpenseFeeByExpenseId(ids);
				optCnt += entityAccountDao.deleteEntityAccountByEntityIds(ids);
			}
		}
		return optCnt;
	}
}
