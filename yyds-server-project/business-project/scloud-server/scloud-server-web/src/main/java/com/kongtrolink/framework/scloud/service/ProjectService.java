package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.scloud.entity.ProjectOrderEntity;
import com.kongtrolink.framework.scloud.entity.ProjectOrderLogEntity;
import com.kongtrolink.framework.scloud.entity.ProjectOrderTestEntity;
import com.kongtrolink.framework.scloud.entity.RelatedDeviceInfo;
import com.kongtrolink.framework.scloud.entity.model.ProjectOrderModel;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.query.ProjectOrderQuery;

import java.util.List;

/**
 * 工程管理 接口类
 * Created by Eric on 2020/4/13.
 */
public interface ProjectService {

    /**
     * 获取测试单列表
     */
    ListResult<ProjectOrderModel> getProjectOrderList(String uniqueCode, ProjectOrderQuery projectOrderQuery);

    /**
     * 创建测试单
     */
    void createProjectOrder(String uniqueCode, User user, ProjectOrderEntity projectOrderEntity);

    /**
     * 获取测试单操作记录
     */
    List<ProjectOrderLogEntity> getOrderLogs(String uniqueCode, ProjectOrderQuery projectOrderQuery);

    /**
     * 生成测试单测试项
     */
    void createProjectOrderTest(String uniqueCode, ProjectOrderQuery projectOrderQuery);

    /**
     * 获取测试项列表
     */
    List<ProjectOrderTestEntity> getProjectOrderTest(String uniqueCode, ProjectOrderQuery projectOrderQuery);

    /**
     * 提交测试单
     */
    void submitProjectOrder(String uniqueCode, User user, List<ProjectOrderTestEntity> list);

    /**
     * 审核测试单
     */
    void projectOrderCheck(String uniqueCode, User user, ProjectOrderQuery  projectOrderQuery);

    /**
     * 获取测试单下测试设备列表
     */
    List<RelatedDeviceInfo> getProjectOrderDevices(String uniqueCode, DeviceQuery deviceQuery);
}
