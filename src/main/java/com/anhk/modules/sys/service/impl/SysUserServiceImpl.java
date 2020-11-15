package com.anhk.modules.sys.service.impl;


import com.anhk.common.enums.StatusEnum;
import com.anhk.common.utils.*;
import com.anhk.modules.sys.entity.SysUserRoleEntity;
import com.anhk.modules.sys.vo.UserVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anhk.common.annotation.DataFilter;
import com.anhk.modules.sys.dao.SysUserDao;
import com.anhk.modules.sys.entity.SysDeptEntity;
import com.anhk.modules.sys.entity.SysUserEntity;
import com.anhk.modules.sys.service.SysDeptService;
import com.anhk.modules.sys.service.SysUserRoleService;
import com.anhk.modules.sys.service.SysUserService;
import com.anhk.modules.sys.shiro.ShiroUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: SysUserServiceImpl
 * @Description: TODO 用户管理业务层
 * @Author: Anhk丶
 * @Date: 2020/10/29 21:33:25
 * @Version: 1.0
 */
@Service("sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUserEntity> implements SysUserService {
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysDeptService sysDeptService;

    @Override
    public List<Long> queryAllMenuId(Long userId) {
        return baseMapper.queryAllMenuId(userId);
    }

    @Override
    @DataFilter(subDept = true, user = false)
    public PageUtils queryPage(Map<String, Object> params) {
        String userName = (String) params.get("userName");

        IPage<SysUserEntity> page = this.page(
                new Query<SysUserEntity>().getPage(params),
                new QueryWrapper<SysUserEntity>()
                        .lambda().like(StringUtils.isNotBlank(userName), SysUserEntity::getUserName, userName)
                        .orderByAsc(SysUserEntity::getCreateTime)
                        .apply(params.get(Constant.SQL_FILTER) != null, (String) params.get(Constant.SQL_FILTER))
        );
        //转换对象
        List<UserVo> userVos = BeanHelper.copyWithCollection(page.getRecords(), UserVo.class);
        //判断非空
        if (!CollectionUtils.isEmpty(userVos)) {
            //遍历设置状态名称
            for (UserVo userVo : userVos) {
                if (userVo.getStatus().equals(StatusEnum.OK.getValue())) {
                    userVo.setStatuName("正常");
                } else {
                    userVo.setStatuName("禁用");
                }
                //设置部门名称
                SysDeptEntity sysDeptEntity = sysDeptService.getById(userVo.getDeptId());
                userVo.setDeptName(sysDeptEntity.getName());
            }
        }
        return new PageUtils(userVos, (int) page.getTotal(), (int) page.getSize(), (int) page.getCurrent());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(SysUserEntity user) {
        user.setCreateTime(new Date());
        //sha256加密
        String salt = RandomStringUtils.randomAlphanumeric(20);
        user.setSalt(salt);
        user.setPassword(ShiroUtils.sha256(user.getPassword(), user.getSalt()));
        this.save(user);

        //保存用户与角色关系
        sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysUserEntity user) {
        if (StringUtils.isBlank(user.getPassword())) {
            user.setPassword(null);
        } else {
            SysUserEntity userEntity = this.getById(user.getUserId());
            user.setPassword(ShiroUtils.sha256(user.getPassword(), userEntity.getSalt()));
        }
        this.updateById(user);

        //保存用户与角色关系
        sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
    }

    @Override
    public boolean updatePassword(Long userId, String password, String newPassword) {
        SysUserEntity userEntity = new SysUserEntity();
        userEntity.setPassword(newPassword);
        return this.update(userEntity,
                new QueryWrapper<SysUserEntity>().eq("user_id", userId).eq("password", password));
    }

    /**
     * 导出用户数据到Excel
     *
     * @param userName
     * @return
     */
    @Override
    public List<UserVo> exportToExcel(String userName) {
        QueryWrapper<SysUserEntity> queryWrapper = new QueryWrapper<>();
        //判断是否含有查询条件
        if (StringUtils.isNotBlank(userName)) {
            queryWrapper.lambda().eq(SysUserEntity::getUserName, userName);
        }
        //根据创建时间进行升序
        queryWrapper.lambda().orderByAsc(SysUserEntity::getCreateTime);
        //查询
        List<SysUserEntity> userList = this.list(queryWrapper);
        //转换对象
        List<UserVo> userVos = BeanHelper.copyWithCollection(userList, UserVo.class);
        //遍历设置状态名称
        if (!CollectionUtils.isEmpty(userVos)) {
            for (UserVo userVo : userVos) {
                if (userVo.getStatus().equals(StatusEnum.OK.getValue())) {
                    userVo.setStatuName("正常");
                } else {
                    userVo.setStatuName("锁定");
                }
                SysDeptEntity sysDeptEntity = sysDeptService.getById(userVo.getDeptId());
                userVo.setDeptName(sysDeptEntity.getName());
            }
        }
        return userVos;
    }

    /**
     * 转换用户状态
     *
     * @param userId
     * @param status
     * @return
     */
    @Override
    public ResultVo convertStatu(Long userId, Integer status) {
        if (userId == null) {
            return ResultVo.error("用户ID不能为空");
        }
        if (status == null) {
            return ResultVo.error("状态码不能为空");
        }
        if (!StatusEnum.contains(status)) {
            return ResultVo.error("状态码不正确");
        }
        SysUserEntity user = this.getById(userId);
        user.setStatus(status);
        boolean update = this.updateById(user);
        if (!update) {
            return ResultVo.error("切换用户状态失败");
        }
        return ResultVo.ok();
    }

    /**
     * 根据角色ID查询用户信息
     * @param params
     * @return
     */
    @Override
    public PageUtils queryUserRole(Map<String, Object> params) {
        String userName = (String) params.get("userName");
        String roleId = (String) params.get("roleId");
        Page<SysUserEntity> page = new Page<>( Long.parseLong(String.valueOf(params.get("page"))), Long.parseLong(String.valueOf(params.get("limit"))));
        QueryWrapper<SysUserEntity> userQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(userName)) {
            userQueryWrapper.lambda().like(SysUserEntity::getUserName, userName);
        }
        QueryWrapper<SysUserRoleEntity> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper.lambda().eq(SysUserRoleEntity::getRoleId, roleId);
        List<SysUserRoleEntity> userRoleList = sysUserRoleService.list(userRoleQueryWrapper);
        //判断是否含有用户信息
        if (CollectionUtils.isEmpty(userRoleList)) {
            return null;
        }
        ArrayList<Long> userIds = new ArrayList<>();
        for (SysUserRoleEntity entity : userRoleList) {
            userIds.add(entity.getUserId());
        }
        userQueryWrapper.lambda().in(SysUserEntity::getUserId, userIds);
        IPage<SysUserEntity> iPage = this.page(page, userQueryWrapper);
        //转换对象
        List<UserVo> userVos = BeanHelper.copyWithCollection(iPage.getRecords(), UserVo.class);
        //判断非空
        if (!CollectionUtils.isEmpty(userVos)) {
            //遍历设置状态名称
            for (UserVo userVo : userVos) {
                if (userVo.getStatus().equals(StatusEnum.OK.getValue())) {
                    userVo.setStatuName("正常");
                } else {
                    userVo.setStatuName("禁用");
                }
                //设置部门名称
                SysDeptEntity sysDeptEntity = sysDeptService.getById(userVo.getDeptId());
                userVo.setDeptName(sysDeptEntity.getName());
            }
        }
        return new PageUtils(userVos, (int) page.getTotal(), (int) page.getSize(), (int) page.getCurrent());
    }

}
