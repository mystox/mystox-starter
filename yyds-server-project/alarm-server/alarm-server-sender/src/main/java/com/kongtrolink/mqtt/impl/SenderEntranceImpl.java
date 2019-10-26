package com.kongtrolink.mqtt.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.enttiy.InformMsg;
import com.kongtrolink.mqtt.SenderEntrance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @Auther: liudd
 * @Date: 2019/10/25 16:26
 * @Description:投递动作
 * 1,根据模板创建发送内容
 * 2，发送等待返回结果
 * 3，将返回结果持久化到数据库
 */
public class SenderEntranceImpl implements SenderEntrance {

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Override
    public void handleSender(String informMsgStr) {
        //加入队列，直接返回
        InformMsg informMsg = JSONObject.parseObject(informMsgStr, InformMsg.class);
        taskExecutor.execute(new SendTask(informMsg));
        return ;
    }

    class SendTask implements Runnable{
        private InformMsg informMsg;

        public SendTask(InformMsg informMsg){
            this.informMsg = informMsg;
        }

        @Override
        public void run(){
            //1，根据模板生成发送内容
            createMsg(informMsg);
            //调用接口发送，这里写死现有的实现方式

        }
    }

    /**
     * @auther: liudd
     * @date: 2019/10/25 19:01
     * 功能描述:参考现有平台，生成短信，邮件和APP接口调用方式.
     */
    private void createMsg(InformMsg informMsg){
        String template = informMsg.getTemplate();
        String type = informMsg.getType();
        String alarmStateType = informMsg.getAlarmStateType();


    }

}
