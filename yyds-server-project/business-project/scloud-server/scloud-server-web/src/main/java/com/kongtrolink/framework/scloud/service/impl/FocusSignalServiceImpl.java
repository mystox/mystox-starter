package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.scloud.constant.RedisKey;
import com.kongtrolink.framework.scloud.dao.FocusSignalDao;
import com.kongtrolink.framework.scloud.entity.FocusSignalEntity;
import com.kongtrolink.framework.scloud.query.FocusSignalQuery;
import com.kongtrolink.framework.scloud.service.FocusSignalService;
import com.kongtrolink.framework.scloud.util.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FocusSignalServiceImpl implements FocusSignalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FocusSignalServiceImpl.class);

    @Autowired
    FocusSignalDao focusSignalDao;
    @Autowired
    RedisUtils redisUtils;
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
        List<FocusSignalEntity> list = focusSignalDao.getList(query);
        if(list!=null){
            for(FocusSignalEntity signalEntity:list){
                String redisKey = RedisUtil.getRealDataKey(signalEntity.getUniqueCode(),signalEntity.getDeviceCode());
                Object value = redisUtils.hget(RedisKey.DEVICE_REAL_DATA,redisKey);
                try{
                    Map<String,Object> redisValue = JSONObject.parseObject(String.valueOf(value));
                    if(redisValue!=null && redisValue.containsKey(signalEntity.getCntbId())){
                        signalEntity.setValue(String.valueOf(redisValue.get(signalEntity.getCntbId())));
                    }
                }catch (Exception e){
                    LOGGER.error("获取redis数据异常 {} ,{} ",RedisKey.DEVICE_REAL_DATA,redisKey);
                }
            }
        }
        return list;
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
