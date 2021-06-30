package tech.mystox.framework.proxy;

import com.alibaba.fastjson.JSONObject;
import tech.mystox.framework.core.IaContext;

/**
 * Created by mystoxlol on 2020/6/29, 20:48.
 * company: kongtrolink
 * description: 广播拦截
 * update record:
 */
public class OperaBroadcastInterceptor extends OperaBaseInterceptor {
    private IaContext iaContext;

    public OperaBroadcastInterceptor(IaContext iaContext) {
        this.iaContext = iaContext;
    }

    @Override
    public Object opera(String operaCode, Object[] arguments, Class<?> genericReturnType) {
        iaContext.getIaENV().getMsgScheduler().getIaHandler().broadcast(operaCode, JSONObject.toJSONString(arguments));
        return null;
    }

}
