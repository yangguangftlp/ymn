package com.vyiyun.common.weixin.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.entity.Sign;
import com.vyiyun.weixin.model.DataResult;
import com.vyiyun.weixin.model.SqlQueryParameter;

public interface ISignService {

	/**
	 * 添加签到信息
	 * 
	 * @param sign
	 */
	public int addSign(Sign... sign);

	/**
	 * 获取签到信息
	 * 
	 * @param sign
	 * @return
	 */
	public List<Map<String, Object>> getSign(Sign sign);

	/**
	 * 根据id删除签到信息
	 * 
	 * @param id
	 */
	public void deleteById(String id);

	/**
	 * 获取签到实体 根据id
	 * 
	 * @return
	 */
	public Map<String, Object> getSignById(String id);

	/**
	 * 更新签到信息
	 * 
	 * @param sign
	 */
	public void update(Sign sign);

	/**
	 * 发布签到信息
	 * 
	 * @param jsonLaunchSignInfo
	 */
	public void launchSign(JSONObject jsonLaunchSignInfo);

	/**
	 * 分页查询
	 * 
	 * @param sign
	 * @param params
	 * @param pageIndex
	 * @param pageSize
	 */
	public DataResult querySignRecord(Sign sign, Map<String, Object> params, int pageIndex, int pageSize);

	/**
	 * 记录数
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public long querySignCount(SqlQueryParameter sqlQueryParameter);

	/**
	 * 签到信息
	 * 
	 * @param responseResult
	 */
	public void doSign(JSONObject jsonSignInfoObj);

	/**
	 * 签到备注 操作 修改 新增
	 * 
	 * @param jsonRemarkObj
	 */
	public void doRemark(JSONObject jsonRemarkObj);

	/**
	 * 查询签到列表数据
	 * @param jsonSearchConditions 查询条件
	 * @return 签到列表数据
	 */
	Map<String, Object> queryAttendanceList(JSONObject jsonSearchConditions);

	/**
	 * 导出考勤数据
	 * @param jsonSearchConditions 查询条件
	 * @return excel文件字节流
	 * @throws Exception 
	 */
	public ResponseEntity<byte[]> exportAttendanceListToExcel(JSONObject jsonSearchConditions) throws Exception;

}
