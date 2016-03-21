package com.vyiyun.common.weixin.controller.manage;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.entity.Absent;
import com.vyiyun.common.weixin.service.IAbsentService;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.controller.AbstWebController;
import com.vyiyun.weixin.entity.SystemStatus;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.DataResult;
import com.vyiyun.weixin.service.ISystemStatusService;
import com.vyiyun.weixin.utils.CommonUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;
import com.vyiyun.weixin.web.model.ResponseResult;
import com.vyy.weixin.annotation.Suite;

/**
 * @ClassName: AbsentMController
 * @Description: 请假管理请求处理器
 * @author zb.shen
 * @date 2015年12月29日
 */
@Controller
@RequestMapping(value = "/manage/absent")
@Suite("OA")
public class AbsentMController extends AbstWebController {
	private static final Logger LOGGER = Logger.getLogger(AbsentMController.class);

	/**
	 * 系统状态服务
	 */
	@Autowired
	private ISystemStatusService systemStatusService;

	/**
	 * 请假服务
	 */
	@Autowired
	private IAbsentService absentservice;

	/*
	 * 视图控制
	 */
	/**
	 * 设置请假类型页面
	 * 
	 * @return 页面视图
	 */
	@RequestMapping(value = "setupAbsentType", method = RequestMethod.GET)
	public ModelAndView setupAbsentTypeView() {
		ModelAndView modelAndView = super.createModelAndView("setupAbsentType");
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
//		List<SystemStatus> absentTypeList = (List<SystemStatus>) SystemCacheUtil.getInstance().getSystemStatusCache().get(corpId + "_" + Constants.SYSTEM_ABSENT_TYPE);
		List<SystemStatus> absentTypeList = systemStatusService.getSystemStatus(corpId, Constants.SYSTEM_ABSENT_TYPE);
		if (!CollectionUtils.isEmpty(absentTypeList)) {
//			absentTypeList = systemStatusService.getSystemStatus(corpId, Constants.SYSTEM_ABSENT_TYPE);
			modelAndView.addObject("absentTypeList", absentTypeList);
		}
		return modelAndView;
	}

	/**
	 * 请假列表页面
	 * 
	 * @return 页面视图
	 */
	@RequestMapping(value = "absentList", method = RequestMethod.GET)
	public ModelAndView absentListView() {
		ModelAndView modelAndView = super.createModelAndView("absentList");
		return modelAndView;
	}

	/*
	 * 数据控制
	 */
	/**
	 * 修改请假类型
	 * 
	 * @return 操作结果
	 */
	@RequestMapping(value = "modifyAbsentType", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object modifyAbsentType() {
		ResponseResult responseResult = new ResponseResult();
		try {
			// 请假类型JSON字符串
			String absentTypeStr = HttpRequestUtil.getInst().getString("absentType");
			SystemStatus systemStatus = JSONObject.parseObject(absentTypeStr, SystemStatus.class);
			String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
			if (StringUtil.isBlank(systemStatus.getId())) {
				// 新增请假类型
				systemStatus.setId(CommonUtil.GeneGUID());
				systemStatus.setCorpId(corpId);
				systemStatus.setType(Constants.SYSTEM_ABSENT_TYPE);
				systemStatusService.addSystemStatus(systemStatus);
				responseResult.setValue(systemStatus.getId());
			} else {				
				// 修改请假类型
				systemStatusService.updateSystemStatus(systemStatus);
				responseResult.setValue(systemStatus.getId());
			}
			// 清除缓存
			SystemCacheUtil.getInstance().getSystemStatusCache().remove(HttpRequestUtil.getInst().getCurrentCorpId() + "_" + Constants.SYSTEM_ABSENT_TYPE);
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 删除请假类型
	 * 
	 * @return 操作结果
	 */
	@RequestMapping(value = "deleteAbsentType", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object deleteAbsentType() {
		ResponseResult responseResult = new ResponseResult();
		try {
			// 请假类型ID
			String id = HttpRequestUtil.getInst().getString("id");
			if (StringUtil.isBlank(id)) {
				responseResult.setStatus(-1);
				responseResult.setErrorMsg("请假类型ID不能为空");
			}
			// 删除请假类型
			String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
			SystemStatus param = new SystemStatus();
			param.setId(id);
			param.setCorpId(corpId);
			List<SystemStatus> systemStatusList = systemStatusService.getSystemStatus(param);
			if (!CollectionUtils.isEmpty(systemStatusList)) {
				SystemStatus systemStatus = systemStatusList.get(0);
				String absentType = systemStatus.getValue();
				absentservice.deleteRelatedRecord(corpId, absentType);
				systemStatusService.deleteBySystemStatusId(id);
				SystemCacheUtil.getInstance().getSystemStatusCache().remove(corpId + "_" + Constants.SYSTEM_ABSENT_TYPE);;
				// 删除该请假类型相关数据
			} else {
				throw new VyiyunException("该请假类型不存在或已删除");
			}

		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setErrorMsg(e.getMessage());
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 查询请假数据
	 * 
	 * @return 请假数据
	 */
	@RequestMapping(value = "queryAbsentList", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object queryAbsentListData() {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		// 获取查询条件
		String searchConditions = HttpRequestUtil.getInst().getString("searchConditions");
		try {
			JSONObject jsonSearchConditions = JSONObject.parseObject(searchConditions);
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
			params.put("userNameLike", jsonSearchConditions.getString("userName"));
			params.put("startDate", jsonSearchConditions.getDate("startTime"));
			// BUG#179修改  by zb.shen 2016-01-25 start.
			Date endDate = jsonSearchConditions.getDate("endTime");
			if (null != endDate) {
				endDate.setTime(endDate.getTime() + 24 * 3600 * 1000 - 1);
			}
			params.put("endDate", endDate);
			// BUG#179修改  by zb.shen 2016-01-25 end.
			// BUG #200 修改 by zb.shen 2016-01-22 start.
			params.put("orderBy", "CreateTime DESC");
			params.put("status", jsonSearchConditions.getString("status"));
			// BUG #200 修改 by zb.shen 2016-01-22 end.
			Absent absent = new Absent();
//			absent.setStatus(jsonSearchConditions.getString("status"));
			absent.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			// 查询列表数据
			DataResult dataResult = absentservice.queryAbsentRecord(absent, params, pageIndex, pageSize);
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

	/**
	 * 导出请假数据
	 * 
	 * @return excel文件字节流
	 */
	@RequestMapping(value = "exportAbsentListToExcel", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<byte[]> exportAbsentListToExcel() {
		// 获取查询条件
		String searchConditions = HttpRequestUtil.getInst().getString("searchConditions");
		try {
			JSONObject jsonSearchConditions = JSONObject.parseObject(searchConditions);
			// 导出请假数据
			jsonSearchConditions.put("corpId", HttpRequestUtil.getInst().getCurrentCorpId());
			return absentservice.exportAbsentListToExcel(jsonSearchConditions);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("导出数据异常!", e);
		}
		return null;

	}

	@Override
	protected String getPrefix() {
		return super.getPrefix() + "manage/absent/";
	}
}
