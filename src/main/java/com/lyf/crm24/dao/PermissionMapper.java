package com.lyf.crm24.dao;

import com.lyf.crm24.base.BaseMapper;
import com.lyf.crm24.vo.Permission;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission, Integer> {

    Integer countPermissionByRoleId(Integer roleId);

    Integer deletePermissionsByRoleId(Integer roleId);

    // 管理角色的菜单（资源）权限
    List<Integer> queryRoleHasAllModuleIdsByRoleId(Integer roleId);

    // 用户进入主页显示的菜单栏
    List<String> queryUserHasRolesHasPermissions(Integer userId);

    // 根据资源id查询权限记录
    Integer countPermissionsByModuleId(Integer mid);

    // 根据资源id删除权限记录
    Integer deletePermissionsByModuleId(Integer mid);
}