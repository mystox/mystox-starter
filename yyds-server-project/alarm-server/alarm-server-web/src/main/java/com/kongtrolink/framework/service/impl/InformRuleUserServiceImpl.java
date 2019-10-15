package com.kongtrolink.framework.log.impl;

import com.kongtrolink.framework.dao.InformRuleUserDao;
import com.kongtrolink.framework.enttiy.InformRuleUser;
import com.kongtrolink.framework.service.InformRuleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/11 15:47
 * @Description:
 */
@Service
public class InformRuleUserServiceImpl implements InformRuleUserService {

    @Autowired
    InformRuleUserDao ruleUserDao;

    @Override
    public boolean save(InformRuleUser ruleUser) {
        return ruleUserDao.save(ruleUser);
    }

    @Override
    public boolean save(List<InformRuleUser> ruleUserList) {
        return ruleUserDao.save(ruleUserList);
    }

    @Override
    public boolean delete(String id) {
        return ruleUserDao.delete(id);
    }

    @Override
    public boolean deleteByRuleId(String ruleId) {
        return ruleUserDao.deleteByRuleId(ruleId);
    }

    @Override
    public boolean deleteByUserIds(List<String> userIds) {
        return ruleUserDao.deleteByUserIds(userIds);
    }

    @Override
    public List<InformRuleUser> getByRuleId(String ruleId) {
        return ruleUserDao.getByRuleId(ruleId);
    }
}
