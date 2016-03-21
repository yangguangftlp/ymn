/**
 * 
 */
package com.vyiyun.common.weixin.service.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.vyiyun.common.weixin.dao.ApproversMapper;
import com.vyiyun.common.weixin.entity.Approvers;
import com.vyiyun.common.weixin.service.IApproversService;

/**
 * 审批人服务实现
 * 
 * @author tf
 * @date 2015年11月10日 下午2:58:33
 */
@Service("approversService")
public class ApproversServiceImpl implements IApproversService {

	@Autowired
	private ApproversMapper approversDao;

	@Override
	public int addApprovers(Approvers... approvals) {
		if (approvals.length < 1) {
			return 0;
		}
		return approversDao.addApprovers(Arrays.asList(approvals));
	}

	@Override
	public Approvers getApproversById(String id) {
		return approversDao.getApproversById(id);
	}

	@Override
	public int deleteApprovers(String id, String entityId) {
		Approvers approvers = new Approvers();
		if (StringUtils.isNotEmpty(id)) {
			approvers.setId(id);
		}
		if (StringUtils.isNotEmpty(entityId)) {
			approvers.setEntityId(entityId);
		}
		return approversDao.deleteApprovers(approvers);
	}

	@Override
	public Approvers getApproversByParentId(String parentId) {
		Approvers approvers = new Approvers();
		approvers.setParentId(parentId);
		List<Approvers> approversList = approversDao.getApprovers(approvers);
		if (!CollectionUtils.isEmpty(approversList)) {
			return approversList.get(0);
		}
		return null;
	}

}
