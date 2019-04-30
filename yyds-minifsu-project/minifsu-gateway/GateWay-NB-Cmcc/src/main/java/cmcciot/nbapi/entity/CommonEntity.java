package cmcciot.nbapi.entity;

import java.io.Serializable;

/**
 * Created by zhuocongbin
 * date 2018/3/16
 */
public abstract class CommonEntity implements Serializable{
    // 设备imei号，平台唯一，必填参数
    protected String imei;
    // ISPO标准中的Object ID
    protected Integer objId;
    // ISPO标准中的Object Instance ID
    protected Integer objInstId;
    // ISPO标准中的Resource ID
    protected Integer resId;

    public abstract String toUrl();
}
