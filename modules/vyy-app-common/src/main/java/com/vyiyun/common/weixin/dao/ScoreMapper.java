/**
 * 
 */
package com.vyiyun.common.weixin.dao;

import java.util.List;
import java.util.Map;

import com.vyiyun.common.weixin.entity.Score;

/**
 * 评分
 * 
 * @author tf
 * @date 2015年11月16日 上午11:18:19
 */
public interface ScoreMapper {

	/**
	 * 添加评分
	 * 
	 * @param score
	 * @return
	 */
	int addScore(List<Score> scores);

	/**
	 * 获取评分记录
	 * 
	 * @param SqlQueryParameter
	 * @return
	 */
	List<Score> getScore(Score score);

	/**
	 * 获取问题评分记录
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

	/**
	 * 更新评分
	 * 
	 * @param score
	 * @return
	 */
	int updateScore(Score score);

	/**
	 * 删除评分
	 * 
	 * @param score
	 * @return
	 */
	int deleteScore(Score score);
}
