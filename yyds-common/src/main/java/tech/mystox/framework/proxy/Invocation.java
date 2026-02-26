package tech.mystox.framework.proxy;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by mystoxlol on 2020/6/24, 9:40.
 * company:
 * description:
 * update record:
 */
public interface Invocation {
    String getOperaCode();

    Object[] getArguments();

    Type getReturnType();

    Map<String, Object> getAttachments();
}
