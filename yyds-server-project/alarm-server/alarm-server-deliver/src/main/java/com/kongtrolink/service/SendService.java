package com.kongtrolink.service;

import com.kongtrolink.framework.enttiy.InformMsg;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Auther: liudd
 * @Date: 2019/10/22 14:54
 * @Description:执行投递
 */
@Service
public class SendService {


    private ConcurrentLinkedQueue<InformMsg> informMsgQueue = new ConcurrentLinkedQueue<>();

    public void addInformMsg(List<InformMsg> informMsgList){
        if(null == informMsgList){
            return ;
        }
        informMsgQueue.addAll(informMsgList);
    }




}
