package com.zhouyutong.zapplication.api;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 远程调用的请求参数
 * 定义了
 * 系统级参数如appKey是每个接口都会传的
 * 应用级参数
 * <p/>
 * Created by zhoutao on 2016/6/2.
 */
@Data
public class Req implements Serializable {
    /**
     * 系统级参数
     */
    private Map<String, Object> sysParam;
    /**
     * 应用级参数
     */
    private Map<String, Object> appParam;
}
