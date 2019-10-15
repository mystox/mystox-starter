package com.kongtrolink.framework.log.impl;

import com.kongtrolink.framework.dao.InformRuleDao;
import com.kongtrolink.framework.enttiy.InformRule;
import com.kongtrolink.framework.query.InformRuleQuery;
import com.kongtrolink.framework.service.InformRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/11 09:42
 * @Description:
 */
@Service
public class InformRuleServiceImpl implements InformRuleService {

    @Autowired
    InformRuleDao ruleDao;

    @Override
    public boolean save(InformRule deliver) {
        return ruleDao.save(deliver);
    }

    @Override
    public boolean delete(String deliverId) {
        return ruleDao.delete(deliverId);
    }

    @Override
    public boolean update(InformRule deliver) {
        return ruleDao.update(deliver);
    }

    @Override
    public InformRule get(String ruleId) {
        return ruleDao.get(ruleId);
    }

    @Override
    public List<InformRule> list(InformRuleQuery deliverQuery) {
        return ruleDao.list(deliverQuery);
    }

    @Override
    public int count(InformRuleQuery deliverQuery) {
        return ruleDao.count(deliverQuery);
    }

    @Override
    public InformRule getOne(InformRuleQuery deliverQuery) {
        return ruleDao.getOne(deliverQuery);
    }

    @Override
    public InformRule getByName(String name) {
        return ruleDao.getByName(name);
    }

    @Override
    public boolean updateStatus(String ruleId, String status) {
        return ruleDao.updateStatus(ruleId, status);
    }
}
