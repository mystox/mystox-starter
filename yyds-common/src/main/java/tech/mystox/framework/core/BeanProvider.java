package tech.mystox.framework.core;

public interface BeanProvider {
    <T> T getBean(Class<T> type);
}