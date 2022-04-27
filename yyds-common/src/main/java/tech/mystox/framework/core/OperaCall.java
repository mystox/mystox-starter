package tech.mystox.framework.core;

/**
 * Created by mystoxlol on 2020/6/12, 16:13.
 * company:
 * description:
 * update record:
 */
public interface OperaCall<T>{
    T operaTarget(String operaCode, String targetServerCode);
}
