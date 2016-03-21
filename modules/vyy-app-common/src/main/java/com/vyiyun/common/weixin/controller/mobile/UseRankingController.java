/**
 * 
 */
package com.vyiyun.common.weixin.controller.mobile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.vyiyun.common.weixin.entity.AppAccess;
import com.vyiyun.common.weixin.enums.CommonAppType;
import com.vyiyun.common.weixin.service.IAppAccessService;
import com.vyiyun.weixin.controller.AbstWebController;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.SpringContextHolder;
import com.vyiyun.weixin.web.model.ResponseResult;
import com.vyy.weixin.annotation.OAuth;
import com.vyy.weixin.annotation.Suite;

/**
 * 使用排行
 * 
 * @author tf
 * @date 2015年10月8日 下午3:45:32
 */
@Controller
@RequestMapping(value = "/mobile/useRanking")
@Suite("2")
@OAuth
public class UseRankingController extends AbstWebController {

	@Autowired
	private IAppAccessService appAccessService;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "userRankingView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView userRanking() {
		ModelAndView modelView = createModelAndView("userRanking");
		Map<String, Object> params = new HashMap<String, Object>();
		AppAccess appAccess = new AppAccess();
		appAccess.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
		params.put("type", CommonAppType.AppAccessType.USER.value());
		params.put("sort", "desc");
		List<Map<String, Object>> topData = (List<Map<String, Object>>) appAccessService.getAppAccessRanking(appAccess,
				params, 1, 20).getData();
		params.put("sort", "asc");
		List<Map<String, Object>> lastData = (List<Map<String, Object>>) appAccessService.getAppAccessRanking(appAccess,
				params, 1, 20).getData();
		if (!CollectionUtils.isEmpty(topData)) {
			modelView.addObject("topData", topData);
		}
		if (!CollectionUtils.isEmpty(lastData)) {
			modelView.addObject("lastData", lastData);
		}
		return modelView;
	}

	@RequestMapping(value = "appRankingView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView appRanking() {
		ModelAndView modelView = createModelAndView("appRanking");
		return modelView;

	}

	@RequestMapping(value = "getAppRankingRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getAppRankingRecord() {

		ResponseResult responseResult = new ResponseResult();

		String opType = HttpRequestUtil.getInst().getString("opType");
		if (StringUtils.isEmpty(opType)) {
			responseResult.setStatus(-1);
			responseResult.setErrorMsg("参数opType不能为空!");
		}
		if (!opType.equals("w") && !opType.equals("m") && !opType.equals("t")) {
			responseResult.setStatus(-1);
			responseResult.setErrorMsg("无效的参数opType值【" + opType + "】!");
			return responseResult;
		}
		int pageIndex = -1;
		int pageSize = -1;
		String sPageIndex = HttpRequestUtil.getInst().getString("pageIndex");
		String sPageSize = HttpRequestUtil.getInst().getString("pageSize");
		if (StringUtils.isNotEmpty(sPageIndex) && sPageIndex.matches("\\d+")) {
			pageIndex = Integer.parseInt(sPageIndex);
		}
		if (StringUtils.isNotEmpty(sPageSize) && sPageSize.matches("\\d+")) {
			pageSize = Integer.parseInt(sPageSize);
		}
		AppAccess appAccess = new AppAccess();
		appAccess.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", CommonAppType.AppAccessType.APP.value());
		params.put("sort", opType);
		try {
			responseResult.setValue(appAccessService.getAppAccessRanking(appAccess, params, pageIndex, pageSize).getData());
		} catch (Exception e) {
			responseResult.setStatus(-1);
			if (e instanceof VyiyunException) {
				responseResult.setErrorMsg(e.getMessage());
			} else {
				responseResult.setErrorMsg(SpringContextHolder.getI18n("1000000"));
			}
		}
		return responseResult;

	}

	@Override
	protected String getPrefix() {
		return super.getPrefix() + "mobile/useRanking/";
	}

}
