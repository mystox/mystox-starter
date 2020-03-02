package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.FocusSignalDao;
import com.kongtrolink.framework.scloud.dao.HistoryDataDao;
import com.kongtrolink.framework.scloud.entity.HistoryDataEntity;
import com.kongtrolink.framework.scloud.entity.model.HistoryDataModel;
import com.kongtrolink.framework.scloud.query.HistoryDataQuery;
import com.kongtrolink.framework.scloud.service.HistoryDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 历史数据功能类
 * @author Mag
 **/
@Service
public class HistoryDataServiceImpl implements HistoryDataService {

    @Autowired
    HistoryDataDao historyDataDao;
    /**
     * 根据查询条件获取 历史数据列表 - 分页
     *
     * @param historyDataQuery 查询条件
     * @return 列表
     */
    @Override
    public List<HistoryDataModel> getHisList(String uniqueCode,HistoryDataQuery historyDataQuery) {
        List<HistoryDataEntity> list = historyDataDao.getHisList(uniqueCode,historyDataQuery);
        String cntbId = historyDataQuery.getCntbId();
        return getList(cntbId,list);
    }

    private List<HistoryDataModel> getList(String cntbId,List<HistoryDataEntity> list){
        List<HistoryDataModel> result = new ArrayList<>();//返回
        if(list!=null){
            for(HistoryDataEntity entity:list){
                try{
                    Object value = entity.getValue().get(cntbId);
                    HistoryDataModel model = new HistoryDataModel(entity.getTime(),String.valueOf(value));
                    result.add(model);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
        return result;
    }
    /**
     * 根据查询条件获取 历史数据列表 - 分页-总数
     *
     * @param historyDataQuery 查询条件
     * @return 列表总数
     */
    @Override
    public int getHisCount(String uniqueCode,HistoryDataQuery historyDataQuery) {
        return historyDataDao.getHisCount(uniqueCode,historyDataQuery);
    }
}
