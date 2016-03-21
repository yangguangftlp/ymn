/**
 * 
 */
package com.vyiyun.common.weixin.dao;

import java.util.List;

import com.vyiyun.common.weixin.entity.Workday;
import com.vyiyun.weixin.model.SqlQueryParameter;

/**
 * 工作日配置DAO
 * @author zb.shen
 * @date 2015年12月31日
 * @version 1.0
 */
public interface WorkdayMapper {

	/**
	 * 添加工作日
	 * @param workday
	 * @return 更新操作数
	 */
	public int addWorkday(List<Workday> workday);

	/**
	 * 获取工作日列表(可分页)
	 * @param sqlQueryParameter 查询条件
	 * @return 工作日列表
	 */
	public List<Workday> queryWorkdayList(SqlQueryParameter sqlQueryParameter);

	/**
	 * 获取工作日列表(可分页)
	 * @param workday 工作日实体
	 * @return 工作日列表
	 */
	public List<Workday> queryWorkdays(Workday workday);

	/**
	 * 删除工作日
	 * @param id 工作日ID
	 * @return 更新操作数
	 */
	public int deleteWorkday(String id);
}
