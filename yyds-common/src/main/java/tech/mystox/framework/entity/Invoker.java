package tech.mystox.framework.entity;

/**
 * Created by mystoxlol on 2020/6/23, 17:16.
 * company: kongtrolink
 * description:
 * update record:
 */
public interface Invoker<T> {
    /**
     * get service interface.
     *
     * @return service interface.
     */
    Class<T> getInterface();

    /**
     * invoke.
     *
     * @param invocation
     * @return result
     */
    MsgResult invoke(Invocation invocation);
}
