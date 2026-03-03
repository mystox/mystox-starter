package tech.mystox.framework.core;

import tech.mystox.framework.entity.StateCode;

/**
 * Created by mystoxlol on 2020/6/12, 16:13.
 * company:
 * description:
 * update record:
 */
public interface OperaCall<T>{
    T operaTarget(String operaCode, String targetServerCode);
    T newResult(StateCode.StateCodeEnum code, String message);

}
