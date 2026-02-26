package tech.mystox.framework.entity;

/**
 * Created by mystoxlol on 2019/9/9, 1:10.
 * company: mystox
 * description:
 * update record:
 */
public class MsgResult {
    private final int stateCode;
    private String msg;

    public int getStateCode() {
        return stateCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public MsgResult(int stateCode, String msg) {
        this.msg = msg;
        this.stateCode = stateCode;
    }
    public MsgResult(StateCode.StateCodeEnum stateCode, String msg) {
        this.msg = msg;
        this.stateCode = stateCode.getCode();
    }

    @Override
    public String toString() {
        return "MsgResult{" +
                "stateCode=" + stateCode +
                ", msg='" + msg + '\'' +
                '}';
    }
}
