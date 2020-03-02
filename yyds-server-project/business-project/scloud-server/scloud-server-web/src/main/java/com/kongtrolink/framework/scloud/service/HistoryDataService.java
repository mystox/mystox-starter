package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.model.HistoryDataModel;
import com.kongtrolink.framework.scloud.query.HistoryDataQuery;

import java.util.List;

/**
 * 历史数据功能类
 */
public interface HistoryDataService {
    /**
     * 根据查询条件获取 历史数据列表 - 分页
     * @param historyDataQuery 查询条件
     * @return 列表
     */
    List<HistoryDataModel> getHisList(String uniqueCode,HistoryDataQuery historyDataQuery);
    /**
     * 根据查询条件获取 历史数据列表 - 分页-总数
     * @param historyDataQuery 查询条件
     * @return 列表总数
     */
    int getHisCount(String uniqueCode,HistoryDataQuery historyDataQuery);
}
