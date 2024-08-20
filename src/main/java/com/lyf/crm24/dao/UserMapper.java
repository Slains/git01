package com.lyf.crm24.dao;

import com.lyf.crm24.base.BaseMapper;
import com.lyf.crm24.model.UserModel;
import com.lyf.crm24.vo.User;

import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User,Integer> {

    // 根据⽤户名查询⽤户对象
    User queryUserByUserName(String userName);

    // 查询所有的销售人员（用于营销机会管理的下拉框）
    List<Map<String, Object>> queryAllCustomerManager();
    /*int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);*/
}