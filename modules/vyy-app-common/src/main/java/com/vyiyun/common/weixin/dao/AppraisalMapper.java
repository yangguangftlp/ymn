/**
 * 
 */
package com.vyiyun.common.weixin.dao;

import java.util.List;
import java.util.Map;

import com.vyiyun.common.weixin.entity.Appraisal;
import com.vyiyun.weixin.model.SqlQueryParameter;

/**
 * 评价Dao
 * 
 * @author tf
 * @date 2015年11月16日 上午11:06:30
 */
public interface AppraisalMapper {

	/**
	 * 添加评价
	 * 
	 * @param appraisal
	 * @return
	 */
	int addAppraisal(List<Appraisal> appraisal);

	/**
	 * 根据id 查询评价记录
	 * 
	 * @param id
	 * @return
	 */
	Appraisal getAppraisalById(String id);

	/**
	 * 查询评价记录
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	List<Appraisal> getAppraisal(SqlQueryParameter sqlQueryParameter);

	/**
	 * 根据评审人来查询 查询评价记录
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	List<Appraisal> getAppraisalRecord(SqlQueryParameter sqlQueryParameter);

	/**
	 * 查询待我评价的详情数据
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	List<Appraisal> getAuditAppraisalRecord(SqlQueryParameter sqlQueryParameter);

	/**
	 * 查询待评价我的详情数据
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	List<Appraisal> getAppraisalMeRecord(SqlQueryParameter sqlQueryParameter);

	/**
	 * 查询待我评价的详情数据
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	List<Map<String, Object>> getAppraisalDetailRecord(SqlQueryParameter sqlQueryParameter);

	/**
	 * 更新评价记录
	 * 
	 * @param appraisal
	 * @return
	 */
	int updateAppraisal(Appraisal appraisal);

	/**
	 * 根据id删除记录
	 * 
	 * @param ids
	 * @return
	 */
	int deleteAppraisalById(List<String> ids);

	/**
	 * 员工排行
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	List<Map<String, Object>> getEmployeeRankingRecord(SqlQueryParameter sqlQueryParameter);

}
