package tech.mystox.framework.proxy;

/**
 * Created by mystoxlol on 2020/6/23, 17:16.
 * company: ink
 * description:
 * update record:
 */
public interface OperaInvoker {
    /**
     * get service interface.
     *
     * @return service interface.
     */
    //Class<T> getInterface();

    /**
     * invoke.
     *
     * @param invocation
     * @return result
     */
    Object invoke(Invocation invocation) throws Throwable;
}
