package tech.mystox.framework.proxy;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mystoxlol on 2020/7/1, 9:30.
 * company:
 * description:
 * update record:
 */
public class OperaInvocation implements Invocation {
    private final String operaCode;
    private final Object[] arguments;
    private final Type returnType;
    private final Map<String, Object> attachments = new HashMap<>();

    public OperaInvocation(String operaCode, Object[] arguments, Type returnType) {
        this.operaCode = operaCode;
        this.arguments = arguments;
        this.returnType = returnType;
    }

    @Override
    public String getOperaCode() {
        return operaCode;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public Type getReturnType() {
        return returnType;
    }

    @Override
    public Map<String, Object> getAttachments() {
        return attachments;
    }
}
