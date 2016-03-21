/**
 * 
 */
package com.vyiyun.common.weixin.dao;

import java.util.List;

import com.vyiyun.common.weixin.entity.Courier;
import com.vyiyun.weixin.model.SqlQueryParameter;

/**
 * 快递到了
 * 
 * @author tf
 * 
 * @date 下午3:13:23
 */
public interface CourierMapper {

	/**
	 * 添加快递
	 * 
	 * @param courier
	 * @return
	 */
	public int addCourier(List<Courier> courier);

	/**
	 * 查询快递
	 * 
	 * @param courier
	 * @return
	 */
	public Courier getCourierById(String id);

	/**
	 * 查询快递
	 * 
	 * @param courier
	 * @return
	 */
	public List<Courier> getCourier(SqlQueryParameter sqlQueryParameter);

	/**
	 * 更新快递记录
	 * 
	 * @param courier
	 * @return
	 */
	public int updateCourier(Courier courier);

	/**
	 * 根据快递号查询
	 * 
	 * @param courierNum
	 */
	public int getCourierNumCount(String courierNum);
}
