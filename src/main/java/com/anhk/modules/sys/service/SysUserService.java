package com.anhk.modules.sys.service;

import com.anhk.common.utils.PageUtils;
import com.anhk.common.utils.ResultVo;
import com.anhk.modules.sys.entity.SysUserEntity;
import com.anhk.modules.sys.vo.UserVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;


/**
 * 系统用户
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface SysUserService extends IService<SysUserEntity> {

	PageUtils queryPage(Map<String, Object> params);
	
	/**
	 * 查询用户的所有菜单ID
	 */
	List<Long> queryAllMenuId(Long userId);
	
	/**
	 * 保存用户
	 */
	ResultVo saveUser(SysUserEntity user);
	
	/**
	 * 修改用户
	 */
	ResultVo update(SysUserEntity user);

	/**
	 * 修改密码
	 * @param userId       用户ID
	 * @param password     原密码
	 * @param newPassword  新密码
	 */
	boolean updatePassword(Long userId, String password, String newPassword);

	/**
	 * 导出用户数据到Excel
	 * @param userName
	 * @return
	 */
    List<UserVo> exportToExcel(String userName);

	/**
	 * 转换用户状态
	 * @param userId
	 * @param status
	 * @return
	 */
	ResultVo convertStatu(Long userId, Integer status);

	PageUtils queryUserRole(Map<String, Object> params);
}
