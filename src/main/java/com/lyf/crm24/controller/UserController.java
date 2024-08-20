package com.lyf.crm24.controller;

import com.lyf.crm24.base.BaseController;
import com.lyf.crm24.base.ResultInfo;
import com.lyf.crm24.model.UserModel;
import com.lyf.crm24.query.UserQuery;
import com.lyf.crm24.service.UserService;
import com.lyf.crm24.utils.LoginUserUtil;
import com.lyf.crm24.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {

    @Autowired
    UserService userService;


    // 营销机会添加界面，指派给的下拉框，获取所有销售人员
    @RequestMapping("queryAllCustomerManager")
    @ResponseBody
    public List<Map<String, Object>> queryAllCustomerManager() {
        List<Map<String, Object>> maps = userService.queryAllCustomerManager();
        return maps;
    }

    /**
     * 删除⽤户
     * @param ids
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteUser(Integer[] ids){
        userService.deleteBatch(ids);
        return success("⽤户记录删除成功");
    }


    /**
     * 进⼊⽤户添加或更新⻚⾯
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("addOrUpdateUserPage")
    public String addUserPage(Integer id, Model model){
        // 主键存在，执行更新操作
        if(null != id){
            model.addAttribute("user1",userService.selectByPrimaryKey(id));
        }
        return "user/add_update";
    }

    /**
     * 更新⽤户
     * @param user
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateUser(User user) {
        userService.updateUser(user);
        return success("⽤户更新成功！");
    }

    /**
     * 添加⽤户
     * @param user
     * @return
     */
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveUser(User user) {
        userService.saveUser(user);
        return success("⽤户添加成功！");
    }


    /**
     * 进⼊⽤户⻚⾯
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "user/user";
    }

    /**
     * 多条件查询⽤户数据
     * @param userQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryUserByParams(UserQuery userQuery) {
        return userService.queryUserByParams(userQuery);
    }

    // 视图转发方法
    @RequestMapping("toPasswordPage")
    public String toPasswordPage(){
        return "user/password";
    }

    @PostMapping("updatePassword")
    @ResponseBody
    public ResultInfo updateUserPassword (HttpServletRequest request, String oldPassword, String newPassword, String confirmPassword) {
        ResultInfo resultInfo = new ResultInfo();

        // 获取userId
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        // 调⽤Service层的密码修改⽅法
        userService.updateUserPassword(userId, oldPassword, newPassword, confirmPassword);
        /*// 通过 try catch 捕获 Service 层抛出的异常
        try {
            // 获取userId
            Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
            // 调⽤Service层的密码修改⽅法
            userService.updateUserPassword(userId, oldPassword, newPassword, confirmPassword);
        } catch (ParamsException e) { // ⾃定义异常
            e.printStackTrace();
            // 设置状态码和提示信息
            resultInfo.setCode(e.getCode());
            resultInfo.setMsg(e.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            resultInfo.setCode(500);
            resultInfo.setMsg("操作失败！");
        }*/
        return resultInfo;
    }


            /**
             * ⽤户登录
             * @param userName
             * @param userPwd
             * @return
             */
    @PostMapping("login")
    @ResponseBody
    public ResultInfo userLogin (String userName, String userPwd) {
        ResultInfo resultInfo = new ResultInfo();

        UserModel userModel = userService.userLogin(userName, userPwd);
        resultInfo.setResult(userModel);
        /*// 通过 try catch 捕获 Service 层抛出的异常
        try {
            // 调⽤Service层的登录⽅法，得到返回的⽤户对象
            UserModel userModel = userService.userLogin(userName, userPwd);
            *//**
             * 登录成功后，有两种处理：
             * 1. 将⽤户的登录信息存⼊ Session （ 问题：重启服务器，Session 失效，客户端需要重复登录 ）
             * 2. 将⽤户信息返回给客户端，由客户端（Cookie）保存
             *//*
            // 将返回的UserModel对象设置到 ResultInfo 对象中
            resultInfo.setResult(userModel);
        } catch (ParamsException e) { // ⾃定义异常
            e.printStackTrace();
            // 设置状态码和提示信息
            resultInfo.setCode(e.getCode());
            resultInfo.setMsg(e.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            resultInfo.setCode(500);
            resultInfo.setMsg("操作失败！");
        }*/
        return resultInfo;
    }

}
