package com.kongtrolink.framework.demo.api;

import com.kongtrolink.framework.entity.MsgResult;

/**
 * Created by mystoxlol on 2019/9/9, 13:14.
 * company: kongtrolink
 * description:
 * update record:
 */
public interface FooPublish {


    void sendMsg(String serverCode,String operaCode,String payload);
    MsgResult sendMsgSyn(String serverCode,String operaCode,String payload);
}
