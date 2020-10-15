package tech.mystox.framework.mqtt.entity;

/**
 * Created by mystoxlol on 2019/8/13, 9:44.
 * company: mystox
 * description:
 * update record:
 */
public class ResponseEntity {
    private int code;
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }



    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
