package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.FilterRuleDao;
import com.kongtrolink.framework.scloud.entity.FilterRule;
import com.kongtrolink.framework.scloud.query.FilterRuleQuery;
import com.kongtrolink.framework.scloud.service.FilterRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/3/5 15:02
 * @Description:
 */
@Service
public class FilterRuleServiceImpl implements FilterRuleService {

    @Autowired
    FilterRuleDao filterRuleDao;

    @Override
    public boolean add(String uniqueCode, FilterRule filterRule) {
        return filterRuleDao.add(uniqueCode, filterRule);
    }

    @Override
    public boolean delete(String uniqueCode, String filterRuleId) {
        return filterRuleDao.delete(uniqueCode, filterRuleId);
    }

    @Override
    public boolean update(String uniqueCode, FilterRule filterRule) {
        return filterRuleDao.update(uniqueCode, filterRule);
    }

    @Override
    public List<FilterRule> list(String uniqueCode, FilterRuleQuery ruleQuery) {
        return filterRuleDao.list(uniqueCode, ruleQuery);
    }

    @Override
    public int count(String uniqeuCode, FilterRuleQuery ruleQuery) {
        return filterRuleDao.count(uniqeuCode, ruleQuery);
    }

    @Override
    public boolean updateState(String uniqueCode, FilterRuleQuery ruleQuery) {
        Boolean state = ruleQuery.getState();
        if(!state){
            return filterRuleDao.unUse(uniqueCode, ruleQuery.getId(), ruleQuery.getOperatorId());
        }
        FilterRule userInUse = getUserInUse(uniqueCode, ruleQuery.getOperatorId());
        if(null != userInUse){
            filterRuleDao.unUse(uniqueCode, userInUse.getId(), ruleQuery.getOperatorId());
        }
        return filterRuleDao.use(uniqueCode, ruleQuery.getId(), ruleQuery.getOperatorId());
    }

    /**
     * @param uniqueCode
     * @param creatorId
     * @auther: liudd
     * @date: 2020/3/5 15:49
     * 功能描述:获取用户正在启用的过滤规则
     */
    @Override
    public FilterRule getUserInUse(String uniqueCode, String creatorId) {
        return filterRuleDao.getUserInUse(uniqueCode, creatorId);
    }
}
