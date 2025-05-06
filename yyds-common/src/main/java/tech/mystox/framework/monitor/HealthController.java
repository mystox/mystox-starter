package tech.mystox.framework.monitor;

import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.entity.JsonResult;
import tech.mystox.framework.entity.ServerStatus;

/**
 * Created by mystoxlol on 2019/8/29, 9:46.
 * company: mystox
 * description:
 * update record:
 */
@Lazy
@RestController
@RequestMapping("/health")
public class HealthController {
    final IaContext iaContext;
    //Logger logger = LoggerFactory.getLogger(HealthController.class);

    public HealthController(IaContext iaContext) {
        this.iaContext = iaContext;
    }

    @RequestMapping("/serverStatus")
    public JsonResult<ServerStatus> testConfigRefresh() {
        ServerStatus serverStatus = iaContext.getIaENV().getServerStatus();
        return new JsonResult<>(serverStatus);
    }
}
