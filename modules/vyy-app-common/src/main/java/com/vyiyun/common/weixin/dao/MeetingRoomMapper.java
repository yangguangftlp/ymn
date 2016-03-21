package com.vyiyun.common.weixin.dao;

import java.util.List;
import java.util.Map;

import com.vyiyun.common.weixin.entity.MeetingRoom;
import com.vyiyun.common.weixin.entity.MeetingRoomVo;
import com.vyiyun.weixin.model.SqlQueryParameter;

/**
 * @ClassName: IMeetingRoomDao
 * @Description: 会议室设置dao
 * @author XiaoWei
 * @date Dec 22, 2015 11:01:40 AM
 */
public interface MeetingRoomMapper {

	/**
	 * @Description: 添加会议室
	 * @param meetingRoom
	 */
	public int addMeetingRoom(MeetingRoom meetingRoom);

	/**
	 * @Description: 根据id获取某个会议室信息
	 * @param id
	 * @return
	 */
	public MeetingRoom queryMeetingRoomById(String id);

	/**
	 * @Description: 获取所有会议室信息
	 * @return
	 */
	public List<MeetingRoom> queryAllMeetingRooms(SqlQueryParameter sqlQueryParameter);

	/**
	 * @Description: 根据条件获取当天可预订的空闲会议室
	 * @param params
	 * @return
	 */
	public List<MeetingRoomVo> queryAvailableMeetingRooms(SqlQueryParameter sqlQueryParameter);

	/**
	 * @Description: 根据条件获取当天的所有会议室
	 * @param params
	 * @return
	 */
	public List<MeetingRoomVo> queryAllMeetingRoomsOfTheDay(SqlQueryParameter sqlQueryParameter);

	/**
	 * @Description: 获取满足指定条件的会议室记录
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<MeetingRoom> queryMeetingRoomsByParameter(SqlQueryParameter sqlQueryParameter);

	/**
	 * @Description: 获取指定会议室的当天非空闲时间
	 * @param sqlQueryParameter
	 * @return
	 */
	public long queryMeetingRoomFreeTime(SqlQueryParameter sqlQueryParameter);

	/**
	 * @Description: 根据id删除会议室
	 * @param id
	 */
	public void deleteMeetingRoomById(String id);

	/**
	 * @Description: 批量删除会议室
	 * @param ids
	 */
	public void deleteMeetingRoomsByIds(List<String> ids);

	/**
	 * @Description: 更新会议室信息
	 * @param meetingRoom
	 */
	public void updateMeetingRoom(MeetingRoom meetingRoom);

	/**
	 * @Description: 选出所有的会议室总数
	 * @param params
	 * @return
	 */
	public int queryALLMeetingRoomsCount();

	/**
	 * @Description: 选出当天空闲的会议室总数
	 * @param params
	 * @return
	 */
	public int queryAvailableMeetingRoomsCount(SqlQueryParameter sqlQueryParameter);

	/**
	 * @Description: 选出当天所有的会议室总数
	 * @param params
	 * @return
	 */
	public int queryAllMeetingRoomsOfTheDayCount(SqlQueryParameter sqlQueryParameter);

	/**
	 * @Description: 选出符合特定条件的会议室总数
	 * @param sqlQueryParameter
	 * @return
	 */
	public int queryMeetingRoomsByParameterCount(SqlQueryParameter sqlQueryParameter);

	/**
	 * 获取空闲时间
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<Map<String, Object>> getAvailableMeetingRooms(SqlQueryParameter sqlQueryParameter);

}
