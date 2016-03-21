/**
 * 
 */
package com.vyiyun.weixin.msg;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.vyiyun.weixin.utils.SystemCacheUtil;

/**
 * 消息队列线程
 * 
 * @author tf
 * 
 * @date 2015年7月16日 下午2:06:55
 * @version 1.0
 */
public class MsgQueueThread implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(MsgQueueThread.class);

	private boolean isStop;

	@Override
	public void run() {
		LinkedBlockingQueue<MsgExecutor> linkedQueue = SystemCacheUtil.getInstance().getLinkedQueue();
		MsgExecutor msgExecutor = null;
		while (!isStop) {
			while (!linkedQueue.isEmpty()) {
				try {
					msgExecutor = linkedQueue.poll();
					msgExecutor.execute();
					LOGGER.debug("执行消息 成功 !消息名称：" + msgExecutor.getName());
				} catch (Exception e) {
					LOGGER.error("执行消息异常 !消息名称：" + msgExecutor.getName(), e);
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
