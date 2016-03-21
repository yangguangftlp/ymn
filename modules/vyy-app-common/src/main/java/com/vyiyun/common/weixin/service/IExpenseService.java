package com.vyiyun.common.weixin.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.entity.Expense;
import com.vyiyun.weixin.model.DataResult;

public interface IExpenseService {

	/**
	 * 添加报销信息
	 * 
	 * @param expense
	 */
	public void addExpense(Expense expense);

	/**
	 * 获取报销信息
	 * 
	 * @return
	 */
	public List<Expense> getExpense(Expense expense);

	/**
	 * 根据id删除报销信息
	 * 
	 * @param id
	 */
	public void deleteById(String id);

	/**
	 * 更新报销信息
	 * 
	 * @param expense
	 */
	public void updateExpense(Expense expense);

	/**
	 * 报销申请
	 * 
	 * @param params
	 *            jsonExpenseApplyInfoObj
	 *            json数据信息{expenseInfo:'',auditor:'',cc:[]}
	 * @param fileltems
	 *            附件信息
	 */
	public void doApply(JSONObject jsonExpenseApplyInfoObj);

	/**
	 * 数据查询
	 * 
	 * @param expense
	 *            报销
	 * @param params
	 * @param pageIndex
	 * @param pageSize
	 */
	public DataResult queryExpenseRecord(Expense expense, Map<String, Object> params, int pageIndex, int pageSize);

	/**
	 * 获取报销信息 附件 审核人 抄送人
	 * 
	 * @param id
	 * @deprecated
	 */
	@Deprecated
	public Map<String, Object> getExpenseInfoById(String id);

	/**
	 * 获取报销信息 根据id
	 * 
	 * @param id
	 * @return
	 */
	public Expense getExpenseById(String id);

	/**
	 * 报销审核处理
	 * 
	 * @param jsonExpenseAuditInfo
	 */
	public void doAudit(JSONObject jsonExpenseAuditInfo);
    
	/**
	 * 获取待审核记录
	 * @param expense
	 * @param params
	 * @param i
	 * @param j
	 * @return
	 */
	public DataResult queryAuditRecord(Expense expense, Map<String, Object> params, int i, int j);

	/**
	 * 根据报销ID获取报销详情数据
	 * @param id 报销ID
	 * @return 报销详情数据
	 */
	public Map<String, Object> getExpenseDataById(String id);

	/**
	 * 导出报销数据
	 * @param jsonSearchConditions 查询条件
	 * @return excel文件字节流
	 * @throws Exception 
	 */
	public ResponseEntity<byte[]> exportExpenseListToExcel(JSONObject jsonSearchConditions) throws Exception;

	/**
	 * 删除关联报销记录
	 * @param corpId
	 * @param costCategory
	 * @return 操作数
	 */
	public int deleteRelatedRecord(String corpId, String costCategory);

}
