/**
 * 
 */
package com.vyiyun.common.weixin.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vyiyun.common.weixin.dao.ScoreMapper;
import com.vyiyun.common.weixin.entity.Score;
import com.vyiyun.common.weixin.service.IScoreService;

/**
 * @author tf
 * @date 2015年11月18日 下午2:43:09
 */
@Service("scoreService")
public class ScoreServiceImpl implements IScoreService {

	/**
	 * 评分dao
	 */
	@Autowired
	private ScoreMapper scoreDao;

	@Override
	public int addScore(Score... score) {
		return scoreDao.addScore(Arrays.asList(score));
	}

	@Override
	public List<Map<String, Object>> getProblemScore(Map<String, Object> params) {
		return scoreDao.getProblemScore(params);
	}

	@Override
	public List<Map<String, Object>> getProblemOpinion(Map<String, Object> params) {
		return scoreDao.getProblemOpinion(params);
	}

	@Override
	public List<Score> getScore(Score score) {
		return scoreDao.getScore(score);
	}

	@Override
	public List<Map<String, Object>> getProblemScoreStatistic(Map<String, Object> params) {
		return scoreDao.getProblemScoreStatistic(params);
	}

}
