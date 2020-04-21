package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.entity.JsonResult;
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
     * @auther: liudd
     * @date: 2020/4/21 8:41
     * 功能描述:获取列表
     */
    List<MaintainerEntity> list(String uniqueCode, MaintainerQuery maintainerQuery);

    /**
     * @auther: liudd
     * @date: 2020/4/21 9:26
     * 功能描述：将entity列表转换为model列表。
     */
    List<MaintainerModel> listModelsFromEntities(String uniquCode, List<MaintainerEntity> maintainerEntities, MaintainerQuery maintainerQuery);

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
     * @return 用户Id
     */
    String addMaintainer(String uniqueCode, MaintainerModel maintainerModel);

    /**
     * 批量添加维护用户
     */
    void addMaintainerList(String uniqueCode, List<MaintainerModel> maintainerModels);

    /**
     * 修改维护用户
     */
    boolean modifyMaintainer(String uniqueCode, MaintainerModel maintainerModel);

    /**
     * 删除维护用户
     */
    void deleteMaintainer(String uniqueCode, MaintainerQuery maintainerQuery);
}
