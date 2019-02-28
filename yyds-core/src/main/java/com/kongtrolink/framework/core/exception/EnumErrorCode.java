package com.kongtrolink.framework.core.exception;

/**
 * Created by mystoxlol on 2019/2/26, 11:06.
 * company: kongtrolink
 * description:
 * update record:
 */
public enum EnumErrorCode
{
    /****************** 工作节点异常 ************************/
    WORKER_ERROR(2000,"worker node server error","工作节点异常");

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
}
