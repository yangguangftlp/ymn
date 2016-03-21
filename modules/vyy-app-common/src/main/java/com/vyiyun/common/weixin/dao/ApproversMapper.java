/**
 * 
 */
package com.vyiyun.common.weixin.dao;

import java.util.List;

import com.vyiyun.common.weixin.entity.Approvers;

/**
 * 审批人Dao
 * 
 * @author tf
 * @date 2015年11月10日 下午2:08:39
 */
public interface ApproversMapper {

	/**
	 * 添加审批人
	 * 
	 * @param approvals
	 * @return
	 */
	int addApprovers(List<Approvers> approvals);

	/**
	 * 根据ID获取审批人
	 * 
	 * @param id
	 * @return
	 */
	Approvers getApproversById(String id);

	/**
	 * 根据父Id获取审批人
	 * 
	 * @param approvers
	 * @return
	 */
	List<Approvers> getApprovers(Approvers approvers);

	/**
	 * 删除审核人记录
	 * 
	 * @param params
	 * @return
	 */
	int deleteApprovers(Approvers approvers);
}
