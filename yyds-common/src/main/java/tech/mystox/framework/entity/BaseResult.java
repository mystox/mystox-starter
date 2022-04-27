package tech.mystox.framework.entity;

/**
 * Created by mystox on 2021/9/2, 15:42.
 * company:
 * description:
 * update record:
 */
class BaseResult {

    protected String info;
    protected Boolean success;

    public BaseResult() {
        this(true,"请求成功");
    }

    public BaseResult(Boolean success,String info) {
        this.info = info;
        this.success = success;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
