package tech.mystox.framework.service.common;

import tech.mystox.framework.stereotype.OperaCode;
import tech.mystox.framework.stereotype.Register;

import java.io.IOException;
import java.util.List;

@Register
public interface OperaRouteService {
    @OperaCode
    List<String> broadcastOperaRoute(String operaCode,List<String> subGroupServerList) throws IOException;
}
