/**
 * 
 */
package com.vyiyun.common.weixin.dao;

import java.util.List;

import com.vyiyun.common.weixin.entity.ExpenseFee;

/**
 * 费用记录
 * 
 * @author tf
 * @date 2015年10月23日 下午3:22:02
 */
public interface ExpenseFeeMapper {

	/**
	 * 添加报销费用
	 * 
	 * @param expenseFee
	 * @return
	 */
	public int addExpenseFee(List<ExpenseFee> expenseFee);

	/**
	 * 删除报销费用
	 * 
	 * @param id
	 * @return
	 */
	public int deleteExpenseFeeById(List<String> id);
	
	/**
	 * 删除报销费用
	 * 
	 * @param expenseId
	 * @return
	 */
	public int deleteExpenseFeeByExpenseId(List<String> expenseId);

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
