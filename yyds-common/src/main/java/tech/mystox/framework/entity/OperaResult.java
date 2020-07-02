package tech.mystox.framework.entity;

/**
 * Created by mystoxlol on 2019/11/10, 17:39.
 * company: mystox
 * description:
 * update record:
 */
public class OperaResult {
    private Integer stateCode;
    private String result;

    public OperaResult() {
    }

    public Integer getStateCode() {
        return stateCode;
    }

    public void setStateCode(Integer stateCode) {
        this.stateCode = stateCode;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
