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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.vyiyun.common.weixin.dao.MeetingRoomMapper;
import com.vyiyun.common.weixin.entity.Meeting;
import com.vyiyun.common.weixin.entity.MeetingRoom;
import com.vyiyun.common.weixin.entity.MeetingRoomVo;
import com.vyiyun.common.weixin.service.IMeetingRoomService;
import com.vyiyun.weixin.entity.SystemConfig;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.DataResult;
import com.vyiyun.weixin.model.SqlQueryParameter;
import com.vyiyun.weixin.service.ISystemConfigService;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.StringUtil;

/**
 * @ClassName: MeetingRoomServiceImpl
 * @Description: TODO
 * @author XiaoWei
 * @date Dec 28, 2015 10:09:11 AM
 */
@Service("meetingRoomService")
public class MeetingRoomServiceImpl implements IMeetingRoomService {

	@Autowired
	private MeetingRoomMapper meetingRoomDao;
	
	@Autowired
	private ISystemConfigService systemConfigService;
	
	//-----------业务最终处理方法，供controller直接调用-----------
	
	@Override
	public List<String> getTotalStartAndEndTime(){
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
		SystemConfig systemConfig1 = new SystemConfig();
		systemConfig1.setType("MeetingRoomTime");
		systemConfig1.setName("meeting_totalStartTime");
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
		systemConfig1.setCorpId(corpId);
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
		List<SystemConfig> totalStartTimeList = systemConfigService.getSystemConfig(systemConfig1);
		SystemConfig systemConfig2 = new SystemConfig();
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
		systemConfig2.setCorpId(corpId);
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
		systemConfig2.setType("MeetingRoomTime");
		systemConfig2.setName("meeting_totalEndTime");
		List<SystemConfig> totalEndTimeList = systemConfigService.getSystemConfig(systemConfig2);
		if((!CollectionUtils.isEmpty(totalStartTimeList)) && (!CollectionUtils.isEmpty(totalEndTimeList))){
			List<String> totalStartAndEndTime = new ArrayList<String>();
			totalStartAndEndTime.add(totalStartTimeList.get(0).getValue());
			totalStartAndEndTime.add(totalEndTimeList.get(0).getValue());
			return totalStartAndEndTime;
		}else {
			throw new VyiyunException("从数据库中取到的总的开始和结束时间为空");
		}
	}
	
	
	//-----------业务基础处理方法-----------
	
	@Override
	public void setTotalStartAndEndTime(String totalStartTimeStr, String totalEndTimeStr){
		try {
			//-----判断start < end
			Date totalStartTime = new SimpleDateFormat("HH:mm:ss").parse(totalStartTimeStr);
			Date totalEndTime = new SimpleDateFormat("HH:mm:ss").parse(totalEndTimeStr);
			if(totalStartTime.getTime() < totalEndTime.getTime()){
				//-----开始时间 设置--------
				String startId = null;
				SystemConfig systemConfig1 = new SystemConfig();
				systemConfig1.setType("meetingCom");
				systemConfig1.setName("meeting_totalStartTime");
				List<SystemConfig> list1 = systemConfigService.getSystemConfig(systemConfig1);
				if(!CollectionUtils.isEmpty(list1)){
					startId = list1.get(0).getId();
				}
				if(startId != null){
					systemConfig1 = new SystemConfig();
					systemConfig1.setId(startId);
					systemConfig1.setValue(totalStartTimeStr);
					systemConfigService.update(systemConfig1);
				}
				//-----结束时间 设置--------
				String endId = null;
				SystemConfig systemConfig2 = new SystemConfig();
				systemConfig2.setType("meetingCom");
				systemConfig2.setName("meeting_totalStartTime");
				List<SystemConfig> list2 = systemConfigService.getSystemConfig(systemConfig2);
				if(!CollectionUtils.isEmpty(list2)){
					endId = list2.get(0).getId();
				}
				if(endId != null){
					systemConfig2 = new SystemConfig();
					systemConfig2.setId(endId);
					systemConfig2.setValue(totalEndTimeStr);
					systemConfigService.update(systemConfig2);
				}
			} else {
				throw new VyiyunException("结束时间必须大于开始时间");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//----------dao层基本方法处理-----------
	
	@Override
	public String addMeetingRoom(MeetingRoom meetingRoom) {
//		String id = UUID.randomUUID().toString();
//		id = id.replaceAll("-",	"");
//		meetingRoom.setId(id);
		meetingRoomDao.addMeetingRoom(meetingRoom);
		return null;
	}

	@Override
	public MeetingRoom queryMeetingRoomById(String id) {
		return meetingRoomDao.queryMeetingRoomById(id);
	}

	@Override
	public List<MeetingRoom> queryAllMeetingRooms(String order) {
		String orderBy = null;
		if(StringUtil.isNotEmpty(order)){
			if("ASC".equalsIgnoreCase(order) || "DESC".equalsIgnoreCase(order)){
				orderBy = order;
			}
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("order", orderBy);
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		sqlQueryParameter.getKeyValMap().putAll(params);
		sqlQueryParameter.setPage(true);
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
		MeetingRoom room = new MeetingRoom();
		room.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
		sqlQueryParameter.setParameter(room);
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
		return meetingRoomDao.queryAllMeetingRooms(sqlQueryParameter);
	}
	
	@Override
	public List<MeetingRoom> queryAllMeetingRooms(){
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("order", "ASC");
		sqlQueryParameter.getKeyValMap().putAll(params);
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
		MeetingRoom room = new MeetingRoom();
		room.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
		sqlQueryParameter.setParameter(room);
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
		return meetingRoomDao.queryAllMeetingRooms(sqlQueryParameter);
	}

	@Override
	public List<MeetingRoomVo> queryAvailableMeetingRooms(Date date,
			String capacity, String roomName, String order, int pageIndex) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("totalTimeEquation", this.getTotalTimeEquation());
		params.put("date", date);
		if(StringUtil.isNotEmpty(roomName)){
			params.put("roomName", roomName);
		}
		if(StringUtil.isNotEmpty(capacity)){
			params.put("capacity", Integer.parseInt(capacity));
		}
		if(StringUtil.isNotEmpty(order)){
			if("ASC".equalsIgnoreCase(order) || "DESC".equalsIgnoreCase(order)){
				params.put("order", order);
			}
		}
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		sqlQueryParameter.getKeyValMap().putAll(params);
		sqlQueryParameter.setPage(true);
		sqlQueryParameter.setPageIndex(pageIndex);
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
		MeetingRoom room = new MeetingRoom();
		room.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId()); // 企业ID
		sqlQueryParameter.setParameter(room);
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
		return meetingRoomDao.queryAvailableMeetingRooms(sqlQueryParameter);
	}
	
	@Override
	public List<MeetingRoomVo> queryAllMeetingRoomsOfTheDay(Date date,
			String capacity, String roomName, String order, int pageIndex) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("totalTimeEquation", this.getTotalTimeEquation());
		params.put("date", date);
		if(StringUtil.isNotEmpty(roomName)){
			params.put("roomName", roomName);
		}
		if(StringUtil.isNotEmpty(capacity)){
			params.put("capacity", Integer.parseInt(capacity));
		}else {
			params.put("capacity", null);
		}
		if(StringUtil.isNotEmpty(order)){
			if("ASC".equalsIgnoreCase(order) || "DESC".equalsIgnoreCase(order)){
				params.put("order", order);
			}
		}
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		sqlQueryParameter.getKeyValMap().putAll(params);
		sqlQueryParameter.setPage(true);
		sqlQueryParameter.setPageIndex(pageIndex);
		List<MeetingRoomVo> list = meetingRoomDao.queryAllMeetingRoomsOfTheDay(sqlQueryParameter);
		return list;
	}

	@Override
	public List<MeetingRoom> queryMeetingRoomsByParameter(MeetingRoom meetingRoom, 
			String order, int pageIndex) {
		Map<String, Object> params = new HashMap<String, Object>();
		if(StringUtil.isNotEmpty(order)){
			if("ASC".equalsIgnoreCase(order) || "DESC".equalsIgnoreCase(order)){
				params.put("order", order);
			}
		}
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		sqlQueryParameter.getKeyValMap().putAll(params);
		sqlQueryParameter.setParameter(meetingRoom);
		sqlQueryParameter.setPage(true);
		sqlQueryParameter.setPageIndex(pageIndex);
		List<MeetingRoom> list = meetingRoomDao.queryMeetingRoomsByParameter(sqlQueryParameter);
		 return list;
	}

	@Override
	public BigDecimal queryMeetingRoomFreeTime(String roomId, Date date) {
		if(this.getTotalTimeEquation() != null){
			Map<String, Object> keyValMap = new HashMap<String, Object>();
			keyValMap.put("totalTimeEquation", this.getTotalTimeEquation());
			Meeting meeting = new Meeting();
			meeting.setRoomId(roomId);
			meeting.setDate(date);
			SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
			sqlQueryParameter.setParameter(meeting);
			sqlQueryParameter.getKeyValMap().putAll(keyValMap);
			long freeTime = meetingRoomDao.queryMeetingRoomFreeTime(sqlQueryParameter);
			BigDecimal meetingRoomFreeTime = new BigDecimal(String.valueOf(freeTime));
			return meetingRoomFreeTime.divide(new BigDecimal("3600"));
		}else {
			throw new VyiyunException("总的起始时间和总的结束时间为空");
		}
	}

	@Override
	public void deleteMeetingRoomById(String id) {
		meetingRoomDao.deleteMeetingRoomById(id);
	}

	@Override
	public void deleteMeetingRoomsByIds(List<String> ids) {
		meetingRoomDao.deleteMeetingRoomsByIds(ids);
	}

	@Override
	public void updateMeetingRoom(MeetingRoom meetingRoom) {
		meetingRoomDao.updateMeetingRoom(meetingRoom);
	}
	
	@Override
	public int queryALLMeetingRoomsCount() {
		return meetingRoomDao.queryALLMeetingRoomsCount();
	}

	@Override
	public int queryAvailableMeetingRoomsCount(Date date, String capacity, String roomName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("totalTimeEquation", this.getTotalTimeEquation());
		params.put("date", date);
		if(StringUtil.isNotEmpty(roomName)){
			params.put("roomName", roomName);
		}
		if(StringUtil.isNotEmpty(capacity)){
			params.put("capacity", Integer.parseInt(capacity));
		}
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		sqlQueryParameter.getKeyValMap().putAll(params);
		return meetingRoomDao.queryAvailableMeetingRoomsCount(sqlQueryParameter);
	}
	
	@Override
	public int queryAllMeetingRoomsOfTheDayCount(Date date, String capacity, String roomName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("totalTimeEquation", this.getTotalTimeEquation());
		params.put("date", date);
		if(StringUtil.isNotEmpty(roomName)){
			params.put("roomName", roomName);
		}
		if(StringUtil.isNotEmpty(capacity)){
			params.put("capacity", Integer.parseInt(capacity));
		}
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		sqlQueryParameter.getKeyValMap().putAll(params);
		return meetingRoomDao.queryAllMeetingRoomsOfTheDayCount(sqlQueryParameter);
	}
	
	@Override
	public int queryMeetingRoomsByParameterCount(MeetingRoom meetingRoom) {
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		sqlQueryParameter.setParameter(meetingRoom);
		return meetingRoomDao.queryMeetingRoomsByParameterCount(sqlQueryParameter);
	}
	
	
	//---------基础方法处理---------
	
	private Double getTotalTimeEquation () {
		String totalStartTime = null;
		String totalEndTime = null;
		SystemConfig systemConfig1 = new SystemConfig();
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId(); // 企业ID
		systemConfig1.setCorpId(corpId);
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
		systemConfig1.setType("meetingCom");
		systemConfig1.setName("meeting_totalStartTime");
		List<SystemConfig> list1 = systemConfigService.getSystemConfig(systemConfig1);
		if(!CollectionUtils.isEmpty(list1)){
			totalStartTime = list1.get(0).getValue();
		}
		SystemConfig systemConfig2 = new SystemConfig();
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
		systemConfig2.setCorpId(corpId);
		// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
		systemConfig2.setType("meetingCom");
		systemConfig2.setName("meeting_totalEndTime");
		List<SystemConfig> list2 = systemConfigService.getSystemConfig(systemConfig2);
		if(!CollectionUtils.isEmpty(list2)){
			totalEndTime = list2.get(0).getValue();
		}
		//-------字符串处理为时间格式------
		if(totalStartTime != null && totalEndTime != null){
			try {
				Date dateStart = new SimpleDateFormat("HH:mm:ss").parse(totalStartTime);
				Date dateEnd = new SimpleDateFormat("HH:mm:ss").parse(totalEndTime);
				BigDecimal decimal = new BigDecimal(String.valueOf((dateEnd.getTime() - dateStart.getTime()))).divide(new BigDecimal(String.valueOf(1000*3600)));
				return decimal.doubleValue();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}


	@Override
	public List<Map<String, Object>> getAvailableMeetingRooms(Date date,
			String capacity, String roomName, String order, int pageIndex) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("totalTimeEquation", this.getTotalTimeEquation());
			params.put("date", date);
			if(StringUtil.isNotEmpty(roomName)){
				params.put("roomName", roomName);
			}
			if(StringUtil.isNotEmpty(capacity)){
				params.put("capacity", Integer.parseInt(capacity));
			}else {
				params.put("capacity", null);
			}
			if(StringUtil.isNotEmpty(order)){
				if("ASC".equalsIgnoreCase(order) || "DESC".equalsIgnoreCase(order)){
					params.put("order", order);
				}
			}
			SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
			// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen start.
			MeetingRoom meetingRoom = new MeetingRoom();
			meetingRoom.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
			sqlQueryParameter.setParameter(meetingRoom);
			// 微依云 公共应用 CorpId追加修正 2016-01-11 By zb.shen end.
			sqlQueryParameter.getKeyValMap().putAll(params);
			sqlQueryParameter.setPage(true);
			sqlQueryParameter.setPageIndex(pageIndex);
			List<Map<String,Object>> list = meetingRoomDao.getAvailableMeetingRooms(sqlQueryParameter);
			return list;
		}


	@Override
	public DataResult queryMeetingRooms(MeetingRoom room, Map<String, Object> params, int pageIndex, int pageSize) {
		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPage(true);
		}
		if (pageSize != -1) {
			sqlQueryParameter.setPageSize(pageSize);
		}

		sqlQueryParameter.getKeyValMap().putAll(params);
		sqlQueryParameter.setParameter(room);
		List<MeetingRoom> meetingRoomList = meetingRoomDao.queryAllMeetingRooms(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(meetingRoomList)) {
			dataResult.setData(meetingRoomList);
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}

}
