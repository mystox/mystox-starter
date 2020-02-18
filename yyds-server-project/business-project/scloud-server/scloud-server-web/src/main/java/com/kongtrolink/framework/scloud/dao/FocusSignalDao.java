package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.entity.FocusSignalEntity;
import com.kongtrolink.framework.scloud.query.FocusSignalQuery;
import com.kongtrolink.framework.scloud.service.FocusSignalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Repository
public class FocusSignalDao{

    @Autowired
    MongoTemplate mongoTemplate;
    /**
     * 保存信息
     *
     * @param focusSignalEntity 关注点信息
     */
    public void saveFocusSignal(FocusSignalEntity focusSignalEntity) {
        mongoTemplate.save(focusSignalEntity);
    }

    /**
     * 根据 登录用户 设备ID 取得关注点
     *
     * @param uniqueCode 企业编码
     * @param deviceId   设备ID
     * @param userId     登录的用户ID
     * @return 列表
     */
    public List<FocusSignalEntity> queryListByDevice(String uniqueCode, int deviceId, String userId) {
        Criteria criteria = Criteria.where("uniqueCode").is(uniqueCode)
                                                    .and("deviceId").is(deviceId)
                                                    .and("userId").is(userId);
        Query query = new Query(criteria);
        return mongoTemplate.find(query,FocusSignalEntity.class);
    }

    /**
     * 根据登录用户 取得关注点列表 - 分页
     *
     * @param focusSignalQuery 查询条件
     * @return 列表
     */
    public List<FocusSignalEntity> getList(FocusSignalQuery focusSignalQuery) {
        Criteria criteria = Criteria.where("uniqueCode").is(focusSignalQuery.getUniqueCode())
                .and("userId").is(focusSignalQuery.getUserId());
        int currentPage = focusSignalQuery.getCurrentPage();
        int pageSize = focusSignalQuery.getPageSize();
        Query query = new Query(criteria);
        query.skip((currentPage - 1) * pageSize).limit(pageSize);
        return mongoTemplate.find(query,FocusSignalEntity.class);
    }

    /**
     * 根据登录用户 取得关注点列表 - 分页总数
     *
     * @param focusSignalQuery 查询条件
     * @return 数量
     */
    public int getListCount(FocusSignalQuery focusSignalQuery) {
        Criteria criteria = Criteria.where("uniqueCode").is(focusSignalQuery.getUniqueCode())
                .and("userId").is(focusSignalQuery.getUserId());
        Query query = new Query(criteria);
        return (int)mongoTemplate.count(query,FocusSignalEntity.class);
    }

    /**
     * 删除 关注点
     *
     * @param id 主键ID
     */
    public void delFocusSignal(int id) {
        Criteria criteria = Criteria.where("id").is(id);
        Query query = new Query(criteria);
        mongoTemplate.remove(query,FocusSignalEntity.class);
    }
}
