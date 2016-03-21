package com.vyiyun.common.weixin.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.vyiyun.common.weixin.entity.Meeting;
import com.vyiyun.weixin.entity.EntityAccount;
import com.vyiyun.weixin.model.SqlQueryParameter;

public interface IMeetingService {

	// -------业务具体处理----------

	// -------业务基础处理----------

	/**
	 * @Description: TODO
	 * @param meetingId
	 * @param eaList
	 */
	public void deleteMeeting(String meetingId, String currentUserId, List<EntityAccount> eaList);

	/**
	 * @Description: TODO
	 * @param id
	 * @return
	 */
	public int getMeetingStatus(String id);

	/**
	 * @Description: TODO
	 * @param meetingId
	 * @param currentUserId
	 * @param isEnsure
	 */
	public void ensureAttendMeeting(String meetingId, String currentUserId, boolean isEnsure);

	/**
	 * @Description: TODO
	 * @param start
	 * @param end
	 * @param theme
	 * @param repeat
	 * @param status
	 * @param attendMeetingUserList
	 */
	public String addMeetingWithEntityAccountService(String roomId, String roomName, Date date, Date start, Date end,
			String theme, String repeat, String status, List<EntityAccount> eaList);

	/**
	 * @Description: TODO
	 * @param meeting
	 * @param eaList
	 */
	@Deprecated
	public void addMeetingWithEntityAccount(Meeting meeting, List<EntityAccount> eaList);

	// -------dao层的处理-------
	/**
	 * @提醒 用addMeetingWithEntityAccount代替
	 * @Description: TODO
	 * @param meeting
	 */
	@Deprecated
	public void addMeeting(Meeting meeting);

	/**
	 * @Description: TODO
	 * @param id
	 */
	public void deleteMeetingById(String id);

	/**
	 * @Description: TODO
	 * @param id
	 * @return
	 */
	public Meeting queryMeetingById(String id);

	/**
	 * @说明 暂时用不到
	 * @Description: TODO
	 * @return
	 */
	public List<Meeting> queryAllMeetings();

	/**
	 * @Description: TODO
	 * @param meeting
	 * @return
	 */
	public List<Meeting> queryMeetingsByParameter(Meeting meeting);
	/**
	 * @Description: TODO
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<Meeting> queryMeetingsWaitByParameter(SqlQueryParameter sqlQueryParameter);

	/**
	 * @Description: TODO
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<Meeting> queryMeetingsOverByParameter(SqlQueryParameter sqlQueryParameter);

	/**
	 * @Description: TODO
	 * @param meetingRoomId
	 * @param date
	 * @return
	 */
	public List<Map<String, Object>> queryStartAndEndWithRepeat(String meetingRoomId, Date date);

	/**
	 * @Description: TODO
	 * @param meeting
	 * @return
	 */
	@Deprecated
	public List<Map<String, Object>> queryStartAndEndWithRepeat(Meeting meeting);

	/**
	 * @Description: TODO
	 * @param meetingId
	 * @return
	 */
	public List<EntityAccount> queryMeetingPeople(String meetingId, String dealResult);
	
	/**
	 * @Description: TODO
	 * @param meeting
	 */
	public void updateMeetingByParameter(Meeting meeting);

	/**
	 * @Description: TODO
	 * @param start
	 * @param end
	 * @param corpId 
	 */
	public void updateRepeatValueWhenUpdateTotalTime(Date start, Date end, String corpId);

	/**
	 * @Description: TODO
	 * @param date
	 * @param start
	 * @param end
	 * @param corpId 
	 * @return
	 */
	public Integer queryMeetingCountWhenUpdateTotalTime(Date date, Date start, Date end, String corpId);

	/**
	 * @Description: TODO
	 * @param sqlQueryParameter
	 * @return
	 */
	public int queryMeetingsWaitCount(SqlQueryParameter sqlQueryParameter);

	/**
	 * @Description: TODO
	 * @param sqlQueryParameter
	 * @return
	 */
	public int queryMeetingsOverCount(SqlQueryParameter sqlQueryParameter);

	/**
	 * @Description: TODO
	 * @param start
	 * @param end
	 * @return
	 */
	public BigDecimal getTimeLength(Date start, Date end);

}
