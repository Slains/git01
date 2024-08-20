package com.lyf.crm24.dao;

import com.lyf.crm24.base.BaseMapper;
import com.lyf.crm24.vo.UserRole;

public interface UserRoleMapper extends BaseMapper<UserRole, Integer> {

    Integer countUserRoleByUserId(Integer userId);

    // 删除用户角色记录
    Integer deleteUserRoleByUserId(Integer userId);
}