package com.kongtrolink.framework.core.entity;

/**
 * Created by mystoxlol on 2019/3/27, 9:35.
 * company: kongtrolink
 * description:
 * update record:
 */
public class RedisHashTable
{
    public final static String COMMUNICATION_HASH = "communication_hash"; //SN1:{GWip：ip1；UUID（连接编号）:id1；BIP ：bip（业务服务地址）,STATUS:0(未注册)1（已注册）}
    public final static String SN_DEVICE_LIST_HASH = "sn_device_list_hash";//  SN1:{ deviceId1, deviceId2...};
    public final static String SN_SIGNAL_DATA = "sn_signal_data";//  SN1:{ 实时数据};
}
