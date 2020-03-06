package com.kongtrolink.framework.scloud.base;

import com.mongodb.DBObject;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

/**
 * mongo 复杂查询时的 管道函数用
 * 参考 HomePageMongo - getHomeFsuNumber
 * Created by Mg on 2018/5/11.
 */
public class CustomOperation implements AggregationOperation {

    private DBObject operation;

    public CustomOperation(DBObject operation) {
        this.operation = operation;
    }

    @Override
    public DBObject toDBObject(AggregationOperationContext aggregationOperationContext) {
        return aggregationOperationContext.getMappedObject(operation);
    }
}
