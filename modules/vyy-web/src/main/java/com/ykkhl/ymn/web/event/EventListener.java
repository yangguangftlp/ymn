/**
 * 
 */
package com.ykkhl.ymn.web.event;

import com.ykkhl.ymn.web.model.ListenerExecuteContext;

/**
 * 事件监听器
 * 
 * @author tf
 * 
 */
public interface EventListener {
	/**
	 * 获取监听器id
	 * 
	 * @return
	 */
	String getId();

	/**
	 * 监听的事件类型
	 * 
	 * @return
	 */
	String getEventType();

	/**
	 * 监听器执行方法
	 * 
	 * @return
	 */
	public Object execute(ListenerExecuteContext executeContext);

	/**
	 * 获取优先级
	 * 
	 * @return
	 */
	int getPriority();
}
