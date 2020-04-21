package com.kongtrolink.framework.scloud.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.core.entity.session.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * \* @Author: mystox
 * \* Date: 2020/4/15 13:42
 * \* Description:
 * \
 */
@Service
public class SessionCommonService {

    Logger logger = LoggerFactory.getLogger(SessionCommonService.class);

    @Autowired
    RedisOperationsSessionRepository sessionRepository;

    public Set<String> getUsernameListByCurrentServerCode(String serverCode) {
        Map byIndexNameAndIndexValue = sessionRepository.findByIndexNameAndIndexValue(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, serverCode);
        Set set = byIndexNameAndIndexValue.keySet();
        Set<String> usernames = new HashSet<>();
        set.forEach(s->{
            String key = String.valueOf(s);
            Session o = (Session) byIndexNameAndIndexValue.get(key);
            Object attribute = o.getAttribute(Const.SESSION_USER);
            if (attribute != null) {
                User user = JSONObject.toJavaObject((JSON) JSON.toJSON(attribute), User.class);
                String username = user.getUsername();
                usernames.add(username);
            }
        });
        logger.info("get username list is {}", JSONObject.toJSONString(usernames));
        return usernames;
    }

}