package com.kongtrolink;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.enttiy.AlarmCycle;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import com.kongtrolink.framework.query.EnterpriseLevelQuery;
import com.kongtrolink.framework.service.EnterpriseLevelService;
import com.mongodb.WriteResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/10/16 18:43
 * @Description:
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class EnterpriseLevelTest {

    @Autowired
    EnterpriseLevelService enterpriseLevelService;
    @Autowired
    MongoTemplate mongoTemplate;
    private String table = MongTable.ENTERPRISE_LEVEL;

    @Test
    public void add(){
        EnterpriseLevel enterpriseLevel = new EnterpriseLevel();
        enterpriseLevel.setEnterpriseCode("zhyd");
        enterpriseLevel.setServerCode("alarm-server");
        String code = StringUtil.getCode(new Date());
        enterpriseLevel.setCode("201910161847504864");
        System.out.println("code:" + code);
        enterpriseLevel.setName("智慧用电企业告警");
        enterpriseLevel.setUpdateTime(new Date());
        enterpriseLevel.setState(Contant.FORBIT);
        String level = "二";
        enterpriseLevel.setLevel(2);
        enterpriseLevel.setLevelName(level + "级告警");
        enterpriseLevel.setColor("#993838");
        enterpriseLevelService.add(enterpriseLevel);
    }

    @Test
    public void distinct(){
        Aggregation agg = Aggregation.newAggregation(
            Aggregation.group("code", "updateTime"),
            Aggregation.sort(Sort.Direction.DESC, "updateTime")
        );
        AggregationResults<EnterpriseLevel> aggregate = mongoTemplate.aggregate(agg, table, EnterpriseLevel.class);
        List<EnterpriseLevel> mappedResults = aggregate.getMappedResults();
        for(EnterpriseLevel enterpriseLevel : mappedResults){
            System.out.println(enterpriseLevel.getCode() +":" + enterpriseLevel.getUpdateTime().toString());
        }
    }

    @Test
    public void nullMap(){
        List<EnterpriseLevel> list = enterpriseLevelService.list(new EnterpriseLevelQuery());
        for(EnterpriseLevel enterpriseLevel : list){
            System.out.println(enterpriseLevel.getCode()+";"+enterpriseLevel.getLevels());
        }
    }

    @Test
    public void auxilary(){

        Criteria criteria = Criteria.where("enterpriseCode").is("auxilary001");
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("aaa", "第一个添加的属性");
        WriteResult result = mongoTemplate.updateFirst(query, update, MongTable.ALARM_CYCLE);
        int n = result.getN();
        System.out.printf("修改了 %s 条记录 %n", n);

    }
}