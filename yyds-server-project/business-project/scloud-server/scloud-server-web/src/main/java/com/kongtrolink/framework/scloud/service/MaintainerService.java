package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.MaintainerEntity;
import com.kongtrolink.framework.scloud.entity.model.MaintainerModel;
import com.kongtrolink.framework.scloud.query.MaintainerQuery;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;

/**
 * 系统管理-用户管理-维护用户 接口类
 * Created by Eric on 2020/2/28.
 */
public interface MaintainerService {

    /**
     * 获取维护用户列表
     */
    List<MaintainerModel> getMaintainerList(String uniqueCode, MaintainerQuery maintainerQuery);

    /**
     * 导出维护用户列表
     */
    HSSFWorkbook exportMaintainerList(List<MaintainerModel> list);

    /**
     * 新增维护用户
     */
    void addMaintainer(String uniqueCode, MaintainerModel maintainerModel);

    /**
     * 修改维护用户
     */
    boolean modifyMaintainer(String uniqueCode, MaintainerModel maintainerModel);

    /**
     * 删除维护用户
     */
    void deleteMaintainer(String uniqueCode, MaintainerQuery maintainerQuery);
}
