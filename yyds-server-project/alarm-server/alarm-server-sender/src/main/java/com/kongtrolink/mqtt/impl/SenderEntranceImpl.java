package com.kongtrolink.mqtt.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.service.JpushService;
import com.kongtrolink.service.EmailService;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.enttiy.InformMsg;
import com.kongtrolink.service.MessageService;
import com.kongtrolink.mqtt.SenderEntrance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Auther: liudd
 * @Date: 2019/10/25 16:26
 * @Description:投递动作
 * 1,根据模板创建发送内容
 * 2，发送等待返回结果
 * 3，将返回结果持久化到数据库
 */
@Service
public class SenderEntranceImpl implements SenderEntrance {

    //使用名字注入，避免与log相关线程池冲突
    @Resource(name = "senderExecutor")
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    private MessageService msgService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JpushService jpushService;

    ConcurrentLinkedQueue<InformMsg> informMsgQueue = new ConcurrentLinkedQueue<>();

    @Override
    public void handleSender(String informMsgStr) {
        //加入队列，直接返回
        InformMsg informMsg = JSONObject.parseObject(informMsgStr, InformMsg.class);
        informMsgQueue.add(informMsg);
        taskExecutor.execute(()-> handleInformMsg());
        return ;
    }

    /**
     * @auther: liudd
     * @date: 2019/10/26 9:49
     * 功能描述:处理告警任务
     */
    private void handleInformMsg(){
        if(informMsgQueue.size() == 0){
            return ;
        }
        InformMsg informMsg = informMsgQueue.poll();
        String type = informMsg.getType();
        if(Contant.TEMPLATE_MSG.equals(type)){
            msgService.doSendMessage(informMsg);
        }else if(Contant.TEMPLATE_EMAIL.equals(type)){
            emailService.sendEmail(informMsg);
        }else{
            jpushService.pushApp(informMsg);
        }
        handleInformMsg();
    }
}
