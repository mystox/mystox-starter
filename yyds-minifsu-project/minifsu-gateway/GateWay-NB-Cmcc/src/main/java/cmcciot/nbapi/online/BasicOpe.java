package cmcciot.nbapi.online;

import cmcciot.nbapi.entity.CommonEntity;
import com.kongtrolink.gateway.nb.cmcc.entity.base.BaseAck;
import okhttp3.Callback;

/**
 * Created by zhuocongbin
 * date 2018/3/16
 */
public abstract class BasicOpe {
    protected String apiKey;
    public BasicOpe(String apiKey) {
        this.apiKey = apiKey;
    }
    public abstract BaseAck operation(CommonEntity commonEntity, String body);
    public abstract BaseAck operation(CommonEntity commonEntity, byte[] body);
    public abstract void operation(CommonEntity commonEntity, String body, Callback callback);
}
