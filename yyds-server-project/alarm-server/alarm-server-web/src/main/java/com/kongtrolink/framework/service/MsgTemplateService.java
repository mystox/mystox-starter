package com.kongtrolink.framework.service;

import com.kongtrolink.framework.enttiy.MsgTemplate;
import com.kongtrolink.framework.query.MsgTemplateQuery;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/23 14:33
 * @Description:
 */
public interface MsgTemplateService {

    boolean save(MsgTemplate msgTemplate);

    boolean delete(String msgTemplateId);

    boolean update(MsgTemplate msgTemplate);

    MsgTemplate get(String msgTemplateId);

    List<MsgTemplate> list(MsgTemplateQuery msgTemplateQuery);

    int count(MsgTemplateQuery msgTemplateQuery);

    MsgTemplate getByName(String enterpriseCode, String serverCode, String name);

    /**
     * @auther: liudd
     * @date: 2019/10/26 16:23
     * 功能描述:系统启动时初始化默认模板
     */
    void initMsgTemplate();
}
