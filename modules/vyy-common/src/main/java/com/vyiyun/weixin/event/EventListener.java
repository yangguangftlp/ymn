/**
 * 
 */
package com.vyiyun.weixin.event;

/**
 * 微信事件
 * 
 * @author tf
 * 
 * @date 下午6:04:37
 */
public interface EventListener {

	String getId();

	/**
	 * 监听的事件类型
	 * 
	 * @return
	 */
	String getEventType();

	/**
	 * 获取优先级
	 * 
	 * @return
	 */
	int getPriority();
}
