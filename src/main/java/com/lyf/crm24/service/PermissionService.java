package com.lyf.crm24.service;

import com.lyf.crm24.base.BaseService;
import com.lyf.crm24.dao.PermissionMapper;
import com.lyf.crm24.vo.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService extends BaseService<Permission,Integer> {
    @Autowired
    private PermissionMapper permissionMapper;

    public List<String> queryUserHasRolesHasPermissions(Integer userId) {
        return permissionMapper.queryUserHasRolesHasPermissions(userId);
    }
}
