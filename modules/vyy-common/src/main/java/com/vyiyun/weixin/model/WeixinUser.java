package com.vyiyun.weixin.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 微信用户
 * 
 * @author tf
 * 
 * @date 上午11:43:25
 */
@SuppressWarnings("serial")
public class WeixinUser implements Serializable {
	/**
	 * 用户ID
	 */
	private String userid;
	/**
	 * 企业corpId
	 */
	private String corpId;
	/**
	 * 用户名称
	 */
	private String name;
	/**
	 * 用户部门
	 */
	private String[] department;
	/**
	 * 用户职位
	 */
	private String position;
	/**
	 * 用户手机号
	 */
	private String mobile;
	/**
	 * 性别。0表示未定义，1表示男性，2表示女性
	 */
	private String gender;
	/**
	 * 邮箱
	 */
	private String email;
	/**
	 * 微信号
	 */
	private String weixinid;
	/**
	 * 头像url。注：如果要获取小图将url最后的"/0"改成"/64"即可
	 */
	private String avatar;
	/**
	 * 关注状态: 1=已关注，2=已冻结，4=未关注
	 */
	private String status;
	/**
	 * 扩展属性
	 */
	private String extattr;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getDepartment() {
		return department;
	}

	public void setDepartment(String[] department) {
		this.department = department;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWeixinid() {
		return weixinid;
	}

	public void setWeixinid(String weixinid) {
		this.weixinid = weixinid;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExtattr() {
		return extattr;
	}

	public void setExtattr(String extattr) {
		this.extattr = extattr;
	}

	/**
	 * 用户是否包含该部门
	 * 
	 * @param deptId
	 * @return
	 */
	public boolean isContainDept(String deptId) {
		if (null != department) {
			return Arrays.asList(department).contains(deptId);
		}
		return false;
	}
}
