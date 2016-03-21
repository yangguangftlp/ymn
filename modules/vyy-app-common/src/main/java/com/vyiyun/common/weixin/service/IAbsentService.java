/**
 * 
 */
package com.vyiyun.common.weixin.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.entity.Absent;
import com.vyiyun.common.weixin.entity.Approval;
import com.vyiyun.weixin.model.DataResult;

/**
 * 请假服务类
 * 
 * @author tf
 * 
 *         2015年6月24日
 */
public interface IAbsentService {

	/**
	 * 请假服务
	 * 
	 * @param params
	 *            jsonAbsentApplyInfo json数据信息{absentInfo:'',auditor:'',cc:[]}
	 */
	public void doApply(JSONObject jsonAbsentApplyInfo);

	/**
	 * 添加请假信息
	 * 
	 * @param absent
	 */
	public int addAbsent(Absent... absent);

	/**
	 * 获取请假信息
	 * 
	 * @param absent
	 * @return
	 */
	public List<Absent> getAbsent(Absent absent);

	/**
	 * 获取请假信息
	 * 
	 * @param id
	 * @return
	 */
	public Absent getAbsentById(String id);

	/**
	 * 根据id删除请假记录
	 * 
	 * @param id
	 */
	public void deleteById(String id);

	/**
	 * 更新请假记录
	 * 
	 * @param absent
	 */
	public void updateAbsent(Absent absent);

	/**
	 * 分页查询
	 * 
	 * @param absent
	 * @param params
	 * @param pageIndex
	 * @param pageSize
	 */
	public DataResult queryAbsentRecord(Absent absent, Map<String, Object> params, int pageIndex, int pageSize);

	/**
	 * 分页查询
	 * 
	 * @param absent
	 * @param pageIndex
	 * @param pageSize
	 */
	public DataResult queryAbsentRecord(Absent absent, int pageIndex, int pageSize);

	/**
	 * 获取待审核记录 已审核记录 参数operationType 0 待审核 1 已审核
	 * 
	 * @param params
	 * @param pageIndex
	 * @param pageSize
	 */
	public DataResult queryAuditRecord(Map<String, Object> params, int pageIndex, int pageSize);

	/**
	 * 请假审批处理
	 * 
	 * @param jsonAuditInfo
	 */
	public void doAbsent(JSONObject jsonAbsentInfoObj);

	/**
	 * 请假抄送
	 * @param approval
	 * @param params
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public DataResult getAbsentCcRecord(Approval approval, Map<String, Object> params, int pageIndex, int pageSize);

	/**
	 * 导出请假数据
	 * @param jsonSearchConditions 查询条件
	 * @return excel文件字节流
	 * @throws Exception 
	 */
	public ResponseEntity<byte[]> exportAbsentListToExcel(JSONObject jsonSearchConditions) throws Exception;

	/**
	 * 根据企业ID和请假类型删除相关数据
	 * @param corpId 企业ID
	 * @param absentType 请假类型
	 */
	public void deleteRelatedRecord(String corpId, String absentType);
}
