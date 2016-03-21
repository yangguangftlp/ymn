/**
 * 
 */
package com.vyiyun.common.weixin.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.entity.Approval;
import com.vyiyun.weixin.model.DataResult;

/**
 * @author tf
 * 
 *         2015年6月24日
 */
public interface IApprovalService {
	/**
	 * 新增审批信息
	 * 
	 * @param approval
	 */
	public int addApproval(Approval... approval);

	/**
	 * 获取审批记录
	 * 
	 * @param approval
	 * @param params
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public DataResult queryApprovalRecord(Approval approval, Map<String, Object> params, int pageIndex, int pageSize);

	/**
	 * 获取待审核记录
	 * 
	 * @param params
	 *            查询条件
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public DataResult getAuditRecord(Approval approval, Map<String, Object> params, int pageIndex, int pageSize);

	/**
	 * 获取已审核的记录
	 * 
	 * @param params
	 *            查询条件
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public DataResult getBeAuditRecord(Approval approval, Map<String, Object> params, int pageIndex, int pageSize);

	/**
	 * 与审批相关操作的处理
	 * 
	 * @param jsonMyApprovalInfo
	 */
	public void doAudit(JSONObject jsonMyApprovalInfo);

	/**
	 * 发起审批操作 普通 合同
	 * 
	 * @param jsonLaunchApprovalInfo
	 * @return 返回id
	 */
	public String launchApproval(JSONObject jsonLaunchApprovalInfo);

	/**
	 * 根据id 获取审批信息
	 * 
	 * @param id
	 */
	public Approval getApprovalById(String id);

	/**
	 * 获取抄送的记录
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public DataResult getApprovalCcRecord(Approval approval, Map<String, Object> params, int pageIndex, int pageSize);

	/**
	 * 根据审批ID获取审批详情数据
	 * 
	 * @param id
	 *            审批ID
	 * @return 审批详情数据
	 */
	public Map<String, Object> getApprovalDataById(String id);

	/**
	 * 导出审批数据
	 * 
	 * @param jsonSearchConditions
	 *            查询条件
	 * @return excel文件字节流
	 * @throws Exception
	 */
	public ResponseEntity<byte[]> exportApprovalListToExcel(JSONObject jsonSearchConditions) throws Exception;
}
