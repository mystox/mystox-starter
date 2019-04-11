package com.kongtrolink.framework.execute.module.dao;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.MongoTableName;
import com.kongtrolink.framework.execute.module.model.Order;
import com.kongtrolink.framework.execute.module.model.Terminal;
import com.kongtrolink.framework.execute.module.model.TerminalProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by mystoxlol on 2019/3/28, 9:06.
 * company: kongtrolink
 * description:
 * update record:
 */
@Component
public class TerminalDao
{
    @Autowired
    MongoTemplate mongoTemplate;

    public Terminal findTerminalBySn(String sn)
    {
        Terminal terminal = mongoTemplate.findOne(Query.query(Criteria.where("SN").is(sn)), Terminal.class, MongoTableName.TERMINAL);
        return terminal;
    }

    public Order findOrderByBid(String bid)
    {
        Order order = mongoTemplate.findOne(Query.query(Criteria.where("BID").is(bid)), Order.class, MongoTableName.ORDER);
        return order;
    }

    public Order findOrderById(String id)
    {
        Order order = mongoTemplate.findById(id, Order.class, MongoTableName.ORDER);
        return order;
    }

    public TerminalProperties findTerminalPropertiesByTerminalId(String terminalId) {
        return mongoTemplate.findOne(Query.query(Criteria.where("terminalId").is(terminalId)), TerminalProperties.class,MongoTableName.TERMINAL_PROPERTY);
    }

    public void saveTerminal(TerminalProperties terminalProperties) {
        mongoTemplate.save(terminalProperties,MongoTableName.TERMINAL_PROPERTY);
    }

    public List<Terminal> findTerminal(JSONObject jsonObject) {

        Criteria criteria = new Criteria();

        return mongoTemplate.find(Query.query(criteria),Terminal.class, MongoTableName.TERMINAL);
    }
}
