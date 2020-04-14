package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.scloud.entity.ProjectOrderEntity;
import com.kongtrolink.framework.scloud.entity.model.ProjectOrderModel;
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
    List<ProjectOrderModel> getProjectOrderList(String uniqueCode, ProjectOrderQuery projectOrderQuery);

    /**
     * 创建测试单
     */
    void createProjectOrder(String uniqueCode, User user, ProjectOrderEntity projectOrderEntity);


}
