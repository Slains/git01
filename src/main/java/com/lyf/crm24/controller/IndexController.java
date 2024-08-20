package com.lyf.crm24.controller;

import com.lyf.crm24.base.BaseController;
import com.lyf.crm24.service.PermissionService;
import com.lyf.crm24.service.UserService;
import com.lyf.crm24.utils.LoginUserUtil;
import com.lyf.crm24.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private PermissionService permissionService;

    /**
     * 系统登录⻚
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "index";
    }

    // 系统界⾯欢迎⻚
    @RequestMapping("welcome")
    public String welcome(){
        return "welcome";
    }
    /**
     * 后端管理主⻚⾯
     * @return
     */
    @RequestMapping("main")
    public String main(HttpServletRequest request){
        // 通过⼯具类，从cookie中获取userId
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        // 调⽤对应Service层的⽅法，通过userId主键查询⽤户对象
        User user = userService.selectByPrimaryKey(userId);
        // 将⽤户对象设置到request作⽤域中
        request.getSession().setAttribute("user", user);
        // 登录的用户拥有自己的权限
        List<String> permissions = permissionService.queryUserHasRolesHasPermissions(userId);
        System.out.println(permissions);
        request.getSession().setAttribute("permissions",permissions);
        return "main";

    }
}
