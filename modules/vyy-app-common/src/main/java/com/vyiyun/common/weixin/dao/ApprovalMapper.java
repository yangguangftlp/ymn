package com.vyiyun.common.weixin.dao;

import java.util.List;
import java.util.Map;

import com.vyiyun.common.weixin.entity.Approval;
import com.vyiyun.weixin.model.SqlQueryParameter;

public interface ApprovalMapper {

	/**
	 * 添加审批内容
	 * 
	 * @param approval
	 */
	public int addApproval(List<Approval> approvals);

	/**
	 * 获取审批信息
	 * 
	 * @param approval
	 * @return
	 */
	public List<Approval> getApproval(Approval approval);

	/**
	 * 根据id删除审批信息
	 * 
	 * @param id
	 */
	public void deleteApprovalById( String id);

	/**
	 * 查询记录数
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public long queryApprovalCount(SqlQueryParameter sqlQueryParameter);

	/**
	 * 分页查询
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<Approval> queryApprovalByPage(SqlQueryParameter sqlQueryParameter);

	/**
	 * 更新操作
	 * 
	 * @param approval
	 */
	public void updateApproval(Approval approval);

	/**
	 * 获取待审核记录
	 * 
	 * @param sqlQueryParameter
	 */
	public List<Approval> getApprovalAuditRecord(SqlQueryParameter sqlQueryParameter);

	/**
	 * 获取以审核的记录
	 * 
	 * @param sqlQueryParameter
	 */
	public List<Map<String, Object>> getApprovalBeAuditRecord(SqlQueryParameter sqlQueryParameter);

	/**
	 * 获取抄送的记录
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<Approval> getApprovalCcRecord(SqlQueryParameter sqlQueryParameter);

	/**
	 * 根据id 获取审批信息
	 * @param corpId
	 * @param id
	 * @return
	 */
	public Approval getApprovalById( String id);
}
