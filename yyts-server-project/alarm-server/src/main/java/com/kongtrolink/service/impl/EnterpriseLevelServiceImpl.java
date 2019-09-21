package com.kongtrolink.service.impl;

import com.kongtrolink.base.Contant;
import com.kongtrolink.dao.EnterpriseLevelDao;
import com.kongtrolink.enttiy.EnterpriseLevel;
import com.kongtrolink.query.EnterpriseLevelQuery;
import com.kongtrolink.service.EnterpriseLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/20 10:46
 * @Description:
 */
@Service
public class EnterpriseLevelServiceImpl implements EnterpriseLevelService{

    @Autowired
    EnterpriseLevelDao enterpriseLevelDao;

    @Override
    public void add(EnterpriseLevel enterpriseLevel) {
        enterpriseLevelDao.add(enterpriseLevel);
    }

    @Override
    public boolean delete(String enterpriseLevelId) {
        //liuddtodo  需要处理原等级颜色等问题
        return enterpriseLevelDao.delete(enterpriseLevelId);
    }

    @Override
    public boolean update(EnterpriseLevel enterpriseLevel) {
        //liuddtodo  需要处理原等级颜色等问题,以及等级重复冲突
        return enterpriseLevelDao.update(enterpriseLevel);
    }

    @Override
    public List<EnterpriseLevel> list(EnterpriseLevelQuery levelQuery) {
        return enterpriseLevelDao.list(levelQuery);
    }

    @Override
    public int count(EnterpriseLevelQuery levelQuery) {
        return enterpriseLevelDao.count(levelQuery);
    }

    @Override
    public EnterpriseLevel getOne(EnterpriseLevelQuery levelQuery) {
        return enterpriseLevelDao.getOne(levelQuery);
    }

    /**
     * @param enterpriseLevel
     * @auther: liudd
     * @date: 2019/9/20 11:17
     * 功能描述:判定原告警是否重复
     */
    @Override
    public boolean isRepeat(EnterpriseLevel enterpriseLevel) {
        EnterpriseLevelQuery levelQuery = new EnterpriseLevelQuery();
        levelQuery.setUniqueCode(enterpriseLevel.getUniqueCode());
        levelQuery.setService(enterpriseLevel.getService());
        levelQuery.setLevel(enterpriseLevel.getLevel());
        EnterpriseLevel one = getOne(levelQuery);
        if(null == one){
            return false;
        }
        if(enterpriseLevel.getId().equals(one.getId())){
            return false;
        }
        return true;
    }

    /**
     * @auther: liudd
     * @date: 2019/9/21 10:04
     * 功能描述:设置默认告警等级，一个企业只有一个默认告警等级
     */
    @Override
    public boolean updateDefault(EnterpriseLevelQuery enterpriseLevelQuery) {
        String defaultLevel = enterpriseLevelQuery.getDefaultLevel();
        //如果是取消默认等级
        if(Contant.NO.equals(defaultLevel)){
            enterpriseLevelQuery.setDefaultLevel(Contant.YES);
            return enterpriseLevelDao.updateDefault(enterpriseLevelQuery, defaultLevel);
        }
        //设置默认等级
        //1，先去除原来的默认等级
        String targetId = enterpriseLevelQuery.getId();
        enterpriseLevelQuery.setId(null);
        enterpriseLevelDao.updateDefault(enterpriseLevelQuery, Contant.NO);
        //保存新的默认等级
        enterpriseLevelQuery.setId(targetId);
        return enterpriseLevelDao.updateDefault(enterpriseLevelQuery, Contant.YES);

    }
}
