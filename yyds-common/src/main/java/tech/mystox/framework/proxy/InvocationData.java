package tech.mystox.framework.proxy;

/**
 * Created by mystoxlol on 2020/7/1, 10:05.
 * company: kongtrolink
 * description:
 * update record:
 */
public class InvocationData {

    private Object[] arguments;
    private Class<?>[] parameterTypes;

    public InvocationData(Object[] arguments, Class<?>[] parameterTypes) {
        this.arguments = arguments;
        this.parameterTypes = parameterTypes;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }
}
