package tech.mystox.framework.api.test.entity;

import java.util.List;

/**
 * Created by mystox on 2021/6/28, 15:28.
 * company:
 * description:
 * update record:
 */
public class ReturnEntity {
    private String result;
    private Integer code;
    List<ReturnEntity> list;
    ReturnEntity returnEntity;

    public ReturnEntity getReturnEntity() {
        return returnEntity;
    }

    public void setReturnEntity(ReturnEntity returnEntity) {
        this.returnEntity = returnEntity;
    }

    public List<ReturnEntity> getList() {
        return list;
    }

    public void setList(List<ReturnEntity> list) {
        this.list = list;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
