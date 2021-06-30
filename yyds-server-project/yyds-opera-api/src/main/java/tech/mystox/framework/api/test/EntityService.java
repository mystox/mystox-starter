package tech.mystox.framework.api.test;

import tech.mystox.framework.api.test.entity.OperaParam;
import tech.mystox.framework.api.test.entity.ReturnEntity;
import tech.mystox.framework.stereotype.OperaCode;
import tech.mystox.framework.stereotype.Register;

/**
 * Created by mystox on 2021/6/28, 15:27.
 * company:
 * description:
 * update record:
 */
@Register
public interface EntityService {
    @OperaCode
    public ReturnEntity getEntity(OperaParam operaParam);
}
