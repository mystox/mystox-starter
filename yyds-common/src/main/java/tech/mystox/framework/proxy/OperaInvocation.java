package tech.mystox.framework.proxy;

import tech.mystox.framework.entity.Invocation;
import tech.mystox.framework.entity.Invoker;

import java.util.Map;

/**
 * Created by mystoxlol on 2020/7/1, 9:30.
 * company: kongtrolink
 * description:
 * update record:
 */
public class OperaInvocation implements Invocation {
    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return new Class<?>[0];
    }

    @Override
    public Object[] getArguments() {
        return new Object[0];
    }

    @Override
    public Map<String, String> getAttachments() {
        return null;
    }

    @Override
    public String getAttachment(String key) {
        return null;
    }

    @Override
    public String getAttachment(String key, String defaultValue) {
        return null;
    }

    @Override
    public Invoker<?> getInvoker() {
        return null;
    }
}
