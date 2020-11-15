package com.anhk.modules.sys.controller;

import com.anhk.common.utils.Constant;
import com.anhk.common.utils.ResultVo;
import com.anhk.modules.sys.entity.SysDeptEntity;
import com.anhk.modules.sys.entity.SysUserEntity;
import com.anhk.modules.sys.service.SysDeptService;
import com.anhk.modules.sys.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;


/**
 * 部门管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/sys/dept")
public class SysDeptController extends AbstractController {
    @Autowired
    private SysDeptService sysDeptService;

    @Autowired
    private SysUserService userService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:dept:list")
    public List<SysDeptEntity> list() {
        return sysDeptService.queryList(new HashMap<String, Object>());
    }

    /**
     * 选择部门(添加、修改菜单)
     */
    @RequestMapping("/select")
    @RequiresPermissions("sys:dept:select")
    public ResultVo select() {
        List<SysDeptEntity> deptList = sysDeptService.queryList(new HashMap<String, Object>());

        //添加一级部门
        if (getUserId() == Constant.SUPER_ADMIN) {
            SysDeptEntity root = new SysDeptEntity();
            root.setDeptId(0L);
            root.setName("一级部门");
            root.setParentId(-1L);
            root.setOpen(true);
            deptList.add(root);
        }

        return ResultVo.ok().put("deptList", deptList);
    }

    /**
     * 上级部门Id(管理员则为0)
     */
    @RequestMapping("/info")
    @RequiresPermissions("sys:dept:list")
    public ResultVo info() {
        long deptId = 0;
        if (getUserId() != Constant.SUPER_ADMIN) {
            List<SysDeptEntity> deptList = sysDeptService.queryList(new HashMap<String, Object>());
            Long parentId = null;
            for (SysDeptEntity sysDeptEntity : deptList) {
                if (parentId == null) {
                    parentId = sysDeptEntity.getParentId();
                    continue;
                }

                if (parentId > sysDeptEntity.getParentId().longValue()) {
                    parentId = sysDeptEntity.getParentId();
                }
            }
            deptId = parentId;
        }

        return ResultVo.ok().put("deptId", deptId);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{deptId}")
    @RequiresPermissions("sys:dept:info")
    public ResultVo info(@PathVariable("deptId") Long deptId) {
        SysDeptEntity dept = sysDeptService.getById(deptId);

        return ResultVo.ok().put("dept", dept);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:dept:save")
    public ResultVo save(@RequestBody SysDeptEntity dept) {
        sysDeptService.save(dept);

        return ResultVo.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:dept:update")
    public ResultVo update(@RequestBody SysDeptEntity dept) {
        //获取此部门的所有子级部门ID
        List<Long> subDeptIdList = sysDeptService.getSubDeptIdList(dept.getDeptId());
        //判断，上级部门是否是自己的子级部门
        if (subDeptIdList.contains(dept.getParentId()) || dept.getDeptId().equals(dept.getParentId())) {
            return ResultVo.error("上级部门不能为自己或自己的子级部门");
        }
        boolean update = sysDeptService.updateById(dept);
        if (!update) {
            return ResultVo.error("更新部门信息失败");
        }
        return ResultVo.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:dept:delete")
    public ResultVo delete(long deptId) {
        //判断你该部门下是否含有用户
        QueryWrapper<SysUserEntity> queryWrapper = new QueryWrapper<SysUserEntity>();
        queryWrapper.lambda().eq(SysUserEntity::getDeptId, deptId);
        List<SysUserEntity> userList = userService.list(queryWrapper);
        //判断
        if (!CollectionUtils.isEmpty(userList)) {
            return ResultVo.error("该部门下含有用户信息，不可删除");
        }
        //判断是否有子部门
        List<Long> deptList = sysDeptService.queryDetpIdList(deptId);
        if (deptList.size() > 0) {
            return ResultVo.error("请先删除子部门");
        }

        boolean remove = sysDeptService.removeById(deptId);
        if (!remove) {
            return ResultVo.error("删除部门信息失败");
        }

        return ResultVo.ok();
    }

}
