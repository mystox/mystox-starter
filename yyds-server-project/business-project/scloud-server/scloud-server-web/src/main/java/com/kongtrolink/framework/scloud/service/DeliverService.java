package com.kongtrolink.framework.scloud.service;

/**
 * @Auther: liudd
 * @Date: 2020/4/21 17:32
 * @Description:
 */
public interface DeliverService {

    /**
     * @auther: liudd
     * @date: 2020/4/21 17:33
     * 功能描述:删除用户时，删除投递模板中绑定的用户
     */
    boolean delDeliverUser(String uniqueCode, String userId);
}
