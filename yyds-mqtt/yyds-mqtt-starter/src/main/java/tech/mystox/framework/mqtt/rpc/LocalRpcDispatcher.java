package tech.mystox.framework.mqtt.rpc;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.mystox.framework.context.MsgHandlerThreadContext;
import tech.mystox.framework.core.BeanProvider;
import tech.mystox.framework.entity.MsgRsp;
import tech.mystox.framework.entity.RegisterSub;
import tech.mystox.framework.entity.StateCode.StateCodeEnum;
import tech.mystox.framework.entity.UnitHead;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Trusted local scan results are resolved once, never supplied by the remote caller. */
final class LocalRpcDispatcher {
    private static final Logger log = LoggerFactory.getLogger(LocalRpcDispatcher.class);
    private final Map<String, Invocation> methods;
    LocalRpcDispatcher(List<RegisterSub> subscriptions, BeanProvider beans) {
        Map<String, Invocation> resolved = new HashMap<>();
        for (RegisterSub sub : subscriptions) {
            String unit = sub.getExecuteUnit();
            if (unit == null || !unit.startsWith(UnitHead.LOCAL)) {
                throw new IllegalArgumentException("Registryless RPC only supports locally scanned methods: " + sub.getOperaCode());
            }
            try {
                String[] parts = unit.substring(UnitHead.LOCAL.length()).split("/", 3);
                Class<?> type = ClassUtils.getClass(parts[0]);
                List<String> names = JSON.parseArray(parts[2], String.class);
                Class<?>[] parameters = new Class<?>[names.size()];
                for (int i = 0; i < names.size(); i++) parameters[i] = ClassUtils.getClass(names.get(i));
                Method method = type.getMethod(parts[1], parameters);
                Object bean = beans.getBean(type);
                if (bean == null) throw new IllegalArgumentException("No bean for " + type.getName());
                if (resolved.putIfAbsent(sub.getOperaCode(), new Invocation(bean, method)) != null) {
                    throw new IllegalArgumentException("Duplicate local RPC operaCode: " + sub.getOperaCode());
                }
            } catch (ReflectiveOperationException | IndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Invalid local RPC method: " + sub, e);
            }
        }
        methods = Map.copyOf(resolved);
    }
    MsgRsp execute(String id, RpcRequest request) {
        if (request.deadline() <= System.currentTimeMillis()) return response(id, StateCodeEnum.TIMEOUT, "Request expired before execution");
        Invocation invocation = methods.get(request.operaCode());
        if (invocation == null) return response(id, StateCodeEnum.OPERA_ROUTE_EXCEPTION, "Unknown local operaCode");
        try {
            MsgHandlerThreadContext context = MsgHandlerThreadContext.getContext();
            context.setOperaCode(request.operaCode());
            context.setSourceAddress(request.sourceAddress());
            context.setGroupCode(request.sourceAddress().split("/", 2)[0]);
            JSONArray values = JSON.parseArray(request.payload());
            Method method = invocation.method();
            if (values == null || values.size() != method.getParameterCount()) {
                return response(id, StateCodeEnum.JSON_ILLEGAL, "RPC argument count does not match method");
            }
            Object[] args = new Object[values.size()];
            for (int i = 0; i < args.length; i++) args[i] = values.getObject(i, method.getGenericParameterTypes()[i]);
            Object result = method.invoke(invocation.bean(), args);
            return response(id, StateCodeEnum.SUCCESS, result == null ? null : result instanceof String ? (String) result : JSON.toJSONString(result));
        } catch (Exception e) {
            Throwable cause = e instanceof InvocationTargetException ? e.getCause() : e;
            log.warn("RPC method failed: {} [{}]", request.operaCode(), id, cause);
            return response(id, StateCodeEnum.EXCEPTION, cause.toString());
        } finally {
            MsgHandlerThreadContext.clear();
        }
    }
    static MsgRsp response(String id, StateCodeEnum state, String payload) {
        MsgRsp response = new MsgRsp(id, payload);
        response.setStateCode(state.getCode());
        return response;
    }
    private record Invocation(Object bean, Method method) { }
}
