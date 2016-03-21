package com.vyiyun.common.weixin.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.vyiyun.common.weixin.entity.MeetingRoom;
import com.vyiyun.common.weixin.entity.MeetingRoomVo;
import com.vyiyun.weixin.model.DataResult;

public interface IMeetingRoomService {

	// -----------------------

	public List<String> getTotalStartAndEndTime();

	public void setTotalStartAndEndTime(String totalStartTimeStr, String totalEndTimeStr);

	// -----------------------

	/**
	 * @Description: 添加会议室
	 * @param meetingRoom
	 */
	public String addMeetingRoom(MeetingRoom meetingRoom);

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
	public List<MeetingRoom> queryAllMeetingRooms(String order);
	
	/**
	 * @Description: 获取所有会议室信息
	 * @return
	 */
	public List<MeetingRoom> queryAllMeetingRooms();

	/**
	 * @Description: 获取当天的可预订的空闲会议室
	 * @param params
	 * @return
	 */
	public List<MeetingRoomVo> queryAvailableMeetingRooms(Date date, String capacity, String roomName,
			String order, int pageIndex);

	/**
	 * @Description: 获取当天的所有的会议室
	 * @param params
	 * @return
	 */
	public List<MeetingRoomVo> queryAllMeetingRoomsOfTheDay(Date date, String capacity, String roomName,
			String order, int pageIndex);

	/**
	 * @Description: 获取满足指定条件的会议室记录
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<MeetingRoom> queryMeetingRoomsByParameter(MeetingRoom meetingRoom, String order,
			int pageIndex);

	/**
	 * @Description: 获取指定会议室的当天非空闲时间
	 * @param sqlQueryParameter
	 * @return
	 */
	public BigDecimal queryMeetingRoomFreeTime(String roomId, Date date);

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
	public int queryAvailableMeetingRoomsCount(Date date, String capacity, String roomName);

	/**
	 * @Description: 选出当天所有的会议室总数
	 * @param params
	 * @return
	 */
	public int queryAllMeetingRoomsOfTheDayCount(Date date, String capacity, String roomName);

	/**
	 * @Description: 选出符合特定条件的会议室总数
	 * @param sqlQueryParameter
	 * @return
	 */
	public int queryMeetingRoomsByParameterCount(MeetingRoom meetingRoom);

	/**
	 * 空闲时间获取
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<Map<String, Object>> getAvailableMeetingRooms(Date date, String capacity, String roomName,
			String order, int pageIndex);

	/**
	 * 查询会议室列表数据
	 * @param room 会议室实体
	 * @param params 查询参数
	 * @param pageIndex 页码
	 * @param pageSize 页面大小
	 * @return
	 */
	public DataResult queryMeetingRooms(MeetingRoom room, Map<String, Object> params, int pageIndex, int pageSize);
}
