package com.github.zhouyutong.zapplication.sms;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;

import java.util.*;

public final class SmsSendHelper {
    /**
     * 发送短信的沙盒地址
     */
    public static final String SEND_URL_SANDBOX = "http://0.0.0.0:8189/v1/sender";
    /**
     * 发送短信的线上地址
     */
    public static final String SEND_URL_ONLINE = "http://0.0.0.0/v1/sender";

    /**
     * 获取短信发送地址的逻辑
     *
     * @param active - 当前系统环境development|testing|production
     * @return
     */
    public static String getSendUrl(String active) {
        if ("development".equals(active) || "testing".equals(active)) {
            return SEND_URL_SANDBOX;
        } else if ("production".equals(active)) {
            return SEND_URL_ONLINE;
        } else {
            throw new SmsException("发送短信前请指定当前系统环境");
        }
    }

    /**
     * 生成签名
     *
     * @param postParams
     * @return
     */
    public static String sign(final Map<String, String> postParams) {
        String timestamp = postParams.get("timestamp");
        String token = postParams.get("token");

        List<String> param1 = Lists.newArrayList();
        param1.add(timestamp);
        param1.add(token);
        param1.add("RRC_Sec");
        Collections.sort(param1);

        StringBuilder part = new StringBuilder();
        for (String str : param1) {
            part.append(str).append("_");
        }

        SortedSet<String> keys = new TreeSet<>(postParams.keySet());
        for (String key : keys) {
            String value = postParams.get(key);
            part.append(value).append("_");
        }

        part.deleteCharAt(part.length() - 1);
        String signBefore = part.toString();
        String signAfter = Hashing.sha1().hashString(signBefore, Charsets.UTF_8).toString().toUpperCase();
        System.out.println(String.format("SmsSendHelper.sign before[%s], after[%s]", signBefore, signAfter));
        return signAfter;
    }

    private SmsSendHelper() {
    }
}
