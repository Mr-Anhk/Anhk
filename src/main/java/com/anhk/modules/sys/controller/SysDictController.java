package com.anhk.modules.sys.controller;

import com.anhk.common.utils.PageUtils;
import com.anhk.common.utils.ResultVo;
import com.anhk.common.validator.ValidatorUtils;
import com.anhk.modules.sys.entity.SysDictEntity;
import com.anhk.modules.sys.service.SysDictService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 数据字典
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("sys/dict")
public class SysDictController {
    @Autowired
    private SysDictService sysDictService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:dict:list")
    public ResultVo list(@RequestParam Map<String, Object> params){
        PageUtils page = sysDictService.queryPage(params);

        return ResultVo.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:dict:info")
    public ResultVo info(@PathVariable("id") Long id){
        SysDictEntity dict = sysDictService.getById(id);

        return ResultVo.ok().put("dict", dict);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:dict:save")
    public ResultVo save(@RequestBody SysDictEntity dict){
        //校验类型
        ValidatorUtils.validateEntity(dict);

        sysDictService.save(dict);

        return ResultVo.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:dict:update")
    public ResultVo update(@RequestBody SysDictEntity dict){
        //校验类型
        ValidatorUtils.validateEntity(dict);

        sysDictService.updateById(dict);

        return ResultVo.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:dict:delete")
    public ResultVo delete(@RequestBody Long[] ids){
        sysDictService.removeByIds(Arrays.asList(ids));

        return ResultVo.ok();
    }

}
