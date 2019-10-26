package com.kongtrolink;

import com.kongtrolink.framework.base.DateUtil;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.enttiy.MsgTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/26 11:29
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MsgTemplateTest {
    private String enterpriseCode = "meitainuo";
    private String serverCode = "zhyd";
    private String table = MongTable.MSG_TEMPLATE;

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    public void list(){
        Criteria enterpriseCri = new Criteria();
        enterpriseCri.and("enterpriseCode").is(enterpriseCode);
        enterpriseCri.and("serverCode").is(serverCode);

        Criteria defalutCri = Criteria.where("enterpriseCode").exists(false);
        Criteria criteria = new Criteria();
        criteria.orOperator(defalutCri, enterpriseCri);
        Query query = Query.query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "enterpriseCode"));
        query.with(new Sort(Sort.Direction.DESC, "updateTime"));
        List<MsgTemplate> msgTemplateList = mongoTemplate.find(query, MsgTemplate.class, table);
        for(MsgTemplate msgTemplate : msgTemplateList){
            System.out.println(msgTemplate.getEnterpriseCode() + ":" + msgTemplate.getEnterpriseName() + ";"
                + msgTemplate.getTemplateType() + ";" + msgTemplate.getType() + DateUtil.format(msgTemplate.getUpdateTime()));
        }
    }
}
