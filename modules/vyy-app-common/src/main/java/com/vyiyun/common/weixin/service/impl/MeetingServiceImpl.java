/**
 * 
 */
package com.vyiyun.common.weixin.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.vyiyun.common.weixin.dao.MeetingMapper;
import com.vyiyun.common.weixin.entity.Meeting;
import com.vyiyun.common.weixin.service.IMeetingService;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.entity.EntityAccount;
import com.vyiyun.weixin.entity.SystemConfig;
import com.vyiyun.weixin.enums.WeixinMsgType;
import com.vyiyun.weixin.exception.VyiyunBusinessException;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.SqlQueryParameter;
import com.vyiyun.weixin.model.WeixinUser;
import com.vyiyun.weixin.model.wxmessage.WeixinMessageBase;
import com.vyiyun.weixin.msg.impl.AbstMsgExecutor;
import com.vyiyun.weixin.service.IEntityAccountService;
import com.vyiyun.weixin.service.impl.AbstractBaseService;
import com.vyiyun.weixin.utils.CommonUtil;
import com.vyiyun.weixin.utils.ConfigUtil;
import com.vyiyun.weixin.utils.DateUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;
import com.vyiyun.weixin.utils.WeixinMessageUtil;

/**
 * @ClassName: MeetingServiceImpl
 * @Description: TODO
 * @author XiaoWei
 * @date Dec 28, 2015 10:09:42 AM
 */
@Service("meetingService")
public class MeetingServiceImpl extends AbstractBaseService implements IMeetingService {

	private static final Logger LOGGER = Logger.getLogger(ExpenseServiceImpl.class);

	@Autowired
	private MeetingMapper meetingDao;
	@Autowired
	private IEntityAccountService entityAccountService;

	// -----------业务的最终处理，供controller调用--------

	// -----------业务的基础处理--------
	@Override
	public void deleteMeeting(String meetingId, String currentUserId, List<EntityAccount> eaList) {
		// entityId accountId eaList.size
		Meeting meeting = this.queryMeetingById(meetingId);
		if (currentUserId.equalsIgnoreCase(meeting.getUserId())) {
			this.sendMessageDeleteMeetingByManager(eaList, meeting);
			entityAccountService.deleteByEntityId(meetingId);
			meetingDao.deleteMeetingById(meetingId);
		} else {
			throw new VyiyunException("只有会议发起人才有权限取消会议");
		}
	}

	@Override
	// 确认参加或拒绝会议
	public void ensureAttendMeeting(String meetingId, String currentUserId, boolean isEnsure) {
		EntityAccount entityAccount = new EntityAccount();
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
		entityAccount.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
		entityAccount.setEntityId(meetingId);
		entityAccount.setAccountId(currentUserId);
		Meeting meeting = this.queryMeetingById(meetingId);
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
		meeting.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
		if (isEnsure) {
			entityAccount.setDealResult("1");
			entityAccount.setRemark("确认参加");
			// 获取是否要反馈消息，要则发送确认参加的消息
			if ("1".equals(meeting.getStatus())) {
				this.sendMessageByAttendMeetingPeople(meeting, true);
			}
		} else {
			entityAccount.setDealResult("2");
			entityAccount.setRemark("拒绝参加");
			// 获取是否要反馈消息，要则发送拒绝参加的消息
			if ("1".equals(meeting.getStatus())) {
				this.sendMessageByAttendMeetingPeople(meeting, false);
			}
		}
		entityAccountService.updateEntityAccount(entityAccount);
	}

	@Override
	// 1=待开会 2=正开会 3=开完会
	public int getMeetingStatus(String id) {
		// ---------当前时间--------
		Date current = new Date();
		String[] currentDateStrArray = this.getDateStrArray(current);
		String[] currentTimeStrArray = this.getTimeStrArray(current);
		int currentYear = Integer.parseInt(currentDateStrArray[0]);
		int currentMonth = Integer.parseInt(currentDateStrArray[1]);
		int currentDay = Integer.parseInt(currentDateStrArray[2]);
		int currentHour = Integer.parseInt(currentTimeStrArray[0]);
		int currentMin = Integer.parseInt(currentTimeStrArray[1]);
		// ---------会议记录的时间---------
		Meeting meeting = this.queryMeetingById(id);
		Date date = meeting.getDate();
		Date start = meeting.getStart();
		Date end = meeting.getEnd();
		String[] meetingDateStrArray = this.getDateStrArray(date);
		String[] startTimeStrArray = this.getTimeStrArray(start);
		String[] endTimeStrArray = this.getTimeStrArray(end);
		int meetingYear = Integer.parseInt(meetingDateStrArray[0]);
		int meetingMonth = Integer.parseInt(meetingDateStrArray[1]);
		int meetingDay = Integer.parseInt(meetingDateStrArray[2]);
		int startHour = Integer.parseInt(startTimeStrArray[0]);
		int startMin = Integer.parseInt(startTimeStrArray[1]);
		int endHour = Integer.parseInt(endTimeStrArray[0]);
		int endMin = Integer.parseInt(endTimeStrArray[1]);
		// ---------比较会议时间和当前时间的大小---------
		if (currentYear > meetingYear) {
			return 3;
		} else if (currentYear < meetingYear) {
			return 1;
		} else {
			if (currentMonth > meetingMonth) {
				return 3;
			} else if (currentMonth < meetingMonth) {
				return 1;
			} else {
				if (currentDay > meetingDay) {
					return 3;
				} else if (currentDay < meetingDay) {
					return 1;
				} else {
					if (currentHour < startHour) {
						return 1;
					} else if (currentHour > endHour) {
						return 3;
					} else if (currentHour == startHour) {
						if (currentMin < startMin) {
							return 1;
						} else {
							return 2;
						}
					} else if (currentHour == endHour) {
						if (currentMin < endMin) {
							return 2;
						} else {
							return 3;
						}
					} else {
						return 2;
					}
				}
			}
		}
	}

	@Override
	public String addMeetingWithEntityAccountService(String roomId, String roomName, Date date, Date start, Date end,
			String theme, String repeat, String status, List<EntityAccount> eaList) {
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
		Meeting meeting = null;
		String meetingId = null;
		if (StringUtil.isNotEmpty(roomId) && StringUtil.isNotEmpty(roomName) && date != null && start != null
				&& end != null && StringUtil.isNotEmpty(theme) && ("0".equals(repeat) || "1".equals(repeat))
				&& ("0".equals(status) || "1".equals(status))) {
			meetingId = CommonUtil.GeneGUID();
			// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
			meeting = new Meeting(meetingId, corpId, theme, HttpRequestUtil.getInst().getCurrentWeixinUser()
					.getUserid(), HttpRequestUtil.getInst().getCurrentWeixinUser().getName(), roomId, roomName, date,
					start, end, repeat, status);
			// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
			this.addMeeting(meeting);
		} else {
			throw new VyiyunBusinessException("主题不能为空");
		}
		if (!CollectionUtils.isEmpty(eaList)) {
			for (EntityAccount ea : eaList) {
				ea.setId(CommonUtil.GeneGUID());
				ea.setEntityId(meetingId);
				entityAccountService.addEntityAccount(ea);
			}
			// entityAccountService.addEntityAccount((EntityAccount[])
			// eaList.toArray());
			// 发消息给各位参会人
			this.sendMessageByManager(eaList, meeting);
		}
		return meetingId;
	}

	@Override
	public void addMeetingWithEntityAccount(Meeting meeting, List<EntityAccount> eaList) {
		if (!CollectionUtils.isEmpty(eaList)) {
			this.addMeeting(meeting);
			entityAccountService.addEntityAccount((EntityAccount[]) eaList.toArray());
			// 发消息给各位参会人
			this.sendMessageByManager(eaList, meeting);
		}
	}

	// -----------dao层的处理---------

	/**
	 * @说明 用addMeetingWithEntityAccount代替
	 */
	@Override
	public void addMeeting(Meeting meeting) {
		meetingDao.addMeeting(meeting);
	}

	@Override
	public void deleteMeetingById(String id) {
		meetingDao.deleteMeetingById(id);
	}

	@Override
	public Meeting queryMeetingById(String id) {
		return meetingDao.queryMeetingById(id);
	}

	// 暂时用不到
	@Override
	public List<Meeting> queryAllMeetings() {
		return null;
	}

	@Override
	public List<Meeting> queryMeetingsByParameter(Meeting meeting) {
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		sqlQueryParameter.setParameter(meeting);
		List<Meeting> parameterList = meetingDao.queryMeetingsByParameter(sqlQueryParameter);
		return parameterList;
	}

	@Override
	public List<Meeting> queryMeetingsWaitByParameter(SqlQueryParameter sqlQueryParameter) {
		return meetingDao.queryMeetingsWaitByParameter(sqlQueryParameter);
	}

	@Override
	public List<Meeting> queryMeetingsOverByParameter(SqlQueryParameter sqlQueryParameter) {
		return meetingDao.queryMeetingsOverByParameter(sqlQueryParameter);
	}

	@Override
	public List<Map<String, Object>> queryStartAndEndWithRepeat(String meetingRoomId, Date date) {
		Meeting meeting = new Meeting();
		meeting.setRoomId(meetingRoomId);
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
		meeting.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId()); // 企业ID
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
		meeting.setDate(date);
		return meetingDao.queryStartAndEndWithRepeat(meeting);
	}

	@Override
	public List<Map<String, Object>> queryStartAndEndWithRepeat(Meeting meeting) {
		return meetingDao.queryStartAndEndWithRepeat(meeting);
	}

	@Override
	public List<EntityAccount> queryMeetingPeople(String meetingId, String dealResult) {
		if ("0".equals(dealResult) || "1".equals(dealResult)) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("meetingId", meetingId);
			params.put("dealResult", dealResult);
			return meetingDao.queryMeetingPeople(params);
		} else {
			throw new VyiyunException("传入的dealResult格式有误");
		}

	}

	@Override
	public void updateMeetingByParameter(Meeting meeting) {
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		sqlQueryParameter.setParameter(meeting);
		meetingDao.updateMeetingByParameter(sqlQueryParameter);
	}

	@Override
	public void updateRepeatValueWhenUpdateTotalTime(Date start, Date end, String corpId) {
		Meeting meeting = new Meeting();
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
		meeting.setCorpId(corpId);
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
		meeting.setStart(start);
		meeting.setEnd(end);
		meetingDao.updateRepeatValueWhenUpdateTotalTime(meeting);
	}

	@Override
	public Integer queryMeetingCountWhenUpdateTotalTime(Date date, Date start, Date end, String corpId) {
		Meeting meeting = new Meeting();
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
		meeting.setCorpId(corpId);
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
		meeting.setDate(date);
		meeting.setStart(start);
		meeting.setEnd(end);
		return meetingDao.queryMeetingCountWhenUpdateTotalTime(meeting);
	}

	@Override
	public int queryMeetingsWaitCount(SqlQueryParameter sqlQueryParameter) {
		return meetingDao.queryMeetingsWaitCount(sqlQueryParameter);
	}

	@Override
	public int queryMeetingsOverCount(SqlQueryParameter sqlQueryParameter) {
		return meetingDao.queryMeetingsOverCount(sqlQueryParameter);
	}

	// -------该类中的基础方法-------

	public String[] getDateStrArray(Date date) {
		String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
		String[] dateStrArray = dateStr.split("-");
		return dateStrArray;
	}

	public String[] getTimeStrArray(Date date) {
		String timeStr = new SimpleDateFormat("HH:mm:ss").format(date);
		String[] timeStrArray = timeStr.split(":");
		return timeStrArray;
	}

	@Override
	public BigDecimal getTimeLength(Date start, Date end) {
		if (start != null && end != null) {
			String[] startTimeStrArray = this.getTimeStrArray(start);
			String[] endTimeStrArray = this.getTimeStrArray(end);
			int startHour = Integer.parseInt(startTimeStrArray[0]);
			int startMin = Integer.parseInt(startTimeStrArray[1]);
			int endHour = Integer.parseInt(endTimeStrArray[0]);
			int endMin = Integer.parseInt(endTimeStrArray[1]);
			int hours = endHour - startHour;
			int mins = endMin - startMin;
			BigDecimal bigDecimal_hours = new BigDecimal(String.valueOf(hours));
			BigDecimal bigDecimal_mins = new BigDecimal(String.valueOf(mins));
			BigDecimal bigDecimal_minsTohours = bigDecimal_mins.divide(new BigDecimal("60"), 1, BigDecimal.ROUND_FLOOR);
			BigDecimal hoursLength = bigDecimal_hours.add(bigDecimal_minsTohours);
			return hoursLength;
		} else {
			throw new VyiyunException("开始结束的时间不能为空");
		}
	}

	/**
	 * @Description: 会议发起人给所有参会人发送参加会议的消息
	 * @param meeting
	 * @param entityAccountList
	 */
	public void sendMessageByManager(List<EntityAccount> entityAccountList, Meeting meeting) {
		Map<String, Object> msgParams = new HashMap<String, Object>();
		msgParams.put("entityAccountList", entityAccountList);
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
		msgParams.put("corpId", HttpRequestUtil.getInst().getCurrentCorpId());
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("userName", meeting.getUserName());
		dataMap.put(
				"date",
				DateUtil.dateToString(meeting.getDate(), "yyyy-MM-dd") + " "
						+ DateUtil.dateToString(meeting.getStart(), "HH:mm"));
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		dataList.add(msgParams);
		dataList.add(dataMap);

		SystemCacheUtil.getInstance().add(new AbstMsgExecutor(dataList) {

			private static final long serialVersionUID = 4396408678168890032L;

			@Override
			public String getName() {
				return "发送通知大家参加会议的信息...";
			}

			@SuppressWarnings("unchecked")
			@Override
			public void execute() throws Exception {
				List<Map<String, Object>> dataListMsg = (List<Map<String, Object>>) this.getObj();
				Map<String, Object> msgParams = dataListMsg.get(0);
				Map<String, Object> dataMap = dataListMsg.get(1);
				List<EntityAccount> entityAccountList = (List<EntityAccount>) msgParams.get("entityAccountList");
				// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
				String _corpId = msgParams.get("corpId").toString();
				// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
				// 需要考虑 用户数过大 微信一次最多1000 个用户
				StringBuffer attendMeetingUser = new StringBuffer();
				if (!CollectionUtils.isEmpty(entityAccountList)) {
					if (entityAccountList.size() > 1000) {
						throw new VyiyunBusinessException("参会人数过大，微信一次最多只能给1000个参会人发消息");
					}
					for (EntityAccount ea : entityAccountList) {
						attendMeetingUser.append("|");
						attendMeetingUser.append(ea.getAccountId());
					}
					attendMeetingUser.deleteCharAt(0);
				}
				String attendMeetingUserStr = attendMeetingUser.toString();
				SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance().getSystemConfigCache()
						.getSystemConfig("system","weburl");
				// ??? ===> 企业的corpId ===> 在weixin_app.properties文件中查找
				// ??? ===> app的id ===> 在weixin_app.properties文件中查找
				String meetingAppId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH, "meeting_agentid"));
				String entityId = entityAccountList.get(0).getEntityId();
				if (StringUtils.isNotEmpty(attendMeetingUserStr)) {
					// 暂时放下，以后在细看
					// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
					WeixinMessageBase contentBase = new WeixinMessageBase(attendMeetingUserStr, WeixinMsgType.text,
							meetingAppId, WeixinMessageUtil.generateLinkUrlMsg("template_meeting_message", dataMap,
									systemConfig.getValue(), new Object[] { _corpId, entityId }));
					// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
					String content = contentBase.toJson();
					sendMessage(content);
				}
			}
		});
	}

	/**
	 * @Description: 会议发起人给所有参会人发送取消会议的消息
	 * @param meeting
	 * @param entityAccountList
	 */
	public void sendMessageDeleteMeetingByManager(List<EntityAccount> entityAccountList, Meeting meeting) {
		Map<String, Object> msgParams = new HashMap<String, Object>();
		msgParams.put("entityAccountList", entityAccountList);
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
		msgParams.put("corpId", HttpRequestUtil.getInst().getCurrentCorpId());
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("userName", meeting.getUserName());
		dataMap.put(
				"date",
				DateUtil.dateToString(meeting.getDate(), "yyyy-MM-dd") + " "
						+ DateUtil.dateToString(meeting.getStart(), "HH:mm"));
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		dataList.add(msgParams);
		dataList.add(dataMap);

		SystemCacheUtil.getInstance().add(new AbstMsgExecutor(dataList) {

			private static final long serialVersionUID = -2717883324502916624L;

			@Override
			public String getName() {
				return "发送通知大取消会议的信息...";
			}

			@SuppressWarnings("unchecked")
			@Override
			public void execute() throws Exception {
				List<Map<String, Object>> dataListMsg = (List<Map<String, Object>>) this.getObj();
				Map<String, Object> msgParams = dataListMsg.get(0);
				Map<String, Object> dataMap = dataListMsg.get(1);
				List<EntityAccount> entityAccountList = (List<EntityAccount>) msgParams.get("entityAccountList");
				// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
				String _corpId = msgParams.get("corpId").toString();
				// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
				// 需要考虑 用户数过大 微信一次最多1000 个用户
				StringBuffer attendMeetingUser = new StringBuffer();
				if (!CollectionUtils.isEmpty(entityAccountList)) {
					if (entityAccountList.size() > 1000) {
						throw new VyiyunBusinessException("参会人数过大，微信一次最多只能给1000个参会人发消息");
					}
					for (EntityAccount ea : entityAccountList) {
						attendMeetingUser.append("|");
						attendMeetingUser.append(ea.getAccountId());
					}
					attendMeetingUser.deleteCharAt(0);
				}
				String attendMeetingUserStr = attendMeetingUser.toString();
				SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance().getSystemConfigCache()
						.getSystemConfig("system","weburl");
				// ??? ===> 企业的corpId ===> 在weixin_app.properties文件中查找
				String corpId = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "corpId");
				// ??? ===> app的id ===> 在weixin_app.properties文件中查找
				String meetingAppId = ConfigUtil.get(Constants.WEIXIN_APP_PATH, "meeting_agentid");
				String entityId = entityAccountList.get(0).getEntityId();
				if (StringUtils.isNotEmpty(attendMeetingUserStr)) {
					// 暂时放下，以后在细看
					// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
					WeixinMessageBase contentBase = new WeixinMessageBase(attendMeetingUserStr, WeixinMsgType.text,
							meetingAppId, WeixinMessageUtil.generateLinkUrlMsg("template_meeting_message_delete",
									dataMap, systemConfig.getValue(), new Object[] { _corpId, entityId }));
					// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
					String content = contentBase.toJson();
					sendMessage(content);
				}
			}
		});
	}

	/**
	 * @Description: 参会人发送消息给发起人
	 * @param meeting
	 * @param isAttendMeeting
	 */
	public void sendMessageByAttendMeetingPeople(Meeting meeting, boolean isAttendMeeting) {
		WeixinUser currentUser = HttpRequestUtil.getInst().getCurrentWeixinUser();
		Map<String, Object> msgParams = new HashMap<String, Object>();
		msgParams.put("userId", meeting.getUserId());
		msgParams.put("meeting", meeting);
		msgParams.put("isAttendMeeting", isAttendMeeting);
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("userName", meeting.getUserName());
		dataMap.put("currentUserName", currentUser.getName());
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		dataList.add(msgParams);
		dataList.add(dataMap);

		SystemCacheUtil.getInstance().add(new AbstMsgExecutor(dataList) {

			private static final long serialVersionUID = 4068928019636040498L;

			@Override
			public String getName() {
				return "发送通知给发起人确认是否参加会议...";
			}

			@SuppressWarnings("unchecked")
			@Override
			public void execute() throws Exception {
				String meetingMessageType = null;
				List<Map<String, Object>> dataListMsg = (List<Map<String, Object>>) this.getObj();
				Map<String, Object> msgParams = dataListMsg.get(0);
				Map<String, Object> dataMap = dataListMsg.get(1);
				String userId = StringUtil.getString(msgParams.get("userId"));
				Meeting meeting = (Meeting) msgParams.get("meeting");
				// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
				String _corpId = meeting.getCorpId();
				// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
				boolean isAttendMeeting = (Boolean) msgParams.get("isAttendMeeting");
				if (isAttendMeeting) {
					// 格式：取自system_config_table表 meetingMessageType <=格式=>
					// Type_Name
					meetingMessageType = "template_meeting_message_agree";
				} else {
					meetingMessageType = "template_meeting_message_refuse";
				}

				// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
				SystemConfig systemConfig = (SystemConfig) SystemCacheUtil.getInstance().getSystemConfigCache()
						.getSystemConfig("system","weburl");
				// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
				// ??? ===> 企业的corpId ===> 在weixin_app.properties文件中查找
				// ??? ===> app的id ===> 在weixin_app.properties文件中查找
				String meetingAppId = convertAppId(ConfigUtil.get(Constants.WEIXIN_APP_PATH, "meeting_agentid"));
				String entityId = meeting.getId();
				if (StringUtils.isNotEmpty(userId)) {
					// 暂时放下，以后在细看
					// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
					WeixinMessageBase contentBase = new WeixinMessageBase(userId, WeixinMsgType.text, meetingAppId,
							WeixinMessageUtil.generateLinkUrlMsg(meetingMessageType, dataMap, systemConfig.getValue(),
									new Object[] { _corpId, entityId }));
					// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
					String content = contentBase.toJson();
					sendMessage(content);
				}
			}
		});
	}

}
