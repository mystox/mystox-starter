package com.kongtrolink.framework.dao;

import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.enttiy.InformMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * @Auther: liudd
 * @Date: 2019/10/22 10:07
 * @Description:
 */
@Repository
public class InformMsgDao {

    @Autowired
    MongoTemplate mongoTemplate;
    private String table = MongTable.INFORM_MSG;

    public boolean save(InformMsg informMsg){
        mongoTemplate.save(informMsg, table);
        if(!StringUtil.isNUll(informMsg.get_id())){
            return true;
        }
        return false;
    }



}
