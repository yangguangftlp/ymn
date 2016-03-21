package com.vyiyun.common.weixin.job;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.vyiyun.common.weixin.job.impl.ScanAttendanceRuleJob;

public class ScanAttendanceRuleQuartzJobBean extends QuartzJobBean {

	private static final Logger LOGGER = Logger.getLogger(ScanAttendanceRuleQuartzJobBean.class);

	private String targetObject;

	private String targetMethod;

	private ApplicationContext ctx;

	protected void executeInternal(JobExecutionContext context)
	throws JobExecutionException {
		try {
			LOGGER.info("execute [" + targetObject + "] at once>>>>>>");
			Object otargetObject = ctx.getBean(targetObject);
			Method m = null;
			try {
				m = otargetObject.getClass().getMethod(targetMethod,new Class[] {ScanAttendanceRuleJob.class});
				ScanAttendanceRuleJob scanAttendanceRuleJob = (ScanAttendanceRuleJob) ctx.getBean("scanAttendanceRuleJob");
				m.invoke(otargetObject, scanAttendanceRuleJob);
			} catch (SecurityException e) {
				LOGGER.error(e);
			} catch (NoSuchMethodException e) {
				LOGGER.error(e);
			}
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

	public void setApplicationContext(ApplicationContext applicationContext) {

		this.ctx = applicationContext;

	}

	public void setTargetObject(String targetObject) {

		this.targetObject = targetObject;

	}

	public void setTargetMethod(String targetMethod) {

		this.targetMethod = targetMethod;

	}

}
