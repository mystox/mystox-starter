package com.kongtrolink.framework.core.entity;

/**
 * Created by mystoxlol on 2019/4/2, 15:40.
 * company: kongtrolink
 * description:
 * update record:
 */
public class StateCode {
    public final static int FAILED = 0; //响应失败
    public final static int SUCCESS = 1; //请求成功
    public final static int UNREGISTY = 2; //未注册
    public final static int CONNECT_ERROR = 3; //通信出错
    public final static int CONNECT_INTERRUPT = 4; //
    public final static int REDIS_ERROR = 5; //redis异常
    public final static int MONGO_ERROR = 6; //MONGO数据库异常


}
