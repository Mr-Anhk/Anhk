package com.anhk.modules.oss.service.impl;

import com.anhk.common.utils.PageUtils;
import com.anhk.common.utils.Query;
import com.anhk.modules.oss.entity.SysOssEntity;
import com.anhk.modules.oss.service.SysOssService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anhk.modules.oss.dao.SysOssDao;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("sysOssService")
public class SysOssServiceImpl extends ServiceImpl<SysOssDao, SysOssEntity> implements SysOssService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<SysOssEntity> page = this.page(
			new Query<SysOssEntity>().getPage(params)
		);

		return new PageUtils(page);
	}
	
}
