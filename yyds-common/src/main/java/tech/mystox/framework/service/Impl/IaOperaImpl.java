package tech.mystox.framework.service.Impl;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Service;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.entity.MsgResult;
import tech.mystox.framework.service.IaOpera;

import java.util.concurrent.TimeUnit;

/**
 * Created by mystoxlol on 2020/5/19, 15:35.
 * company: mystox
 * description:
 * update record:
 */
@Service
public class IaOperaImpl implements IaOpera {
    private final IaContext iaContext;

    public IaOperaImpl(IaContext iaContext) {
        this.iaContext = iaContext;
    }

    //@Override
    //public MsgResult opera(String operaCode, Object msg) {
    //    return iaContext.getIaENV().getMsgScheduler().getIaHandler().opera(operaCode, JSONObject.toJSONString(Collections.singletonList(msg)));
    //}
    @Override
    public MsgResult opera(String operaCode, Object... msg) {
        return iaContext.getIaENV().getMsgScheduler().getIaHandler().opera(operaCode, JSONObject.toJSONString(msg));
    }
    @Override
    public MsgResult operaTarget(String groupServerCode, String operaCode, Object... msg) {
        return iaContext.getIaENV().getMsgScheduler().getIaHandler().sendToMqttSync(groupServerCode, operaCode, JSONObject.toJSONString(msg));
    }
    @Override
    public MsgResult operaGroup(String groupCode, String operaCode, Object... msg) {
        return iaContext.getIaENV().getMsgScheduler().getIaHandler().operaGroupCode(groupCode, operaCode, JSONObject.toJSONString(msg));
    }
    @Override
    public MsgResult opera(String operaCode, int qos, long timeout, TimeUnit timeUnit,Object... msg) {
        return iaContext.getIaENV().getMsgScheduler().getIaHandler().opera(operaCode, JSONObject.toJSONString(msg), qos, timeout, timeUnit);
    }

    @Override
    public void operaAsync(String operaCode, Object... msg) {
        iaContext.getIaENV().getMsgScheduler().getIaHandler().operaAsync(operaCode, JSONObject.toJSONString(msg));
    }
    @Override
    public void operaTargetAsync(String groupServerCode, String operaCode, Object... msg) throws Exception {
        iaContext.getIaENV().getMsgScheduler().getIaHandler().sendToMqtt(groupServerCode, operaCode, JSONObject.toJSONString(msg));
    }

    @Override
    public void broadcast(String operaCode, Object... msg) {
        iaContext.getIaENV().getMsgScheduler().getIaHandler().broadcast(operaCode, JSONObject.toJSONString(msg));
    }


}
