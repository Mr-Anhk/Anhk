package com.anhk.common.exception;

import com.anhk.common.utils.ResultVo;
import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理器
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(BusinessException.class)
	public ResultVo handleRRException(BusinessException e){
		ResultVo r = new ResultVo();
		r.put("code", e.getCode());
		r.put("msg", e.getMessage());

		return r;
	}

	@ExceptionHandler(DuplicateKeyException.class)
	public ResultVo handleDuplicateKeyException(DuplicateKeyException e){
		logger.error(e.getMessage(), e);
		return ResultVo.error("数据库中已存在该记录");
	}

	@ExceptionHandler(AuthorizationException.class)
	public ResultVo handleAuthorizationException(AuthorizationException e){
		logger.error(e.getMessage(), e);
		return ResultVo.error("没有权限，请联系管理员授权");
	}

	@ExceptionHandler(Exception.class)
	public ResultVo handleException(Exception e){
		logger.error(e.getMessage(), e);
		return ResultVo.error();
	}
}
