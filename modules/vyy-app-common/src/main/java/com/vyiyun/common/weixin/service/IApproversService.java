/**
 * 
 */
package com.vyiyun.common.weixin.service;

import com.vyiyun.common.weixin.entity.Approvers;

/**
 * 审核人服务
 * 
 * @author tf
 * @date 2015年11月10日 下午2:57:42
 */
public interface IApproversService {
	/**
	 * 添加审批人
	 * 
	 * @param approvals
	 * @return
	 */
	int addApprovers(Approvers... approvals);

	/**
	 * 根据ID获取审批人
	 * 
	 * @param id
	 * @return
	 */
	Approvers getApproversById(String id);

	/**
	 * 根据父Id填写内容
	 * 
	 * @param parentId
	 * @return
	 */
	Approvers getApproversByParentId(String parentId);

	/**
	 * 删除记录数据
	 * 
	 * @param id
	 *            数据id
	 * @param entityId
	 *            实体id
	 * @return
	 */
	int deleteApprovers(String id, String entityId);
}
