package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.scloud.constant.FsuOperationState;
import com.kongtrolink.framework.scloud.constant.ProjectOrderConstant;
import com.kongtrolink.framework.scloud.dao.DeviceMongo;
import com.kongtrolink.framework.scloud.dao.ProjectMongo;
import com.kongtrolink.framework.scloud.entity.*;
import com.kongtrolink.framework.scloud.entity.model.ProjectOrderModel;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.query.ProjectOrderQuery;
import com.kongtrolink.framework.scloud.service.ProjectService;
import com.kongtrolink.framework.scloud.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 工程管理 接口实现类
 * Created by Eric on 2020/4/13.
 */
@Service
public class ProjectServiceImpl implements ProjectService{

    @Autowired
    ProjectMongo projectMongo;
    @Autowired
    DeviceMongo deviceMongo;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceImpl.class);


    /**
     * 获取测试单列表
     */
    @Override
    public ListResult<ProjectOrderModel> getProjectOrderList(String uniqueCode, ProjectOrderQuery projectOrderQuery) {
        List<ProjectOrderModel> list = new ArrayList<>();

        List<String> fsuCodes = null;
        //查询满足条件的FSU
        if (!StringUtil.isNUll(projectOrderQuery.getManufacturer()) || !StringUtil.isNUll(projectOrderQuery.getFsuState())){
            DeviceQuery deviceQuery = new DeviceQuery();
            deviceQuery.setCurrentPage(1);
            deviceQuery.setPageSize(Integer.MAX_VALUE);
            deviceQuery.setManufacturer(projectOrderQuery.getManufacturer());
            deviceQuery.setState(projectOrderQuery.getFsuState());
            List<DeviceEntity> fsuList = deviceMongo.findDeviceList(uniqueCode, deviceQuery);
            fsuCodes = new ArrayList<>();
            for (DeviceEntity fsu : fsuList){
                fsuCodes.add(fsu.getCode());
            }
        }

        //获取测试单
        projectOrderQuery.setFsuCodes(fsuCodes);
        int count = projectMongo.countProjectOrder(uniqueCode, projectOrderQuery);
        List<ProjectOrderEntity> projectOrderList = projectMongo.findProjectOrders(uniqueCode, projectOrderQuery);

        if (projectOrderList != null && projectOrderList.size() > 0) {
            //获取所有站点


            for (ProjectOrderEntity projectOrder : projectOrderList){
                ProjectOrderModel model = new ProjectOrderModel();
            }
        }

        return new ListResult<>(list, count);
    }

    /**
     * 创建测试单
     */
    @Override
    public void createProjectOrder(String uniqueCode, User user, ProjectOrderEntity projectOrderEntity) {
        Long currentTime = new Date().getTime();

        //保存测试单
        projectOrderEntity.setState(ProjectOrderConstant.STATE_SUBMIT);
        projectOrderEntity.setApplyTime(currentTime);
        projectMongo.saveProjectOrder(uniqueCode, projectOrderEntity);

        //更新FSU运行状态为"测试态"
        deviceMongo.updateFsuOperationState(uniqueCode, projectOrderEntity.getFsuCode(), FsuOperationState.TEST);

        //保存测试单操作记录
        projectMongo.saveProjectOrderLog(uniqueCode,
                new ProjectOrderLogEntity(projectOrderEntity.getId()
                        , new FacadeView(user.getId(),user.getName())
                        , ProjectOrderConstant.ACTION_CREATE
                        , null
                        , null
                        , currentTime));
    }

    /**
     * 获取测试单操作记录
     */
    @Override
    public List<ProjectOrderLogEntity> getOrderLogs(String uniqueCode, ProjectOrderQuery projectOrderQuery) {
        return projectMongo.findProjectOrderLogs(uniqueCode, projectOrderQuery);
    }
}
