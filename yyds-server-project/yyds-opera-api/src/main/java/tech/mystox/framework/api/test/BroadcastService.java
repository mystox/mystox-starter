package tech.mystox.framework.api.test;

import tech.mystox.framework.stereotype.OperaCode;
import tech.mystox.framework.stereotype.Register;

import java.util.List;

@Register
public interface BroadcastService {

    @OperaCode
    public void callHelloWorld(String name, List<String> home);

    public void callHelloWorld2(String name, List<String> home);


}
