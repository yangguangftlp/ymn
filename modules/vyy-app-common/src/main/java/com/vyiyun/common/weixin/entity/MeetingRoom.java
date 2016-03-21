package com.vyiyun.common.weixin.entity;

import java.io.Serializable;

/**
 * @ClassName: MeetingRoom
 * @Description: 会议室类
 * @author XiaoWei
 * @date Dec 22, 2015 10:23:50 AM
 */
public class MeetingRoom implements Serializable {

	private static final long serialVersionUID = -8254869272236630312L;

	/**
	 * ID
	 */
	private String id;
	
	/**
	 * 企业ID
	 */
	private String corpId;

	/**
	 * 会议室名称
	 */
	private String roomName;

	/**
	 * 容纳人数
	 */
	private Integer capacity;

	/**
	 * 设备情况
	 */
	private String equipment;

	/**
	 * 会议室地址
	 */
	private String address;

	public MeetingRoom() {
		super();
	}

	public MeetingRoom(String id, String corpId, String roomName, Integer capacity,
			String equipment, String address) {
		super();
		this.id = id;
		this.corpId = corpId;
		this.roomName = roomName;
		this.capacity = capacity;
		this.equipment = equipment;
		this.address = address;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public String getEquipment() {
		return equipment;
	}

	public void setEquipment(String equipment) {
		this.equipment = equipment;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "MeetingRoom [id=" + id + ", corpId=" + corpId + ", roomName=" + roomName
				+ ", capacity=" + capacity + ", equipment=" + equipment
				+ ", adress=" + address + "]";
	}

}
