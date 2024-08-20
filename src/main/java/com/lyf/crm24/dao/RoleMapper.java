package com.lyf.crm24.dao;

import com.lyf.crm24.base.BaseMapper;
import com.lyf.crm24.vo.Role;

import java.util.List;
import java.util.Map;

public interface RoleMapper extends BaseMapper<Role, Integer> {

    // 查询⻆⾊列表,只需要id和username所以用了map
    public List<Map<String,Object>> queryAllRoles(Integer userId);

    Role queryRoleByRoleName(String roleName);
}