package com.lyf.crm24.controller;

import com.lyf.crm24.annotations.RequirePermission;
import com.lyf.crm24.base.BaseController;
import com.lyf.crm24.base.ResultInfo;
import com.lyf.crm24.query.SaleChanceQuery;
import com.lyf.crm24.service.SaleChanceService;
import com.lyf.crm24.utils.CookieUtil;
import com.lyf.crm24.utils.LoginUserUtil;
import com.lyf.crm24.vo.SaleChance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {

    @Autowired
    private SaleChanceService saleChanceService;

    /**
     * 机会数据添加与更新⻚⾯视图转发
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("addOrUpdateSaleChancePage")
    public String addOrUpdateSaleChancePage(Integer id, Model model){
        if (null != id) {
            model.addAttribute("saleChance", saleChanceService.selectByPrimaryKey(id));
        }
        return "saleChance/add_update";
    }

    /**
     * 更新营销机会的开发状态
     * @param id
     * @param devResult
     * @return
     */
    @RequestMapping("updateSaleChanceDevResult")
    @ResponseBody
    public ResultInfo updateSaleChanceDevResult(Integer id,Integer devResult){
        saleChanceService.updateSaleChanceDevResult(id,devResult);
        return success("开发状态更新成功！");
    }

    /**
     * 进⼊营销机会⻚⾯ 1010
     * @return
     */
    @RequirePermission(code = "1010")
    @RequestMapping("index")
    public String index () {
        return "saleChance/sale_chance";
    }
    /**
     * 多条件分⻚查询营销机会  101001
     * @param query
     * @return
     */
    @RequirePermission(code = "101001")
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> querySaleChanceByParams (SaleChanceQuery query, Integer flag,
                                                        HttpServletRequest request) {

        // 查询参数 flag=1 代表当前查询为开发计划数据，设置查询分配⼈参数
        if (null != flag && flag == 1) {
            // 获取当前登录⽤户的ID
            Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
            query.setAssignMan(userId);
        }
        // flag不为1，查询营销计划
        return saleChanceService.querySaleChanceByParams(query);
    }

    /**
     * 添加营销机会数据
     * @param request
     * @param saleChance
     * @return
     */
    @RequirePermission(code = "101002")
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveSaleChance(HttpServletRequest request, SaleChance saleChance){
        // 从cookie中获取⽤户姓名
        String userName = CookieUtil.getCookieValue(request, "userName");
        // 设置营销机会的创建⼈
        saleChance.setCreateMan(userName);
        // 添加营销机会的数据
        saleChanceService.saveSaleChance(saleChance);
        return success("营销机会数据添加成功！");
    }

    /**
     * 删除营销机会数据 101003
     * @param ids
     * @return
     */
    @RequirePermission(code = "101003")
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteSaleChance (Integer[] ids) {
        // 删除营销机会的数据
        saleChanceService.deleteSaleChance(ids);
        return success("营销机会数据删除成功！");
    }

    /**
     * 更新营销机会数据 101004
     * @param request
     * @param saleChance
     * @return
     */
    @RequirePermission(code = "101004")
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateSaleChance(HttpServletRequest request, SaleChance saleChance){
        // 更新营销机会的数据
        saleChanceService.updateSaleChance(saleChance);
        return success("营销机会数据更新成功！");
    }

}
