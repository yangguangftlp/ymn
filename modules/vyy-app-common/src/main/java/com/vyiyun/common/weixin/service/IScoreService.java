/**
 * 
 */
package com.vyiyun.common.weixin.service;

import java.util.List;
import java.util.Map;

import com.vyiyun.common.weixin.entity.Score;

/**
 * 评分服务
 * 
 * @author tf
 * @date 2015年11月18日 下午2:32:41
 */
public interface IScoreService {

	/**
	 * 添加评分
	 * 
	 * @param score
	 * @return
	 */
	int addScore(Score... score);

	/**
	 * 获取评价
	 * 
	 * @param score
	 * @return
	 */
	List<Score> getScore(Score score);

	/**
	 * 获取某个人问题评价的评分记录
	 * 
	 * @param SqlQueryParameter
	 * @return
	 */
	List<Map<String, Object>> getProblemScore(Map<String, Object> params);

	/**
	 * 获取问题评价的评分统计记录
	 * 
	 * @param SqlQueryParameter
	 * @return
	 */
	List<Map<String, Object>> getProblemScoreStatistic(Map<String, Object> params);

	/**
	 * 获取某个人问题综合评价的记录
	 * 
	 * @param SqlQueryParameter
	 * @return
	 */
	List<Map<String, Object>> getProblemOpinion(Map<String, Object> params);
}
