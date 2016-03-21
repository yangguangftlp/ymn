package com.vyiyun.common.weixin.controller.mobile;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vyiyun.common.weixin.entity.Meeting;
import com.vyiyun.common.weixin.entity.MeetingRoom;
import com.vyiyun.common.weixin.entity.MeetingRoomVo;
import com.vyiyun.common.weixin.service.IMeetingRoomService;
import com.vyiyun.common.weixin.service.IMeetingService;
import com.vyiyun.weixin.cache.impl.SystemConfigCache;
import com.vyiyun.weixin.controller.AbstWebController;
import com.vyiyun.weixin.entity.EntityAccount;
import com.vyiyun.weixin.entity.SystemConfig;
import com.vyiyun.weixin.exception.VyiyunBusinessException;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.SqlQueryParameter;
import com.vyiyun.weixin.service.IEntityAccountService;
import com.vyiyun.weixin.utils.DateUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;
import com.vyiyun.weixin.web.model.ResponseResult;
import com.vyy.weixin.annotation.App;
import com.vyy.weixin.annotation.OAuth;
import com.vyy.weixin.annotation.Suite;

/**
 * @ClassName: MeetingController
 * @author XiaoWei
 * @date Dec 30, 2015 6:53:31 PM
 */
@Controller
@RequestMapping(value = "/mobile/meeting")
@Suite("3")
@OAuth
@App(id = "meeting")
public class MeetingController extends AbstWebController {
	static String[] defaultTime = new String[] { "00:00", "00:30", "01:00", "01:30", "02:00", "02:30", "03:00",
			"03:30", "04:00", "04:30", "05:00", "05:30", "06:00", "06:30", "07:00", "07:30", "08:00", "08:30", "09:00",
			"09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00",
			"15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00",
			"21:30", "22:00", "22:30", "23:00", "23:30" };
	@Autowired
	private IMeetingRoomService meetingRoomService;
	@Autowired
	private IMeetingService meetingService;
	@Autowired
	private IEntityAccountService entityAccountService;

	/**
	 * @Description: 会议室预定条件初始化页面
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "meeting_init", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView meeting_init(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelView = createModelAndView("meeting_init");
		List<MeetingRoom> meetingRoomList = meetingRoomService.queryAllMeetingRooms(null);
		if (!CollectionUtils.isEmpty(meetingRoomList)) {
			modelView.addObject("meetingRoomList", meetingRoomList);
		}
		return modelView;
	}

	/**
	 * @Description: 获取数据，当天的所有的会议室数据
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getMeetingRoomsOfTheDay", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getMeetingRoomsOfTheDay(HttpServletRequest request) {

		String pageIndexStr = HttpRequestUtil.getInst().getString("pageIndex");
		int pageIndex = 1;
		if (StringUtil.isNotEmpty(pageIndexStr)) {
			pageIndex = Integer.parseInt(pageIndexStr);
		}
		String dateStr = HttpRequestUtil.getInst().getString("date");
		String capacity = HttpRequestUtil.getInst().getString("capacity");
		if (StringUtil.isNotEmpty(capacity)) {
			if (!capacity.matches("^[1-9]\\d*$")) {
				throw new VyiyunBusinessException("容纳人数必须为纯数字");
			}
		}
		String roomName = HttpRequestUtil.getInst().getString("roomName");
		String order = HttpRequestUtil.getInst().getString("order");
		Date date = DateUtil.stringToDate(dateStr, "yyyy-MM-dd");
		if (date != null) {
			/*
			 * List<MeetingRoomVo> meetingRoomVoList = meetingRoomService
			 * .queryAllMeetingRoomsOfTheDay(date, capacity, roomName, order,
			 * pageIndex);
			 */
			List<Map<String, Object>> meetingRoomVoList = meetingRoomService.getAvailableMeetingRooms(date, capacity,
					roomName, order, pageIndex);

			if (!CollectionUtils.isEmpty(meetingRoomVoList)) {
				ArrayList<String> defaultTimes = new ArrayList<String>();
				Date currentTime = new Date();
				String currentDay = DateUtil.dateToString(currentTime, "yyyy-MM-dd");
				String currentTimes = DateUtil.dateToString(currentTime, "HH:mm");
				if (!currentDay.equals(dateStr)) {
					currentTimes = null;
				}
				SystemConfigCache<Object> systemConfigCache = SystemCacheUtil.getInstance().getSystemConfigCache();
				// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
				String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
				SystemConfig sc = systemConfigCache
						.getSystemConfig(corpId, "MeetingRoomTime", "meeting_totalStartTime");
				if (null == sc) {
					throw new VyiyunException("请配置会议室开始时间");
				}
				// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
				String startTime = sc.getValue();
				// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
				sc = systemConfigCache.getSystemConfig(corpId, "MeetingRoomTime", "meeting_totalEndTime");
				if (null == sc) {
					throw new VyiyunException("请配置会议室结束时间");
				}
				// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
				String endTime = sc.getValue();
				startTime = startTime.substring(0, 5);
				endTime = endTime.substring(0, 5);
				String time = null;
				int j = 0;
				int size = defaultTime.length;
				for (int i = 0; i < size; i++) {
					time = defaultTime[i];
					if (startTime.compareTo(time) <= 0) {
						defaultTimes.add(time);
						j = i + 1;
						break;
					}
				}
				for (; j < size; j++) {
					time = defaultTime[j];
					if (endTime.compareTo(time) <= 0) {
						break;
					}
					defaultTimes.add(time);
				}

				List<String> newTime = null;
				Map<String, Object> map = null;
				String roomId = null;
				String startAndEnd = null;
				String[] seStr = null;
				String[] seTime = null;
				float ztime = 0;
				int i = 0;
				for (int ij = 0; ij < meetingRoomVoList.size(); ij++) {
					ztime = 0;
					newTime = new ArrayList<String>(defaultTimes);
					map = meetingRoomVoList.get(ij);
					roomId = StringUtil.getString(map.get("roomId"));
					if (!StringUtil.isEmpty(roomId)) {
						startAndEnd = StringUtil.getString(map.get("startAndEnd"));
						startAndEnd = startAndEnd.substring(1);
						seStr = startAndEnd.split("#");
						j = 0;
						for (int length = seStr.length; j < length; j++) {
							seTime = seStr[j].split(",");
							String st = seTime[0].substring(0, 5);
							String et = seTime[1].substring(0, 5);
							// 这里除去已经预定的会议室时间
							List<String> delTimeId = new ArrayList<String>();

							for (int k = 0; k < newTime.size(); k++) {
								if (newTime.get(k).equals(st)) {
									delTimeId.add(newTime.get(k));
									for (int k2 = k + 1; k2 < newTime.size(); k2++) {
										if (newTime.get(k2).compareTo(et) < 0) {
											delTimeId.add(newTime.get(k2));
										}
									}
								}
							}
							newTime.removeAll(delTimeId);
						}
						// 计算当前时间

						/*
						 * for (String time : newTime) { if
						 * (currentTimes.compareTo(time) <= 0) {
						 * defaultTimes.add(time); ztime += 30; } }
						 */
						//
					}
					i = 0;
					size = newTime.size();
					// j = 0;
					if (StringUtil.isNotEmpty(currentTimes)) {
						for (; i < size; i++) {
							time = newTime.get(i);
							if (time.compareTo(currentTimes) >= 0) {
								// j = i;
								break;
							}
						}
					}
					for (; i < size; i++) {
						ztime += 30;
					}
					map.put("totalTimeEquation", (ztime / 60));
				}
			}
			return meetingRoomVoList;
		} else {
			throw new VyiyunException("传入的日期格式有问题或为空");
		}
	}

	/**
	 * @Description: 获取数据，当天的空闲的会议室数据
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getAvailableMeetingRoomsOfTheDay", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getAvailableMeetingRoomsOfTheDay(HttpServletRequest request) {
		String pageIndexStr = HttpRequestUtil.getInst().getString("pageIndex");
		int pageIndex = 1;
		if (StringUtil.isNotEmpty(pageIndexStr)) {
			pageIndex = Integer.parseInt(pageIndexStr);
		}
		String dateStr = HttpRequestUtil.getInst().getString("date");
		String capacity = HttpRequestUtil.getInst().getString("capacity");
		if (StringUtil.isNotEmpty(capacity)) {
			if (!capacity.matches("^[1-9]\\d*$")) {
				throw new VyiyunBusinessException("容纳人数必须为纯数字");
			}
		}
		String roomName = HttpRequestUtil.getInst().getString("roomName");
		String order = HttpRequestUtil.getInst().getString("order");
		Date date = DateUtil.stringToDate(dateStr, "yyyy-MM-dd");
		if (date != null) {
			List<MeetingRoomVo> availableMeetingRoomVoList = meetingRoomService.queryAvailableMeetingRooms(date,
					capacity, roomName, order, pageIndex);
			return availableMeetingRoomVoList;
		} else {
			throw new VyiyunException("传入的日期格式有问题或为空");
		}
	}

	/**
	 * @Description: 会议室预定添加页面
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "meeting_add", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView meeting_add(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelView = createModelAndView("meeting_add");
		String dateStr = HttpRequestUtil.getInst().getString("date");
		String meetingRoomId = HttpRequestUtil.getInst().getString("meetingRoomId");
		String flag = HttpRequestUtil.getInst().getString("flag");
		if (StringUtil.isNotEmpty(flag)) {
			modelView.addObject("flag", flag);
		}
		MeetingRoom meetingRoom = meetingRoomService.queryMeetingRoomById(meetingRoomId);
		modelView.addObject("meetingRoom", meetingRoom);
		Date date = DateUtil.stringToDate(dateStr, "yyyy-MM-dd");
		if (date != null) {
			StringBuilder builder = new StringBuilder();
			builder.append(dateStr).append(" ").append(DateUtil.getDayOfWeek(date));
			modelView.addObject("dateBuilder", builder.toString());
		} else {
			throw new VyiyunException("传入的日期格式有问题或为空");
		}
		return modelView;
	}

	/**
	 * @Description: 会议室预定添加页面更改日期后获取数据
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "meeting_add_updateDate", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object meeting_add_updateDate(HttpServletRequest request, HttpServletResponse response) {
		String dateStr = HttpRequestUtil.getInst().getString("date");
		String meetingRoomId = HttpRequestUtil.getInst().getString("meetingRoomId");
		String flag = HttpRequestUtil.getInst().getString("flag");
		Date date = DateUtil.stringToDate(dateStr, "yyyy-MM-dd");
		Map<String, Object> mapResult = new HashMap<String, Object>();
		if (date != null) {
			List<Map<String, Object>> startAndEndList = meetingService.queryStartAndEndWithRepeat(meetingRoomId, date);
			mapResult.put("startAndEndList", startAndEndList);
		} else {
			throw new VyiyunException("传入的日期格式有问题或为空");
		}
		if (flag != null) {
			mapResult.put("flag", flag);
		}
		// ******返回总得开始和结束时间
		List<String> totalStartAndEndTime = meetingRoomService.getTotalStartAndEndTime();
		mapResult.put("totalStartAndEndTime", totalStartAndEndTime);
		return mapResult;
	}

	/**
	 * @Description: 点击筛选按钮
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "filterRoomNameAndCapacity", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object filterRoomNameAndCapacity(HttpServletRequest request, HttpServletResponse response) {
		List<MeetingRoom> meetingRoomList = meetingRoomService.queryAllMeetingRooms();
		return meetingRoomList;
	}

	/**
	 * @Description: 会议室预定成功的插入数据
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "meetingBookSuccess_insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object meetingBookSuccess_insert(HttpServletRequest request, HttpServletResponse response) {
		ResponseResult responseResult = new ResponseResult();
		try {
			String meetingId = null;
			// ******获取会议起始结束时间的json数组
			String startAndEndTimeStr = HttpRequestUtil.getInst().getString("startAndEndTimeJsonObj");
			JSONObject startAndEndTime = JSON.parseObject(startAndEndTimeStr);
			// ******获取参会人员json数组
			String attendMeetingUsersStr = HttpRequestUtil.getInst().getString("attendMeetingUsersJsonArray");
			JSONObject attendMeetingUsersJosonObj = JSON.parseObject(attendMeetingUsersStr);
			JSONArray attendMeetingUsersJsonArray = attendMeetingUsersJosonObj.getJSONArray("users");
			// ******添加参会人员列表
			List<EntityAccount> eaList = new ArrayList<EntityAccount>();
			if (!CollectionUtils.isEmpty(attendMeetingUsersJsonArray)) {
				for (Object attendMeetingUser : attendMeetingUsersJsonArray) {
					JSONObject user = (JSONObject) attendMeetingUser;
					EntityAccount entityAccount = new EntityAccount("", "4", user.getString("userId"), "2", "1", "0",
							new Date(), "", user.getString("avatar"));
					// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
					entityAccount.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
					// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
					entityAccount.setAccountName(user.getString("userName"));
					entityAccount.setUpdateTime(new Date());
					eaList.add(entityAccount);
				}
			}
			// ******添加会议记录，参会人员记录；返回当前添加的会议的id；返回时长；返回日期；返回会议主题；返回房间名称；返回地址；返回会议状态；当前处理状态
			if (startAndEndTime != null) {
				String roomId = HttpRequestUtil.getInst().getString("roomId");
				String roomName = HttpRequestUtil.getInst().getString("roomName");
				String dateStr = HttpRequestUtil.getInst().getString("date");
				String theme = HttpRequestUtil.getInst().getString("theme");
				String repeat = HttpRequestUtil.getInst().getString("repeat");
				String status = HttpRequestUtil.getInst().getString("status");
				Date date = DateUtil.stringToDate(dateStr, "yyyy-MM-dd");
				String startStr = startAndEndTime.getString("start");
				String endStr = startAndEndTime.getString("end");
				Date start = DateUtil.stringToDate(startStr, "HH:mm:ss");
				Date end = DateUtil.stringToDate(endStr, "HH:mm:ss");
				if (date != null && start != null && end != null) {
					// ******添加会议记录插入数据库
					meetingId = meetingService.addMeetingWithEntityAccountService(roomId, roomName, date, start, end,
							theme, repeat, status, eaList);
				} else {
					throw new VyiyunException("传入的日期格式有误");
				}
			}
			responseResult.setValue(meetingId);
		} catch (Exception e) {
			responseResult.setStatus(-1);
			e.printStackTrace();
		}
		return responseResult;
	}

	/**
	 * @Description: 会议室预定成功的详情页面
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "meetingBookSuccess_detail", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView meetingBookSuccess_detail(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelView = createModelAndView("meeting_detail");
		// ******获取并返回会议id
		String meetingId = HttpRequestUtil.getInst().getString("meetingId");
		modelView.addObject("meetingId", meetingId);
		// ******根据id得到会议对象
		Meeting meeting = meetingService.queryMeetingById(meetingId);
		if (meeting != null) {
			// ******返回会议主题
			String theme = meeting.getTheme();
			modelView.addObject("theme", theme);
			// ******返回会议名称
			String roomName = meeting.getRoomName();
			modelView.addObject("roomName", roomName);
			// ******返回日期和开始结束时间
			Date date = meeting.getDate();
			Date start = meeting.getStart();
			Date end = meeting.getEnd();
			String dateStr = DateUtil.dateToString(date, "yyyy-MM-dd");
			String startStr = DateUtil.dateToString(start, "HH:mm:ss");
			String endStr = DateUtil.dateToString(end, "HH:mm:ss");
			modelView.addObject("date", dateStr);
			StringBuilder startAndEnd = new StringBuilder();
			startAndEnd.append(startStr).append("~").append(endStr);
			modelView.addObject("startAndEnd", startAndEnd.toString());
			// ******返回时长
			BigDecimal timeLength = meetingService.getTimeLength(start, end);
			String timeLengthStr = timeLength.toString();
			modelView.addObject("timeLengthStr", timeLengthStr);
			// ******返回地点
			String roomId = meeting.getRoomId();
			MeetingRoom meetingRoom = meetingRoomService.queryMeetingRoomById(roomId);
			String adress = meetingRoom.getAddress();
			modelView.addObject("adress", adress);
		}
		// ******返回会议状态
		int meetingStatus = meetingService.getMeetingStatus(meetingId);
		modelView.addObject("meetingStatusDigit", meetingStatus);
		if (1 == meetingStatus) {
			modelView.addObject("meetingStatus", "未开始");
		} else if (2 == meetingStatus) {
			modelView.addObject("meetingStatus", "进行中");
		} else if (3 == meetingStatus) {
			modelView.addObject("meetingStatus", "已结束");
		}
		// ******返回参会人员列表
		EntityAccount entityAccount = new EntityAccount();
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
		entityAccount.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
		entityAccount.setEntityId(meetingId);
		List<EntityAccount> eaList = entityAccountService.getEntityAccount(entityAccount);
		if (!CollectionUtils.isEmpty(eaList)) {
			modelView.addObject("eaListSize", eaList.size());
			modelView.addObject("eaList", eaList);
		}
		// ******返回当前用户类型，用于控制取消会议按钮是否显示 0:发起人 1:参会人
		String currentUserId = HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid();
		String orgnizedUserId = meetingService.queryMeetingById(meetingId).getUserId();
		if (currentUserId.equalsIgnoreCase(orgnizedUserId)) {
			modelView.addObject("currentUserType", "0");
		} else {
			modelView.addObject("currentUserType", "1");
		}
		// ******返回当前用户的处理状态
		EntityAccount entityAccount2 = new EntityAccount();
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
		entityAccount2.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
		entityAccount2.setEntityId(meetingId);
		entityAccount2.setAccountId(currentUserId);
		List<EntityAccount> list = entityAccountService.getEntityAccount(entityAccount2);
		if (!CollectionUtils.isEmpty(list)) {
			String dealResult = list.get(0).getDealResult();
			modelView.addObject("dealResult", dealResult);
		} else {
			modelView.addObject("dealResult", -1);
		}
		return modelView;
	}

	/**
	 * @Description: 点击确认参加按钮
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "ensureAttendMeeting", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object ensureAttendMeeting(HttpServletRequest request, HttpServletResponse response) {
		String meetingId = HttpRequestUtil.getInst().getString("meetingId");
		String currentUserId = HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid();
		meetingService.ensureAttendMeeting(meetingId, currentUserId, true);
		return 1;
	}

	/**
	 * @Description: 点击取消会议按钮 == 一个
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "deleteMeeting", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object deleteMeeting(HttpServletRequest request, HttpServletResponse response) {
		String currentUserId = HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid();
		String meetingId = HttpRequestUtil.getInst().getString("meetingId");
		EntityAccount entityAccount = new EntityAccount();
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
		entityAccount.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
		entityAccount.setEntityId(meetingId);
		List<EntityAccount> eaList = entityAccountService.getEntityAccount(entityAccount);
		meetingService.deleteMeeting(meetingId, currentUserId, eaList);
		return 1;
	}

	/**
	 * @Description: 点击取消会议按钮 == 多个
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "deleteMeetingList", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object deleteMeetingList(HttpServletRequest request, HttpServletResponse response) {
		String currentUserId = HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid();
		String meetingIdsList = HttpRequestUtil.getInst().getString("meetingIdsList");
		JSONObject meetingIdsJsonObj = JSON.parseObject(meetingIdsList);
		JSONArray meetingIdsJsonArray = meetingIdsJsonObj.getJSONArray("meetingIds");
		if (!CollectionUtils.isEmpty(meetingIdsJsonArray)) {
			for (Object meetingIdObj : meetingIdsJsonArray) {
				String meetingId = String.valueOf(meetingIdObj);
				EntityAccount entityAccount = new EntityAccount();
				// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
				entityAccount.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
				// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
				entityAccount.setEntityId(meetingId);
				List<EntityAccount> eaList = entityAccountService.getEntityAccount(entityAccount);
				meetingService.deleteMeeting(meetingId, currentUserId, eaList);
			}
		}
		return 1;
	}

	/**
	 * @Description: 我的会议待参加页面
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "myMeetingWait", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView myMeetingWait(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelView = createModelAndView("meeting_record");
		return modelView;
	}

	/**
	 * @Description: 获取我的会议的待参加的数据
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getMyMeetingWaitData", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getMyMeetingWaitData(HttpServletRequest request, HttpServletResponse response) {

		ResponseResult responseResult = new ResponseResult();
		try {
			String pageIndexStr = HttpRequestUtil.getInst().getString("pageIndex");
			int pageIndex = 1;
			if (StringUtil.isNotEmpty(pageIndexStr)) {
				pageIndex = Integer.parseInt(pageIndexStr);
			}
			String currentUserId = HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("currentUserId", currentUserId);
			params.put("currentDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			params.put("currentTime", new SimpleDateFormat("HH:mm:ss").format(new Date()));
			SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
			sqlQueryParameter.getKeyValMap().putAll(params);
			sqlQueryParameter.setPage(true);
			sqlQueryParameter.setPageIndex(pageIndex);
			// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
			Meeting meeting = new Meeting();
			meeting.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			sqlQueryParameter.setParameter(meeting);
			// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
			List<Meeting> meetingList = meetingService.queryMeetingsWaitByParameter(sqlQueryParameter);
			responseResult.setValue(meetingList);
		} catch (Exception e) {
			responseResult.setStatus(-1);

		}
		return responseResult;
	}

	/**
	 * @Description: 获取我的会议的已结束的数据
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getMyMeetingOverData", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getMyMeetingOverData(HttpServletRequest request, HttpServletResponse response) {
		ResponseResult responseResult = new ResponseResult();
		try {
			String pageIndexStr = HttpRequestUtil.getInst().getString("pageIndex");
			int pageIndex = 1;
			if (StringUtil.isNotEmpty(pageIndexStr)) {
				pageIndex = Integer.parseInt(pageIndexStr);
			}
			String currentUserId = HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("currentUserId", currentUserId);
			params.put("currentDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			params.put("currentTime", new SimpleDateFormat("HH:mm:ss").format(new Date()));
			SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
			sqlQueryParameter.getKeyValMap().putAll(params);
			sqlQueryParameter.setPage(true);
			sqlQueryParameter.setPageIndex(pageIndex);
			// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
			Meeting meeting = new Meeting();
			meeting.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			sqlQueryParameter.setParameter(meeting);
			// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
			List<Meeting> meetingList = meetingService.queryMeetingsOverByParameter(sqlQueryParameter);
			responseResult.setValue(meetingList);
		} catch (Exception e) {
			responseResult.setStatus(-1);
		}

		return responseResult;
	}

	/**
	 * @Description: 我的会议的详情页面
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "myMeetingDetail", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView myMeetingDetail(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelView = createModelAndView("myMeetingDetail");
		String meetingId = HttpRequestUtil.getInst().getString("meetingId");
		modelView.addObject("meetingId", meetingId);
		// ******返回会议主题；返回房间名称；返回日期；返回会议起始结束时间
		Meeting meeting = meetingService.queryMeetingById(meetingId);
		if (meeting != null) {
			// ******返回会议主题
			String theme = meeting.getTheme();
			modelView.addObject("theme", theme);
			// ******返回会议名称
			String roomName = meeting.getRoomName();
			modelView.addObject("roomName", roomName);
			// ******返回日期和开始结束时间
			Date date = meeting.getDate();
			Date start = meeting.getStart();
			Date end = meeting.getEnd();
			String dateStr = DateUtil.dateToString(date, "yyyy-MM-dd");
			String startStr = DateUtil.dateToString(start, "HH:mm:ss");
			String endStr = DateUtil.dateToString(end, "HH:mm:ss");
			modelView.addObject("date", dateStr);
			StringBuilder startAndEnd = new StringBuilder();
			startAndEnd.append(startStr).append("~").append(endStr);
			modelView.addObject("startAndEnd", startAndEnd.toString());
			// ******返回时长
			BigDecimal timeLength = meetingService.getTimeLength(start, end);
			String timeLengthStr = timeLength.toString();
			modelView.addObject("timeLengthStr", timeLengthStr);
			// ******返回地点
			String roomId = meeting.getRoomId();
			MeetingRoom meetingRoom = meetingRoomService.queryMeetingRoomById(roomId);
			String adress = meetingRoom.getAddress();
			modelView.addObject("adress", adress);
		} else {
			throw new VyiyunBusinessException("会议室已被取消");
		}
		// ******返回会议状态；
		int meetingStatus = meetingService.getMeetingStatus(meetingId);
		modelView.addObject("meetingStatusDigit", meetingStatus);
		if (1 == meetingStatus) {
			modelView.addObject("meetingStatus", "未开始");
		} else if (2 == meetingStatus) {
			modelView.addObject("meetingStatus", "进行中");
		} else if (3 == meetingStatus) {
			modelView.addObject("meetingStatus", "已结束");
		}
		// ******返回参会人员列表
		EntityAccount entityAccount = new EntityAccount();
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
		entityAccount.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
		entityAccount.setEntityId(meetingId);
		List<EntityAccount> eaList = entityAccountService.getEntityAccount(entityAccount);
		if (!CollectionUtils.isEmpty(eaList)) {
			modelView.addObject("eaListSize", eaList.size());
			modelView.addObject("eaList", eaList);
		}
		// ******返回当前用户类型，用于控制取消会议按钮是否显示 0:发起人 1:参会人
		String currentUserId = HttpRequestUtil.getInst().getCurrentWeixinUser().getUserid();
		String orgnizedUserId = meetingService.queryMeetingById(meetingId).getUserId();
		if (currentUserId.equalsIgnoreCase(orgnizedUserId)) {
			modelView.addObject("currentUserType", "0");
		} else {
			modelView.addObject("currentUserType", "1");
		}
		// ******返回当前用户的处理状态
		EntityAccount entityAccount2 = new EntityAccount();
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
		entityAccount2.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
		entityAccount2.setEntityId(meetingId);
		entityAccount2.setAccountId(currentUserId);
		List<EntityAccount> list = entityAccountService.getEntityAccount(entityAccount2);
		if (!CollectionUtils.isEmpty(list)) {
			String dealResult = list.get(0).getDealResult();
			modelView.addObject("dealResult", dealResult);
		} else {
			modelView.addObject("dealResult", -1);
		}
		return modelView;
	}

	@Override
	protected String getPrefix() {
		return super.getPrefix() + "mobile/meeting/";
	}
}
