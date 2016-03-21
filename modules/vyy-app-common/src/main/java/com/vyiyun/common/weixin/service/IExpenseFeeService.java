/**
 * 
 */
package com.vyiyun.common.weixin.service;

import java.util.List;

import com.vyiyun.common.weixin.entity.ExpenseFee;

/**
 * @author tf
 * @date 2015年10月23日 下午4:43:45
 */
public interface IExpenseFeeService {

	/**
	 * 添加报销费用
	 * 
	 * @param expenseFee
	 * @return
	 */
	public int addExpenseFee(ExpenseFee... expenseFee);

	/**
	 * 删除报销费用
	 * 
	 * @param id
	 * @return
	 */
	public int deleteExpenseFeeById(String... id);
	
	/**
	 * 删除报销费用
	 * 
	 * @param expenseId
	 * @return
	 */
	public int deleteExpenseFeeByExpenseId(String... expenseId);

	/**
	 * 更新报销费用
	 * 
	 * @param expenseFee
	 * @return
	 */
	public int updateExpenseFeeById(ExpenseFee expenseFee);

	/**
	 * 获取报销费用信息
	 * 
	 * @param expenseId
	 * @return
	 */
	public List<ExpenseFee> getExpenseFee(ExpenseFee expenseFee);
}