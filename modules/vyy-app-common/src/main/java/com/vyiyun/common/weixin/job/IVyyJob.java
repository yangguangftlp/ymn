package com.vyiyun.common.weixin.job;

/**
 * 任务
 * 
 * @author zb.shen
 * @date 2015年12月30日
 * @version 1.0
 */
public interface IVyyJob {

	/**
	 * 任务是否是活动
	 */
	public boolean isActivity();

	/**
	 * 设置是否活动
	 * 
	 * @param activity
	 */
	public void setActivity(boolean activity);

	/**
	 * 任务执行
	 */
	public void execute();
}
