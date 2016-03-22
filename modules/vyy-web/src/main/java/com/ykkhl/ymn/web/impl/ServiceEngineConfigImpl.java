/**
 * 
 */
package com.ykkhl.ymn.web.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vyiyun.weixin.msg.MsgQueueThread;
import com.ykkhl.ymn.web.event.EventListener;

/**
 * @author tf
 * 
 */
public class ServiceEngineConfigImpl extends ServiceEngineImpl {

	private static Logger LOGGER = LoggerFactory.getLogger(ServiceEngineConfigImpl.class);

	public ServiceEngineConfigImpl(ServiceEngineConfigImpl serviceEngineConfig) {
		super(serviceEngineConfig);
		init();
	}

	/**
	 * 所有的事件监听配置(系统级和外部) 事件监听配置
	 */
	protected List<EventListener> eventListeners = new ArrayList<EventListener>(10);
	/**
	 * 监听器
	 */
	protected Map<String, List<EventListener>> eventMapListeners;

	public void init() {
		init();
	}

	protected void initEventListeners() {

		if (LOGGER.isDebugEnabled() && !eventListeners.isEmpty()) {
			EventListener eventListener = null;
			LOGGER.debug("监听器优先级排序，结果如下：");
			for (Iterator<EventListener> iterator = eventListeners.iterator(); iterator.hasNext();) {
				eventListener = iterator.next();
				LOGGER.debug("监听编号：{0},监听事件：{1},优先级：{2},类名：{3}", eventListener.getId(), eventListener.getEventType(),
						eventListener.getPriority(), eventListener.getClass().getSimpleName());
			}
		}
		if (eventListeners != null) {
			this.eventMapListeners = new HashMap<String, List<EventListener>>();
			List<EventListener> tmpEventListeners = null;
			for (EventListener tmp : eventListeners) {
				if (eventMapListeners.get(tmp.getEventType()) == null) {
					tmpEventListeners = new ArrayList<EventListener>();
					tmpEventListeners.add(tmp);
					eventMapListeners.put(tmp.getEventType(), tmpEventListeners);
				} else {
					eventMapListeners.get(tmp.getEventType()).add(tmp);
				}
			}
		}
	}
}
