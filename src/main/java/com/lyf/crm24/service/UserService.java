package com.lyf.crm24.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lyf.crm24.base.BaseService;
import com.lyf.crm24.dao.UserMapper;
import com.lyf.crm24.dao.UserRoleMapper;
import com.lyf.crm24.model.UserModel;
import com.lyf.crm24.query.UserQuery;
import com.lyf.crm24.utils.AssertUtil;
import com.lyf.crm24.utils.Md5Util;
import com.lyf.crm24.utils.PhoneUtil;
import com.lyf.crm24.utils.UserIDBase64;
import com.lyf.crm24.vo.User;
import com.lyf.crm24.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserService extends BaseService<User, Integer> {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    private void relationUserRole(int userId, String roleIds) {
        /**
         * ⽤户⻆⾊关联
         * 添加操作
         *      原始⻆⾊不存在
         *          1.不添加新的角色记录   不操作用户角色表
         *          2.添加新的⻆⾊记录    给指定用户绑定相关的角色记录
         * 更新操作
         *      原始角色不存在
         *          1.不添加新的角色记录   不操作用户角色表
         *          2.添加新的⻆⾊记录    给指定用户绑定相关的角色记录
         *      原始⻆⾊存在
         *          1.添加新的⻆⾊记录  判断已有的角色记录不添加，添加没有的角色记录
         *          2.清空所有的角色记录 删除用户绑定的角色记录
         *          3.删除一部分角色记录 删除数据库中不存在的角色记录，存在的角色记录保留
         *          4.添加一些，删除一些
         * 如何进⾏⻆⾊分配???
         * 如果⽤户原始⻆⾊存在 ⾸先清空原始所有⻆⾊ 添加新的⻆⾊记录到⽤户⻆⾊表
         */
        // 通过用户ID查询角色记录
        int count = userRoleMapper.countUserRoleByUserId(userId);
        // 查询到则先执行删除操作
        if (count > 0) {
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count, "⽤户⻆⾊分配失败!");
        }
        if (StringUtils.isNotBlank(roleIds)) {
            //重新添加新的⻆⾊
            List<UserRole> userRoles = new ArrayList<UserRole>();
            for (String s : roleIds.split(",")) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(Integer.parseInt(s));
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());
                userRoles.add(userRole);
            }
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoles) < userRoles.size(), "⽤户⻆⾊分配失败!");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(Integer userId) {
        User user = selectByPrimaryKey(userId);
        AssertUtil.isTrue(null == userId || null == user, "待删除记录不存在!");
        // 判断⽤户是否绑定了⻆⾊信息
        int count = userRoleMapper.countUserRoleByUserId(userId);
        // 如果绑定了⻆⾊信息则删除对应的数据
        if (count > 0) {
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count, "⽤户⻆⾊删除 失败!");
        }
        user.setIsValid(0);
        AssertUtil.isTrue(updateByPrimaryKeySelective(user) < 1, "⽤户记录删除失败!");
    }

    /**
     * 删除⽤户
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUserByIds(Integer[] ids) {
        AssertUtil.isTrue(null==ids || ids.length == 0,"请选择待删除的⽤户记录!");
        AssertUtil.isTrue(deleteBatch(ids) != ids.length,"⽤户记录删除失败!");
    }
    /**
     * 更新⽤户
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user) {
        // 1. 参数校验
        // 通过id查询⽤户对象
        User temp = userMapper.selectByPrimaryKey(user.getId());
        // 判断对象是否存在
        AssertUtil.isTrue(temp == null, "待更新记录不存在！");
        // 验证参数
        checkParams(user.getUserName(),user.getEmail(),user.getPhone(),user.getId());
        // 2. 设置默认参数
        temp.setUpdateDate(new Date());
        // 3. 执⾏更新，判断结果
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1, "⽤户更新失败！");

        relationUserRole(user.getId(), user.getRoleIds());
    }

    /**
     * 添加⽤户
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUser(User user) {
        // 1. 参数校验
        checkParams(user.getUserName(), user.getEmail(), user.getPhone(), null);
        // 2. 设置默认参数
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setUserPwd(Md5Util.encode("123456"));
        // 3. 执⾏添加，判断结果
        AssertUtil.isTrue(userMapper.insertSelective(user) == null, "⽤户添加失败！");

        // 给用户分配角色
        relationUserRole(user.getId(), user.getRoleIds());
    }
    /**
     * 参数校验
     * @param userName
     * @param email
     * @param phone
     */
    private void checkParams(String userName, String email, String phone, Integer userId) {
        AssertUtil.isTrue(StringUtils.isBlank(userName), "⽤户名不能为空！");
        // 验证⽤户名是否存在
        User temp = userMapper.queryUserByUserName(userName);
        // 如果是添加操作，数据库是没有数据的，数据库中只要查询到⽤户记录就表示不可⽤
        // 如果是修改操作，数据库是有数据的，查询到⽤户记录就是当前要修改的记录本身就表示可⽤，否则不可⽤
        // 数据存在，且不是当前要修改的⽤户记录，则表示其他⽤户占⽤了该⽤户名
        AssertUtil.isTrue(null != temp && !(temp.getId().equals(userId)), "该⽤户已存在！");
        AssertUtil.isTrue(StringUtils.isBlank(email), "请输⼊邮箱地址！");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone), "⼿机号码格式不正确！");
    }

    /**
     * 多条件分⻚查询⽤户数据
     * @param query
     * @return
     */
    public Map<String, Object> queryUserByParams (UserQuery query) {
        Map<String, Object> map = new HashMap<>();
        PageHelper.startPage(query.getPage(), query.getLimit());
        PageInfo<User> pageInfo = new PageInfo<>(userMapper.selectByParams(query));
        map.put("code",0);
        map.put("msg", "");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());
        return map;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserPassword (Integer userId, String oldPassword,
                                    String newPassword, String confirmPassword ) {
        // 通过userId获取⽤户对象
        User user = userMapper.selectByPrimaryKey(userId);
        // 1. 参数校验
        checkPasswordParams(user, oldPassword, newPassword, confirmPassword);
        // 2. 设置⽤户新密码
        user.setUserPwd(Md5Util.encode(newPassword));
        // 3. 执⾏更新操作
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1, "⽤户密码更新失败！");
    }

    private void checkPasswordParams(User user, String oldPassword,
                                     String newPassword, String confirmPassword) {
        // user对象 ⾮空验证
        AssertUtil.isTrue(null == user, "⽤户未登录或不存在！");
        // 原始密码 ⾮空验证
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword), "请输⼊原始密码！");
        // 原始密码要与数据库中的密⽂密码保持⼀致
        AssertUtil.isTrue(!(user.getUserPwd().equals(Md5Util.encode(oldPassword))), "原始密码不正确！");
        // 新密码 ⾮空校验
        AssertUtil.isTrue(StringUtils.isBlank(newPassword), "请输⼊新密码！");
        // 新密码与原始密码不能相同
        AssertUtil.isTrue(oldPassword.equals(newPassword), "新密码不能与原始密码相同！");
        // 确认密码 ⾮空校验
        AssertUtil.isTrue(StringUtils.isBlank(confirmPassword), "请输⼊确认密码！");
        // 新密码要与确认密码保持⼀致
        AssertUtil.isTrue(!(newPassword.equals(confirmPassword)), "新密码与确认密码不⼀致！");
    }

    /**
     * ⽤户登录
     * @param userName
     * @param userPwd
     * @return
     */
    public UserModel userLogin(String userName, String userPwd) {
        // 1. 验证参数
        checkLoginParams(userName, userPwd);
        // 2. 根据⽤户名，查询⽤户对象
        User user = userMapper.queryUserByUserName(userName);
        // 3. 判断⽤户是否存在 (⽤户对象为空，记录不存在，⽅法结束)
        AssertUtil.isTrue(null == user, "⽤户不存在或已注销！");
        // 4. ⽤户对象不为空（⽤户存在，校验密码。密码不正确，⽅法结束）
        checkLoginPwd(userPwd, user.getUserPwd());
        // 5. 密码正确（⽤户登录成功，返回⽤户的相关信息）
        return buildUserInfo(user);
    }
    /**
     * 构建返回的⽤户信息
     * @param user
     * @return
     */
    private UserModel buildUserInfo(User user) {
        UserModel userModel = new UserModel();
        // 设置⽤户信息
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }
    /**
     * 验证登录密码
     * @param userPwd 前台传递的密码
     * @param upwd 数据库中查询到的密码
     * @return
     */
    private void checkLoginPwd(String userPwd, String upwd) {
        // 数据库中的密码是经过加密的，将前台传递的密码先加密，再与数据库中的密码作⽐较
        userPwd = Md5Util.encode(userPwd);
        // ⽐较密码
        AssertUtil.isTrue(!userPwd.equals(upwd), "⽤户密码不正确！");
    }
    /**
     * 验证⽤户登录参数
     * @param userName
     * @param userPwd
     */
    private void checkLoginParams(String userName, String userPwd) {
        // 判断姓名
        AssertUtil.isTrue(StringUtils.isBlank(userName), "⽤户姓名不能为空！");
        // 判断密码
        AssertUtil.isTrue(StringUtils.isBlank(userPwd), "⽤户密码不能为空！");
    }

    public List<Map<String, Object>> queryAllCustomerManager() {
        List<Map<String, Object>> userList = userMapper.queryAllCustomerManager();

        return userList;
    }
}
