/**
 * 
 */
package com.vyiyun.weixin.exception;

/**
 * 业务异常
 * 
 * @author tf
 * @date 2015年11月26日 上午10:37:09
 */
public class VyiyunBusinessException extends VyiyunException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7538857714225174360L;

	public VyiyunBusinessException() {
		super();
	}

	public VyiyunBusinessException(String exceptionCode, String defaultMessage,
			Throwable cause) {
		super(exceptionCode, defaultMessage, cause);
	}

	public VyiyunBusinessException(String exceptionCode, String defaultMessage) {
		super(exceptionCode, defaultMessage);
	}

	public VyiyunBusinessException(String exceptionCode, String[] args,
			String defaultMessage, Throwable cause) {
		super(exceptionCode, args, defaultMessage, cause);
	}

	public VyiyunBusinessException(String exceptionCode, String[] args,
			String defaultMessage) {
		super(exceptionCode, args, defaultMessage);
	}

	public VyiyunBusinessException(String exceptionCode, String[] args,
			Throwable cause) {
		super(exceptionCode, args, cause);
	}

	public VyiyunBusinessException(String exceptionCode, String[] args) {
		super(exceptionCode, args);
	}

	public VyiyunBusinessException(String exceptionCode, Throwable cause) {
		super(exceptionCode, cause);
	}

	public VyiyunBusinessException(String exceptionCode) {
		super(exceptionCode);
	}

	public VyiyunBusinessException(Throwable cause) {
		super(cause);
	}
}
