package cmcciot.nbapi.online;

import cmcciot.nbapi.entity.CommonEntity;
import cmcciot.nbapi.utils.HttpSendCenter;
import com.kongtrolink.gateway.nb.cmcc.entity.base.BaseAck;
import okhttp3.Callback;

/**
 * Created by zhuocongbin
 * date 2018/3/15
 * apiKey: the product of api-key which can be found on OneNET
 */
public class ExecuteOpe extends BasicOpe{
    public ExecuteOpe(String apiKey) {
        super(apiKey);
    }

    @Override
    public BaseAck operation(CommonEntity commonEntity, String body) {
        return HttpSendCenter.post(apiKey, commonEntity.toUrl(), body);
    }
    @Override
    public BaseAck operation(CommonEntity commonEntity, byte[] body) {
        return HttpSendCenter.post(apiKey, commonEntity.toUrl(), body);
    }
    @Override
    public void operation(CommonEntity commonEntity, String body, Callback callback) {
        HttpSendCenter.postAsync(apiKey, commonEntity.toUrl(), body, callback);
    }
}
