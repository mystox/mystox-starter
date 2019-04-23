package com.kongtrolink.framework.execute.module.dao;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.MongoTableName;
import com.kongtrolink.framework.execute.module.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by mystoxlol on 2019/3/28, 9:06.
 * company: kongtrolink
 * description:
 * update record:
 */
@Component
public class TerminalDao {
    @Autowired
    MongoTemplate mongoTemplate;

    public Terminal findTerminalBySn(String sn) {
        Terminal terminal = mongoTemplate.findOne(Query.query(Criteria.where("SN").is(sn)), Terminal.class, MongoTableName.TERMINAL);
        return terminal;
    }

    public Order findOrderByBid(String bid) {
        Order order = mongoTemplate.findOne(Query.query(Criteria.where("BID").is(bid)), Order.class, MongoTableName.ORDER);
        return order;
    }

    public Order findOrderById(String id) {
        Order order = mongoTemplate.findById(id, Order.class, MongoTableName.ORDER);
        return order;
    }

    public TerminalProperties findTerminalPropertiesByTerminalId(String terminalId) {
        return mongoTemplate.findOne(Query.query(Criteria.where("terminalId").is(terminalId)), TerminalProperties.class, MongoTableName.TERMINAL_PROPERTY);
    }

    public void saveTerminalProperties(TerminalProperties terminalProperties) {
        mongoTemplate.save(terminalProperties, MongoTableName.TERMINAL_PROPERTY);
    }

    public List<Terminal> findTerminal(JSONObject jsonObject) {

        Criteria criteria = new Criteria();

        return mongoTemplate.find(Query.query(criteria), Terminal.class, MongoTableName.TERMINAL);
    }

    public boolean existsBySn(String sn) {
        return mongoTemplate.exists(Query.query(Criteria.where("SN").is(sn)), MongoTableName.TERMINAL);
    }

    public void saveTerminal(Terminal terminal) {
        mongoTemplate.save(terminal, MongoTableName.TERMINAL);
    }

    public void saveTerminalBatch(List<Terminal> terminals) {
        mongoTemplate.insert(terminals, MongoTableName.TERMINAL);
    }

    public void saveTerminalLog(TerminalLog terminalLog) {
        mongoTemplate.save(terminalLog, MongoTableName.TERMINAL_LOG);
    }

    public List<TerminalLog> findTerminalLog(String sn, JSONObject search) {
        Criteria criteria = Criteria.where("sn").is(sn);
        Object startTime = search.get("startTime") + "";
        if (startTime != null) {
            Object endTime = search.get("endTime");
            if (endTime != null) {
                criteria.and("recordTime").gte(new Date(Long.valueOf(startTime + ""))).lte(new Date(Long.valueOf(endTime +"")));
            } else {
                criteria.and("recordTime").gte(new Date(Long.valueOf(startTime + "")));
            }
        }
        return mongoTemplate.find(Query.query(criteria), TerminalLog.class, MongoTableName.TERMINAL_LOG);
    }

    public List<RunState> findRunStates(String sn, JSONObject search) {
        Criteria criteria = Criteria.where("sn").is(sn);
        Long startTime = Long.valueOf(search.get("startTime") + "");
        if (startTime != null) {
            Object endTime = search.get("endTime");
            if (endTime != null) {
                criteria.and("createTime").gte(new Date(Long.valueOf(startTime + ""))).lte(new Date(Long.valueOf(endTime +"")));
            } else {
                criteria.and("createTime").gte(new Date(Long.valueOf(startTime + "")));
            }
        }
        return mongoTemplate.find(Query.query(criteria), RunState.class, MongoTableName.TERMINAL_RUN_STATE);
    }
}
