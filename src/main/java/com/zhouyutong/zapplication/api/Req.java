package com.zhouyutong.zapplication.api;

import lombok.Data;

import java.io.Serializable;

/**
 * 系统级参数
 * TODO:具体哪些系统级参数待定
 */
@Data
public class Req implements Serializable {
    private String version;
    private String timestamp;
}
