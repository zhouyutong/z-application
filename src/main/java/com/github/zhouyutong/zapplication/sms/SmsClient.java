package com.github.zhouyutong.zapplication.sms;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.github.zhouyutong.zapplication.serialization.json.FastJson;
import com.github.zhouyutong.zapplication.utils.HttpClientUtils;
import com.github.zhouyutong.zapplication.utils.StringUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

/**
 * 提供对消息中心(prometheus)项目的封装
 * http://wiki.shanyishanmei.com/display/usertech/prometheus#prometheus-prometheus接口文档
 *
 * @author zhoutao
 * @date created 2017-12-5
 */
@Slf4j
public class SmsClient {

    @Getter
    private HttpClientUtils httpClientUtils;

    /**
     * prometheus发送地址
     */
    @Getter
    private String sendUrl;

    /**
     * 当前系统环境
     * development
     * testing
     * production
     */
    public SmsClient(String active) {
        this.sendUrl = SmsSendHelper.getSendUrl(active);
        this.httpClientUtils = new HttpClientUtils();

    }


    /**
     * 短信发送
     * 使用httpClientUtils默认的10秒超时，异步发送方式忽略
     *
     * @param smsSendReq
     * @return
     * @throws IllegalArgumentException
     * @throws SmsException
     */
    public SmsSendResp send(SmsSendReq smsSendReq) throws SmsException {
        /**
         * 前置校验
         */
        Preconditions.checkNotNull(smsSendReq);
        Preconditions.checkArgument(StringUtils.isNotBlank(smsSendReq.getToken()), "Param token must be not null and empty");
        Preconditions.checkArgument(StringUtils.isNotBlank(smsSendReq.getTplId()), "Param tplId must be not null and empty");
        Preconditions.checkArgument(StringUtils.isNotBlank(smsSendReq.getUserId()), "Param userId must be not null and empty");

        try {
            Map<String, String> postParams = Maps.newHashMap();
            postParams.put("timestamp", (System.currentTimeMillis() / 1000) + "");
            postParams.put("token", smsSendReq.getToken());
            postParams.put("user_ids", smsSendReq.getUserId());
            postParams.put("tpl_id", smsSendReq.getTplId());
            postParams.put("tpl_meta", smsSendReq.getTplMeta() == null ? "" : smsSendReq.getTplMeta());
            postParams.put("user_type", smsSendReq.getSmsUserTypeEnum().getUserType());
            postParams.put("user_role", smsSendReq.getSmsUserRoleEnum().getUserRole());
            postParams.put("sync", smsSendReq.getSmsSyncFlagEnum().getFlag() + "");
            postParams.put("delay", smsSendReq.getDelay() + "");

            String sign = SmsSendHelper.sign(postParams);
            String url = this.sendUrl + "?sign=" + sign;
//            String url = this.sendUrl;

            String respJson = httpClientUtils.httpPostForm(url, postParams);
            Map respMap = FastJson.jsonStr2Object(respJson, Map.class);
            /**
             * 由于我们只支持单个手机发送，所以返回的success_list和fail_list忽略
             * 消息平台那边只要有一个发送成功，status都是0，So 对于我们来说不好处理
             */
            SmsSendResp resp = new SmsSendResp();
            resp.setStatus(MapUtils.getIntValue(respMap, "status"));
            resp.setErrMsg(MapUtils.getString(respMap, "err_msg"));
            resp.setTaskid(MapUtils.getString(respMap, "taskid"));
            return resp;
        } catch (Throwable ex) {
            throw new SmsException(ex.getMessage(), ex);
        }
    }
}
