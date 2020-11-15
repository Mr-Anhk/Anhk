package com.anhk.modules.sys.controller;

import com.anhk.common.utils.PageUtils;
import com.anhk.common.utils.ResultVo;
import com.anhk.modules.sys.service.SysLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;


/**
 * 系统日志
 *
 * @author Mark sunlightcs@gmail.com
 */
@Controller
@RequestMapping("/sys/log")
public class SysLogController {
	@Autowired
	private SysLogService sysLogService;
	
	/**
	 * 列表
	 */
	@ResponseBody
	@RequestMapping("/list")
	@RequiresPermissions("sys:log:list")
	public ResultVo list(@RequestParam Map<String, Object> params){
		PageUtils page = sysLogService.queryPage(params);

		return ResultVo.ok().put("page", page);
	}
	
}
