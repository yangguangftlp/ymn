/**
 * 
 */
package com.vyiyun.weixin.exception;

import com.vyiyun.weixin.utils.SpringContextHolder;

/**
 * energy异常
 * 
 * @author tf
 * 
 *         2015年6月29日 上午9:41:45
 */
public class VyiyunException extends RuntimeException {

	/**
	 * 错误码
	 */
	private String errorCode;

	/**
	 * 错误原因
	 */
	private String reason;

	/**
	 * 解决途径
	 */
	private String solution;
	/**
	 * 
	 */
	private static final long serialVersionUID = -7899365293345477282L;

	public VyiyunException(String exceptionCode) {
		super(SpringContextHolder.getI18n(exceptionCode, null, null));
		this.errorCode = exceptionCode;
		this.reason = getMessage();// SpringContextHolder.getI18n(exceptionCode
									// + "_reason");
		this.solution = getMessage();// SpringContextHolder.getI18n(exceptionCode
										// + "_solution");
	}

	public VyiyunException(String exceptionCode, String[] args) {
		super(SpringContextHolder.getI18n(exceptionCode, args, null));
		this.errorCode = exceptionCode;
	}

	@Deprecated
	public VyiyunException(String exceptionCode, String defaultMessage) {
		super(SpringContextHolder.getI18n(exceptionCode, null, defaultMessage));
		this.errorCode = exceptionCode;
		this.reason = getMessage();// SpringContextHolder.getI18n(exceptionCode
									// + "_reason");
		this.solution = getMessage();// SpringContextHolder.getI18n(exceptionCode
										// + "_solution");
	}

	public VyiyunException(String exceptionCode, String[] args, String defaultMessage) {
		super(SpringContextHolder.getI18n(exceptionCode, args, defaultMessage));
		this.errorCode = exceptionCode;
		this.reason = getMessage();// SpringContextHolder.getI18n(exceptionCode
									// + "_reason");
		this.solution = getMessage();// SpringContextHolder.getI18n(exceptionCode
										// + "_solution");
	}

	public VyiyunException() {
		super();
	}

	public VyiyunException(String exceptionCode, Throwable cause) {
		super(SpringContextHolder.getI18n(exceptionCode, null, null), cause);
		this.errorCode = exceptionCode;
		this.reason = getMessage();// SpringContextHolder.getI18n(exceptionCode
									// + "_reason");
		this.solution = getMessage();// SpringContextHolder.getI18n(exceptionCode
										// + "_solution");
	}

	public VyiyunException(String exceptionCode, String[] args, Throwable cause) {
		super(SpringContextHolder.getI18n(exceptionCode, args, null), cause);
		this.errorCode = exceptionCode;
		this.reason = getMessage();// SpringContextHolder.getI18n(exceptionCode
									// + "_reason");
		this.solution = getMessage();// SpringContextHolder.getI18n(exceptionCode
										// + "_solution");
	}

	public VyiyunException(String exceptionCode, String defaultMessage, Throwable cause) {
		super(SpringContextHolder.getI18n(exceptionCode, null, defaultMessage), cause);
		this.errorCode = exceptionCode;
		this.reason = getMessage();// SpringContextHolder.getI18n(exceptionCode
									// + "_reason");
		this.solution = getMessage();// SpringContextHolder.getI18n(exceptionCode
										// + "_solution");
	}

	public VyiyunException(String exceptionCode, String[] args, String defaultMessage, Throwable cause) {
		super(SpringContextHolder.getI18n(exceptionCode, args, defaultMessage), cause);
		this.errorCode = exceptionCode;
		this.reason = getMessage();// SpringContextHolder.getI18n(exceptionCode
									// + "_reason");
		this.solution = getMessage();// SpringContextHolder.getI18n(exceptionCode
										// + "_solution");
	}

	public VyiyunException(Throwable cause) {
		super(cause);
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

}
