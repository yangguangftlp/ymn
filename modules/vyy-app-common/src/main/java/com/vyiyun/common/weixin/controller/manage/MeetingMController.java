package com.vyiyun.common.weixin.controller.manage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.entity.Meeting;
import com.vyiyun.common.weixin.entity.MeetingRoom;
import com.vyiyun.common.weixin.service.IMeetingRoomService;
import com.vyiyun.common.weixin.service.IMeetingService;
import com.vyiyun.weixin.cache.impl.SystemConfigCache;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.controller.AbstWebController;
import com.vyiyun.weixin.entity.SystemConfig;
import com.vyiyun.weixin.exception.VyiyunBusinessException;
import com.vyiyun.weixin.model.DataResult;
import com.vyiyun.weixin.service.IEntityAccountService;
import com.vyiyun.weixin.service.ISystemConfigService;
import com.vyiyun.weixin.utils.CommonUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;
import com.vyiyun.weixin.web.model.ResponseResult;
import com.vyy.weixin.annotation.Suite;

/**
 * @ClassName: MeetingMController
 * @Description: 会议室管理
 * @author zb.shen
 * @date 2016年1月11日
 */
@Controller
@RequestMapping(value = "/manage/meeting")
@Suite("OA")
public class MeetingMController extends AbstWebController {

	private static final String DEFAULT_START_TIME = "08:30:00";

	private static final String DEFAULT_END_TIME = "21:00:00";

	/**
	 * 会议室服务
	 */
	@Autowired
	private IMeetingRoomService meetingRoomService;

	/**
	 * 会议服务
	 */
	@Autowired
	private IMeetingService meetingService;

	/**
	 * 系统配置服务
	 */
	@Autowired
	private ISystemConfigService systemConfigService;

	/**
	 * 实体-账户服务
	 */
	@Autowired
	private IEntityAccountService entityAccountService;

	/*
	 * 视图控制
	 */
	/**
	 * 请求会议室设置页面
	 * 
	 * @return 页面视图
	 */
	@RequestMapping(value = "setMeetingRoomConfig", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView setMeetingRoomConfigView() {
		ModelAndView modelView = createModelAndView("setMeetingRoomConfig");
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
		// **获取总得会议时间
		SystemConfig systemConfig = new SystemConfig();
		systemConfig.setType(Constants.SYSTEM_MEETING_ROOM_TIME);
		systemConfig.setCorpId(corpId);
		List<SystemConfig> sysConfList = systemConfigService.getSystemConfig(systemConfig);
		if (CollectionUtils.isEmpty(sysConfList) || sysConfList.size() != 2) {
			modelView.addObject("startTime", DEFAULT_START_TIME);
			modelView.addObject("endTime", DEFAULT_END_TIME);
		} else {
			SystemConfig sysConf1 = sysConfList.get(0);
			SystemConfig sysConf2 = sysConfList.get(1);
			if (sysConf1.getName().equals("meeting_totalStartTime")) {
				modelView.addObject("startTime", sysConf1.getValue());
				modelView.addObject("endTime", sysConf2.getValue());
				// modelView.addObject("startTime",
				// sysConf1.getValue().substring(0, 5));
				// modelView.addObject("endTime",
				// sysConf2.getValue().substring(0, 5));
			} else if (sysConf1.getName().equals("meeting_totalEndTime")) {
				modelView.addObject("startTime", sysConf2.getValue());
				modelView.addObject("endTime", sysConf1.getValue());
				// modelView.addObject("startTime",
				// sysConf2.getValue().substring(0, 5));
				// modelView.addObject("endTime",
				// sysConf1.getValue().substring(0, 5));
			}
		}

		return modelView;
	}

	/*
	 * 数据控制
	 */
	/**
	 * 获取所有的会议室列表数据
	 * 
	 * @return 会议室列表数据
	 */
	@RequestMapping(value = "queryMeetingRooms", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object queryMeetingRooms() {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		// 获取查询条件
		// String searchConditions =
		// HttpRequestUtil.getInst().getString("searchConditions");
		try {
			// JSONObject jsonSearchConditions =
			// JSONObject.parseObject(searchConditions);
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
			MeetingRoom room = new MeetingRoom();
			room.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			// room.setCapacity(jsonSearchConditions.getInteger("capacity"));
			// room.setRoomName(jsonSearchConditions.getString("roomName"));
			// 查询列表数据
			DataResult dataResult = meetingRoomService.queryMeetingRooms(room, new HashMap<String, Object>(),
					pageIndex, pageSize);
			dataMap.put("sEcho", HttpRequestUtil.getInst().getString("sEcho"));
			dataMap.put("iTotalRecords", dataResult.getTotal());
			dataMap.put("iTotalDisplayRecords", dataResult.getTotal());
			dataMap.put("data", dataResult.getData());
			return dataMap;
		} catch (Exception e) {
			dataMap.put("status", -1);
			dataMap.put("errorMsg", "数据获取异常!");
		}
		return dataMap;
	}

	/**
	 * 添加/修改会议室
	 * 
	 * @return 会议室ID
	 */
	@RequestMapping(value = "modifyMeetingRoom", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object modifyMeetingRoom() {
		ResponseResult responseResult = new ResponseResult();
		try {
			String meetingRoomJsonStr = HttpRequestUtil.getInst().getString("meetingRoom");
			MeetingRoom meetingRoom = JSONObject.parseObject(meetingRoomJsonStr, MeetingRoom.class);
			String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
			if (StringUtil.isBlank(meetingRoom.getId())) {
				String id = CommonUtil.GeneGUID();
				meetingRoom.setId(id);
				meetingRoom.setCorpId(corpId);
				meetingRoomService.addMeetingRoom(meetingRoom);
				responseResult.setValue(id);
			} else {
				meetingRoom.setCorpId(corpId);
				meetingRoomService.updateMeetingRoom(meetingRoom);
				responseResult.setValue(meetingRoom.getId());

				// 更新已创建会议的会议室名称
				Meeting meeting = new Meeting();
				meeting.setRoomId(meetingRoom.getId());
				meeting.setRoomName(meetingRoom.getRoomName());
				meetingService.updateMeetingByParameter(meeting);
			}

		} catch (Exception e) {
			if (e instanceof JSONException) {
				responseResult.setErrorMsg("解析会议室数据包出错，请检查重试");
			} else {
				responseResult.setErrorMsg("系统错误" + e.getMessage());
			}
			responseResult.setStatus(-1);
			e.printStackTrace();
		}
		return responseResult;
	}

	/**
	 * 删除会议室
	 * 
	 * @return 操作结果
	 */
	@RequestMapping(value = "deleteMeetingRoom", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object deleteMeetingRoom() {
		String roomIdsStr = HttpRequestUtil.getInst().getString("ids");
		ResponseResult responseResult = new ResponseResult();
		try {
			JSONArray roomIdsJSONArray = JSONObject.parseArray(roomIdsStr);
			List<String> roomIdList = new ArrayList<String>();
			String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
			// 删除与该会议室相关的会议记录
			if (!CollectionUtils.isEmpty(roomIdsJSONArray)) {
				for (Object roomId : roomIdsJSONArray) {
					roomIdList.add(String.valueOf(roomId));
					Meeting meeting = new Meeting();
					meeting.setCorpId(corpId);
					meeting.setRoomId(String.valueOf(roomId));
					List<Meeting> meetingList = meetingService.queryMeetingsByParameter(meeting);
					if (!CollectionUtils.isEmpty(meetingList)) {
						for (Meeting entity : meetingList) {
							String id = entity.getId();
							entityAccountService.deleteByEntityId(id);
							meetingService.deleteMeetingById(id);
						}
					}
				}
				meetingRoomService.deleteMeetingRoomsByIds(roomIdList);
			} else {
				throw new VyiyunBusinessException("请选择要删除的会议室");
			}
		} catch (Exception e) {

			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 更改会议室开放时间
	 * 
	 * @return 操作结果
	 */
	@RequestMapping(value = "modifyMeetingRoomOpenTime", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Object modifyMeetingRoomOpenTime() {
		ResponseResult responseResult = new ResponseResult();
		List<String> totalStartAndEndList = new ArrayList<String>();
		String startTime = HttpRequestUtil.getInst().getString("totalStart");
		String endTime = HttpRequestUtil.getInst().getString("totalEnd");
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
		if (StringUtil.isNotEmpty(startTime) && StringUtil.isNotEmpty(endTime)) {
			Pattern pattern = Pattern.compile("^\\d{1,2}:\\d{1,2}:\\d{1,2}$");
			Matcher matcherStart = pattern.matcher(startTime);
			Matcher matcherEnd = pattern.matcher(endTime);
			if ((!matcherStart.matches()) || (!matcherEnd.matches())) {
				responseResult.setStatus(-1);
				responseResult.setErrorMsg("时间格式不对");
				return responseResult;
			}
			try {
				Date start = new SimpleDateFormat("HH:mm:ss").parse(startTime);
				Date end = new SimpleDateFormat("HH:mm:ss").parse(endTime);
				// **获取当前日期并判断在会议室不对外开放时间段中是否有会议存在
				Integer usedMeetingCount = meetingService.queryMeetingCountWhenUpdateTotalTime(new Date(), start, end,
						corpId);
				if (usedMeetingCount != null && usedMeetingCount > 0) {
					responseResult.setStatus(-2);
					responseResult.setErrorMsg("在未选定的时间段有会议没有完成，暂时不能更改");
					return responseResult;
				} else {
					// **开始结束时间放入缓存
					SystemConfigCache<Object> systemConfigCache = SystemCacheUtil.getInstance().getSystemConfigCache();
					// **更新总得会议时间
					SystemConfig systemConfig = new SystemConfig();
					systemConfig.setCorpId(corpId);
					systemConfig.setType(Constants.SYSTEM_MEETING_ROOM_TIME);
					systemConfig.setName("meeting_totalStartTime");
					systemConfig.setValue(startTime);
					if (systemConfigService.update(systemConfig) < 1) {
						systemConfig.setId(CommonUtil.GeneGUID());
						systemConfig.setDesc(null);
						systemConfig.setPx(1);
						systemConfigService.addSystemConfig(systemConfig);
						systemConfigCache.add(corpId+"_MeetingRoomTime_meeting_totalStartTime", systemConfig);
					}
					SystemConfig sc = (SystemConfig) systemConfigCache.getSystemConfig(corpId, "MeetingRoomTime",
							"meeting_totalStartTime");
					if (sc != null) {
						sc.setValue(startTime);
					}

					systemConfig = new SystemConfig();
					systemConfig.setCorpId(corpId);
					systemConfig.setType(Constants.SYSTEM_MEETING_ROOM_TIME);
					systemConfig.setName("meeting_totalEndTime");
					systemConfig.setValue(endTime);
					if (systemConfigService.update(systemConfig) < 1) {
						systemConfig.setId(CommonUtil.GeneGUID());
						systemConfig.setDesc(null);
						systemConfig.setPx(1);
						systemConfigService.addSystemConfig(systemConfig);
						systemConfigCache.add(corpId+"_MeetingRoomTime_meeting_meeting_totalEndTime", systemConfig);
					}
					// **更新repeat=0
					meetingService.updateRepeatValueWhenUpdateTotalTime(start, end, corpId);
					totalStartAndEndList.add(startTime);
					totalStartAndEndList.add(endTime);
					sc = (SystemConfig) systemConfigCache.getSystemConfig(corpId, "MeetingRoomTime",
							"meeting_totalEndTime");
					if (sc != null) {
						sc.setValue(endTime);
					}
				}

			} catch (ParseException pe) {
				responseResult.setStatus(-3);
				responseResult.setErrorMsg("更改会议室时间失败");
				return responseResult;
			}
		} else {
			responseResult.setStatus(-4);
			responseResult.setErrorMsg("传入的时间字段为空");
			return responseResult;
		}
		responseResult.setStatus(0);
		responseResult.setValue(totalStartAndEndList);
		return responseResult;
	}

	@Override
	protected String getPrefix() {
		return super.getPrefix() + "manage/meeting/";
	}
}
