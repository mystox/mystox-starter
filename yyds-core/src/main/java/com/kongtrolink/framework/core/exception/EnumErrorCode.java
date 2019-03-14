package com.kongtrolink.framework.core.exception;

/**
 * Created by mystoxlol on 2019/2/26, 11:06.
 * company: kongtrolink
 * description:
 * update record:
 */
public enum EnumErrorCode
{
    /**********************控制节点异常**********************/
    CONTROLLER_ERROR(0x100000, "controller node server error", "控制节点异常"),
    CONTROLLER_COORDINATION_REGISTRY_NODEEXISTS(0x111001, "controller node is exists...", "注册出错, 控制节点重复注册"),
    CONTROLLER_COORDINATION_REGISTRY_ZOOKEEPERERROR(0x111002, "controller registry zookeeper exception...", "注册出错, zookeeper异常"),
    CONTROLLER_COORDINATION_REGISTRY_NETWORKERERROR(0x111004, "controller registry network exception...", "注册出错, 网络链接失效"),
    CONTROLLER_COORDINATION_REGISTRY_SCHEDULE(0x111003, "coordinate-registry-schedule update false...", "更新集群/控制节点信息出错"),
    /****************** 工作节点异常 ************************/
    WORKER_ERROR(0x200000,"worker node server error","工作节点异常");

    private final int code;
    private final String context;
    private final String description;
    EnumErrorCode(int code, String context, String description)
    {
        this.code = code;
        this.context = context;
        this.description = description;

    }

    public int getCode()
    {
        return code;
    }

    public String getContext()
    {
        return context;
    }

    public String getDescription()
    {
        return description;
    }

    public String getEntity()
    {
        return "[" + String.format("0x%06X", this.code) + "]" + this.context + "{}";
    }

    public static void main(String[] args)
    {
        System.out.println(EnumErrorCode.CONTROLLER_COORDINATION_REGISTRY_NODEEXISTS.getEntity());
    }
}
