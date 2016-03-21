package com.vyiyun.common.weixin.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.entity.Courier;
import com.vyiyun.weixin.model.DataResult;

/**
 * 快递服务类
 * 
 * @author tf
 * 
 * @date 下午3:42:54
 */
public interface ICourierService {

	/**
	 * 添加快递信息
	 * 
	 * @param courier
	 * @return
	 */
	public int addCourier(JSONObject jsonCourier);

	/**
	 * 添加快递信息
	 * 
	 * @param courier
	 * @return
	 */
	public int updateCourier(String id, String status);

	/**
	 * 更新快递信息
	 * 
	 * @param courier
	 */
	public int updateCourier(Courier courier);

	/**
	 * 更加id查询快递
	 * 
	 * @param id
	 * @return
	 */
	public Courier getCourierById(String id);

	/**
	 * 查询快递
	 * 
	 * @param courier
	 * @param dataMap
	 */
	public DataResult getCourier(Courier courier, Map<String, Object> dataMap, int pageIndex, int pageSize);
}
