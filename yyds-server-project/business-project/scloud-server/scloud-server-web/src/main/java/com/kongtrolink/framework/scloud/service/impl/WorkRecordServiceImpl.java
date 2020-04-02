package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.WorkRecordDao;
import com.kongtrolink.framework.scloud.entity.FacadeView;
import com.kongtrolink.framework.scloud.entity.Work;
import com.kongtrolink.framework.scloud.entity.WorkRecord;
import com.kongtrolink.framework.scloud.query.WorkRecordQuery;
import com.kongtrolink.framework.scloud.service.WorkRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

/**
 * 工单记录表service实现类
 */
@Service
public class WorkRecordServiceImpl implements WorkRecordService {

    @Autowired
    WorkRecordDao recordMongo;

    @Override
    public void add(String uniqueCode, WorkRecord workRecord) {
        recordMongo.add(uniqueCode, workRecord);
    }

    @Override
    public List<WorkRecord> list(String uniqueCode, WorkRecordQuery recordQuery) {
        return recordMongo.list(uniqueCode, recordQuery, new Sort(Sort.Direction.ASC, "operateTime"));
    }

    @Override
    public WorkRecord getLastWorkRecord(String uniqueCode, String workId) {
        return recordMongo.getLastWorkRecord(uniqueCode, workId);
    }

    /**
     * 获取工单的所有处理记录
     * @param uniqueCode
     * @param workId
     * @return
     */
    public List<WorkRecord> getListByWorkId(String uniqueCode, String workId){
        return recordMongo.getListByWorkId(uniqueCode, workId);
    }

    /**
     * 生成工单记录
     * @param work
     * @param maintainer:动作发起者
     * @param operateType
     * @param operateDescribe
     * @param operateTime
     * @param handleTime
     * @param operateFTU
     * @param receiver：动作接受者
     * @return
     */
    public WorkRecord createWorkRecord(Work work, FacadeView maintainer, String operateType, String operateDescribe, Date operateTime,
                                       float handleTime, String operateFTU, FacadeView receiver){
        WorkRecord workRecord = new WorkRecord();
        workRecord.setWorkId(work.getId());
        workRecord.setWorker(maintainer);
        workRecord.setOperateType(operateType);
        workRecord.setOperateDescribe(operateDescribe);
        workRecord.setOperateTime(operateTime);
        workRecord.setHandleTime(handleTime);
        workRecord.setOperateFTU(operateFTU);
        workRecord.setReceiver(receiver);
        return workRecord;
    }
}
