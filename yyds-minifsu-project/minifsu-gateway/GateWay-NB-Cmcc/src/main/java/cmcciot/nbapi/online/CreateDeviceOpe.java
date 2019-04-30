package cmcciot.nbapi.online;

import cmcciot.nbapi.entity.CommonEntity;
import cmcciot.nbapi.utils.HttpSendCenter;
import com.kongtrolink.gateway.nb.cmcc.entity.base.BaseAck;
import okhttp3.Callback;

/**
 * Created by zhuocongbin
 * date 2018/3/16
 * apiKey: the product of api-key which can be found on OneNET
 */
public class CreateDeviceOpe extends BasicOpe{
    public CreateDeviceOpe(String apiKey) {
        super(apiKey);
    }
    @Override
    public BaseAck operation(CommonEntity commonEntity, String body) {
        return HttpSendCenter.post(this.apiKey, commonEntity.toUrl(), body);
    }

    @Override
    public BaseAck operation(CommonEntity commonEntity, byte[] body) {
        return null;
    }

    @Override
    public void operation(CommonEntity commonEntity, String body, Callback callback) {
        HttpSendCenter.postAsync(this.apiKey, commonEntity.toUrl(), body, callback);
    }

}
