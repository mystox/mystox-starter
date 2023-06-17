package tech.mystox.framework.proxy;

import com.alibaba.fastjson2.JSONObject;
import tech.mystox.framework.core.IaContext;

import java.lang.reflect.Type;

/**
 * Created by mystoxlol on 2020/6/29, 20:48.
 * company:
 * description: 广播拦截
 * update record:
 */
public class OperaBroadcastInterceptor extends OperaBaseInterceptor {

    public OperaBroadcastInterceptor(IaContext iaContext) {
        super(iaContext);
    }

    @Override
    public Object opera(String operaCode, Object[] arguments, Type genericReturnType) {
        getIaContext().getIaENV().getMsgScheduler().getIaHandler().broadcast(operaCode, JSONObject.toJSONString(arguments));
        return null;
    }

}
