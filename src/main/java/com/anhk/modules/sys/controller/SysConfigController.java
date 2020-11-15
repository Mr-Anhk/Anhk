package com.anhk.modules.sys.controller;


import com.anhk.common.utils.PageUtils;
import com.anhk.common.utils.ResultVo;
import com.anhk.common.annotation.SysLog;
import com.anhk.common.validator.ValidatorUtils;
import com.anhk.modules.sys.entity.SysConfigEntity;
import com.anhk.modules.sys.service.SysConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系统配置信息
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/sys/config")
public class SysConfigController extends AbstractController {
	@Autowired
	private SysConfigService sysConfigService;
	
	/**
	 * 所有配置列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:config:list")
	public ResultVo list(@RequestParam Map<String, Object> params){
		PageUtils page = sysConfigService.queryPage(params);

		return ResultVo.ok().put("page", page);
	}
	
	
	/**
	 * 配置信息
	 */
	@RequestMapping("/info/{id}")
	@RequiresPermissions("sys:config:info")
	@ResponseBody
	public ResultVo info(@PathVariable("id") Long id){
		SysConfigEntity config = sysConfigService.getById(id);
		
		return ResultVo.ok().put("config", config);
	}
	
	/**
	 * 保存配置
	 */
	@SysLog("保存配置")
	@RequestMapping("/save")
	@RequiresPermissions("sys:config:save")
	public ResultVo save(@RequestBody SysConfigEntity config){
		ValidatorUtils.validateEntity(config);

		sysConfigService.saveConfig(config);
		
		return ResultVo.ok();
	}
	
	/**
	 * 修改配置
	 */
	@SysLog("修改配置")
	@RequestMapping("/update")
	@RequiresPermissions("sys:config:update")
	public ResultVo update(@RequestBody SysConfigEntity config){
		ValidatorUtils.validateEntity(config);
		
		sysConfigService.update(config);
		
		return ResultVo.ok();
	}
	
	/**
	 * 删除配置
	 */
	@SysLog("删除配置")
	@RequestMapping("/delete")
	@RequiresPermissions("sys:config:delete")
	public ResultVo delete(@RequestBody Long[] ids){
		sysConfigService.deleteBatch(ids);
		
		return ResultVo.ok();
	}

}
