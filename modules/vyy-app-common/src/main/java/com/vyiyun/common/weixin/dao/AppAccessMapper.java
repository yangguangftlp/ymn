/**
 * 
 */
package com.vyiyun.common.weixin.dao;

import java.util.List;
import java.util.Map;

import com.vyiyun.common.weixin.entity.AppAccess;
import com.vyiyun.weixin.model.SqlQueryParameter;

/**
 * 应用访问DAO
 * 
 * @author tf
 * @date 2015年10月9日 上午9:16:33
 */
public interface AppAccessMapper {
	/**
	 * 新增记录
	 * 
	 * @param AppAccess
	 */
	public int addAppAccess(List<AppAccess> appAccess);

	/**
	 * 
	 * 获取记录
	 * 
	 * @param AppAccess
	 * @return
	 */
	public List<AppAccess> getAppAccess(SqlQueryParameter sqlQueryParameter);

	/**
	 * 使用排行
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<Map<String, Object>> getAppAccessRanking(SqlQueryParameter sqlQueryParameter);

	/**
	 * 更新每天访问次数 如果不存在就新增
	 * 
	 * @param appAccess
	 * @return
	 */
	public void updateAppAccessByDay(AppAccess appAccess);

}
