/**
 * 
 */
package com.vyiyun.common.weixin.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.entity.Appraisal;
import com.vyiyun.weixin.model.DataResult;

/**
 * 评价服务
 * 
 * @author tf
 * @date 2015年11月16日 上午11:42:49
 */
public interface IAppraisalService {

	
	public int addAppraisal(Appraisal ...appraisal);
	
	/**
	 * 获取评价
	 * 
	 * @param id
	 * @return
	 */
	public Appraisal getAppraisalById(String id);

	/**
	 * 发起评价
	 * 
	 * @param jsonAppraisalInfo
	 * @return
	 */
	public Object launchAppraisal(JSONObject jsonAppraisalInfo);

	/**
	 * 获取评论我的评价记录
	 * 
	 * @param appraisal
	 * @param params
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public DataResult getAppraisalMeRecord(Map<String, Object> params, int pageIndex, int pageSize);

	/**
	 * 获取待处理评价记录
	 * 
	 * @param appraisal
	 * @param params
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public DataResult getAuditAppraisalRecord(Appraisal appraisal, Map<String, Object> params, int pageIndex,
			int pageSize);

	/**
	 * 获取评价记录
	 * 
	 * @param appraisal
	 * @param params
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public DataResult getAppraisalRecord(Appraisal appraisal, Map<String, Object> params, int pageIndex, int pageSize);

	/**
	 * 查询待我评价的详情数据
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	DataResult getAppraisalDetailRecord(Map<String, Object> params, int pageIndex, int pageSize);

	/**
	 * 员工排行
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	DataResult getEmployeeRankingRecord(Map<String, Object> params, int pageIndex, int pageSize);

	/**
	 * 评价处理
	 * 
	 * @param jsonAppraisalInfo
	 * @return
	 */
	public Object appraisalOperate(JSONObject jsonAppraisalInfo);

	/**
	 * 导出员工评价数据
	 * @param jsonSearchConditions 查询条件
	 * @return excel文件字节流
	 * @throws Exception 
	 */
	public ResponseEntity<byte[]> exportAppraisalListToExcel(JSONObject jsonSearchConditions) throws Exception;

	/**
	 * 获取评价详情数据
	 * @param id 评价ID
	 * @param corpId 企业ID
	 * @return 评价详情数据
	 */
	public Map<String, Object> getAppraisalDetailData(String id, String corpId);

	/**
	 * 获取员工评价详情数据
	 * @param id 评价ID
	 * @param accountId 员工ID
	 * @param corpId 企业ID
	 * @return 员工评价详情数据
	 */
	public Map<String, Object> getUserDetailData(String id, String accountId, String corpId);
}
