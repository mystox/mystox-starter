package com.kongtrolink.framework.entity;

/**
 * Created by mystoxlol on 2019/4/2, 15:40.
 * company: kongtrolink
 * description:
 * update record:
 */
public class StateCode {
    public final static int FAILED = 0; //响应失败
    public final static int SUCCESS = 1; //请求成功
    public final static int UNREGISTERED = 2; // 注册错误
    public final static int CONNECT_ERROR = 3; //通信出错
    public final static int CONNECT_INTERRUPT = 4; //通讯中断
    public final static int REDIS_ERROR = 5; //redis异常
    public final static int MONGO_ERROR = 6; //MONGO数据库异常
    public final static int ILLEGAL_LOGIN = 7; //非法登录
    public final static int JSON_ILLEGAL = 8; //json报文不合规
    public final static int XML_ILLEGAL = 9; //xml报文不合规
    public final static int MSG_DUPLICATE = 10; //消息重复发送
    public final static int TIMEOUT = 11; //消息超时
    public final static int EXCEPTION = 12; //非法异常
    public final static int MESSAGE_EXCEPTION = 13; //消息异常
    public final static int CALLBACK_FULL = 14; //回调集合满

    public enum StateCodeEnum {
        FAILED(0, "响应失败"), SUCCESS(1, "请求成功"), UNREGISTERED(2, "注册错误"), CONNECT_ERROR(3, "通信出错"),
        CONNECT_INTERRUPT(4, "通讯中断"), REDIS_ERROR(5, "redis异常"), MONGO_ERROR(6, "MONGO数据库异常"),
        ILLEGAL_LOGIN(7, "非法登录"), JSON_ILLEGAL(8, "json报文不合规"), XML_ILLEGAL(9, "xml报文不合规"),
        MSG_DUPLICATE(10, "消息重复发送"), TIMEOUT(11, "消息超时"), EXCEPTION(12, "非法异常"), MESSAGE_EXCEPTION(13, "消息异常"),
        CALLBACK_FULL(14,"回调集合满");

        private int code;
        private String stateCodeName;

        StateCodeEnum(int code, String stateCodeName) {
            this.code = code;
            this.stateCodeName = stateCodeName;
        }

        public static String toStateCodeName(int code) {
            StateCodeEnum[] values = StateCodeEnum.values();
            for (StateCodeEnum value : values) {
                if (value.code == code) {
                    return value.stateCodeName;
                }
            }
            return null;
        }


    }
}
