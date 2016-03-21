/**
 * 
 */
package com.vyiyun.common.weixin.dao;

import java.util.List;

import com.vyiyun.common.weixin.entity.ProblemTemplate;
import com.vyiyun.weixin.model.SqlQueryParameter;

/**
 * 评价问题模板Dao
 * 
 * @author tf
 * @date 2015年11月16日 上午11:11:40
 */
public interface ProblemTemplateMapper {

	/**
	 * 添加问题记录
	 * 
	 * @param problemTemplate
	 * @return
	 */
	int addProblemTemplate(List<ProblemTemplate> problemTemplate);

	/**
	 * 获取问题记录
	 * 
	 * @param SqlQueryParameter
	 * @return
	 */
	List<ProblemTemplate> getProblemTemplate(SqlQueryParameter sqlQueryParameter);

	/**
	 * 删除问题模板
	 * 
	 * @param problemTemplate
	 * @return
	 */
	int deleteProblemTemplate(ProblemTemplate problemTemplate);

}
