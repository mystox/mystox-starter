package tech.mystox.framework.scheduler;

import tech.mystox.framework.entity.MsgResult;
import tech.mystox.framework.entity.ServerMsg;
import tech.mystox.framework.entity.StateCode;
import tech.mystox.framework.exception.RegisterException;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Created by mystoxlol on 2020/6/8, 8:58.
 * company:
 * description:
 * update record:
 */
public interface LoadBalanceScheduler extends Schedule/*,Callable<MsgResult> */{
    void addServers(List<String> serverCodeList);

    ServerMsg chooseServer(String groupCode, Object key) throws RegisterException;


    void markServerDown(String serverCode);

    /**
     * 有效服务列表
     */
    List<String> getReachableServers();

    /**
     * 获取所有服务列表
     */
    List<String> getAllServers();

    //<T extends MsgResult> T operaCall(OperaCall<T> operaCall, String targetServerCode, Object key);

    public <T extends MsgResult> T operaCall(
            BiFunction<String, String, T> executor,
            BiFunction<StateCode.StateCodeEnum, String,T> errorSupplier,
            String targetServerCode,
            String operaCode);
    List<String> getOperaRouteArr(String operaCode);

    void retryOpera(String operaCode);

    // ServerMsg retryServer(Object key);
}
