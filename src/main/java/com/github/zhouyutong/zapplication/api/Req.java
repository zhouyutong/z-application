package com.github.zhouyutong.zapplication.api;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * 远程调用的请求参数
 * 定义了系统级参数和应用级参数(data)
 * <p/>
 * Created by lianjia on 2016/6/2.
 */
@Getter
@Setter
@ToString
public class Req implements Serializable {
    /**
     * 系统级参数|必填|分配给调用方的appKey，用于区分调用方
     */
    private String appKey;
    /**
     * 系统级参数|非必填|分配给用户的令牌，通过授权获取，方法详见(授权说明TODO)
     */
    private String accessToken;
    /**
     * 系统级参数|非必填|签名，具体签名算法接口自行约定
     */
    private String sign;
    /**
     * 系统级参数|必填|时间戳，格式为yyyy-MM-dd HH:mm:ss.SSS，例如：2011-06-16 13:23:30.667
     */
    private String timestamp;
    /**
     * 系统级参数|必填|API协议版本，可选值:v1
     */
    private String version;
    /**
     * 系统级参数|必填|当前操作城市
     */
    private String cityCode;
    /**
     * 系统级参数|必填|当前操作人系统号
     */
    private String userCode;
    /**
     * 应用级参数|非必填|
     */

    private Map<String, Object> data;
}
