/**
 * 
 */
package com.vyiyun.common.weixin.dao;

import java.util.List;
import java.util.Map;

import com.vyiyun.common.weixin.entity.Loan;
import com.vyiyun.weixin.model.SqlQueryParameter;

/**
 * 借款Dao
 * 
 * @author tf
 * @date 2015年11月3日 下午1:49:51
 */
public interface LoanMapper {

	/**
	 * 添加借款
	 * 
	 * @param loan
	 */
	public int addLoan(List<Loan> loans);

	/**
	 * 删除借款
	 * 
	 * @param loan
	 */
	public int deleteLoan(Loan loan);

	/**
	 * 更新借款信息
	 * 
	 * @param loan
	 */
	public int updateLoan(Loan loan);

	/**
	 * 提供根据id 获取借款信息
	 * 
	 * @param id
	 * @return
	 */
	public Loan getLoanById(String id);

	/**
	 * 查询记
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<Loan> getLoan(SqlQueryParameter sqlQueryParameter);

	/**
	 * 获取审批记录
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<Map<String, Object>> getLoanBeAuditRecord(SqlQueryParameter sqlQueryParameter);

	/**
	 * 获取待审批记录
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<Map<String, Object>> getLoanAuditRecord(SqlQueryParameter sqlQueryParameter);

	/**
	 * 删除借款记录
	 * @param ids 借款ID
	 * @return 操作数
	 */
	public int deleteByIds(List<String> ids);
}
