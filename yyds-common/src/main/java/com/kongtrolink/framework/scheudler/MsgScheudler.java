package com.kongtrolink.framework.scheudler;

import com.kongtrolink.framework.core.IaENV;
import com.kongtrolink.framework.core.callMe;
import com.kongtrolink.framework.entity.RegisterSub;
import com.kongtrolink.framework.service.MsgHandler;

import java.util.List;

public interface MsgScheudler  {
    public void subTopic(List<RegisterSub> subList);
    public void removerSubTopic(List<RegisterSub> subList);
    public void build(IaENV iaENV);
    public void initCaller(callMe caller);//设置回调方法
    public MsgHandler getIahander();
}
