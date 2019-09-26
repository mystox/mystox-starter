package com.kongtrolink.framework.dao;

import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.enttiy.Auxilary;
import com.kongtrolink.framework.query.AuxilaryQuery;
import com.mongodb.WriteResult;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
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
        Update update = new Update();
        update.set("proStrList", auxilary.getProStrList());
        update.set("proNameList", auxilary.getProNameList());
        update.set("proTypeList", auxilary.getProTypeList());
        WriteResult result = mongoTemplate.updateFirst(query, update, table);
        return result.getN()>0 ? true : false;
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
        String uniqueCode = auxilaryQuery.getUniqueCode();
        if(!StringUtil.isNUll(uniqueCode)){
            criteria.and("uniqueCode ").is(uniqueCode );
        }
        String service = auxilaryQuery.getService();
        if(!StringUtil.isNUll(service)){
            criteria.and("service").is(service);
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
}
