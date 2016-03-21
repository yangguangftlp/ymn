/**
 * 
 */
package com.vyiyun.common.weixin.job.impl;

import com.vyiyun.common.weixin.job.IVyyJob;

/**
 * @author zb.shen
 * 
 * @date 2015年12月30日
 * @version 1.0
 */
public abstract class AbstVvyJob implements IVyyJob {

	/**
	 * 任务活动情况
	 */
	private boolean activity;

	@Override
	public boolean isActivity() {
		return activity;
	}

	@Override
	public void setActivity(boolean activity) {
		this.activity = activity;
	}

	@Override
	public void execute() {
		if (!activity) {
			try {
				executeJob();
			} finally {
				activity = false;
			}
		}
	}

	public abstract void executeJob();
}
