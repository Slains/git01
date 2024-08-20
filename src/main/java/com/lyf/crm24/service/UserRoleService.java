package com.lyf.crm24.service;

import com.lyf.crm24.base.BaseService;
import com.lyf.crm24.dao.UserRoleMapper;
import com.lyf.crm24.vo.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleService extends BaseService<UserRole, Integer> {

    @Autowired
    private UserRoleMapper userRoleMapper;


}
