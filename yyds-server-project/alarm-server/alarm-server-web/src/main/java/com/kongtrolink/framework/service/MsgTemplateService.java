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

    List<MsgTemplate> list(MsgTemplateQuery msgTemplateQuery);

    int count(MsgTemplateQuery msgTemplateQuery);

    MsgTemplate getByName(String enterpriseCode, String serverCode, String name);
}
