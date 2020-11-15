package com.anhk.common.exception;

/**
 * 自定义异常
 *
 * @author Mark sunlightcs@gmail.com
 */
public class BusinessException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private int code = 500;
	private String msg;

    public BusinessException(String msg) {
		super(msg);
		this.msg = msg;
	}
	
	public BusinessException(String msg, Throwable e) {
		super(msg, e);
		this.msg = msg;
	}
	
	public BusinessException(int code, String msg) {
		super(msg);
		this.code = code;
		this.msg = msg;
	}
	
	public BusinessException(int code, String msg, Throwable e) {
		super(msg, e);
		this.code = code;
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	
}
