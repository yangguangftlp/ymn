package com.vyiyun.common.weixin.dao;

import java.util.List;
import java.util.Map;

import com.vyiyun.common.weixin.entity.Meeting;
import com.vyiyun.weixin.entity.EntityAccount;
import com.vyiyun.weixin.model.SqlQueryParameter;

/**
 * @ClassName: IMeetingDao
 * @Description: 会议预定dao
 * @author XiaoWei
 * @date 2016年1月11日
 */
public interface MeetingMapper {

	/**
	 * @Description: 添加会议项
	 * @param meeting
	 */
	public int addMeeting(Meeting meeting);

	/**
	 * @Description: 删除某个会议项
	 * @param id
	 */
	public void deleteMeetingById(String id);

	/**
	 * @Description: 通过id获取某个会议项
	 * @param id
	 * @return
	 */
	public Meeting queryMeetingById(String id);
	
	/**
	 * @Description: 获取所有的会议项
	 * @return
	 */
	public List<Meeting> queryAllMeetings();
	
	/**
	 * @Description: TODO
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<Meeting> queryMeetingsByParameter(SqlQueryParameter sqlQueryParameter);

	/**
	 * @Description: 根据currentUserId date currentTime获取大于当前时间的会议项
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<Meeting> queryMeetingsWaitByParameter(SqlQueryParameter sqlQueryParameter);
	
	/**
	 * @Description: 根据currentUserId date currentTime获取小于当前时间的会议项
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<Meeting> queryMeetingsOverByParameter(SqlQueryParameter sqlQueryParameter);

	/**
	 * @Description: 根据roomId=? repeat=1 或者 roomId=? repeat=0 date=? 
	 * 				   返回 start end
	 * @param meeting
	 * @return
	 */
	public List<Map<String, Object>> queryStartAndEndWithRepeat(Meeting meeting);
	
	/**
	 * @Description: 显示某项会议的参会人员
	 * @param meetingId
	 * @return
	 */
	public List<EntityAccount> queryMeetingPeople (Map<String, Object> params);
	
	/**
	 * @Description: 更新
	 * @param sqlQueryParameter
	 * @return
	 */
	public int updateMeetingByParameter(SqlQueryParameter sqlQueryParameter);
	/**
	 * @Description: 将在会议室不对外开放时间段中的会议的repeat为1的改为repeat为0
	 * @param meeting
	 * @return
	 */
	public int updateRepeatValueWhenUpdateTotalTime(Meeting meeting);
	
	/**
	 * @Description: 判断在会议室不对外开放时间段中是否有会议存在
	 * @param meeting
	 * @return
	 */
	public Integer queryMeetingCountWhenUpdateTotalTime(Meeting meeting);
	/**
	 * @Description: 查询待参加的会议的记录数
	 * @param sqlQueryParameter
	 * @return
	 */
	public int queryMeetingsWaitCount(SqlQueryParameter sqlQueryParameter);
	
	/**
	 * @Description: 查询已结束的会议的记录数
	 * @param sqlQueryParameter
	 * @return
	 */
	public int queryMeetingsOverCount(SqlQueryParameter sqlQueryParameter);

}
