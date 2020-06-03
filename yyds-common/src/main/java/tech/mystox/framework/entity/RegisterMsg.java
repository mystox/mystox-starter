package tech.mystox.framework.entity;

/**
 * Created by mystoxlol on 2019/8/28, 14:51.
 * company: mystox
 * description:
 * update record:
 */
public class RegisterMsg {
    private String registerUrl;
    private RegisterType registerType;
    private String registerUrlHeader;

    public String getRegisterUrlHeader() {
        return registerUrlHeader;
    }

    public String getRegistURI()
    {
        return registerUrlHeader+"://"+registerUrl;
    }
    public void setRegisterUrlHeader(String registerUrlHeader) {
        this.registerUrlHeader = registerUrlHeader;
    }
    public String getRegisterUrl() {
        return registerUrl;
    }
    public void setRegisterUrl(String registerUrl) {
        this.registerUrl = registerUrl;
    }
    public RegisterType getRegisterType() {
        return registerType;
    }
    public void setRegisterType(RegisterType registerType) {
        this.registerType = registerType;
    }
}
