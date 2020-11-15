package com.anhk.modules.sys.service;


import com.anhk.common.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import com.anhk.modules.sys.entity.SysLogEntity;

import java.util.Map;


/**
 * 系统日志
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface SysLogService extends IService<SysLogEntity> {

    PageUtils  queryPage(Map<String, Object> params);

}
