package com.kongtrolink.framework.service.impl;

import com.kongtrolink.framework.dao.MsgTemplateDao;
import com.kongtrolink.framework.enttiy.MsgTemplate;
import com.kongtrolink.framework.query.MsgTemplateQuery;
import com.kongtrolink.framework.service.MsgTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/23 14:34
 * @Description:
 */
@Service
public class MsgTemplateServiceImpl implements MsgTemplateService{
    @Autowired
    MsgTemplateDao msgTemplateDao;
    @Override
    public boolean save(MsgTemplate msgTemplate) {
        return msgTemplateDao.save(msgTemplate);
    }

    @Override
    public boolean delete(String msgTemplateId) {
        return msgTemplateDao.delete(msgTemplateId);
    }

    @Override
    public boolean update(MsgTemplate msgTemplate) {
        return msgTemplateDao.update(msgTemplate);
    }

    @Override
    public MsgTemplate get(String msgTemplateId) {
        return msgTemplateDao.get(msgTemplateId);
    }

    @Override
    public List<MsgTemplate> list(MsgTemplateQuery msgTemplateQuery) {
        return msgTemplateDao.list(msgTemplateQuery);
    }

    @Override
    public int count(MsgTemplateQuery msgTemplateQuery) {
        return msgTemplateDao.count(msgTemplateQuery);
    }

    @Override
    public MsgTemplate getByName(String enterpriseCode, String serverCode, String name) {
        return msgTemplateDao.getByName(enterpriseCode, serverCode, name);
    }
}
