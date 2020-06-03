package tech.mystox.framework.api;

import tech.mystox.framework.stereotype.OperaCode;
import tech.mystox.framework.stereotype.Register;

/**
 * Created by mystoxlol on 2019/8/15, 13:31.
 * company: mystox
 * description:
 * update record:
 */
@Register
public interface LocalService {
    @OperaCode(code = "sayHi")
    String hello(String param);
}
