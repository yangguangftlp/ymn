/**
 * 
 */
package com.vyiyun.common.weixin.dao;

import java.util.List;
import java.util.Map;

import com.vyiyun.common.weixin.entity.Feedback;
import com.vyiyun.weixin.model.SqlQueryParameter;

/**
 * 系统反馈DAO
 * 
 * @author tf
 * @date 2015年10月8日 上午10:49:16
 */
public interface FeedbackMapper {

	/**
	 * 添加系统反馈
	 * 
	 * @param Feedback
	 */

	public int addFeedback(List<Feedback> feedbackList);

	/**
	 * 获取反馈记录
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public Feedback getFeedbackById(String id);

	/**
	 * 获取反馈记录
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<Feedback> getFeedback(SqlQueryParameter sqlQueryParameter);

	/**
	 * 反馈排行
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<Map<String, Object>> getFeedbackTop(SqlQueryParameter sqlQueryParameter);

	/**
	 * 更新系统反馈记录
	 * 
	 * @param Feedback
	 * @return
	 */
	public int updateFeedback(Feedback feedback);
}
