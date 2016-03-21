/**
 * 
 */
package com.vyiyun.weixin.msg;

/**
 * 消息执行器
 * 
 * 
 * @author tf
 * 
 * @date 2015年7月16日 下午2:10:47
 * @version 1.0
 */
public interface MsgExecutor {

	/**
	 * 消息名称
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * 设置参数
	 * 
	 * @param obj
	 */
	public void set(Object obj);

	/**
	 * 任务执行
	 */
	public void execute() throws Exception;

	/**
	 * 设置corpId
	 * 
	 * @param corpId
	 */
	public void setCorpId(String corpId);

	/**
	 * 设置套件id
	 * 
	 * @param suiteId
	 */
	public void setSuiteId(String suiteId);

	/**
	 * 设置套件Secret
	 * 
	 * @param suiteSecret
	 */
	public void setSuiteSecret(String suiteSecret);
}
