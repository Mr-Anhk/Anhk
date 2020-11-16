package com.anhk.modules.sys.service.impl;


import com.anhk.common.annotation.DataFilter;
import com.anhk.common.enums.StatusEnum;
import com.anhk.common.utils.*;
import com.anhk.modules.sys.dao.SysUserDao;
import com.anhk.modules.sys.entity.SysDeptEntity;
import com.anhk.modules.sys.entity.SysUserEntity;
import com.anhk.modules.sys.entity.SysUserRoleEntity;
import com.anhk.modules.sys.service.SysDeptService;
import com.anhk.modules.sys.service.SysUserRoleService;
import com.anhk.modules.sys.service.SysUserService;
import com.anhk.modules.sys.shiro.ShiroUtils;
import com.anhk.modules.sys.vo.UserVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

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

    /**
     * 分页查询用户信息
     *
     * @param params
     * @return
     */
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

    /**
     * 新增用户
     *
     * @param user
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVo saveUser(SysUserEntity user) {
        //判断用户名的长度
        if (!RegexUtils.validateUserName(user.getUserName())) {
            return ResultVo.error("用户名长度不能超过20个字符（一个汉字为2个字符）");
        }
        //判断密码是否符合复杂度
        if (!RegexUtils.checkPassWord(user.getPassword())) {
            return ResultVo.error("密码需要同时包含数字、字母以及特殊符号");
        }
        if (StringUtils.isNotBlank(user.getMobile()) && !RegexUtils.isPhone(user.getMobile())) {
            return ResultVo.error("手机号格式不正确");
        }
        user.setCreateTime(new Date());
        //sha256加密
        String salt = RandomStringUtils.randomAlphanumeric(20);
        user.setSalt(salt);
        user.setPassword(ShiroUtils.sha256(user.getPassword(), user.getSalt()));
        boolean save = this.save(user);
        if (!save) {
            return ResultVo.error("新增用户失败");
        }
        //保存用户与角色关系
        sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
        return ResultVo.ok();
    }

    /**
     * 修改用户
     *
     * @param user
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVo update(SysUserEntity user) {
        //判断用户名的长度
        if (RegexUtils.getStrLength(user.getUserName()) > 50) {
            return ResultVo.error("用户名长度不能超过50个字符（一个汉字为2个字符）");
        }
        if (StringUtils.isNotBlank(user.getMobile()) && !RegexUtils.isPhone(user.getMobile())) {
            return ResultVo.error("手机号格式不正确");
        }
        //根据ID查询用户信息
        SysUserEntity entity = this.getById(user.getUserId());
        entity.setUserName(user.getUserName());
        entity.setDeptId(user.getDeptId());
        entity.setStatus(user.getStatus());
        entity.setEmail(user.getEmail());
        entity.setMobile(user.getMobile());
        entity.setRoleIdList(user.getRoleIdList());
        boolean update = this.updateById(entity);
        if (!update) {
            return ResultVo.error("更新用户失败");
        }
        //保存用户与角色关系
        sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
        return ResultVo.ok();
    }

    /**
     * 修改密码
     * @param userId       用户ID
     * @param password     原密码
     * @param newPassword  新密码
     * @return
     */
    @Override
    public boolean updatePassword(Long userId, String password, String newPassword) {
        SysUserEntity userEntity = new SysUserEntity();
        userEntity.setPassword(newPassword);
        return this.update(userEntity,
                new QueryWrapper<SysUserEntity>()
                        .lambda().eq(SysUserEntity::getUserId, userId)
                                 .eq(SysUserEntity::getPassword, password));
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
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryUserRole(Map<String, Object> params) {
        String userName = (String) params.get("userName");
        String roleId = (String) params.get("roleId");
        Page<SysUserEntity> page = new Page<>(Long.parseLong(String.valueOf(params.get("page"))), Long.parseLong(String.valueOf(params.get("limit"))));
        QueryWrapper<SysUserEntity> userQueryWrapper = new QueryWrapper<>();
        //判断是否含有查询条件
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
        //根据创建时间进行升序
        userQueryWrapper.lambda().orderByAsc(SysUserEntity::getCreateTime);
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

    /**
     * 测试稀疏数组
     *
     * @param args
     */
    public static void main(String[] args) {
        int[][] arr1 = new int[11][11];
        arr1[1][2] = 1;
        arr1[2][3] = 2;
        //定义变量，获取有效值的个数
        int count = 0;
        System.out.println("原数组");
        for (int[] ints : arr1) {
            for (int anInt : ints) {
                //判断
                if (anInt != 0) {
                    count++;
                }
                System.out.print(anInt + "\t");
            }
            System.out.println();
        }
        System.out.println("==========================");
        //定义稀疏数组
        int[][] arr2 = new int[count + 1][3];
        //设置稀疏数组的头信息
        arr2[0][0] = 11;
        arr2[0][1] = 11;
        arr2[0][2] = count;
        int sum = 0;
        for (int i = 0; i < arr1.length; i++) {
            for (int j = 0; j < arr1[i].length; j++) {
                if (arr1[i][j] != 0) {
                    sum++;
                    arr2[sum][0] = i;
                    arr2[sum][1] = j;
                    arr2[sum][2] = arr1[i][j];
                }
            }
        }
        System.out.println("稀疏数组");
        for (int[] ints : arr2) {
            System.out.print(ints[0] + "\t" + ints[1] + "\t" + ints[2] + "\t");
            System.out.println();
        }
        System.out.println("==========================");
        //通过稀疏数组还原原来的数组
        System.out.println("稀疏数组还原为原先的数组");
        int[][] arr3 = new int[arr2[0][0]][arr2[0][1]];
        for (int i = 1; i < arr2.length; i++) {
            arr3[arr2[i][0]][arr2[i][1]] = arr2[i][2];
        }
        for (int i = 0; i < arr3.length; i++) {
            for (int j = 0; j < arr3[i].length; j++) {
                System.out.print(arr3[i][j] + "\t");
            }
            System.out.println();
        }
    }
}
