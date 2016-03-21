/**
 * 
 */
package com.vyiyun.weixin.service;

import java.util.List;

import com.vyiyun.weixin.enums.WeixinDepartStatus;
import com.vyiyun.weixin.model.WeixinDept;
import com.vyiyun.weixin.model.WeixinTag;
import com.vyiyun.weixin.model.WeixinUser;

/**
 * 微信用户管理
 * 
 * @author tf
 * 
 * @date 上午10:22:39
 */
public interface IWeixinContactsService {
	/**
	 * 根据部门id获取微信用户
	 * 
	 * @param deptId
	 * @return
	 */
	public List<WeixinUser> getWeixinUserByDeptId(String deptId);

	/**
	 * depId ，部门id fetchChild ，1/0，是否递归获取子部门下面的成员 statuses
	 * ，0获取全部成员，1获取已关注成员列表，2获取禁用成员列表，4获取未关注成员列表。statuses可填写多个
	 * 
	 * @param deptId
	 * @return
	 */
	public List<WeixinUser> getWeixinUserByDeptId(String deptId, int fetchChild, WeixinDepartStatus... statuses);

	/**
	 * 获取授权部门
	 * 
	 * @return
	 */
	public List<WeixinDept> getWeixinDeptIds();

	/**
	 * 获取授权标签
	 * 
	 * @return
	 */
	public List<WeixinTag> getWeixinTag();

}
