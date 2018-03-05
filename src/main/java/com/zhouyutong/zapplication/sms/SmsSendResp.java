package com.zhouyutong.zapplication.sms;

import lombok.Data;

/**
 * 短信发送响应对象
 */
@Data
public class SmsSendResp {
    private int status;
    private String errMsg;
    /**
     * 短信平台生成唯一标识一个发送
     */
    private String taskid;

    /**
     * 发送是否成功
     *
     * @return
     */
    public boolean success() {
        return this.status == 0;
    }
}
