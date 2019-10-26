package com.kongtrolink.framework.dao;

import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.enttiy.InformMsg;
import com.mongodb.BulkWriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    /**
     * @auther: liudd
     * @date: 2019/10/25 15:29
     * 功能描述:批量添加
     */
    public boolean save(List<InformMsg> informMsgList){
        // BulkMode.UNORDERED:表示并行处理，遇到错误时能继续执行不影响其他操作；BulkMode.ORDERED：表示顺序执行，遇到错误时会停止所有执行
        BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, table);
        for(InformMsg informMsg : informMsgList) {
            ops.insert(informMsg);
        }
        // 执行操作
        BulkWriteResult execute = ops.execute();
        int insertedCount = execute.getInsertedCount();
        return insertedCount>0 ? true : false;
    }



}
