/**
 * 
 */
package com.vyiyun.common.weixin.controller.mobile;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.entity.Courier;
import com.vyiyun.common.weixin.service.ICourierService;
import com.vyiyun.weixin.controller.AbstWebController;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.WeixinUser;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.SpringContextHolder;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyiyun.weixin.web.model.ResponseResult;
import com.vyy.weixin.annotation.App;
import com.vyy.weixin.annotation.OAuth;
import com.vyy.weixin.annotation.Suite;

/**
 * 快递
 * 
 * @author tf
 * @date 2015年10月15日 下午2:04:25
 */
@Controller
@RequestMapping(value = "/mobile/courier")
@Suite("2")
@OAuth
@App(id = "courier")
public class CourierController extends AbstWebController {
	private static final Logger LOGGER = Logger.getLogger(CourierController.class);

	/**
	 * 快递服务
	 */
	@Autowired
	private ICourierService courierSerivce;

	/**
	 * 快递登记
	 * 
	 * @return
	 */
	@RequestMapping(value = "registerView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView registerView(HttpServletRequest request) {
		ModelAndView modelView = createModelAndViewWithSign("register", request);
		return modelView;
	}

	/**
	 * 处理快递登记
	 * 
	 * @return
	 */
	@RequestMapping(value = "doRegister", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object doRegister() {
		ResponseResult responseResult = new ResponseResult();
		try {
			JSONObject jsonCourier = JSONObject.parseObject(HttpRequestUtil.getInst().getString("courierInfo"));
			courierSerivce.addCourier(jsonCourier);
		} catch (Exception e) {
			responseResult.setStatus(-1);
			LOGGER.error("注册快递信息失败!", e);
			if (e instanceof VyiyunException) {
				responseResult.setErrorMsg(e.getMessage());
			} else {
				responseResult.setErrorMsg(SpringContextHolder.getI18n("1000000"));
			}
		}
		return responseResult;
	}

	/**
	 * 详情
	 * 
	 * @return
	 */
	@RequestMapping(value = "courierDetailView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView courierDetailView() {
		ModelAndView modelAndView = createModelAndView("courierDetail");
		String id = HttpRequestUtil.getInst().getString("id");
		Courier courier = courierSerivce.getCourierById(id);
		WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
		if (weixinUser.getUserid().equalsIgnoreCase(courier.getCreatorId())) {
			modelAndView.addObject("isShow", "true");
		}
		modelAndView.addObject("courier", courier);
		return modelAndView;
	}

	/**
	 * 获取快递列表页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "courierListView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView courierListView() {
		ModelAndView modelAndView = createModelAndView("courierList");
		return modelAndView;
	}

	/**
	 * 获取快递列表页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "myCourierListView", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView myCourierListView() {
		ModelAndView modelAndView = createModelAndView("myCourierList");
		return modelAndView;
	}

	/**
	 * 获取快递列表数据
	 * 
	 * @return
	 */
	@RequestMapping(value = "getCourierList", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getCourierList() {
		String consigneeName = HttpRequestUtil.getInst().getString("consigneeName");
		String status = HttpRequestUtil.getInst().getString("status");
		WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
		Courier courier = new Courier();
		courier.setCreatorId(weixinUser.getUserid());
		courier.setCorpId(weixinUser.getCorpId());
		if (StringUtil.isNotEmpty(consigneeName)) {
			courier.setConsigneeName(consigneeName);
		}
		if (StringUtil.isNotEmpty(status)) {
			courier.setStatus(status);
		}
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("orderby", "CreateTime desc ,ConsigneeId desc");
		return getCourierDataList(courier, dataMap);
	}

	/**
	 * 获取快递列表数据
	 * 
	 * @return
	 */
	@RequestMapping(value = "getMyCourierList", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getMyCourierList() {
		String status = HttpRequestUtil.getInst().getString("status");
		WeixinUser weixinUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
		Courier courier = new Courier();
		courier.setConsigneeId(weixinUser.getUserid());
		courier.setCorpId(weixinUser.getCorpId());
		if (StringUtil.isNotEmpty(status)) {
			courier.setStatus(status);
		}
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("orderby", "CreateTime desc ,ConsigneeId desc");
		return getCourierDataList(courier, dataMap);
	}

	private ResponseResult getCourierDataList(Courier courier, Map<String, Object> dataMap) {
		ResponseResult responseResult = new ResponseResult();
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
		try {
			responseResult.setValue(courierSerivce.getCourier(courier, dataMap, pageIndex, pageSize));
		} catch (Exception e) {
			responseResult.setStatus(-1);
			LOGGER.error("获取快递记录失败!", e);
			if (e instanceof VyiyunException) {
				responseResult.setErrorMsg(e.getMessage());
			} else {
				responseResult.setErrorMsg(SpringContextHolder.getI18n("1000000"));
			}
		}
		return responseResult;
	}

	/**
	 * 快递收取
	 * 
	 * @return
	 */
	@RequestMapping(value = "receive", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object receive() {
		// 获取当前用户id、快递单号
		// 这里需要更新快递数据
		ResponseResult responseResult = new ResponseResult();
		try {
			String id = HttpRequestUtil.getInst().getString("id");
			if (StringUtil.isEmpty(id)) {
				throw new VyiyunException("1002002", new String[] { id });
			}
			Courier courier = courierSerivce.getCourierById(id);
			if (null == courier) {
				throw new VyiyunException("记录不存在或已被删除!");
			}

			if ("1".equals(courier.getStatus())) {
				responseResult.setErrorMsg("快递已被领取!");
				responseResult.setErrorCode(-1);
			} else {
				courier = new Courier();
				courier.setId(id);
				courier.setStatus("1");
				courierSerivce.updateCourier(courier);
			}
		} catch (Exception e) {
			responseResult.setErrorCode(-1);
			LOGGER.error("领取快递失败!", e);
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
		return super.getPrefix() + "mobile/courier/";
	}
}
