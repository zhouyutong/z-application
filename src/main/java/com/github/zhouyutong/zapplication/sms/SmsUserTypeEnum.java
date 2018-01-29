package com.github.zhouyutong.zapplication.sms;


import lombok.Getter;

/**
 * 目标用户类型枚举
 *
 * @author zhoutao
 * @description:
 * @date created in 9:55 2017/10/26
 */
public enum SmsUserTypeEnum {
    PHONE("phone"),
    ORDER_ID("orderId"),
    CAR_ID("carId");

    SmsUserTypeEnum(String userType) {
        this.userType = userType;
    }

    @Getter
    private String userType;
}
