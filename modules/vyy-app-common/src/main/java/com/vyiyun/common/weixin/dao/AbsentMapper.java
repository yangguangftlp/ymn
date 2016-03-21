package com.vyiyun.common.weixin.dao;

import java.util.List;

import com.vyiyun.common.weixin.entity.Absent;
import com.vyiyun.weixin.model.SqlQueryParameter;

/**
 * 请假
 * 
 * @author tf
 * 
 *         2015年6月24日
 */
public interface AbsentMapper {

	/**
	 * 添加请假信息
	 * 
	 * @param absent
	 */
	public int addAbsent(List<Absent> absentList);

	/**
	 * 获取请假信息
	 * 
	 * @return
	 */
	public List<Absent> getAbsent(Absent absent);

	/**
	 * 根据id删除请假信息
	 * 
	 * @param id
	 */
	public void deleteAbsentById(String id);

	/**
	 * 更新请假信息
	 * 
	 * @param absent
	 */
	public void updateAbsent(Absent absent);

	/**
	 * 查询记录数
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public long queryAbsentCount(SqlQueryParameter sqlQueryParameter);

	/**
	 * 分页查询
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<Absent> queryAbsentByPage(SqlQueryParameter sqlQueryParameter);

	/**
	 * 查询审核记录 待审核 以及 已审核
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<Absent> queryAuditRecord(SqlQueryParameter sqlQueryParameter);

	/**
	 * 获取请假信息
	 * 
	 * @param id
	 * @return
	 */
	public Absent getAbsentById(String id);

	/**
	 * 获取请假抄送
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<Absent> getAbsentCcRecord(SqlQueryParameter sqlQueryParameter);

	/**
	 * 根据条件删除请假记录
	 * @param absent 请假实体
	 */
	public void deleteRelatedRecord(Absent absent);

}
