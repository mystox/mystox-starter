package tech.mystox.framework.proxy;

public interface OperaFilter {

    Object invoke(OperaInvoker next, Invocation invocation) throws Throwable;

    default int getOrder() {
        return 0;
    }
}