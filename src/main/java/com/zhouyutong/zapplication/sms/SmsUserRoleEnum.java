package com.zhouyutong.zapplication.sms;


import lombok.Getter;

/**
 * 目标用户角色枚举
 *
 * @author zhoutao
 * @description:
 * @date created in 9:55 2017/10/26
 */
public enum SmsUserRoleEnum {
    C1("c1"),
    C2("c2"),
    INSPECT("inspect"),
    SALES("sales");

    SmsUserRoleEnum(String userRole) {
        this.userRole = userRole;
    }

    @Getter
    private String userRole;
}
