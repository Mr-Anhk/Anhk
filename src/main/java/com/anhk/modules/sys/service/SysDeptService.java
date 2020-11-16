package com.anhk.modules.sys.service;


import com.anhk.common.utils.ResultVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.anhk.modules.sys.entity.SysDeptEntity;

import java.util.List;
import java.util.Map;

/**
 * 部门管理
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface SysDeptService extends IService<SysDeptEntity> {

	/**
	 * 分页查询部门信息
	 * @param map
	 * @return
	 */
	List<SysDeptEntity> queryList(Map<String, Object> map);

	/**
	 * 查询子部门ID列表
	 * @param parentId  上级部门ID
	 */
	List<Long> queryDetpIdList(Long parentId);

	/**
	 * 获取子部门ID，用于数据过滤
	 * @param deptId
	 * @return
	 */
	List<Long> getSubDeptIdList(Long deptId);

	/**
	 * 新增部门信息
	 * @param dept
	 * @return
	 */
    ResultVo add(SysDeptEntity dept);

	/**
	 * 更新部门信息
	 * @param dept
	 * @return
	 */
	ResultVo update(SysDeptEntity dept);
}
