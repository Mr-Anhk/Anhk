package com.anhk.modules.sys.controller;


import com.anhk.common.annotation.SysLog;
import com.anhk.common.utils.ExcelUtils;
import com.anhk.common.utils.PageUtils;
import com.anhk.common.utils.ResultVo;
import com.anhk.common.validator.Assert;
import com.anhk.common.validator.ValidatorUtils;
import com.anhk.common.validator.group.AddGroup;
import com.anhk.common.validator.group.UpdateGroup;
import com.anhk.modules.sys.entity.SysUserEntity;
import com.anhk.modules.sys.service.SysUserRoleService;
import com.anhk.modules.sys.service.SysUserService;
import com.anhk.modules.sys.shiro.ShiroUtils;
import com.anhk.modules.sys.vo.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.ArrayUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 系统用户
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/sys/user")
@Api("用户管理")
public class SysUserController extends AbstractController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserRoleService sysUserRoleService;

    /**
     * 所有用户列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:user:list")
    public ResultVo list(@RequestParam Map<String, Object> params) {
        PageUtils page = sysUserService.queryPage(params);
        return ResultVo.ok().put("page", page);
    }

    /**
     * 获取登录的用户信息
     */
    @RequestMapping("/info")
    public ResultVo info() {
        return ResultVo.ok().put("user", getUser());
    }

    /**
     * 修改登录用户密码
     */
    @SysLog("修改密码")
    @RequestMapping("/password")
    @ApiOperation(value = "修改密码")
    public ResultVo password(String password, String newPassword) {
        Assert.isBlank(newPassword, "新密码不为能空");
        //原密码
        password = ShiroUtils.sha256(password, getUser().getSalt());
        //新密码
        newPassword = ShiroUtils.sha256(newPassword, getUser().getSalt());
        //更新密码
        boolean flag = sysUserService.updatePassword(getUserId(), password, newPassword);
        if (!flag) {
            return ResultVo.error("原密码不正确");
        }
        return ResultVo.ok();
    }

    /**
     * 用户信息
     */
    @RequestMapping("/info/{userId}")
    @RequiresPermissions("sys:user:info")
    public ResultVo info(@PathVariable("userId") Long userId) {
        SysUserEntity user = sysUserService.getById(userId);
        //获取用户所属的角色列表
        List<Long> roleIdList = sysUserRoleService.queryRoleIdList(userId);
        user.setRoleIdList(roleIdList);
        return ResultVo.ok().put("user", user);
    }

    /**
     * 保存用户
     */
    @SysLog("保存用户")
    @RequestMapping("/save")
    @RequiresPermissions("sys:user:save")
    public ResultVo save(@RequestBody SysUserEntity user) {
        ValidatorUtils.validateEntity(user, AddGroup.class);
        return sysUserService.saveUser(user);
    }

    /**
     * 修改用户
     */
    @SysLog("修改用户")
    @RequestMapping("/update")
    @RequiresPermissions("sys:user:update")
    public ResultVo update(@RequestBody SysUserEntity user) {
        ValidatorUtils.validateEntity(user, UpdateGroup.class);
        return sysUserService.update(user);
    }

    /**
     * 删除用户
     */
    @SysLog("删除用户")
    @RequestMapping("/delete")
    @RequiresPermissions("sys:user:delete")
    public ResultVo delete(@RequestBody Long[] userIds) {
        if (ArrayUtils.contains(userIds, 1L)) {
            return ResultVo.error("系统管理员不能删除");
        }
        if (ArrayUtils.contains(userIds, getUserId())) {
            return ResultVo.error("当前用户不能删除");
        }
        boolean remove = sysUserService.removeByIds(Arrays.asList(userIds));
        if (!remove) {
            return ResultVo.error("删除用户信息失败");
        }
        return ResultVo.ok();
    }

    /**
     * 导出用户数据到Excel
     *
     * @param headerNames
     * @param headerFields
     * @param userName
     * @param response
     * @throws IOException
     */
    @SysLog("导出用户数据到Excel")
    @RequestMapping("/export")
    @RequiresPermissions("sys:user:export")
    public void exportToExcel(String headerNames, String headerFields, String userName, HttpServletResponse response) throws IOException {
        //创建标题名称集合
        String[] nameArr = headerNames.split(",");
        List<String> names = Arrays.asList(nameArr);
        String[] fieldArr = headerFields.split(",");
        List<String> fields = Arrays.asList(fieldArr);
        //根据条件查询用户信息
        List<UserVo> userVos = sysUserService.exportToExcel(userName);
        //创建集合
        ArrayList<Object> list = new ArrayList<>();
        list.addAll(userVos);
        //导出
        ExcelUtils.exportBeanToExcel("用户信息表", names, fields, list, response);
    }

    /**
     * 切换用户状态
     *
     * @param userEntity
     * @return
     */
    @SysLog("切换用户状态")
    @RequestMapping("/convertStatu")
    @RequiresPermissions("sys:user:convertStatu")
    public ResultVo convertStatu(@RequestBody SysUserEntity userEntity) {
        return sysUserService.convertStatu(userEntity.getUserId(), userEntity.getStatus());
    }

    /**
     * 根据角色ID查询用户信息
     *
     * @param params
     * @return
     */
    @RequestMapping("/userRole")
    public ResultVo queryUserRole(@RequestParam Map<String, Object> params) {
        PageUtils page = sysUserService.queryUserRole(params);
        return ResultVo.ok().put("page", page);
    }
}
