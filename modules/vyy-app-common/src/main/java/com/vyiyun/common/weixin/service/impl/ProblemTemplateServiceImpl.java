/**
 * 
 */
package com.vyiyun.common.weixin.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.vyiyun.common.weixin.dao.ProblemTemplateMapper;
import com.vyiyun.common.weixin.entity.ProblemTemplate;
import com.vyiyun.common.weixin.service.IProblemTemplateService;
import com.vyiyun.weixin.model.DataResult;
import com.vyiyun.weixin.model.SqlQueryParameter;

/**
 * 问题模板实现
 * 
 * @author tf
 * @date 2015年11月17日 上午11:07:39
 */
@Service("problemTemplateService")
public class ProblemTemplateServiceImpl implements IProblemTemplateService {

	@Autowired
	private ProblemTemplateMapper problemTemplateDao;

	@Override
	public int addProblemTemplate(ProblemTemplate... problemTemplate) {
		if (problemTemplate.length < 1) {
			return 0;
		}
		return problemTemplateDao.addProblemTemplate(Arrays.asList(problemTemplate));
	}

	@Override
	public DataResult getProblemTemplate(ProblemTemplate problemTemplate, Map<String, Object> params, int pageIndex,
			int pageSize) {
		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPage(true);
		}
		if (pageSize != -1) {
			sqlQueryParameter.setPageSize(pageSize);
		}
		if (!CollectionUtils.isEmpty(params)) {
			sqlQueryParameter.getKeyValMap().putAll(params);
		}
		List<ProblemTemplate> problemTemplateList = problemTemplateDao.getProblemTemplate(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(problemTemplateList)) {
			dataResult.setData(problemTemplateList);
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}

	@Override
	public List<ProblemTemplate> getProblemTemplate(ProblemTemplate problemTemplate, Map<String, Object> params) {
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (!CollectionUtils.isEmpty(params)) {
			sqlQueryParameter.getKeyValMap().putAll(params);
		}
		sqlQueryParameter.setParameter(problemTemplate);
		return problemTemplateDao.getProblemTemplate(sqlQueryParameter);
	}

	@Override
	public List<ProblemTemplate> getProblemTemplate(ProblemTemplate problemTemplate) {
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		sqlQueryParameter.setParameter(problemTemplate);
		return problemTemplateDao.getProblemTemplate(sqlQueryParameter);
	}

	@Override
	public int deleteByAppraisalId(String id) {
		ProblemTemplate problemTemplate = new ProblemTemplate();
		problemTemplate.setAppraisalId(id);
		return problemTemplateDao.deleteProblemTemplate(problemTemplate);
	}

}
