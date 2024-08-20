package com.lyf.crm24.query;

import com.lyf.crm24.base.BaseQuery;

/*
* 营销机会管理多条件查询的条件
* */
public class SaleChanceQuery extends BaseQuery {

    private String customerName; // 客户名称
    private String createMan; // 创建⼈
    private String state; // 分配状态
    private Integer devResult; // 开发状态
    private Integer assignMan;// 分配⼈

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCreateMan() {
        return createMan;
    }

    public void setCreateMan(String createMan) {
        this.createMan = createMan;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getDevResult() {
        return devResult;
    }

    public void setDevResult(Integer devResult) {
        this.devResult = devResult;
    }

    public Integer getAssignMan() {
        return assignMan;
    }

    public void setAssignMan(Integer assignMan) {
        this.assignMan = assignMan;
    }
}
