package com.vyiyun.common.weixin.controller.manage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.entity.AppAccess;
import com.vyiyun.common.weixin.enums.CommonAppType;
import com.vyiyun.common.weixin.service.IAppAccessService;
import com.vyiyun.weixin.controller.AbstWebController;
import com.vyiyun.weixin.model.DataResult;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyy.weixin.annotation.Suite;

/**
 * @ClassName: UseRankingMController
 * @Description: 使用排行管理请求处理器
 * @author zb.shen
 * @date 2016年1月8日
 */
@Controller
@RequestMapping(value = "/manage/useRanking")
@Suite("OA")
public class UseRankingMController extends AbstWebController {
	private static final Logger LOGGER = Logger.getLogger(UseRankingMController.class);

	/**
	 * 应用访问记录
	 */
	@Autowired
	private IAppAccessService appAccessService;

	/*
	 * 视图控制
	 */
	/**
	 * 人员使用排行页面
	 * @return 页面视图
	 */
	@RequestMapping(value = "userRankingList", method = RequestMethod.GET)
	public ModelAndView userRankingListView() {
		ModelAndView modelAndView = super.createModelAndView("userRankingList");
		return modelAndView;
	}

	/**
	 * 应用使用排行页面
	 * @return 页面视图
	 */
	@RequestMapping(value = "appRankingList", method = RequestMethod.GET)
	public ModelAndView appRankingListView() {
		ModelAndView modelAndView = super.createModelAndView("appRankingList");
		return modelAndView;
	}

	/*
	 * 数据控制
	 */
	/**
	 * 查询使用排行数据
	 * @return 使用排行数据
	 */
	@RequestMapping(value = "queryAppAccessList", method = { RequestMethod.GET, RequestMethod.POST })
	public Object queryUserRankingListData() {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		// 获取查询条件
		String searchConditions = HttpRequestUtil.getInst().getString("searchConditions");
		try {
			JSONObject jsonSearchConditions = JSONObject.parseObject(searchConditions);
			String appAccessType = jsonSearchConditions.getString("appAccessType"); // 排行类型
			String sPageIndex = HttpRequestUtil.getInst().getString("start"); // 页码
			String sPageSize = HttpRequestUtil.getInst().getString("length"); // 页面大小
			int pageIndex = -1;
			int pageSize = -1;
			if (StringUtils.isNotEmpty(sPageSize) && sPageSize.matches("\\d+")) {
				pageSize = Integer.parseInt(sPageSize);
			}
			if (StringUtils.isNotEmpty(sPageIndex) && sPageIndex.matches("\\d+")) {
				pageIndex = Integer.parseInt(sPageIndex);
				if (pageIndex == 0) {
					pageIndex = 1;
				} else {
					pageIndex = pageIndex / pageSize + 1;
				}
			}

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("type", appAccessType);
			if (appAccessType.equals(CommonAppType.AppAccessType.USER.value())) {
				// 人员使用排行
				params.put("userNameLike", jsonSearchConditions.getString("userName"));
				params.put("sort", "desc");
			} else {
				// 应用使用排行
				String opType = jsonSearchConditions.getString("opType");
				if (!Arrays.asList(new String[] {"w", "m", "t"}).contains(opType)) {
					dataMap.put("status", -1);
					dataMap.put("errorMsg", "无效的参数opType值【" + opType + "】!");
					return dataMap;
				}
				params.put("appNameLike", jsonSearchConditions.getString("appName"));
				params.put("sort", opType);
			}
			AppAccess appAccess = new AppAccess();
			appAccess.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			// 查询列表数据
			DataResult dataResult = appAccessService.getAppAccessRanking(appAccess, params, pageIndex, pageSize);
			dataMap.put("sEcho", HttpRequestUtil.getInst().getString("sEcho"));
			dataMap.put("iTotalRecords", dataResult.getTotal());
			dataMap.put("iTotalDisplayRecords", dataResult.getTotal());
			dataMap.put("data", dataResult.getData());
			return dataMap;
		} catch (Exception e) {
			dataMap.put("status", -1);
			dataMap.put("errorMsg", "数据获取异常!");
			LOGGER.error("数据获取异常!", e);
		}
		return dataMap;
		
	}

	@Override
	protected String getPrefix() {
		return super.getPrefix() + "manage/useRanking/";
	}
}
