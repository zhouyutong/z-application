package com.zhouyutong.zapplication.sms;

import lombok.Data;

/**
 * 短信发送请求
 */
@Data
public class SmsSendReq {
    /**
     * 必填|token|发送前需要提前申请
     */
    private String token;
    /**
     * 必填|模板id|发送前需要提前申请
     */
    private String tplId;
    /**
     * 必填|模板元数据|发送前需要提前申请
     * 英文逗号分隔，注意：英文逗号为分隔专用符号，如内容中包含英文逗号，请使用中文逗号替换;
     * 逗号分隔内容依次替换模板中%s部分，该参数留空(tpl_meta='')表示无需填充模板;
     */
    private String tplMeta;
    /**
     * 目标用户
     * 封装不支持批量发送，因为消息平台返回部分失败，不好处理
     */
    private String userId;
    /**
     * 异步发送时延时
     * 指定延迟发送时间(单位秒，默认为0，最大延迟7天)，前提需为异步方式发送
     */
    private int delay;
    /**
     * 目标用户类型，默认PHPNE
     */
    private SmsUserTypeEnum smsUserTypeEnum = SmsUserTypeEnum.PHONE;
    /**
     * 目标用户角色，默认销售
     */
    private SmsUserRoleEnum smsUserRoleEnum = SmsUserRoleEnum.SALES;
    /**
     * 同步/异步(默认)发送
     */
    private SmsSyncFlagEnum smsSyncFlagEnum = SmsSyncFlagEnum.ASYNC;
}
