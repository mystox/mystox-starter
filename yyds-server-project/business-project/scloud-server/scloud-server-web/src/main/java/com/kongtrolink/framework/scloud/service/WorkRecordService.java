package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.FacadeView;
import com.kongtrolink.framework.scloud.entity.Work;
import com.kongtrolink.framework.scloud.entity.WorkRecord;
import com.kongtrolink.framework.scloud.query.WorkRecordQuery;

import java.util.Date;
import java.util.List;

/**
 * 工单记录表service
 */
public interface WorkRecordService {

    /**
     * 添加
     * @param uniqueCode
     * @param workRecord
     */
    void add(String uniqueCode, WorkRecord workRecord);

    /**
     * 获取列表
     * @param uniqueCode
     * @param recordQuery
     * @return
     */
    List<WorkRecord> list(String uniqueCode, WorkRecordQuery recordQuery);

    /**
     * 获取工单的最后一条工单记录
     * @param uniqueCode
     * @param workId
     * @return
     */
    WorkRecord getLastWorkRecord(String uniqueCode, String workId);

    /**
     * 获取工单的所有处理记录
     * @param uniqueCode
     * @param workId
     * @return
     */
    List<WorkRecord> getListByWorkId(String uniqueCode, String workId);

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
    WorkRecord createWorkRecord(Work work, FacadeView maintainer, String operateType, String operateDescribe, Date operateTime,
                                float handleTime, String operateFTU, FacadeView receiver);
}
