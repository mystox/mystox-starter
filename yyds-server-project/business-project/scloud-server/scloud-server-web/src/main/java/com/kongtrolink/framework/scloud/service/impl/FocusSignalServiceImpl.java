package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.FocusSignalDao;
import com.kongtrolink.framework.scloud.entity.FocusSignalEntity;
import com.kongtrolink.framework.scloud.query.FocusSignalQuery;
import com.kongtrolink.framework.scloud.service.FocusSignalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FocusSignalServiceImpl implements FocusSignalService {

    @Autowired
    FocusSignalDao focusSignalDao;
    /**
     * 保存信息
     *
     * @param focusSignalEntity 关注点信息
     */
    @Override
    public void saveFocusSignal(FocusSignalEntity focusSignalEntity) {
        focusSignalDao.saveFocusSignal(focusSignalEntity);
    }

    /**
     * 根据 登录用户 设备ID 取得关注点
     *
     * @param uniqueCode 企业编码
     * @param deviceId   设备ID
     * @param userId     登录的用户ID
     * @return 列表
     */
    @Override
    public List<FocusSignalEntity> queryListByDevice(String uniqueCode, int deviceId, String userId) {
        return focusSignalDao.queryListByDevice(uniqueCode, deviceId, userId);
    }

    /**
     * 根据登录用户 取得关注点列表 - 分页
     *
     * @param query 查询条件
     * @return 列表
     */
    @Override
    public List<FocusSignalEntity> getList(FocusSignalQuery query) {
        return focusSignalDao.getList(query);
    }

    /**
     * 根据登录用户 取得关注点列表 - 分页总数
     *
     * @param query 查询条件
     * @return 数量
     */
    @Override
    public int getListCount(FocusSignalQuery query) {
        return focusSignalDao.getListCount(query);
    }

    /**
     * 删除 关注点
     *
     * @param id 主键ID
     */
    @Override
    public void delFocusSignal(int id) {
        focusSignalDao.delFocusSignal(id);
    }
}
