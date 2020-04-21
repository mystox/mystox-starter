package com.kongtrolink.framework.service;

import com.kongtrolink.framework.enttiy.InformRuleUser;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/11 15:47
 * @Description:
 */
public interface InformRuleUserService {

    boolean save(InformRuleUser ruleUser);

    boolean delete(String id);

    boolean deleteByRuleId(String ruleId);

    boolean deleteByUserIds(List<String> userIds);

    boolean deleteByUserId(String userId);

    List<InformRuleUser> getByRuleId(String ruleId);
}
