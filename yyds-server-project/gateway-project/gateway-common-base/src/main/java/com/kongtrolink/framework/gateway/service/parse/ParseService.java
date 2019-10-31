package com.kongtrolink.framework.gateway.service.parse;

/**
 * Created by mystoxlol on 2019/10/15, 15:40.
 * company: kongtrolink
 * description: 消息解析器
 * update record:
 */
public interface ParseService {


    public void execute(String payload);

    //    public void parse();
    public void init();

    public void configTransverterInit();

}
