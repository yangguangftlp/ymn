package com.vyiyun.common.weixin.controller.manage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.entity.AttendanceRule;
import com.vyiyun.common.weixin.entity.Workday;
import com.vyiyun.common.weixin.service.IAttendanceRuleService;
import com.vyiyun.common.weixin.service.ISignService;
import com.vyiyun.common.weixin.service.IWorkdayService;
import com.vyiyun.weixin.controller.AbstWebController;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.DataResult;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyiyun.weixin.web.model.ResponseResult;
import com.vyy.weixin.annotation.Suite;

/**
 * @ClassName: SignMController
 * @Description: 签到管理请求处理器
 * @author zb.shen
 * @date 2015年12月29日
 */
@Controller
@RequestMapping(value = "/manage/sign")
@Suite("OA")
public class SignMController extends AbstWebController {
	private static final Logger LOGGER = Logger.getLogger(SignMController.class);

	/**
	 * 考勤规则服务
	 */
	@Autowired
	private IAttendanceRuleService attendanceRuleService;

	/**
	 * 签到服务
	 */
	@Autowired
	private ISignService signService;

	/**
	 * 工作日配置服务
	 */
	@Autowired
	private IWorkdayService workdayService;

	/*
	 * 视图控制
	 */
	/**
	 * 考勤列表页面
	 * @return 页面视图
	 */
	@RequestMapping(value = "attendanceList", method = RequestMethod.GET)
	public ModelAndView attendanceListView() {
		ModelAndView modelAndView = super.createModelAndView("attendanceList");
		return modelAndView;
	}

	/**
	 * 特殊考勤日期列表页面
	 * @return 页面视图
	 */
	@RequestMapping(value = "workdayList", method = RequestMethod.GET)
	public ModelAndView workdayListView() {
		ModelAndView modelAndView = super.createModelAndView("workdayList");
//		modelAndView.addObject("workdayList", workdayService.queryWorkdays(new Workday()));
		return modelAndView;
	}

	/**
	 * 设置考勤规则页面
	 * @return 页面视图
	 */
	@RequestMapping(value = "setupAttendanceRule", method = RequestMethod.GET)
	public ModelAndView setupAttendanceRuleView() {
		ModelAndView modelAndView = super.createModelAndView("setupAttendanceRule");

		// 企业ID
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId();

		AttendanceRule attendanceRule = attendanceRuleService.getAttendanceRule(corpId);
//		modelAndView.addObject("attendanceRule", attendanceRule);
		if (attendanceRule != null) {
			Map<String, Object> retData = attendanceRule.getPersistentState();
			List<String> attendDays = new ArrayList<String>();
			if (attendanceRule.getMon().equals("1")) {
				attendDays.add("mon");
			}
			if (attendanceRule.getTues().equals("1")) {
				attendDays.add("tues");
			}
			if (attendanceRule.getWed().equals("1")) {
				attendDays.add("wed");
			}
			if (attendanceRule.getThur().equals("1")) {
				attendDays.add("thur");
			}
			if (attendanceRule.getFri().equals("1")) {
				attendDays.add("fri");
			}
			if (attendanceRule.getSat().equals("1")) {
				attendDays.add("sat");
			}
			if (attendanceRule.getSun().equals("1")) {
				attendDays.add("sun");
			} 
			retData.put("attendDays", attendDays);
			modelAndView.addObject("attendanceRuleJson", JSONObject.toJSON(retData));
		}

		return modelAndView;
	}

	/*
	 * 数据控制
	 */
	/**
	 * 修改考勤规则处理
	 * @return 操作结果
	 */
	@RequestMapping(value = "modifyAttendanceRule", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object modifyAttendanceRule() {
		ResponseResult responseResult = new ResponseResult();
		try {
			// 获取考勤规则
			String attendanceRuleStr = HttpRequestUtil.getInst().getString("attendanceRule");
			// 修改考勤规则
			boolean resultFlag = attendanceRuleService.modifyAttendanceRule(attendanceRuleStr);
			if (!resultFlag) {
				responseResult.setStatus(-1);
				responseResult.setErrorMsg("考勤规则更新失败");
			}
			responseResult.setValue(resultFlag);
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 获取考勤规则
	 * @return 考勤规则
	 */
	@RequestMapping(value = "getAttendanceRule", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object getAttendanceRule() {
		ResponseResult responseResult = new ResponseResult();
		try {
			responseResult.setValue(attendanceRuleService.getAttendanceRule(HttpRequestUtil.getInst().getCurrentCorpId()));
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 查询考勤列表数据
	 * @return 考勤列表数据
	 */
	@RequestMapping(value = "queryAttendanceList", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object queryAttendanceListData() {

		Map<String, Object> dataMap = new HashMap<String, Object>();
		// 获取查询条件
		String searchConditions = HttpRequestUtil.getInst().getString("searchConditions");
		try {
			JSONObject jsonSearchConditions = JSONObject.parseObject(searchConditions);
			// 查询列表数据
			jsonSearchConditions.put("corpId", HttpRequestUtil.getInst().getCurrentCorpId());
			Map<String, Object> dataResult = signService.queryAttendanceList(jsonSearchConditions);
			dataMap.put("sEcho", HttpRequestUtil.getInst().getString("sEcho"));
			dataMap.put("iTotalRecords", dataResult.get("totalCount"));
			dataMap.put("iTotalDisplayRecords", dataResult.get("totalCount"));
			dataMap.put("data", dataResult.get("list"));
			return dataMap;
		} catch (Exception e) {
			dataMap.put("status", -1);
			dataMap.put("errorMsg", "数据获取异常!");
			LOGGER.error("数据获取异常!", e);
		}
		return dataMap;
	}

	/**
	 * 导出考勤数据
	 * @return excel文件字节流
	 */
	@RequestMapping(value = "exportAttendanceListToExcel", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<byte[]> exportAttendanceListToExcel() {
		// 获取查询条件
		String searchConditions = HttpRequestUtil.getInst().getString("searchConditions");
		try {
			JSONObject jsonSearchConditions = JSONObject.parseObject(searchConditions);
			// 导出考勤数据
			jsonSearchConditions.put("corpId", HttpRequestUtil.getInst().getCurrentCorpId());
			return signService.exportAttendanceListToExcel(jsonSearchConditions);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("导出数据异常!", e);
		}
		return null;
		
	}

	/**
	 * 增加工作日
	 * @return 操作结果
	 */
	@RequestMapping(value = "addWorkDay", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object addWorkDay() {
		ResponseResult responseResult = new ResponseResult();
		try {
			// 获取工作日JSON字符串
			String workDayStr = HttpRequestUtil.getInst().getString("workDay");
			if (StringUtil.isBlank(workDayStr)) {
				responseResult.setStatus(-1);
				responseResult.setErrorMsg("工作日信息数据包不能为空");
				return responseResult;
			}
			// 增加工作日
			String[] ids = workdayService.addWorkday(workDayStr);
			responseResult.setValue(ids);
		} catch (VyiyunException e) {
			responseResult.setStatus(0);
			responseResult.setErrorMsg(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 查询工作日列表数据
	 * @return 工作日列表数据
	 */
	@RequestMapping(value = "queryWorkDayList", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object queryWorkDayListData() {

		Map<String, Object> dataMap = new HashMap<String, Object>();
		// 获取查询条件
		try {
			// 查询列表数据
			Workday workday = new Workday();
			workday.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			DataResult dataResult = workdayService.queryWorkdays(workday);
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
	 * 删除工作日
	 * @return 操作结果
	 */
	@RequestMapping(value = "delWorkDay", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object delWorkDay() {
		ResponseResult responseResult = new ResponseResult();
		try {
			// 获取工作日ID
			String id = HttpRequestUtil.getInst().getString("id");
			if (StringUtil.isBlank(id)) {
				responseResult.setStatus(-1);
				responseResult.setErrorMsg("工作日ID不能为空");
				return responseResult;
			}
			// 删除工作日
			int retCnt = workdayService.deleteWorkday(id);
			responseResult.setValue(retCnt);
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	@Override
	protected String getPrefix() {
		return super.getPrefix() + "manage/sign/";
	}
}
