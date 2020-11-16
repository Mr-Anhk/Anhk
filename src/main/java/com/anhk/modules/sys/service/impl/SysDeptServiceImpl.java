package com.anhk.modules.sys.service.impl;

import com.anhk.common.utils.RegexUtils;
import com.anhk.common.utils.ResultVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anhk.common.annotation.DataFilter;
import com.anhk.modules.sys.dao.SysDeptDao;
import com.anhk.modules.sys.entity.SysDeptEntity;
import com.anhk.modules.sys.service.SysDeptService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service("sysDeptService")
public class SysDeptServiceImpl extends ServiceImpl<SysDeptDao, SysDeptEntity> implements SysDeptService {
	
	@Override
	@DataFilter(subDept = true, user = false, tableAlias = "t1")
	public List<SysDeptEntity> queryList(Map<String, Object> params){
		return baseMapper.queryList(params);
	}

	/**
	 * 根据部门ID查询自己部门的ID
	 * @param parentId  上级部门ID
	 * @return
	 */
	@Override
	public List<Long> queryDetpIdList(Long parentId) {
		return baseMapper.queryDetpIdList(parentId);
	}

	/**
	 * 根据部门ID查询该部门下的所有子级部门ID
	 * @param deptId
	 * @return
	 */
	@Override
	public List<Long> getSubDeptIdList(Long deptId){
		//部门及子部门ID列表
		List<Long> deptIdList = new ArrayList<>();
		//获取子部门ID
		List<Long> subIdList = queryDetpIdList(deptId);
		getDeptTreeList(subIdList, deptIdList);

		return deptIdList;
	}

	/**
	 * 新增部门信息
	 * @param dept
	 * @return
	 */
	@Override
	public ResultVo add(SysDeptEntity dept) {
		if (RegexUtils.getStrLength(dept.getName()) > 50) {
			return ResultVo.error("部门名称不能超过50个字符（一个汉字2个字符）");
		}
		boolean save = this.save(dept);
		if (!save) {
			return ResultVo.error("新增部门信息失败");
		}
		return ResultVo.ok();
	}

	/**
	 * 更新部门信息
	 * @param dept
	 * @return
	 */
	@Override
	public ResultVo update(SysDeptEntity dept) {
		if (RegexUtils.getStrLength(dept.getName()) > 50) {
			return ResultVo.error("部门名称不能超过50个字符（一个汉字2个字符）");
		}
		//获取此部门的所有子级部门ID
		List<Long> subDeptIdList = this.getSubDeptIdList(dept.getDeptId());
		//判断，上级部门是否是自己的子级部门
		if (subDeptIdList.contains(dept.getParentId()) || dept.getDeptId().equals(dept.getParentId())) {
			return ResultVo.error("上级部门不能为自己或自己的子级部门");
		}
		boolean update = this.updateById(dept);
		if (!update) {
			return ResultVo.error("更新部门信息失败");
		}
		return ResultVo.ok();
	}

	/**
	 * 递归
	 */
	private void getDeptTreeList(List<Long> subIdList, List<Long> deptIdList){
		for(Long deptId : subIdList){
			List<Long> list = queryDetpIdList(deptId);
			if(list.size() > 0){
				getDeptTreeList(list, deptIdList);
			}

			deptIdList.add(deptId);
		}
	}
}
