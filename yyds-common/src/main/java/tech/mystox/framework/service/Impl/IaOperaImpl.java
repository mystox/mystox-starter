package tech.mystox.framework.service.Impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.entity.MsgResult;
import tech.mystox.framework.service.IaOpera;

import java.util.Collections;
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

    @Override
    public MsgResult opera(String operaCode, String msg) {
        return iaContext.getIaENV().getMsgScheduler().getIaHandler().opera(operaCode, JSONObject.toJSONString(Collections.singletonList(msg)));
    }

    @Override
    public MsgResult opera(String operaCode, String msg, int qos, long timeout, TimeUnit timeUnit) {
        return iaContext.getIaENV().getMsgScheduler().getIaHandler().opera(operaCode, JSONObject.toJSONString(Collections.singletonList(msg)), qos, timeout, timeUnit);
    }

    @Override
    public void operaAsync(String operaCode, String msg) {
        iaContext.getIaENV().getMsgScheduler().getIaHandler().operaAsync(operaCode, JSONObject.toJSONString(Collections.singletonList(msg)));
    }

    @Override
    public void broadcast(String operaCode, String msg) {
        iaContext.getIaENV().getMsgScheduler().getIaHandler().broadcast(operaCode, JSONObject.toJSONString(Collections.singletonList(msg)));
    }
}
