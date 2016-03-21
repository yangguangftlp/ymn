package com.vyiyun.common.weixin.entity;

import java.io.Serializable;

/**
 * @ClassName: MeetingRoomBosumVo
 * @Description: 会议室类扩展类
 * @author XiaoWei
 * @date Dec 25, 2015 5:11:41 PM
 */
public class MeetingRoomVo implements Serializable {

	private static final long serialVersionUID = -792324671966969740L;

	private MeetingRoom meetingRoomBosum;
	private String totalTimeEquation;

	public MeetingRoomVo() {
		super();
	}

	public MeetingRoomVo(MeetingRoom meetingRoomBosum,
			String totalTimeEquation) {
		super();
		this.meetingRoomBosum = meetingRoomBosum;
		this.totalTimeEquation = totalTimeEquation;
	}

	public MeetingRoom getMeetingRoomBosum() {
		return meetingRoomBosum;
	}

	public void setMeetingRoomBosum(MeetingRoom meetingRoomBosum) {
		this.meetingRoomBosum = meetingRoomBosum;
	}

	public String getTotalTimeEquation() {
		String newTotalTimeEquation = strToStr(this.totalTimeEquation);
		return newTotalTimeEquation;
	}

	public void setTotalTimeEquation(String totalTimeEquation) {
		this.totalTimeEquation = totalTimeEquation;
	}

	@Override
	public String toString() {
		return "MeetingRoomBosumVo [meetingRoomBosum=" + meetingRoomBosum
				+ ", totalTimeEquation=" + totalTimeEquation + "]";
	}
	
	public String strToStr(String dig) {
		String digNew = "";
		String[] digArrays = dig.split("\\.");
		if(digArrays != null){
			if(digArrays.length > 1){
				String secStr = digArrays[1].substring(0, 1);
				if(Integer.parseInt(secStr)!=0){
					dig = digArrays[0].concat(".").concat(secStr);
				} else {
					dig = digArrays[0];
				}
			} else {
				dig = digArrays[0];
			}
		} 
		digNew = dig;
		return digNew;
	}

}
