package com.lyf.crm24.enums;

public enum StateStatus {
    //未分配
    UNSTATE(0),
    STATED(1);

    private Integer type;
    StateStatus(int value) {
        this.type = value;
    }

    public Integer getType() {return type;}
}
