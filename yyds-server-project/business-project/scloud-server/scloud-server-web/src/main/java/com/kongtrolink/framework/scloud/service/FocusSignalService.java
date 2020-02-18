package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.FocusSignalEntity;
import com.kongtrolink.framework.scloud.query.FocusSignalQuery;

import java.util.List;

/**
 * 关注信号点
 */
public interface FocusSignalService {

    /**
     * 保存信息
     * @param focusSignalEntity 关注点信息
     */
    void saveFocusSignal(FocusSignalEntity focusSignalEntity);

    /**
     * 根据 登录用户 设备ID 取得关注点
     * @param uniqueCode 企业编码
     * @param deviceId 设备ID
     * @param userId 登录的用户ID
     * @return 列表
     */
    List<FocusSignalEntity> queryListByDevice(String uniqueCode,int deviceId,String userId);

    /**
     * 根据登录用户 取得关注点列表 - 分页
     * @param query 查询条件
     * @return 列表
     */
    List<FocusSignalEntity> getList(FocusSignalQuery query);

    /**
     * 根据登录用户 取得关注点列表 - 分页总数
     * @param query 查询条件
     * @return 数量
     */
    int  getListCount(FocusSignalQuery query);

    /**
     * 删除 关注点
     * @param id 主键ID
     */
    void delFocusSignal(int id);

}
