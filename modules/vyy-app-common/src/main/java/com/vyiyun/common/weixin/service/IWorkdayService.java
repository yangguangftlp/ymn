/**
 * 
 */
package com.vyiyun.common.weixin.service;

import com.vyiyun.common.weixin.entity.Workday;
import com.vyiyun.weixin.model.DataResult;

/**
 * 工作日配置服务
 * @author zb.shen
 * @date 2015年12月31日
 * @version 1.0
 */
public interface IWorkdayService {

	/**
	 * 添加工作日
	 * @param workDayStr 工作日JSON字符串
	 * @return 工作日列表
	 */
	public String[] addWorkday(String workDayStr);

	/**
	 * 获取工作日列表
	 * @param workDay 工作日实体
	 * @return 工作日列表
	 */
	public DataResult queryWorkdays(Workday workDay);

	/**
	 * 删除工作日
	 * @param id 工作日ID
	 * @return 更新操作数
	 */
	public int deleteWorkday(String id);
}
