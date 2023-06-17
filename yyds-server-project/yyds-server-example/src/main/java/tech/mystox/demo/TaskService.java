package tech.mystox.demo;

import com.alibaba.fastjson2.JSON;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import tech.mystox.framework.api.test.LocalService;
import tech.mystox.framework.common.util.SpringContextUtil;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.entity.MsgResult;
import tech.mystox.framework.service.IaOpera;
import tech.mystox.framework.stereotype.Opera;

import java.util.List;

/**
 * Created by mystox on 2022/4/28, 17:33.
 * company:
 * description:
 * update record:
 */
@Service
public class TaskService  implements CommandLineRunner {
    @Opera
    LocalService localService;
    @Override
    public void run(String... args) throws Exception {
        System.out.println("开始执行任务...");
        IaContext bean1 = SpringContextUtil.getBean(IaContext.class);
        MsgResult hello = bean1.getIaENV().getMsgScheduler().getIaHandler().opera("hello", "");
        System.out.println(JSON.toJSONString(hello));
        IaOpera bean = SpringContextUtil.getBean(IaOpera.class);
        MsgResult opera = bean.opera("sayHi", "hihihihihi");
        System.out.println(JSON.toJSONString(opera));

        List<String> helloList = localService.helloList("helloList");
        System.out.println("helloList:"+JSON.toJSONString(helloList));
    }

}
