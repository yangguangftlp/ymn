package com.vyiyun.common.weixin.dao;

import java.util.List;
import java.util.Map;

import com.vyiyun.common.weixin.entity.Expense;
import com.vyiyun.weixin.model.SqlQueryParameter;

public interface ExpenseMapper {

	/**
	 * 添加报销信息
	 * 
	 * @param expense
	 */
	public int addExpense(Expense expense);

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
	 * 查询记录数
	 * 
	 * @param SqlQueryParameter
	 * @return
	 */
	public long queryExpenseCount(SqlQueryParameter SqlQueryParameter);

	/**
	 * 分页查询
	 * 
	 * @param SqlQueryParameter
	 * @return
	 */
	public List<Expense> queryExpenseByPage(SqlQueryParameter SqlQueryParameter);

	/**
	 * 更新报销内容
	 * 
	 * @param expense
	 */
	public void updateExpense(Expense expense);

	/**
	 * 根据id获取报销实体
	 * 
	 * @param id
	 * @return
	 */
	public Expense getExpenseById(String id);

	/**
	 * 获取代办任务
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<Expense> queryExpenseAuditRecord(SqlQueryParameter sqlQueryParameter);

	/**
	 * 通过报销类型查询报销数据
	 * @param param 查询条件
	 * @return 报销列表
	 */
	public List<Expense> queryExpenseByCostCategory(Map<String, String> param);

	/**
	 * 根据id列表删除报销信息
	 * 
	 * @param ids id列表
	 */
	public int deleteByIds(List<String> ids);
}
