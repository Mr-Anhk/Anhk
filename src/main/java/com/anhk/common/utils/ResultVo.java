package com.anhk.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */
public class ResultVo extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	
	public ResultVo() {
		put("status", 0);
		put("message", "success");
	}
	
	public static ResultVo error() {
		return error(500, "未知异常，请联系管理员");
	}
	
	public static ResultVo error(String message) {
		return error(500, message);
	}
	
	public static ResultVo error(int status, String message) {
		ResultVo r = new ResultVo();
		r.put("status", status);
		r.put("message", message);
		return r;
	}

	public static ResultVo ok(String message) {
		ResultVo r = new ResultVo();
		r.put("message", message);
		return r;
	}
	
	public static ResultVo ok(Map<String, Object> map) {
		ResultVo r = new ResultVo();
		r.putAll(map);
		return r;
	}
	
	public static ResultVo ok() {
		return new ResultVo();
	}

	@Override
	public ResultVo put(String key, Object value) {
		super.put(key, value);
		return this;
	}
}
