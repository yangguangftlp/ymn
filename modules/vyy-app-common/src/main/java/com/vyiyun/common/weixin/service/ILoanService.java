/**
 * 
 */
package com.vyiyun.common.weixin.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.entity.Loan;
import com.vyiyun.weixin.model.DataResult;

/**
 * 借款服务
 * 
 * @author tf
 * @date 2015年11月3日 上午11:50:14
 */
public interface ILoanService {

	/**
	 * 添加借款
	 * 
	 * @param loan
	 * @return
	 */
	public int addLoan(Loan... loan);

	/**
	 * 根据id获取借款
	 * 
	 * @param id
	 * @return
	 */
	public Loan getLoanById(String id);

	/**
	 * 发起项目操作 发起 暂存
	 * 
	 * @param launchLoanInfo
	 * @return
	 */
	public Object launchLoan(JSONObject jsonLaunchLoanInfo);

	/**
	 * 获取借款记录
	 * 
	 * @param loan
	 * @param params
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public DataResult getLoanRecord(Loan loan, Map<String, Object> params, int pageIndex, int pageSize);

	/**
	 * 查询被审核的记录 根据PersonType来控制 审批 报销
	 * 
	 * @param params
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public DataResult getBeAuditRecord(Map<String, Object> params, int pageIndex, int pageSize);

	/**
	 * 查询审核记录 根据PersonType来控制 审批 报销
	 * 
	 * @param params
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public DataResult getAuditRecord(Map<String, Object> params, int pageIndex, int pageSize);

	/**
	 * 借款操作
	 * 
	 * @param jsonLoanInfo
	 */
	public Object loanOperate(JSONObject jsonLoanInfo);

	/**
	 * 导出报销数据
	 * 
	 * @param jsonSearchConditions
	 *            查询条件
	 * @return excel文件字节流
	 * @throws Exception
	 */
	public ResponseEntity<byte[]> exportLoanListToExcel(JSONObject jsonSearchConditions) throws Exception;

	/**
	 * 获取借款详情
	 * 
	 * @param id
	 *            借款ID
	 * @param corpId
	 *            企业ID
	 * @return 借款详情数据
	 */
	public Map<String, Object> getLoanDetail(String id, String corpId);

	/**
	 * 删除借款用途关联记录
	 * 
	 * @param corpId
	 *            企业ID
	 * @param loanUse
	 *            借款用途
	 * @return 操作数
	 */
	public int deleteRelatedRecord(String corpId, String loanUse);
}
