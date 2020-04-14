package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.scloud.dao.ProjectMongo;
import com.kongtrolink.framework.scloud.entity.ProjectOrderEntity;
import com.kongtrolink.framework.scloud.entity.model.ProjectOrderModel;
import com.kongtrolink.framework.scloud.query.ProjectOrderQuery;
import com.kongtrolink.framework.scloud.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 工程管理 接口实现类
 * Created by Eric on 2020/4/13.
 */
@Service
public class ProjectServiceImpl implements ProjectService{

    @Autowired
    ProjectMongo projectMongo;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceImpl.class);


    /**
     * 获取测试单列表
     */
    @Override
    public List<ProjectOrderModel> getProjectOrderList(String uniqueCode, ProjectOrderQuery projectOrderQuery) {
        return null;
    }

    /**
     * 创建测试单
     */
    @Override
    public void createProjectOrder(String uniqueCode, User user, ProjectOrderEntity projectOrderEntity) {

    }
}
