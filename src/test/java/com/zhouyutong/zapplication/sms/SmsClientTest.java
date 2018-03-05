package com.zhouyutong.zapplication.sms;

public class SmsClientTest {
    private SmsClient smsClient;

    @org.junit.Before
    public void setUp() throws Exception {
        smsClient = new SmsClient("testing");
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void send() throws Exception {
        SmsSendReq smsSendReq = new SmsSendReq();
        smsSendReq.setToken("ddd");
        smsSendReq.setTplId("ddd");
        smsSendReq.setUserId("13681542909");

        try {
            SmsSendResp smsSendResp = smsClient.send(smsSendReq);
            System.out.println(smsSendResp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}