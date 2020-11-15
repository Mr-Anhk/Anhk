package com.anhk.modules.sys.service;

import com.anhk.common.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import com.anhk.modules.sys.entity.SysDictEntity;

import java.util.Map;

/**
 * 数据字典
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface SysDictService extends IService<SysDictEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

