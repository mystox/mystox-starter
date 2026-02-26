package tech.mystox.framework.config.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import tech.mystox.framework.entity.PrivFuncEntity;


/**
 * Created by mystoxlol on 2019/11/6, 20:27.
 * company: mystox
 * description:
 * update record:
 */
@ConfigurationProperties(prefix = "")
public class WebPrivFuncConfig {
    private PrivFuncEntity privFunc;
    public PrivFuncEntity getPrivFunc() {
        return privFunc;
    }
    public void setPrivFunc(PrivFuncEntity privFunc) {
        this.privFunc = privFunc;
    }

}
