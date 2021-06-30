package tech.mystox.framework.foo.service.api.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import tech.mystox.framework.api.test.EntityService;
import tech.mystox.framework.api.test.entity.OperaParam;
import tech.mystox.framework.api.test.entity.ReturnEntity;

/**
 * Created by mystox on 2021/6/28, 15:32.
 * company:
 * description:
 * update record:
 */
@Service
public class EntityServiceImpl implements EntityService {

    @Override
    public ReturnEntity getEntity(OperaParam operaParam) {
        System.out.println(JSONObject.toJSONString(operaParam));
        ReturnEntity returnEntity = new ReturnEntity();
        returnEntity.setResult("11111111111");
        returnEntity.setCode(11);
        return returnEntity;
    }
}
