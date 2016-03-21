package com.vyiyun.weixin.enums;

/**
* @ClassName: WeixinDepartStatus 
* @Description: 微信部门获取成员时的状态类 
* @author CCLIU 
* @date 2015-6-26 下午4:42:23 
* v1.0
 */
public enum WeixinDepartStatus {
	// 0获取全部成员，1获取已关注成员列表，2获取禁用成员列表，4获取未关注成员列表
	获取全部成员(0), 获取已关注成员列表(1), 获取禁用成员列表(2), 获取未关注成员列表(4);
		
	private int index = 0;

    private WeixinDepartStatus(int index) {
        this.index = index;
    }

    public static WeixinDepartStatus valueOf(int index) {
        switch (index) {
        case 0:
            return 获取全部成员;
        case 1:
            return 获取已关注成员列表;
        case 2:
            return 获取禁用成员列表;
        case 4:
            return 获取未关注成员列表;
        default:
            return null;
        }
    }

    public int index() {
        return this.index;
    }
    public String value(){
    	return String.valueOf(index);
    }
}
