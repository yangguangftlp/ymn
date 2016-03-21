/**
 * 
 */
package com.vyiyun.common.weixin.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.entity.Feedback;
import com.vyiyun.weixin.model.DataResult;

/**
 * 系统反馈服务
 * 
 * @author tf
 * @date 2015年10月8日 下午4:14:16
 */
public interface IFeedbackService {

	/**
	 * 添加系统反馈
	 * 
	 * @param Feedback
	 * @return
	 */
	int addFeedback(Feedback feedback);

	/**
	 * 获取系统反馈记录 当 pageIndex，pageSize同时不为-1时启动分页
	 * 
	 * @param Feedback
	 * @param params
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	DataResult getFeedback(Feedback Feedback, Map<String, Object> params, int pageIndex, int pageSize);

	/**
	 * 更新系统反馈
	 * 
	 * @param Feedback
	 */
	int updateFeedback(Feedback feedback);

	/**
	 * 反馈排行
	 * 
	 * @param sort
	 *            desc asc 排序
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	DataResult getFeedbackTop(String sort, int pageIndex, int pageSize);

	/**
	 * 根据id获取反馈信息
	 * 
	 * @param id
	 */
	Feedback getFeedbackById(String id);

	/**
	 * 提交问题反馈
	 * 
	 * @param jsonFeedback
	 * @return 返回记录id
	 */
	Object submitFeedback(JSONObject jsonFeedback);

	/**
	 * 反馈处理
	 * 
	 * @param jsonFeedback
	 * @return
	 */
	Object doFeedbackOperate(JSONObject jsonFeedback);

	/**
	 * 解决提醒
	 * @param jsonReminder
	 * @return 操作结果
	 */
	Object feedbackRemind(JSONObject jsonReminder);
}
