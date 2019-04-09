package com.kongtrolink.framework.excaptions;

/**
 * @Auther: liudd
 * @Date: 2019/4/8 15:12
 * @Description:数据解析异常，主要用于捕获后发送请求保存错误日志
 */
public class DataAnalisisExcetion extends Exception{

    public DataAnalisisExcetion(){};

    public DataAnalisisExcetion(String message){
        super(message);
    }
}
