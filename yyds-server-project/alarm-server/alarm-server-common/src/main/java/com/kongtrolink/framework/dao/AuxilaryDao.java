package com.kongtrolink.framework.dao;

import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.enttiy.Auxilary;
import com.kongtrolink.framework.query.AuxilaryQuery;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/24 10:40
 * @Description:
 */
@Repository
public class AuxilaryDao {

    @Autowired
    MongoTemplate mongoTemplate;
    private String table = MongTable.AUXILARY;

    public void save(Auxilary auxilary) {
        mongoTemplate.save(auxilary, table);
    }

    public boolean delete(String auxilaryId) {
        Criteria criteria = Criteria.where("_id").is(auxilaryId);
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, table);
        return remove.getN()>0 ? true : false;
    }

    public boolean update(Auxilary auxilary) {
        Criteria criteria = Criteria.where("_id").is(auxilary.get_id());
        Query query = Query.query(criteria);
        mongoTemplate.remove(query, table);

        mongoTemplate.save(auxilary);

        return true;
    }

    public List<Auxilary> list(AuxilaryQuery auxilaryQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, auxilaryQuery);
        Query query = Query.query(criteria);
        int currentPage = auxilaryQuery.getCurrentPage();
        int pageSize = auxilaryQuery.getPageSize();
        query.skip((currentPage-1)* pageSize).limit(pageSize);
        return mongoTemplate.find(query, Auxilary.class, table);
    }

    public int count(AuxilaryQuery auxilaryQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, auxilaryQuery);
        Query query = Query.query(criteria);
        return (int)mongoTemplate.count(query, table);
    }

    Criteria baseCriteria(Criteria criteria, AuxilaryQuery auxilaryQuery){
        String id = auxilaryQuery.get_id();
        if(!StringUtil.isNUll(id)){
            criteria.and("_id").is(id);
        }
        String enterpriseCode = auxilaryQuery.getEnterpriseCode();
        if(!StringUtil.isNUll(enterpriseCode)){
            criteria.and("enterpriseCode ").is(enterpriseCode );
        }
        String serverCode = auxilaryQuery.getServerCode();
        if(!StringUtil.isNUll(serverCode)){
            criteria.and("serverCode").is(serverCode);
        }
        //liuddtodo 需要做mongodb特殊字符串处理
        String proStr = auxilaryQuery.getProStr();
        if(!StringUtil.isNUll(proStr)){
            criteria.and("proStrList").regex(".*?" + proStr + ".*?");
        }
        String proName = auxilaryQuery.getProName();
        if(!StringUtil.isNUll(proName)){
            criteria.and("proNameList").regex(".*?" + proName + ".*?");
        }
        String proType = auxilaryQuery.getProType();
        if(!StringUtil.isNUll(proType)){
            criteria.and("proTypeList").regex(".*?" + proType + ".*?");
        }
        return criteria;
    }

    public Auxilary getByEnterServerCode(String enterpriseCode, String serverCode) {
        Criteria criteria = Criteria.where("enterpriseCode").is(enterpriseCode);
        criteria.and("serverCode").is(serverCode);
        Query query = Query.query(criteria);
        Auxilary auxilary = mongoTemplate.findOne(query, Auxilary.class, table);
        return auxilary;
    }
}
