package com.anhk.modules.oss.service;

import com.anhk.common.utils.PageUtils;
import com.anhk.modules.oss.entity.SysOssEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 文件上传
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface SysOssService extends IService<SysOssEntity> {

	PageUtils queryPage(Map<String, Object> params);
}
