package com.kongtrolink.framework.core.node.register;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.io.UnsupportedEncodingException;

/**
 * Created by mystoxlol on 2019/2/19, 9:03.
 * company: kongtrolink
 * description: 服务注册公共接口
 * update record:
 */
public interface ServerRegistryProvider
{
    /**
     * 根据context 注册节点
     * @param context
     */
    boolean register(Object context) throws KeeperException, InterruptedException; //注册

    /**
     * 根据context 注销节点
     * @param context
     */
    void unregister(Object context); //注销

    /**
     *  根据context获取节点信息
     * @param context
     * @return
     */
    Object getNodeData(Object context) throws KeeperException, InterruptedException;
    <T> T getNodeData(Object context, Class<T> clazz) throws KeeperException, InterruptedException;
    /**
     * 获根据context 设置或更新节点信息
     * @param context
     */
    void setData(Object context) throws UnsupportedEncodingException, KeeperException, InterruptedException;

    /**
     * 判断节点是否存在
     * @param path
     * @return
     */
    boolean exists(String path) throws KeeperException, InterruptedException;

    /**
     * 创建节点信息
     * @param context
     * @return
     */
    String create(Object context, CreateMode createMode) throws KeeperException, InterruptedException, UnsupportedEncodingException;
}
