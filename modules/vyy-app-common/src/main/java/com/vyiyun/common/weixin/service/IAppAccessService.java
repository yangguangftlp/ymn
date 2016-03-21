/**
 * 
 */
package com.vyiyun.common.weixin.service;

import java.util.Map;

import com.vyiyun.common.weixin.entity.AppAccess;
import com.vyiyun.weixin.model.DataResult;

/**
 * 应用访问记录
 * 
 * @author tf
 * @date 2015年10月8日 下午3:39:56
 */
public interface IAppAccessService {

	/**
	 * 更新当天用户访问记录 不存在将新增
	 * 
	 * @param AppAccess
	 * @return
	 */
	void updateAppAccessByDay(AppAccess AppAccess);

	/**
	 * 添加应用访问记录
	 * 
	 * @param AppAccess
	 */
	int addAppAccess(AppAccess... appAccess);

	/**
	 * 应用访问排行
	 * 
	 * @param appAccess
	 *            TODO
	 * @param params
	 *            类型0 人员 1 应用
	 * @param pageIndex
	 * @param pageSize
	 * 
	 * @return
	 */
	DataResult getAppAccessRanking(AppAccess appAccess, Map<String, Object> params, int pageIndex, int pageSize);

}
