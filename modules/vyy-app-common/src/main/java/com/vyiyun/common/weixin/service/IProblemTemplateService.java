/**
 * 
 */
package com.vyiyun.common.weixin.service;

import java.util.List;
import java.util.Map;

import com.vyiyun.common.weixin.entity.ProblemTemplate;
import com.vyiyun.weixin.model.DataResult;

/**
 * 问题模板配置
 * 
 * @author tf
 * @date 2015年11月17日 上午11:06:03
 */
public interface IProblemTemplateService {
	/**
	 * 添加问题记录
	 * 
	 * @param problemTemplate
	 * @return
	 */
	int addProblemTemplate(ProblemTemplate... problemTemplate);

	/**
	 * 获取问题记录
	 * 
	 * @param SqlQueryParameter
	 * @return
	 */
	DataResult getProblemTemplate(ProblemTemplate problemTemplate, Map<String, Object> params, int pageIndex,
			int pageSize);

	/**
	 * 获取问题记录
	 * 
	 * @param problemTemplate
	 * @param params
	 * @return
	 */
	List<ProblemTemplate> getProblemTemplate(ProblemTemplate problemTemplate, Map<String, Object> params);

	/**
	 * 获取问题记录
	 * 
	 * @param problemTemplate
	 * @param params
	 * @return
	 */
	public List<ProblemTemplate> getProblemTemplate(ProblemTemplate problemTemplate);

	/**
	 * 根据评价id删除问题
	 * 
	 * @param id
	 * @return
	 */
	int deleteByAppraisalId(String id);
}
