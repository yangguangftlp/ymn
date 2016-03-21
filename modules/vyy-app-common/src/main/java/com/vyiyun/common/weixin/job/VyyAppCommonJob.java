package com.vyiyun.common.weixin.job;

import java.io.Serializable;

import com.vyiyun.common.weixin.job.impl.ScanAttendanceRuleJob;

public class VyyAppCommonJob implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8207716069981964526L;

	public void scanAttendanceRuleJob(ScanAttendanceRuleJob scanAttendanceRuleJob) {
		scanAttendanceRuleJob.execute();
	}
}
