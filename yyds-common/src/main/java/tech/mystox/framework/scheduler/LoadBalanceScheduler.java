package tech.mystox.framework.scheduler;

import tech.mystox.framework.core.OperaCall;
import tech.mystox.framework.entity.ServerMsg;
import tech.mystox.framework.exception.RegisterException;

import java.util.List;

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
     * @return
     */
    List<String> getAllServers();

    <T> T operaCall(OperaCall<T> operaCall, String targetServerCode, Object key);

    List<String> getOperaRouteArr(String operaCode);

    // ServerMsg retryServer(Object key);
}
